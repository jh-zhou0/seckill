package cn.zjh.seckill.activity.domain.service.impl;

import cn.zjh.seckill.activity.domain.event.SeckillActivityEvent;
import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;
import cn.zjh.seckill.activity.domain.repository.SeckillActivityRepository;
import cn.zjh.seckill.activity.domain.service.SeckillActivityDomainService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.enums.SeckillActivityStatus;
import cn.zjh.seckill.mq.MessageSenderService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 活动领域层的服务实现类
 *
 * @author zjh - kayson
 */
@Service
public class SeckillActivityDomainServiceImpl implements SeckillActivityDomainService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillActivityDomainServiceImpl.class);

    @Resource
    private SeckillActivityRepository seckillActivityRepository;
    @Resource
    private MessageSenderService messageSenderService;
    @Value("${message.mq.type}")
    private String eventType;

    @Override
    public void saveSeckillActivity(SeckillActivity seckillActivity) {
        logger.info("SeckillActivityPublish|发布秒杀活动|{}", JSON.toJSON(seckillActivity));
        if (seckillActivity == null || !seckillActivity.validateParams()) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityRepository.saveSeckillActivity(seckillActivity);
        logger.info("SeckillActivityPublish|秒杀活动已发布|{}", seckillActivity.getId());

        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(seckillActivity.getId(), seckillActivity.getStatus(), getTopicEvent());
        messageSenderService.send(seckillActivityEvent);
        logger.info("SeckillActivityPublish|秒杀活动事件已发布|{}", JSON.toJSON(seckillActivityEvent));
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
        if (id == null) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        return seckillActivityRepository.getSeckillActivityById(id);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        logger.info("SeckillActivityPublish|更新秒杀活动状态|{},{}", status, id);
        if (status == null || id == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillActivityRepository.updateStatus(status, id);
        logger.info("SeckillActivityPublish|发布秒杀活动状态事件|{},{}", status, id);
        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(id, status, getTopicEvent());
        messageSenderService.send(seckillActivityEvent);
        logger.info("SeckillActivityPublish|秒杀活动事件已发布|{}", id);
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent() {
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_ACTIVITY : SeckillConstants.TOPIC_EVENT_COLA;
    }

}