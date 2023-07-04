package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.application.service.SeckillOrderService;
import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.dto.SeckillOrderDTO;
import cn.zjh.seckill.domain.enums.SeckillGoodsStatus;
import cn.zjh.seckill.domain.enums.SeckillOrderStatus;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillGoods;
import cn.zjh.seckill.domain.model.SeckillOrder;
import cn.zjh.seckill.domain.service.SeckillOrderDomainService;
import cn.zjh.seckill.infrastructure.utils.beans.BeanUtil;
import cn.zjh.seckill.infrastructure.utils.id.SnowFlakeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 订单
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {
    
    @Resource
    private SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    private SeckillGoodsService seckillGoodsService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrder saveSeckillOrder(SeckillOrderDTO seckillOrderDTO) {
        if (seckillOrderDTO == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        // 获取商品
        SeckillGoods seckillGoods = seckillGoodsService.getSeckillGoodsById(seckillOrderDTO.getGoodsId());
        // 商品不存在
        if (seckillGoods == null) {
            throw new SeckillException(HttpCode.GOODS_NOT_EXISTS);
        }
        // 商品未上线
        if (Objects.equals(seckillGoods.getStatus(), SeckillGoodsStatus.PUBLISHED.getCode())){
            throw new SeckillException(HttpCode.GOODS_PUBLISH);
        }
        // 商品已下架
        if (Objects.equals(seckillGoods.getStatus(), SeckillGoodsStatus.OFFLINE.getCode())){
            throw new SeckillException(HttpCode.GOODS_OFFLINE);
        }
        // 触发限购
        if (seckillGoods.getLimitNum() < seckillOrderDTO.getQuantity()){
            throw new SeckillException(HttpCode.BEYOND_LIMIT_NUM);
        }
        // 库存不足
        if (seckillGoods.getAvailableStock() == null
                || seckillGoods.getAvailableStock() <= 0
                || seckillOrderDTO.getQuantity() > seckillGoods.getAvailableStock()){
            throw new SeckillException(HttpCode.STOCK_LT_ZERO);
        }

        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderDTO, seckillOrder);
        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillOrder.setGoodsName(seckillGoods.getGoodsName());
        seckillOrder.setActivityPrice(seckillGoods.getActivityPrice());
        BigDecimal orderPrice = seckillGoods.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        
        // 保存订单
        seckillOrderDomainService.saveSeckillOrder(seckillOrder);
        // 扣减库存
        seckillGoodsService.updateAvailableStock(seckillOrderDTO.getQuantity(), seckillOrderDTO.getGoodsId());
        return seckillOrder;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderDomainService.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderDomainService.getSeckillOrderByActivityId(activityId);
    }
    
}
