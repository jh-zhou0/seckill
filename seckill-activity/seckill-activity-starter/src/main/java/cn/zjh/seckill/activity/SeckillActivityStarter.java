package cn.zjh.seckill.activity;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zjh - kayson
 */
@EnableDubbo
@SpringBootApplication
public class SeckillActivityStarter {

    public static void main(String[] args) {
        SpringApplication.run(SeckillActivityStarter.class, args);
    }
    
}
