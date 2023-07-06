package cn.zjh.seckill.infrastructure.cache.local.guava;

import cn.zjh.seckill.infrastructure.cache.local.LocalCacheService;
import com.google.common.cache.Cache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * 基于Guava实现的本地缓存
 * 
 * @author zjh - kayson
 */
@Service
@ConditionalOnProperty(value = "local.cache.type", havingValue = "guava")
public class GuavaLocalCacheService<K, V> implements LocalCacheService<K, V> {

    // 基于Guava实现的本地缓存
    private final Cache<K, V> cache = LocalCacheFactory.getLocalCache();

    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V getIfPresent(Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void delete(Object key) {
        cache.invalidate(key);
    }
    
}
