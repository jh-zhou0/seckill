package cn.zjh.seckill.order.application.model.command;

/**
 * 订单任务参数
 * 
 * @author zjh - kayson
 */
public class SeckillOrderTaskCommand {

    /**
     * 订单任务id
     */
    private String orderTaskId;
    /**
     * 商品id
     */
    private Long goodsId;

    public String getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(String orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
