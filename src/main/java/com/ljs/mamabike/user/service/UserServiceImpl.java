package com.ljs.mamabike.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ljs.mamabike.cache.CommonCacheUtil;
import com.ljs.mamabike.common.exception.MaMaBikeException;
import com.ljs.mamabike.security.AESUtil;
import com.ljs.mamabike.security.Base64Util;
import com.ljs.mamabike.security.MD5Util;
import com.ljs.mamabike.security.RSAUtil;
import com.ljs.mamabike.user.dao.UserMapper;
import com.ljs.mamabike.user.entity.User;
import com.ljs.mamabike.user.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author ljs
 * @Description TODO
 * @Date 2018/8/30 18:02
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommonCacheUtil cacheUtil;

    /**
     * Author ljs
     * Description 登录业务
     * Date 2018/9/3 23:01
     **/
    @Override
    public String login(String data, String key) throws MaMaBikeException {
        String decryptData = null;
        String token = null;
        try {

            //RSA解密AES的key
            byte[] aesKey = RSAUtil.decryptByPrivateKey(Base64Util.decode(key));
            //AES的key解密AES加密数据
            decryptData = AESUtil.decrypt(data, new String(aesKey, "utf-8"));
            if (decryptData == null) {
                throw new Exception();
            }
            //解密成功后,使用fastjson转为对象，因为移动端传过来的是json数据
            JSONObject jsonObject = JSON.parseObject(decryptData);
            String mobile = jsonObject.getString("mobile"); //电话
            String code = jsonObject.getString("code"); //验证码
            String platform = jsonObject.getString("platform"); //机器类型
           // String channelId = jsonObject.getString("channelId"); //推送频道编码， 单个设备唯一

            //转换为json对象获取值后进行校验
            if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code) ||
                    StringUtils.isBlank(platform)) {
                throw new Exception();
            }


            //去redis取验证码比较手机号码和验证码是否匹配 若匹配 说明是本人手机
            String verCode = cacheUtil.getCacheValue(mobile);
            User user = null;
            ///用code去匹配verCode，因为code上面已经验证过是不为null，而v可能为null，null.equals空指针异常
            if (code.equals(verCode)) {
                //手机匹配
                user = userMapper.selectByMobile(mobile);
                if(user==null){
                    //用户不存在，帮他注册
                    user = new User();
                    user.setMobile(mobile);
                    user.setNickname(mobile);       //默认是手机号
                    userMapper.insertSelective(user);
                }

            }else {
                throw new MaMaBikeException("验证码或者手机号不匹配");
            }

            //生成token
            try{
                token = this.generateToken(user);

            }catch (Exception e){
                throw new MaMaBikeException("fail.to.generate.token");
            }

            /**存入redis**/
            UserElement ue = new UserElement();
            ue.setMobile(mobile);
            ue.setUserId(user.getId());
            ue.setToken(token);
            ue.setPlatform(platform);
            //ue.setPushChannelId(channelId);
            cacheUtil.putTokenWhenLogin(ue);

        } catch (Exception e) {
            log.error("Fail to decypt data", e);
            //传给移动端
            throw new MaMaBikeException("数据解析错误！");
        }
        return token;
    }

    /**
     * Author ljs
     * Description 修改用户信息业务
     * Date 2018/9/25 16:30
     **/
    @Override
    public void modifyUsername(User user) throws MaMaBikeException{
        userMapper.updateByPrimaryKeySelective(user);
    }

    /**
     * Author ljs
     * Description 生成唯一标识token，并且把token加密
     * Date 2018/9/4 15:04
     **/
    private String generateToken(User user)
            throws Exception {
        String source = user.getId() + ":" + user.getMobile() + System.currentTimeMillis();
        return MD5Util.getMD5(source);
    }
}
