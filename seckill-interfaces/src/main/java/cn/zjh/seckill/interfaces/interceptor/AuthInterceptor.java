package cn.zjh.seckill.interfaces.interceptor;

import cn.zjh.seckill.domain.code.ErrorCode;
import cn.zjh.seckill.domain.constants.SeckillConstants;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.infrastructure.shiro.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录授权拦截验证
 * 
 * @author zjh - kayson
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private static final String USER_ID = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Object userIdObj = request.getAttribute(USER_ID);
        if (userIdObj != null) {
            return true;
        }
        String token = request.getHeader(SeckillConstants.TOKEN_HEADER_NAME);
        if (!StringUtils.hasText(token)) {
            throw new SeckillException(ErrorCode.USER_NOT_LOGIN);
        }
        Long userId = JwtUtils.getUserId(token);
        if (userId == null) {
            throw new SeckillException(ErrorCode.USER_NOT_LOGIN);
        }
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        wrapper.setAttribute(USER_ID, userId);
        return true;
    }
    
}
