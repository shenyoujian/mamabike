package com.ljs.mamabike.user.controller;

import com.ljs.mamabike.common.constants.Constants;
import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.ljs.mamabike.common.resp.ApiResult;
import com.ljs.mamabike.common.rest.BaseController;
import com.ljs.mamabike.user.dao.UserMapper;
import com.ljs.mamabike.user.entity.LoginInfo;
import com.ljs.mamabike.user.entity.User;
import com.ljs.mamabike.user.entity.UserElement;
import com.ljs.mamabike.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/8/21 11:19
 **/

@RestController
@RequestMapping("user")
@Slf4j
public class UserController extends BaseController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Qualifier("userServiceImpl")
    private UserService userService;


    /**
     * Author ljs
     * Description 用户注册接口
     * Date 2018/9/3 22:17
     **/
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResult<String> login(@RequestBody LoginInfo loginInfo) {

        ApiResult<String> resp = new ApiResult();

        try {

            //进行校验
            String data = loginInfo.getData();
            String key = loginInfo.getKey();
            if (StringUtils.isBlank(data) || StringUtils.isBlank(key)) {
                throw new MaMaBikeException("校验失败!");
            }
            // 登录成功，返回token
            String token = userService.login(data, key);
            resp.setData(token);

        } catch (MaMaBikeException e) {
            //校验失败
            log.error(e.getMessage());
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            // 登录失败，返回失败信息,就不用返回data
            // 记录日志
            log.error("Fail to login", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误！");
        }
        return resp;
    }

    /**
     * Author ljs
     * Description 修改用户名字的接口
     * Date 2018/9/25 21:41
     **/
    @RequestMapping("modifyNickName")
    public ApiResult modifyNickname(@RequestBody User user){
        ApiResult resp = new ApiResult();
        try {
            //根据token获取用户id
            UserElement ue = getCurrenUser();
            user.setId(ue.getUserId());
            userService.modifyNickName(user);
        } catch (MaMaBikeException e) {
            //校验失败
            log.error(e.getMessage());
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage(e.getMessage());
        } catch (Exception e) {
            // 登录失败，返回失败信息,就不用返回data
            // 记录日志
            log.error("Fail to modifyUsername", e);
            resp.setCode(Constants.RESP_STATUS_INTERNAL_ERROR);
            resp.setMessage("内部错误！");
        }
        return resp;
    }
}
