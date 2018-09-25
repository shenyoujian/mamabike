package com.ljs.mamabike.user.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Author ljs
 * Description 用于缓存的user信息体
 * Date 2018/9/4 15:09
 **/
@Data
public class UserElement {

    private long userId;

    private String mobile;

    private String token;

    private String platform;  //ios或者andriod

    private String pushUserId;  //单设备推送标识

    private String pushChannelId;   //所以设备推送标识


    /**
     * 转 map
     * @return
     */
    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("platform", this.platform);
        map.put("userId", this.userId + "");
        map.put("token", token);
        map.put("mobile", mobile);
        if (this.pushUserId != null) {
            map.put("pushUserId", this.pushUserId);
        }
        if (this.pushChannelId != null) {
            map.put("pushChannelId", this.pushChannelId);
        }
        return map;
    }

    /**
     * map转对象
     * @param map
     * @return
     */
    public static UserElement fromMap(Map<String, String> map) {
        UserElement ue = new UserElement();
        ue.setPlatform(map.get("platform"));
        ue.setToken(map.get("token"));
        ue.setMobile(map.get("mobile"));
        ue.setUserId(Long.parseLong(map.get("userId")));
        ue.setPushUserId(map.get("pushUserId"));
        ue.setPushChannelId(map.get("pushChannelId"));
        return ue;
    }

}
