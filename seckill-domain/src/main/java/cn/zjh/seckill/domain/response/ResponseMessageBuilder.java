package cn.zjh.seckill.domain.response;

/**
 * 响应数据的构造类
 * 
 * @author zjh - kayson
 */
public class ResponseMessageBuilder {

    public static <T> ResponseMessage<T> build(Integer code, T body){
        return new ResponseMessage<T>(code, body);
    }

    public static <T> ResponseMessage<T> build(Integer code){
        return new ResponseMessage<T>(code);
    }

}
