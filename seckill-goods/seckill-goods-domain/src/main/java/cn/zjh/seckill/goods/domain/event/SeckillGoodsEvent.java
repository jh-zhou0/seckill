package cn.zjh.seckill.goods.domain.event;

import cn.zjh.seckill.common.model.event.SeckillBaseEvent;

/**
 * 秒杀商品事件模型
 * 
 * @author zjh - kayson
 */
public class SeckillGoodsEvent extends SeckillBaseEvent {
    
    private Long activityId;
    
    public SeckillGoodsEvent(Long id, Long activityId, Integer status, String topicEvent) {
        super(id, status, topicEvent);
        this.activityId = activityId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    
}