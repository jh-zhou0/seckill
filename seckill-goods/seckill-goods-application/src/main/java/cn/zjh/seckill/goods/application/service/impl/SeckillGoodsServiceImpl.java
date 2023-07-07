package cn.zjh.seckill.goods.application.service.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.cache.local.LocalCacheService;
import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillActivityDTO;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.common.model.enums.SeckillGoodsStatus;
import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;
import cn.zjh.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import cn.zjh.seckill.goods.application.builder.SeckillGoodsBuilder;
import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsCacheService;
import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsListCacheService;
import cn.zjh.seckill.goods.application.command.SeckillGoodsCommand;
import cn.zjh.seckill.goods.application.service.SeckillGoodsService;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;
import cn.zjh.seckill.goods.domain.service.SeckillGoodsDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    
    @Resource
    private SeckillGoodsDomainService seckillGoodsDomainService;
    @DubboReference(version = "1.0.0")
    private SeckillActivityDubboService seckillActivityDubboService;
    @Resource
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    @Resource
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private LocalCacheService<String, SeckillGoods> localCacheService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand) {
        if (seckillGoodsCommand == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivityDTO seckillActivity = seckillActivityDubboService.getSeckillActivity(seckillGoodsCommand.getActivityId(), seckillGoodsCommand.getVersion());
        if (seckillActivity == null) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillGoods seckillGoods = SeckillGoodsBuilder.toSeckillGoods(seckillGoodsCommand);
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        seckillGoods.setAvailableStock(seckillGoodsCommand.getInitialStock());
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        // 将商品的库存同步到redis
        String stockKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillGoods.getId()));
        distributedCacheService.put(stockKey, seckillGoods.getAvailableStock());
        // 将商品限购同步到Redis
        String limitKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, String.valueOf(seckillGoods.getId()));
        distributedCacheService.put(limitKey, seckillGoods.getLimitNum());
        // 保存商品
        seckillGoodsDomainService.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsDomainService.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsDomainService.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer status, Long id) {
        if (Objects.equals(status, SeckillGoodsStatus.OFFLINE.getCode())) {
            // 清空缓存
            clearCache(String.valueOf(id));
        }
        seckillGoodsDomainService.updateStatus(status, id);
    }

    /**
     * 清空缓存的商品数据
     */
    private void clearCache(String id) {
        // 清除本地缓存中的商品
        String goodsKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_KEY_PREFIX, id);
        localCacheService.delete(goodsKey);
        // 清除redis缓存缓存中的商品库存
        distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, id));
        // 清除redis缓存中商品信息
        distributedCacheService.delete(goodsKey);
        // 清除redis缓存商品的限购信息
        distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, id));
    }

    @Override
    public void updateAvailableStock(Integer count, Long id) {
        seckillGoodsDomainService.updateAvailableStock(count, id);
    }

    @Override
    public boolean updateDBAvailableStock(Integer count, Long id) {
        return seckillGoodsDomainService.updateDBAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsDomainService.getAvailableStockById(id);
    }

    @Override
    public List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version) {
        if (activityId == null) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = seckillGoodsListCacheService.getCachedGoodsList(activityId, version);
        if (!seckillGoodsListCache.isExist()) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillGoodsListCache.isRetryLater()) {
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        return seckillGoodsListCache.getData().stream().map(seckillGoods -> {
            SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoods);
            seckillGoodsDTO.setVersion(seckillGoodsListCache.getVersion());
            return seckillGoodsDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        if (id == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = seckillGoodsCacheService.getCachedGoods(id, version);
        if (!seckillGoodsCache.isExist()) {
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillGoodsCache.isRetryLater()) {
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoodsCache.getData());
        seckillGoodsDTO.setVersion(seckillGoodsCache.getVersion());
        return seckillGoodsDTO;
    }

}