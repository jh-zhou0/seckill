package cn.zjh.seckill.activity.application.event.handler;

import cn.zjh.seckill.activity.application.cache.service.SeckillActivityCacheService;
import cn.zjh.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import cn.zjh.seckill.activity.domain.event.SeckillActivityEvent;
import cn.zjh.seckill.common.constants.SeckillConstants;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 接受活动事件 - 基于RocketMQ
 *
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_ACTIVITY_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ACTIVITY)
public class SeckillActivityRocketMQEventHandler implements RocketMQListener<String> {

    public static final Logger logger = LoggerFactory.getLogger(SeckillActivityRocketMQEventHandler.class);

    @Resource
    private SeckillActivityCacheService seckillActivityCacheService;
    @Resource
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Override
    public void onMessage(String message) {
        logger.info("RocketMQ|SeckillActivityEvent|接受活动事件|{}", message);
        if (StringUtils.isEmpty(message)) {
            logger.info("SeckillActivityEvent|事件参数错误");
            return;
        }
        SeckillActivityEvent seckillActivityEvent = getEventMessage(message);
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId(), false);
        seckillActivityListCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getStatus(), false);
    }

    private SeckillActivityEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillActivityEvent.class);
    }

}
