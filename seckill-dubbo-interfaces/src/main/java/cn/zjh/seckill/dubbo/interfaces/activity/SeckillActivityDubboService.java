package cn.zjh.seckill.dubbo.interfaces.activity;

import cn.zjh.seckill.common.model.dto.SeckillActivityDTO;

/**
 * 活动相关的Dubbo服务
 *
 * @author zjh - kayson
 */
public interface SeckillActivityDubboService {

    /**
     * 获取活动信息
     */
    SeckillActivityDTO getSeckillActivity(Long id, Long version);

}
