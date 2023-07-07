package cn.zjh.seckill.activity.application.service.impl;

import cn.zjh.seckill.activity.application.builder.SeckillActivityBuilder;
import cn.zjh.seckill.activity.application.cache.service.SeckillActivityCacheService;
import cn.zjh.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import cn.zjh.seckill.activity.application.command.SeckillActivityCommand;
import cn.zjh.seckill.activity.application.service.SeckillActivityService;
import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;
import cn.zjh.seckill.activity.domain.service.SeckillActivityDomainService;
import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillActivityDTO;
import cn.zjh.seckill.common.model.enums.SeckillActivityStatus;
import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private SeckillActivityDomainService seckillActivityDomainService;
    @Resource
    private SeckillActivityListCacheService seckillActivityListCacheService;
    @Resource
    private SeckillActivityCacheService seckillActivityCacheService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        if (seckillActivityCommand == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = SeckillActivityBuilder.toSeckillActivity(seckillActivityCommand);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityDomainService.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityDomainService.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityDomainService.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityDomainService.getSeckillActivityById(id);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        seckillActivityDomainService.updateStatus(status, id);
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivityListCache = seckillActivityListCacheService.getCachedActivities(status, version);
        // 活动不存在
        if (!seckillActivityListCache.isExist()) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillActivityListCache.isRetryLater()) {
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        return seckillActivityListCache.getData().stream().map(seckillActivity -> {
            SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivity);
            seckillActivityDTO.setVersion(seckillActivityListCache.getVersion());
            return seckillActivityDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        if (id == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillActivity> seckillActivityCache  = seckillActivityCacheService.getCachedSeckillActivity(id, version);
        // 缓存中的活动不存在
        if (!seckillActivityCache.isExist()) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        // 稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivityCache.isRetryLater()) {
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivityCache.getData());
        seckillActivityDTO.setVersion(seckillActivityCache.getVersion());
        return seckillActivityDTO;
    }

}