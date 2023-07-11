package cn.zjh.seckill.goods.infrastructure.repository;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;
import cn.zjh.seckill.goods.domain.repository.SeckillGoodsRepository;
import cn.zjh.seckill.goods.infrastructure.mapper.SeckillGoodsMapper;
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
    public void saveSeckillGoods(SeckillGoods seckillGoods) {
        if (seckillGoods == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillGoodsMapper.saveSeckillGoods(seckillGoods);
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
    public void updateStatus(Integer status, Long id) {
        seckillGoodsMapper.updateStatus(status, id);
    }

    @Override
    public int updateAvailableStock(Integer count, Long id) {
        return seckillGoodsMapper.updateAvailableStock(count, id);
    }

    @Override
    public int incrementAvailableStock(Integer count, Long id) {
        return seckillGoodsMapper.incrementAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsMapper.getAvailableStockById(id);
    }
    
}