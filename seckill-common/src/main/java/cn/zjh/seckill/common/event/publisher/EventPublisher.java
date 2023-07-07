package cn.zjh.seckill.common.event.publisher;

import com.alibaba.cola.event.DomainEventI;

/**
 * 事件发布器
 * 
 * @author zjh - kayson
 */
public interface EventPublisher {

    /**
     * 发布事件
     */
    void publish(DomainEventI domainEvent);

}
