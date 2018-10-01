package com.ljs.mamabike.security;

import com.ljs.mamabike.common.exception.BadCredentialException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * @Author ljs
 * @Description 自定义security的provider
 * @Date 2018/9/29 13:54
 **/
public class RestAuthenticationProvider implements AuthenticationProvider {

    /**
     * Author ljs
     * Description 对符合要求的合法token授权
     * Date 2018/10/1 13:06
     **/
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken preAuth = (PreAuthenticatedAuthenticationToken) authentication;
            RestAuthenticationToken sysAuth = (RestAuthenticationToken) preAuth.getPrincipal();
            //开始判断用户角色
            if (sysAuth.getAuthorities() != null && sysAuth.getAuthorities().size() > 0) {
                GrantedAuthority authority = sysAuth.getAuthorities().iterator().next();
                if ("BIKE_CLIENT".equals(authority.getAuthority())) {
                    return sysAuth;
                } else if ("ROLE_SOMEONE".equals(authority.getAuthority())) {
                    return sysAuth;
                }
            }
        } else if (authentication instanceof RestAuthenticationToken) {
            RestAuthenticationToken sysAuth = (RestAuthenticationToken) authentication;
            if (sysAuth.getAuthorities() != null && sysAuth.getAuthorities().size() > 0) {
                GrantedAuthority gauth = sysAuth.getAuthorities().iterator().next();
                if ("BIKE_CLIENT".equals(gauth.getAuthority())) {
                    return sysAuth;
                } else if ("ROLE_SOMEONE".equals(gauth.getAuthority())) {
                    return sysAuth;
                }
            }
        }

        throw new BadCredentialException("unknown.error");
    }

    /**
     * Author ljs
     * Description 校验filter传递过来的对象，校验成功返回true
     * Date 2018/10/1 13:06
     **/
    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication) || RestAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
