package cn.zjh.seckill.application.cache.service.activity;

import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.common.SeckillCacheService;
import cn.zjh.seckill.domain.model.SeckillActivity;

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
