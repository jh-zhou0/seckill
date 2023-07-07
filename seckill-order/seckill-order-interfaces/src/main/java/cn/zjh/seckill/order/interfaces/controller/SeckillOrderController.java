package cn.zjh.seckill.order.interfaces.controller;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.response.ResponseMessage;
import cn.zjh.seckill.common.response.ResponseMessageBuilder;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.service.SeckillOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单
 *
 * @author zjh - kayson
 */
@RestController
@RequestMapping("/order")
public class SeckillOrderController {

    @Resource
    private SeckillOrderService seckillOrderService;

    /**
     * 保存秒杀订单
     */
    @PostMapping("/saveSeckillOrder")
    public ResponseMessage<Long> saveSeckillOrder(@RequestAttribute Long userId, SeckillOrderCommand seckillOrderCommand) {
        Long orderId = seckillOrderService.saveSeckillOrder(userId, seckillOrderCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), orderId);
    }

    /**
     * 获取用户维度的订单列表
     */
    @GetMapping("/getSeckillOrderByUserId")
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByUserId(Long userId) {
        List<SeckillOrder> seckillOrderList = seckillOrderService.getSeckillOrderByUserId(userId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderList);
    }

    /**
     * 获取活动维度的订单列表
     */
    @GetMapping("/getSeckillOrderByActivityId")
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByActivityId(Long activityId) {
        List<SeckillOrder> seckillOrderList = seckillOrderService.getSeckillOrderByActivityId(activityId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderList);
    }

}