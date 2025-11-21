package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.AdminOperationLog;
import com.xystapp.entity.ContentAuditLog;
import com.xystapp.entity.User;

import java.util.List;

/**
 * 后台管理服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface AdminService {

    // ==================== 管理员认证 ====================

    /**
     * 管理员登录（已废弃，使用AdminAuthService）
     * @deprecated 使用 AdminAuthService.login() 替代
     */
    @Deprecated
    AdminLoginResult adminLogin(String username, String password, String ipAddress, String userAgent);

    /**
     * 检查用户是否为管理员
     */
    boolean isAdmin(Long userId);

    /**
     * 检查用户是否为超级管理员
     */
    boolean isSuperAdmin(Long userId);

    /**
     * 获取管理员信息
     */
    AdminInfo getAdminInfo(Long userId);

    /**
     * 检查管理员权限（委托给AdminAuthService）
     */
    boolean hasPermission(Long adminId, String permission);

    /**
     * 获取管理员权限列表（委托给AdminAuthService）
     */
    List<String> getAdminPermissions(Long adminId);

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表
     */
    IPage<User> getUserList(Integer page, Integer size, String keyword, String role, String status);

    /**
     * 更新用户角色
     */
    void updateUserRole(Long adminId, Long userId, String role);

    /**
     * 封禁用户
     */
    void banUser(Long adminId, Long userId, String reason);

    /**
     * 解封用户
     */
    void unbanUser(Long adminId, Long userId, String reason);

    /**
     * 删除用户
     */
    void deleteUser(Long adminId, Long userId, String reason);

    /**
     * 重置用户密码
     */
    void resetUserPassword(Long adminId, Long userId, String newPassword);

    // ==================== 内容审核 ====================

    /**
     * 获取待审核内容列表
     */
    PendingContentList getPendingContentList(Integer page, Integer size, String contentType);

    /**
     * 审核帖子
     */
    void auditPost(Long adminId, Long postId, String action, String reason);

    /**
     * 审核活动
     */
    void auditActivity(Long adminId, Long activityId, String action, String reason);

    /**
     * 审核用户
     */
    void auditUser(Long adminId, Long userId, String action, String reason);

    /**
     * 审核场地
     */
    void auditField(Long adminId, Long fieldId, String action, String reason);

    /**
     * 批量审核内容
     */
    void batchAuditContent(Long adminId, List<ContentAuditRequest> requests);

    // ==================== 操作日志管理 ====================

    /**
     * 记录管理员操作日志
     */
    void logAdminOperation(AdminOperationLog log);

    /**
     * 获取操作日志列表
     */
    IPage<AdminOperationLog> getOperationLogs(Integer page, Integer size, String operationType, 
                                              String targetType, String adminId, String timeRange);

    /**
     * 获取操作日志详情
     */
    AdminOperationLog getOperationLogDetail(Long logId);

    /**
     * 清理过期操作日志
     */
    int cleanupOperationLogs(Integer days);

    // ==================== 内容审核日志管理 ====================

    /**
     * 记录内容审核日志
     */
    void logContentAudit(ContentAuditLog log);

    /**
     * 获取内容审核日志列表
     */
    IPage<ContentAuditLog> getContentAuditLogs(Integer page, Integer size, String contentType, 
                                               String action, String adminId, String timeRange);

    /**
     * 获取内容审核日志详情
     */
    ContentAuditLog getContentAuditLogDetail(Long logId);

    // ==================== 数据统计 ====================

    /**
     * 获取系统统计概览
     */
    SystemStats getSystemStats();

    /**
     * 获取用户统计
     */
    UserStats getUserStats(String timeRange);

    /**
     * 获取内容统计
     */
    ContentStats getContentStats(String timeRange);

    /**
     * 获取管理员活动统计
     */
    AdminActivityStats getAdminActivityStats(String timeRange);

    /**
     * 获取热门内容统计
     */
    HotContentStats getHotContentStats(String contentType, Integer limit);

    // ==================== 系统配置 ====================

    /**
     * 获取系统配置
     */
    SystemConfig getSystemConfig();

    /**
     * 更新系统配置
     */
    void updateSystemConfig(Long adminId, SystemConfig config);

    /**
     * 获取系统状态
     */
    SystemStatus getSystemStatus();

    // ==================== 数据导出 ====================

    /**
     * 导出用户数据
     */
    List<User> exportUsers(String role, String status, String timeRange);

    /**
     * 导出操作日志
     */
    List<AdminOperationLog> exportOperationLogs(String operationType, String timeRange);

    /**
     * 导出内容审核日志
     */
    List<ContentAuditLog> exportContentAuditLogs(String contentType, String action, String timeRange);

    /**
     * 导出系统统计数据
     */
    String exportSystemStats(String timeRange);

    // ==================== 数据维护 ====================

    /**
     * 清理过期数据
     */
    CleanupResult cleanupExpiredData(Integer days);

    /**
     * 重建索引
     */
    void rebuildIndexes();

    /**
     * 数据备份
     */
    String backupData();

    /**
     * 数据恢复
     */
    void restoreData(String backupFile);

    // ==================== 权限验证 ====================

    /**
     * 检查管理员权限
     * TODO: 实现权限检查逻辑
     */
    // boolean hasPermission(Long adminId, String permission);

    /**
     * 获取管理员权限列表
     * TODO: 实现权限获取逻辑
     */
    // List<String> getAdminPermissions(Long adminId);

    /**
     * 分配权限
     */
    void assignPermissions(Long adminId, Long targetUserId, List<String> permissions);

    // ==================== 内部类定义 ====================

    /**
     * 管理员登录结果类
     */
    class AdminLoginResult {
        private String token;
        private AdminInfo adminInfo;
        private Long expiresIn;

        public AdminLoginResult(String token, AdminInfo adminInfo, Long expiresIn) {
            this.token = token;
            this.adminInfo = adminInfo;
            this.expiresIn = expiresIn;
        }

        public String getToken() { return token; }
        public AdminInfo getAdminInfo() { return adminInfo; }
        public Long getExpiresIn() { return expiresIn; }
    }

    /**
     * 管理员信息类
     */
    class AdminInfo {
        private Long id;
        private String username;
        private String nickname;
        private String avatar;
        private String role;
        private List<String> permissions;
        private java.time.LocalDateTime lastLoginTime;

        public AdminInfo(Long id, String username, String nickname, String avatar, String role, 
                        List<String> permissions, java.time.LocalDateTime lastLoginTime) {
            this.id = id;
            this.username = username;
            this.nickname = nickname;
            this.avatar = avatar;
            this.role = role;
            this.permissions = permissions;
            this.lastLoginTime = lastLoginTime;
        }

        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public String getAvatar() { return avatar; }
        public String getRole() { return role; }
        public List<String> getPermissions() { return permissions; }
        public java.time.LocalDateTime getLastLoginTime() { return lastLoginTime; }
    }

    /**
     * 待审核内容列表类
     */
    class PendingContentList {
        private List<PendingContent> posts;
        private List<PendingContent> activities;
        private List<PendingContent> users;
        private List<PendingContent> fields;

        public PendingContentList(List<PendingContent> posts, List<PendingContent> activities, 
                                 List<PendingContent> users, List<PendingContent> fields) {
            this.posts = posts;
            this.activities = activities;
            this.users = users;
            this.fields = fields;
        }

        public List<PendingContent> getPosts() { return posts; }
        public List<PendingContent> getActivities() { return activities; }
        public List<PendingContent> getUsers() { return users; }
        public List<PendingContent> getFields() { return fields; }
        
        public Integer getTotal() {
            int total = 0;
            if (posts != null) total += posts.size();
            if (activities != null) total += activities.size();
            if (users != null) total += users.size();
            if (fields != null) total += fields.size();
            return total;
        }
    }

    /**
     * 待审核内容类
     */
    class PendingContent {
        private Long id;
        private String title;
        private String description;
        private String authorUsername;
        private String authorNickname;
        private String status;
        private java.time.LocalDateTime createdAt;

        public PendingContent(Long id, String title, String description, String authorUsername, 
                            String authorNickname, String status, java.time.LocalDateTime createdAt) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.authorUsername = authorUsername;
            this.authorNickname = authorNickname;
            this.status = status;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getAuthorUsername() { return authorUsername; }
        public String getAuthorNickname() { return authorNickname; }
        public String getStatus() { return status; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    }

    /**
     * 内容审核请求类
     */
    class ContentAuditRequest {
        private String contentType;
        private Long contentId;
        private String action;
        private String reason;

        public ContentAuditRequest(String contentType, Long contentId, String action, String reason) {
            this.contentType = contentType;
            this.contentId = contentId;
            this.action = action;
            this.reason = reason;
        }

        public String getContentType() { return contentType; }
        public Long getContentId() { return contentId; }
        public String getAction() { return action; }
        public String getReason() { return reason; }
    }

    /**
     * 系统统计概览类
     */
    class SystemStats {
        private Long totalUsers;
        private Long totalPosts;
        private Long totalActivities;
        private Long totalFields;
        private Long totalComments;
        private Long todayNewUsers;
        private Long todayNewPosts;
        private Long todayNewActivities;
        private Long activeUsers;
        private Long bannedUsers;
        private Long pendingAudits;

        public SystemStats(Long totalUsers, Long totalPosts, Long totalActivities, Long totalFields,
                          Long totalComments, Long todayNewUsers, Long todayNewPosts, Long todayNewActivities,
                          Long activeUsers, Long bannedUsers, Long pendingAudits) {
            this.totalUsers = totalUsers;
            this.totalPosts = totalPosts;
            this.totalActivities = totalActivities;
            this.totalFields = totalFields;
            this.totalComments = totalComments;
            this.todayNewUsers = todayNewUsers;
            this.todayNewPosts = todayNewPosts;
            this.todayNewActivities = todayNewActivities;
            this.activeUsers = activeUsers;
            this.bannedUsers = bannedUsers;
            this.pendingAudits = pendingAudits;
        }

        // Getters
        public Long getTotalUsers() { return totalUsers; }
        public Long getTotalPosts() { return totalPosts; }
        public Long getTotalActivities() { return totalActivities; }
        public Long getTotalFields() { return totalFields; }
        public Long getTotalComments() { return totalComments; }
        public Long getTodayNewUsers() { return todayNewUsers; }
        public Long getTodayNewPosts() { return todayNewPosts; }
        public Long getTodayNewActivities() { return todayNewActivities; }
        public Long getActiveUsers() { return activeUsers; }
        public Long getBannedUsers() { return bannedUsers; }
        public Long getPendingAudits() { return pendingAudits; }
    }

    /**
     * 用户统计类
     */
    class UserStats {
        private Long totalUsers;
        private Long activeUsers;
        private Long newUsers;
        private Long bannedUsers;
        private Long certifiedUsers;
        private List<UserTrend> trends;

        public UserStats(Long totalUsers, Long activeUsers, Long newUsers, Long bannedUsers, 
                        Long certifiedUsers, List<UserTrend> trends) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.newUsers = newUsers;
            this.bannedUsers = bannedUsers;
            this.certifiedUsers = certifiedUsers;
            this.trends = trends;
        }

        public Long getTotalUsers() { return totalUsers; }
        public Long getActiveUsers() { return activeUsers; }
        public Long getNewUsers() { return newUsers; }
        public Long getBannedUsers() { return bannedUsers; }
        public Long getCertifiedUsers() { return certifiedUsers; }
        public List<UserTrend> getTrends() { return trends; }
    }

    /**
     * 用户趋势类
     */
    class UserTrend {
        private String date;
        private Long newUsers;
        private Long activeUsers;

        public UserTrend(String date, Long newUsers, Long activeUsers) {
            this.date = date;
            this.newUsers = newUsers;
            this.activeUsers = activeUsers;
        }

        public String getDate() { return date; }
        public Long getNewUsers() { return newUsers; }
        public Long getActiveUsers() { return activeUsers; }
    }

    /**
     * 内容统计类
     */
    class ContentStats {
        private Long totalPosts;
        private Long totalActivities;
        private Long totalFields;
        private Long totalComments;
        private Long newPosts;
        private Long newActivities;
        private Long newFields;
        private Long newComments;

        public ContentStats(Long totalPosts, Long totalActivities, Long totalFields, Long totalComments,
                           Long newPosts, Long newActivities, Long newFields, Long newComments) {
            this.totalPosts = totalPosts;
            this.totalActivities = totalActivities;
            this.totalFields = totalFields;
            this.totalComments = totalComments;
            this.newPosts = newPosts;
            this.newActivities = newActivities;
            this.newFields = newFields;
            this.newComments = newComments;
        }

        public Long getTotalPosts() { return totalPosts; }
        public Long getTotalActivities() { return totalActivities; }
        public Long getTotalFields() { return totalFields; }
        public Long getTotalComments() { return totalComments; }
        public Long getNewPosts() { return newPosts; }
        public Long getNewActivities() { return newActivities; }
        public Long getNewFields() { return newFields; }
        public Long getNewComments() { return newComments; }
    }

    /**
     * 管理员活动统计类
     */
    class AdminActivityStats {
        private Long totalOperations;
        private Long successfulOperations;
        private Long failedOperations;
        private Long activeAdmins;
        private List<AdminActivity> activities;

        public AdminActivityStats(Long totalOperations, Long successfulOperations, Long failedOperations,
                                 Long activeAdmins, List<AdminActivity> activities) {
            this.totalOperations = totalOperations;
            this.successfulOperations = successfulOperations;
            this.failedOperations = failedOperations;
            this.activeAdmins = activeAdmins;
            this.activities = activities;
        }

        public Long getTotalOperations() { return totalOperations; }
        public Long getSuccessfulOperations() { return successfulOperations; }
        public Long getFailedOperations() { return failedOperations; }
        public Long getActiveAdmins() { return activeAdmins; }
        public List<AdminActivity> getActivities() { return activities; }
    }

    /**
     * 管理员活动类
     */
    class AdminActivity {
        private String adminUsername;
        private String adminNickname;
        private Long operationCount;
        private String lastOperationTime;

        public AdminActivity(String adminUsername, String adminNickname, Long operationCount, String lastOperationTime) {
            this.adminUsername = adminUsername;
            this.adminNickname = adminNickname;
            this.operationCount = operationCount;
            this.lastOperationTime = lastOperationTime;
        }

        public String getAdminUsername() { return adminUsername; }
        public String getAdminNickname() { return adminNickname; }
        public Long getOperationCount() { return operationCount; }
        public String getLastOperationTime() { return lastOperationTime; }
    }

    /**
     * 热门内容统计类
     */
    class HotContentStats {
        private List<HotContent> posts;
        private List<HotContent> activities;
        private List<HotContent> fields;

        public HotContentStats(List<HotContent> posts, List<HotContent> activities, List<HotContent> fields) {
            this.posts = posts;
            this.activities = activities;
            this.fields = fields;
        }

        public List<HotContent> getPosts() { return posts; }
        public List<HotContent> getActivities() { return activities; }
        public List<HotContent> getFields() { return fields; }
    }

    /**
     * 热门内容类
     */
    class HotContent {
        private Long id;
        private String title;
        private String description;
        private String authorUsername;
        private String authorNickname;
        private Long viewCount;
        private Long likeCount;
        private Long commentCount;
        private java.time.LocalDateTime createdAt;

        public HotContent(Long id, String title, String description, String authorUsername, 
                         String authorNickname, Long viewCount, Long likeCount, Long commentCount, 
                         java.time.LocalDateTime createdAt) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.authorUsername = authorUsername;
            this.authorNickname = authorNickname;
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.createdAt = createdAt;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getAuthorUsername() { return authorUsername; }
        public String getAuthorNickname() { return authorNickname; }
        public Long getViewCount() { return viewCount; }
        public Long getLikeCount() { return likeCount; }
        public Long getCommentCount() { return commentCount; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    }

    /**
     * 系统配置类
     */
    class SystemConfig {
        private String siteName;
        private String siteDescription;
        private String siteLogo;
        private String contactEmail;
        private Boolean userRegistrationEnabled;
        private Boolean contentAuditEnabled;
        private Integer maxPostsPerDay;
        private Integer maxActivitiesPerDay;
        private Integer maxFileSize;
        private List<String> allowedFileTypes;

        public SystemConfig(String siteName, String siteDescription, String siteLogo, String contactEmail,
                           Boolean userRegistrationEnabled, Boolean contentAuditEnabled, Integer maxPostsPerDay,
                           Integer maxActivitiesPerDay, Integer maxFileSize, List<String> allowedFileTypes) {
            this.siteName = siteName;
            this.siteDescription = siteDescription;
            this.siteLogo = siteLogo;
            this.contactEmail = contactEmail;
            this.userRegistrationEnabled = userRegistrationEnabled;
            this.contentAuditEnabled = contentAuditEnabled;
            this.maxPostsPerDay = maxPostsPerDay;
            this.maxActivitiesPerDay = maxActivitiesPerDay;
            this.maxFileSize = maxFileSize;
            this.allowedFileTypes = allowedFileTypes;
        }

        // Getters
        public String getSiteName() { return siteName; }
        public String getSiteDescription() { return siteDescription; }
        public String getSiteLogo() { return siteLogo; }
        public String getContactEmail() { return contactEmail; }
        public Boolean getUserRegistrationEnabled() { return userRegistrationEnabled; }
        public Boolean getContentAuditEnabled() { return contentAuditEnabled; }
        public Integer getMaxPostsPerDay() { return maxPostsPerDay; }
        public Integer getMaxActivitiesPerDay() { return maxActivitiesPerDay; }
        public Integer getMaxFileSize() { return maxFileSize; }
        public List<String> getAllowedFileTypes() { return allowedFileTypes; }
    }

    /**
     * 系统状态类
     */
    class SystemStatus {
        private Boolean isHealthy;
        private String version;
        private Long uptime;
        private Integer cpuUsage;
        private Integer memoryUsage;
        private Integer diskUsage;
        private Long databaseConnections;
        private String lastBackupTime;
        private String databaseStatus;
        private String redisStatus;

        public SystemStatus(Boolean isHealthy, String version, Long uptime, Integer cpuUsage,
                           Integer memoryUsage, Integer diskUsage, Long databaseConnections, String lastBackupTime) {
            this.isHealthy = isHealthy;
            this.version = version;
            this.uptime = uptime;
            this.cpuUsage = cpuUsage;
            this.memoryUsage = memoryUsage;
            this.diskUsage = diskUsage;
            this.databaseConnections = databaseConnections;
            this.lastBackupTime = lastBackupTime;
            this.databaseStatus = "healthy"; // 默认值
            this.redisStatus = "healthy"; // 默认值
        }

        public Boolean getIsHealthy() { return isHealthy; }
        public String getVersion() { return version; }
        public Long getUptime() { return uptime; }
        public Integer getCpuUsage() { return cpuUsage; }
        public Integer getMemoryUsage() { return memoryUsage; }
        public Integer getDiskUsage() { return diskUsage; }
        public Long getDatabaseConnections() { return databaseConnections; }
        public String getLastBackupTime() { return lastBackupTime; }
        public String getDatabaseStatus() { return databaseStatus; }
        public String getRedisStatus() { return redisStatus; }
        
        public void setDatabaseStatus(String databaseStatus) { this.databaseStatus = databaseStatus; }
        public void setRedisStatus(String redisStatus) { this.redisStatus = redisStatus; }
    }

    /**
     * 清理结果类
     */
    class CleanupResult {
        private Integer cleanedLogs;
        private Integer cleanedTempFiles;
        private Integer cleanedCache;
        private Long freedSpace;

        public CleanupResult(Integer cleanedLogs, Integer cleanedTempFiles, Integer cleanedCache, Long freedSpace) {
            this.cleanedLogs = cleanedLogs;
            this.cleanedTempFiles = cleanedTempFiles;
            this.cleanedCache = cleanedCache;
            this.freedSpace = freedSpace;
        }

        public Integer getCleanedLogs() { return cleanedLogs; }
        public Integer getCleanedTempFiles() { return cleanedTempFiles; }
        public Integer getCleanedCache() { return cleanedCache; }
        public Long getFreedSpace() { return freedSpace; }
    }
}
