package cn.zjh.seckill.order.application.message;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.model.message.TxMessage;
import cn.zjh.seckill.order.application.place.SeckillPlaceOrderService;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 监听事务消息
 *
 * @author zjh - kayson
 */
@Component
@RocketMQTransactionListener
public class OrderTxMessageListener implements RocketMQLocalTransactionListener {

    public static final Logger logger = LoggerFactory.getLogger(OrderTxMessageListener.class);

    @Resource
    private SeckillPlaceOrderService seckillPlaceOrderService;
    @Resource
    private DistributedCacheService distributedCacheService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        TxMessage txMessage = getTxMessage(message);
        try {
            // 已经抛出了异常，则直接回滚
            if (Boolean.TRUE.equals(txMessage.getException())) {
                return RocketMQLocalTransactionState.ROLLBACK;
            }
            seckillPlaceOrderService.saveOrderInTransaction(txMessage);
            logger.info("executeLocalTransaction|秒杀订单微服务成功提交本地事务|{}", txMessage.getTxNo());
            return RocketMQLocalTransactionState.COMMIT;
        } catch (Exception e) {
            logger.error("executeLocalTransaction|秒杀订单微服务异常回滚事务|{}", txMessage.getTxNo());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        TxMessage txMessage = getTxMessage(message);
        logger.info("checkLocalTransaction|秒杀订单微服务查询本地事务|{}", txMessage.getTxNo());
        Boolean submitTransaction = distributedCacheService.hasKey(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
        return Boolean.TRUE.equals(submitTransaction) ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.UNKNOWN;
    }

    private TxMessage getTxMessage(Message msg) {
        String messageString = new String((byte[]) msg.getPayload());
        JSONObject jsonObject = JSONObject.parseObject(messageString);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }

}
