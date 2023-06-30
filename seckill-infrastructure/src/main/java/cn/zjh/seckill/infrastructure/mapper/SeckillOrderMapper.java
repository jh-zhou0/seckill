package cn.zjh.seckill.infrastructure.mapper;

import cn.zjh.seckill.domain.model.SeckillOrder;
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

}
