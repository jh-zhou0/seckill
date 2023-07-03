package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.builder.common.activity.SeckillActivityBuilder;
import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.application.cache.service.activity.SeckillActivityCacheService;
import cn.zjh.seckill.application.cache.service.activity.SeckillActivityListCacheService;
import cn.zjh.seckill.application.service.SeckillActivityService;
import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.dto.SeckillActivityDTO;
import cn.zjh.seckill.domain.enums.SeckillActivityStatus;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillActivity;
import cn.zjh.seckill.domain.repository.SeckillActivityRepository;
import cn.zjh.seckill.infrastructure.utils.beans.BeanUtil;
import cn.zjh.seckill.infrastructure.utils.id.SnowFlakeFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {
    
    @Resource
    private SeckillActivityRepository seckillActivityRepository;
    @Resource
    private SeckillActivityListCacheService seckillActivityListCacheService;
    @Resource
    private SeckillActivityCacheService seckillActivityCacheService;
    
    @Override
    public void saveSeckillActivityDTO(SeckillActivityDTO seckillActivityDTO) {
        if (seckillActivityDTO == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtil.copyProperties(seckillActivityDTO, seckillActivity);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityRepository.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityRepository.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityRepository.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityRepository.getSeckillActivityById(id);
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillActivityRepository.updateStatus(status, id);
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache = seckillActivityListCacheService.getCachedActivities(status, version);
        // 活动不存在
        if (!seckillActivityListCache.isExist()) {
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillActivityListCache.isRetryLater()) {
            throw new SeckillException(HttpCode.RETRY_LATER);
        }
        return seckillActivityListCache.getData().stream().map(seckillActivity -> {
            SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
            BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
            seckillActivityDTO.setVersion(seckillActivityListCache.getVersion());
            return seckillActivityDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        if (id == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillActivity> seckillActivityCache  = seckillActivityCacheService.getCachedSeckillActivity(id, version);
        // 缓存中的活动不存在
        if (!seckillActivityCache.isExist()) {
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        // 稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivityCache.isRetryLater()) {
            throw new SeckillException(HttpCode.RETRY_LATER);
        }
        SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivityCache.getData());
        seckillActivityDTO.setVersion(seckillActivityCache.getVersion());
        return seckillActivityDTO;
    }

}
