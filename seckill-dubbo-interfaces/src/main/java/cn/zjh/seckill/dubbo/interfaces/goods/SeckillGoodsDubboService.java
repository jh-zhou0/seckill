package cn.zjh.seckill.dubbo.interfaces.goods;

import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import org.dromara.hmily.annotation.Hmily;

/**
 * 商品Dubbo服务接口
 *
 * @author zjh - kayson
 */
public interface SeckillGoodsDubboService {

    /**
     * 根据id和版本号获取商品详情
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);

    /**
     * 扣减商品库存
     */
    @Hmily
    boolean updateAvailableStock(Integer count, Long id, Long txNo);

}
