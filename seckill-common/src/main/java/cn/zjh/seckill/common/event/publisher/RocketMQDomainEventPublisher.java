package cn.zjh.seckill.common.event.publisher;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.event.SeckillBaseEvent;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 基于RocketMQ发布事件
 *
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
public class RocketMQDomainEventPublisher implements EventPublisher {

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void publish(SeckillBaseEvent domainEvent) {
        // 发送事件消息给订单微服务
        rocketMQTemplate.send(domainEvent.getTopicEvent(), getEventMessage(domainEvent));
    }
    
    private Message<String> getEventMessage(SeckillBaseEvent domainEvent) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SeckillConstants.EVENT_MSG_KEY, domainEvent);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }

}
