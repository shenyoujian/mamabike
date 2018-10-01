package com.ljs.mamabike.security;

import com.ljs.mamabike.cache.CommonCacheUtil;
import com.ljs.mamabike.common.constants.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;


/**
 * @Author ljs
 * @Description security配置类
 * @Date 2018/9/28 16:33
 **/

@Configuration              //springboot启动时加载该配置类
@EnableWebSecurity          //启动springsecurity的web安全支持
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //注入Parameter类
    @Autowired
    private Parameters parameters;

    @Autowired
    private CommonCacheUtil redis;

    //给filter里设置manager
    private RestPreAuthenticatedProcessingFilter getPreAuthenticatedProcessingFilter() throws Exception {
        RestPreAuthenticatedProcessingFilter filter = new RestPreAuthenticatedProcessingFilter(parameters.getNoneSecurityPath(), redis);
        filter.setAuthenticationManager(this.authenticationManagerBean());
        return filter;
    }

    //往manager里添加provider
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new RestAuthenticationProvider());
    }

    //web安全配置的细节，如定义哪些url路径应该被保护，哪些不应该。
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭默认打开的crsf保护
        http.csrf().disable()
                //允许含login不需要身份验证
                .authorizeRequests()
                .antMatchers((String[])parameters.getNoneSecurityPath().toArray(new String[parameters.getNoneSecurityPath().size()])).permitAll()//符合条件的路径放过验证
                //其他请求都需要身份验证
                .anyRequest().authenticated()
                .and()
                //创建成无状态的请求，即不创建session，因为我们是和移动端对接的。
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and().addFilter(getPreAuthenticatedProcessingFilter());
    }


    //当我们设置了跨域之后，移动端会先发一个options请求来探测一下
    //确认你允不允许我跨域，都支持跨域哪些方法，所以我们需要不能拦截options方法的请求
    @Override
    public void configure(WebSecurity web) throws Exception {
        //忽略 OPTIONS 方法的请求
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
        //放过swagger
    }

}
