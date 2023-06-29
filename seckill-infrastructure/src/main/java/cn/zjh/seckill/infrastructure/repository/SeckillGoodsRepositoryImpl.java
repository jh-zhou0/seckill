package cn.zjh.seckill.infrastructure.repository;

import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillGoods;
import cn.zjh.seckill.domain.repository.SeckillGoodsRepository;
import cn.zjh.seckill.infrastructure.mapper.SeckillGoodsMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
@Repository
public class SeckillGoodsRepositoryImpl implements SeckillGoodsRepository {
    
    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public int saveSeckillGoods(SeckillGoods seckillGoods) {
        if (seckillGoods == null) {
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        return seckillGoodsMapper.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsById(Long id) {
        return seckillGoodsMapper.getSeckillGoodsById(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsMapper.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillGoodsMapper.updateStatus(status, id);
    }

    @Override
    public int updateAvailableStock(Integer count, Long id) {
        return seckillGoodsMapper.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsMapper.getAvailableStockById(id);
    }
    
}