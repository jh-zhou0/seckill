package cn.zjh.seckill.stock.application.event.handler;

import cn.zjh.seckill.stock.application.cache.SeckillStockBucketCacheService;
import cn.zjh.seckill.stock.domain.event.SeckillStockBucketEvent;
import cn.zjh.seckill.stock.domain.model.enums.SeckillStockBucketEventType;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.Resource;

/**
 * 基于Cola的库存事件处理器
 *
 * @author zjh - kayson
 */
@EventHandler
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class SeckillStockColaEventHandler implements EventHandlerI<Response, SeckillStockBucketEvent> {
    
    private final Logger logger = LoggerFactory.getLogger(SeckillStockColaEventHandler.class);
    
    @Resource
    private SeckillStockBucketCacheService seckillStockBucketCacheService;

    @Override
    public Response execute(SeckillStockBucketEvent seckillStockBucketEvent) {
        logger.info("cola|stockEvent|接收库存事件|{}", JSON.toJSON(seckillStockBucketEvent));
        if (seckillStockBucketEvent == null || seckillStockBucketEvent.getId() == null) {
            logger.info("cola|stockEvent|库存事件参数错误");
            return Response.buildSuccess();
        }
        // 开启了库存分桶，就更新缓存数据
        if (SeckillStockBucketEventType.ENABLED.getCode().equals(seckillStockBucketEvent.getStatus())) {
            seckillStockBucketCacheService.tryUpdateSeckillStockBucketCacheByLock(seckillStockBucketEvent.getId(), false);
        }
        return Response.buildSuccess();
    }
}