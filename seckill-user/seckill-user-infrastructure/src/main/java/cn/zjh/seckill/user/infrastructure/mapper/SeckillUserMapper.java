package cn.zjh.seckill.user.infrastructure.mapper;

import cn.zjh.seckill.user.domain.model.entity.SeckillUser;
import org.apache.ibatis.annotations.Param;

/**
 * 用户
 * 
 * @author zjh - kayson
 */
public interface SeckillUserMapper {

    /**
     * 根据用户名获取用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    SeckillUser getSeckillUserByUserName(@Param("userName") String userName);
    
}