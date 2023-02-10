package com.java1234.controller;


import com.java1234.entity.R;
import com.java1234.entity.SysUser;
import com.java1234.service.SysUserService;
import com.java1234.util.JwtUtils;
import com.java1234.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/user/list")
    public R userList(@RequestHeader(required = false) String token){
        log.info("token={}",token);
        if(StringUtil.isNotEmpty(token)){
            Map<String,Object> resutMap=new HashMap<>();
            List<SysUser> userList = sysUserService.list();
            resutMap.put("userList",userList);
            return R.ok(resutMap);
        }else{
            return R.error(401,"没有权限访问");
        }
    }

    @RequestMapping("/login")
    public R login(){
        String token = JwtUtils.genJwtToken("java1234");
        return R.ok().put("token",token);
    }

}
