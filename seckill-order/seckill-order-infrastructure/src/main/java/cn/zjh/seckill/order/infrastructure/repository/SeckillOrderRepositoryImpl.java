package cn.zjh.seckill.order.infrastructure.repository;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.repository.SeckillOrderRepository;
import cn.zjh.seckill.order.infrastructure.mapper.SeckillOrderMapper;
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
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
       return seckillOrderMapper.saveSeckillOrder(seckillOrder) == 1;
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