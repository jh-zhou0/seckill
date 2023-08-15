package cn.zjh.seckill.stock.domain.model.enums;

/**
 * 库存分桶事件类型
 * 
 * @author zjh - kayson
 */
public enum SeckillStockBucketEventType {

    DISABLED(0),
    ENABLED(1),
    ARRANGED(2);

    private final Integer code;
    
    SeckillStockBucketEventType(Integer code) {
        this.code = code;
    }
    
    public Integer getCode() {
        return code;
    }

}
