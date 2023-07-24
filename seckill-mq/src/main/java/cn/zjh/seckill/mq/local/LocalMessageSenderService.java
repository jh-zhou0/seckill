package cn.zjh.seckill.mq.local;

import cn.zjh.seckill.common.model.message.TopicMessage;
import cn.zjh.seckill.mq.MessageSenderService;
import com.alibaba.cola.event.EventBusI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 本地消息发送
 * 
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class LocalMessageSenderService implements MessageSenderService {
    
    @Resource
    private EventBusI eventBusI;
    
    @Override
    public boolean send(TopicMessage message) {
        eventBusI.fire(message);
        return true;
    }
    
}
