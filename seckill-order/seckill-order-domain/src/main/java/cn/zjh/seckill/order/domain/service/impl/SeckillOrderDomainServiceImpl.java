package cn.zjh.seckill.order.domain.service.impl;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.enums.SeckillOrderStatus;
import cn.zjh.seckill.mq.MessageSenderService;
import cn.zjh.seckill.order.domain.event.SeckillOrderEvent;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.repository.SeckillOrderRepository;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderDomainServiceImpl.class);
    
    @Resource
    private SeckillOrderRepository seckillOrderRepository;
    @Resource
    private MessageSenderService messageSenderService;
    @Value("${message.mq.type}")
    private String eventType;
    
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
            SeckillOrderEvent seckillOrderEvent = new SeckillOrderEvent(seckillOrder.getId(), seckillOrder.getStatus(), getTopicEvent());
            messageSenderService.send(seckillOrderEvent);
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

    @Override
    public void deleteSeckillOrder(Long orderId) {
        if (orderId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (seckillOrderRepository.deleteSeckillOrder(orderId)) {
            logger.info("deleteSeckillOrder|删除订单成功|{}", orderId);
            SeckillOrderEvent seckillOrderEvent = new SeckillOrderEvent(orderId, SeckillOrderStatus.DELETED.getCode(), getTopicEvent());
            messageSenderService.send(seckillOrderEvent);
        }
    }

    @Override
    public void deleteOrder(Long orderId) {
        seckillOrderRepository.deleteOrder(orderId);
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_ORDER : SeckillConstants.TOPIC_EVENT_COLA;
    }

}