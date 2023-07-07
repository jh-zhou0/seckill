package cn.zjh.seckill.goods.config;

import cn.zjh.seckill.common.serializer.NumberSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 新增Long类型序列化规则，数值超过2^53-1，在JS会出现精度丢失问题，因此Long自动序列化为字符串类型
 * 
 * @author zjh - kayson
 */
@Configuration
public class LongNumberConfig {

    @Bean
    public BeanPostProcessor objectMapperBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (!(bean instanceof ObjectMapper)) {
                    return bean;
                }
                ObjectMapper objectMapper = (ObjectMapper) bean;
                SimpleModule simpleModule = new SimpleModule();

                simpleModule
                        .addSerializer(Long.class, NumberSerializer.INSTANCE)
                        .addSerializer(Long.TYPE, NumberSerializer.INSTANCE);
                objectMapper.registerModule(simpleModule);
                
                return bean;
            }
        };
    }
    
}
