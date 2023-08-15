package cn.zjh.seckill.stock.application.cache;

import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.cache.service.SeckillCacheService;
import cn.zjh.seckill.stock.application.model.dto.SeckillStockBucketDTO;

/**
 * 分桶库存缓存 Service
 * 
 * @author zjh - kayson
 */
public interface SeckillStockBucketCacheService extends SeckillCacheService {

    /**
     * 缓存库存分桶信息
     * 
     * @param goodsId 商品id
     * @param version 版本号
     * @return 分桶库存缓存信息
     */
    SeckillBusinessCache<SeckillStockBucketDTO> getTotalStockBuckets(Long goodsId, Long version);

    /**
     * 更新分桶库存缓存
     * 
     * @param goodsId 商品id
     * @param doubleCheck 是否使用双重检查
     * @return 分桶库存缓存信息
     */
    SeckillBusinessCache<SeckillStockBucketDTO> tryUpdateSeckillStockBucketCacheByLock(Long goodsId, boolean doubleCheck);
    
}
