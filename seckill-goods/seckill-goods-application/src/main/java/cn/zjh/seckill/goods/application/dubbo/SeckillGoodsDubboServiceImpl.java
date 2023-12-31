package cn.zjh.seckill.goods.application.dubbo;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.goods.application.service.SeckillGoodsService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品Dubbo服务实现类
 *
 * @author zjh - kayson
 */
@Service
@DubboService(version = "1.0.0")
public class SeckillGoodsDubboServiceImpl implements SeckillGoodsDubboService {

    @Resource
    private SeckillGoodsService seckillGoodsService;
    @Resource
    private DistributedCacheService distributedCacheService;

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        return seckillGoodsService.getSeckillGoods(id, version);
    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        return seckillGoodsService.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long goodsId) {
        return seckillGoodsService.getAvailableStockById(goodsId);
    }

}
