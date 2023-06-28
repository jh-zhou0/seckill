package cn.zjh.seckill.infrastructure.repository;

import cn.zjh.seckill.domain.model.SeckillUser;
import cn.zjh.seckill.domain.repository.SeckillUserRepository;
import cn.zjh.seckill.infrastructure.mapper.SeckillUserMapper;
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
