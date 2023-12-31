package cn.zjh.seckill.goods.application.service.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.cache.local.LocalCacheService;
import cn.zjh.seckill.common.cache.model.SeckillBusinessCache;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillActivityDTO;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.common.model.enums.SeckillGoodsStatus;
import cn.zjh.seckill.common.model.message.ErrorMessage;
import cn.zjh.seckill.common.model.message.TxMessage;
import cn.zjh.seckill.common.utils.id.SnowFlakeFactory;
import cn.zjh.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import cn.zjh.seckill.goods.application.builder.SeckillGoodsBuilder;
import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsCacheService;
import cn.zjh.seckill.goods.application.cache.service.SeckillGoodsListCacheService;
import cn.zjh.seckill.goods.application.command.SeckillGoodsCommand;
import cn.zjh.seckill.goods.application.service.SeckillGoodsService;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;
import cn.zjh.seckill.goods.domain.service.SeckillGoodsDomainService;
import cn.zjh.seckill.mq.MessageSenderService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillGoodsServiceImpl.class);
    
    @Resource
    private SeckillGoodsDomainService seckillGoodsDomainService;
    @DubboReference(version = "1.0.0", check = false)
    private SeckillActivityDubboService seckillActivityDubboService;
    @Resource
    private SeckillGoodsListCacheService seckillGoodsListCacheService;
    @Resource
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private LocalCacheService<String, SeckillGoods> localCacheService;
    @Resource
    private MessageSenderService messageSenderService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand) {
        if (seckillGoodsCommand == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivityDTO seckillActivity = seckillActivityDubboService.getSeckillActivity(seckillGoodsCommand.getActivityId(), seckillGoodsCommand.getVersion());
        if (seckillActivity == null) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillGoods seckillGoods = SeckillGoodsBuilder.toSeckillGoods(seckillGoodsCommand);
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        seckillGoods.setAvailableStock(seckillGoodsCommand.getInitialStock());
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        // 保存商品
        boolean success = seckillGoodsDomainService.saveSeckillGoods(seckillGoods);
        if (success) {
            // 将商品的库存同步到redis
            String stockKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillGoods.getId()));
            distributedCacheService.put(stockKey, seckillGoods.getAvailableStock());
            // 将商品限购同步到Redis
            String limitKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, String.valueOf(seckillGoods.getId()));
            distributedCacheService.put(limitKey, seckillGoods.getLimitNum());
        }
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsDomainService.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsDomainService.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer status, Long id) {
        if (Objects.equals(status, SeckillGoodsStatus.OFFLINE.getCode())) {
            // 清空缓存
            clearCache(String.valueOf(id));
        }
        seckillGoodsDomainService.updateStatus(status, id);
    }

    /**
     * 清空缓存的商品数据
     */
    private void clearCache(String id) {
        // 清除本地缓存中的商品
        String goodsKey = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_KEY_PREFIX, id);
        localCacheService.delete(goodsKey);
        // 清除redis缓存缓存中的商品库存
        distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, id));
        // 清除redis缓存中商品信息
        distributedCacheService.delete(goodsKey);
        // 清除redis缓存商品的限购信息
        distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, id));
    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        return seckillGoodsDomainService.updateAvailableStock(count, id);
    }

    @Override
    public boolean incrementAvailableStock(Integer count, Long id) {
        return seckillGoodsDomainService.incrementAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsDomainService.getAvailableStockById(id);
    }

    @Override
    public List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version) {
        if (activityId == null) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache = seckillGoodsListCacheService.getCachedGoodsList(activityId, version);
        if (!seckillGoodsListCache.isExist()) {
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillGoodsListCache.isRetryLater()) {
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        return seckillGoodsListCache.getData().stream().map(seckillGoods -> {
            SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoods);
            seckillGoodsDTO.setVersion(seckillGoodsListCache.getVersion());
            return seckillGoodsDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        if (id == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = seckillGoodsCacheService.getCachedGoods(id, version);
        if (!seckillGoodsCache.isExist()) {
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        // 稍后重试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后重试
        if (seckillGoodsCache.isRetryLater()) {
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoodsCache.getData());
        seckillGoodsDTO.setVersion(seckillGoodsCache.getVersion());
        return seckillGoodsDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvailableStock(TxMessage txMessage) {
        String goodsTxKey = SeckillConstants.getKey(SeckillConstants.GOODS_TX_KEY, String.valueOf(txMessage.getTxNo()));
        Boolean decrementStock = distributedCacheService.hasKey(goodsTxKey);
        if (Boolean.TRUE.equals(decrementStock)) {
            logger.info("updateAvailableStock|秒杀商品微服务已经扣减过库存|{}", txMessage.getTxNo());
        }
        boolean isUpdate;
        try {
            isUpdate = seckillGoodsDomainService.updateAvailableStock(txMessage.getQuantity(), txMessage.getGoodsId());
            // 成功扣减库存成功
            if (isUpdate) {
                // 记录事务日志
                distributedCacheService.put(goodsTxKey, String.valueOf(txMessage.getTxNo()), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
            } else {
                // 发送失败消息给订单微服务
                messageSenderService.send(getErrorMessage(txMessage));
            }
        } catch (Exception e) {
            // 发送失败消息给订单微服务
            messageSenderService.send(getErrorMessage(txMessage));
        }
    }

    /**
     * 发送给订单微服务的错误消息
     */
    private ErrorMessage getErrorMessage(TxMessage txMessage){
        return new ErrorMessage(SeckillConstants.TOPIC_ERROR_MSG, txMessage.getTxNo(), txMessage.getGoodsId(), 
                txMessage.getQuantity(), txMessage.getPlaceOrderType(), txMessage.getException());
    }
    
}