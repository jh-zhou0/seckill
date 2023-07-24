package cn.zjh.seckill.order.application.place;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.common.model.enums.SeckillGoodsStatus;
import cn.zjh.seckill.common.model.enums.SeckillOrderStatus;
import cn.zjh.seckill.common.model.message.TxMessage;
import cn.zjh.seckill.common.utils.beans.BeanUtil;
import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;
import cn.zjh.seckill.order.application.model.command.SeckillOrderCommand;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;

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
     * 本地事务执行保存订单操作
     */
    void saveOrderInTransaction(TxMessage txMessage);

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(TxMessage txMessage) {
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(txMessage.getTxNo());
        seckillOrder.setUserId(txMessage.getUserId());
        seckillOrder.setGoodsId(txMessage.getGoodsId());
        seckillOrder.setGoodsName(txMessage.getGoodsName());
        seckillOrder.setActivityId(txMessage.getActivityId());
        seckillOrder.setActivityPrice(txMessage.getActivityPrice());
        seckillOrder.setQuantity(txMessage.getQuantity());
        BigDecimal orderPrice = txMessage.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        return seckillOrder;
    }

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods) {
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
    default void checkSeckillGoods(SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods) {
        // 商品不存在
        if (seckillGoods == null) {
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 商品未上线
        if (Objects.equals(seckillGoods.getStatus(), SeckillGoodsStatus.PUBLISHED.getCode())) {
            throw new SeckillException(ErrorCode.GOODS_PUBLISH);
        }
        // 商品已下架
        if (Objects.equals(seckillGoods.getStatus(), SeckillGoodsStatus.OFFLINE.getCode())) {
            throw new SeckillException(ErrorCode.GOODS_OFFLINE);
        }
        // 触发限购
        if (seckillGoods.getLimitNum() < seckillOrderCommand.getQuantity()) {
            throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
        }
        // 库存不足
        if (seckillGoods.getAvailableStock() == null
                || seckillGoods.getAvailableStock() <= 0
                || seckillOrderCommand.getQuantity() > seckillGoods.getAvailableStock()) {
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }

    /**
     * 构建事务消息
     */
    default TxMessage getTxMessage(String destination, Long txNo, Long userId, String placeOrderType, Boolean exception, SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods) {
        return new TxMessage(destination, txNo, seckillOrderCommand.getGoodsId(), seckillGoods.getGoodsName(), 
                seckillOrderCommand.getVersion(), seckillOrderCommand.getQuantity(), seckillOrderCommand.getActivityId(), 
                userId, seckillGoods.getActivityPrice(), placeOrderType, exception);
    }

}