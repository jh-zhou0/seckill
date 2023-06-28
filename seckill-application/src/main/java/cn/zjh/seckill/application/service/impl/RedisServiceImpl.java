package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.service.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author zjh - kayson
 */
@Service
public class RedisServiceImpl implements RedisService {
    
    @Resource
    private RedisTemplate<String, Object> RedisTemplate;
    
    @Override
    public void set(String key, Object value) {
        RedisTemplate.opsForValue().set(key, value);
    }
    
}
