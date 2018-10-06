package com.ljs.mamabike.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * Author ljs
 * Description 生成随机验证码
 * Date 2018/10/2 3:00
 **/
public class RandomNumberCode {

    public static String verCode(){
        Random random =new Random();
        /**每次生成太长我们去第2到第6位**/
        return StringUtils.substring(String.valueOf(random.nextInt()*-10), 2, 6);
    }
}
