package cn.zjh.seckill.goods.domain.service;

import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

/**
 * 商品领域层的服务接口
 * 
 * @author zjh - kayson
 */
public interface SeckillGoodsDomainService {

    /**
     * 保存商品信息
     */
    void saveSeckillGoods(SeckillGoods seckillGoods);

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
    boolean updateAvailableStock(Integer count, Long id);

    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);

}