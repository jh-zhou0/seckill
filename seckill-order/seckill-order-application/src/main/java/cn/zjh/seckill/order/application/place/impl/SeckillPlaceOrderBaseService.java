package cn.zjh.seckill.order.application.place.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 基础的下单服务，主要包含Confirm与Cancel方法
 *
 * @author zjh - kayson
 */
@Service
public class SeckillPlaceOrderBaseService {
    
    @DubboReference(version = "1.0.0", check = false)
    protected SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    protected SeckillOrderDomainService seckillOrderDomainService;
    @Resource
    protected DistributedCacheService distributedCacheService;

}
