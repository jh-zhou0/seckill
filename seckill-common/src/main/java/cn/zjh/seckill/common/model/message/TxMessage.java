package cn.zjh.seckill.common.model.message;

import java.math.BigDecimal;

/**
 * 事务消息
 * 
 * @author zjh - kayson
 */
public class TxMessage extends ErrorMessage {
    
    // 商品名称
    private String goodsName;
    // 商品版本号
    private Long version;
    // 活动id
    private Long activityId;
    // 用户id
    private Long userId;
    // 秒杀活动价格
    private BigDecimal activityPrice;

    public TxMessage() {
    }

    public TxMessage(String destination, Long txNo, Long goodsId, String goodsName, Long version, Integer quantity, Long activityId, 
                     Long userId, BigDecimal activityPrice, String placeOrderType, Boolean exception) {
        super(destination, txNo, goodsId, quantity, placeOrderType, exception);
        this.goodsName = goodsName;
        this.version = version;
        this.activityId = activityId;
        this.userId = userId;
        this.activityPrice = activityPrice;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(BigDecimal activityPrice) {
        this.activityPrice = activityPrice;
    }
}
