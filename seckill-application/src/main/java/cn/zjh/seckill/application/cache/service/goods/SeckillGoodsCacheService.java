package cn.zjh.seckill.application.cache.service.goods;

import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.common.SeckillCacheService;
import cn.zjh.seckill.domain.model.SeckillGoods;

/**
 * 秒杀商品详情缓存 Service
 * 
 * @author zjh - kayson
 */
public interface SeckillGoodsCacheService extends SeckillCacheService {

    /**
     * 根商品id和版本号获取商品列表
     */
    SeckillBusinessCache<SeckillGoods> getCachedGoods(Long goodsId, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillActivityCacheByLock(Long goodsId);
    
}
