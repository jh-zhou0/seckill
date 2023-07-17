package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 基于数据库下单，防止库存超卖
 *
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDBService implements SeckillPlaceOrderService {
    
    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillOrder seckillOrder = null;
        try {
            // 获取商品信息(带缓存)
            SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
            // 检测商品信息
            checkSeckillGoods(seckillOrderCommand, seckillGoods);
            // 扣减库存不成功，则库存不足
            if (!seckillGoodsDubboService.updateAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId())) {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            // 构建订单
            seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 手动抛个异常，测试分布式事务问题
//            int i = 1 / 0;
        } catch (Exception e) {
            if (e instanceof SeckillException) {
                throw e;
            } else {
                throw new SeckillException(ErrorCode.ORDER_FAILED);
            }
        }
        return seckillOrder.getId();
    }

}