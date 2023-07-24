package cn.zjh.seckill.order.application.model.task;

import cn.zjh.seckill.common.model.message.TopicMessage;
import cn.zjh.seckill.order.application.model.command.SeckillOrderCommand;
import org.apache.commons.lang3.StringUtils;

/**
 * 异步下单提交的订单任务
 * 
 * @author zjh - kayson
 */
public class SeckillOrderTask extends TopicMessage {

    // 订单任务id
    private String orderTaskId;
    // 用户id
    private Long userId;
    // 提交的订单数据
    private SeckillOrderCommand seckillOrderCommand;

    public SeckillOrderTask() {
    }

    public SeckillOrderTask(String destination, String orderTaskId, Long userId, SeckillOrderCommand seckillOrderCommand) {
        super(destination);
        this.orderTaskId = orderTaskId;
        this.userId = userId;
        this.seckillOrderCommand = seckillOrderCommand;
    }

    public String getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(String orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SeckillOrderCommand getSeckillOrderCommand() {
        return seckillOrderCommand;
    }

    public void setSeckillOrderCommand(SeckillOrderCommand seckillOrderCommand) {
        this.seckillOrderCommand = seckillOrderCommand;
    }

    public boolean isEmpty(){
        return StringUtils.isEmpty(this.getDestination())
                || StringUtils.isEmpty(orderTaskId)
                || userId == null
                || seckillOrderCommand == null;
    }
}
