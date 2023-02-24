package com.java1234.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName sys_role
 */
@TableName(value ="sys_role")
@Data
public class SysRole extends BaseEntity implements Serializable {
    /**
     * 角色名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 角色权限字符串
     */
    @TableField(value = "code")
    private String code;

    /**
     * 删除标记（0:可用 1:已删除）
     */
    @TableLogic  //逻辑删除 默认效果 0 没有删除 1 已经删除
    @TableField("is_deleted")
    private Integer isDeleted;

}