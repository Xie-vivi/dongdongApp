package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xystapp.entity.ChatMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 聊天成员Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ChatMemberMapper extends BaseMapper<ChatMember> {

    /**
     * 获取聊天成员列表（包含用户信息）
     */
    List<ChatMember> selectMembersByChatId(@Param("chatId") Long chatId);

    /**
     * 获取私聊中的对方用户
     */
    ChatMember selectOtherUserInPrivateChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 获取聊天成员信息
     */
    ChatMember selectMember(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 获取除发送者外的聊天成员
     */
    List<ChatMember> selectMembersExceptSender(@Param("chatId") Long chatId, @Param("senderId") Long senderId);

    /**
     * 获取私聊ID
     */
    Long selectPrivateChatId(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    /**
     * 获取用户未读消息数
     */
    Integer selectUnreadCount(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 获取用户总未读消息数
     */
    Integer selectTotalUnreadCount(@Param("userId") Long userId);

    /**
     * 标记聊天为已读
     */
    int markChatAsRead(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("readAt") java.time.LocalDateTime readAt);

    /**
     * 更新未读计数
     */
    int updateUnreadCount(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("delta") int delta);

    /**
     * 重置聊天所有成员的未读计数
     */
    int resetUnreadCount(@Param("chatId") Long chatId);

    /**
     * 删除聊天成员
     */
    int deleteMembersByChatId(@Param("chatId") Long chatId);

    /**
     * 检查用户是否是聊天成员
     */
    boolean isChatMember(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 获取用户在聊天中的角色
     */
    String selectUserRole(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 更新用户角色
     */
    int updateUserRole(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("role") String role);

    /**
     * 获取用户参与的聊天列表
     */
    List<Long> selectUserChatIds(@Param("userId") Long userId);

    /**
     * 批量获取聊天成员
     */
    List<ChatMember> selectMembersByChatIds(@Param("chatIds") List<Long> chatIds);
}
