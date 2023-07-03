package cn.zjh.seckill.application.builder.common;

import cn.zjh.seckill.application.cache.model.SeckillBusinessCache;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.List;

/**
 * @author zjh - kayson
 */
public class SeckillCommonBuilder {

    /**
     * Json泛型化处理
     */
    public static <T> SeckillBusinessCache<T> getSeckillBusinessCache(Object object, Class<T> clazz){
        if (object == null){
            return null;
        }
        return JSON.parseObject(object.toString(), new TypeReference<SeckillBusinessCache<T>>(clazz){});
    }

    /**
     * Json泛型化处理
     */
    public static <T> SeckillBusinessCache<List<T>> getSeckillBusinessCacheList(Object object, Class<T> clazz){
        if (object == null){
            return null;
        }
        return JSON.parseObject(object.toString(), new TypeReference<SeckillBusinessCache<List<T>>>(clazz){});
    }

}
