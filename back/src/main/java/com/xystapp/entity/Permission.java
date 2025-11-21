package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("permissions")
public class Permission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("display_name")
    private String displayName;

    @TableField("resource")
    private String resource;

    @TableField("action")
    private String action;

    @TableField("description")
    private String description;

    @TableField("is_system")
    private Boolean isSystem;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 资源类型常量
     */
    public static final String RESOURCE_USER = "user";
    public static final String RESOURCE_POST = "post";
    public static final String RESOURCE_ACTIVITY = "activity";
    public static final String RESOURCE_COMMENT = "comment";
    public static final String RESOURCE_FIELD = "field";
    public static final String RESOURCE_ADMIN = "admin";
    public static final String RESOURCE_ROLE = "role";
    public static final String RESOURCE_PERMISSION = "permission";

    /**
     * 操作类型常量
     */
    public static final String ACTION_CREATE = "create";
    public static final String ACTION_READ = "read";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_AUDIT = "audit";
    public static final String ACTION_MANAGE = "manage";
    public static final String ACTION_EXPORT = "export";

    /**
     * 生成权限名称
     */
    public static String generatePermissionName(String resource, String action) {
        return resource + ":" + action;
    }

    /**
     * 检查是否为系统权限
     */
    public boolean isSystemPermission() {
        return Boolean.TRUE.equals(isSystem);
    }

    /**
     * 检查是否可以删除
     */
    public boolean canDelete() {
        return !isSystemPermission();
    }

    /**
     * 检查是否可以修改
     */
    public boolean canUpdate() {
        return !isSystemPermission();
    }

    /**
     * 获取完整权限标识
     */
    public String getFullPermission() {
        return generatePermissionName(resource, action);
    }

    /**
     * 预定义的系统权限
     */
    public static class SystemPermissions {
        // 超级管理员权限
        public static final String SUPER_ADMIN = "admin:super";
        
        // 用户管理权限
        public static final String USER_CREATE = "user:create";
        public static final String USER_READ = "user:read";
        public static final String USER_UPDATE = "user:update";
        public static final String USER_DELETE = "user:delete";
        public static final String USER_AUDIT = "user:audit";
        
        // 内容管理权限
        public static final String POST_CREATE = "post:create";
        public static final String POST_READ = "post:read";
        public static final String POST_UPDATE = "post:update";
        public static final String POST_DELETE = "post:delete";
        public static final String POST_AUDIT = "post:audit";
        
        public static final String ACTIVITY_CREATE = "activity:create";
        public static final String ACTIVITY_READ = "activity:read";
        public static final String ACTIVITY_UPDATE = "activity:update";
        public static final String ACTIVITY_DELETE = "activity:delete";
        public static final String ACTIVITY_AUDIT = "activity:audit";
        
        // 角色权限管理
        public static final String ROLE_MANAGE = "role:manage";
        public static final String PERMISSION_MANAGE = "permission:manage";
        
        // 数据统计权限
        public static final String STATISTICS_READ = "statistics:read";
        public static final String DATA_EXPORT = "data:export";
    }
}
