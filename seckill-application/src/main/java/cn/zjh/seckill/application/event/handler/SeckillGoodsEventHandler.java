package cn.zjh.seckill.application.event.handler;

import cn.zjh.seckill.application.cache.service.goods.SeckillGoodsCacheService;
import cn.zjh.seckill.application.cache.service.goods.SeckillGoodsListCacheService;
import cn.zjh.seckill.domain.event.SeckillGoodsEvent;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 接收商品事件
 * 
 * @author zjh - kayson
 */
@EventHandler
public class SeckillGoodsEventHandler implements EventHandlerI<Response, SeckillGoodsEvent> {

    public static final Logger logger = LoggerFactory.getLogger(SeckillGoodsEventHandler.class);
    
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
        seckillGoodsCacheService.tryUpdateSeckillActivityCacheByLock(seckillGoodsEvent.getId());
        seckillGoodsListCacheService.tryUpdateSeckillActivityCacheByLock(seckillGoodsEvent.getId());
        return Response.buildSuccess();
    }
    
}
