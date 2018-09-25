package com.ljs.mamabike.common.rest;

import com.ljs.mamabike.cache.CommonCacheUtil;
import com.ljs.mamabike.common.constants.Constants;
import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.ljs.mamabike.user.entity.UserElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/9/25 20:08
 **/
public class BaseController {

    @Autowired
    private CommonCacheUtil redis;

    /**
     * Author ljs
     * Description 根据token获取user
     * Date 2018/9/25 20:10
     **/
    protected UserElement getCurrenUser(){
        //1、使用springmvc提供的类去获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        //2、从header里获取token
        String token = request.getHeader(Constants.REQUEST_TOKEN_KEY);
        if (!StringUtils.isBlank(token)) {
            //3、不为空，根据token去redis获取用户
            try{
                UserElement ue = redis.getUserByToken(token);
                return ue;
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }
}
