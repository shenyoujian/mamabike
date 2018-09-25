package com.ljs.mamabike.common.resp;

import com.ljs.mamabike.common.constants.Constants;
import lombok.Data;

/**
 * @Author ljs
 * @Description 返回请求数据的工具类
 * @Date 2018/9/3 21:57
 **/
@Data
public class ApiResult<T> {

    private int code = Constants.RESP_STATUS_OK;
    private String message;
    private T data;
}
