package cn.zjh.seckill.application.cache.service.goods.impl;

import cn.zjh.seckill.application.builder.goods.SeckillGoodsBuilder;
import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.goods.SeckillGoodsListCacheService;
import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.domain.constants.SeckillConstants;
import cn.zjh.seckill.domain.model.SeckillActivity;
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
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 秒杀商品列表缓存 Service 实现类
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillGoodsListCacheServiceImpl implements SeckillGoodsListCacheService {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillGoodsListCacheServiceImpl.class);
    // 分布式锁的key
    private static final String SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY_";
    // 本地锁
    private final Lock localCacheUpdateLock = new ReentrantLock();
    
    @Resource
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillGoods>>> localCacheService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private DistributedLockFactory distributedLockFactory;
    
    @Override
    public SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version) {
        // 从本地缓存获取数据
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = localCacheService.getIfPresent(activityId);
        if (seckillGoodsListCache != null) {
            // 版本号为空
            if (version == null) {
                logger.info("SeckillGoodsListCache|命中本地缓存|{}", activityId);
                return seckillGoodsListCache;
            }
            // 传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillGoodsListCache.getVersion()) <= 0) {
                logger.info("SeckillGoodsListCache|命中本地缓存|{}", activityId);
                return seckillGoodsListCache;
            }
            // 传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillGoodsListCache.getVersion()) > 0){
                return getDistributedCache(activityId);
            }
        }
        return getDistributedCache(activityId);
    }

    /**
     * 从分布式缓存中获取数据
     */
    private SeckillBusinessCache<List<SeckillGoods>> getDistributedCache(Long activityId) {
        logger.info("SeckillGoodsListCache|读取分布式缓存|{}", activityId);
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = SeckillGoodsBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(activityId)), SeckillGoods.class);
        // 分布式缓存为空
        if (seckillGoodsListCache == null) {
            seckillGoodsListCache =  tryUpdateSeckillActivityCacheByLock(activityId);
        }
        // 分布式缓存不为空，且无需重试
        if (seckillGoodsListCache != null && !seckillGoodsListCache.isRetryLater()) {
            // 获取本地锁
            if (localCacheUpdateLock.tryLock()) {
                try {
                    // 更新本地缓存
                    localCacheService.put(activityId, seckillGoodsListCache);
                    logger.info("SeckillGoodsListCache|本地缓存已经更新|{}", activityId);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillGoodsListCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillActivityCacheByLock(Long activityId) {
        logger.info("SeckillGoodsListCache|更新分布式缓存|{}", activityId);
        // 获取分布式锁
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(activityId)));
        try {
            boolean isLockSuccess = lock.tryLock(2, 5, TimeUnit.SECONDS);
            // 1.未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isLockSuccess) {
                return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
            }
            // 2.获取分布式锁成功，从数据库获取数据
            List<SeckillGoods> seckillGoodsList = seckillGoodsService.getSeckillGoodsByActivityId(activityId);
            SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache;
            if (CollectionUtils.isEmpty(seckillGoodsList)) {
                seckillGoodsListCache = new SeckillBusinessCache<List<SeckillGoods>>().notExist();
            } else {
                seckillGoodsListCache = new SeckillBusinessCache<List<SeckillGoods>>().with(seckillGoodsList).withVersion(SystemClock.millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(activityId), JSON.toJSONString(seckillGoodsListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillGoodsListCache|分布式缓存已经更新|{}", activityId);
            return seckillGoodsListCache;
        } catch (InterruptedException e) {
            logger.info("SeckillActivitiesCache|更新分布式缓存失败|{}", activityId);
            return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY, key);
    }
    
}
