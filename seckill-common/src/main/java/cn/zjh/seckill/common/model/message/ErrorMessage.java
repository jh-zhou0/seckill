package cn.zjh.seckill.common.model.message;

/**
 * 错误消息，扣减库存失败，由商品微服务发送给订单微服务
 * 
 * @author zjh - kayson
 */
public class ErrorMessage {

    // 全局事务编号
    private Long txNo;
    // 商品id
    private Long goodsId;
    // 购买数量
    private Integer quantity;
    // 下单的类型
    private String placeOrderType;
    // 是否扣减了缓存库存
    private Boolean exception;

    public ErrorMessage() {
    }

    public ErrorMessage(Long txNo, Long goodsId, Integer quantity, String placeOrderType, Boolean exception) {
        this.txNo = txNo;
        this.goodsId = goodsId;
        this.quantity = quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
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
