package com.java1234.config;

import com.java1234.common.security.LoginFailureHandler;
import com.java1234.common.security.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Spring Security配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    private static final String URL_WHITELIST[] ={
            "/login",
            "/logout",
            "/captcha",
            "/password",
            "/image/**",
            "/test/**"
    };
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //开启跨域 以及csrf攻击关闭
        http
                .cors()
                .and()
                .csrf()
                .disable()
        //登录登出配置
                .formLogin()
                .successHandler(loginSuccessHandler)    //登录成功处理器
                .failureHandler(loginFailureHandler)    //登录失败处理器
//                .and()
//                .logout().logoutSuccessHandler()
        //session禁用配置
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)//无状态
        //拦截规则配置
                .and()
                .authorizeRequests()
                .antMatchers(URL_WHITELIST).permitAll() //放行白名单
                .anyRequest().authenticated();           //要认证的请求
        //异常处理配置

        //自定义过滤器配置

    }
}
