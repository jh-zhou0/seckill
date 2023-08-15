package cn.zjh.seckill.stock.interfaces.controller;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.response.ResponseMessage;
import cn.zjh.seckill.common.response.ResponseMessageBuilder;
import cn.zjh.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import cn.zjh.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import cn.zjh.seckill.stock.application.service.SeckillStockBucketService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 分桶库存
 *
 * @author zjh - kayson
 */
@RestController
@RequestMapping("/stock/bucket")
public class SeckillStockBucketController {

    @Resource
    private SeckillStockBucketService seckillStockBucketService;

    @PostMapping("/arrangeStockBuckets")
    public ResponseMessage<String> arrangeStockBuckets(@RequestAttribute Long userId, @RequestBody SeckillStockBucketWrapperCommand seckillStockCommand) {
        seckillStockBucketService.arrangeStockBuckets(userId, seckillStockCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    @GetMapping(value = "/getTotalStockBuckets")
    public ResponseMessage<SeckillStockBucketDTO> getTotalStockBuckets(Long goodsId, Long version) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillStockBucketService.getTotalStockBuckets(goodsId, version));
    }

}
