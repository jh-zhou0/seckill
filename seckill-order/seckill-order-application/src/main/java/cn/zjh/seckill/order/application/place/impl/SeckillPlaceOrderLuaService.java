package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.common.model.message.TxMessage;
import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.mq.MessageSenderService;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 基于lua脚本防止库存超卖
 *
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lua")
public class SeckillPlaceOrderLuaService implements SeckillPlaceOrderService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLuaService.class);

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    private MessageSenderService messageSenderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        boolean exception = false;
        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        // 获取库存key
        String stockKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        try {
            // 获取商品限购信息
            String limitKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
            Object limitObj = distributedCacheService.getObject(limitKey);
            // 如果从Redis获取到的限购信息为null，则说明商品已经下线
            if (limitObj == null) {
                throw new SeckillException(ErrorCode.GOODS_OFFLINE);
            }
            if (Integer.parseInt(String.valueOf(limitObj)) < seckillOrderCommand.getQuantity()){
                throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
            }
            // 扣减缓存中的库存
            Long result = distributedCacheService.decrementByLua(stockKey, seckillOrderCommand.getQuantity());
            // 检查lua脚本执行结果
            checkResult(result);
        } catch (Exception e) {
            logger.error("SeckillPlaceOrderLuaService|下单异常|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            exception = true;
        }
        // 构建事务消息
        TxMessage message = getTxMessage(SeckillConstants.TOPIC_TX_MSG, txNo, userId, SeckillConstants.PLACE_ORDER_TYPE_LUA, exception, seckillOrderCommand, seckillGoods);
        // 发送事务消息
        messageSenderService.sendMessageInTransaction(message, null);
        return txNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderInTransaction(TxMessage txMessage) {
        String orderTxKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo()));
        try {
            Boolean submitTransaction = distributedCacheService.hasKey(orderTxKey);
            if (Boolean.TRUE.equals(submitTransaction)) {
                logger.info("saveOrderInTransaction|已经执行过本地事务|{}", txMessage.getTxNo());
                return;
            }
            // 构建订单
            SeckillOrder seckillOrder = buildSeckillOrder(txMessage);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 保存事务日志
            distributedCacheService.put(orderTxKey, txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
            // 测试分布式事务问题
//            int i = 1 / 0;
        } catch (Exception e) {
            logger.error("saveOrderInTransaction|异常|{}", e.getMessage());
            distributedCacheService.delete(orderTxKey);
            rollbackCacheStock(txMessage);
            throw e;
        }
    }

    /**
     * 回滚缓存库存
     */
    private void rollbackCacheStock(TxMessage txMessage) {
        // 扣减过缓存库存
        if (Boolean.FALSE.equals(txMessage.getException())) {
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            // 已经执行过恢复缓存库存的方法
            Long result = distributedCacheService.checkRecoverStockByLua(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);
            if (SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE.equals(result)) {
                logger.info("handlerCacheStock|已经执行过恢复缓存库存的方法|{}", JSONObject.toJSONString(txMessage));
                return;
            }
            // 只有分布式锁方式和Lua脚本方法才会扣减缓存中的库存
            String stockKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(txMessage.getGoodsId()));
            distributedCacheService.incrementByLua(stockKey, txMessage.getQuantity());
        }
    }

    private void checkResult(Long result) {
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new SeckillException(ErrorCode.STOCK_IS_NULL);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_LT_ZERO) {
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }

}