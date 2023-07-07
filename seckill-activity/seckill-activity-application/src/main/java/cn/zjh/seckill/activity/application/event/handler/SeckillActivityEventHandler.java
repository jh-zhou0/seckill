package cn.zjh.seckill.activity.application.event.handler;

import cn.zjh.seckill.activity.application.cache.service.SeckillActivityCacheService;
import cn.zjh.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import cn.zjh.seckill.activity.domain.event.SeckillActivityEvent;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 接收活动事件
 * 
 * @author zjh - kayson
 */
@EventHandler
public class SeckillActivityEventHandler implements EventHandlerI<Response, SeckillActivityEvent> {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillActivityEventHandler.class);
    
    @Resource
    private SeckillActivityCacheService seckillActivityCacheService;
    @Resource
    private SeckillActivityListCacheService seckillActivityListCacheService;
    
    @Override
    public Response execute(SeckillActivityEvent seckillActivityEvent) {
        logger.info("SeckillActivityEvent|接受活动事件|{}", JSON.toJSON(seckillActivityEvent));
        if (seckillActivityEvent == null) {
            logger.info("SeckillActivityEvent|事件参数错误");
            return Response.buildSuccess();
        }
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId());
        seckillActivityListCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getStatus());
        return Response.buildSuccess();
    }
    
}