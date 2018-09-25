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
                    }else{
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
}
