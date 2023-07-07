package cn.zjh.seckill.activity.interfaces.controller;

import cn.zjh.seckill.activity.application.command.SeckillActivityCommand;
import cn.zjh.seckill.activity.application.service.SeckillActivityService;
import cn.zjh.seckill.activity.domain.model.entity.SeckillActivity;
import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.model.dto.SeckillActivityDTO;
import cn.zjh.seckill.common.response.ResponseMessage;
import cn.zjh.seckill.common.response.ResponseMessageBuilder;
import cn.zjh.seckill.common.utils.date.JodaDateTimeUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 活动
 *
 * @author zjh - kayson
 */
@RestController
@RequestMapping("/activity")
public class SeckillActivityController {

    @Resource
    private SeckillActivityService seckillActivityService;

    /**
     * 保存秒杀活动
     */
    @PostMapping("/saveSeckillActivity")
    public ResponseMessage<String> saveSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        seckillActivityService.saveSeckillActivity(seckillActivityCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 根据状态获取活动列表
     */
    @GetMapping("/getSeckillActivityList")
    public ResponseMessage<List<SeckillActivity>> getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityList(status));
    }

    /**
     * 获取秒杀活动列表(带缓存)
     */
    @GetMapping("/seckillActivityList")
    public ResponseMessage<List<SeckillActivityDTO>> getSeckillActivityList(@RequestParam(value = "status", required = false) Integer status,
                                                                            @RequestParam(value = "version", required = false) Long version) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityList(status, version));
    }

    /**
     * 获取id获取秒杀活动详情
     */
    @GetMapping("/getSeckillActivityById")
    public ResponseMessage<SeckillActivity> getSeckillActivityById(@RequestParam(value = "id", required = false) Long id) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivityById(id));
    }

    /**
     * 根据id获取秒杀活动详情(带缓存)
     */
    @GetMapping("/seckillActivity")
    public ResponseMessage<SeckillActivityDTO> getSeckillActivityById(@RequestParam(value = "id", required = false) Long id,
                                                                      @RequestParam(value = "version", required = false) Long version) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityService.getSeckillActivity(id, version));
    }

    /**
     * 根据时间和状态获取活动列表
     */
    @GetMapping("/getSeckillActivityListBetweenStartTimeAndEndTime")
    public ResponseMessage<List<SeckillActivity>> getSeckillActivityListBetweenStartTimeAndEndTime(@RequestParam(value = "currentTime", required = false) String currentTime,
                                                                                                   @RequestParam(value = "status", required = false) Integer status) {
        List<SeckillActivity> seckillActivityList = seckillActivityService.getSeckillActivityListBetweenStartTimeAndEndTime(JodaDateTimeUtils.parseStringToDate(currentTime, JodaDateTimeUtils.DATE_TIME_FORMAT), status);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillActivityList);
    }

    /**
     * 更新活动的状态
     */
    @PostMapping("/updateStatus")
    public ResponseMessage<String> updateStatus(@RequestParam(value = "status", required = false) Integer status,
                                                @RequestParam(value = "id", required = false) Long id) {
        seckillActivityService.updateStatus(status, id);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

}