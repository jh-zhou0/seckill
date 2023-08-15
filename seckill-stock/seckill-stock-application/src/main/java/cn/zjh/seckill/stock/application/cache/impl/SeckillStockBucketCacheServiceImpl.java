package cn.zjh.seckill.stock.application.cache.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.cache.local.LocalCacheService;
import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.lock.DistributedLock;
import cn.zjh.seckill.common.lock.factory.DistributedLockFactory;
import cn.zjh.seckill.common.utils.string.StringUtil;
import cn.zjh.seckill.common.utils.time.SystemClock;
import cn.zjh.seckill.stock.application.builder.SeckillStockBucketBuilder;
import cn.zjh.seckill.stock.application.cache.SeckillStockBucketCacheService;
import cn.zjh.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import cn.zjh.seckill.stock.domain.model.entity.SeckillStockBucket;
import cn.zjh.seckill.stock.domain.service.SeckillStockBucketDomainService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 分桶库存缓存 Service 实现类
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillStockBucketCacheServiceImpl implements SeckillStockBucketCacheService {

    private final static Logger logger = LoggerFactory.getLogger(SeckillStockBucketCacheServiceImpl.class);

    // 更新活动时获取分布式锁使用
    private static final String SECKILL_GOODS_STOCK_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_STOCK_UPDATE_CACHE_LOCK_KEY_";
    // 本地可重入锁
    private final Lock localCacheUpdateLock = new ReentrantLock();

    @Resource
    private LocalCacheService<Long, SeckillBusinessCache<SeckillStockBucketDTO>> localCacheService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillStockBucketDomainService seckillStockBucketDomainService;
    @Resource
    private DistributedLockFactory distributedLockFactory;
    
    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SeckillConstants.SECKILL_STOCK_CACHE_KEY, key);
    }

    @Override
    public SeckillBusinessCache<SeckillStockBucketDTO> getTotalStockBuckets(Long goodsId, Long version) {
        // 先从本地缓存中获取数据
        SeckillBusinessCache<SeckillStockBucketDTO> seckillStockBucketCache = localCacheService.getIfPresent(goodsId);
        if (seckillStockBucketCache != null) {
            // 传递的版本号为空，则直接返回本地缓存中的数据
            if (version == null){
                logger.info("SeckillStockBucketCache|命中本地缓存|{}", goodsId);
                return seckillStockBucketCache;
            }
            // 传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillStockBucketCache.getVersion()) <= 0){
                logger.info("SeckillStockBucketCache|命中本地缓存|{}", goodsId);
                return seckillStockBucketCache;
            }
            // 传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillStockBucketCache.getVersion()) > 0){
                return getDistributedCache(goodsId);
            }
        }
        return getDistributedCache(goodsId);
    }

    /**
     * 从分布式缓存中获取数据
     */
    private SeckillBusinessCache<SeckillStockBucketDTO> getDistributedCache(Long goodsId) {
        logger.info("SeckillStockBucketCache|读取分布式缓存|{}", goodsId);
        // 从分布式缓存中获取数据
        SeckillBusinessCache<SeckillStockBucketDTO> seckillStockBucketCache = SeckillStockBucketBuilder.getSeckillBusinessCache(
                distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillStockBucketDTO.class);
        // 分布式缓存中没有数据
        if (seckillStockBucketCache == null){
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillStockBucketCache = tryUpdateSeckillStockBucketCacheByLock(goodsId, true);
        }
        // 获取的数据不为空，并且不需要重试
        if (seckillStockBucketCache != null && !seckillStockBucketCache.isRetryLater()){
            // 获取本地锁，更新本地缓存
            if (localCacheUpdateLock.tryLock()){
                try {
                    localCacheService.put(goodsId, seckillStockBucketCache);
                    logger.info("SeckillStockBucketCache|本地缓存已经更新|{}", goodsId);
                }finally {
                    localCacheUpdateLock.unlock();
                }
            }
        }
        return seckillStockBucketCache;
    }

    @Override
    public SeckillBusinessCache<SeckillStockBucketDTO> tryUpdateSeckillStockBucketCacheByLock(Long goodsId, boolean doubleCheck) {
        logger.info("SeckillStockBucketCache|更新分布式缓存|{}", goodsId);
        // 获取分布式锁
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_GOODS_STOCK_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(goodsId)));
        try {
            boolean isLockSuccess = lock.tryLock(2, 5, TimeUnit.SECONDS);
            // 未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isLockSuccess){
                return new SeckillBusinessCache<SeckillStockBucketDTO>().retryLater();
            }
            SeckillBusinessCache<SeckillStockBucketDTO> seckillStockBucketCache;
            if (doubleCheck) {
                // 获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程在等待2秒的过程中，
                // 前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据。
                seckillStockBucketCache = SeckillStockBucketBuilder.getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(goodsId)), SeckillStockBucketDTO.class);
                if (seckillStockBucketCache != null) {
                    return seckillStockBucketCache;
                }
            }
            SeckillStockBucketDTO seckillStockBucketDTO = getSeckillStockBucketDTO(goodsId);
            if (seckillStockBucketDTO == null){
                seckillStockBucketCache = new SeckillBusinessCache<SeckillStockBucketDTO>().notExist();
            }else{
                seckillStockBucketCache = new SeckillBusinessCache<SeckillStockBucketDTO>().with(seckillStockBucketDTO).withVersion(SystemClock.millisClock().now());
            }
            // 将数据保存到分布式缓存
            distributedCacheService.put(buildCacheKey(goodsId), JSON.toJSONString(seckillStockBucketCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillStockBucketCache|分布式缓存已经更新|{}", goodsId);
            return seckillStockBucketCache;
            // 将数据放入分布式缓存
        } catch (InterruptedException e) {
            logger.error("SeckillStockBucketCache|更新分布式缓存失败|{}", goodsId);
            return new SeckillBusinessCache<SeckillStockBucketDTO>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    private SeckillStockBucketDTO getSeckillStockBucketDTO(Long goodsId) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        List<SeckillStockBucket> buckets = seckillStockBucketDomainService.getBucketsByGoodsId(goodsId);
        if (CollectionUtils.isNotEmpty(buckets)){
            int availableStock = buckets.stream().mapToInt(SeckillStockBucket::getAvailableStock).sum();
            int totalStock = buckets.stream().mapToInt(SeckillStockBucket::getInitialStock).sum();
            return new SeckillStockBucketDTO(totalStock, availableStock, buckets);
        }
        return null;
    }
}
