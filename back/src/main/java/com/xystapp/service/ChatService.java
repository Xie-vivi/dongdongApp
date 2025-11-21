package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Chat;
import com.xystapp.entity.ChatMember;

import java.util.List;

/**
 * 聊天服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface ChatService {

    // ==================== 私聊管理 ====================

    /**
     * 获取或创建私聊
     */
    Chat getOrCreatePrivateChat(Long userId1, Long userId2);

    /**
     * 获取私聊详情
     */
    Chat getPrivateChat(Long chatId, Long userId);

    /**
     * 获取用户的聊天列表
     */
    IPage<Chat> getUserChatList(Long userId, Integer page, Integer size);

    /**
     * 获取聊天成员列表
     */
    List<ChatMember> getChatMembers(Long chatId);

    /**
     * 获取私聊中的对方用户信息
     */
    ChatMember getOtherUserInPrivateChat(Long chatId, Long currentUserId);

    // ==================== 聊天操作 ====================

    /**
     * 标记聊天为已读
     */
    void markChatAsRead(Long chatId, Long userId);

    /**
     * 获取未读消息数
     */
    Integer getUnreadCount(Long chatId, Long userId);

    /**
     * 获取用户总未读消息数
     */
    Integer getTotalUnreadCount(Long userId);

    /**
     * 清空聊天记录
     */
    void clearChatHistory(Long chatId, Long userId);

    /**
     * 删除聊天
     */
    void deleteChat(Long chatId, Long userId);

    // ==================== 业务验证 ====================

    /**
     * 检查用户是否是聊天成员
     */
    boolean isChatMember(Long chatId, Long userId);

    /**
     * 检查私聊是否存在
     */
    boolean privateChatExists(Long userId1, Long userId2);

    /**
     * 获取私聊ID
     */
    Long getPrivateChatId(Long userId1, Long userId2);

    /**
     * 检查用户是否有权限操作聊天
     */
    boolean hasChatPermission(Long chatId, Long userId, String operation);

    // ==================== 批量操作 ====================

    /**
     * 批量获取聊天信息
     */
    List<Chat> getChatsByIds(List<Long> chatIds, Long userId);

    /**
     * 批量更新未读计数
     */
    void updateUnreadCounts(Long chatId, Long senderId, int delta);

    /**
     * 获取用户活跃聊天列表（有消息的聊天）
     */
    List<Chat> getActiveChats(Long userId, Integer limit);
}
