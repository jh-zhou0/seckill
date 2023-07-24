package cn.zjh.seckill.order.application.service.impl;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import cn.zjh.seckill.order.application.model.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import cn.zjh.seckill.order.application.security.SecurityService;
import cn.zjh.seckill.order.application.service.SeckillSubmitOrderService;
import org.apache.dubbo.config.annotation.DubboReference;

import javax.annotation.Resource;

/**
 * 提交订单基础实现类
 *
 * @author zjh - kayson
 */
public abstract class SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {

    @Resource
    private SecurityService securityService;
    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Resource
    protected SeckillPlaceOrderService seckillPlaceOrderService;

    @Override
    public void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (userId == null || seckillOrderCommand == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        // 模拟风控
        if (!securityService.securityPolicy(userId)){
            throw new SeckillException(ErrorCode.USER_INVALID);
        }
        // 获取商品信息
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        // 检测商品信息
        seckillPlaceOrderService.checkSeckillGoods(seckillOrderCommand, seckillGoods);
    }
}
