package cn.zjh.seckill.order.application.service;

import cn.zjh.seckill.order.application.model.task.SeckillOrderTask;

/**
 * 订单任务服务
 * 
 * @author zjh - kayson
 */
public interface PlaceOrderTaskService {

    /**
     * 提交订单任务
     */
    boolean submitOrderTask(SeckillOrderTask seckillOrderTask);
    
}
