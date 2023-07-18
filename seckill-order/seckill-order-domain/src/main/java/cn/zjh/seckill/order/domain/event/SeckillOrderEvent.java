package cn.zjh.seckill.order.domain.event;

import cn.zjh.seckill.common.event.SeckillBaseEvent;

/**
 * 秒杀订单事件模型
 * 
 * @author zjh - kayson
 */
public class SeckillOrderEvent extends SeckillBaseEvent {
    
    public SeckillOrderEvent(Long id, Integer status, String topicEvent) {
        super(id, status, topicEvent);
    }
    
}