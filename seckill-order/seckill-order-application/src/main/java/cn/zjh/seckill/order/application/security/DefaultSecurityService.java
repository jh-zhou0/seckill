package cn.zjh.seckill.order.application.security;

import org.springframework.stereotype.Service;

/**
 * 模拟风控
 * 
 * @author zjh - kayson
 */
@Service
public class DefaultSecurityService implements SecurityService {
    
    @Override
    public boolean securityPolicy(Long userId) {
        return true;
    }
    
}
