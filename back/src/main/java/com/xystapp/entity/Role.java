package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("roles")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("display_name")
    private String displayName;

    @TableField("description")
    private String description;

    @TableField("is_system")
    private Boolean isSystem;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 关联字段（不存储在数据库中）
    @TableField(exist = false)
    private List<Permission> permissions;

    @TableField(exist = false)
    private Integer userCount;

    @TableField(exist = false)
    private Integer adminCount;

    /**
     * 系统角色常量
     */
    public static final String SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ADMIN = "ADMIN";
    public static final String MODERATOR = "MODERATOR";
    public static final String EDITOR = "EDITOR";

    /**
     * 检查是否为系统角色
     */
    public boolean isSystemRole() {
        return Boolean.TRUE.equals(isSystem);
    }

    /**
     * 检查是否为超级管理员角色
     */
    public boolean isSuperAdmin() {
        return SUPER_ADMIN.equals(name);
    }

    /**
     * 检查是否可以删除
     */
    public boolean canDelete() {
        return !isSystemRole();
    }

    /**
     * 检查是否可以修改
     */
    public boolean canUpdate() {
        return !isSystemRole() || !SUPER_ADMIN.equals(name);
    }
}
