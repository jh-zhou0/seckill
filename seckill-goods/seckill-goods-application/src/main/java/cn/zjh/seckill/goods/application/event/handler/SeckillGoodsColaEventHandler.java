package cn.zjh.seckill.goods.application.event.handler;

import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsCacheService;
import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsListCacheService;
import cn.zjh.seckill.goods.domain.event.SeckillGoodsEvent;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.annotation.Resource;

/**
 * 接收商品事件 - 基于Cola
 * 
 * @author zjh - kayson
 */
@EventHandler
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class SeckillGoodsColaEventHandler implements EventHandlerI<Response, SeckillGoodsEvent> {

    public static final Logger logger = LoggerFactory.getLogger(SeckillGoodsColaEventHandler.class);
    
    @Resource
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Resource
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Override
    public Response execute(SeckillGoodsEvent seckillGoodsEvent) {
        logger.info("SeckillGoodsEvent|接受秒杀商品事件|{}", JSON.toJSON(seckillGoodsEvent));
        if (seckillGoodsEvent == null) {
            logger.info("SeckillGoodsEvent|接受秒杀商品事件参数错误");
            return Response.buildSuccess();
        }
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        seckillGoodsListCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        return Response.buildSuccess();
    }
    
}