package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.MessageSendRequest;
import com.xystapp.entity.Message;

import java.util.List;

/**
 * 消息服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface MessageService {

    // ==================== 消息发送 ====================

    /**
     * 发送消息
     */
    Message sendMessage(MessageSendRequest request, Long senderId);

    /**
     * 发送文本消息
     */
    Message sendTextMessage(Long chatId, String content, Long senderId);

    /**
     * 发送图片消息
     */
    Message sendImageMessage(Long chatId, String imageUrl, Long senderId);

    /**
     * 撤回消息
     */
    void recallMessage(Long messageId, Long userId);

    /**
     * 删除消息
     */
    void deleteMessage(Long messageId, Long userId);

    // ==================== 消息查询 ====================

    /**
     * 获取聊天消息列表
     */
    IPage<Message> getChatMessages(Long chatId, Long userId, Integer page, Integer size);

    /**
     * 获取消息详情
     */
    Message getMessageById(Long messageId, Long userId);

    /**
     * 获取最新消息
     */
    Message getLatestMessage(Long chatId);

    /**
     * 获取未读消息
     */
    List<Message> getUnreadMessages(Long chatId, Long userId);

    /**
     * 搜索消息
     */
    IPage<Message> searchMessages(Long chatId, Long userId, String keyword, Integer page, Integer size);

    // ==================== 消息状态 ====================

    /**
     * 标记消息已读
     */
    void markMessageAsRead(Long messageId, Long userId);

    /**
     * 标记多条消息已读
     */
    void markMessagesAsRead(List<Long> messageIds, Long userId);

    /**
     * 获取消息已读状态
     */
    boolean isMessageRead(Long messageId, Long userId);

    /**
     * 获取消息已读用户列表
     */
    List<Long> getReadUserIds(Long messageId);

    // ==================== 业务验证 ====================

    /**
     * 检查消息是否存在
     */
    boolean messageExists(Long messageId);

    /**
     * 检查用户是否是消息发送者
     */
    boolean isMessageSender(Long messageId, Long userId);

    /**
     * 检查用户是否有权限查看消息
     */
    boolean canViewMessage(Long messageId, Long userId);

    /**
     * 检查消息是否可以撤回（发送后5分钟内）
     */
    boolean canRecallMessage(Long messageId, Long userId);

    // ==================== 批量操作 ====================

    /**
     * 批量删除聊天消息
     */
    void deleteChatMessages(Long chatId, Long userId);

    /**
     * 批量获取消息信息
     */
    List<Message> getMessagesByIds(List<Long> messageIds, Long userId);

    /**
     * 获取聊天消息统计
     */
    MessageStats getMessageStats(Long chatId, Long userId);

    /**
     * 消息统计类
     */
    class MessageStats {
        private Long totalCount;
        private Long todayCount;
        private Long unreadCount;
        private Long imageCount;
        private Long textCount;

        public MessageStats(Long totalCount, Long todayCount, Long unreadCount, Long imageCount, Long textCount) {
            this.totalCount = totalCount;
            this.todayCount = todayCount;
            this.unreadCount = unreadCount;
            this.imageCount = imageCount;
            this.textCount = textCount;
        }

        public Long getTotalCount() { return totalCount; }
        public Long getTodayCount() { return todayCount; }
        public Long getUnreadCount() { return unreadCount; }
        public Long getImageCount() { return imageCount; }
        public Long getTextCount() { return textCount; }
    }
}
