package cn.zjh.seckill.infrastructure.shiro.utils;

import org.apache.shiro.crypto.hash.SimpleHash;

/**
 * @author zjh - kayson
 */
public class CommonUtils {

    /**
     * 手机号正则校验
     *
     * @param phone 手机号
     * @return 校验是否成功
     */
    public static boolean phoneRegexCheck(String phone) {
        return phone.length() == 11;
    }

    /**
     * 获取六位数验证码
     *
     * @return 验证码
     */
    public static int getCode() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    /**
     * 使用md5加密
     *
     * @param password 需要加密的密码
     * @param salt     盐值
     * @return 返回加密后的密码
     */
    public static String encryptPassword(String password, String salt) {
        return String.valueOf(new SimpleHash("MD5", password, salt, 1024));
    }

//    public static void main(String[] args) {
//        System.out.println(encryptPassword("kayson", "kayson"));
//    }

}
