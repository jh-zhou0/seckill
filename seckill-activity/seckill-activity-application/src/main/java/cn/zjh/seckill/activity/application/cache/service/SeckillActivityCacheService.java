package cn.zjh.seckill.activity.application.cache.service;

import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;
import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.cache.service.SeckillCacheService;

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
    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId, boolean doubleCheck);

}
