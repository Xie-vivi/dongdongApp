package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 场地成员实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@TableName("user_fields")
public class FieldMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 场地ID
     */
    private Long fieldId;

    /**
     * 角色：member(普通成员), admin(管理员)
     */
    private String role;

    /**
     * 加入时间
     */
    private LocalDateTime joinedAt;

    // ==================== 临时字段（不映射到数据库） ====================

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String username;

    /**
     * 用户昵称
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String avatar;

    /**
     * 场地名称
     */
    @TableField(exist = false)
    private String fieldName;

    /**
     * 场地头像
     */
    @TableField(exist = false)
    private String fieldAvatar;

    /**
     * 用户状态
     */
    @TableField(exist = false)
    private String userStatus;

    /**
     * 最后活跃时间
     */
    @TableField(exist = false)
    private LocalDateTime lastActiveAt;

    // ==================== 枚举定义 ====================

    /**
     * 成员角色枚举
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
     * 成员状态枚举
     */
    public enum Status {
        ACTIVE("active", "活跃"),
        INACTIVE("inactive", "非活跃"),
        BANNED("banned", "已禁用");

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

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldAvatar() { return fieldAvatar; }
    public void setFieldAvatar(String fieldAvatar) { this.fieldAvatar = fieldAvatar; }

    public String getUserStatus() { return userStatus; }
    public void setUserStatus(String userStatus) { this.userStatus = userStatus; }

    public LocalDateTime getLastActiveAt() { return lastActiveAt; }
    public void setLastActiveAt(LocalDateTime lastActiveAt) { this.lastActiveAt = lastActiveAt; }
}
