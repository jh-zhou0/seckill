package cn.zjh.seckill.common.model.message;

import java.math.BigDecimal;

/**
 * 事务消息
 * 
 * @author zjh - kayson
 */
public class TxMessage {

    // 全局事务编号
    private Long txNo;
    // 商品id
    private Long goodsId;
    // 商品名称
    private String goodsName;
    // 商品版本号
    private Long version;
    // 购买数量
    private Integer quantity;
    // 活动id
    private Long activityId;
    // 用户id
    private Long userId;
    // 秒杀活动价格
    private BigDecimal activityPrice;
    // 下单的类型
    private String placeOrderType;
    // 是否抛出了异常
    private Boolean exception;

    public TxMessage() {
    }

    public TxMessage(Long txNo, Long goodsId, String goodsName, Long version, Integer quantity, Long activityId, 
                     Long userId, BigDecimal activityPrice, String placeOrderType, Boolean exception) {
        this.txNo = txNo;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.version = version;
        this.quantity = quantity;
        this.activityId = activityId;
        this.userId = userId;
        this.activityPrice = activityPrice;
        this.placeOrderType = placeOrderType;
        this.exception = exception;
    }

    public Long getTxNo() {
        return txNo;
    }

    public void setTxNo(Long txNo) {
        this.txNo = txNo;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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

    public String getPlaceOrderType() {
        return placeOrderType;
    }

    public void setPlaceOrderType(String placeOrderType) {
        this.placeOrderType = placeOrderType;
    }

    public Boolean getException() {
        return exception;
    }

    public void setException(Boolean exception) {
        this.exception = exception;
    }
}
