package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivitySignup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动报名Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {

    /**
     * 根据活动ID和用户ID获取报名记录
     */
    ActivitySignup selectByActivityIdAndUserId(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 获取报名详情（包含用户和活动信息）
     */
    ActivitySignup selectSignupDetail(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 获取活动报名列表（包含用户信息）
     */
    IPage<ActivitySignup> selectActivitySignups(Page<ActivitySignup> page, @Param("activityId") Long activityId, @Param("status") String status);

    /**
     * 获取用户报名列表（包含活动信息）
     */
    IPage<ActivitySignup> selectUserSignups(Page<ActivitySignup> page, @Param("userId") Long userId, @Param("status") String status);

    // ==================== 统计相关 ====================

    /**
     * 统计活动报名数量
     */
    Integer countByActivityIdAndStatus(@Param("activityId") Long activityId, @Param("status") String status);

    /**
     * 统计用户报名数量
     */
    Integer countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 统计今日报名数量
     */
    Integer countTodaySignups(@Param("activityId") Long activityId);

    /**
     * 统计用户本月报名数量
     */
    Long countUserSignupsByMonth(@Param("userId") Long userId);

    /**
     * 统计待审核报名数量
     */
    Integer countPendingSignups(@Param("activityId") Long activityId);

    // ==================== 批量操作 ====================

    /**
     * 批量获取报名记录
     */
    List<ActivitySignup> selectSignupsByIds(@Param("signupIds") List<Long> signupIds);

    /**
     * 批量获取用户在指定活动中的报名信息
     */
    List<ActivitySignup> selectUserSignupsByActivityIds(@Param("activityIds") List<Long> activityIds, @Param("userId") Long userId);

    /**
     * 批量检查报名状态
     */
    List<com.xystapp.service.ActivitySignupService.SignupStatusInfo> selectBatchSignupStatus(@Param("activityIds") List<Long> activityIds, @Param("userId") Long userId);

    // ==================== 业务查询 ====================

    /**
     * 获取最近报名的用户
     */
    List<ActivitySignup> selectRecentSignups(@Param("activityId") Long activityId, @Param("limit") Integer limit);

    /**
     * 获取待审核的报名列表
     */
    List<ActivitySignup> selectPendingSignups(@Param("activityId") Long activityId);

    /**
     * 获取活动创建者的待处理报名
     */
    List<ActivitySignup> selectCreatorPendingSignups(@Param("creatorId") Long creatorId, @Param("limit") Integer limit);

    /**
     * 获取报名趋势统计
     */
    List<com.xystapp.service.ActivitySignupService.SignupTrendStats> selectSignupTrend(@Param("activityId") Long activityId, @Param("days") Integer days);

    /**
     * 获取活动报名用户列表
     */
    List<ActivitySignup> selectActivitySignupUsers(@Param("activityId") Long activityId, @Param("status") String status, @Param("limit") Integer limit);

    /**
     * 获取用户报名的活动列表
     */
    List<ActivitySignup> selectUserSignupActivities(@Param("userId") Long userId, @Param("status") String status, @Param("limit") Integer limit);

    // ==================== 数据导出 ====================

    /**
     * 导出活动报名列表
     */
    List<ActivitySignup> selectExportActivitySignups(@Param("activityId") Long activityId, @Param("status") String status);

    /**
     * 导出用户报名历史
     */
    List<ActivitySignup> selectExportUserSignups(@Param("userId") Long userId, @Param("status") String status);

    // ==================== 数据维护 ====================

    /**
     * 清理无效的报名记录（活动或用户不存在）
     */
    int cleanupInvalidSignups();

    /**
     * 获取需要清理的报名数量
     */
    Long countSignupsToCleanup(@Param("days") Integer days);

    /**
     * 更新报名状态
     */
    int updateSignupStatus(@Param("activityId") Long activityId, @Param("userId") Long userId, @Param("status") String status);

    /**
     * 批量更新报名状态
     */
    int batchUpdateSignupStatus(@Param("activityId") Long activityId, @Param("userIds") List<Long> userIds, @Param("status") String status);

    /**
     * 根据活动ID删除所有报名记录
     */
    int deleteByActivityId(@Param("activityId") Long activityId);

    /**
     * 根据用户ID删除所有报名记录
     */
    int deleteByUserId(@Param("userId") Long userId);

    // ==================== 权限相关 ====================

    /**
     * 检查用户是否是活动创建者
     */
    boolean isActivityCreator(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 获取活动的报名用户ID列表
     */
    List<Long> selectActivitySignupUserIds(@Param("activityId") Long activityId, @Param("status") String status);

    /**
     * 获取用户报名的活动ID列表
     */
    List<Long> selectUserSignupActivityIds(@Param("userId") Long userId, @Param("status") String status);

    // ==================== 时间相关查询 ====================

    /**
     * 获取指定时间段内的报名记录
     */
    List<ActivitySignup> selectSignupsByTimeRange(@Param("activityId") Long activityId, 
                                                  @Param("startTime") java.time.LocalDateTime startTime,
                                                  @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 获取用户指定时间段内的报名记录
     */
    List<ActivitySignup> selectUserSignupsByTimeRange(@Param("userId") Long userId,
                                                      @Param("startTime") java.time.LocalDateTime startTime,
                                                      @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 获取即将到期的报名（活动即将开始）
     */
    List<ActivitySignup> selectUpcomingSignups(@Param("days") Integer days);

    /**
     * 获取过期的报名（活动已结束）
     */
    List<ActivitySignup> selectExpiredSignups(@Param("days") Integer days);

    // ==================== 统计分析 ====================

    /**
     * 获取活动报名统计
     */
    ActivitySignupStats selectActivitySignupStats(@Param("activityId") Long activityId);

    /**
     * 获取用户报名统计
     */
    UserSignupStats selectUserSignupStats(@Param("userId") Long userId);

    /**
     * 获取热门活动报名统计
     */
    List<ActivitySignupStats> selectPopularActivityStats(@Param("limit") Integer limit);

    /**
     * 获取活跃用户报名统计
     */
    List<UserSignupStats> selectActiveUserStats(@Param("limit") Integer limit);

    /**
     * 活动报名统计类
     */
    class ActivitySignupStats {
        private Long activityId;
        private String activityTitle;
        private Long totalSignups;
        private Long confirmedSignups;
        private java.time.LocalDateTime lastSignupTime;

        public ActivitySignupStats(Long activityId, String activityTitle, Long totalSignups,
                                 Long confirmedSignups, java.time.LocalDateTime lastSignupTime) {
            this.activityId = activityId;
            this.activityTitle = activityTitle;
            this.totalSignups = totalSignups;
            this.confirmedSignups = confirmedSignups;
            this.lastSignupTime = lastSignupTime;
        }

        public Long getActivityId() { return activityId; }
        public String getActivityTitle() { return activityTitle; }
        public Long getTotalSignups() { return totalSignups; }
        public Long getConfirmedSignups() { return confirmedSignups; }
        public java.time.LocalDateTime getLastSignupTime() { return lastSignupTime; }
    }

    /**
     * 用户报名统计类
     */
    class UserSignupStats {
        private Long userId;
        private String username;
        private String nickname;
        private Long totalSignups;
        private Long confirmedSignups;
        private java.time.LocalDateTime lastSignupTime;

        public UserSignupStats(Long userId, String username, String nickname,
                             Long totalSignups, Long confirmedSignups, java.time.LocalDateTime lastSignupTime) {
            this.userId = userId;
            this.username = username;
            this.nickname = nickname;
            this.totalSignups = totalSignups;
            this.confirmedSignups = confirmedSignups;
            this.lastSignupTime = lastSignupTime;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public Long getTotalSignups() { return totalSignups; }
        public Long getConfirmedSignups() { return confirmedSignups; }
        public java.time.LocalDateTime getLastSignupTime() { return lastSignupTime; }
    }
}
