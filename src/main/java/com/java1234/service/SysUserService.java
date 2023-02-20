package com.java1234.service;

import com.java1234.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SysUserService extends IService<SysUser> {

    //根据用户名查询用户
    SysUser getByUsername(String username);
}
