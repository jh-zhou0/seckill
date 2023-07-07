package cn.zjh.seckill.activity.domain.event;

import cn.zjh.seckill.common.event.SeckillBaseEvent;

/**
 * 秒杀活动事件模型
 * 
 * @author zjh - kayson
 */
public class SeckillActivityEvent extends SeckillBaseEvent {
    
    public SeckillActivityEvent(Long id, Integer status) {
        super(id, status);
    }
    
}