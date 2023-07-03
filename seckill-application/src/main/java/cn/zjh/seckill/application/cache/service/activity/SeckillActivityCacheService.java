package cn.zjh.seckill.application.cache.service.activity;

import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.common.SeckillCacheService;
import cn.zjh.seckill.domain.model.SeckillActivity;

/**
 * 秒杀活动详情缓存 Service
 * 
 * @author zjh - kayson
 */
public interface SeckillActivityCacheService extends SeckillCacheService {

    /**
     * 根据id和版本获取活动信息
     */
    SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId);

}
