package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import org.dromara.hmily.annotation.HmilyTCC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 基于数据库下单，防止库存超卖
 *
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDBService extends SeckillPlaceOrderBaseService implements SeckillPlaceOrderService {

    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderDBService.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    @HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand, Long txNo) {
        // 幂等处理
        if (distributedCacheService.isMemberSet(orderTryKey, txNo)) {
            logger.warn("placeOrder|基于数据库防止超卖的方法已经执行过Try方法,txNo:{}", txNo);
            return txNo;
        }
        // 空回滚和悬挂处理
        if (distributedCacheService.isMemberSet(orderConfirmKey, txNo)
                || distributedCacheService.isMemberSet(orderCancelKey, txNo)) {
            logger.warn("placeOrder|基于数据库防止超卖的方法已经执行过Confirm方法或者Cancel方法,txNo:{}", txNo);
            return txNo;
        }
        boolean isSaveTryLog = false;
        SeckillOrder seckillOrder = null;
        try {
            // 获取商品信息(带缓存)
            SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
            // 检测商品信息
            checkSeckillGoods(seckillOrderCommand, seckillGoods);
            // 扣减库存不成功，则库存不足
            if (!seckillGoodsDubboService.updateAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId(), txNo)) {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            // 构建订单
            seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            // 巧妙的使用事务编号作为订单id，避免过多资源浪费，也可以使用其他方式生成订单id
            seckillOrder.setId(txNo);
            // 保存try日志
            distributedCacheService.addSet(orderTryKey, txNo);
            isSaveTryLog = true;
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 手动抛个异常，测试分布式事务问题
//            int i = 1 / 0;
        } catch (Exception e) {
            if (isSaveTryLog) {
                distributedCacheService.removeSet(orderTryKey, txNo);
            }
            if (e instanceof SeckillException) {
                throw e;
            } else {
                throw new SeckillException(ErrorCode.ORDER_FAILED);
            }
        }
        return seckillOrder.getId();
    }

}