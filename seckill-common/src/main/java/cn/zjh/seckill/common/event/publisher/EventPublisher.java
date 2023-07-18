package cn.zjh.seckill.common.event.publisher;

import cn.zjh.seckill.common.event.SeckillBaseEvent;

/**
 * 事件发布器
 * 
 * @author zjh - kayson
 */
public interface EventPublisher {

    /**
     * 发布事件
     */
    void publish(SeckillBaseEvent domainEvent);

}
