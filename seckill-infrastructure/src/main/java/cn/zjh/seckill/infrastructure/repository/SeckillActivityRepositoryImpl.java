package cn.zjh.seckill.infrastructure.repository;

import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillActivity;
import cn.zjh.seckill.domain.repository.SeckillActivityRepository;
import cn.zjh.seckill.infrastructure.mapper.SeckillActivityMapper;
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
    public int saveSeckillActivity(SeckillActivity seckillActivity) {
        if (seckillActivity == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        return seckillActivityMapper.saveSeckillActivity(seckillActivity);
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
    public int updateStatus(Integer status, Long id) {
        return seckillActivityMapper.updateStatus(status, id);
    }
    
}
