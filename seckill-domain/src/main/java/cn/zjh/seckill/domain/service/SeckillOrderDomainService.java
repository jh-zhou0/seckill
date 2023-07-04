package cn.zjh.seckill.domain.service;

import cn.zjh.seckill.domain.model.SeckillOrder;

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
    
}
