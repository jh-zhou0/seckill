package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.service.RedisService;
import cn.zjh.seckill.application.service.SeckillUserService;
import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.constants.SeckillConstants;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.model.SeckillUser;
import cn.zjh.seckill.domain.repository.SeckillUserRepository;
import cn.zjh.seckill.infrastructure.shiro.utils.CommonUtils;
import cn.zjh.seckill.infrastructure.shiro.utils.JwtUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 用户
 * 
 * @author zjh - kayson
 */
@Service
public class SeckillUserServiceImpl implements SeckillUserService {
    
    @Resource
    private SeckillUserRepository seckillUserRepository;
    @Resource
    private RedisService redisService;
    
    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        return seckillUserRepository.getSeckillUserByUserName(userName);
    }

    @Override
    public SeckillUser getSeckillUserByUserId(Long userId) {
        String key = SeckillConstants.getKey(SeckillConstants.USER_KEY_PREFIX, String.valueOf(userId));
        return (SeckillUser) redisService.get(key);
    }

    @Override
    public String login(String userName, String password) {
        if (!StringUtils.hasText(userName)){
            throw new SeckillException(HttpCode.USERNAME_IS_NULL);
        }
        if (!StringUtils.hasText(password)){
            throw new SeckillException(HttpCode.PASSWORD_IS_NULL);
        }
        SeckillUser seckillUser = seckillUserRepository.getSeckillUserByUserName(userName);
        if (seckillUser == null){
            throw new SeckillException(HttpCode.USERNAME_IS_ERROR);
        }
        String paramsPassword = CommonUtils.encryptPassword(password, userName);
        if (!paramsPassword.equals(seckillUser.getPassword())){
            throw new SeckillException(HttpCode.PASSWORD_IS_ERROR);
        }
        String token = JwtUtils.sign(seckillUser.getId());
        String key = SeckillConstants.getKey(SeckillConstants.USER_KEY_PREFIX, String.valueOf(seckillUser.getId()));
        // 缓存到Redis
        redisService.set(key, seckillUser);
        // 返回Token
        return token;
    }

}
