package cn.zjh.seckill.mq.rocketmq;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.model.message.TopicMessage;
import cn.zjh.seckill.mq.MessageSenderService;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 基于RocketMQ的消息发送服务
 * 
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
public class RocketMQMessageSenderService implements MessageSenderService {
    
    @Resource
    private RocketMQTemplate rocketMQTemplate;
    
    @Override
    public boolean send(TopicMessage message) {
        try {
            SendResult sendResult = rocketMQTemplate.syncSend(message.getDestination(), getMessage(message));
            return SendStatus.SEND_OK.equals(sendResult.getSendStatus());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public TransactionSendResult sendMessageInTransaction(TopicMessage message, Object arg) {
        return rocketMQTemplate.sendMessageInTransaction(message.getDestination(), getMessage(message), arg);
    }
    
    // 构建RocketMQ发送的消息
    private Message<String> getMessage(TopicMessage message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SeckillConstants.MSG_KEY, message);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }
}
