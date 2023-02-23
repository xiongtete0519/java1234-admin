package com.java1234.common.security;


import com.java1234.common.constant.Constant;
import com.java1234.common.exception.CaptchaException;
import com.java1234.util.RedisUtil;
import com.java1234.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码过滤器
 * 生成验证码的地址 把验证码存redis，同时设置key也是就是uuid，用来判断是哪个用户请求，模拟session；
 *
 * 用户前端 用户输入验证码 提交 验证码，带上uuid 后端去redis判断。
 */
@Component
public class CaptchaFilter extends OncePerRequestFilter {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String url = httpServletRequest.getRequestURI();

        if ("/login".equals(url) && httpServletRequest.getMethod().equals("POST")) {
            try{
                // 校验验证码
                validate(httpServletRequest);
            } catch (CaptchaException e) {
                // 交给认证失败处理器
                loginFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    // 校验验证码逻辑
    private void validate(HttpServletRequest httpServletRequest) {

        String code = httpServletRequest.getParameter("code");
        String key = httpServletRequest.getParameter("uuid");

        if (StringUtil.isEmpty(code) || StringUtil.isEmpty(key)) {
            throw new CaptchaException("验证码错误");
        }

        if (!code.equals(redisUtil.hget(Constant.CAPTCHA_KEY, key))) {
            throw new CaptchaException("验证码错误");
        }

    }
}
