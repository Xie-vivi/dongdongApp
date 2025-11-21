package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 活动报名实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("activity_signups")
public class ActivitySignup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("activity_id")
    private Long activityId;

    @TableField("user_id")
    private Long userId;

    @TableField("status")
    private String status;

    @TableField("signup_time")
    private LocalDateTime signupTime;

    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    @TableField("note")
    private String note;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

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
     * 活动标题
     */
    @TableField(exist = false)
    private String activityTitle;

    /**
     * 活动时间
     */
    @TableField(exist = false)
    private String activityTime;

    /**
     * 活动地点
     */
    @TableField(exist = false)
    private String activityLocation;

    /**
     * 活动状态
     */
    @TableField(exist = false)
    private String activityStatus;

    /**
     * 活动最大参与人数
     */
    @TableField(exist = false)
    private Integer activityMaxParticipants;

    // ==================== Getter和Setter方法 ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSignupTime() {
        return signupTime;
    }

    public void setSignupTime(LocalDateTime signupTime) {
        this.signupTime = signupTime;
    }

    public LocalDateTime getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(LocalDateTime cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getActivityTitle() {
        return activityTitle;
    }

    public void setActivityTitle(String activityTitle) {
        this.activityTitle = activityTitle;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }

    public String getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        this.activityStatus = activityStatus;
    }

    public Integer getActivityMaxParticipants() {
        return activityMaxParticipants;
    }

    public void setActivityMaxParticipants(Integer activityMaxParticipants) {
        this.activityMaxParticipants = activityMaxParticipants;
    }

    public Integer getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(Integer currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public LocalDateTime getSignupDeadline() {
        return signupDeadline;
    }

    public void setSignupDeadline(LocalDateTime signupDeadline) {
        this.signupDeadline = signupDeadline;
    }

    /**
     * 当前参与人数
     */
    @TableField(exist = false)
    private Integer currentParticipants;

    /**
     * 报名截止时间
     */
    @TableField(exist = false)
    private LocalDateTime signupDeadline;

    /**
     * 活动创建者ID
     */
    @TableField(exist = false)
    private Long activityCreatorId;

    /**
     * 活动创建者用户名
     */
    @TableField(exist = false)
    private String activityCreatorUsername;

    /**
     * 是否可以取消
     */
    @TableField(exist = false)
    private Boolean canCancel;

    /**
     * 是否可以审核
     */
    @TableField(exist = false)
    private Boolean canApprove;

    /**
     * 报名序号（按报名时间排序）
     */
    @TableField(exist = false)
    private Integer signupOrder;

    // ==================== 枚举定义 ====================

    /**
     * 报名状态枚举
     */
    public enum Status {
        PENDING("pending", "待审核"),
        CONFIRMED("confirmed", "已确认"),
        CANCELLED("cancelled", "已取消"),
        REJECTED("rejected", "已拒绝");

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
     * 报名操作枚举
     */
    public enum Action {
        SIGNUP("signup", "报名"),
        CANCEL("cancel", "取消"),
        APPROVE("approve", "通过"),
        REJECT("reject", "拒绝");

        private final String code;
        private final String description;

        Action(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static Action fromCode(String code) {
            for (Action action : values()) {
                if (action.code.equals(code)) {
                    return action;
                }
            }
            throw new IllegalArgumentException("Unknown action code: " + code);
        }
    }
}
