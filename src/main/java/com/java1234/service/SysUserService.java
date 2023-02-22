package com.java1234.service;

import com.java1234.entity.SysMenu;
import com.java1234.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public interface SysUserService extends IService<SysUser> {

    //根据用户名查询用户
    SysUser getByUsername(String username);

    //获取权限字符串
    String getUserAuthorityInfo(Long userId);

}
