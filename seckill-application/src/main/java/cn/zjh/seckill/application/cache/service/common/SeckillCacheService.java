package cn.zjh.seckill.application.cache.service.common;

/**
 * 缓存顶层接口
 * 
 * @author zjh - kayson
 */
public interface SeckillCacheService {
    
    String buildCacheKey(Object key);
    
}
