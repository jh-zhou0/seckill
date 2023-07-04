package cn.zjh.seckill.application.cache.service.activity.impl;

import cn.zjh.seckill.application.builder.activity.SeckillActivityBuilder;
import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.activity.SeckillActivityListCacheService;
import cn.zjh.seckill.domain.constants.SeckillConstants;
import cn.zjh.seckill.domain.model.SeckillActivity;
import cn.zjh.seckill.domain.repository.SeckillActivityRepository;
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
 * 秒杀活动列表缓存 Service 实现类
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillActivityListCacheServiceImpl implements SeckillActivityListCacheService {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillActivityListCacheServiceImpl.class);
    // 分布式锁的key
    private static final String SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY_";
    // 本地锁
    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Resource
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillActivity>>> localCacheService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillActivityRepository seckillActivityRepository;
    @Resource
    private DistributedLockFactory distributedLockFactory;

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache  = localCacheService.getIfPresent(status.longValue());
        if (seckillActivityListCache != null) {
            // 版本号为空
            if (version == null) {
                logger.info("SeckillActivitiesCache|命中本地缓存|{}", status);
                return seckillActivityListCache;
            }
            // 传递过来的版本小于或等于缓存中的版本号
            if (version.compareTo(seckillActivityListCache.getVersion()) <= 0) {
                logger.info("SeckillActivitiesCache|命中本地缓存|{}", status);
                return seckillActivityListCache;
            }
            if (version.compareTo(seckillActivityListCache.getVersion()) > 0){
                return getDistributedCache(status);
            }
        }
        return getDistributedCache(status);
    }

    /**
     * 获取分布式缓存中的数据
     */
    private SeckillBusinessCache<List<SeckillActivity>> getDistributedCache(Integer status) {
        logger.info("SeckillActivitiesCache|读取分布式缓存|{}", status);
        SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache = SeckillActivityBuilder
                .getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(status)), SeckillActivity.class);
        // 分布式缓存为空
        if (seckillActivityListCache == null) {
            seckillActivityListCache = tryUpdateSeckillActivityCacheByLock(status);
        }
        // 分布式缓存不为空，且无需重试
        if (seckillActivityListCache != null && !seckillActivityListCache.isRetryLater()) {
            // 获取本地锁
            if (localCacheUpdateLock.tryLock()) {
                try {
                    // 更新本地缓存
                    localCacheService.put(status.longValue(), seckillActivityListCache);
                    logger.info("SeckillActivitiesCache|本地缓存已经更新|{}", status);
                } finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillActivityListCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Integer status) {
        logger.info("SeckillActivitiesCache|更新分布式缓存|{}", status);
        // 注意，分布式锁的key与Cache的key不同
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(status)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            // 1.获取分布式锁失败，稍后重试，未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isLockSuccess) {
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }
            // 2.获取分布式锁成功
            // 从数据库获取数据
            List<SeckillActivity> seckillActivityList = seckillActivityRepository.getSeckillActivityList(status);
            SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache;
            if (CollectionUtils.isEmpty(seckillActivityList)) { // 数据库没有数据，返回不存在
                seckillActivityListCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            } else {
                seckillActivityListCache =  new SeckillBusinessCache<List<SeckillActivity>>().with(seckillActivityList).withVersion(SystemClock.millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(status), JSON.toJSONString(seckillActivityListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivitiesCache|分布式缓存已经更新|{}", status);
            return seckillActivityListCache;
        } catch (InterruptedException e) {
            logger.info("SeckillActivitiesCache|更新分布式缓存失败|{}", status);
            return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY, key);
    }
    
}
