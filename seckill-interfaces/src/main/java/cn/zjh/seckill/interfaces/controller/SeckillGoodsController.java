package cn.zjh.seckill.interfaces.controller;

import cn.zjh.seckill.application.command.SeckillGoodsCommand;
import cn.zjh.seckill.application.service.SeckillGoodsService;
import cn.zjh.seckill.domain.code.ErrorCode;
import cn.zjh.seckill.domain.dto.SeckillGoodsDTO;
import cn.zjh.seckill.domain.model.SeckillGoods;
import cn.zjh.seckill.domain.response.ResponseMessage;
import cn.zjh.seckill.domain.response.ResponseMessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    @RequestMapping(value = "/saveSeckillGoods", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<String> saveSeckillActivity(SeckillGoodsCommand seckillGoodsCommand) {
        seckillGoodsService.saveSeckillGoods(seckillGoodsCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/getSeckillGoodsById", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<SeckillGoods> getSeckillGoodsId(Long id) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsById(id));
    }

    /**
     * 根据商品id和版本获取商品详情
     */
    @RequestMapping(value = "/getSeckillGoods", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<SeckillGoodsDTO> getSeckillGoods(Long id, Long version) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoods(id, version));
    }

    /**
     * 获取商品列表
     */
    @RequestMapping(value = "/getSeckillGoodsByActivityId", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<List<SeckillGoods>> getSeckillGoodsByActivityId(Long activityId) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsByActivityId(activityId));
    }

    /**
     * 根据活动id和版本获取商品列表
     */
    @RequestMapping(value = "/getSeckillGoodsList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<List<SeckillGoodsDTO>> getSeckillGoodsList(Long activityId, Long version) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsList(activityId, version));
    }

    /**
     * 更新商品状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<String> updateStatus(Integer status, Long id) {
        seckillGoodsService.updateStatus(status, id);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

}
