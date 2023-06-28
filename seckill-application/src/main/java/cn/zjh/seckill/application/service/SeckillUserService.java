package cn.zjh.seckill.application.service;

import cn.zjh.seckill.domain.model.SeckillUser;

/**
 * 用户
 * 
 * @author zjh - kayson
 */
public interface SeckillUserService {

    /**
     * 根据用户名获取用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);
    
}
