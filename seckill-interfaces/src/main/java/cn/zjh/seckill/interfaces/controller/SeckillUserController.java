package cn.zjh.seckill.interfaces.controller;

import cn.zjh.seckill.application.service.SeckillUserService;
import cn.zjh.seckill.domain.code.HttpCode;
import cn.zjh.seckill.domain.dto.SeckillUserDTO;
import cn.zjh.seckill.domain.model.SeckillUser;
import cn.zjh.seckill.domain.response.ResponseMessage;
import cn.zjh.seckill.domain.response.ResponseMessageBuilder;
import com.alibaba.druid.wall.violation.ErrorCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 用户
 * 
 * @author zjh - kayson
 */
@RestController
@RequestMapping(value = "/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", originPatterns = "*")
public class SeckillUserController {
    
    @Resource
    private SeckillUserService seckillUserService;
    
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<SeckillUser> getUser(@RequestParam(value = "username") String username) {
        SeckillUser user = seckillUserService.getSeckillUserByUserName(username);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), user);
    }
    
    @PostMapping("/login")
    public ResponseMessage<String> login(@RequestBody SeckillUserDTO userDTO) {
        String token = seckillUserService.login(userDTO.getUserName(), userDTO.getPassword());
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), token);
    }
    
}
