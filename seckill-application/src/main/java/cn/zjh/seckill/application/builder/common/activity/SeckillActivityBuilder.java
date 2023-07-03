package cn.zjh.seckill.application.builder.common.activity;

import cn.zjh.seckill.application.builder.common.SeckillCommonBuilder;
import cn.zjh.seckill.application.command.SeckillActivityCommand;
import cn.zjh.seckill.domain.dto.SeckillActivityDTO;
import cn.zjh.seckill.domain.model.SeckillActivity;
import cn.zjh.seckill.infrastructure.utils.beans.BeanUtil;

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
