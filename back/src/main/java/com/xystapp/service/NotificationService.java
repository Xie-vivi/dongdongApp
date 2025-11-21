package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Notification;
import com.xystapp.entity.NotificationSetting;

import java.util.List;

/**
 * 通知服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface NotificationService {

    // ==================== 通知创建 ====================

    /**
     * 创建通知
     */
    Notification createNotification(Long userId, String type, String relatedType, 
                                  Long relatedId, Long senderId, String content);

    /**
     * 创建点赞通知
     */
    Notification createLikeNotification(Long userId, Long senderId, String relatedType, Long relatedId);

    /**
     * 创建收藏通知
     */
    Notification createStarNotification(Long userId, Long senderId, String relatedType, Long relatedId);

    /**
     * 创建评论通知
     */
    Notification createCommentNotification(Long userId, Long senderId, Long relatedId, String content);

    /**
     * 创建@用户通知
     */
    Notification createAtNotification(Long userId, Long senderId, Long relatedId, String content);

    /**
     * 创建关注通知
     */
    Notification createFollowNotification(Long userId, Long senderId);

    /**
     * 创建关注用户内容通知
     */
    Notification createFollowContentNotification(Long userId, Long senderId, String relatedType, Long relatedId);

    // ==================== 通知查询 ====================

    /**
     * 获取用户通知列表
     */
    IPage<Notification> getUserNotifications(Long userId, Integer page, Integer size);

    /**
     * 获取未读通知列表
     */
    List<Notification> getUnreadNotifications(Long userId, Integer limit);

    /**
     * 获取通知详情
     */
    Notification getNotificationById(Long notificationId, Long userId);

    /**
     * 获取用户未读通知数量
     */
    Integer getUnreadCount(Long userId);

    /**
     * 获取指定类型的通知
     */
    IPage<Notification> getNotificationsByType(Long userId, String type, Integer page, Integer size);

    // ==================== 通知操作 ====================

    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 批量标记通知为已读
     */
    void markMultipleAsRead(List<Long> notificationIds, Long userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long notificationId, Long userId);

    /**
     * 批量删除通知
     */
    void deleteMultipleNotifications(List<Long> notificationIds, Long userId);

    /**
     * 清空所有通知
     */
    void clearAllNotifications(Long userId);

    // ==================== 通知设置 ====================

    /**
     * 获取用户通知设置
     */
    List<NotificationSetting> getUserNotificationSettings(Long userId);

    /**
     * 获取指定类型的通知设置
     */
    NotificationSetting getNotificationSetting(Long userId, String type);

    /**
     * 更新通知设置
     */
    void updateNotificationSetting(Long userId, String type, Boolean enabled, 
                                  Boolean pushEnabled, Boolean emailEnabled);

    /**
     * 批量更新通知设置
     */
    void updateMultipleNotificationSettings(Long userId, List<NotificationSetting> settings);

    /**
     * 重置为默认通知设置
     */
    void resetToDefaultSettings(Long userId);

    // ==================== 业务验证 ====================

    /**
     * 检查通知是否存在
     */
    boolean notificationExists(Long notificationId);

    /**
     * 检查用户是否是通知接收者
     */
    boolean isNotificationRecipient(Long notificationId, Long userId);

    /**
     * 检查是否应该创建通知（基于用户设置）
     */
    boolean shouldCreateNotification(Long userId, String type);

    /**
     * 检查通知是否已读
     */
    boolean isNotificationRead(Long notificationId);

    // ==================== 批量操作 ====================

    /**
     * 批量获取通知信息
     */
    List<Notification> getNotificationsByIds(List<Long> notificationIds, Long userId);

    /**
     * 获取通知统计
     */
    NotificationStats getNotificationStats(Long userId);

    /**
     * 通知统计类
     */
    class NotificationStats {
        private Long totalCount;
        private Long unreadCount;
        private Long todayCount;
        private Long likeCount;
        private Long commentCount;
        private Long followCount;
        private Long atCount;

        public NotificationStats(Long totalCount, Long unreadCount, Long todayCount, 
                               Long likeCount, Long commentCount, Long followCount, Long atCount) {
            this.totalCount = totalCount;
            this.unreadCount = unreadCount;
            this.todayCount = todayCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
            this.followCount = followCount;
            this.atCount = atCount;
        }

        public Long getTotalCount() { return totalCount; }
        public Long getUnreadCount() { return unreadCount; }
        public Long getTodayCount() { return todayCount; }
        public Long getLikeCount() { return likeCount; }
        public Long getCommentCount() { return commentCount; }
        public Long getFollowCount() { return followCount; }
        public Long getAtCount() { return atCount; }
    }
}
