package com.java1234.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.java1234.entity.PageBean;
import com.java1234.entity.R;
import com.java1234.entity.SysRole;
import com.java1234.entity.SysUser;
import com.java1234.service.SysRoleService;
import com.java1234.service.SysUserService;
import com.java1234.util.DateUtil;
import com.java1234.util.StringUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${avatarImagesFilePath}")
    private String avatarImagesFilePath;

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
    /**
     * 上传用户头像图片
     */
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

    /**
     * 修改用户头像
     * @param sysUser
     * @return
     */
    @RequestMapping("/updateAvatar")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public R updateAvatar(@RequestBody SysUser sysUser){
        SysUser currentUser = sysUserService.getById(sysUser.getId());
        currentUser.setAvatar(sysUser.getAvatar());
        sysUserService.updateById(currentUser);
        return R.ok();
    }

    //条件分页查询用户信息
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
}
