package cn.zjh.seckill.goods.application.builder;

import cn.zjh.seckill.common.builder.SeckillCommonBuilder;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.common.utils.beans.BeanUtil;
import cn.zjh.seckill.goods.application.command.SeckillGoodsCommand;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;

/**
 * 秒杀商品构建类
 * 
 * @author zjh - kayson
 */
public class SeckillGoodsBuilder extends SeckillCommonBuilder {

    public static SeckillGoods toSeckillGoods(SeckillGoodsCommand seckillGoodsCommand){
        if (seckillGoodsCommand == null){
            return null;
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsCommand, seckillGoods);
        return seckillGoods;
    }

    public static SeckillGoodsDTO toSeckillGoodsDTO(SeckillGoods seckillGoods){
        if (seckillGoods == null){
            return null;
        }
        SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
        BeanUtil.copyProperties(seckillGoods, seckillGoodsDTO);
        return seckillGoodsDTO;
    }

}