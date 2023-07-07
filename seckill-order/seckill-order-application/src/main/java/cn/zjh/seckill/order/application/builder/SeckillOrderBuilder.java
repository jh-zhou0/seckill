package cn.zjh.seckill.order.application.builder;

import cn.zjh.seckill.common.builder.SeckillCommonBuilder;
import cn.zjh.seckill.common.utils.beans.BeanUtil;
import cn.zjh.seckill.order.application.command.SeckillOrderCommand;
import cn.zjh.seckill.order.domain.model.entity.SeckillOrder;

/**
 * 订单对象转换类
 * 
 * @author zjh - kayson
 */
public class SeckillOrderBuilder extends SeckillCommonBuilder {

    public static SeckillOrder toSeckillOrder(SeckillOrderCommand seckillOrderCommand){
        if (seckillOrderCommand == null){
            return null;
        }
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        return seckillOrder;
    }
    
}
