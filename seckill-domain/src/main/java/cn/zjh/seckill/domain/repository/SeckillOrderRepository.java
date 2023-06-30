package cn.zjh.seckill.domain.repository;

import cn.zjh.seckill.domain.model.SeckillOrder;

import java.util.List;

/**
 * 订单
 * 
 * @author zjh - kayson
 */
public interface SeckillOrderRepository {

    /**
     * 保存订单
     */
    int saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

}
