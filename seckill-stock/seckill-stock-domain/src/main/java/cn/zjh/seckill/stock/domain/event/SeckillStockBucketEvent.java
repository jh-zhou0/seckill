package cn.zjh.seckill.stock.domain.event;

import cn.zjh.seckill.common.model.event.SeckillBaseEvent;

/**
 * 商品库存事件
 * 
 * @author zjh - kayson
 */
public class SeckillStockBucketEvent extends SeckillBaseEvent {
    
    public SeckillStockBucketEvent(Long id, Integer status, String topicEvent) {
        super(id, status, topicEvent);
    }
    
}
