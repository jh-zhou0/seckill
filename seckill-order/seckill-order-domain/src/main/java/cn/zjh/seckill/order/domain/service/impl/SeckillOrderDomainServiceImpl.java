package cn.zjh.seckill.order.domain.service.impl;

import cn.zjh.seckill.common.event.publisher.EventPublisher;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.enums.SeckillOrderStatus;
import cn.zjh.seckill.order.domain.event.SeckillOrderEvent;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.repository.SeckillOrderRepository;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 订单领域层的服务实现
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillOrderDomainServiceImpl implements SeckillOrderDomainService {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillOrderDomainServiceImpl.class);
    
    @Resource
    private SeckillOrderRepository seckillOrderRepository;
    @Resource
    private EventPublisher eventPublisher;
    
    @Override
    public void saveSeckillOrder(SeckillOrder seckillOrder) {
        logger.info("saveSeckillOrder|下单|{}", JSON.toJSONString(seckillOrder));
        if (seckillOrder == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        boolean isSuccess = seckillOrderRepository.saveSeckillOrder(seckillOrder);
        if (isSuccess) {
            logger.info("saveSeckillOrder|创建订单成功|{}", JSON.toJSONString(seckillOrder));
            SeckillOrderEvent seckillOrderEvent = new SeckillOrderEvent(seckillOrder.getId(), seckillOrder.getStatus());
            eventPublisher.publish(seckillOrderEvent);
        }
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (userId == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        if (activityId == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByActivityId(activityId);
    }
    
}