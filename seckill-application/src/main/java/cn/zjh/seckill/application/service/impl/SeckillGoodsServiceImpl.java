package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.dto.SeckillGoodsDTO;
import cn.zjh.seckill.domain.enums.SeckillGoodsStatus;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillActivity;
import cn.zjh.seckill.domain.model.SeckillGoods;
import cn.zjh.seckill.domain.repository.SeckillActivityRepository;
import cn.zjh.seckill.domain.repository.SeckillGoodsRepository;
import cn.zjh.seckill.infrastructure.utils.beans.BeanUtil;
import cn.zjh.seckill.infrastructure.utils.id.SnowFlakeFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    
    @Resource
    private SeckillGoodsRepository seckillGoodsRepository;
    @Resource
    private SeckillActivityRepository seckillActivityRepository;
    
    @Override
    public int saveSeckillGoods(SeckillGoodsDTO seckillGoodsDTO) {
        if (seckillGoodsDTO == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = seckillActivityRepository.getSeckillActivityById(seckillGoodsDTO.getActivityId());
        if (seckillActivity == null) {
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsDTO, seckillGoods);
        seckillGoods.setId(SnowFlakeFactory.getSnowFlake().nextId());
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        seckillGoods.setAvailableStock(seckillGoodsDTO.getInitialStock());
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        return seckillGoodsRepository.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsRepository.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsRepository.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillGoodsRepository.updateStatus(status, id);
    }

    @Override
    public int updateAvailableStock(Integer count, Long id) {
        return seckillGoodsRepository.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsRepository.getAvailableStockById(id);
    }
    
}
