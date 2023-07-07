package cn.zjh.seckill.order.application.security;

/**
 * 模拟风控服务
 *
 * @author zjh - kayson
 */
public interface SecurityService {

    /**
     * 对用户进行风控处理
     */
    boolean securityPolicy(Long userId);
}