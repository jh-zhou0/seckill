package cn.zjh.seckill.order.infrastructure.mapper;

import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单
 * 
 * @author zjh - kayson
 */
public interface SeckillOrderMapper {

    /**
     * 保存订单
     */
    int saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(@Param("userId") Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(@Param("activityId") Long activityId);

    /**
     * 删除订单，下单时异常由TCC分布式事务调用
     */
    int deleteSeckillOrder(@Param("orderId") Long orderId);

    /**
     * 删除订单数据
     */
    void deleteOrder(@Param("orderId") Long orderId);
    
}