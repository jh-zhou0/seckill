package cn.zjh.seckill.infrastructure.mapper;

import cn.zjh.seckill.domain.model.SeckillUser;
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
