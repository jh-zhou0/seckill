package cn.zjh.seckill.user.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Mybatis 配置
 * 
 * @author zjh - kayson
 */
public class MyBatisConfig {
    
    @Value("${mybatis.scan-packages}")
    private String scanPackages;
    
    @Bean
    public SqlSessionFactoryBean sqlSessionFactory(DruidDataSource dataSource) {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dataSource);
        sqlSessionFactory.setTypeAliasesPackage(scanPackages);
        return sqlSessionFactory;
    }
    
}
