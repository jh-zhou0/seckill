package cn.zjh.seckill.application.order.place;

import cn.zjh.seckill.application.command.SeckillOrderCommand;
import cn.zjh.seckill.domain.code.ErrorCode;
import cn.zjh.seckill.domain.dto.SeckillGoodsDTO;
import cn.zjh.seckill.domain.enums.SeckillGoodsStatus;
import cn.zjh.seckill.domain.enums.SeckillOrderStatus;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillOrder;
import cn.zjh.seckill.infrastructure.utils.beans.BeanUtil;
import cn.zjh.seckill.infrastructure.utils.id.SnowFlakeFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * 下单接口
 * 
 * @author zjh - kayson
 */
public interface SeckillPlaceOrderService {

    /**
     * 下单操作
     */
    Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillOrder.setGoodsName(seckillGoods.getGoodsName());
        seckillOrder.setUserId(userId);
        seckillOrder.setActivityPrice(seckillGoods.getActivityPrice());
        BigDecimal orderPrice = seckillGoods.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        return seckillOrder;
    }

    /**
     * 检测商品信息
     */
    default void checkSeckillGoods(SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        // 商品不存在
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 商品未上线
        if (Objects.equals(seckillGoods.getStatus(), SeckillGoodsStatus.PUBLISHED.getCode())){
            throw new SeckillException(ErrorCode.GOODS_PUBLISH);
        }
        // 商品已下架
        if (Objects.equals(seckillGoods.getStatus(), SeckillGoodsStatus.OFFLINE.getCode())){
            throw new SeckillException(ErrorCode.GOODS_OFFLINE);
        }
        // 触发限购
        if (seckillGoods.getLimitNum() < seckillOrderCommand.getQuantity()){
            throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
        }
        // 库存不足
        if (seckillGoods.getAvailableStock() == null
                || seckillGoods.getAvailableStock() <= 0
                || seckillOrderCommand.getQuantity() > seckillGoods.getAvailableStock()){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }
    
}
