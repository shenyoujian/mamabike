package com.ljs.mamabike.security;

import com.ljs.mamabike.cache.CommonCacheUtil;
import com.ljs.mamabike.common.constants.Constants;
import com.ljs.mamabike.user.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @Author ljs
 * @Description 自定义security的过滤器
 * @Date 2018/9/29 13:40
 **/

@Slf4j
public class RestPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    /**spring路径匹配器**/
    private AntPathMatcher matcher = new AntPathMatcher();

    private List<String> noneSecurityList;

    private CommonCacheUtil redis;

    /**使用构造器注入，不能使用setget不然注不进去
    因为这个filter在spring容器加载的时候就加载了。**/
    public RestPreAuthenticatedProcessingFilter(List<String> noneSecurityList, CommonCacheUtil redis) {
        this.noneSecurityList = noneSecurityList;
        this.redis = redis;
    }

    /**提取用户提交的信息，然后交给provider做校验，校验不通过进入entrypoint做异常处理**/
    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(1);


        /**第一种情况：无需拦截的请求**/
        if (isNoneSecurity(request.getRequestURI().toString()) || "OPTIONS".equals(request.getMethod())) {
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_SOMEONE");
            authorities.add(authority);
            //无需权限的url直接发放token走Provider授权
            return new RestAuthenticationToken(authorities);
        }


        /**第二种情况：需要拦截的请求**/
        String version = request.getHeader(Constants.REQUEST_VERSION_KEY);
        String token = request.getHeader(Constants.REQUEST_TOKEN_KEY);


        if (version == null) {
            request.setAttribute("header-error", 400);
        }

        /**校验token,如果header-error==null说明version是有值的**/
        if (request.getAttribute("header-error") == null) {
            try {
                if (token != null && !token.trim().isEmpty()) {
                    UserElement ue = redis.getUserByToken(token);

                    if (ue instanceof UserElement) {
                        //检查到token说明用户已经登录 授权给用户BIKE_CLIENT角色 允许访问
                        GrantedAuthority authority = new SimpleGrantedAuthority("BIKE_CLIENT");
                        authorities.add(authority);
                        RestAuthenticationToken authToken = new RestAuthenticationToken(authorities);
                        authToken.setUser(ue);
                        return authToken;
                    }
                } else {
                    log.warn("Got no token from request header");
                    //token不存在 告诉移动端 登录
                    request.setAttribute("header-error", 401);
                }
            } catch (Exception e) {
                log.error("Fail to authenticate user", e);
            }

        }

        /**第三种情况：给400和401一个角色，反正不能返回null，一定得返回一个token**/
        if (request.getAttribute("header-error") != null) {
            //请求头有错误  随便给个角色 让逻辑继续
            GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_NONE");
            authorities.add(authority);
        }
        RestAuthenticationToken authToken = new RestAuthenticationToken(authorities);
        return authToken;
    }


    /**
     * Author ljs
     * Description 校验是否无需权限的uri
     * Date 2018/9/30 16:59
     **/
    private boolean isNoneSecurity(String uri) {
        boolean result = false;
        if (this.noneSecurityList != null) {
            for (String pattern : this.noneSecurityList) {
                if (matcher.match(pattern, uri)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }
}
