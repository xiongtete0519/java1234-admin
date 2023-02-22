package com.java1234.controller;

import com.java1234.entity.R;
import com.java1234.entity.SysUser;
import com.java1234.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //更新个人信息
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('system:user:edit','system:user:add')")
    public R save(@RequestBody SysUser sysUser){
        if(sysUser.getId()==null||sysUser.getId()==-1){

        }else{
            sysUserService.updateById(sysUser);
        }
        return R.ok();
    }

    //修改密码
    @PostMapping("updateUserPwd")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public R updateUserPwd(@RequestBody SysUser sysUser){
        SysUser currentUser = sysUserService.getById(sysUser.getId());
        if(bCryptPasswordEncoder.matches(sysUser.getOldPassword(),currentUser.getPassword())){
            currentUser.setPassword(bCryptPasswordEncoder.encode(sysUser.getNewPassword()));
            sysUserService.updateById(currentUser);
            return R.ok();
        }else{
            return R.error("输入旧密码错误!");
        }

    }

}
