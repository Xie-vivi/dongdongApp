package com.xystapp.controller;

import com.xystapp.common.Result;
import com.xystapp.service.AdminAuthService;
import com.xystapp.service.AdminService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员统计控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "管理员统计")
@RestController
@RequestMapping("/admin/statistics")
public class AdminStatisticsController {

    private static final Logger log = LoggerFactory.getLogger(AdminStatisticsController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminAuthService adminAuthService;

    /**
     * 获取系统统计概览
     */
    @ApiOperation("获取系统统计概览")
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('statistics', 'read')")
    public Result<AdminService.SystemStats> getSystemOverview() {
        try {
            log.info("获取系统统计概览");
            AdminService.SystemStats stats = adminService.getSystemStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取系统统计概览失败", e);
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户统计数据
     */
    @ApiOperation("获取用户统计数据")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('statistics', 'read')")
    public Result<AdminService.UserStats> getUserStatistics(
            @ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        try {
            log.info("获取用户统计数据: timeRange={}", timeRange);
            AdminService.UserStats stats = adminService.getUserStats(timeRange);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户统计数据失败", e);
            return Result.error("获取用户统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取内容统计数据
     */
    @ApiOperation("获取内容统计数据")
    @GetMapping("/content")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('statistics', 'read')")
    public Result<AdminService.ContentStats> getContentStatistics(
            @ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        try {
            log.info("获取内容统计数据: timeRange={}", timeRange);
            AdminService.ContentStats stats = adminService.getContentStats(timeRange);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取内容统计数据失败", e);
            return Result.error("获取内容统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取管理员活动统计
     */
    @ApiOperation("获取管理员活动统计")
    @GetMapping("/admin-activity")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('statistics', 'read')")
    public Result<AdminService.AdminActivityStats> getAdminActivityStatistics(
            @ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        try {
            log.info("获取管理员活动统计: timeRange={}", timeRange);
            AdminService.AdminActivityStats stats = adminService.getAdminActivityStats(timeRange);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取管理员活动统计失败", e);
            return Result.error("获取管理员活动统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门内容统计
     */
    @ApiOperation("获取热门内容统计")
    @GetMapping("/hot-content")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('statistics', 'read')")
    public Result<AdminService.HotContentStats> getHotContentStatistics(
            @ApiParam("内容类型") @RequestParam(required = false) String contentType,
            @ApiParam("限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        try {
            log.info("获取热门内容统计: contentType={}, limit={}", contentType, limit);
            AdminService.HotContentStats stats = adminService.getHotContentStats(contentType, limit);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取热门内容统计失败", e);
            return Result.error("获取热门内容统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取审核队列统计
     */
    @ApiOperation("获取审核队列统计")
    @GetMapping("/audit-queue")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('statistics', 'read')")
    public Result<AuditQueueStats> getAuditQueueStatistics() {
        try {
            log.info("获取审核队列统计");
            
            // 获取待审核内容列表
            AdminService.PendingContentList pendingContent = adminService.getPendingContentList(1, 1000, null);
            
            AuditQueueStats stats = new AuditQueueStats();
            stats.setTotalPending(pendingContent.getTotal());
            stats.setPendingPosts(pendingContent.getPosts().size());
            stats.setPendingActivities(pendingContent.getActivities().size());
            stats.setPendingUsers(pendingContent.getUsers().size());
            stats.setPendingFields(pendingContent.getFields().size());
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取审核队列统计失败", e);
            return Result.error("获取审核队列统计失败：" + e.getMessage());
        }
    }

    /**
     * 获取系统健康状态
     */
    @ApiOperation("获取系统健康状态")
    @GetMapping("/system-health")
    @PreAuthorize("hasRole('ADMIN') and hasPermission('statistics', 'read')")
    public Result<SystemHealthStats> getSystemHealthStatistics() {
        try {
            log.info("获取系统健康状态");
            
            SystemHealthStats stats = new SystemHealthStats();
            
            // 获取系统状态
            AdminService.SystemStatus systemStatus = adminService.getSystemStatus();
            stats.setSystemStatus(systemStatus);
            
            // 获取管理员活动统计
            AdminService.AdminActivityStats activityStats = adminService.getAdminActivityStats("today");
            stats.setTodayAdminActivity(activityStats);
            
            // 设置系统健康度（示例逻辑）
            stats.setHealthScore(calculateHealthScore(systemStatus, activityStats));
            
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取系统健康状态失败", e);
            return Result.error("获取系统健康状态失败：" + e.getMessage());
        }
    }

    /**
     * 审核队列统计内部类
     */
    public static class AuditQueueStats {
        private Integer totalPending;
        private Integer pendingPosts;
        private Integer pendingActivities;
        private Integer pendingUsers;
        private Integer pendingFields;

        // getters and setters
        public Integer getTotalPending() { return totalPending; }
        public void setTotalPending(Integer totalPending) { this.totalPending = totalPending; }
        public Integer getPendingPosts() { return pendingPosts; }
        public void setPendingPosts(Integer pendingPosts) { this.pendingPosts = pendingPosts; }
        public Integer getPendingActivities() { return pendingActivities; }
        public void setPendingActivities(Integer pendingActivities) { this.pendingActivities = pendingActivities; }
        public Integer getPendingUsers() { return pendingUsers; }
        public void setPendingUsers(Integer pendingUsers) { this.pendingUsers = pendingUsers; }
        public Integer getPendingFields() { return pendingFields; }
        public void setPendingFields(Integer pendingFields) { this.pendingFields = pendingFields; }
    }

    /**
     * 系统健康状态统计内部类
     */
    public static class SystemHealthStats {
        private AdminService.SystemStatus systemStatus;
        private AdminService.AdminActivityStats todayAdminActivity;
        private Integer healthScore;

        // getters and setters
        public AdminService.SystemStatus getSystemStatus() { return systemStatus; }
        public void setSystemStatus(AdminService.SystemStatus systemStatus) { this.systemStatus = systemStatus; }
        public AdminService.AdminActivityStats getTodayAdminActivity() { return todayAdminActivity; }
        public void setTodayAdminActivity(AdminService.AdminActivityStats todayAdminActivity) { this.todayAdminActivity = todayAdminActivity; }
        public Integer getHealthScore() { return healthScore; }
        public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
    }

    /**
     * 计算系统健康分数
     */
    private Integer calculateHealthScore(AdminService.SystemStatus systemStatus, 
                                        AdminService.AdminActivityStats activityStats) {
        // 简单的健康分数计算逻辑
        int score = 100;
        
        // 根据系统状态调整分数
        if (systemStatus != null) {
            if (systemStatus.getDatabaseStatus() != null && !"healthy".equals(systemStatus.getDatabaseStatus())) {
                score -= 30;
            }
            if (systemStatus.getRedisStatus() != null && !"healthy".equals(systemStatus.getRedisStatus())) {
                score -= 20;
            }
            if (systemStatus.getDiskUsage() != null && systemStatus.getDiskUsage() > 80) {
                score -= 20;
            }
        }
        
        // 根据管理员活动调整分数
        if (activityStats != null && activityStats.getTotalOperations() != null) {
            if (activityStats.getTotalOperations() == 0) {
                score -= 10; // 无管理员活动可能表示异常
            }
        }
        
        return Math.max(0, score);
    }
}
