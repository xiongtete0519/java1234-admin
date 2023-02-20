package com.java1234.common.security;

import com.java1234.common.exception.UserCountLockException;
import com.java1234.entity.SysUser;
import com.java1234.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义UserDetails
 */
@Service
public class MyUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser=sysUserService.getByUsername(username);
        if(sysUser==null){
            throw new UsernameNotFoundException("用户名或者密码错误");
        }else if("1".equals(sysUser.getStatus())){
            throw new UserCountLockException("该用户账号被封禁，具体请联系管理员");
        }
        return new User(sysUser.getUsername(),sysUser.getPassword(),getUserAuthority());
    }

    private List<GrantedAuthority> getUserAuthority() {
        return new ArrayList<>();
    }
}
