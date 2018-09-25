package com.ljs.mamabike.common.exception;

import com.ljs.mamabike.common.constants.Constants;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/9/3 22:49
 **/
public class MaMaBikeException extends Exception {

    public MaMaBikeException(String message){
        super(message);
    }

    public int getStatusCode(){
        return Constants.RESP_STATUS_INTERNAL_ERROR;
    }
}
