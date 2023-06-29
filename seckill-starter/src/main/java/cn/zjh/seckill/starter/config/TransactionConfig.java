package cn.zjh.seckill.starter.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring事务编程配置类
 * 
 * @author zjh - kayson
 */
@Configuration
@MapperScan(value = {"cn.zjh.seckill.infrastructure.mapper"})
@ComponentScan(value = {"cn.zjh.seckill"})
@PropertySource(value = {"classpath:properties/jdbc.properties", "classpath:properties/mybatis.properties"})
@Import({JdbcConfig.class, MyBatisConfig.class, RedisConfig.class, LongNumberConfig.class})
@ServletComponentScan(basePackages = {"cn.zjh.seckill"})
@EnableTransactionManagement(proxyTargetClass = true)
public class TransactionConfig {

    @Bean
    public TransactionManager transactionManager(DruidDataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
    
}

