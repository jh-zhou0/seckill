package cn.zjh.seckill.order.application.event.handler;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.order.domain.event.SeckillOrderEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 接受订单事件 - 基于RocketMQ
 * 
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_ORDER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ORDER)
public class SeckillOrderRocketMQEventHandler implements RocketMQListener<String> {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillOrderRocketMQEventHandler.class);
    
    @Override
    public void onMessage(String message) {
        if (StringUtils.isEmpty(message)){
            logger.info("RocketMQ|SeckillOrderEvent|接收订单事件为空");
            return;
        }
        SeckillOrderEvent seckillOrderEvent = getEventMessage(message);
        if (seckillOrderEvent.getId() == null){
            logger.info("RocketMQ|SeckillOrderEvent|订单参数错误");
        }
        logger.info("RocketMQ|SeckillOrderEvent|接收订单事件|{}", JSON.toJSON(seckillOrderEvent));
    }

    private SeckillOrderEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillOrderEvent.class);
    }

}
