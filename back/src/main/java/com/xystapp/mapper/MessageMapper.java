package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 获取消息详情（包含用户信息）
     */
    Message selectMessageWithUserInfo(@Param("messageId") Long messageId);

    /**
     * 获取聊天消息列表（包含用户信息）
     */
    IPage<Message> selectMessagesByChatId(Page<Message> page, @Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 获取最新消息
     */
    Message selectLatestMessageByChatId(@Param("chatId") Long chatId);

    /**
     * 获取未读消息
     */
    List<Message> selectUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 搜索消息
     */
    IPage<Message> searchMessages(Page<Message> page, @Param("chatId") Long chatId, @Param("keyword") String keyword);

    /**
     * 批量获取消息信息
     */
    List<Message> selectMessagesByIds(@Param("messageIds") List<Long> messageIds, @Param("userId") Long userId);

    /**
     * 删除聊天的所有消息
     */
    int deleteMessagesByChatId(@Param("chatId") Long chatId);

    /**
     * 获取已读消息的用户ID列表
     */
    List<Long> selectReadUserIds(@Param("messageId") Long messageId);

    /**
     * 根据消息ID获取聊天ID
     */
    Long selectChatIdByMessageId(@Param("messageId") Long messageId);

    // ==================== 统计相关 ====================

    /**
     * 统计聊天消息总数
     */
    Long countMessagesByChatId(@Param("chatId") Long chatId);

    /**
     * 统计今日消息数量
     */
    Long countTodayMessagesByChatId(@Param("chatId") Long chatId);

    /**
     * 统计指定类型的消息数量
     */
    Long countMessagesByType(@Param("chatId") Long chatId, @Param("contentType") String contentType);

    /**
     * 统计用户发送的消息数量
     */
    Long countMessagesByUserId(@Param("userId") Long userId);

    /**
     * 统计用户今日发送的消息数量
     */
    Long countTodayMessagesByUserId(@Param("userId") Long userId);

    // ==================== 业务查询 ====================

    /**
     * 获取用户在指定时间范围内的消息
     */
    List<Message> selectMessagesByTimeRange(@Param("userId") Long userId, 
                                           @Param("startTime") java.time.LocalDateTime startTime,
                                           @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 获取聊天中指定用户的消息
     */
    List<Message> selectMessagesByUserInChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    /**
     * 获取图片消息列表
     */
    List<Message> selectImageMessages(@Param("chatId") Long chatId, @Param("limit") Integer limit);

    /**
     * 获取最近的N条消息
     */
    List<Message> selectRecentMessages(@Param("chatId") Long chatId, @Param("limit") Integer limit);

    /**
     * 检查消息是否存在
     */
    boolean messageExists(@Param("messageId") Long messageId);

    /**
     * 检查用户是否是消息发送者
     */
    boolean isMessageSender(@Param("messageId") Long messageId, @Param("userId") Long userId);

    /**
     * 获取消息发送时间
     */
    java.time.LocalDateTime selectMessageCreatedAt(@Param("messageId") Long messageId);
}
