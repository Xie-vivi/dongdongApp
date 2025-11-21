package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {

    /**
     * 获取通知详情（包含用户信息）
     */
    Notification selectNotificationWithUserInfo(@Param("notificationId") Long notificationId);

    /**
     * 获取用户通知列表（包含用户信息）
     */
    IPage<Notification> selectUserNotifications(Page<Notification> page, @Param("userId") Long userId);

    /**
     * 获取未读通知列表
     */
    List<Notification> selectUnreadNotifications(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取指定类型的通知
     */
    IPage<Notification> selectNotificationsByType(Page<Notification> page, @Param("userId") Long userId, @Param("type") String type);

    /**
     * 批量获取通知信息
     */
    List<Notification> selectNotificationsByIds(@Param("notificationIds") List<Long> notificationIds, @Param("userId") Long userId);

    /**
     * 标记通知为已读
     */
    int markAsRead(@Param("notificationId") Long notificationId);

    /**
     * 批量标记通知为已读
     */
    int markMultipleAsRead(@Param("notificationIds") List<Long> notificationIds, @Param("userId") Long userId);

    /**
     * 标记所有通知为已读
     */
    int markAllAsRead(@Param("userId") Long userId);

    /**
     * 批量删除通知
     */
    int deleteMultipleNotifications(@Param("notificationIds") List<Long> notificationIds, @Param("userId") Long userId);

    /**
     * 删除用户所有通知
     */
    int deleteAllByUserId(@Param("userId") Long userId);

    // ==================== 统计相关 ====================

    /**
     * 统计用户通知总数
     */
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户未读通知数量
     */
    Integer selectUnreadCount(@Param("userId") Long userId);

    /**
     * 统计用户今日通知数量
     */
    Long countTodayByUserId(@Param("userId") Long userId);

    /**
     * 统计用户指定类型的通知数量
     */
    Long countByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 统计用户未读的指定类型通知数量
     */
    Long countUnreadByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    // ==================== 业务查询 ====================

    /**
     * 获取用户最近的通知
     */
    List<Notification> selectRecentNotifications(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取指定时间范围内的通知
     */
    List<Notification> selectNotificationsByTimeRange(@Param("userId") Long userId, 
                                                      @Param("startTime") java.time.LocalDateTime startTime,
                                                      @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 获取用户发送的通知列表
     */
    IPage<Notification> selectSentNotifications(Page<Notification> page, @Param("senderId") Long senderId);

    /**
     * 获取关联内容的通知
     */
    List<Notification> selectNotificationsByRelated(@Param("relatedType") String relatedType, @Param("relatedId") Long relatedId);

    /**
     * 检查通知是否存在
     */
    boolean notificationExists(@Param("notificationId") Long notificationId);

    /**
     * 检查用户是否是通知接收者
     */
    boolean isNotificationRecipient(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    /**
     * 获取通知创建时间
     */
    java.time.LocalDateTime selectCreatedAt(@Param("notificationId") Long notificationId);

    /**
     * 删除过期的通知
     */
    int deleteExpiredNotifications(@Param("expireDays") Integer expireDays);

    /**
     * 清理已读的旧通知
     */
    int cleanupOldReadNotifications(@Param("userId") Long userId, @Param("keepDays") Integer keepDays);
}
