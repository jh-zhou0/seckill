package cn.zjh.seckill.stock.application.event.handler;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.stock.application.cache.SeckillStockBucketCacheService;
import cn.zjh.seckill.stock.domain.event.SeckillStockBucketEvent;
import cn.zjh.seckill.stock.domain.model.enums.SeckillStockBucketEventType;
import com.alibaba.fastjson.JSON;
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
 * 基于RocketMQ的库存事件处理器
 * 
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_STOCK_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_STOCK)
public class SeckillStockRocketMQEventHandler implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(SeckillStockRocketMQEventHandler.class);
    
    @Resource
    private SeckillStockBucketCacheService seckillStockBucketCacheService;
    
    @Override
    public void onMessage(String message) {
        if (StringUtils.isEmpty(message)) {
            logger.info("rocketmq|stockEvent|接收库存事件为空");
            return;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = getEventMessage(message);
        if (seckillStockBucketEvent.getId() == null){
            logger.info("rocketmq|stockEvent|库存事件参数错误");
        }
        logger.info("rocketmq|stockEvent|接收库存事件|{}", JSON.toJSON(seckillStockBucketEvent));
        // 开启了库存分桶，就更新缓存数据
        if (SeckillStockBucketEventType.ENABLED.getCode().equals(seckillStockBucketEvent.getStatus())){
            seckillStockBucketCacheService.tryUpdateSeckillStockBucketCacheByLock(seckillStockBucketEvent.getId(), false);
        }
    }

    private SeckillStockBucketEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillStockBucketEvent.class);
    }
    
}
