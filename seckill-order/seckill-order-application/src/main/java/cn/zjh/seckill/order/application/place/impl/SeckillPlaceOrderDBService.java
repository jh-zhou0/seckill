package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.common.model.message.TxMessage;
import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 基于数据库下单，防止库存超卖
 *
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDBService implements SeckillPlaceOrderService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderDBService.class);

    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 获取商品信息(带缓存)
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        boolean exception = false;
        try {
            // 获取商品库存
            Integer availableStock = seckillGoodsDubboService.getAvailableStockById(seckillOrderCommand.getGoodsId());
            //库存不足
            if (availableStock == null || availableStock < seckillOrderCommand.getQuantity()) {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
        } catch (Exception e) {
            exception = true;
            logger.error("SeckillPlaceOrderDbService|下单异常|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
        }
        // 事务消息
        Message<String> message = this.getTxMessage(txNo, userId, SeckillConstants.PLACE_ORDER_TYPE_DB, exception, seckillOrderCommand, seckillGoods);
        // 发送事务消息
        rocketMQTemplate.sendMessageInTransaction(SeckillConstants.TOPIC_TX_MSG, message, null);
        return txNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderInTransaction(TxMessage txMessage) {
        String orderTxKey = SeckillConstants.getKey(SeckillConstants.TX_MSG_KEY, String.valueOf(txMessage.getTxNo()));
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
        } catch (Exception e) {
            logger.error("saveOrderInTransaction|异常|{}", e.getMessage());
            distributedCacheService.delete(orderTxKey);
            throw e;
        }
    }

}