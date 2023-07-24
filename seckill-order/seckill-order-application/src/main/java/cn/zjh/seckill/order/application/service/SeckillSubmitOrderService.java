package cn.zjh.seckill.order.application.service;

import cn.zjh.seckill.common.model.dto.SeckillOrderSubmitDTO;
import cn.zjh.seckill.order.application.model.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.model.task.SeckillOrderTask;

/**
 * 提交订单的服务
 * 
 * @author zjh - kayson
 */
public interface SeckillSubmitOrderService {

    /**
     * 保存订单
     */
    SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);
    
    /**
     * 处理订单任务
     */
    default void handlePlaceOrderTask(SeckillOrderTask seckillOrderTask){
    }

    /**
     * 实现基础校验功能
     */
    default void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand){
    }
    
}
