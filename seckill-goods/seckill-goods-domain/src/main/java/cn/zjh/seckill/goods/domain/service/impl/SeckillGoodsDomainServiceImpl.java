package cn.zjh.seckill.goods.domain.service.impl;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.enums.SeckillGoodsStatus;
import cn.zjh.seckill.goods.domain.event.SeckillGoodsEvent;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;
import cn.zjh.seckill.goods.domain.repository.SeckillGoodsRepository;
import cn.zjh.seckill.goods.domain.service.SeckillGoodsDomainService;
import cn.zjh.seckill.mq.MessageSenderService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品领域层的服务实现类
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillGoodsDomainServiceImpl implements SeckillGoodsDomainService {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillGoodsDomainServiceImpl.class);
    
    @Resource
    private SeckillGoodsRepository seckillGoodsRepository;
    @Resource
    private MessageSenderService messageSenderService;
    @Value("${message.mq.type}")
    private String eventType;
    
    @Override
    public void saveSeckillGoods(SeckillGoods seckillGoods) {
        logger.info("SeckillGoodsPublish|发布秒杀商品|{}", JSON.toJSON(seckillGoods));
        if (seckillGoods == null || !seckillGoods.validateParams()) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        seckillGoodsRepository.saveSeckillGoods(seckillGoods);
        logger.info("SeckillGoodsPublish|秒杀商品已发布|{}", seckillGoods.getId());

        SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(seckillGoods.getId(), seckillGoods.getActivityId(), seckillGoods.getStatus(), getTopicEvent());
        messageSenderService.send(seckillGoodsEvent);
        logger.info("SeckillGoodsPublish|秒杀商品事件已发布|{}", seckillGoods.getId());
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsRepository.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsRepository.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        logger.info("SeckillGoodsPublish|更新秒杀商品状态|{},{}", status, id);
        if (status == null || id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsById(id);
        if (seckillGoods == null) {
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 更新状态
        seckillGoodsRepository.updateStatus(status, id);
        logger.info("SeckillGoodsPublish|发布秒杀商品状态事件|{},{}", status, id);
        
        SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(id, seckillGoods.getActivityId(), status, getTopicEvent());
        messageSenderService.send(seckillGoodsEvent);
        logger.info("SeckillGoodsPublish|秒杀商品事件已发布|{}", id);
    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        logger.info("SeckillGoodsPublish|更新秒杀商品库存|{}", id);
        if (count == null || count <= 0 || id == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsById(id);
        if (seckillGoods == null) {
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 更新库存
        boolean isUpdate = seckillGoodsRepository.updateAvailableStock(count, id) > 0;
        if (isUpdate) {
            logger.info("SeckillGoodsPublish|秒杀商品库存已更新|{}", id);
            SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(id, seckillGoods.getActivityId(), seckillGoods.getStatus(), getTopicEvent());
            messageSenderService.send(seckillGoodsEvent);
            logger.info("SeckillGoodsPublish|秒杀商品库存事件已发布|{}", id);
        } else {
            logger.info("SeckillGoodsPublish|秒杀商品库存未更新|{}", id);
        }
        return isUpdate;
    }

    @Override
    public boolean incrementAvailableStock(Integer count, Long id) {
        logger.info("SeckillGoodsPublish|更新秒杀商品库存|{}", id);
        if (count == null || count <= 0 || id == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillGoods seckillGoods = seckillGoodsRepository.getSeckillGoodsById(id);
        if (seckillGoods == null) {
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 更新库存
        boolean isUpdate = seckillGoodsRepository.incrementAvailableStock(count, id) > 0;
        if (isUpdate) {
            logger.info("SeckillGoodsPublish|秒杀商品库存已更新|{}", id);
            SeckillGoodsEvent seckillGoodsEvent = new SeckillGoodsEvent(id, seckillGoods.getActivityId(), seckillGoods.getStatus(), getTopicEvent());
            messageSenderService.send(seckillGoodsEvent);
            logger.info("SeckillGoodsPublish|秒杀商品库存事件已发布|{}", id);
        } else {
            logger.info("SeckillGoodsPublish|秒杀商品库存未更新|{}", id);
        }
        return isUpdate;
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsRepository.getAvailableStockById(id);
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS : SeckillConstants.TOPIC_EVENT_COLA;
    }
    
}