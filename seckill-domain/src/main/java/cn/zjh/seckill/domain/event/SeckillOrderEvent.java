package cn.zjh.seckill.domain.event;

/**
 * 秒杀订单事件模型
 * 
 * @author zjh - kayson
 */
public class SeckillOrderEvent extends SeckillBaseEvent {
    
    public SeckillOrderEvent(Long id, Integer status) {
        super(id, status);
    }
    
}
