package cn.zjh.seckill.goods;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zjh - kayson
 */
@EnableDubbo
@SpringBootApplication
public class SeckillGoodsStarter {

    public static void main(String[] args) {
        SpringApplication.run(SeckillGoodsStarter.class, args);
    }
    
}
