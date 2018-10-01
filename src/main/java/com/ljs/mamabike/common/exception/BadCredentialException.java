package com.ljs.mamabike.common.exception;


import org.springframework.security.core.AuthenticationException;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/10/1 13:17
 **/
public class BadCredentialException extends AuthenticationException {
    public BadCredentialException(String message) {
        super(message);
    }
}
