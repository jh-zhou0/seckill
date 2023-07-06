package cn.zjh.seckill.infrastructure.handler.exception;

import cn.zjh.seckill.domain.code.ErrorCode;
import cn.zjh.seckill.domain.exception.SeckillException;
import cn.zjh.seckill.domain.response.ResponseMessage;
import cn.zjh.seckill.domain.response.ResponseMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局统一异常处理器
 * 
 * @author zjh - kayson
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(SeckillException.class)
    public ResponseMessage<String> handleSeckillException(SeckillException e) {
        logger.error("[SeckillException]服务器抛出了异常：{}", e.getMessage());
        return ResponseMessageBuilder.build(e.getCode(), e.getMessage());
    }
    
    /**
     * 全局异常处理，统一返回状态码
     */
    @ExceptionHandler(Exception.class)
    public ResponseMessage<String> handleException(Exception e) {
        logger.error("[Exception]服务器抛出了异常：{}", e.getMessage());
        return ResponseMessageBuilder.build(ErrorCode.SERVER_EXCEPTION.getCode(), e.getMessage());
    }
    
}
