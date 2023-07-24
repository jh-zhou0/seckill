package cn.zjh.seckill.common.model.dto;

/**
 * 订单提交后返回的数据
 * 
 * @author zjh - kayson
 */
public class SeckillOrderSubmitDTO {

    /**
     * 同步下单时，为订单id
     * 异步下单时，为许可id
     */
    private String id;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 类型
     * type_order：id为订单号
     * type_task：id为下单许可号
     */
    private String type;

    public SeckillOrderSubmitDTO() {
    }

    public SeckillOrderSubmitDTO(String id, Long goodsId, String type) {
        this.id = id;
        this.goodsId = goodsId;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
