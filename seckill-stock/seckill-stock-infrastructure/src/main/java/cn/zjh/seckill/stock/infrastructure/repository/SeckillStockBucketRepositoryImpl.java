package cn.zjh.seckill.stock.infrastructure.repository;

import cn.zjh.seckill.common.model.enums.SeckillStockBucketStatus;
import cn.zjh.seckill.stock.domain.model.entity.SeckillStockBucket;
import cn.zjh.seckill.stock.domain.repository.SeckillStockBucketRepository;
import cn.zjh.seckill.stock.infrastructure.mapper.SeckillStockBucketMapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 分桶库存Repository实现类
 * 
 * @author zjh - kayson
 */
@Repository
public class SeckillStockBucketRepositoryImpl implements SeckillStockBucketRepository {
    
    @Resource
    private SeckillStockBucketMapper seckillStockBucketMapper;
    
    @Override
    public boolean suspendBuckets(Long goodsId) {
        if (goodsId == null){
            return false;
        }
        seckillStockBucketMapper.updateStatusByGoodsId(SeckillStockBucketStatus.DISABLED.getCode(), goodsId);
        return true;
    }

    @Override
    public boolean resumeBuckets(Long goodsId) {
        if (goodsId == null){
            return false;
        }
        seckillStockBucketMapper.updateStatusByGoodsId(SeckillStockBucketStatus.ENABLED.getCode(), goodsId);
        return true;
    }

    @Override
    public List<SeckillStockBucket> getBucketsByGoodsId(Long goodsId) {
        if (goodsId == null){
            return Collections.emptyList();
        }
        return seckillStockBucketMapper.getBucketsByGoodsId(goodsId);
    }

    @Override
    public boolean submitBuckets(Long goodsId, List<SeckillStockBucket> buckets) {
        if (goodsId == null || CollectionUtils.isEmpty(buckets)){
            return false;
        }
        seckillStockBucketMapper.deleteByGoodsId(goodsId);
        seckillStockBucketMapper.insertBatch(buckets);
        return true;
    }

    @Override
    public boolean decreaseStock(Integer quantity, Integer serialNo, Long goodsId) {
        if (hasNull(quantity, serialNo, goodsId)){
            return false;
        }
        seckillStockBucketMapper.decreaseStock(quantity, serialNo, goodsId);
        return true;
    }

    @Override
    public boolean increaseStock(Integer quantity, Integer serialNo, Long goodsId) {
        if (hasNull(quantity, serialNo, goodsId)){
            return false;
        }
        seckillStockBucketMapper.increaseStock(quantity, serialNo, goodsId);
        return true;
    }
    
    private boolean hasNull(Integer quantity, Integer serialNo, Long goodsId) {
        return quantity == null || serialNo == null || goodsId == null;
    }
}
