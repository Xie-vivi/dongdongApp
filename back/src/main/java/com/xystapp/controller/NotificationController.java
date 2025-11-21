package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.Notification;
import com.xystapp.entity.NotificationSetting;
import com.xystapp.service.NotificationService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 通知控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "通知管理")
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    // ==================== 通知查询 ====================

    /**
     * 获取用户通知列表
     */
    @ApiOperation("获取用户通知列表")
    @GetMapping("/list")
    public Result<IPage<Notification>> getUserNotifications(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户通知列表: page={}, size={}", page, size);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<Notification> result = notificationService.getUserNotifications(currentUserId, page, size);

        return Result.success(result);
    }

    /**
     * 获取未读通知列表
     */
    @ApiOperation("获取未读通知列表")
    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取未读通知列表: limit={}", limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Notification> notifications = notificationService.getUnreadNotifications(currentUserId, limit);

        return Result.success(notifications);
    }

    /**
     * 获取通知详情
     */
    @ApiOperation("获取通知详情")
    @GetMapping("/{notificationId}")
    public Result<Notification> getNotificationById(@ApiParam("通知ID") @PathVariable Long notificationId) {
        log.info("获取通知详情: notificationId={}", notificationId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Notification notification = notificationService.getNotificationById(notificationId, currentUserId);

        if (notification == null) {
            return Result.error(404, "通知不存在");
        }

        return Result.success(notification);
    }

    /**
     * 获取用户未读通知数量
     */
    @ApiOperation("获取用户未读通知数量")
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        log.info("获取用户未读通知数量");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer unreadCount = notificationService.getUnreadCount(currentUserId);

        return Result.success(unreadCount);
    }

    /**
     * 获取指定类型的通知
     */
    @ApiOperation("获取指定类型的通知")
    @GetMapping("/type/{type}")
    public Result<IPage<Notification>> getNotificationsByType(
            @ApiParam("通知类型") @PathVariable String type,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取指定类型的通知: type={}", type);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<Notification> result = notificationService.getNotificationsByType(currentUserId, type, page, size);

        return Result.success(result);
    }

    /**
     * 获取通知统计
     */
    @ApiOperation("获取通知统计")
    @GetMapping("/stats")
    public Result<NotificationService.NotificationStats> getNotificationStats() {
        log.info("获取通知统计");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        NotificationService.NotificationStats stats = notificationService.getNotificationStats(currentUserId);

        return Result.success(stats);
    }

    // ==================== 通知操作 ====================

    /**
     * 标记通知为已读
     */
    @ApiOperation("标记通知为已读")
    @PostMapping("/{notificationId}/read")
    public Result<Void> markAsRead(@ApiParam("通知ID") @PathVariable Long notificationId) {
        log.info("标记通知为已读: notificationId={}", notificationId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(notificationId, currentUserId);

        return Result.success();
    }

    /**
     * 批量标记通知为已读
     */
    @ApiOperation("批量标记通知为已读")
    @PostMapping("/batch-read")
    public Result<Void> markMultipleAsRead(@ApiParam("通知ID列表") @RequestBody List<Long> notificationIds) {
        log.info("批量标记通知为已读: notificationIds={}", notificationIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.markMultipleAsRead(notificationIds, currentUserId);

        return Result.success();
    }

    /**
     * 标记所有通知为已读
     */
    @ApiOperation("标记所有通知为已读")
    @PostMapping("/read-all")
    public Result<Void> markAllAsRead() {
        log.info("标记所有通知为已读");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(currentUserId);

        return Result.success();
    }

    /**
     * 删除通知
     */
    @ApiOperation("删除通知")
    @DeleteMapping("/{notificationId}")
    public Result<Void> deleteNotification(@ApiParam("通知ID") @PathVariable Long notificationId) {
        log.info("删除通知: notificationId={}", notificationId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.deleteNotification(notificationId, currentUserId);

        return Result.success();
    }

    /**
     * 批量删除通知
     */
    @ApiOperation("批量删除通知")
    @DeleteMapping("/batch")
    public Result<Void> deleteMultipleNotifications(@ApiParam("通知ID列表") @RequestBody List<Long> notificationIds) {
        log.info("批量删除通知: notificationIds={}", notificationIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.deleteMultipleNotifications(notificationIds, currentUserId);

        return Result.success();
    }

    /**
     * 清空所有通知
     */
    @ApiOperation("清空所有通知")
    @DeleteMapping("/clear")
    public Result<Void> clearAllNotifications() {
        log.info("清空所有通知");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.clearAllNotifications(currentUserId);

        return Result.success();
    }

    // ==================== 通知设置 ====================

    /**
     * 获取用户通知设置
     */
    @ApiOperation("获取用户通知设置")
    @GetMapping("/settings")
    public Result<List<NotificationSetting>> getUserNotificationSettings() {
        log.info("获取用户通知设置");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<NotificationSetting> settings = notificationService.getUserNotificationSettings(currentUserId);

        return Result.success(settings);
    }

    /**
     * 获取指定类型的通知设置
     */
    @ApiOperation("获取指定类型的通知设置")
    @GetMapping("/settings/{type}")
    public Result<NotificationSetting> getNotificationSetting(@ApiParam("通知类型") @PathVariable String type) {
        log.info("获取指定类型的通知设置: type={}", type);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        NotificationSetting setting = notificationService.getNotificationSetting(currentUserId, type);

        return Result.success(setting);
    }

    /**
     * 更新通知设置
     */
    @ApiOperation("更新通知设置")
    @PutMapping("/settings/{type}")
    public Result<Void> updateNotificationSetting(
            @ApiParam("通知类型") @PathVariable String type,
            @ApiParam("启用通知") @RequestParam(required = false) Boolean enabled,
            @ApiParam("启用推送") @RequestParam(required = false) Boolean pushEnabled,
            @ApiParam("启用邮件") @RequestParam(required = false) Boolean emailEnabled) {
        log.info("更新通知设置: type={}, enabled={}, pushEnabled={}, emailEnabled={}", 
                type, enabled, pushEnabled, emailEnabled);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.updateNotificationSetting(currentUserId, type, enabled, pushEnabled, emailEnabled);

        return Result.success();
    }

    /**
     * 批量更新通知设置
     */
    @ApiOperation("批量更新通知设置")
    @PutMapping("/settings/batch")
    public Result<Void> updateMultipleNotificationSettings(@ApiParam("通知设置列表") @RequestBody List<NotificationSetting> settings) {
        log.info("批量更新通知设置: settings数量={}", settings.size());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.updateMultipleNotificationSettings(currentUserId, settings);

        return Result.success();
    }

    /**
     * 重置为默认通知设置
     */
    @ApiOperation("重置为默认通知设置")
    @PostMapping("/settings/reset")
    public Result<Void> resetToDefaultSettings() {
        log.info("重置为默认通知设置");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        notificationService.resetToDefaultSettings(currentUserId);

        return Result.success();
    }

    // ==================== 业务验证 ====================

    /**
     * 检查通知是否存在
     */
    @ApiOperation("检查通知是否存在")
    @GetMapping("/{notificationId}/exists")
    public Result<Boolean> notificationExists(@ApiParam("通知ID") @PathVariable Long notificationId) {
        log.info("检查通知是否存在: notificationId={}", notificationId);

        boolean exists = notificationService.notificationExists(notificationId);
        return Result.success(exists);
    }

    /**
     * 检查用户是否是通知接收者
     */
    @ApiOperation("检查用户是否是通知接收者")
    @GetMapping("/{notificationId}/is-recipient")
    public Result<Boolean> isNotificationRecipient(@ApiParam("通知ID") @PathVariable Long notificationId) {
        log.info("检查用户是否是通知接收者: notificationId={}", notificationId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isRecipient = notificationService.isNotificationRecipient(notificationId, currentUserId);

        return Result.success(isRecipient);
    }

    /**
     * 检查通知是否已读
     */
    @ApiOperation("检查通知是否已读")
    @GetMapping("/{notificationId}/is-read")
    public Result<Boolean> isNotificationRead(@ApiParam("通知ID") @PathVariable Long notificationId) {
        log.info("检查通知是否已读: notificationId={}", notificationId);

        boolean isRead = notificationService.isNotificationRead(notificationId);
        return Result.success(isRead);
    }

    /**
     * 检查是否应该创建通知
     */
    @ApiOperation("检查是否应该创建通知")
    @GetMapping("/should-create/{type}")
    public Result<Boolean> shouldCreateNotification(@ApiParam("通知类型") @PathVariable String type) {
        log.info("检查是否应该创建通知: type={}", type);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean shouldCreate = notificationService.shouldCreateNotification(currentUserId, type);

        return Result.success(shouldCreate);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量获取通知信息
     */
    @ApiOperation("批量获取通知信息")
    @PostMapping("/batch")
    public Result<List<Notification>> getNotificationsByIds(@ApiParam("通知ID列表") @RequestBody List<Long> notificationIds) {
        log.info("批量获取通知信息: notificationIds={}", notificationIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Notification> notifications = notificationService.getNotificationsByIds(notificationIds, currentUserId);

        return Result.success(notifications);
    }

    // ==================== 通知创建 ====================
    // 注意：通知创建应该由内部服务调用，通过事件驱动机制触发
    // 例如：LikeService、CommentService、FollowService等在相关操作时发布事件
    // NotificationService监听这些事件并创建相应的通知
    // 不对外暴露通知创建接口，防止恶意调用和垃圾通知
}
