package cn.zjh.seckill.stock.domain.repository;

import cn.zjh.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.util.List;

/**
 * 商品库存Repository接口
 * 
 * @author zjh - kayson
 */
public interface SeckillStockBucketRepository {
    
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
     * 批量提交商品库存信息
     */
    boolean submitBuckets(Long goodsId, List<SeckillStockBucket> buckets);

    /**
     * 扣减库存
     */
    boolean decreaseStock(Integer quantity, Integer serialNo, Long goodsId);

    /**
     * 增加库存
     */
    boolean increaseStock(Integer quantity, Integer serialNo, Long goodsId);
    
}
