package cn.zjh.seckill.application.cache.service.goods.impl;

import cn.zjh.seckill.application.builder.goods.SeckillGoodsBuilder;
import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.goods.SeckillGoodsCacheService;
import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.domain.constants.SeckillConstants;
import cn.zjh.seckill.domain.model.SeckillGoods;
import cn.zjh.seckill.infrastructure.cache.distribute.DistributedCacheService;
import cn.zjh.seckill.infrastructure.cache.local.LocalCacheService;
import cn.zjh.seckill.infrastructure.lock.DistributedLock;
import cn.zjh.seckill.infrastructure.lock.factory.DistributedLockFactory;
import cn.zjh.seckill.infrastructure.utils.string.StringUtil;
import cn.zjh.seckill.infrastructure.utils.time.SystemClock;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 秒杀商品详情缓存 Service 实现类
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillGoodsCacheServiceImpl implements SeckillGoodsCacheService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillGoodsCacheServiceImpl.class);
    // 分布式锁的key
    private static final String SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY_";
    // 本地锁
    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Resource
    private LocalCacheService<Long, SeckillBusinessCache<SeckillGoods>> localCacheService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private DistributedLockFactory distributedLockFactory;
    
    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY, key);
    }

    @Override
    public SeckillBusinessCache<SeckillGoods> getCachedGoods(Long goodsId, Long version) {
        // 先从本地缓存获取
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = localCacheService.getIfPresent(goodsId);
        if (seckillGoodsCache != null) {
            // 版本号为空
            if (version == null) {
                logger.info("SeckillGoodsCache|命中本地缓存|{}", goodsId);
                return seckillGoodsCache;
            }
            // 传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillGoodsCache.getVersion()) <= 0) {
                logger.info("SeckillGoodsCache|命中本地缓存|{}", goodsId);
                return seckillGoodsCache;
            }
            // 传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillGoodsCache.getVersion()) > 0) {
                logger.info("SeckillGoodsCache|命中本地缓存|{}", goodsId);
                return getDistributedCache(goodsId);
            }
        }
        return getDistributedCache(goodsId);
    }

    /**
     * 从分布式缓存中获取
     */
    private SeckillBusinessCache<SeckillGoods> getDistributedCache(Long goodsId) {
        logger.info("SeckillGoodsCache|读取分布式缓存|{}", goodsId);
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = SeckillGoodsBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillGoods.class);
        // 分布式缓存中没有数据
        if (seckillGoodsCache == null) {
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillGoodsCache = tryUpdateSeckillActivityCacheByLock(goodsId);
        }
        // 获取的数据不为空，并且不需要重试
        if (seckillGoodsCache != null && !seckillGoodsCache.isRetryLater()) {
            // 获取本地锁，更新本地缓存
            if (localCacheUpdateLock.tryLock()) {
                try {
                    localCacheService.put(goodsId, seckillGoodsCache);
                    logger.info("SeckillGoodsCache|本地缓存已经更新|{}", goodsId);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillGoodsCache;
    }

    @Override
    public SeckillBusinessCache<SeckillGoods> tryUpdateSeckillActivityCacheByLock(Long goodsId) {
        logger.info("SeckillGoodsCache|更新分布式缓存|{}", goodsId);
        // 获取分布式锁
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_GOODS_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(goodsId)));
        try {
            boolean isSuccessLock = lock.tryLock(2, 5, TimeUnit.SECONDS);
            // 1.未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isSuccessLock) {
                return new SeckillBusinessCache<SeckillGoods>().retryLater();
            }
            // 2.获取分布式锁成功，从数据库获取数据
            SeckillGoods seckillGoods = seckillGoodsService.getSeckillGoodsById(goodsId);
            SeckillBusinessCache<SeckillGoods> seckillGoodsCache;
            if (seckillGoods == null) {
                seckillGoodsCache = new SeckillBusinessCache<SeckillGoods>().notExist();
            } else {
                seckillGoodsCache = new SeckillBusinessCache<SeckillGoods>().with(seckillGoods).withVersion(SystemClock.millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(goodsId), JSON.toJSONString(seckillGoodsCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillGoodsCache|分布式缓存已经更新|{}", goodsId);
            return seckillGoodsCache;
        } catch (InterruptedException e) {
            logger.info("SeckillActivitiesCache|更新分布式缓存失败|{}", goodsId);
            return new SeckillBusinessCache<SeckillGoods>().retryLater();
        } finally {
            lock.unlock();
        }
    }
}
