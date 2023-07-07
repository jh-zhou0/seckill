package cn.zjh.seckill.common.cache.distributed;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 分布式缓存接口
 * 
 * @author zjh - kayson
 */
public interface DistributedCacheService {
    
    void put(String key, String value);

    void put(String key, Object value);

    void put(String key, Object value, long timeout, TimeUnit unit);

    void put(String key, Object value, long expireTime);

    <T> T getObject(String key, Class<T> targetClass);

    Object getObject(String key);

    String getString(String key);

    <T> List<T> getList(String key, Class<T> targetClass);

    Boolean delete(String key);

    Boolean hasKey(String key);

    /**
     * 扣减内存中的数据
     */
    default Long decrement(String key, long delta){
        return null;
    }
    /**
     * 增加内存中的数据
     */
    default Long increment(String key, long delta){
        return null;
    }

    /**
     * 使用Lua脚本扣减库存
     */
    default Long decrementByLua(String key, Integer quantity){
        return null;
    }
    /**
     * 使用Lua脚本增加库存
     */
    default Long incrementByLua(String key, Integer quantity){
        return null;
    }

    /**
     * 使用Lua脚本初始化库存
     */
    default Long initByLua(String key, Integer quantity){
        return null;
    }

}