package com.ljs.mamabike.user.controller;

import com.ljs.mamabike.user.dao.UserMapper;
import com.ljs.mamabike.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/8/21 11:19
 **/

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserMapper userMapper;


    @RequestMapping("/hello")
    public User hello(){
        User user = userMapper.selectByPrimaryKey(1L);
        return user;
    }
}
