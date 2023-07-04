package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.builder.goods.SeckillGoodsBuilder;
import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.goods.SeckillGoodsCacheService;
import cn.zjh.seckill.application.cache.service.goods.SeckillGoodsListCacheService;
import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.dto.SeckillGoodsDTO;
import cn.zjh.seckill.domain.enums.SeckillGoodsStatus;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillActivity;
import cn.zjh.seckill.domain.model.SeckillGoods;
import cn.zjh.seckill.domain.repository.SeckillActivityRepository;
import cn.zjh.seckill.domain.repository.SeckillGoodsRepository;
import cn.zjh.seckill.infrastructure.utils.beans.BeanUtil;
import cn.zjh.seckill.infrastructure.utils.id.SnowFlakeFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    
    @Resource
    private SeckillGoodsRepository seckillGoodsRepository;
    @Resource
    private SeckillActivityRepository seckillActivityRepository;
    @Resource
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    @Resource
    private SeckillGoodsCacheService seckillGoodsCacheService;
    
    @Override
    public void saveSeckillGoods(SeckillGoodsDTO seckillGoodsDTO) {
        if (seckillGoodsDTO == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = seckillActivityRepository.getSeckillActivityById(seckillGoodsDTO.getActivityId());
        if (seckillActivity == null) {
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsDTO, seckillGoods);
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        seckillGoods.setAvailableStock(seckillGoodsDTO.getInitialStock());
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        seckillGoodsRepository.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsRepository.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsRepository.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillGoodsRepository.updateStatus(status, id);
    }

    @Override
    public int updateAvailableStock(Integer count, Long id) {
        return seckillGoodsRepository.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsRepository.getAvailableStockById(id);
    }

    @Override
    public List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version) {
        if (activityId == null) {
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = seckillGoodsListCacheService.getCachedGoodsList(activityId, version);
        if (!seckillGoodsListCache.isExist()) {
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillGoodsListCache.isRetryLater()) {
            throw new SeckillException(HttpCode.RETRY_LATER);
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
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = seckillGoodsCacheService.getCachedGoods(id, version);
        if (!seckillGoodsCache.isExist()) {
            throw new SeckillException(HttpCode.GOODS_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillGoodsCache.isRetryLater()) {
            throw new SeckillException(HttpCode.RETRY_LATER);
        }
        SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoodsCache.getData());
        seckillGoodsDTO.setVersion(seckillGoodsCache.getVersion());
        return seckillGoodsDTO;
    }

}
