package cn.zjh.seckill.domain.exception;

import cn.zjh.seckill.domain.code.ErrorCode;

/**
 * 自定义异常 
 * 
 * @author zjh - kayson
 */
public class SeckillException extends RuntimeException {

    private Integer code;

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(ErrorCode errorCode){
        this(errorCode.getCode(), errorCode.getMessage());
    }

    public SeckillException(Integer code, String message){
        super(message);
        this.code = code;
    }
    
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
    
}
