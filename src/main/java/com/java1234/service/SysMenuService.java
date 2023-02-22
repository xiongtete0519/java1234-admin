package com.java1234.service;

import com.java1234.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public interface SysMenuService extends IService<SysMenu> {

    //生成树形菜单
    List<SysMenu> buildTreeMenu(List<SysMenu> sysMenuList);
}
