package com.java1234.controller;

import com.java1234.entity.R;
import com.java1234.entity.SysUser;
import com.java1234.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('system:user:edit','system:user:add')")
    public R save(@RequestBody SysUser sysUser){
        if(sysUser.getId()==null||sysUser.getId()==-1){

        }else{
            sysUser.setUpdateTime(new Date());
            sysUserService.updateById(sysUser);
        }
        return R.ok();
    }
}
