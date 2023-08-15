package cn.zjh.seckill.stock.domain.model.entity;

import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;

import java.io.Serializable;

/**
 * 
 * 商品库存分桶
 * 
 * @author zjh - kayson
 */
public class SeckillStockBucket implements Serializable {

    private static final long serialVersionUID = 6965796752002288513L;
    
    // 主键id
    private Long id;
    // 商品id
    private Long goodsId;
    // 初始库存
    private Integer initialStock;
    // 当前可用库存
    private Integer availableStock;
    // 状态，0: 不可用; 1:可用
    private Integer status;
    // 分桶编号
    private Integer serialNo;

    public SeckillStockBucket() {
    }

    public SeckillStockBucket(Long goodsId, Integer initialStock, Integer availableStock, Integer status, Integer serialNo) {
        this.id = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        this.goodsId = goodsId;
        this.initialStock = initialStock;
        this.availableStock = availableStock;
        this.status = status;
        this.serialNo = serialNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(Integer initialStock) {
        this.initialStock = initialStock;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }
}
