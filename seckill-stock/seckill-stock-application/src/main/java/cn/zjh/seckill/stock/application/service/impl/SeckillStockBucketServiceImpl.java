package cn.zjh.seckill.stock.application.service.impl;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.lock.DistributedLock;
import cn.zjh.seckill.common.lock.factory.DistributedLockFactory;
import cn.zjh.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import cn.zjh.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import cn.zjh.seckill.stock.application.service.SeckillStockBucketArrangementService;
import cn.zjh.seckill.stock.application.service.SeckillStockBucketService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品库存 Service 实现类
 *
 * @author zjh - kayson
 */
@Service
public class SeckillStockBucketServiceImpl implements SeckillStockBucketService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillStockBucketServiceImpl.class);

    @Resource
    private DistributedLockFactory distributedLockFactory;
    @Resource
    private SeckillStockBucketArrangementService seckillStockBucketArrangementService;

    @Override
    public void arrangeStockBuckets(Long userId, SeckillStockBucketWrapperCommand stockBucketWrapperCommand) {
        if (userId == null || stockBucketWrapperCommand == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        stockBucketWrapperCommand.setUserId(userId);
        if (stockBucketWrapperCommand.isEmpty()) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        logger.info("arrangeStockBuckets|编排库存分桶|{}", JSON.toJSON(stockBucketWrapperCommand));
        String lockKey = SeckillConstants.getKey(SeckillConstants.getKey(SeckillConstants.GOODS_BUCKET_ARRANGEMENT_KEY, String.valueOf(stockBucketWrapperCommand.getUserId())), String.valueOf(stockBucketWrapperCommand.getGoodsId()));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        try {
            boolean isLock = lock.tryLock();
            if (!isLock) {
                throw new SeckillException(ErrorCode.FREQUENTLY_ERROR);
            }
            // 获取到锁，编排库存
            seckillStockBucketArrangementService.arrangeStockBuckets(stockBucketWrapperCommand.getGoodsId(),
                    stockBucketWrapperCommand.getStockBucketCommand().getTotalStock(),
                    stockBucketWrapperCommand.getStockBucketCommand().getBucketsQuantity(),
                    stockBucketWrapperCommand.getStockBucketCommand().getArrangementMode());
            logger.info("arrangeStockBuckets|库存编排完成|{}", stockBucketWrapperCommand.getGoodsId());
        } catch (SeckillException e) {
            logger.error("arrangeStockBuckets|库存编排失败|{}", stockBucketWrapperCommand.getGoodsId(), e);
            throw e;
        } catch (Exception e) {
            logger.error("arrangeStockBuckets|库存编排错误|{}", stockBucketWrapperCommand.getGoodsId(), e);
            throw new SeckillException(ErrorCode.BUCKET_CREATE_FAILED);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillStockBucketDTO getTotalStockBuckets(Long goodsId, Long version) {
        if (goodsId == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        logger.info("getTotalStockBuckets|获取库存分桶数据|{}", goodsId);
        return seckillStockBucketArrangementService.getSeckillStockBucketDTO(goodsId, version);
    }
}
