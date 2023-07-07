package cn.zjh.seckill.user.application.service.impl;

import cn.zjh.seckill.common.cache.distributed.DistributedCacheService;
import cn.zjh.seckill.common.constants.SeckillConstants;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.exception.SeckillException;
import cn.zjh.seckill.common.shiro.utils.CommonUtils;
import cn.zjh.seckill.common.shiro.utils.JwtUtils;
import cn.zjh.seckill.user.application.service.SeckillUserService;
import cn.zjh.seckill.user.domain.model.entity.SeckillUser;
import cn.zjh.seckill.user.domain.repository.SeckillUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
    private DistributedCacheService distributedCacheService;

    @Override
    public SeckillUser getSeckillUserByUserId(Long userId) {
        String key = SeckillConstants.getKey(SeckillConstants.USER_KEY_PREFIX, String.valueOf(userId));
        return (SeckillUser) distributedCacheService.getObject(key);
    }

    @Override
    public String login(String userName, String password) {
        if (StringUtils.isEmpty(userName)){
            throw new SeckillException(ErrorCode.USERNAME_IS_NULL);
        }
        if (StringUtils.isEmpty(password)){
            throw new SeckillException(ErrorCode.PASSWORD_IS_NULL);
        }
        SeckillUser seckillUser = seckillUserRepository.getSeckillUserByUserName(userName);
        if (seckillUser == null){
            throw new SeckillException(ErrorCode.USERNAME_IS_ERROR);
        }
        String paramsPassword = CommonUtils.encryptPassword(password, userName);
        if (!paramsPassword.equals(seckillUser.getPassword())){
            throw new SeckillException(ErrorCode.PASSWORD_IS_ERROR);
        }
        String token = JwtUtils.sign(seckillUser.getId());
        String key = SeckillConstants.getKey(SeckillConstants.USER_KEY_PREFIX, String.valueOf(seckillUser.getId()));
        // 缓存到Redis
        distributedCacheService.put(key, seckillUser);
        // 返回Token
        return token;
    }

}