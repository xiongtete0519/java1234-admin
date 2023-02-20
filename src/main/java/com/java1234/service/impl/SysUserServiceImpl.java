package com.java1234.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.java1234.entity.SysMenu;
import com.java1234.entity.SysRole;
import com.java1234.entity.SysUser;
import com.java1234.mapper.SysMenuMapper;
import com.java1234.mapper.SysRoleMapper;
import com.java1234.service.SysUserService;
import com.java1234.mapper.SysUserMapper;
import com.java1234.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
implements SysUserService{

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new QueryWrapper<SysUser>().eq("username",username));
    }

    //获取权限字符串
    // 格式ROLE_admin,ROLE_common,system:user:resetPwd,system:role:delete,system:user:list,system:menu:query,system:menu:list,system:menu:add,system:user:delete,system:role:list,system:role:menu,system:user:edit,system:user:query,system:role:edit,system:user:add,system:user:role,system:menu:delete,system:role:add,system:role:query,system:menu:edit

    @Override
    public String getUserAuthorityInfo(Long userId) {
        StringBuffer authority=new StringBuffer();
        //根据用户id获取所有的角色信息
        List<SysRole> roleList = sysRoleMapper.selectList(
                new QueryWrapper<SysRole>()
                        .inSql("id", "select role_id from sys_user_role where user_id=" + userId)
        );
        if(roleList.size()>0){
            // 格式ROLE_admin,ROLE_common
            String roleCodeStrs = roleList.stream().map(r -> "ROLE_" + r.getCode()).collect(Collectors.joining(","));
            authority.append(roleCodeStrs);
        }
        //遍历所有的角色，获取所有菜单权限，而且不重复
        Set<String> menuCodeSet=new HashSet<>();
        for (SysRole sysRole : roleList) {
            //每个角色的菜单权限
            List<SysMenu> sysMenuList = sysMenuMapper.selectList(
                    new QueryWrapper<SysMenu>()
                            .inSql("id", "select menu_id from sys_role_menu where role_id=" + sysRole.getId())

            );
            //取出菜单权限编码
            for (SysMenu sysMenu : sysMenuList) {
                String perms = sysMenu.getPerms();
                if(StringUtil.isNotEmpty(perms)){
                    menuCodeSet.add(perms);
                }
            }
        }
        //将权限编码设置到authority里面
        if(menuCodeSet.size()>0){
            authority.append(",");
            String menuCodeStrs = menuCodeSet.stream().collect(Collectors.joining(","));
            authority.append(menuCodeStrs);
        }
        System.out.println("authority="+authority.toString());
        return authority.toString();
    }

    //生成树形菜单
    @Override
    public List<SysMenu> buildTreeMenu(ArrayList<SysMenu> sysMenuList) {
        List<SysMenu> resultMenuList=new ArrayList<>();

        for (SysMenu sysMenu : sysMenuList) {
            for (SysMenu e : sysMenuList) {
                //寻找子节点
                if(e.getParentId().equals(sysMenu.getId())){
                    sysMenu.getChildren().add(e);
                }
            }
            if(sysMenu.getParentId()==0L){
                resultMenuList.add(sysMenu);
            }
        }

        return resultMenuList;
    }
}




