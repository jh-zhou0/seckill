package cn.zjh.seckill.common.event.publisher;

import cn.zjh.seckill.common.event.SeckillBaseEvent;
import com.alibaba.cola.event.EventBusI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 本地事件发布 - 基于Cola
 * 
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "cola")
public class LocalDomainEventPublisher implements EventPublisher {
    
    @Resource
    private EventBusI eventBus;
    
    @Override
    public void publish(SeckillBaseEvent domainEvent) {
        eventBus.fire(domainEvent);
    }
    
}
