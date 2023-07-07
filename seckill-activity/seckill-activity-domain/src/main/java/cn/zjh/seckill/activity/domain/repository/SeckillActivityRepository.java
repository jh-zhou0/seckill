package cn.zjh.seckill.activity.domain.repository;

import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;

import java.util.Date;
import java.util.List;

/**
 * 活动
 * 
 * @author zjh - kayson
 */
public interface SeckillActivityRepository {

    /**
     * 保存活动信息
     */
    void saveSeckillActivity(SeckillActivity seckillActivity);

    /**
     * 根据状态获取活动列表
     */
    List<SeckillActivity> getSeckillActivityList(Integer status);

    /**
     * 根据时间和状态获取活动列表
     */
    List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status);

    /**
     * 根据id获取活动信息
     */
    SeckillActivity getSeckillActivityById(Long id);

    /**
     * 修改状态
     */
    void updateStatus(Integer status, Long id);

}