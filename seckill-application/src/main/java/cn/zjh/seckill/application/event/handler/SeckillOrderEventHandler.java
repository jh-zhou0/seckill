package cn.zjh.seckill.application.event.handler;

import cn.zjh.seckill.domain.event.SeckillOrderEvent;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 接收订单事件
 * 
 * @author zjh - kayson
 */
@EventHandler
public class SeckillOrderEventHandler implements EventHandlerI<Response, SeckillOrderEvent> {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillOrderEventHandler.class);
    
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
