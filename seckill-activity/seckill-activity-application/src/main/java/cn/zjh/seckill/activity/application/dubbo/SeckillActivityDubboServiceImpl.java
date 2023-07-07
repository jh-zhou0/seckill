package cn.zjh.seckill.activity.application.dubbo;

import cn.zjh.seckill.activity.application.service.SeckillActivityService;
import cn.zjh.seckill.common.model.dto.SeckillActivityDTO;
import cn.zjh.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Dubbo服务
 * 
 * @author zjh - kayson
 */
@Service
@DubboService(version = "1.0.0")
public class SeckillActivityDubboServiceImpl implements SeckillActivityDubboService {
    
    @Resource
    private SeckillActivityService seckillActivityService;
    
    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        return seckillActivityService.getSeckillActivity(id, version);
    }
    
}
