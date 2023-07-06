package cn.zjh.seckill.application.order.place.impl;

import cn.zjh.seckill.application.command.SeckillOrderCommand;
import cn.zjh.seckill.application.order.place.SeckillPlaceOrderService;
import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.domain.code.ErrorCode;
import cn.zjh.seckill.domain.constants.SeckillConstants;
import cn.zjh.seckill.domain.dto.SeckillGoodsDTO;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillOrder;
import cn.zjh.seckill.domain.service.SeckillOrderDomainService;
import cn.zjh.seckill.infrastructure.cache.distribute.DistributedCacheService;
import cn.zjh.seckill.infrastructure.lock.DistributedLock;
import cn.zjh.seckill.infrastructure.lock.factory.DistributedLockFactory;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 基于分布式锁防止库存超卖
 * 
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lock")
public class SeckillPlaceOrderLockService implements SeckillPlaceOrderService {
    
    public static final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLockService.class);
    
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private DistributedLockFactory distributedLockFactory;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 获取商品信息(带缓存)
        SeckillGoodsDTO seckillGoods = seckillGoodsService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        checkSeckillGoods(seckillOrderCommand, seckillGoods);
        // 获取分布式锁对象
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
            distributedCacheService.decrement(stockKey, seckillOrderCommand.getQuantity());
            // 正常执行了扣减缓存中库存的操作
            isDecrementCacheStock = true;
            // 构建订单
            SeckillOrder seckillOrder = buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            // 保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            // 扣减数据库库存
            seckillGoodsService.updateDBAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId());
            // 返回订单id
            return seckillOrder.getId();
        } catch (Exception e) {
            // 已经扣减了缓存中的库存，则需要增加回来
            if (isDecrementCacheStock) {
                distributedCacheService.increment(stockKey, seckillOrderCommand.getQuantity());
            }
            if (e instanceof InterruptedException) {
                logger.error("SeckillPlaceOrderLockService|下单分布式锁被中断|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            } else {
                logger.error("SeckillPlaceOrderLockService|分布式锁下单失败|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            }
            throw new SeckillException(e.getMessage());
        } finally {
            lock.unlock();
        }
    }
    
}
