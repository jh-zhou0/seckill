package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 基础的下单服务，主要包含Confirm与Cancel方法
 *
 * @author zjh - kayson
 */
@Service
public class SeckillPlaceOrderBaseService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderBaseService.class);

    public static final String orderTryKey = SeckillConstants.getKey(SeckillConstants.ORDER_TRY_KEY_PREFIX, SeckillConstants.ORDER_KEY);
    public static final String orderConfirmKey = SeckillConstants.getKey(SeckillConstants.ORDER_CONFIRM_KEY_PREFIX, SeckillConstants.ORDER_KEY);
    public static final String orderCancelKey = SeckillConstants.getKey(SeckillConstants.ORDER_CANCEL_KEY_PREFIX, SeckillConstants.ORDER_KEY);

    @DubboReference(version = "1.0.0", check = false)
    protected SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    protected SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    protected DistributedCacheService distributedCacheService;

    @Transactional(rollbackFor = Exception.class)
    public Long confirmMethod(Long userId, SeckillOrderCommand seckillOrderCommand, Long txNo) {
        // 幂等处理
        if (distributedCacheService.isMemberSet(orderConfirmKey, txNo)) {
            logger.warn("confirmMethod|提交订单已经执行过Confirm方法,txNo:{}", txNo);
            return txNo;
        }
        logger.info("confirmMethod|提交订单执行Confirm方法,txNo:{}", txNo);
        try {
            // 保存Confirm日志
            distributedCacheService.addSet(orderConfirmKey, txNo);
        } catch (Exception e) {
            distributedCacheService.removeSet(orderConfirmKey, txNo);
        }
        return txNo;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long cancelMethod(Long userId, SeckillOrderCommand seckillOrderCommand, Long txNo) {
        // 幂等处理
        if (distributedCacheService.isMemberSet(orderCancelKey, txNo)) {
            logger.warn("confirmMethod|提交订单已经执行过Cancel方法,txNo:{}", txNo);
            return txNo;
        }
        logger.info("confirmMethod|提交订单执行Cancel方法,txNo:{}", txNo);
        boolean isSaveCancelLog = false;
        try {
            // 保存Confirm日志
            distributedCacheService.addSet(orderCancelKey, txNo);
            isSaveCancelLog = true;
            // 删除提交的订单数据
            seckillOrderDomainService.deleteSeckillOrder(txNo);
        } catch (Exception e) {
            if (isSaveCancelLog) {
                distributedCacheService.removeSet(orderCancelKey, txNo);
            }
        }
        return txNo;
    }

}
