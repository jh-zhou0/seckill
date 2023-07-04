package cn.zjh.seckill.application.cache.service.goods;

import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.common.SeckillCacheService;
import cn.zjh.seckill.domain.model.SeckillGoods;

import java.util.List;

/**
 * 秒杀商品列表缓存 Service
 * 
 * @author zjh - kayson
 */
public interface SeckillGoodsListCacheService extends SeckillCacheService {

    /**
     * 根据活动id和版本号获取商品列表
     */
    SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillActivityCacheByLock(Long activityId);
    
}
