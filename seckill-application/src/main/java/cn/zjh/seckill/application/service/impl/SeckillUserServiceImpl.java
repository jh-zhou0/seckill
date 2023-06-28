package cn.zjh.seckill.application.service.impl;

import cn.zjh.seckill.application.service.SeckillUserService;
import cn.zjh.seckill.domain.model.SeckillUser;
import cn.zjh.seckill.domain.repository.SeckillUserRepository;
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
    
    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        return seckillUserRepository.getSeckillUserByUserName(userName);
    }
    
}
