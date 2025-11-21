package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivitySignup;

import java.util.List;

/**
 * 活动报名服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface ActivitySignupService {

    // ==================== 报名管理 ====================

    /**
     * 活动报名
     */
    ActivitySignup signupActivity(Long activityId, Long userId, String note);

    /**
     * 取消报名
     */
    ActivitySignup cancelSignup(Long activityId, Long userId);

    /**
     * 审核报名（通过）
     */
    ActivitySignup approveSignup(Long activityId, Long userId, Long operatorId);

    /**
     * 审核报名（拒绝）
     */
    ActivitySignup rejectSignup(Long activityId, Long userId, Long operatorId, String reason);

    /**
     * 批量审核报名
     */
    List<ActivitySignup> batchApproveSignups(Long activityId, List<Long> userIds, Long operatorId);

    /**
     * 批量拒绝报名
     */
    List<ActivitySignup> batchRejectSignups(Long activityId, List<Long> userIds, Long operatorId, String reason);

    // ==================== 报名查询 ====================

    /**
     * 获取活动报名列表
     */
    IPage<ActivitySignup> getActivitySignups(Long activityId, Integer page, Integer size, String status);

    /**
     * 获取用户报名列表
     */
    IPage<ActivitySignup> getUserSignups(Long userId, Integer page, Integer size, String status);

    /**
     * 获取报名详情
     */
    ActivitySignup getSignupDetail(Long activityId, Long userId);

    /**
     * 获取用户在活动中的报名信息
     */
    ActivitySignup getUserActivitySignup(Long activityId, Long userId);

    /**
     * 获取活动报名统计
     */
    ActivitySignupStats getActivitySignupStats(Long activityId);

    /**
     * 获取用户报名统计
     */
    UserSignupStats getUserSignupStats(Long userId);

    // ==================== 权限验证 ====================

    /**
     * 检查用户是否已报名
     */
    boolean isUserSignedUp(Long activityId, Long userId);

    /**
     * 检查用户是否可以报名
     */
    boolean canUserSignup(Long activityId, Long userId);

    /**
     * 检查用户是否可以取消报名
     */
    boolean canUserCancelSignup(Long activityId, Long userId);

    /**
     * 检查用户是否有审核权限
     */
    boolean hasApprovePermission(Long activityId, Long userId);

    /**
     * 检查活动是否存在
     */
    boolean activityExists(Long activityId);

    /**
     * 检查报名是否存在
     */
    boolean signupExists(Long activityId, Long userId);

    // ==================== 业务验证 ====================

    /**
     * 验证报名条件
     */
    void validateSignupConditions(Long activityId, Long userId);

    /**
     * 验证取消报名条件
     */
    void validateCancelConditions(Long activityId, Long userId);

    /**
     * 验证审核权限
     */
    void validateApprovePermission(Long activityId, Long operatorId);

    // ==================== 统计信息 ====================

    /**
     * 更新活动参与人数
     */
    void updateActivityParticipants(Long activityId);

    /**
     * 获取活动报名数量
     */
    Integer getActivitySignupCount(Long activityId, String status);

    /**
     * 获取用户报名数量
     */
    Integer getUserSignupCount(Long userId, String status);

    /**
     * 获取今日报名数量
     */
    Integer getTodaySignupCount(Long activityId);

    /**
     * 获取报名趋势统计
     */
    List<SignupTrendStats> getSignupTrend(Long activityId, Integer days);

    // ==================== 批量操作 ====================

    /**
     * 批量获取报名信息
     */
    List<ActivitySignup> getSignupsByIds(List<Long> signupIds);

    /**
     * 批量获取用户报名信息
     */
    List<ActivitySignup> getUserSignupsByActivityIds(List<Long> activityIds, Long userId);

    /**
     * 批量检查报名状态
     */
    List<SignupStatusInfo> batchCheckSignupStatus(List<Long> activityIds, Long userId);

    // ==================== 数据导出 ====================

    /**
     * 导出活动报名列表
     */
    List<ActivitySignup> exportActivitySignups(Long activityId, String status);

    /**
     * 导出用户报名历史
     */
    List<ActivitySignup> exportUserSignups(Long userId, String status);

    // ==================== 业务查询 ====================

    /**
     * 获取最近报名的用户
     */
    List<ActivitySignup> getRecentSignups(Long activityId, Integer limit);

    /**
     * 获取待审核的报名
     */
    List<ActivitySignup> getPendingSignups(Long activityId);

    /**
     * 获取需要处理的报名数量
     */
    Integer getPendingSignupCount(Long activityId);

    /**
     * 获取活动创建者的待处理报名
     */
    List<ActivitySignup> getCreatorPendingSignups(Long creatorId, Integer limit);

    // ==================== 统计类定义 ====================

    /**
     * 活动报名统计类
     */
    class ActivitySignupStats {
        private Long totalSignups;
        private Long confirmedSignups;
        private Long pendingSignups;
        private Long cancelledSignups;
        private Long rejectedSignups;
        private Long todaySignups;
        private Integer availableSlots;

        public ActivitySignupStats(Long totalSignups, Long confirmedSignups, Long pendingSignups,
                                 Long cancelledSignups, Long rejectedSignups, Long todaySignups, Integer availableSlots) {
            this.totalSignups = totalSignups;
            this.confirmedSignups = confirmedSignups;
            this.pendingSignups = pendingSignups;
            this.cancelledSignups = cancelledSignups;
            this.rejectedSignups = rejectedSignups;
            this.todaySignups = todaySignups;
            this.availableSlots = availableSlots;
        }

        public Long getTotalSignups() { return totalSignups; }
        public Long getConfirmedSignups() { return confirmedSignups; }
        public Long getPendingSignups() { return pendingSignups; }
        public Long getCancelledSignups() { return cancelledSignups; }
        public Long getRejectedSignups() { return rejectedSignups; }
        public Long getTodaySignups() { return todaySignups; }
        public Integer getAvailableSlots() { return availableSlots; }
    }

    /**
     * 用户报名统计类
     */
    class UserSignupStats {
        private Long totalSignups;
        private Long confirmedSignups;
        private Long pendingSignups;
        private Long cancelledSignups;
        private Long rejectedSignups;
        private Long thisMonthSignups;

        public UserSignupStats(Long totalSignups, Long confirmedSignups, Long pendingSignups,
                             Long cancelledSignups, Long rejectedSignups, Long thisMonthSignups) {
            this.totalSignups = totalSignups;
            this.confirmedSignups = confirmedSignups;
            this.pendingSignups = pendingSignups;
            this.cancelledSignups = cancelledSignups;
            this.rejectedSignups = rejectedSignups;
            this.thisMonthSignups = thisMonthSignups;
        }

        public Long getTotalSignups() { return totalSignups; }
        public Long getConfirmedSignups() { return confirmedSignups; }
        public Long getPendingSignups() { return pendingSignups; }
        public Long getCancelledSignups() { return cancelledSignups; }
        public Long getRejectedSignups() { return rejectedSignups; }
        public Long getThisMonthSignups() { return thisMonthSignups; }
    }

    /**
     * 报名趋势统计类
     */
    class SignupTrendStats {
        private String date;
        private Long signupCount;
        private Long confirmedCount;

        public SignupTrendStats(String date, Long signupCount, Long confirmedCount) {
            this.date = date;
            this.signupCount = signupCount;
            this.confirmedCount = confirmedCount;
        }

        public String getDate() { return date; }
        public Long getSignupCount() { return signupCount; }
        public Long getConfirmedCount() { return confirmedCount; }
    }

    /**
     * 报名状态信息类
     */
    class SignupStatusInfo {
        private Long activityId;
        private String activityTitle;
        private String status;
        private java.time.LocalDateTime signupTime;

        public SignupStatusInfo(Long activityId, String activityTitle, String status, java.time.LocalDateTime signupTime) {
            this.activityId = activityId;
            this.activityTitle = activityTitle;
            this.status = status;
            this.signupTime = signupTime;
        }

        public Long getActivityId() { return activityId; }
        public String getActivityTitle() { return activityTitle; }
        public String getStatus() { return status; }
        public java.time.LocalDateTime getSignupTime() { return signupTime; }
    }
}
