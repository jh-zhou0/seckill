package cn.zjh.seckill.stock.domain.service.service;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.mq.MessageSenderService;
import cn.zjh.seckill.stock.domain.event.SeckillStockBucketEvent;
import cn.zjh.seckill.stock.domain.model.dto.SeckillStockBucketDeduction;
import cn.zjh.seckill.stock.domain.model.entity.SeckillStockBucket;
import cn.zjh.seckill.stock.domain.model.enums.SeckillStockBucketEventType;
import cn.zjh.seckill.stock.domain.repository.SeckillStockBucketRepository;
import cn.zjh.seckill.stock.domain.service.SeckillStockBucketDomainService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品库存领域层实现类
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillStockBucketDomainServiceImpl implements SeckillStockBucketDomainService {
    
    private static final Logger logger = LoggerFactory.getLogger(SeckillStockBucketDomainServiceImpl.class);

    @Resource
    private SeckillStockBucketRepository seckillStockBucketRepository;
    
    @Resource
    private MessageSenderService messageSenderService;

    @Value("${message.mq.type}")
    private String eventType;

    @Override
    public boolean suspendBuckets(Long goodsId) {
        logger.info("suspendBuckets|禁用库存分桶|{}", goodsId);
        checkGoodsId(goodsId);
        boolean success = seckillStockBucketRepository.suspendBuckets(goodsId);
        if (!success){
            return false;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(goodsId, SeckillStockBucketEventType.DISABLED.getCode(), getTopicEvent());
        messageSenderService.send(seckillStockBucketEvent);
        logger.info("suspendBuckets|库存分桶已禁用|{}", goodsId);
        return true;
    }

    @Override
    public boolean resumeBuckets(Long goodsId) {
        logger.info("resumeBuckets|启用库存分桶|{}", goodsId);
        checkGoodsId(goodsId);
        boolean success = seckillStockBucketRepository.resumeBuckets(goodsId);
        if (!success){
            return false;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(goodsId, SeckillStockBucketEventType.ENABLED.getCode(), getTopicEvent());
        messageSenderService.send(seckillStockBucketEvent);
        logger.info("resumeBuckets|库存分桶已启用|{}", goodsId);
        return true;
    }

    @Override
    public List<SeckillStockBucket> getBucketsByGoodsId(Long goodsId) {
        checkGoodsId(goodsId);
        return seckillStockBucketRepository.getBucketsByGoodsId(goodsId);
    }
    
    private void checkGoodsId(Long goodsId) {
        if (goodsId == null || goodsId <= 0){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
    }

    @Override
    public boolean arrangeBuckets(Long goodsId, List<SeckillStockBucket> buckets) {
        logger.info("arrangeBuckets|编排库存分桶|{},{}", goodsId, JSON.toJSONString(buckets));
        if (goodsId == null || goodsId <= 0 || CollectionUtils.isEmpty(buckets)){
            logger.info("arrangeBuckets|库存分桶参数错误|{}", goodsId);
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        // 校验数据
        checkBuckets(goodsId, buckets);
        // 存储分桶数据
        boolean success = seckillStockBucketRepository.submitBuckets(goodsId, buckets);
        if (!success){
            return false;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = new SeckillStockBucketEvent(goodsId, SeckillStockBucketEventType.ARRANGED.getCode(), getTopicEvent());
        messageSenderService.send(seckillStockBucketEvent);
        logger.info("arrangeBuckets|编排库存分桶已完成|{}", goodsId);
        return true;
    }

    private void checkBuckets(Long goodsId, List<SeckillStockBucket> buckets){
        buckets.forEach((bucket) -> {
            if (!goodsId.equals(bucket.getGoodsId())){
                throw new SeckillException(ErrorCode.BUCKET_GOODSID_ERROR);
            }
            if (bucket.getInitialStock() == null || bucket.getInitialStock() < 0){
                throw new SeckillException(ErrorCode.BUCKET_INIT_STOCK_ERROR);
            }
            if (bucket.getAvailableStock() == null || bucket.getAvailableStock() < 0){
                throw new SeckillException(ErrorCode.BUCKET_AVAILABLE_STOCK_ERROR);
            }
            if (bucket.getInitialStock() < bucket.getAvailableStock()){
                throw new SeckillException(ErrorCode.BUCKET_STOCK_ERROR);
            }
        });
    }

    @Override
    public boolean decreaseStock(SeckillStockBucketDeduction stockDeduction) {
        logger.info("decreaseItemStock|扣减库存|{}", JSON.toJSONString(stockDeduction));
        checkStockDeduction(stockDeduction);
        return seckillStockBucketRepository.decreaseStock(stockDeduction.getQuantity(), stockDeduction.getSerialNo(), stockDeduction.getGoodsId());
    }

    @Override
    public boolean increaseStock(SeckillStockBucketDeduction stockDeduction) {
        logger.info("increaseItemStock|恢复库存|{}", JSON.toJSONString(stockDeduction));
        checkStockDeduction(stockDeduction);
        return seckillStockBucketRepository.increaseStock(stockDeduction.getQuantity(), stockDeduction.getSerialNo(), stockDeduction.getGoodsId());
    }
    
    private void checkStockDeduction(SeckillStockBucketDeduction stockDeduction) {
        if (stockDeduction == null || stockDeduction.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ? SeckillConstants.TOPIC_EVENT_ROCKETMQ_STOCK : SeckillConstants.TOPIC_EVENT_COLA;
    }
}
