package cn.zjh.seckill.activity.infrastructure.repository;

import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;
import cn.zjh.seckill.activity.domain.repository.SeckillActivityRepository;
import cn.zjh.seckill.activity.infrastructure.mapper.SeckillActivityMapper;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 活动
 * 
 * @author zjh - kayson
 */
@Repository
public class SeckillActivityRepositoryImpl implements SeckillActivityRepository {
    
    @Resource
    private SeckillActivityMapper seckillActivityMapper;
    
    @Override
    public void saveSeckillActivity(SeckillActivity seckillActivity) {
        if (seckillActivity == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillActivityMapper.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityMapper.getSeckillActivityList(status);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityMapper.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }

    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityMapper.getSeckillActivityById(id);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        seckillActivityMapper.updateStatus(status, id);
    }
    
}