package cn.zjh.seckill.stock.domain.model.dto;

import java.io.Serializable;

/**
 * 商品库存扣减与恢复数据
 * 
 * @author zjh - kayson
 */
public class SeckillStockBucketDeduction implements Serializable {

    private static final long serialVersionUID = -6298907463471862983L;
    
    // 商品id
    private Long goodsId;
    // 商品数量
    private Integer quantity;
    // 用户id
    private Long userId;
    // 分桶编号
    private Integer serialNo;

    public SeckillStockBucketDeduction() {
    }

    public SeckillStockBucketDeduction(Long goodsId, Integer quantity, Long userId, Integer serialNo) {
        this.goodsId = goodsId;
        this.quantity = quantity;
        this.userId = userId;
        this.serialNo = serialNo;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }

    public boolean isEmpty(){
        return this.goodsId == null
                || this.quantity == null
                || this.userId == null
                || this.serialNo == null;
    }
}
