package cn.zjh.seckill.user.domain.repository;

import cn.zjh.seckill.user.domain.model.entity.SeckillUser;

/**
 * 用户
 * 
 * @author zjh - kayson
 */
public interface SeckillUserRepository {

    /**
     * 根据用户名获取用户信息
     * 
     * @param userName 用户名
     * @return 用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);
    
}