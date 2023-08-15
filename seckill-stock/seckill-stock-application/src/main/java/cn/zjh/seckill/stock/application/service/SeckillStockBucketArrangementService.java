package cn.zjh.seckill.stock.application.service;

import cn.zjh.seckill.stock.application.model.dto.SeckillStockBucketDTO;

/**
 * 库存编排 Service
 * 
 * @author zjh - kayson
 */
public interface SeckillStockBucketArrangementService {

    /**
     * 编码分桶库存
     * 
     * @param goodsId 商品id
     * @param stock 库存数量
     * @param bucketsQuantity 分桶数量
     * @param assignmentMode 编排模式, 1:总量模式; 2:增量模式
     */
    void arrangeStockBuckets(Long goodsId, Integer stock, Integer bucketsQuantity, Integer assignmentMode);

    /**
     * 通过商品id获取库存分桶信息
     * 
     * @param goodsId 商品id
     * @param version 版本号
     * @return 库存分桶信息
     */
    SeckillStockBucketDTO getSeckillStockBucketDTO(Long goodsId, Long version);
    
}
