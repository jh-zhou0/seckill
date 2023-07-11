package cn.zjh.seckill.activity.config;

import org.dromara.hmily.spring.HmilyApplicationContextAware;
import org.dromara.hmily.spring.annotation.RefererAnnotationBeanPostProcessor;
import org.dromara.hmily.spring.aop.SpringHmilyTransactionAspect;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Hmily 配置类
 * 
 * @author zjh - kayson
 */
@Configuration
public class HmilyConfig {

    @Bean
    public SpringHmilyTransactionAspect hmilyTransactionAspect(){
        return new SpringHmilyTransactionAspect();
    }

    @Bean
    public HmilyApplicationContextAware hmilyApplicationContextAware(){
        return new HmilyApplicationContextAware();
    }

    @Bean
    public BeanPostProcessor refererAnnotationBeanPostProcessor() {
        return new RefererAnnotationBeanPostProcessor();
    }
    
}
