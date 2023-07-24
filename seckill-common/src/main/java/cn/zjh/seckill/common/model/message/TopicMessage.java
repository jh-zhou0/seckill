package cn.zjh.seckill.common.model.message;

import com.alibaba.cola.event.DomainEventI;

/**
 * 基础消息
 * 
 * @author zjh - kayson
 */
public class TopicMessage implements DomainEventI {

    /**
     * 消息目的地，可以是消息主题
     */
    private String destination;

    public TopicMessage() {
    }

    public TopicMessage(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
