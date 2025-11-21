package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.MessageSendRequest;
import com.xystapp.entity.Chat;
import com.xystapp.entity.ChatMember;
import com.xystapp.entity.Message;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.ChatMapper;
import com.xystapp.mapper.ChatMemberMapper;
import com.xystapp.mapper.MessageMapper;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.ChatService;
import com.xystapp.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ChatMemberMapper chatMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ChatService chatService;

    private static final int RECALL_TIME_LIMIT_MINUTES = 5; // 撤回时间限制（分钟）

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendMessage(MessageSendRequest request, Long senderId) {
        log.info("发送消息请求: chatId={}, senderId={}, contentType={}", 
                request.getChatId(), senderId, request.getContentType());

        // 验证参数
        if (request.getChatId() == null || senderId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查聊天是否存在
        Chat chat = chatMapper.selectById(request.getChatId());
        if (chat == null) {
            throw new BusinessException(404, "聊天不存在");
        }

        // 检查用户是否是聊天成员
        if (!chatService.isChatMember(request.getChatId(), senderId)) {
            throw new BusinessException(403, "无权限发送消息");
        }

        // 获取发送者信息
        com.xystapp.entity.User user = userMapper.selectById(senderId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 创建消息
        Message message = new Message();
        message.setChatId(request.getChatId());
        message.setSenderId(senderId);
        message.setContentType(request.getContentType());
        message.setContent(request.getContent());
        message.setSenderName(user.getNickname() != null ? user.getNickname() : user.getUsername());

        int result = messageMapper.insert(message);
        if (result <= 0) {
            throw new BusinessException(500, "发送消息失败");
        }

        // 更新聊天最后消息时间
        chatMapper.updateLastMessageTime(request.getChatId());

        // 更新其他成员的未读计数
        chatService.updateUnreadCounts(request.getChatId(), senderId, 1);

        log.info("发送消息成功: messageId={}, chatId={}", message.getId(), message.getChatId());
        return message;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendTextMessage(Long chatId, String content, Long senderId) {
        MessageSendRequest request = new MessageSendRequest();
        request.setChatId(chatId);
        request.setContent(content);
        request.setContentType("text");

        return sendMessage(request, senderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Message sendImageMessage(Long chatId, String imageUrl, Long senderId) {
        MessageSendRequest request = new MessageSendRequest();
        request.setChatId(chatId);
        request.setContent(imageUrl);
        request.setContentType("image");

        return sendMessage(request, senderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recallMessage(Long messageId, Long userId) {
        log.info("撤回消息请求: messageId={}, userId={}", messageId, userId);

        if (messageId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查消息是否存在
        Message message = getMessageById(messageId, userId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }

        // 检查是否是消息发送者
        if (!isMessageSender(messageId, userId)) {
            throw new BusinessException(403, "无权限撤回该消息");
        }

        // 检查是否可以撤回
        if (!canRecallMessage(messageId, userId)) {
            throw new BusinessException(400, "消息发送时间超过5分钟，无法撤回");
        }

        // 更新消息内容为撤回状态
        message.setContent("[消息已撤回]");
        message.setContentType("text");
        int result = messageMapper.updateById(message);
        if (result <= 0) {
            throw new BusinessException(500, "撤回消息失败");
        }

        log.info("撤回消息成功: messageId={}", messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMessage(Long messageId, Long userId) {
        log.info("删除消息请求: messageId={}, userId={}", messageId, userId);

        if (messageId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查消息是否存在
        Message message = getMessageById(messageId, userId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }

        // 检查权限（消息发送者或聊天管理员可以删除）
        if (!isMessageSender(messageId, userId) && 
            !chatService.hasChatPermission(message.getChatId(), userId, "delete")) {
            throw new BusinessException(403, "无权限删除该消息");
        }

        // 删除消息
        int result = messageMapper.deleteById(messageId);
        if (result <= 0) {
            throw new BusinessException(500, "删除消息失败");
        }

        log.info("删除消息成功: messageId={}", messageId);
    }

    @Override
    public IPage<Message> getChatMessages(Long chatId, Long userId, Integer page, Integer size) {
        log.info("获取聊天消息列表: chatId={}, userId={}", chatId, userId);

        if (chatId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否是聊天成员
        if (!chatService.isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限查看该聊天消息");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Message> pageParam = new Page<>(page, size);
        IPage<Message> result = messageMapper.selectMessagesByChatId(pageParam, chatId, userId);

        // 设置已读状态
        result.getRecords().forEach(message -> {
            message.setIsRead(isMessageRead(message.getId(), userId));
        });

        log.info("获取聊天消息列表成功: chatId={}, 总数={}", chatId, result.getTotal());
        return result;
    }

    @Override
    public Message getMessageById(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return null;
        }

        // 检查权限
        if (!canViewMessage(messageId, userId)) {
            throw new BusinessException(403, "无权限查看该消息");
        }

        return messageMapper.selectMessageWithUserInfo(messageId);
    }

    @Override
    public Message getLatestMessage(Long chatId) {
        if (chatId == null) {
            return null;
        }

        return messageMapper.selectLatestMessageByChatId(chatId);
    }

    @Override
    public List<Message> getUnreadMessages(Long chatId, Long userId) {
        if (chatId == null || userId == null) {
            return new ArrayList<>();
        }

        // 检查用户是否是聊天成员
        if (!chatService.isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限查看该聊天消息");
        }

        return messageMapper.selectUnreadMessages(chatId, userId);
    }

    @Override
    public IPage<Message> searchMessages(Long chatId, Long userId, String keyword, Integer page, Integer size) {
        log.info("搜索消息: chatId={}, userId={}, keyword={}", chatId, userId, keyword);

        if (chatId == null || userId == null || keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否是聊天成员
        if (!chatService.isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限查看该聊天消息");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Message> pageParam = new Page<>(page, size);
        IPage<Message> result = messageMapper.searchMessages(pageParam, chatId, keyword.trim());

        log.info("搜索消息成功: chatId={}, 总数={}", chatId, result.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessageAsRead(Long messageId, Long userId) {
        log.info("标记消息已读: messageId={}, userId={}", messageId, userId);

        if (messageId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!canViewMessage(messageId, userId)) {
            throw new BusinessException(403, "无权限操作该消息");
        }

        // 获取消息信息
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException(404, "消息不存在");
        }

        // 更新聊天成员的最后阅读时间
        chatService.markChatAsRead(message.getChatId(), userId);

        log.info("标记消息已读成功: messageId={}", messageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markMessagesAsRead(List<Long> messageIds, Long userId) {
        log.info("批量标记消息已读: messageIds={}, userId={}", messageIds, userId);

        if (messageIds == null || messageIds.isEmpty() || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 获取消息对应的聊天ID
        if (!messageIds.isEmpty()) {
            Long chatId = messageMapper.selectChatIdByMessageId(messageIds.get(0));
            if (chatId != null) {
                // 标记整个聊天为已读
                chatService.markChatAsRead(chatId, userId);
            }
        }

        log.info("批量标记消息已读成功: messageIds={}", messageIds);
    }

    @Override
    public boolean isMessageRead(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return false;
        }

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            return false;
        }

        ChatMember member = chatMemberMapper.selectMember(message.getChatId(), userId);
        if (member == null || member.getLastReadAt() == null) {
            return false;
        }

        return message.getCreatedAt().isBefore(member.getLastReadAt()) || 
               message.getCreatedAt().isEqual(member.getLastReadAt());
    }

    @Override
    public List<Long> getReadUserIds(Long messageId) {
        if (messageId == null) {
            return new ArrayList<>();
        }

        return messageMapper.selectReadUserIds(messageId);
    }

    // ==================== 业务验证 ====================

    @Override
    public boolean messageExists(Long messageId) {
        if (messageId == null) {
            return false;
        }

        return messageMapper.selectById(messageId) != null;
    }

    @Override
    public boolean isMessageSender(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return false;
        }

        Message message = messageMapper.selectById(messageId);
        return message != null && message.getSenderId().equals(userId);
    }

    @Override
    public boolean canViewMessage(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return false;
        }

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            return false;
        }

        return chatService.isChatMember(message.getChatId(), userId);
    }

    @Override
    public boolean canRecallMessage(Long messageId, Long userId) {
        if (messageId == null || userId == null) {
            return false;
        }

        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            return false;
        }

        // 检查是否是发送者
        if (!message.getSenderId().equals(userId)) {
            return false;
        }

        // 检查时间限制
        LocalDateTime recallLimit = message.getCreatedAt().plusMinutes(RECALL_TIME_LIMIT_MINUTES);
        return LocalDateTime.now().isBefore(recallLimit);
    }

    // ==================== 批量操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChatMessages(Long chatId, Long userId) {
        log.info("删除聊天消息: chatId={}, userId={}", chatId, userId);

        if (chatId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!chatService.hasChatPermission(chatId, userId, "delete")) {
            throw new BusinessException(403, "无权限删除聊天消息");
        }

        int deletedCount = messageMapper.deleteMessagesByChatId(chatId);
        log.info("删除聊天消息成功: chatId={}, 删除数量={}", chatId, deletedCount);
    }

    @Override
    public List<Message> getMessagesByIds(List<Long> messageIds, Long userId) {
        if (messageIds == null || messageIds.isEmpty() || userId == null) {
            return new ArrayList<>();
        }

        return messageMapper.selectMessagesByIds(messageIds, userId);
    }

    @Override
    public MessageStats getMessageStats(Long chatId, Long userId) {
        log.info("获取消息统计: chatId={}, userId={}", chatId, userId);

        if (chatId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!chatService.isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限查看该聊天统计");
        }

        Long totalCount = messageMapper.countMessagesByChatId(chatId);
        Long todayCount = messageMapper.countTodayMessagesByChatId(chatId);
        Long unreadCount = (long) chatService.getUnreadCount(chatId, userId);
        Long imageCount = messageMapper.countMessagesByType(chatId, "image");
        Long textCount = messageMapper.countMessagesByType(chatId, "text");

        MessageStats stats = new MessageStats(totalCount, todayCount, unreadCount, imageCount, textCount);

        log.info("获取消息统计成功: chatId={}, total={}, today={}, unread={}, image={}, text={}", 
                chatId, totalCount, todayCount, unreadCount, imageCount, textCount);

        return stats;
    }
}
