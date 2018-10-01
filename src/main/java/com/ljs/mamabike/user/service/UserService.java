package com.ljs.mamabike.user.service;

import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.ljs.mamabike.user.entity.User;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/8/30 18:02
 **/
public interface UserService {

    String login(String data, String key) throws MaMaBikeException;

    void modifyNickName(User user) throws MaMaBikeException;
}
