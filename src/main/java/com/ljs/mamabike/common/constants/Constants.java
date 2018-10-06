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


    /**秒滴SMS start**/
    public static final String MDSMS_ACCOUNT_SID = "337f2665cf2444458ccef8afcb2a0209";

    public static final String MDSMS_AUTH_TOKEN = "7e4ea0e723ab4e28815612d4dcbb9c04";

    public static final String MDSMS_REST_URL = "https://api.miaodiyun.com/20150822";

    public static final String MDSMS_VERCODE_TPLID = "708849297";

    /**秒滴SMS end**/


    /**七牛key start**/
    public static final String QINIU_ACCESS_KEY = "Okx7YbP2l1ZzE5PpJx_eeetkfOI47QYSwNMg6yNp";

    public static final String QINIU_SECRET_KEY = "sZezKipd3y3Caexa57lnOVZZXRpYSyG9pamPlgza";

    public static final String QINIU_HEAD_IMG_BUCKET_NAME="mamabike";

    public static final String QINIU_HEAD_IMG_BUCKET_URL="pg1y0keec.bkt.clouddn.com";
    /**七牛key end**/

}
