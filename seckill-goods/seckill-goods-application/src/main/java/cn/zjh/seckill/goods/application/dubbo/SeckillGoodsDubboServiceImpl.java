package cn.zjh.seckill.goods.application.dubbo;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.goods.application.service.SeckillGoodsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.hmily.annotation.HmilyTCC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 商品Dubbo服务实现类
 *
 * @author zjh - kayson
 */
@Service
@DubboService(version = "1.0.0")
public class SeckillGoodsDubboServiceImpl implements SeckillGoodsDubboService {

    public static final Logger logger = LoggerFactory.getLogger(SeckillGoodsDubboServiceImpl.class);

    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private DistributedCacheService distributedCacheService;

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        return seckillGoodsService.getSeckillGoods(id, version);
    }

    @Override
    @HmilyTCC(confirmMethod = "confirmMethod", cancelMethod = "cancelMethod")
    public boolean updateAvailableStock(Integer count, Long id, Long txNo) {
        String goodsServiceOrderTryKey = SeckillConstants.getKey(SeckillConstants.ORDER_TRY_KEY_PREFIX, SeckillConstants.GOODS_KEY);
        String goodsServiceOrderConfirmKey = SeckillConstants.getKey(SeckillConstants.ORDER_CONFIRM_KEY_PREFIX, SeckillConstants.GOODS_KEY);
        String goodsServiceOrderCancelKey = SeckillConstants.getKey(SeckillConstants.ORDER_CANCEL_KEY_PREFIX, SeckillConstants.GOODS_KEY);
        // 幂等处理
        if (distributedCacheService.isMemberSet(goodsServiceOrderTryKey, txNo)) {
            logger.warn("confirmMethod|提交订单已经执行过Confirm方法,txNo:{}", txNo);
            return false;
        }
        // 空回滚和悬挂处理
        if (distributedCacheService.isMemberSet(goodsServiceOrderConfirmKey, txNo)
                || distributedCacheService.isMemberSet(goodsServiceOrderCancelKey, txNo)) {
            logger.warn("updateAvailableStock|更新库存已经执行过Confirm方法或者Cancel方法|{}", txNo);
            return false;
        }
        boolean result;
        boolean isSaveTryLog = false;
        try {
            distributedCacheService.addSet(goodsServiceOrderTryKey, txNo);
            isSaveTryLog = true;
            result = seckillGoodsService.updateAvailableStock(count, id);
        } catch (Exception e) {
            if (isSaveTryLog) {
                distributedCacheService.removeSet(goodsServiceOrderTryKey, txNo);
            }
            if (e instanceof SeckillException) {
                throw e;
            } else {
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
        }
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean confirmMethod(Integer count, Long id, Long txNo) {
        String goodsServiceOrderConfirmKey = SeckillConstants.getKey(SeckillConstants.ORDER_CONFIRM_KEY_PREFIX, SeckillConstants.GOODS_KEY);
        // 幂等处理
        if (distributedCacheService.isMemberSet(goodsServiceOrderConfirmKey, txNo)) {
            logger.warn("confirmMethod|更新库存已经执行过Confirm方法|{}", txNo);
            return false;
        }
        logger.info("confirmMethod|更新库存执行Confirm方法|{}", txNo);
        boolean isSaveConfirmLog = false;
        try {
            distributedCacheService.addSet(goodsServiceOrderConfirmKey, txNo);
            isSaveConfirmLog = true;
        } catch (Exception e) {
            distributedCacheService.removeSet(goodsServiceOrderConfirmKey, txNo);
        }
        return isSaveConfirmLog;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean cancelMethod(Integer count, Long id, Long txNo) {
        String goodsServiceOrderCancelKey = SeckillConstants.getKey(SeckillConstants.ORDER_CANCEL_KEY_PREFIX, SeckillConstants.GOODS_KEY);
        // 幂等处理
        if (distributedCacheService.isMemberSet(goodsServiceOrderCancelKey, txNo)) {
            logger.warn("confirmMethod|更新库存已经执行过Confirm方法|{}", txNo);
            return false;
        }
        logger.info("cancelMethod|更新库存执行Cancel方法|{}", txNo);
        boolean result = false;
        boolean isSaveCancelLog = false;
        try {
            distributedCacheService.addSet(goodsServiceOrderCancelKey, txNo);
            isSaveCancelLog = true;
            result = seckillGoodsService.incrementAvailableStock(count, id);
        } catch (Exception e) {
            if (isSaveCancelLog) {
                distributedCacheService.removeSet(goodsServiceOrderCancelKey, txNo);
            }
        }
        return result;
    }

}
