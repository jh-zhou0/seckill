package cn.zjh.seckill.goods.application.cache.service;

import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.cache.service.SeckillCacheService;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;

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
    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId, boolean doubleCheck);
    
}
