package com.ljs.mamabike.user.entity;

import lombok.Data;

/**
 * @Author ljs
 * @Description 封装接收的登录信息
 * @Date 2018/9/3 22:26
 **/
@Data
public class LoginInfo {

    /**登录信息密文**/
    private String data;

    /**RSA加密的AES的密钥**/
    private String key;
}
