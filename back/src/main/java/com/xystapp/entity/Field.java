package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 场地实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@TableName("fields")
public class Field {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 场地名称
     */
    private String name;

    /**
     * 场地描述
     */
    private String description;

    /**
     * 场地头像
     */
    private String avatar;

    /**
     * 创建者ID
     */
    private Long creatorId;

    /**
     * 成员数量
     */
    private Integer membersCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ==================== 临时字段（不映射到数据库） ====================

    /**
     * 创建者用户名
     */
    @TableField(exist = false)
    private String creatorUsername;

    /**
     * 创建者昵称
     */
    @TableField(exist = false)
    private String creatorNickname;

    /**
     * 创建者头像
     */
    @TableField(exist = false)
    private String creatorAvatar;

    /**
     * 当前用户在场地中的角色
     */
    @TableField(exist = false)
    private String currentUserRole;

    /**
     * 当前用户是否是成员
     */
    @TableField(exist = false)
    private Boolean isMember;

    /**
     * 当前用户是否是管理员
     */
    @TableField(exist = false)
    private Boolean isAdmin;

    /**
     * 当前用户是否是创建者
     */
    @TableField(exist = false)
    private Boolean isCreator;

    /**
     * 场地帖子数量
     */
    @TableField(exist = false)
    private Integer postsCount;

    /**
     * 场地活动数量
     */
    @TableField(exist = false)
    private Integer activitiesCount;

    /**
     * 最近活动时间
     */
    @TableField(exist = false)
    private LocalDateTime lastActivityAt;

    // 添加缺失的status字段
    private String status;

    // ==================== Getter和Setter方法 ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public String getCreatorNickname() {
        return creatorNickname;
    }

    public void setCreatorNickname(String creatorNickname) {
        this.creatorNickname = creatorNickname;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(String currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    public Boolean getIsMember() {
        return isMember;
    }

    public void setIsMember(Boolean isMember) {
        this.isMember = isMember;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public Boolean getIsCreator() {
        return isCreator;
    }

    public void setIsCreator(Boolean isCreator) {
        this.isCreator = isCreator;
    }

    public Integer getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(Integer postsCount) {
        this.postsCount = postsCount;
    }

    public Integer getActivitiesCount() {
        return activitiesCount;
    }

    public void setActivitiesCount(Integer activitiesCount) {
        this.activitiesCount = activitiesCount;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(LocalDateTime lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ==================== 枚举定义 ====================

    /**
     * 成员角色枚举（对应数据库user_fields.role字段）
     */
    public enum Role {
        MEMBER("member", "普通成员"),
        ADMIN("admin", "管理员");

        private final String code;
        private final String description;

        Role(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Role fromCode(String code) {
            for (Role role : values()) {
                if (role.code.equals(code)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Unknown role code: " + code);
        }
    }

    /**
     * 场地状态枚举
     */
    public enum Status {
        ACTIVE("active", "活跃"),
        INACTIVE("inactive", "非活跃"),
        SUSPENDED("suspended", "暂停");

        private final String code;
        private final String description;

        Status(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Status fromCode(String code) {
            for (Status status : values()) {
                if (status.code.equals(code)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown status code: " + code);
        }
    }
}
