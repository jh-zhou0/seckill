package cn.zjh.seckill.stock.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring事务编程配置类
 * 
 * @author zjh - kayson
 */
@Configuration
@MapperScan(value = {"cn.zjh.seckill.stock.infrastructure.mapper"})
@ComponentScan(value = {"cn.zjh.seckill"})
@Import({RedisConfig.class})
@ServletComponentScan(basePackages = {"cn.zjh.seckill"})
@EnableTransactionManagement(proxyTargetClass = true)
public class TransactionConfig {
    
}

