package com.ljs.mamabike.cache;

import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.ljs.mamabike.user.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Map;

/**
 * @Author ljs
 * @Description redis操作
 * @Date 2018/9/4 14:43
 **/

@Component
@Slf4j
public class CommonCacheUtil {
    private static final String TOKEN_PREFIX = "token.";

    private static final String USER_PREFIX = "user.";


    @Autowired
    private JedisPoolWrapper jedisPoolWrapper;

    /**
     * 缓存 可以value 永久
     *
     * @param key
     * @param value
     */
    public void cache(String key, String value) {
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis Jedis = pool.getResource()) {
                    Jedis.select(0);        //选择redis第0片区
                    Jedis.set(key, value);
                }
            }
        } catch (Exception e) {
            log.error("Fail to cache value", e);
        }
    }

    /**
     * 获取缓存key
     *
     * @param key
     * @return
     */
    public String getCacheValue(String key) {
        String value = null;
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis Jedis = pool.getResource()) {
                    Jedis.select(0);
                    value = Jedis.get(key);
                }
            }
        } catch (Exception e) {
            log.error("Fail to get cached value", e);
        }
        return value;
    }

    /**
     * 设置key value 以及过期时间
     *
     * @param key
     * @param value
     * @param expiry
     * @return
     */
    public long cacheNxExpire(String key, String value, int expiry) {
        long result = 0;
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.select(0);
                    result = jedis.setnx(key, value);
                    jedis.expire(key, expiry);
                }
            }
        } catch (Exception e) {
            log.error("Fail to cacheNx value", e);
        }

        return result;
    }

    /**
     * 删除缓存key
     *
     * @param key
     */
    public void delKey(String key) {
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {

            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                try {
                    jedis.del(key);
                } catch (Exception e) {
                    log.error("Fail to remove key from redis", e);
                }
            }
        }
    }


    /**
     * 登录时设置token
     *
     * @param ue
     */
    public void putTokenWhenLogin(UserElement ue) {
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {

            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                Transaction trans = jedis.multi();
                try {
                    //重新设置token
                    trans.del(TOKEN_PREFIX + ue.getToken());
                    //token为key，用户信息转为map之后为value
                    trans.hmset(TOKEN_PREFIX + ue.getToken(), ue.toMap());
                    //设置超时时间3天
                    trans.expire(TOKEN_PREFIX + ue.getToken(), 2592000);
                    //sadd将多个token存入一个集合key中
                    //因为该用户可能在多个设备登录有多个token，我们需要提醒一下
                    trans.sadd(USER_PREFIX + ue.getUserId(), ue.getToken());
                    trans.exec();
                } catch (Exception e) {
                    trans.discard();
                    log.error("Fail to cache token to redis", e);
                }
            }
        }
    }

    /**
     * Author ljs
     * Description 根据token获取缓存的用户
     * Date 2018/9/25 21:23
     **/
    public UserElement getUserByToken(String token) throws MaMaBikeException {
        UserElement ue = null;
        JedisPool pool = jedisPoolWrapper.getJedisPool();
        if (pool != null) {
            //1.7支持try()括号里的内容在try之后自动关闭流或者资源,不用自动关闭
            try (Jedis jedis = pool.getResource()) {
                jedis.select(0);
                //根据key从redis获取Map
                try {
                    Map<String, String> map = jedis.hgetAll(TOKEN_PREFIX + token);
                    if (!CollectionUtils.isEmpty(map)) {
                        //把map转对象
                        ue = UserElement.fromMap(map);
                    } else {
                        log.warn("Fail to find cached element for token {}", token);
                    }

                } catch (Exception e) {
                    log.error("Fail to get token from redis", e);
                    throw new MaMaBikeException("Fail to get token content");
                }
            }
        }
        return ue;
    }

    /**
     * Author ljs
     * Description 缓存手机验证码专用 限制了发送次数
     *
     * @return 1 当前验证码未过期   2  手机号超过当前验证码次数上限  3、ip超过当日验证码上线
     * Date 2018/10/2 15:57
     **/
    public int cacheForVerificationCode(String key, String verCode, String type, int second, String ip) throws MaMaBikeException {

        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.select(0);
                    /**ip次数判断**/
                    String ipKey = "ip." + ip;
                    if (ip == null) {
                        return 3;
                    } else {
                        String ipSendCount = jedis.get(ipKey);
                        try {
                            if (ipSendCount != null && Integer.parseInt(ipSendCount) >= 10) {
                                return 3;
                            }
                        } catch (NumberFormatException e) {
                            log.error("Fail to process ip send count", e);
                            return 3;
                        }
                    }

                    /**验证码存储,被设置了返回0**/
                    long succ = jedis.setnx(key, verCode);
                    if (succ == 0) {
                        return 1;
                    }


                    /**手机号次数判断**/
                    String phoneCountKey = key + "." + type;
                    String sendCount = jedis.get(phoneCountKey);
                    try {
                        if (sendCount != null && Integer.parseInt(sendCount) >= 10) {
                            jedis.del(phoneCountKey);
                            return 2;
                        }
                    } catch (NumberFormatException e) {
                        log.error("Fail to process send count", e);
                        jedis.del(key);
                        return 2;
                    }


                    /**都没问题之后给三个key设置过期时间并且value+1**/
                    try {
                        jedis.expire(key, second);
                        long val = jedis.incr(ipKey);
                        /**该ip是第一次存储**/
                        if (val == 1) {
                            jedis.expire(ipKey, 86400);
                        }

                        val = jedis.incr(phoneCountKey);
                        if (val == 1) {
                            jedis.expire(phoneCountKey, 86400);
                        }
                    }catch (Exception e){
                        log.error("Fail to cache data into redis", e);
                    }

                }
            }
        } catch (Exception e) {
            log.error("Fail to cache for expiry", e);
            throw new MaMaBikeException("Fail to cache for expiry");
        }

        return 0;
    }
}
