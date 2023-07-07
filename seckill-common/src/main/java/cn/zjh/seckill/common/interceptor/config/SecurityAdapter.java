package cn.zjh.seckill.common.interceptor.config;

import cn.zjh.seckill.common.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * 安全适配器
 * 
 * @author zjh - kayson
 */
@Configuration
public class SecurityAdapter implements WebMvcConfigurer {
    
    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns("/user/login");
    }
    
}
