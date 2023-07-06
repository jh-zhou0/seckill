package cn.zjh.seckill.interfaces.controller;

import cn.zjh.seckill.application.command.SeckillOrderCommand;
import cn.zjh.seckill.application.service.SeckillOrderService;
import cn.zjh.seckill.domain.code.ErrorCode;
import cn.zjh.seckill.domain.model.SeckillOrder;
import cn.zjh.seckill.domain.response.ResponseMessage;
import cn.zjh.seckill.domain.response.ResponseMessageBuilder;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "/saveSeckillOrder", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<Long> saveSeckillOrder(@RequestAttribute Long userId, SeckillOrderCommand seckillOrderCommand) {
        Long orderId = seckillOrderService.saveSeckillOrder(userId, seckillOrderCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), orderId);
    }

    /**
     * 获取用户维度的订单列表
     */
    @RequestMapping(value = "/getSeckillOrderByUserId", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByUserId(Long userId) {
        List<SeckillOrder> seckillOrderList = seckillOrderService.getSeckillOrderByUserId(userId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderList);
    }

    /**
     * 获取活动维度的订单列表
     */
    @RequestMapping(value = "/getSeckillOrderByActivityId", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByActivityId(Long activityId) {
        List<SeckillOrder> seckillOrderList = seckillOrderService.getSeckillOrderByActivityId(activityId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderList);
    }

}
