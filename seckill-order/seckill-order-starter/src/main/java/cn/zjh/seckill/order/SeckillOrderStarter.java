package cn.zjh.seckill.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zjh - kayson
 */
@EnableDubbo
@SpringBootApplication
public class SeckillOrderStarter {

    public static void main(String[] args) {
        // fix -> org.apache.dubbo.common.cache.FileCacheStoreFactory$PathNotExclusiveException: C:\Users\zjh\.dubbo\.metadata.nacos192.168.100.99%003a8848.dubbo.cache is not exclusive.
        System.setProperty("user.home", "/home/zjh/order");
        SpringApplication.run(SeckillOrderStarter.class, args);
    }
    
}
