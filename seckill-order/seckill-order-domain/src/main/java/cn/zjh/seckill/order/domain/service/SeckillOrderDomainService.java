package cn.zjh.seckill.order.domain.service;

import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

/**
 * 订单领域层的服务接口
 * 
 * @author zjh - kayson
 */
public interface SeckillOrderDomainService {

    /**
     * 保存订单
     */
    void saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

    /**
     * 删除订单，下单时异常由TCC分布式事务调用
     */
    void deleteSeckillOrder(Long orderId);

    /**
     * 删除订单
     */
    void deleteOrder(Long orderId);
    
}