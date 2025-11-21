package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 通知设置实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@TableName("notification_settings")
public class NotificationSetting {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 通知类型：like, star, comment, at, follow, follow_post, follow_activity
     */
    private String type;

    /**
     * 是否启用通知：true-启用，false-禁用
     */
    private Boolean enabled;

    /**
     * 是否发送推送通知：true-发送，false-不发送
     */
    private Boolean pushEnabled;

    /**
     * 是否发送邮件通知：true-发送，false-不发送
     */
    private Boolean emailEnabled;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    // ==================== Getter和Setter方法 ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getPushEnabled() {
        return pushEnabled;
    }

    public void setPushEnabled(Boolean pushEnabled) {
        this.pushEnabled = pushEnabled;
    }

    public Boolean getEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(Boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
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

    // ==================== 枚举定义 ====================

    /**
     * 通知类型枚举
     */
    public enum Type {
        LIKE("like", "点赞通知"),
        STAR("star", "收藏通知"),
        COMMENT("comment", "评论通知"),
        AT("at", "@用户通知"),
        FOLLOW("follow", "关注通知"),
        FOLLOW_POST("follow_post", "关注用户帖子通知"),
        FOLLOW_ACTIVITY("follow_activity", "关注用户活动通知");

        private final String code;
        private final String description;

        Type(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Type fromCode(String code) {
            for (Type type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown notification type: " + code);
        }
    }
}
