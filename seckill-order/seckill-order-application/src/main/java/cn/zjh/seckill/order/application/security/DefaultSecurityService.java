package cn.zjh.seckill.order.application.security;

/**
 * 模拟风控
 * 
 * @author zjh - kayson
 */
public class DefaultSecurityService implements SecurityService {
    
    @Override
    public boolean securityPolicy(Long userId) {
        return true;
    }
    
}
