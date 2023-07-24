package cn.zjh.seckill.order.application.service.impl;

import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.model.dto.SeckillOrderSubmitDTO;
import cn.zjh.seckill.order.application.model.command.SeckillOrderCommand;
import cn.zjh.seckill.order.application.service.SeckillSubmitOrderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 同步提交订单
 * 
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "sync")
public class SeckillSyncSubmitOrderServiceImpl extends SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        // 进行基本的检查 
        checkSeckillOrder(userId, seckillOrderCommand);
        Long orderId = seckillPlaceOrderService.placeOrder(userId, seckillOrderCommand);
        return new SeckillOrderSubmitDTO(String.valueOf(orderId), seckillOrderCommand.getGoodsId(), SeckillConstants.TYPE_ORDER);
    }
    
}
