package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.SysUser;
import com.java1234.service.SysUserService;
import com.java1234.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
implements SysUserService{

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new QueryWrapper<SysUser>().eq("username",username));
    }
}




