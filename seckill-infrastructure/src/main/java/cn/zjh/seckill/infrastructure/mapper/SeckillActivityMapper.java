package cn.zjh.seckill.infrastructure.mapper;

import cn.zjh.seckill.domain.model.SeckillActivity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 活动
 * 
 * @author zjh - kayson
 */
public interface SeckillActivityMapper {

    /**
     * 保存活动信息
     */
    int saveSeckillActivity(SeckillActivity seckillActivity);

    /**
     * 根据状态获取活动列表
     */
    List<SeckillActivity> getSeckillActivityList(@Param("status") Integer status);

    /**
     * 根据时间和状态获取活动列表
     */
    List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(@Param("currentTime") Date currentTime, @Param("status") Integer status);

    /**
     * 根据id获取活动信息
     */
    SeckillActivity getSeckillActivityById(@Param("id") Long id);
    /**
     * 修改状态
     */
    int updateStatus(@Param("status") Integer status, @Param("id") Long id);

}
