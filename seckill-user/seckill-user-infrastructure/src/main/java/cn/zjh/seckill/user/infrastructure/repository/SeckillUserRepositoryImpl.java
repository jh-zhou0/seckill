package cn.zjh.seckill.user.infrastructure.repository;

import cn.zjh.seckill.user.domain.model.entity.SeckillUser;
import cn.zjh.seckill.user.domain.repository.SeckillUserRepository;
import cn.zjh.seckill.user.infrastructure.mapper.SeckillUserMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 用户
 * 
 * @author zjh - kayson
 */
@Repository
public class SeckillUserRepositoryImpl implements SeckillUserRepository {
    
    @Resource
    private SeckillUserMapper seckillUserMapper;
    
    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        return seckillUserMapper.getSeckillUserByUserName(userName);
    }
    
}