package cn.zjh.seckill.domain.event.publisher;

import com.alibaba.cola.event.DomainEventI;
import com.alibaba.cola.event.EventBusI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 本地事件发布
 * 
 * @author zjh - kayson
 */
@Component
public class LocalDomainEventPublisher implements EventPublisher {
    
    @Resource
    private EventBusI eventBus;
    
    @Override
    public void publish(DomainEventI domainEvent) {
        eventBus.fire(domainEvent);
    }
    
}
