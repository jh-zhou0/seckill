package cn.zjh.seckill.common.model.enums;

/**
 * 分桶库存状态
 * 
 * @author zjh - kayson
 */
public enum SeckillStockBucketStatus {

    ENABLED(1),
    DISABLED(0);

    private final Integer code;

    SeckillStockBucketStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
    
}
