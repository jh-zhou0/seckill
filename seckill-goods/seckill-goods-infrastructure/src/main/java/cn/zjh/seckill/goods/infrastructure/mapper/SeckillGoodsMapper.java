package cn.zjh.seckill.goods.infrastructure.mapper;

import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品
 * 
 * @author zjh - kayson
 */
public interface SeckillGoodsMapper {

    /**
     * 保存商品信息
     */
    int saveSeckillGoods(SeckillGoods seckillGoods);

    /**
     * 根据id获取商品详细信息
     */
    SeckillGoods getSeckillGoodsById(@Param("id") Long id);

    /**
     * 根据活动id获取商品列表
     */
    List<SeckillGoods> getSeckillGoodsByActivityId(@Param("activityId") Long activityId);

    /**
     * 修改商品状态
     */
    int updateStatus(@Param("status") Integer status, @Param("id") Long id);

    /**
     * 扣减库存
     */
    int updateAvailableStock(@Param("count") Integer count, @Param("id") Long id);

    /**
     * 增加库存
     */
    int incrementAvailableStock(@Param("count") Integer count, @Param("id") Long id);

    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(@Param("id") Long id);

}