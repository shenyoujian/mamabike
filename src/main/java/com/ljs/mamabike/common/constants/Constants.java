package com.ljs.mamabike.common.constants;

/**
 * @Author ljs
 * @Description 状态码常量类
 * @Date 2018/9/3 22:02
 **/
public class Constants {
    /**自定义状态码 start**/
    public static final int RESP_STATUS_OK = 200;

    public static final int RESP_STATUS_NOAUTH = 401;

    public static final int RESP_STATUS_INTERNAL_ERROR = 500;

    public static final int RESP_STATUS_BADREQUEST = 400;

    /**状态码 end**/

    /**用户token**/
    public static final String REQUEST_TOKEN_KEY = "user-token";

    /**App版本,如果版本过低不给访问**/
    public static final String REQUEST_VERSION_KEY = "version";
}
