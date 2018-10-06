package com.ljs.mamabike.sms;

/**
 * Author ljs
 * Description 短信发送接口
 * 防止以后使用其他短信服务，弄个接口
 * Date 2018/10/3 10:35
 **/
public interface SmsSender {

     void sendSms(String phone, String tplId, String params);
}
