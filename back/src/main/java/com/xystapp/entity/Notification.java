package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@TableName("notifications")
public class Notification {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接收通知的用户ID
     */
    private Long userId;

    /**
     * 通知类型：like(点赞), star(收藏), comment(评论), at(@用户), follow(关注), 
     * follow_post(关注用户帖子), follow_activity(关注用户活动)
     */
    private String type;

    /**
     * 关联内容类型：post(帖子), activity(活动), comment(评论)
     */
    private String relatedType;

    /**
     * 关联内容ID
     */
    private Long relatedId;

    /**
     * 发送者用户ID
     */
    private Long senderId;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 是否已读：0-未读，1-已读
     */
    private Boolean isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // ==================== 临时字段（不映射到数据库） ====================

    /**
     * 发送者用户名
     */
    @TableField(exist = false)
    private String senderUsername;

    /**
     * 发送者昵称
     */
    @TableField(exist = false)
    private String senderNickname;

    /**
     * 发送者头像
     */
    @TableField(exist = false)
    private String senderAvatar;

    /**
     * 关联内容标题
     */
    @TableField(exist = false)
    private String relatedTitle;

    /**
     * 关联内容预览
     */
    @TableField(exist = false)
    private String relatedPreview;

    /**
     * 是否可以点击跳转
     */
    @TableField(exist = false)
    private Boolean canClick;

    /**
     * 跳转链接
     */
    @TableField(exist = false)
    private String jumpUrl;

    // ==================== 枚举定义 ====================

    /**
     * 通知类型枚举
     */
    public enum Type {
        LIKE("like", "点赞"),
        STAR("star", "收藏"),
        COMMENT("comment", "评论"),
        AT("at", "@用户"),
        FOLLOW("follow", "关注"),
        FOLLOW_POST("follow_post", "关注用户帖子"),
        FOLLOW_ACTIVITY("follow_activity", "关注用户活动");

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

    /**
     * 关联类型枚举
     */
    public enum RelatedType {
        POST("post", "帖子"),
        ACTIVITY("activity", "活动"),
        COMMENT("comment", "评论");

        private final String code;
        private final String description;

        RelatedType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static RelatedType fromCode(String code) {
            for (RelatedType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown related type: " + code);
        }
    }

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRelatedType() { return relatedType; }
    public void setRelatedType(String relatedType) { this.relatedType = relatedType; }

    public Long getRelatedId() { return relatedId; }
    public void setRelatedId(Long relatedId) { this.relatedId = relatedId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }

    public String getSenderNickname() { return senderNickname; }
    public void setSenderNickname(String senderNickname) { this.senderNickname = senderNickname; }

    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }

    public String getRelatedTitle() { return relatedTitle; }
    public void setRelatedTitle(String relatedTitle) { this.relatedTitle = relatedTitle; }

    public String getRelatedPreview() { return relatedPreview; }
    public void setRelatedPreview(String relatedPreview) { this.relatedPreview = relatedPreview; }

    public Boolean getCanClick() { return canClick; }
    public void setCanClick(Boolean canClick) { this.canClick = canClick; }

    public String getJumpUrl() { return jumpUrl; }
    public void setJumpUrl(String jumpUrl) { this.jumpUrl = jumpUrl; }
}
