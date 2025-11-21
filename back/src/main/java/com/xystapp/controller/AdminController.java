package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.*;
import com.xystapp.service.AdminService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 后台管理控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "后台管理")
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    // ==================== 管理员认证 ====================

    /**
     * 管理员登录
     */
    @ApiOperation("管理员登录(旧版)")
    @PostMapping("/auth/login-legacy")
    public Result<AdminService.AdminLoginResult> adminLogin(@ApiParam("用户名") @RequestParam String username,
                                                            @ApiParam("密码") @RequestParam String password,
                                                            HttpServletRequest request) {
        log.info("管理员登录请求: username={}", username);

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        AdminService.AdminLoginResult result = adminService.adminLogin(username, password, ipAddress, userAgent);

        return Result.success(result);
    }

    /**
     * 获取管理员信息
     */
    @ApiOperation("获取管理员信息")
    @GetMapping("/info")
    public Result<AdminService.AdminInfo> getAdminInfo() {
        log.info("获取管理员信息");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        AdminService.AdminInfo adminInfo = adminService.getAdminInfo(currentUserId);

        if (adminInfo == null) {
            return Result.error(404, "管理员信息不存在");
        }

        return Result.success(adminInfo);
    }

    /**
     * 检查管理员权限
     */
    @ApiOperation("检查管理员权限")
    @GetMapping("/permission/check")
    public Result<Boolean> checkPermission(@ApiParam("权限标识") @RequestParam String permission) {
        log.info("检查管理员权限: permission={}", permission);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean hasPermission = adminService.hasPermission(currentUserId, permission);

        return Result.success(hasPermission);
    }

    /**
     * 获取管理员权限列表
     */
    @ApiOperation("获取管理员权限列表")
    @GetMapping("/permissions")
    public Result<List<String>> getAdminPermissions() {
        log.info("获取管理员权限列表");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<String> permissions = adminService.getAdminPermissions(currentUserId);

        return Result.success(permissions);
    }

    // ==================== 用户管理 ====================

    /**
     * 获取用户列表
     */
    @ApiOperation("获取用户列表")
    @GetMapping("/users")
    public Result<IPage<User>> getUserList(@ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                           @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                           @ApiParam("关键词搜索") @RequestParam(required = false) String keyword,
                                           @ApiParam("角色筛选") @RequestParam(required = false) String role,
                                           @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        log.info("获取用户列表: page={}, size={}, keyword={}, role={}, status={}", page, size, keyword, role, status);

        // 权限检查
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "user:view")) {
            return Result.error(403, "无权限查看用户列表");
        }

        IPage<User> result = adminService.getUserList(page, size, keyword, role, status);

        return Result.success(result);
    }

    /**
     * 更新用户角色
     */
    @ApiOperation("更新用户角色")
    @PutMapping("/users/{userId}/role")
    public Result<Void> updateUserRole(@ApiParam("用户ID") @PathVariable Long userId,
                                       @ApiParam("角色") @RequestParam String role) {
        log.info("更新用户角色: userId={}, role={}", userId, role);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "user:manage")) {
            return Result.error(403, "无权限管理用户角色");
        }

        adminService.updateUserRole(currentUserId, userId, role);

        return Result.success();
    }

    /**
     * 封禁用户
     */
    @ApiOperation("封禁用户")
    @PutMapping("/users/{userId}/ban")
    public Result<Void> banUser(@ApiParam("用户ID") @PathVariable Long userId,
                                @ApiParam("封禁理由") @RequestParam(required = false) String reason) {
        log.info("封禁用户: userId={}, reason={}", userId, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "user:ban")) {
            return Result.error(403, "无权限封禁用户");
        }

        adminService.banUser(currentUserId, userId, reason);

        return Result.success();
    }

    /**
     * 解封用户
     */
    @ApiOperation("解封用户")
    @PutMapping("/users/{userId}/unban")
    public Result<Void> unbanUser(@ApiParam("用户ID") @PathVariable Long userId,
                                  @ApiParam("解封理由") @RequestParam(required = false) String reason) {
        log.info("解封用户: userId={}, reason={}", userId, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "user:ban")) {
            return Result.error(403, "无权限解封用户");
        }

        adminService.unbanUser(currentUserId, userId, reason);

        return Result.success();
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @DeleteMapping("/users/{userId}")
    public Result<Void> deleteUser(@ApiParam("用户ID") @PathVariable Long userId,
                                   @ApiParam("删除理由") @RequestParam(required = false) String reason) {
        log.info("删除用户: userId={}, reason={}", userId, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "user:delete")) {
            return Result.error(403, "无权限删除用户");
        }

        adminService.deleteUser(currentUserId, userId, reason);

        return Result.success();
    }

    /**
     * 重置用户密码
     */
    @ApiOperation("重置用户密码")
    @PutMapping("/users/{userId}/password")
    public Result<Void> resetUserPassword(@ApiParam("用户ID") @PathVariable Long userId,
                                          @ApiParam("新密码") @RequestParam String newPassword) {
        log.info("重置用户密码: userId={}", userId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "user:manage")) {
            return Result.error(403, "无权限管理用户");
        }

        adminService.resetUserPassword(currentUserId, userId, newPassword);

        return Result.success();
    }

    /**
     * 分配权限
     */
    @ApiOperation("分配权限")
    @PutMapping("/users/{userId}/permissions")
    public Result<Void> assignPermissions(@ApiParam("用户ID") @PathVariable Long userId,
                                          @ApiParam("权限列表") @RequestBody List<String> permissions) {
        log.info("分配权限: userId={}, permissions={}", userId, permissions);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "admin:manage")) {
            return Result.error(403, "无权限分配权限");
        }

        adminService.assignPermissions(currentUserId, userId, permissions);

        return Result.success();
    }

    // ==================== 内容审核 ====================

    /**
     * 获取待审核内容列表
     */
    @ApiOperation("获取待审核内容列表")
    @GetMapping("/content/pending")
    public Result<AdminService.PendingContentList> getPendingContentList(@ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                                          @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                                          @ApiParam("内容类型") @RequestParam(required = false) String contentType) {
        log.info("获取待审核内容列表: page={}, size={}, contentType={}", page, size, contentType);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "content:audit")) {
            return Result.error(403, "无权限审核内容");
        }

        AdminService.PendingContentList result = adminService.getPendingContentList(page, size, contentType);

        return Result.success(result);
    }

    /**
     * 审核帖子
     */
    @ApiOperation("审核帖子")
    @PutMapping("/posts/{postId}/audit")
    public Result<Void> auditPost(@ApiParam("帖子ID") @PathVariable Long postId,
                                  @ApiParam("审核动作") @RequestParam String action,
                                  @ApiParam("审核理由") @RequestParam(required = false) String reason) {
        log.info("审核帖子: postId={}, action={}, reason={}", postId, action, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "content:audit")) {
            return Result.error(403, "无权限审核帖子");
        }

        adminService.auditPost(currentUserId, postId, action, reason);

        return Result.success();
    }

    /**
     * 审核活动
     */
    @ApiOperation("审核活动")
    @PutMapping("/activities/{activityId}/audit")
    public Result<Void> auditActivity(@ApiParam("活动ID") @PathVariable Long activityId,
                                      @ApiParam("审核动作") @RequestParam String action,
                                      @ApiParam("审核理由") @RequestParam(required = false) String reason) {
        log.info("审核活动: activityId={}, action={}, reason={}", activityId, action, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "content:audit")) {
            return Result.error(403, "无权限审核活动");
        }

        adminService.auditActivity(currentUserId, activityId, action, reason);

        return Result.success();
    }

    /**
     * 审核用户
     */
    @ApiOperation("审核用户")
    @PutMapping("/users/{userId}/audit")
    public Result<Void> auditUser(@ApiParam("用户ID") @PathVariable Long userId,
                                  @ApiParam("审核动作") @RequestParam String action,
                                  @ApiParam("审核理由") @RequestParam(required = false) String reason) {
        log.info("审核用户: userId={}, action={}, reason={}", userId, action, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "content:audit")) {
            return Result.error(403, "无权限审核用户");
        }

        adminService.auditUser(currentUserId, userId, action, reason);

        return Result.success();
    }

    /**
     * 审核场地
     */
    @ApiOperation("审核场地")
    @PutMapping("/fields/{fieldId}/audit")
    public Result<Void> auditField(@ApiParam("场地ID") @PathVariable Long fieldId,
                                   @ApiParam("审核动作") @RequestParam String action,
                                   @ApiParam("审核理由") @RequestParam(required = false) String reason) {
        log.info("审核场地: fieldId={}, action={}, reason={}", fieldId, action, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "content:audit")) {
            return Result.error(403, "无权限审核场地");
        }

        adminService.auditField(currentUserId, fieldId, action, reason);

        return Result.success();
    }

    /**
     * 批量审核内容
     */
    @ApiOperation("批量审核内容")
    @PostMapping("/content/batch-audit")
    public Result<Void> batchAuditContent(@ApiParam("审核请求列表") @RequestBody List<AdminService.ContentAuditRequest> requests) {
        log.info("批量审核内容: requestCount={}", requests.size());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "content:audit")) {
            return Result.error(403, "无权限审核内容");
        }

        adminService.batchAuditContent(currentUserId, requests);

        return Result.success();
    }

    // ==================== 操作日志管理 ====================

    /**
     * 获取操作日志列表
     */
    @ApiOperation("获取操作日志列表")
    @GetMapping("/logs/operations")
    public Result<IPage<AdminOperationLog>> getOperationLogs(@ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                             @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                             @ApiParam("操作类型") @RequestParam(required = false) String operationType,
                                                             @ApiParam("目标类型") @RequestParam(required = false) String targetType,
                                                             @ApiParam("管理员ID") @RequestParam(required = false) String adminId,
                                                             @ApiParam("时间范围") @RequestParam(required = false) String timeRange) {
        log.info("获取操作日志列表: page={}, size={}, operationType={}, targetType={}, adminId={}, timeRange={}", 
                page, size, operationType, targetType, adminId, timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "log:view")) {
            return Result.error(403, "无权限查看操作日志");
        }

        IPage<AdminOperationLog> result = adminService.getOperationLogs(page, size, operationType, targetType, adminId, timeRange);

        return Result.success(result);
    }

    /**
     * 获取操作日志详情
     */
    @ApiOperation("获取操作日志详情")
    @GetMapping("/logs/operations/{logId}")
    public Result<AdminOperationLog> getOperationLogDetail(@ApiParam("日志ID") @PathVariable Long logId) {
        log.info("获取操作日志详情: logId={}", logId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "log:view")) {
            return Result.error(403, "无权限查看操作日志");
        }

        AdminOperationLog result = adminService.getOperationLogDetail(logId);

        if (result == null) {
            return Result.error(404, "操作日志不存在");
        }

        return Result.success(result);
    }

    /**
     * 清理过期操作日志
     */
    @ApiOperation("清理过期操作日志")
    @PostMapping("/logs/operations/cleanup")
    public Result<Integer> cleanupOperationLogs(@ApiParam("天数") @RequestParam(defaultValue = "90") Integer days) {
        log.info("清理过期操作日志: days={}", days);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:maintain")) {
            return Result.error(403, "无权限维护系统");
        }

        int result = adminService.cleanupOperationLogs(days);

        return Result.success(result);
    }

    // ==================== 内容审核日志管理 ====================

    /**
     * 获取内容审核日志列表
     */
    @ApiOperation("获取内容审核日志列表")
    @GetMapping("/logs/audits")
    public Result<IPage<ContentAuditLog>> getContentAuditLogs(@ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                              @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                              @ApiParam("内容类型") @RequestParam(required = false) String contentType,
                                                              @ApiParam("审核动作") @RequestParam(required = false) String action,
                                                              @ApiParam("管理员ID") @RequestParam(required = false) String adminId,
                                                              @ApiParam("时间范围") @RequestParam(required = false) String timeRange) {
        log.info("获取内容审核日志列表: page={}, size={}, contentType={}, action={}, adminId={}, timeRange={}", 
                page, size, contentType, action, adminId, timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "log:view")) {
            return Result.error(403, "无权限查看审核日志");
        }

        IPage<ContentAuditLog> result = adminService.getContentAuditLogs(page, size, contentType, action, adminId, timeRange);

        return Result.success(result);
    }

    /**
     * 获取内容审核日志详情
     */
    @ApiOperation("获取内容审核日志详情")
    @GetMapping("/logs/audits/{logId}")
    public Result<ContentAuditLog> getContentAuditLogDetail(@ApiParam("日志ID") @PathVariable Long logId) {
        log.info("获取内容审核日志详情: logId={}", logId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "log:view")) {
            return Result.error(403, "无权限查看审核日志");
        }

        ContentAuditLog result = adminService.getContentAuditLogDetail(logId);

        if (result == null) {
            return Result.error(404, "审核日志不存在");
        }

        return Result.success(result);
    }

    // ==================== 数据统计 ====================

    /**
     * 获取系统统计概览
     */
    @ApiOperation("获取系统统计概览")
    @GetMapping("/stats/system")
    public Result<AdminService.SystemStats> getSystemStats() {
        log.info("获取系统统计概览");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "stats:view")) {
            return Result.error(403, "无权限查看统计数据");
        }

        AdminService.SystemStats result = adminService.getSystemStats();

        return Result.success(result);
    }

    /**
     * 获取用户统计
     */
    @ApiOperation("获取用户统计")
    @GetMapping("/stats/users")
    public Result<AdminService.UserStats> getUserStats(@ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        log.info("获取用户统计: timeRange={}", timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "stats:view")) {
            return Result.error(403, "无权限查看统计数据");
        }

        AdminService.UserStats result = adminService.getUserStats(timeRange);

        return Result.success(result);
    }

    /**
     * 获取内容统计
     */
    @ApiOperation("获取内容统计")
    @GetMapping("/stats/content")
    public Result<AdminService.ContentStats> getContentStats(@ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        log.info("获取内容统计: timeRange={}", timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "stats:view")) {
            return Result.error(403, "无权限查看统计数据");
        }

        AdminService.ContentStats result = adminService.getContentStats(timeRange);

        return Result.success(result);
    }

    /**
     * 获取管理员活动统计
     */
    @ApiOperation("获取管理员活动统计")
    @GetMapping("/stats/admin")
    public Result<AdminService.AdminActivityStats> getAdminActivityStats(@ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        log.info("获取管理员活动统计: timeRange={}", timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "stats:view")) {
            return Result.error(403, "无权限查看统计数据");
        }

        AdminService.AdminActivityStats result = adminService.getAdminActivityStats(timeRange);

        return Result.success(result);
    }

    /**
     * 获取热门内容统计
     */
    @ApiOperation("获取热门内容统计")
    @GetMapping("/stats/hot")
    public Result<AdminService.HotContentStats> getHotContentStats(@ApiParam("内容类型") @RequestParam(required = false) String contentType,
                                                                   @ApiParam("限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("获取热门内容统计: contentType={}, limit={}", contentType, limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "stats:view")) {
            return Result.error(403, "无权限查看统计数据");
        }

        AdminService.HotContentStats result = adminService.getHotContentStats(contentType, limit);

        return Result.success(result);
    }

    // ==================== 系统配置 ====================

    /**
     * 获取系统配置
     */
    @ApiOperation("获取系统配置")
    @GetMapping("/system/config")
    public Result<AdminService.SystemConfig> getSystemConfig() {
        log.info("获取系统配置");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:config")) {
            return Result.error(403, "无权限管理系统配置");
        }

        AdminService.SystemConfig result = adminService.getSystemConfig();

        return Result.success(result);
    }

    /**
     * 更新系统配置
     */
    @ApiOperation("更新系统配置")
    @PutMapping("/system/config")
    public Result<Void> updateSystemConfig(@ApiParam("系统配置") @RequestBody AdminService.SystemConfig config) {
        log.info("更新系统配置");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:config")) {
            return Result.error(403, "无权限管理系统配置");
        }

        adminService.updateSystemConfig(currentUserId, config);

        return Result.success();
    }

    /**
     * 获取系统状态
     */
    @ApiOperation("获取系统状态")
    @GetMapping("/system/status")
    public Result<AdminService.SystemStatus> getSystemStatus() {
        log.info("获取系统状态");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:monitor")) {
            return Result.error(403, "无权限监控系统状态");
        }

        AdminService.SystemStatus result = adminService.getSystemStatus();

        return Result.success(result);
    }

    // ==================== 数据导出 ====================

    /**
     * 导出用户数据
     */
    @ApiOperation("导出用户数据")
    @GetMapping("/export/users")
    public Result<List<User>> exportUsers(@ApiParam("角色筛选") @RequestParam(required = false) String role,
                                          @ApiParam("状态筛选") @RequestParam(required = false) String status,
                                          @ApiParam("时间范围") @RequestParam(required = false) String timeRange) {
        log.info("导出用户数据: role={}, status={}, timeRange={}", role, status, timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "data:export")) {
            return Result.error(403, "无权限导出数据");
        }

        List<User> result = adminService.exportUsers(role, status, timeRange);

        return Result.success(result);
    }

    /**
     * 导出操作日志
     */
    @ApiOperation("导出操作日志")
    @GetMapping("/export/logs/operations")
    public Result<List<AdminOperationLog>> exportOperationLogs(@ApiParam("操作类型") @RequestParam(required = false) String operationType,
                                                                @ApiParam("时间范围") @RequestParam(required = false) String timeRange) {
        log.info("导出操作日志: operationType={}, timeRange={}", operationType, timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "data:export")) {
            return Result.error(403, "无权限导出数据");
        }

        List<AdminOperationLog> result = adminService.exportOperationLogs(operationType, timeRange);

        return Result.success(result);
    }

    /**
     * 导出内容审核日志
     */
    @ApiOperation("导出内容审核日志")
    @GetMapping("/export/logs/audits")
    public Result<List<ContentAuditLog>> exportContentAuditLogs(@ApiParam("内容类型") @RequestParam(required = false) String contentType,
                                                                 @ApiParam("审核动作") @RequestParam(required = false) String action,
                                                                 @ApiParam("时间范围") @RequestParam(required = false) String timeRange) {
        log.info("导出内容审核日志: contentType={}, action={}, timeRange={}", contentType, action, timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "data:export")) {
            return Result.error(403, "无权限导出数据");
        }

        List<ContentAuditLog> result = adminService.exportContentAuditLogs(contentType, action, timeRange);

        return Result.success(result);
    }

    /**
     * 导出系统统计数据
     */
    @ApiOperation("导出系统统计数据")
    @GetMapping("/export/stats/system")
    public Result<String> exportSystemStats(@ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        log.info("导出系统统计数据: timeRange={}", timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "data:export")) {
            return Result.error(403, "无权限导出数据");
        }

        String result = adminService.exportSystemStats(timeRange);

        return Result.success(result);
    }

    // ==================== 数据维护 ====================

    /**
     * 清理过期数据
     */
    @ApiOperation("清理过期数据")
    @PostMapping("/maintenance/cleanup")
    public Result<AdminService.CleanupResult> cleanupExpiredData(@ApiParam("天数") @RequestParam(defaultValue = "90") Integer days) {
        log.info("清理过期数据: days={}", days);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:maintain")) {
            return Result.error(403, "无权限维护系统");
        }

        AdminService.CleanupResult result = adminService.cleanupExpiredData(days);

        return Result.success(result);
    }

    /**
     * 重建索引
     */
    @ApiOperation("重建索引")
    @PostMapping("/maintenance/rebuild-indexes")
    public Result<Void> rebuildIndexes() {
        log.info("重建索引");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:maintain")) {
            return Result.error(403, "无权限维护系统");
        }

        adminService.rebuildIndexes();

        return Result.success();
    }

    /**
     * 数据备份
     */
    @ApiOperation("数据备份")
    @PostMapping("/maintenance/backup")
    public Result<String> backupData() {
        log.info("数据备份");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:backup")) {
            return Result.error(403, "无权限备份数据");
        }

        String result = adminService.backupData();

        return Result.success(result);
    }

    /**
     * 数据恢复
     */
    @ApiOperation("数据恢复")
    @PostMapping("/maintenance/restore")
    public Result<Void> restoreData(@ApiParam("备份文件") @RequestParam String backupFile) {
        log.info("数据恢复: backupFile={}", backupFile);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!adminService.hasPermission(currentUserId, "system:restore")) {
            return Result.error(403, "无权限恢复数据");
        }

        adminService.restoreData(backupFile);

        return Result.success();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
