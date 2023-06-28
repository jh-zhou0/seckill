package cn.zjh.seckill.domain.enums;

/**
 * 用户状态
 * 
 * @author zjh - kayson
 */
public enum SeckillUserStatus {

    NORMAL(1),
    FREEZE(2);

    private final Integer code;

    SeckillUserStatus(Integer code) {
        this.code = code;
    }

    public static boolean isNormal(Integer status) {
        return NORMAL.getCode().equals(status);
    }

    public Integer getCode() {
        return code;
    }

}
