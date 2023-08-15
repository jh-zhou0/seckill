package cn.zjh.seckill.stock.application.model.dto;

import cn.zjh.seckill.stock.domain.model.entity.SeckillStockBucket;

import java.io.Serializable;
import java.util.List;

/**
 * 库存DTO
 * 
 * @author zjh - kayson
 */
public class SeckillStockBucketDTO implements Serializable {

    private static final long serialVersionUID = 6707252274621460974L;
    
    // 库存总量
    private Integer totalStock;
    // 可用库存量
    private Integer availableStock;
    // 分桶数量
    private Integer bucketsQuantity;
    // 库存分桶信息
    private List<SeckillStockBucket> buckets;

    public SeckillStockBucketDTO() {
    }

    public SeckillStockBucketDTO(Integer totalStock, Integer availableStock, List<SeckillStockBucket> buckets) {
        this.totalStock = totalStock;
        this.availableStock = availableStock;
        this.buckets = buckets;
        this.bucketsQuantity = buckets.size();
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getBucketsQuantity() {
        return bucketsQuantity;
    }

    public void setBucketsQuantity(Integer bucketsQuantity) {
        this.bucketsQuantity = bucketsQuantity;
    }

    public List<SeckillStockBucket> getBuckets() {
        return buckets;
    }

    public void setBuckets(List<SeckillStockBucket> buckets) {
        this.buckets = buckets;
    }
}
