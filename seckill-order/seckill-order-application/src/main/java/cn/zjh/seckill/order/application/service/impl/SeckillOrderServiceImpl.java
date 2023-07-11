package cn.zjh.seckill.order.application.service.impl;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.application.service.SeckillOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    private SeckillPlaceOrderService seckillPlaceOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (seckillOrderCommand == null) {  
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillPlaceOrderService.placeOrder(userId, seckillOrderCommand, SnowFlakeFactory.getSnowFlakeFromCache().nextId());
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderDomainService.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderDomainService.getSeckillOrderByActivityId(activityId);
    }
    
}