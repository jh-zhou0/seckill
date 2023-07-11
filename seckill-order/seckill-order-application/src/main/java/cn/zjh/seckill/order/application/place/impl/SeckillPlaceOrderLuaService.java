package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import org.dromara.hmily.annotation.HmilyTCC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于lua脚本防止库存超卖
 *
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lua")
public class SeckillPlaceOrderLuaService extends SeckillPlaceOrderBaseService implements SeckillPlaceOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand, Long txNo) {
        // 幂等处理
        if (distributedCacheService.isMemberSet(orderTryKey, txNo)) {
            logger.warn("placeOrder|基于lua脚本防止超卖的方法已经执行过Try方法,txNo:{}", txNo);
            return txNo;
        }
        // 空回滚和悬挂处理
        if (distributedCacheService.isMemberSet(orderConfirmKey, txNo)
                || distributedCacheService.isMemberSet(orderCancelKey, txNo)) {
            logger.warn("placeOrder|基于lua脚本防止超卖的方法已经执行过Confirm方法或者Cancel方法,txNo:{}", txNo);
            return txNo;
        }
        boolean isDecrementStock = false;
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        // 获取库存key
        String stockKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        // 是否保存try日志
        boolean isSaveTryLog = false;
        try {
            // 扣减缓存中的库存
            Long result = distributedCacheService.decrementByLua(stockKey, seckillOrderCommand.getQuantity());
            // 检查lua脚本执行结果
            checkResult(result);
            isDecrementStock = true;
            // 构建订单，保存
            SeckillOrder seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            // 巧妙的使用事务编号作为订单id，避免过多资源浪费，也可以使用其他方式生成订单id
            seckillOrder.setId(txNo);
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 保存try日志
            distributedCacheService.addSet(orderTryKey, txNo);
            isSaveTryLog = true;
            // 扣减数据库中的库存
            seckillGoodsDubboService.updateAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId(), txNo);
            // 手动抛个异常，测试分布式事务问题
//            int i = 1 / 0;
            return seckillOrder.getId();
        } catch (Exception e) {
            if (isDecrementStock) {
                // 将缓存中的库存增加回去
                distributedCacheService.incrementByLua(stockKey, seckillOrderCommand.getQuantity());
            }
            if (isSaveTryLog) {
                distributedCacheService.removeSet(orderTryKey, txNo);
            }
            if (e instanceof SeckillException) {
                throw e;
            } else {
                throw new SeckillException(ErrorCode.ORDER_FAILED);
            }
        }
    }

    private void checkResult(Long result) {
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new SeckillException(ErrorCode.STOCK_IS_NULL);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_LT_ZERO) {
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }

}