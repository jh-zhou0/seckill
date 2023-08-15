package cn.zjh.seckill.stock.application.service;

import cn.zjh.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import cn.zjh.seckill.stock.application.model.dto.SeckillStockBucketDTO;

/**
 * 商品库存 Service
 * 
 * @author zjh - kayson
 */
public interface SeckillStockBucketService {

    /**
     * 编排库存
     * 
     * @param userId 用户id
     * @param stockBucketWrapperCommand 编排信息
     */
    void arrangeStockBuckets(Long userId, SeckillStockBucketWrapperCommand stockBucketWrapperCommand);

    /**
     * 获取库存分桶
     * 
     * @param goodsId 商品id
     * @param version 版本号
     * @return 库存分桶信息
     */
    SeckillStockBucketDTO getTotalStockBuckets(Long goodsId, Long version);
    
}
