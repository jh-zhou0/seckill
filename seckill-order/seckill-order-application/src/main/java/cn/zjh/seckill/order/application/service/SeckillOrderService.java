package cn.zjh.seckill.order.application.service;

import cn.zjh.seckill.common.model.message.ErrorMessage;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

/**
 * 订单
 * 
 * @author zjh - kayson
 */
public interface SeckillOrderService {

    /**
     * 保存订单
     */
    Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

    /**
     * 删除订单
     */
    void deleteOrder(ErrorMessage errorMessage);
    
}