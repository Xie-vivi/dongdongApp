package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容审核记录实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@TableName("content_audit_logs")
public class ContentAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 审核管理员ID
     */
    private Long adminId;

    /**
     * 审核管理员用户名
     */
    private String adminUsername;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 内容ID
     */
    private Long contentId;

    /**
     * 审核动作
     */
    private String action;

    /**
     * 审核理由
     */
    private String reason;

    /**
     * 原始状态
     */
    private String originalStatus;

    /**
     * 新状态
     */
    private String newStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // ==================== 临时字段（不映射到数据库） ====================

    /**
     * 管理员昵称
     */
    @TableField(exist = false)
    private String adminNickname;

    /**
     * 管理员头像
     */
    @TableField(exist = false)
    private String adminAvatar;

    /**
     * 内容标题
     */
    @TableField(exist = false)
    private String contentTitle;

    /**
     * 内容描述
     */
    @TableField(exist = false)
    private String contentDescription;

    /**
     * 内容作者ID
     */
    @TableField(exist = false)
    private Long contentAuthorId;

    /**
     * 内容作者用户名
     */
    @TableField(exist = false)
    private String contentAuthorUsername;

    /**
     * 内容作者昵称
     */
    @TableField(exist = false)
    private String contentAuthorNickname;

    /**
     * 是否可以撤销
     */
    @TableField(exist = false)
    private Boolean canRevoke;

    // ==================== Getter和Setter方法 ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(String originalStatus) {
        this.originalStatus = originalStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname;
    }

    public String getAdminAvatar() {
        return adminAvatar;
    }

    public void setAdminAvatar(String adminAvatar) {
        this.adminAvatar = adminAvatar;
    }

    public String getContentTitle() {
        return contentTitle;
    }

    public void setContentTitle(String contentTitle) {
        this.contentTitle = contentTitle;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public Long getContentAuthorId() {
        return contentAuthorId;
    }

    public void setContentAuthorId(Long contentAuthorId) {
        this.contentAuthorId = contentAuthorId;
    }

    public String getContentAuthorUsername() {
        return contentAuthorUsername;
    }

    public void setContentAuthorUsername(String contentAuthorUsername) {
        this.contentAuthorUsername = contentAuthorUsername;
    }

    public String getContentAuthorNickname() {
        return contentAuthorNickname;
    }

    public void setContentAuthorNickname(String contentAuthorNickname) {
        this.contentAuthorNickname = contentAuthorNickname;
    }

    public Boolean getCanRevoke() {
        return canRevoke;
    }

    public void setCanRevoke(Boolean canRevoke) {
        this.canRevoke = canRevoke;
    }

    // ==================== 枚举定义 ====================

    /**
     * 内容类型枚举
     */
    public enum ContentType {
        POST("post", "帖子"),
        ACTIVITY("activity", "活动"),
        USER("user", "用户"),
        FIELD("field", "场地"),
        COMMENT("comment", "评论");

        private final String code;
        private final String description;

        ContentType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static ContentType fromCode(String code) {
            for (ContentType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown content type code: " + code);
        }
    }

    /**
     * 审核动作枚举
     */
    public enum AuditAction {
        APPROVE("approve", "通过"),
        REJECT("reject", "拒绝"),
        DELETE("delete", "删除"),
        HIDE("hide", "隐藏"),
        RESTORE("restore", "恢复"),
        BAN("ban", "封禁"),
        UNBAN("unban", "解封");

        private final String code;
        private final String description;

        AuditAction(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static AuditAction fromCode(String code) {
            for (AuditAction action : values()) {
                if (action.code.equals(code)) {
                    return action;
                }
            }
            throw new IllegalArgumentException("Unknown audit action code: " + code);
        }
    }

    /**
     * 时间范围枚举
     */
    public enum TimeRange {
        TODAY("today", "今天"),
        YESTERDAY("yesterday", "昨天"),
        WEEK("week", "本周"),
        MONTH("month", "本月"),
        YEAR("year", "今年");

        private final String code;
        private final String description;

        TimeRange(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static TimeRange fromCode(String code) {
            for (TimeRange range : values()) {
                if (range.code.equals(code)) {
                    return range;
                }
            }
            throw new IllegalArgumentException("Unknown time range code: " + code);
        }
    }

    /**
     * 排序方式枚举
     */
    public enum SortBy {
        TIME_DESC("time_desc", "时间倒序"),
        TIME_ASC("time_asc", "时间正序"),
        ACTION_DESC("action_desc", "动作倒序"),
        ACTION_ASC("action_asc", "动作正序");

        private final String code;
        private final String description;

        SortBy(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static SortBy fromCode(String code) {
            for (SortBy sort : values()) {
                if (sort.code.equals(code)) {
                    return sort;
                }
            }
            throw new IllegalArgumentException("Unknown sort code: " + code);
        }
    }
}
