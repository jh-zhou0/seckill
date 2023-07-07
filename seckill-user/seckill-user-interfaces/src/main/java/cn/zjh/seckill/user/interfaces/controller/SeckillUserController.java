package cn.zjh.seckill.user.interfaces.controller;

import cn.zjh.seckill.common.exception.ErrorCode;
import cn.zjh.seckill.common.model.dto.SeckillUserDTO;
import cn.zjh.seckill.common.response.ResponseMessage;
import cn.zjh.seckill.common.response.ResponseMessageBuilder;
import cn.zjh.seckill.user.application.service.SeckillUserService;
import cn.zjh.seckill.user.domain.model.entity.SeckillUser;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户
 * 
 * @author zjh - kayson
 */
@RestController
@RequestMapping(value = "/user")
public class SeckillUserController {
    
    @Resource
    private SeckillUserService seckillUserService;
    
    @GetMapping("/get")
    public ResponseMessage<SeckillUser> getUser(@RequestAttribute Long userId) {
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillUserService.getSeckillUserByUserId(userId));
    }
    
    @PostMapping("/login")
    public ResponseMessage<String> login(@RequestBody SeckillUserDTO userDTO) {
        String token = seckillUserService.login(userDTO.getUserName(), userDTO.getPassword());
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), token);
    }
    
}