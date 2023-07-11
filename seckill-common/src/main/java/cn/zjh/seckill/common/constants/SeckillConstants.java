package cn.zjh.seckill.common.constants;

/**
 * 秒杀常量类
 * 
 * @author zjh - kayson
 */
public class SeckillConstants {

    /**
     * tcc try
     */
    public static final String ORDER_TRY_KEY_PREFIX = "order:try:";

    /**
     * tcc confirm
     */
    public static final String ORDER_CONFIRM_KEY_PREFIX = "order:confirm:";

    /**
     * tcc cancel
     */
    public static final String ORDER_CANCEL_KEY_PREFIX = "order:cancel:";

    /**
     * 订单
     */
    public static final String ORDER_KEY = "order";

    /**
     * 商品
     */
    public static final String GOODS_KEY = "goods";

    /**
     * LUA脚本商品库存不存在
     */
    public static final int LUA_RESULT_GOODS_STOCK_NOT_EXISTS = -1;

    /**
     * LUA脚本要扣减的商品数量小于等于0
     */
    public static final int LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO = -2;

    /**
     * LUA脚本库存不足
     */
    public static final int LUA_RESULT_GOODS_STOCK_LT_ZERO = -3;

    /**
     * 商品key前缀
     */
    public static final String GOODS_ITEM_KEY_PREFIX = "item:";

    /**
     * 订单Key前缀
     */
    public static final String ORDER_KEY_PREFIX = "order:";

    /**
     * 订单锁
     */
    public static final String ORDER_LOCK_KEY_PREFIX = "order:lock:";

    /**
     * 商品库存的Key
     */
    public static final String GOODS_ITEM_STOCK_KEY_PREFIX = "item:stock:";

    /**
     * 商品限购数量Key
     */
    public static final String GOODS_ITEM_LIMIT_KEY_PREFIX = "item:limit:";

    /**
     * 商品上架标识
     */
    public static final String GOODS_ITEM_ONLINE_KEY_PREFIX = "item:onffline:";

    /**
     * 用户缓存前缀
     */
    public static final String USER_KEY_PREFIX = "user:";

    /**
     * 获取Key
     */
    public static String getKey(String prefix, String key){
        return prefix.concat(key);
    }

    /**
     * token的载荷中存放的信息 只存放一个userId
     */
    public static final String TOKEN_CLAIM = "userId";

    /**
     * jwtToken过期时间 默认为7天
     */
    public static final Long TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * token请求头名称
     */
    public static final String TOKEN_HEADER_NAME = "access-token";

    /**
     * JWT的密钥
     */
    public static final String JWT_SECRET = "a814edb0e7c1ba4c";

    /*****************缓存相关的配置****************/
    public static final Long FIVE_MINUTES = 5 * 60L;
    public static final Long FIVE_SECONDS = 5L;
    public static final Long HOURS_24 = 3600 * 24L;

    public static final String SECKILL_ACTIVITY_CACHE_KEY = "SECKILL_ACTIVITY_CACHE_KEY";
    public static final String SECKILL_ACTIVITIES_CACHE_KEY = "SECKILL_ACTIVITIES_CACHE_KEY";

    public static final String SECKILL_GOODS_CACHE_KEY = "SECKILL_GOODS_CACHE_KEY";
    public static final String SECKILL_GOODSES_CACHE_KEY = "SECKILL_GOODSES_CACHE_KEY";

}
