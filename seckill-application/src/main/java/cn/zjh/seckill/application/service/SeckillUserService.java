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

    /**
     * 根据用户id获取用户信息
     * 
     * @param userId 用户id
     * @return 用户信息
     */
    SeckillUser getSeckillUserByUserId(Long userId);

    /**
     * 登录
     * 
     * @param userName 用户名
     * @param password 密码
     * @return token
     */
    String login(String userName, String password);
    
}
