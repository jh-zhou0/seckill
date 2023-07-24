package cn.zjh.seckill.order.application.service.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillOrderSubmitDTO;
import cn.zjh.seckill.order.application.model.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.model.task.SeckillOrderTask;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.application.service.OrderTaskGenerateService;
import cn.zjh.seckill.order.application.service.PlaceOrderTaskService;
import cn.zjh.seckill.order.application.service.SeckillSubmitOrderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 异步提交订单
 * 
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
public class SeckillAsyncSubmitOrderServiceImpl extends SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {
    
    @Resource
    private OrderTaskGenerateService orderTaskGenerateService;
    @Resource
    private PlaceOrderTaskService placeOrderTaskService;
    @Resource
    private SeckillPlaceOrderService seckillPlaceOrderService;
    @Resource
    private DistributedCacheService distributedCacheService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 进行基本的检查
        checkSeckillOrder(userId, seckillOrderCommand);
        // 生成订单任务id
        String orderTaskId = orderTaskGenerateService.generatePlaceOrderTaskId(userId, seckillOrderCommand.getGoodsId());
        // 构造下单任务
        SeckillOrderTask seckillOrderTask = new SeckillOrderTask(SeckillConstants.TOPIC_ORDER_MSG, orderTaskId, userId, seckillOrderCommand);
        // 提交订单任务
        boolean isSuccess = placeOrderTaskService.submitOrderTask(seckillOrderTask);
        // 提交失败
        if (!isSuccess) {
            throw new SeckillException(ErrorCode.ORDER_FAILED);
        }
        return new SeckillOrderSubmitDTO(orderTaskId, seckillOrderCommand.getGoodsId(), SeckillConstants.TYPE_TASK);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handlePlaceOrderTask(SeckillOrderTask seckillOrderTask) {
        Long orderId = seckillPlaceOrderService.placeOrder(seckillOrderTask.getUserId(), seckillOrderTask.getSeckillOrderCommand());
        if (orderId != null) {
            String key = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ORDER_ID_KEY, seckillOrderTask.getOrderTaskId());
            distributedCacheService.put(key, orderId, SeckillConstants.ORDER_TASK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
    }
}
