package com.ljs.mamabike.user.service;

import com.ljs.mamabike.user.dao.UserMapper;
import com.ljs.mamabike.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/8/30 18:02
 **/
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public String login() {
        User user = new User();
        user.setId(1L);
        userMapper.insertSelective(user);
        return null;
    }
}
