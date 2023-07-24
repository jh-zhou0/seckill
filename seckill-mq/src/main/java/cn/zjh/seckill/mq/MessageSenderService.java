package cn.zjh.seckill.mq;

import cn.zjh.seckill.common.model.message.TopicMessage;
import org.apache.rocketmq.client.producer.TransactionSendResult;

/**
 * 消息队列服务
 * 
 * @author zjh - kayson
 */
public interface MessageSenderService {

    /**
     * 发送消息
     * 
     * @param message 消息
     * @return 发送结果
     */
    boolean send(TopicMessage message);

    /**
     * 发送事务消息，主要是RocketMQ
     * 
     * @param message 事务消息
     * @param arg 其他参数
     * @return 事务消息发送结果
     */
    default TransactionSendResult sendMessageInTransaction(TopicMessage message, Object arg){
        return null;
    }
    
}
