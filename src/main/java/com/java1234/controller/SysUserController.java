package com.java1234.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.common.constant.Constant;
import com.java1234.entity.*;
import com.java1234.service.SysRoleService;
import com.java1234.service.SysUserRoleService;
import com.java1234.service.SysUserService;
import com.java1234.util.DateUtil;
import com.java1234.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.util.*;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserRoleService sysUserRoleService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${avatarImagesFilePath}")
    private String avatarImagesFilePath;

    @ApiOperation("更新个人信息")
    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('system:user:edit','system:user:add')")
    public R save(@RequestBody @Valid SysUser sysUser){
        if(sysUser.getId()==null||sysUser.getId()==-1){
            sysUser.setPassword(bCryptPasswordEncoder.encode(sysUser.getPassword()));
            sysUserService.save(sysUser);
        }else{
            sysUserService.updateById(sysUser);
        }
        return R.ok();
    }

    @ApiOperation("修改密码")
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

    @ApiOperation("上传用户头像图片")
    @RequestMapping("/uploadImage")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Map<String,Object> uploadImage(MultipartFile file)throws Exception{
        Map<String,Object> resultMap=new HashMap<>();
        if(!file.isEmpty()){
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            String suffixName=originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFileName= DateUtil.getCurrentDateStr()+suffixName;
            FileUtils.copyInputStreamToFile(file.getInputStream(),new File(avatarImagesFilePath+newFileName));
            resultMap.put("code",0);
            resultMap.put("msg","上传成功");
            Map<String,Object> dataMap=new HashMap<>();
            dataMap.put("title",newFileName);
            dataMap.put("src","image/userAvatar/"+newFileName);
            resultMap.put("data",dataMap);
        }
        return resultMap;
    }

    @ApiOperation("修改用户头像")
    @RequestMapping("/updateAvatar")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public R updateAvatar(@RequestBody SysUser sysUser){
        SysUser currentUser = sysUserService.getById(sysUser.getId());
        currentUser.setAvatar(sysUser.getAvatar());
        sysUserService.updateById(currentUser);
        return R.ok();
    }

    @ApiOperation("条件分页查询用户信息")
    @PostMapping("/list")
    @PreAuthorize("hasAuthority('system:user:query')")
    public R list(@RequestBody PageBean pageBean){
        String query = pageBean.getQuery().trim();


        //传入当前页和每页记录数
        Page<SysUser> pageModel = new Page<>(pageBean.getPageNum(), pageBean.getPageSize());
        Page<SysUser> pageResult = sysUserService.page(
                pageModel,
                new QueryWrapper<SysUser>().like(StringUtil.isNotEmpty(query),"username",query)
        );
        List<SysUser> userList = pageResult.getRecords();
        //给每个用户设置角色
        for (SysUser sysUser : userList) {
            List<SysRole> roleList = sysRoleService.list(new QueryWrapper<SysRole>().inSql("id", "select role_id from sys_user_role where user_id=" + sysUser.getId()));
            sysUser.setSysRoleList(roleList);
        }
        long total = pageResult.getTotal();
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("userList",userList);
        resultMap.put("total",total);
        return R.ok(resultMap);
    }

    @ApiOperation("根据id查询")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public R findById(@PathVariable(value = "id")Integer id){
        SysUser sysUser = sysUserService.getById(id);
        Map<String,Object> map=new HashMap<>();
        map.put("sysUser",sysUser);
        return R.ok(map);
    }

    @ApiOperation("验证用户名")
    @PostMapping("/checkUserName")
    @PreAuthorize("hasAuthority('system:user:query')")
    public R checkUserName(@RequestBody SysUser sysUser){
        if(sysUserService.getByUsername(sysUser.getUsername())==null){
            return R.ok();
        }else{
            return R.error();
        }
    }

    @ApiOperation("删除")
    @Transactional
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public R delete(@RequestBody Long[] ids){
        sysUserService.removeByIds(Arrays.asList(ids));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id",ids));
        return R.ok();
    }

    @ApiOperation("重置密码")
    @GetMapping("/resetPassword/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public R resetPassword(@PathVariable(value = "id")Integer id){
        SysUser sysUser = sysUserService.getById(id);
        sysUser.setPassword(bCryptPasswordEncoder.encode(Constant.DEFAULT_PASSWORD));
        sysUserService.updateById(sysUser);
        return R.ok();
    }

    @ApiOperation("更新status状态")
    @GetMapping("/updateStatus/{id}/status/{status}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public R updateStatus(@PathVariable(value = "id")Integer id,@PathVariable(value = "status")String status){
        SysUser sysUser = sysUserService.getById(id);
        sysUser.setStatus(status);
        sysUserService.saveOrUpdate(sysUser);
        return R.ok();
    }

    @ApiOperation("用户角色授权")
    @Transactional
    @PostMapping("/grantRole/{userId}")
    @PreAuthorize("hasAuthority('system:user:role')")
    public R grantRole(@PathVariable("userId") Long userId,@RequestBody Long[] roleIds){
        List<SysUserRole> userRoleList=new ArrayList<>();
        Arrays.stream(roleIds).forEach(r -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);//设置role_id
            sysUserRole.setUserId(userId);//设置user_id
            userRoleList.add(sysUserRole);
        });
        //清空用户目前的角色
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        //新的user_role
        sysUserRoleService.saveBatch(userRoleList);
        return R.ok();
    }
}
