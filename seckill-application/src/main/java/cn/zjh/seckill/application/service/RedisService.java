package cn.zjh.seckill.application.service;

/**
 * Redis缓存接口
 * 
 * @author zjh - kayson
 */
public interface RedisService {

    /**
     * 设置缓存
     * 
     * @param key 键
     * @param value 值
     */
    void set(String key, Object value);
    
}
