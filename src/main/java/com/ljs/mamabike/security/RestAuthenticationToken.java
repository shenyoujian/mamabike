package com.ljs.mamabike.security;

import com.ljs.mamabike.user.entity.UserElement;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @Author ljs
 * @Description 自定义的token,需要传递给provider
 * @Date 2018/9/29 13:52
 **/

@Data
public class RestAuthenticationToken extends AbstractAuthenticationToken {


    @Autowired
    private UserElement user;

    public RestAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
