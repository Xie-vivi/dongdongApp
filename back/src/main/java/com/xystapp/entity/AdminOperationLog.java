package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员操作日志实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@TableName("admin_operation_logs")
public class AdminOperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 管理员ID
     */
    private Long adminId;

    /**
     * 管理员用户名
     */
    private String adminUsername;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作描述
     */
    private String operationDesc;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 目标ID
     */
    private Long targetId;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 请求URL
     */
    private String requestUrl;

    /**
     * 请求参数
     */
    private String requestParams;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 操作状态
     */
    private String status;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行时间(毫秒)
     */
    private Integer executionTime;

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
     * 目标标题
     */
    @TableField(exist = false)
    private String targetTitle;

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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationDesc() {
        return operationDesc;
    }

    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
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

    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    public Boolean getCanRevoke() {
        return canRevoke;
    }

    public void setCanRevoke(Boolean canRevoke) {
        this.canRevoke = canRevoke;
    }

    // ==================== 枚举定义 ====================

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        LOGIN("login", "登录"),
        LOGOUT("logout", "登出"),
        USER_MANAGE("user_manage", "用户管理"),
        USER_DELETE("user_delete", "删除用户"),
        USER_BAN("user_ban", "封禁用户"),
        USER_UNBAN("user_unban", "解封用户"),
        CONTENT_AUDIT("content_audit", "内容审核"),
        CONTENT_DELETE("content_delete", "删除内容"),
        CONTENT_HIDE("content_hide", "隐藏内容"),
        CONTENT_RESTORE("content_restore", "恢复内容"),
        SYSTEM_CONFIG("system_config", "系统配置"),
        DATA_EXPORT("data_export", "数据导出"),
        DATA_CLEANUP("data_cleanup", "数据清理"),
        ROLE_MANAGE("role_manage", "角色管理"),
        PERMISSION_MANAGE("permission_manage", "权限管理");

        private final String code;
        private final String description;

        OperationType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static OperationType fromCode(String code) {
            for (OperationType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown operation type code: " + code);
        }
    }

    /**
     * 目标类型枚举
     */
    public enum TargetType {
        USER("user", "用户"),
        POST("post", "帖子"),
        ACTIVITY("activity", "活动"),
        FIELD("field", "场地"),
        COMMENT("comment", "评论"),
        ROLE("role", "角色"),
        PERMISSION("permission", "权限"),
        SYSTEM("system", "系统");

        private final String code;
        private final String description;

        TargetType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static TargetType fromCode(String code) {
            for (TargetType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown target type code: " + code);
        }
    }

    /**
     * 操作状态枚举
     */
    public enum Status {
        SUCCESS("success", "成功"),
        FAILED("failed", "失败");

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
}
