package cn.zjh.seckill.application.cache.service.activity.impl;

import cn.zjh.seckill.application.builder.activity.SeckillActivityBuilder;
import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.activity.SeckillActivityCacheService;
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

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 秒杀活动详情缓存 Service 实现
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillActivityCacheServiceImpl implements SeckillActivityCacheService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillActivityCacheServiceImpl.class);
    // 分布式锁的key
    private static final String SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY_";
    // 本地锁
    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Resource
    private LocalCacheService<Long, SeckillBusinessCache<SeckillActivity>> localCacheService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillActivityRepository seckillActivityRepository;
    @Resource
    private DistributedLockFactory distributedLockFactory;
    
    @Override
    public SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version) {
        // 先从本地缓存中获取数据
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = localCacheService.getIfPresent(activityId);
        if (seckillActivityCache != null) {
            // 传递的版本号为空，则直接返回本地缓存中的数据
            if (version == null){
                logger.info("SeckillActivityCache|命中本地缓存|{}", activityId);
                return seckillActivityCache;
            }
            // 传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillActivityCache.getVersion()) <= 0){
                logger.info("SeckillActivityCache|命中本地缓存|{}", activityId);
                return seckillActivityCache;
            }
            // 传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillActivityCache.getVersion()) > 0){
                return getDistributedCache(activityId);
            }
        }
        return getDistributedCache(activityId);
    }

    /**
     * 从分布式缓存中获取数据
     */
    private SeckillBusinessCache<SeckillActivity> getDistributedCache(Long activityId) {
        logger.info("SeckillActivityCache|读取分布式缓存|{}", activityId);
        // 从分布式缓存中获取数据
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = SeckillActivityBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(activityId)), SeckillActivity.class);
        // 分布式缓存中没有数据
        if (seckillActivityCache == null){
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillActivityCache = tryUpdateSeckillActivityCacheByLock(activityId);
        }
        // 获取的数据不为空，并且不需要重试
        if (seckillActivityCache != null && !seckillActivityCache.isRetryLater()){
            // 获取本地锁，更新本地缓存
            if (localCacheUpdateLock.tryLock()){
                try {
                    localCacheService.put(activityId, seckillActivityCache);
                    logger.info("SeckillActivityCache|本地缓存已经更新|{}", activityId);
                }finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillActivityCache;
    }

    @Override
    public SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId) {
        logger.info("SeckillActivityCache|更新分布式缓存|{}", activityId);
        // 获取分布式锁
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(activityId)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            // 未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isLockSuccess){
                return new SeckillBusinessCache<SeckillActivity>().retryLater();
            }
            SeckillActivity seckillActivity = seckillActivityRepository.getSeckillActivityById(activityId);
            SeckillBusinessCache<SeckillActivity> seckillActivityCache;
            if (seckillActivity == null){
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>().notExist();
            }else{
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>().with(seckillActivity).withVersion(SystemClock.millisClock().now());
            }
            // 将数据保存到分布式缓存
            distributedCacheService.put(buildCacheKey(activityId), JSON.toJSONString(seckillActivityCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivityCache|分布式缓存已经更新|{}", activityId);
            return seckillActivityCache;
            // 将数据放入分布式缓存
        } catch (InterruptedException e) {
            logger.error("SeckillActivityCache|更新分布式缓存失败|{}", activityId);
            return new SeckillBusinessCache<SeckillActivity>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY, key);
    }
    
}
