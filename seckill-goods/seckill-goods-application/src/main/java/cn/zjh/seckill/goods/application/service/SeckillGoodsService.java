package cn.zjh.seckill.goods.application.service;

import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.goods.application.command.SeckillGoodsCommand;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
public interface SeckillGoodsService {

    /**
     * 保存商品信息
     */
    void saveSeckillGoods(SeckillGoodsCommand seckillGoodsCommand);

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
     * 增加库存
     */
    boolean incrementAvailableStock(Integer count, Long id);
    
    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);

    /**
     * 根据活动id和版本获取商品列表（带缓存）
     */
    List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version);

    /**
     * 根据id获取商品详细信息（带缓存）
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);

}