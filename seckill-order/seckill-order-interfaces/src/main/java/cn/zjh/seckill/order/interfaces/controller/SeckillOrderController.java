package cn.zjh.seckill.order.interfaces.controller;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.model.dto.SeckillOrderSubmitDTO;
import cn.zjh.seckill.common.response.ResponseMessage;
import cn.zjh.seckill.common.response.ResponseMessageBuilder;
import cn.zjh.seckill.order.application.model.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.model.command.SeckillOrderTaskCommand;
import cn.zjh.seckill.order.application.service.SeckillOrderService;
import cn.zjh.seckill.order.application.service.SeckillSubmitOrderService;
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
    @Resource
    private SeckillSubmitOrderService seckillSubmitOrderService;

    /**
     * 保存秒杀订单
     */
    @PostMapping("/saveSeckillOrder")
    public ResponseMessage<SeckillOrderSubmitDTO> saveSeckillOrder(@RequestAttribute Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillOrderSubmitDTO seckillOrderSubmitDTO = seckillSubmitOrderService.saveSeckillOrder(userId, seckillOrderCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderSubmitDTO);
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

    /**
     * 异步下单时，会返回任务id，如果下单返回的是任务id，则调用此接口获取订单id，如果前端多次调用此接口返回的数据仍为为任务id，则表示下单失败，不再调用
     * 如果调用此接口（含重试），返回了订单id，则异步下单获取到了与同步下单相同的数据，就可以通过订单id来查询订单信息了。
     */
    @GetMapping("/getSeckillOrderSubmitDTO")
    public ResponseMessage<SeckillOrderSubmitDTO> get(@RequestAttribute Long userId, SeckillOrderTaskCommand seckillOrderTaskCommand) {
        SeckillOrderSubmitDTO seckillOrderSubmitDTO = seckillOrderService.getSeckillOrderSubmitDTOByTaskId(seckillOrderTaskCommand.getOrderTaskId(), userId, seckillOrderTaskCommand.getGoodsId());
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderSubmitDTO);
    }
}