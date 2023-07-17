package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.lock.DistributedLock;
import cn.zjh.seckill.common.lock.factory.DistributedLockFactory;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 基于分布式锁下单，防止库存超卖
 *
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lock")
public class SeckillPlaceOrderLockService implements SeckillPlaceOrderService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLockService.class);

    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    private DistributedLockFactory distributedLockFactory;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 获取商品信息(带缓存)
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        // 获取分布式锁
        String lockKey = SeckillConstants.getKey(SeckillConstants.ORDER_LOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        // 获取内存中的库存信息
        String stockKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        // 是否扣减了缓存中的库存
        boolean isDecrementCacheStock = false;
        try {
            boolean isSuccessLock = lock.tryLock(2, 5, TimeUnit.SECONDS);
            // 未获取到分布式锁，稍后重试
            if (!isSuccessLock) {
                throw new SeckillException(ErrorCode.RETRY_LATER);
            }
            // 查询库存信息
            Integer stock = distributedCacheService.getObject(stockKey, Integer.class);
            // 库存不足
            if (stock < seckillOrderCommand.getQuantity()) {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            // 扣减库存
            Long result = distributedCacheService.decrement(stockKey, seckillOrderCommand.getQuantity());
            if (result < 0) {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            // 正常执行了扣减缓存中库存的操作
            isDecrementCacheStock = true;
            // 构建订单
            SeckillOrder seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 扣减数据库库存
            seckillGoodsDubboService.updateAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId());
            // 手动抛个异常，测试分布式事务问题
//            int i = 1 / 0;
            // 返回订单id
            return seckillOrder.getId();
        } catch (Exception e) {
            // 已经扣减了缓存中的库存，则需要增加回来
            if (isDecrementCacheStock) {
                distributedCacheService.increment(stockKey, seckillOrderCommand.getQuantity());
            }
            if (e instanceof InterruptedException) {
                logger.error("SeckillPlaceOrderLockService|下单分布式锁被中断|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
                throw new SeckillException(ErrorCode.ORDER_FAILED);
            } else if (e instanceof SeckillException) {
                SeckillException se = (SeckillException) e;
                throw new SeckillException(se.getCode(), se.getMessage());
            } else {
                logger.error("SeckillPlaceOrderLockService|分布式锁下单失败|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
                throw new SeckillException(ErrorCode.ORDER_FAILED);
            }
        } finally {
            lock.unlock();
        }
    }

}