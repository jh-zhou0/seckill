package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.command.SeckillOrderCommand;
import cn.zjh.seckill.application.order.place.SeckillPlaceOrderService;
import cn.zjh.seckill.application.service.SeckillOrderService;
import cn.zjh.seckill.domain.code.ErrorCode;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillOrder;
import cn.zjh.seckill.domain.service.SeckillOrderDomainService;
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
        return seckillPlaceOrderService.placeOrder(userId, seckillOrderCommand);
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
