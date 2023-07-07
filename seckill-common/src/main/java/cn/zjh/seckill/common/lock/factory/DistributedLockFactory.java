package cn.zjh.seckill.common.lock.factory;

import cn.zjh.seckill.common.lock.DistributedLock;

/**
 * 分布式锁工厂
 * 
 * @author zjh - kayson
 */
public interface DistributedLockFactory {

    /**
     * 根据key获取分布式锁
     */
    DistributedLock getDistributedLock(String key);

}
