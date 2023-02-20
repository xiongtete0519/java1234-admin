package com.java1234.common.security;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.entity.R;
import com.java1234.entity.SysMenu;
import com.java1234.entity.SysRole;
import com.java1234.entity.SysUser;
import com.java1234.service.SysMenuService;
import com.java1234.service.SysRoleService;
import com.java1234.service.SysUserService;
import com.java1234.util.JwtUtils;
import com.java1234.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 登录成功处理器
 */
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=UTF-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();

        String username=authentication.getName();
        String token = JwtUtils.genJwtToken(username);

        SysUser currentUser = sysUserService.getByUsername(username);

        //根据用户id获取所有的角色信息
        List<SysRole> roleList = sysRoleService.list(
                new QueryWrapper<SysRole>()
                        .inSql("id", "select role_id from sys_user_role where user_id=" + currentUser.getId())
        );

        //遍历所有的角色，获取所有菜单权限，而且不重复
        Set<SysMenu> menuSet=new HashSet<>();
        for (SysRole sysRole : roleList) {
            //每个角色的菜单权限
            List<SysMenu> sysMenuList = sysMenuService.list(
                    new QueryWrapper<SysMenu>()
                            .inSql("id", "select menu_id from sys_role_menu where role_id=" + sysRole.getId())

            );
            menuSet.addAll(sysMenuList);
        }
        ArrayList<SysMenu> sysMenuList = new ArrayList<>(menuSet);

        //排序
        sysMenuList.sort(Comparator.comparing(SysMenu::getOrderNum));

        //生成树形菜单
        List<SysMenu> menuList = sysUserService.buildTreeMenu(sysMenuList);



        outputStream.write(JSONUtil.toJsonStr(R.ok("登录成功").put("authorization",token).put("currentUser",currentUser).put("menuList",menuList)).getBytes());
        outputStream.flush();
        outputStream.close();
    }
}
