package cn.zjh.seckill.application.service;

import cn.zjh.seckill.domain.dto.SeckillOrderDTO;
import cn.zjh.seckill.domain.model.SeckillOrder;

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
    SeckillOrder saveSeckillOrder(SeckillOrderDTO seckillOrderDTO);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

}
