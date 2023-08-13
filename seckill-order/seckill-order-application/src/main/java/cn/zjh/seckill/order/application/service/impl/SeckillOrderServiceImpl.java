package cn.zjh.seckill.order.application.service.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillOrderSubmitDTO;
import cn.zjh.seckill.common.model.message.ErrorMessage;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.application.service.OrderTaskGenerateService;
import cn.zjh.seckill.order.application.service.SeckillOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单
 *
 * @author zjh - kayson
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderServiceImpl.class);

    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    private SeckillPlaceOrderService seckillPlaceOrderService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private OrderTaskGenerateService orderTaskGenerateService;

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderDomainService.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderDomainService.getSeckillOrderByActivityId(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(ErrorMessage errorMessage) {
        // 成功提交过事务，才能清理订单，增加缓存库存
        Boolean submitTransaction = distributedCacheService.hasKey(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(errorMessage.getTxNo())));
        if (Boolean.TRUE.equals(submitTransaction)) {
            logger.info("deleteOrder|订单微服务未执行本地事务|{}", errorMessage.getTxNo());
            return;
        }
        seckillOrderDomainService.deleteOrder(errorMessage.getTxNo());
        handlerCacheStock(errorMessage);
    }

    @Override
    public SeckillOrderSubmitDTO getSeckillOrderSubmitDTOByTaskId(String taskId, Long userId, Long goodsId) {
        String generateTaskId = orderTaskGenerateService.generatePlaceOrderTaskId(userId, goodsId);
        if (!generateTaskId.equals(taskId)) {
            throw new SeckillException(ErrorCode.ORDER_TASK_ID_INVALID);
        }
        String key = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ORDER_ID_KEY, taskId);
        Long orderId = distributedCacheService.getObject(key, Long.class);
        if (orderId == null) {
            // 如果未获取到订单id，则再次返回任务id
            return new SeckillOrderSubmitDTO(taskId, goodsId, SeckillConstants.TYPE_TASK);
        }
        // 返回订单id
        return new SeckillOrderSubmitDTO(String.valueOf(orderId), goodsId, SeckillConstants.TYPE_ORDER);
    }

    /**
     * 处理缓存库存
     */
    private void handlerCacheStock(ErrorMessage errorMessage) {
        // 订单微服务之前未抛出异常，说明已经扣减了缓存中的库存，此时需要将缓存中的库存增加回来
        if (Boolean.FALSE.equals(errorMessage.getException())) {
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(errorMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            Long result = distributedCacheService.checkExecute(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);
            // 已经执行过恢复缓存库存的方法
            if (SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE.equals(result)) {
                logger.info("handlerCacheStock|已经执行过恢复缓存库存的方法|{}", JSONObject.toJSONString(errorMessage));
                return;
            }
            // 只有分布式锁方式和Lua脚本方法才会扣减缓存中的库存
            String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(errorMessage.getGoodsId()));
            logger.info("handlerCacheStock|回滚缓存库存|{}", JSONObject.toJSONString(errorMessage));
            if (SeckillConstants.PLACE_ORDER_TYPE_LOCK.equalsIgnoreCase(errorMessage.getPlaceOrderType())) { // 分布式锁方式
                distributedCacheService.increment(key, errorMessage.getQuantity());
            } else if (SeckillConstants.PLACE_ORDER_TYPE_LUA.equalsIgnoreCase(errorMessage.getPlaceOrderType())) { // Lua方式
                distributedCacheService.incrementByLua(key, errorMessage.getQuantity());
            }
        }
    }

}