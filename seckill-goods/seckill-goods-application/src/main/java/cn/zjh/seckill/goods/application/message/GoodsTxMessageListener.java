package cn.zjh.seckill.goods.application.message;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.model.message.TxMessage;
import cn.zjh.seckill.goods.application.service.SeckillGoodsService;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 商品微服务事务消息
 * 
 * @author zjh - kayson
 */
@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_GOODS_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_TX_MSG)
public class GoodsTxMessageListener implements RocketMQListener<String> {
    
    public static final Logger logger = LoggerFactory.getLogger(GoodsTxMessageListener.class);
    
    @Resource
    private SeckillGoodsService seckillGoodsService;
    
    @Override
    public void onMessage(String message) {
        if (StringUtils.isEmpty(message)) {
            return;
        }
        logger.info("秒杀商品微服务开始消费事务消息:{}", message);
        TxMessage txMessage = getTxMessage(message);
        // 如果协调的异常信息字段为false，订单微服务没有抛出异常，则处理库存信息
        if (Boolean.FALSE.equals(txMessage.getException())) {
            seckillGoodsService.updateAvailableStock(txMessage);
        }
    }

    private TxMessage getTxMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.TX_MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }
    
}
