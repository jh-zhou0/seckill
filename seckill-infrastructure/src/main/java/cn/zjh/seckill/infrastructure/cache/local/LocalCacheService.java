package cn.zjh.seckill.infrastructure.cache.local;

/**
 * 本地缓存服务接口
 * 
 * @author zjh - kayson
 */
public interface LocalCacheService<K, V> {

    void put(K key, V value);

    V getIfPresent(Object key);
    
}
