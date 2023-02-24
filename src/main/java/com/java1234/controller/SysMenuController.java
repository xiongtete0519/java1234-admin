package com.java1234.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.java1234.entity.R;
import com.java1234.entity.SysMenu;
import com.java1234.service.SysMenuService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Api(tags = "菜单管理")
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    //查询所有菜单树信息
    @RequestMapping("/treeList")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public R list(){
        List<SysMenu> menuList = sysMenuService.list(new QueryWrapper<SysMenu>().orderByAsc("order_num"));
        return R.ok().put("treeMenu",sysMenuService.buildTreeMenu(menuList));
    }

    //添加或者修改
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('system:menu:add','system:menu:edit')")
    public R save(@RequestBody SysMenu sysMenu){
        if(sysMenu.getId()==null || sysMenu.getId()==-1){   //添加
            sysMenuService.save(sysMenu);
        }else{  //修改
            sysMenuService.updateById(sysMenu);
        }
        return R.ok();
    }

    //根据id查询
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:menu:query')")
    public R findById(@PathVariable(value = "id")Long id){
        SysMenu sysMenu = sysMenuService.getById(id);
        Map<String,Object> map=new HashMap<>();
        map.put("sysMenu",sysMenu);
        return R.ok(map);
    }

    //删除菜单
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('system:menu:delete')")
    public R delete(@PathVariable(value = "id")Long id){
        //是否有父节点
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if(count>0){
            return R.error("请先删除子菜单！");
        }
        sysMenuService.removeById(id);
        return R.ok();
    }
}