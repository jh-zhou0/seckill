package cn.zjh.seckill.infrastructure.repository;

import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillOrder;
import cn.zjh.seckill.domain.repository.SeckillOrderRepository;
import cn.zjh.seckill.infrastructure.mapper.SeckillOrderMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单
 * 
 * @author zjh - kayson
 */
@Repository
public class SeckillOrderRepositoryImpl implements SeckillOrderRepository {
    
    @Resource
    private SeckillOrderMapper seckillOrderMapper;
    
    @Override
    public void saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        seckillOrderMapper.saveSeckillOrder(seckillOrder);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderMapper.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderMapper.getSeckillOrderByActivityId(activityId);
    }
    
}
