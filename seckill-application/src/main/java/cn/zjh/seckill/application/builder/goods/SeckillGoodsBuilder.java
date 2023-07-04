package cn.zjh.seckill.application.builder.goods;

import cn.zjh.seckill.application.builder.common.SeckillCommonBuilder;
import cn.zjh.seckill.application.command.SeckillGoodsCommand;
import cn.zjh.seckill.domain.dto.SeckillGoodsDTO;
import cn.zjh.seckill.domain.model.SeckillGoods;
import cn.zjh.seckill.infrastructure.utils.beans.BeanUtil;

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
