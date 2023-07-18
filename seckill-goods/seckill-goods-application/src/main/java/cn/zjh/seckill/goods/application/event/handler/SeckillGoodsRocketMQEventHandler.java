package cn.zjh.seckill.goods.application.event.handler;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsCacheService;
import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsListCacheService;
import cn.zjh.seckill.goods.domain.event.SeckillGoodsEvent;
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
 * 接受商品事件 - 基于RocketMQ
 * 
 * @author zjh - kayson
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_GOODS_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS)
public class SeckillGoodsRocketMQEventHandler implements RocketMQListener<String> {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillGoodsRocketMQEventHandler.class);

    @Resource
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Resource
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    
    @Override
    public void onMessage(String message) {
        logger.info("RocketMQ|SeckillGoodsEvent|接受秒杀商品事件|{}", message);
        if (StringUtils.isEmpty(message)) {
            logger.info("RocketMQ|SeckillGoodsEvent|接受秒杀商品事件参数错误");
            return;
        }
        SeckillGoodsEvent seckillGoodsEvent = getEventMessage(message);
        seckillGoodsCacheService.tryUpdateSeckillActivityCacheByLock(seckillGoodsEvent.getId());
        seckillGoodsListCacheService.tryUpdateSeckillActivityCacheByLock(seckillGoodsEvent.getId());
    }

    private SeckillGoodsEvent getEventMessage(String message) {
        JSONObject jsonObject = JSONObject.parseObject(message);
        String eventStr = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillGoodsEvent.class);
    }

}
