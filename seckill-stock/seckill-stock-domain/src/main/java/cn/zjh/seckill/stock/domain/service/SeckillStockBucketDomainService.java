package cn.zjh.seckill.stock.domain.service;

import cn.zjh.seckill.stock.domain.model.dto.SeckillStockBucketDeduction;
import cn.zjh.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.util.List;

/**
 * 商品库存领域层接口
 * 
 * @author zjh - kayson
 */
public interface SeckillStockBucketDomainService {

    /**
     * 暂停库存
     */
    boolean suspendBuckets(Long goodsId);

    /**
     * 恢复库存
     */
    boolean resumeBuckets(Long goodsId);

    /**
     * 根据商品id获取库存分桶列表
     */
    List<SeckillStockBucket> getBucketsByGoodsId(Long goodsId);

    /**
     * 编排库存分桶
     */
    boolean arrangeBuckets(Long goodsId, List<SeckillStockBucket> buckets);

    /**
     * 库存扣减
     */
    boolean decreaseStock(SeckillStockBucketDeduction stockDeduction);

    /**
     * 库存恢复
     */
    boolean increaseStock(SeckillStockBucketDeduction stockDeduction);
    
}
