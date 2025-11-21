package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Notification;
import com.xystapp.entity.NotificationSetting;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.NotificationMapper;
import com.xystapp.mapper.NotificationSettingMapper;
import com.xystapp.mapper.PostMapper;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 通知服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private NotificationSettingMapper notificationSettingMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createNotification(Long userId, String type, String relatedType, 
                                         Long relatedId, Long senderId, String content) {
        log.info("创建通知: userId={}, type={}, relatedType={}, relatedId={}, senderId={}", 
                userId, type, relatedType, relatedId, senderId);

        // 验证参数
        if (userId == null || type == null || senderId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否存在
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException(404, "接收用户不存在");
        }

        // 检查发送者是否存在
        if (userMapper.selectById(senderId) == null) {
            throw new BusinessException(404, "发送用户不存在");
        }

        // 检查是否应该创建通知
        if (!shouldCreateNotification(userId, type)) {
            log.info("用户禁用了该类型通知: userId={}, type={}", userId, type);
            return null;
        }

        // 检查是否给自己发通知
        if (userId.equals(senderId)) {
            log.info("不能给自己发送通知");
            return null;
        }

        // 验证关联内容是否存在
        if (relatedType != null && relatedId != null) {
            validateRelatedContent(relatedType, relatedId);
        }

        // 创建通知
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setRelatedType(relatedType);
        notification.setRelatedId(relatedId);
        notification.setSenderId(senderId);
        notification.setContent(content);
        notification.setIsRead(false);

        int result = notificationMapper.insert(notification);
        if (result <= 0) {
            throw new BusinessException(500, "创建通知失败");
        }

        log.info("创建通知成功: notificationId={}", notification.getId());
        return notification;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createLikeNotification(Long userId, Long senderId, String relatedType, Long relatedId) {
        String content = buildLikeNotificationContent(senderId, relatedType, relatedId);
        return createNotification(userId, Notification.Type.LIKE.getCode(), relatedType, relatedId, senderId, content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createStarNotification(Long userId, Long senderId, String relatedType, Long relatedId) {
        String content = buildStarNotificationContent(senderId, relatedType, relatedId);
        return createNotification(userId, Notification.Type.STAR.getCode(), relatedType, relatedId, senderId, content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createCommentNotification(Long userId, Long senderId, Long relatedId, String content) {
        String notificationContent = buildCommentNotificationContent(senderId, relatedId, content);
        return createNotification(userId, Notification.Type.COMMENT.getCode(), 
                                Notification.RelatedType.COMMENT.getCode(), relatedId, senderId, notificationContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createAtNotification(Long userId, Long senderId, Long relatedId, String content) {
        String notificationContent = buildAtNotificationContent(senderId, relatedId, content);
        return createNotification(userId, Notification.Type.AT.getCode(), 
                                Notification.RelatedType.COMMENT.getCode(), relatedId, senderId, notificationContent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createFollowNotification(Long userId, Long senderId) {
        String content = buildFollowNotificationContent(senderId);
        return createNotification(userId, Notification.Type.FOLLOW.getCode(), null, null, senderId, content);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createFollowContentNotification(Long userId, Long senderId, String relatedType, Long relatedId) {
        String type = Notification.RelatedType.POST.getCode().equals(relatedType) ? 
                     Notification.Type.FOLLOW_POST.getCode() : 
                     Notification.Type.FOLLOW_ACTIVITY.getCode();
        String content = buildFollowContentNotificationContent(senderId, relatedType, relatedId);
        return createNotification(userId, type, relatedType, relatedId, senderId, content);
    }

    @Override
    public IPage<Notification> getUserNotifications(Long userId, Integer page, Integer size) {
        log.info("获取用户通知列表: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Notification> pageParam = new Page<>(page, size);
        IPage<Notification> result = notificationMapper.selectUserNotifications(pageParam, userId);

        log.info("获取用户通知列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public List<Notification> getUnreadNotifications(Long userId, Integer limit) {
        if (userId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 10;
        }

        return notificationMapper.selectUnreadNotifications(userId, limit);
    }

    @Override
    public Notification getNotificationById(Long notificationId, Long userId) {
        if (notificationId == null || userId == null) {
            return null;
        }

        // 检查权限
        if (!isNotificationRecipient(notificationId, userId)) {
            throw new BusinessException(403, "无权限查看该通知");
        }

        return notificationMapper.selectNotificationWithUserInfo(notificationId);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        if (userId == null) {
            return 0;
        }

        return notificationMapper.selectUnreadCount(userId);
    }

    @Override
    public IPage<Notification> getNotificationsByType(Long userId, String type, Integer page, Integer size) {
        log.info("获取指定类型通知: userId={}, type={}", userId, type);

        if (userId == null || type == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Notification> pageParam = new Page<>(page, size);
        IPage<Notification> result = notificationMapper.selectNotificationsByType(pageParam, userId, type);

        log.info("获取指定类型通知成功: userId={}, type={}, 总数={}", userId, type, result.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId, Long userId) {
        log.info("标记通知为已读: notificationId={}, userId={}", notificationId, userId);

        if (notificationId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!isNotificationRecipient(notificationId, userId)) {
            throw new BusinessException(403, "无权限操作该通知");
        }

        int result = notificationMapper.markAsRead(notificationId);
        if (result <= 0) {
            throw new BusinessException(500, "标记已读失败");
        }

        log.info("标记通知为已读成功: notificationId={}", notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMultipleAsRead(List<Long> notificationIds, Long userId) {
        log.info("批量标记通知为已读: notificationIds={}, userId={}", notificationIds, userId);

        if (notificationIds == null || notificationIds.isEmpty() || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        int result = notificationMapper.markMultipleAsRead(notificationIds, userId);
        log.info("批量标记通知为已读成功: userId={}, 更新数量={}", userId, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllAsRead(Long userId) {
        log.info("标记所有通知为已读: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        int result = notificationMapper.markAllAsRead(userId);
        log.info("标记所有通知为已读成功: userId={}, 更新数量={}", userId, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotification(Long notificationId, Long userId) {
        log.info("删除通知: notificationId={}, userId={}", notificationId, userId);

        if (notificationId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!isNotificationRecipient(notificationId, userId)) {
            throw new BusinessException(403, "无权限删除该通知");
        }

        int result = notificationMapper.deleteById(notificationId);
        if (result <= 0) {
            throw new BusinessException(500, "删除通知失败");
        }

        log.info("删除通知成功: notificationId={}", notificationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMultipleNotifications(List<Long> notificationIds, Long userId) {
        log.info("批量删除通知: notificationIds={}, userId={}", notificationIds, userId);

        if (notificationIds == null || notificationIds.isEmpty() || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        int result = notificationMapper.deleteMultipleNotifications(notificationIds, userId);
        log.info("批量删除通知成功: userId={}, 删除数量={}", userId, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearAllNotifications(Long userId) {
        log.info("清空所有通知: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        int result = notificationMapper.deleteAllByUserId(userId);
        log.info("清空所有通知成功: userId={}, 删除数量={}", userId, result);
    }

    // ==================== 通知设置 ====================

    @Override
    public List<NotificationSetting> getUserNotificationSettings(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }

        return notificationSettingMapper.selectByUserId(userId);
    }

    @Override
    public NotificationSetting getNotificationSetting(Long userId, String type) {
        if (userId == null || type == null) {
            return null;
        }

        NotificationSetting setting = notificationSettingMapper.selectByUserIdAndType(userId, type);
        if (setting == null) {
            // 返回默认设置
            return createDefaultSetting(userId, type);
        }

        return setting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotificationSetting(Long userId, String type, Boolean enabled, 
                                         Boolean pushEnabled, Boolean emailEnabled) {
        log.info("更新通知设置: userId={}, type={}, enabled={}, pushEnabled={}, emailEnabled={}", 
                userId, type, enabled, pushEnabled, emailEnabled);

        if (userId == null || type == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        NotificationSetting setting = getNotificationSetting(userId, type);
        if (setting.getId() == null) {
            // 创建新设置
            setting.setUserId(userId);
            setting.setType(type);
            setting.setCreatedAt(LocalDateTime.now());
        }

        setting.setEnabled(enabled != null ? enabled : true);
        setting.setPushEnabled(pushEnabled != null ? pushEnabled : true);
        setting.setEmailEnabled(emailEnabled != null ? emailEnabled : false);
        setting.setUpdatedAt(LocalDateTime.now());

        if (setting.getId() == null) {
            notificationSettingMapper.insert(setting);
        } else {
            notificationSettingMapper.updateById(setting);
        }

        log.info("更新通知设置成功: userId={}, type={}", userId, type);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMultipleNotificationSettings(Long userId, List<NotificationSetting> settings) {
        log.info("批量更新通知设置: userId={}, settings数量={}", userId, settings.size());

        if (userId == null || settings == null || settings.isEmpty()) {
            throw new BusinessException(400, "参数不能为空");
        }

        for (NotificationSetting setting : settings) {
            updateNotificationSetting(userId, setting.getType(), setting.getEnabled(), 
                                     setting.getPushEnabled(), setting.getEmailEnabled());
        }

        log.info("批量更新通知设置成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetToDefaultSettings(Long userId) {
        log.info("重置为默认通知设置: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 删除现有设置
        notificationSettingMapper.deleteByUserId(userId);

        // 创建默认设置
        for (NotificationSetting.Type type : NotificationSetting.Type.values()) {
            NotificationSetting defaultSetting = createDefaultSetting(userId, type.getCode());
            notificationSettingMapper.insert(defaultSetting);
        }

        log.info("重置为默认通知设置成功: userId={}", userId);
    }

    // ==================== 业务验证 ====================

    @Override
    public boolean notificationExists(Long notificationId) {
        if (notificationId == null) {
            return false;
        }

        return notificationMapper.selectById(notificationId) != null;
    }

    @Override
    public boolean isNotificationRecipient(Long notificationId, Long userId) {
        if (notificationId == null || userId == null) {
            return false;
        }

        Notification notification = notificationMapper.selectById(notificationId);
        return notification != null && notification.getUserId().equals(userId);
    }

    @Override
    public boolean shouldCreateNotification(Long userId, String type) {
        if (userId == null || type == null) {
            return false;
        }

        NotificationSetting setting = getNotificationSetting(userId, type);
        return setting != null && setting.getEnabled();
    }

    @Override
    public boolean isNotificationRead(Long notificationId) {
        if (notificationId == null) {
            return false;
        }

        Notification notification = notificationMapper.selectById(notificationId);
        return notification != null && notification.getIsRead();
    }

    // ==================== 批量操作 ====================

    @Override
    public List<Notification> getNotificationsByIds(List<Long> notificationIds, Long userId) {
        if (notificationIds == null || notificationIds.isEmpty() || userId == null) {
            return new ArrayList<>();
        }

        return notificationMapper.selectNotificationsByIds(notificationIds, userId);
    }

    @Override
    public NotificationStats getNotificationStats(Long userId) {
        log.info("获取通知统计: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Long totalCount = notificationMapper.countByUserId(userId);
        Long unreadCount = (long) getUnreadCount(userId);
        Long todayCount = notificationMapper.countTodayByUserId(userId);
        Long likeCount = notificationMapper.countByUserIdAndType(userId, Notification.Type.LIKE.getCode());
        Long commentCount = notificationMapper.countByUserIdAndType(userId, Notification.Type.COMMENT.getCode());
        Long followCount = notificationMapper.countByUserIdAndType(userId, Notification.Type.FOLLOW.getCode());
        Long atCount = notificationMapper.countByUserIdAndType(userId, Notification.Type.AT.getCode());

        NotificationStats stats = new NotificationStats(totalCount, unreadCount, todayCount, 
                                                       likeCount, commentCount, followCount, atCount);

        log.info("获取通知统计成功: userId={}, total={}, unread={}, today={}", 
                userId, totalCount, unreadCount, todayCount);

        return stats;
    }

    // ==================== 私有方法 ====================

    /**
     * 验证关联内容是否存在
     */
    private void validateRelatedContent(String relatedType, Long relatedId) {
        if (Notification.RelatedType.POST.getCode().equals(relatedType)) {
            if (postMapper.selectById(relatedId) == null) {
                throw new BusinessException(404, "关联帖子不存在");
            }
        } else if (Notification.RelatedType.ACTIVITY.getCode().equals(relatedType)) {
            if (activityMapper.selectById(relatedId) == null) {
                throw new BusinessException(404, "关联活动不存在");
            }
        } else if (Notification.RelatedType.COMMENT.getCode().equals(relatedType)) {
            // 评论验证需要根据具体实现
        }
    }

    /**
     * 构建点赞通知内容
     */
    private String buildLikeNotificationContent(Long senderId, String relatedType, Long relatedId) {
        // 这里可以根据senderId获取用户信息，构建动态内容
        // 简化实现，实际应该从数据库获取用户名
        return "用户点赞了你的" + getRelatedTypeDescription(relatedType);
    }

    /**
     * 构建收藏通知内容
     */
    private String buildStarNotificationContent(Long senderId, String relatedType, Long relatedId) {
        return "用户收藏了你的" + getRelatedTypeDescription(relatedType);
    }

    /**
     * 构建评论通知内容
     */
    private String buildCommentNotificationContent(Long senderId, Long relatedId, String content) {
        return "用户评论了你的内容：" + (content.length() > 50 ? content.substring(0, 50) + "..." : content);
    }

    /**
     * 构建@用户通知内容
     */
    private String buildAtNotificationContent(Long senderId, Long relatedId, String content) {
        return "用户在评论中@了你：" + (content.length() > 50 ? content.substring(0, 50) + "..." : content);
    }

    /**
     * 构建关注通知内容
     */
    private String buildFollowNotificationContent(Long senderId) {
        return "用户关注了你";
    }

    /**
     * 构建关注内容通知内容
     */
    private String buildFollowContentNotificationContent(Long senderId, String relatedType, Long relatedId) {
        return "你关注的用户发布了新的" + getRelatedTypeDescription(relatedType);
    }

    /**
     * 获取关联类型描述
     */
    private String getRelatedTypeDescription(String relatedType) {
        if (Notification.RelatedType.POST.getCode().equals(relatedType)) {
            return "帖子";
        } else if (Notification.RelatedType.ACTIVITY.getCode().equals(relatedType)) {
            return "活动";
        } else if (Notification.RelatedType.COMMENT.getCode().equals(relatedType)) {
            return "评论";
        }
        return "内容";
    }

    /**
     * 创建默认通知设置
     */
    private NotificationSetting createDefaultSetting(Long userId, String type) {
        NotificationSetting setting = new NotificationSetting();
        setting.setUserId(userId);
        setting.setType(type);
        setting.setEnabled(true);
        setting.setPushEnabled(true);
        setting.setEmailEnabled(false);
        setting.setCreatedAt(LocalDateTime.now());
        setting.setUpdatedAt(LocalDateTime.now());
        return setting;
    }
}
