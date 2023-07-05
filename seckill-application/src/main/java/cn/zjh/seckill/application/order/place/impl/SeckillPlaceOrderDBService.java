package cn.zjh.seckill.application.order.place.impl;

import cn.zjh.seckill.application.command.SeckillOrderCommand;
import cn.zjh.seckill.application.order.place.SeckillPlaceOrderService;
import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.dto.SeckillGoodsDTO;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillOrder;
import cn.zjh.seckill.domain.service.SeckillOrderDomainService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 基于数据库防止库存超卖
 * 
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDBService implements SeckillPlaceOrderService {
    
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    
    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 获取商品信息(带缓存)
        SeckillGoodsDTO seckillGoods = seckillGoodsService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        // 扣减库存不成功，则库存不足
        if (!seckillGoodsService.updateDBAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId())) {
            throw new SeckillException(HttpCode.STOCK_LT_ZERO);
        }
        // 构建订单
        SeckillOrder seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
        // 保存订单
        seckillOrderDomainService.saveSeckillOrder(seckillOrder);
        return seckillOrder.getId();
    }
    
}
