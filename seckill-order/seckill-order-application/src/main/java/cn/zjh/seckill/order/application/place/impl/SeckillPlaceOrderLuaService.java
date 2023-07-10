package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
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

import javax.annotation.Resource;

/**
 * 基于lua脚本防止库存超卖
 * 
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lua")
public class SeckillPlaceOrderLuaService implements SeckillPlaceOrderService {
    
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    private DistributedCacheService distributedCacheService;
    
    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        boolean isDecrementStock = false;
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        // 获取库存key
        String stockKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        try {
            // 扣减缓存中的库存
            Long result = distributedCacheService.decrementByLua(stockKey, seckillOrderCommand.getQuantity());
            // 检查lua脚本执行结果
            checkResult(result);
            isDecrementStock = true;
            // 构建订单，保存
            SeckillOrder seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 扣减数据库中的库存
            seckillGoodsDubboService.updateAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId());
            // 手动抛个异常，测试分布式事务问题
//            int i = 1 / 0;
            return seckillOrder.getId();
        } catch (Exception e) {
            if (isDecrementStock) {
                // 将缓存中的库存增加回去
                distributedCacheService.incrementByLua(stockKey, seckillOrderCommand.getQuantity());
            }
            throw e;
        }
    }

    private void checkResult(Long result) {
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new SeckillException(ErrorCode.STOCK_IS_NULL);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_LT_ZERO){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }
    
}