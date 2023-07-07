package cn.zjh.seckill.activity.application.cache.service;

import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;
import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.cache.service.SeckillCacheService;

import java.util.List;

/**
 * 秒杀活动列表缓存 Service
 * 
 * @author zjh - kayson
 */
public interface SeckillActivityListCacheService extends SeckillCacheService {

    /**
     * 增加二级缓存的根据状态获取活动列表
     */
    SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Integer status);

}
