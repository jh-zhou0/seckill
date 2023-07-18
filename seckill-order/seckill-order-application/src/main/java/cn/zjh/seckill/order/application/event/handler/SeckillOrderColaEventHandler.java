package cn.zjh.seckill.order.application.event.handler;

import cn.zjh.seckill.order.domain.event.SeckillOrderEvent;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * 接收订单事件 - 基于Cola
 * 
 * @author zjh - kayson
 */
@EventHandler
@ConditionalOnProperty(name = "event.publish.type", havingValue = "cola")
public class SeckillOrderColaEventHandler implements EventHandlerI<Response, SeckillOrderEvent> {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillOrderColaEventHandler.class);
    
    @Override
    public Response execute(SeckillOrderEvent seckillOrderEvent) {
        logger.info("SeckillOrderEvent|接受订单事件|{}", JSON.toJSON(seckillOrderEvent));
        if (seckillOrderEvent == null) {
            logger.info("SeckillOrderEvent|订单参数错误");
            return Response.buildSuccess();
        }
        return Response.buildSuccess();
    }
    
}