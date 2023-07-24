package cn.zjh.seckill.order.application.message;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.model.message.ErrorMessage;
import cn.zjh.seckill.order.application.service.SeckillOrderService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听异常消息
 *
 * @author zjh - kayson
 */
@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_ORDER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_ERROR_MSG)
public class OrderErrorMessageListener implements RocketMQListener<String> {

    public static final Logger logger = LoggerFactory.getLogger(OrderErrorMessageListener.class);
    
    @Resource
    private SeckillOrderService seckillOrderService;

    @Override
    public void onMessage(String message) {
        logger.info("onMessage|秒杀订单微服务开始消费消息:{}", message);
        if (StringUtils.isEmpty(message)) {
            return;
        }
        // 删除数据库中对应的订单
        ErrorMessage errorMessage = getErrorMessage(message);
        seckillOrderService.deleteOrder(errorMessage);
    }

    private ErrorMessage getErrorMessage(String msg) {
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, ErrorMessage.class);
    }

}
