package cn.zjh.seckill.common.model.event;

import cn.zjh.seckill.common.model.message.TopicMessage;

/**
 * 事件基础模型
 * 
 * @author zjh - kayson
 */
public class SeckillBaseEvent extends TopicMessage {

    private Long id;
    private Integer status;
    
    public SeckillBaseEvent(Long id, Integer status, String topicEvent) {
        super(topicEvent);
        this.id = id;
        this.status = status;
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
}
