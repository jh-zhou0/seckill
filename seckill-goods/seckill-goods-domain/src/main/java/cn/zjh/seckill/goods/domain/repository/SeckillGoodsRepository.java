package cn.zjh.seckill.goods.domain.repository;

import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
public interface SeckillGoodsRepository {

    /**
     * 保存商品信息
     */
    int saveSeckillGoods(SeckillGoods seckillGoods);

    /**
     * 根据id获取商品详细信息
     */
    SeckillGoods getSeckillGoodsById(Long id);

    /**
     * 根据活动id获取商品列表
     */
    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    /**
     * 修改商品状态
     */
    void updateStatus(Integer status, Long id);

    /**
     * 扣减库存
     */
    int updateAvailableStock(Integer count, Long id);

    /**
     * 增加库存
     */
    int incrementAvailableStock(Integer count, Long id);

    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);

}