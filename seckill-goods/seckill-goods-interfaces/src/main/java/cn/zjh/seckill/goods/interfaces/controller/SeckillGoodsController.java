package cn.zjh.seckill.goods.interfaces.controller;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.model.dto.SeckillGoodsDTO;
import cn.zjh.seckill.common.response.ResponseMessage;
import cn.zjh.seckill.common.response.ResponseMessageBuilder;
import cn.zjh.seckill.goods.application.command.SeckillGoodsCommand;
import cn.zjh.seckill.goods.application.service.SeckillGoodsService;
import cn.zjh.seckill.goods.domain.model.entity.SeckillGoods;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 商品
 *
 * @author zjh - kayson
 */
@RestController
@RequestMapping("/goods")
public class SeckillGoodsController {

    @Resource
    private SeckillGoodsService seckillGoodsService;

    /**
     * 保存秒杀商品
     */
    @PostMapping("/saveSeckillGoods")
    public ResponseMessage<String> saveSeckillActivity(SeckillGoodsCommand seckillGoodsCommand) {
        seckillGoodsService.saveSeckillGoods(seckillGoodsCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/getSeckillGoodsById")
    public ResponseMessage<SeckillGoods> getSeckillGoodsId(Long id) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsById(id));
    }

    /**
     * 根据商品id和版本获取商品详情(带缓存)
     */
    @GetMapping("/getSeckillGoods")
    public ResponseMessage<SeckillGoodsDTO> getSeckillGoods(Long id, Long version) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoods(id, version));
    }

    /**
     * 获取商品列表
     */
    @GetMapping("/getSeckillGoodsByActivityId")
    public ResponseMessage<List<SeckillGoods>> getSeckillGoodsByActivityId(Long activityId) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsByActivityId(activityId));
    }

    /**
     * 根据活动id和版本获取商品列表(带缓存)
     */
    @GetMapping("/getSeckillGoodsList")
    public ResponseMessage<List<SeckillGoodsDTO>> getSeckillGoodsList(Long activityId, Long version) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsList(activityId, version));
    }

    /**
     * 更新商品状态
     */
    @PostMapping("/updateStatus")
    public ResponseMessage<String> updateStatus(Integer status, Long id) {
        seckillGoodsService.updateStatus(status, id);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

}