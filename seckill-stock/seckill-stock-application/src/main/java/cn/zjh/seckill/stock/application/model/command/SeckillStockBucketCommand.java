package cn.zjh.seckill.stock.application.model.command;

import java.io.Serializable;

/**
 * 库存分桶Command
 * 
 * @author zjh - kayson
 */
public class SeckillStockBucketCommand implements Serializable {

    private static final long serialVersionUID = 5541107936872329915L;
    
    // 总库存
    private Integer totalStock;
    // 分桶数量
    private Integer bucketsQuantity;
    // 分桶编排模式, 1:总量模式  2：增量模式
    private Integer arrangementMode;

    public SeckillStockBucketCommand() {
    }

    public SeckillStockBucketCommand(Integer totalStock, Integer bucketsQuantity, Integer arrangementMode) {
        this.totalStock = totalStock;
        this.bucketsQuantity = bucketsQuantity;
        this.arrangementMode = arrangementMode;
    }

    public Integer getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Integer totalStock) {
        this.totalStock = totalStock;
    }

    public Integer getBucketsQuantity() {
        return bucketsQuantity;
    }

    public void setBucketsQuantity(Integer bucketsQuantity) {
        this.bucketsQuantity = bucketsQuantity;
    }

    public Integer getArrangementMode() {
        return arrangementMode;
    }

    public void setArrangementMode(Integer arrangementMode) {
        this.arrangementMode = arrangementMode;
    }

    public boolean isEmpty(){
        return this.totalStock == null
                || this.bucketsQuantity == null
                || this.arrangementMode == null;
    }
}
