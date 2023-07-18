package cn.zjh.seckill.common.event;

import com.alibaba.cola.event.DomainEventI;

/**
 * 事件基础模型
 * 
 * @author zjh - kayson
 */
public class SeckillBaseEvent implements DomainEventI {

    private Long id;
    private Integer status;
    private String topicEvent;
    
    public SeckillBaseEvent(Long id, Integer status, String topicEvent) {
        this.id = id;
        this.status = status;
        this.topicEvent = topicEvent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getTopicEvent() {
        return topicEvent;
    }

    public void setTopicEvent(String topicEvent) {
        this.topicEvent = topicEvent;
    }
}
