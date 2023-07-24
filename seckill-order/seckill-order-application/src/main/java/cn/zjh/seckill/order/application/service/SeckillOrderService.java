package cn.zjh.seckill.order.application.service;

import cn.zjh.seckill.common.model.dto.SeckillOrderSubmitDTO;
import cn.zjh.seckill.common.model.message.ErrorMessage;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

/**
 * 订单
 * 
 * @author zjh - kayson
 */
public interface SeckillOrderService {

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

    /**
     * 根据任务id获取订单号
     */
    SeckillOrderSubmitDTO getSeckillOrderSubmitDTOByTaskId(String taskId, Long userId, Long goodsId);
    
}