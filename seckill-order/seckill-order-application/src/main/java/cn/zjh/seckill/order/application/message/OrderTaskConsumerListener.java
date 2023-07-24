package cn.zjh.seckill.order.application.message;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.order.application.model.task.SeckillOrderTask;
import cn.zjh.seckill.order.application.service.SeckillSubmitOrderService;
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
 * 订单任务监听类
 * 
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
@RocketMQMessageListener(consumerGroup = SeckillConstants.SUBMIT_ORDER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_ORDER_MSG)
public class OrderTaskConsumerListener implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(OrderTaskConsumerListener.class);
    
    @Resource
    private SeckillSubmitOrderService seckillSubmitOrderService;
    
    @Override
    public void onMessage(String message) {
        logger.info("onMessage|秒杀订单微服务接收异步订单任务消息:{}", message);
        if (StringUtils.isEmpty(message)){
            logger.info("onMessage|秒杀订单微服务接收异步订单任务消息为空:{}", message);
            return;
        }
        SeckillOrderTask seckillOrderTask = getTaskMessage(message);
        if (seckillOrderTask.isEmpty()) {
            logger.info("onMessage|秒杀订单微服务接收异步订单任务消息转换成任务对象为空{}", message);
            return;
        }
        logger.info("onMessage|处理下单任务:{}", seckillOrderTask.getOrderTaskId());
        seckillSubmitOrderService.handlePlaceOrderTask(seckillOrderTask);
    }

    private SeckillOrderTask getTaskMessage(String message){
        JSONObject jsonObject = JSONObject.parseObject(message);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, SeckillOrderTask.class);
    }
}
