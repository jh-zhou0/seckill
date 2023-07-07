package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 基于数据库下单，防止库存超卖
 * 
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDBService implements SeckillPlaceOrderService {
    
    @Resource
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    
    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 获取商品信息(带缓存)
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        // 扣减库存不成功，则库存不足
        if (!seckillGoodsDubboService.updateDBAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId())) {
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
        // 构建订单
        SeckillOrder seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
        // 保存订单
        seckillOrderDomainService.saveSeckillOrder(seckillOrder);
        return seckillOrder.getId();
    }
    
}