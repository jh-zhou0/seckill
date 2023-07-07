package cn.zjh.seckill.common.cache.service;

/**
 * 缓存顶层接口
 * 
 * @author zjh - kayson
 */
public interface SeckillCacheService {
    
    String buildCacheKey(Object key);
    
}