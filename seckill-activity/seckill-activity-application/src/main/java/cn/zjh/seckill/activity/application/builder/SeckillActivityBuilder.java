package cn.zjh.seckill.activity.application.builder;

import cn.zjh.seckill.activity.application.command.SeckillActivityCommand;
import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;
import cn.zjh.seckill.common.builder.SeckillCommonBuilder;
import cn.zjh.seckill.common.model.dto.SeckillActivityDTO;
import cn.zjh.seckill.common.utils.beans.BeanUtil;

/**
 * 秒杀活动构建类
 * 
 * @author zjh - kayson
 */
public class SeckillActivityBuilder extends SeckillCommonBuilder {

    public static SeckillActivity toSeckillActivity(SeckillActivityCommand seckillActivityCommand){
        if (seckillActivityCommand == null){
            return null;
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtil.copyProperties(seckillActivityCommand, seckillActivity);
        return seckillActivity;
    }

    public static SeckillActivityDTO toSeckillActivityDTO(SeckillActivity seckillActivity){
        if (seckillActivity == null){
            return null;
        }
        SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
        BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
        return seckillActivityDTO;
    }

}