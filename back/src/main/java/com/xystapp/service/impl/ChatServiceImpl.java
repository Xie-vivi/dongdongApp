package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Chat;
import com.xystapp.entity.ChatMember;
import com.xystapp.entity.Message;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.ChatMapper;
import com.xystapp.mapper.ChatMemberMapper;
import com.xystapp.mapper.MessageMapper;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 聊天服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class ChatServiceImpl implements ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ChatMemberMapper chatMemberMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Chat getOrCreatePrivateChat(Long userId1, Long userId2) {
        log.info("获取或创建私聊: userId1={}, userId2={}", userId1, userId2);

        // 验证参数
        if (userId1 == null || userId2 == null || userId1.equals(userId2)) {
            throw new BusinessException(400, "用户ID无效");
        }

        // 检查用户是否存在
        if (userMapper.selectById(userId1) == null || userMapper.selectById(userId2) == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 确保userId1 < userId2以避免重复
        Long user1 = userId1 < userId2 ? userId1 : userId2;
        Long user2 = userId1 < userId2 ? userId2 : userId1;

        // 查找已存在的私聊（双重检查避免竞态条件）
        Long existingChatId = getPrivateChatId(user1, user2);
        if (existingChatId != null) {
            Chat chat = chatMapper.selectById(existingChatId);
            if (chat != null) {
                log.info("找到已存在的私聊: chatId={}", existingChatId);
                return chat;
            }
        }

        // 创建新的私聊
        Chat chat = new Chat();
        chat.setType("private");
        chat.setName(null); // 私聊不需要名称
        chat.setAvatar(null);
        chat.setNotice(null);
        chat.setActivity(null);
        chat.setGroupField(null);
        chat.setDescription(null);

        try {
            int result = chatMapper.insert(chat);
            if (result <= 0) {
                throw new BusinessException(500, "创建私聊失败");
            }

            // 添加聊天成员
            ChatMember member1 = new ChatMember();
            member1.setChatId(chat.getId());
            member1.setUserId(user1);
            member1.setRole("owner");
            member1.setUnreadCount(0);

            ChatMember member2 = new ChatMember();
            member2.setChatId(chat.getId());
            member2.setUserId(user2);
            member2.setRole("owner");
            member2.setUnreadCount(0);

            chatMemberMapper.insert(member1);
            chatMemberMapper.insert(member2);

            log.info("创建私聊成功: chatId={}", chat.getId());
            return chat;
        } catch (Exception e) {
            // 如果创建失败，可能是并发创建导致的，重新查找
            log.warn("创建私聊失败，尝试查找已存在的私聊: {}", e.getMessage());
            Long retryChatId = getPrivateChatId(user1, user2);
            if (retryChatId != null) {
                Chat retryChat = chatMapper.selectById(retryChatId);
                if (retryChat != null) {
                    log.info("找到已存在的私聊（重试）: chatId={}", retryChatId);
                    return retryChat;
                }
            }
            throw new BusinessException(500, "创建私聊失败: " + e.getMessage());
        }
    }

    @Override
    public Chat getPrivateChat(Long chatId, Long userId) {
        log.info("获取私聊详情: chatId={}, userId={}", chatId, userId);

        if (chatId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否是聊天成员
        if (!isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限访问该聊天");
        }

        Chat chat = chatMapper.selectChatWithUserInfo(chatId, userId);
        if (chat == null) {
            throw new BusinessException(404, "聊天不存在");
        }

        // 获取未读消息数
        Integer unreadCount = getUnreadCount(chatId, userId);
        chat.setUnreadCount(unreadCount);

        // 获取最后一条消息
        Message lastMessage = messageMapper.selectLatestMessageByChatId(chatId);
        chat.setLastMessage(lastMessage);

        log.info("获取私聊详情成功: chatId={}", chatId);
        return chat;
    }

    @Override
    public IPage<Chat> getUserChatList(Long userId, Integer page, Integer size) {
        log.info("获取用户聊天列表: userId={}", userId);

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

        Page<Chat> pageParam = new Page<>(page, size);
        IPage<Chat> result = chatMapper.selectUserChatList(pageParam, userId);

        // 设置未读消息数和最后消息
        result.getRecords().forEach(chat -> {
            Integer unreadCount = getUnreadCount(chat.getId(), userId);
            chat.setUnreadCount(unreadCount);

            Message lastMessage = messageMapper.selectLatestMessageByChatId(chat.getId());
            chat.setLastMessage(lastMessage);
        });

        log.info("获取用户聊天列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public List<ChatMember> getChatMembers(Long chatId) {
        log.info("获取聊天成员列表: chatId={}", chatId);

        if (chatId == null) {
            throw new BusinessException(400, "聊天ID不能为空");
        }

        List<ChatMember> members = chatMemberMapper.selectMembersByChatId(chatId);
        log.info("获取聊天成员列表成功: chatId={}, 成员数={}", chatId, members.size());
        return members;
    }

    @Override
    public ChatMember getOtherUserInPrivateChat(Long chatId, Long currentUserId) {
        log.info("获取私聊中的对方用户: chatId={}, currentUserId={}", chatId, currentUserId);

        if (chatId == null || currentUserId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        ChatMember otherUser = chatMemberMapper.selectOtherUserInPrivateChat(chatId, currentUserId);
        if (otherUser == null) {
            throw new BusinessException(404, "未找到对方用户");
        }

        log.info("获取私聊中的对方用户成功: userId={}", otherUser.getUserId());
        return otherUser;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markChatAsRead(Long chatId, Long userId) {
        log.info("标记聊天为已读: chatId={}, userId={}", chatId, userId);

        if (chatId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否是聊天成员
        if (!isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限操作该聊天");
        }

        // 更新最后阅读时间和未读计数
        int result = chatMemberMapper.markChatAsRead(chatId, userId, LocalDateTime.now());
        if (result <= 0) {
            throw new BusinessException(500, "标记已读失败");
        }

        log.info("标记聊天为已读成功: chatId={}, userId={}", chatId, userId);
    }

    @Override
    public Integer getUnreadCount(Long chatId, Long userId) {
        if (chatId == null || userId == null) {
            return 0;
        }

        return chatMemberMapper.selectUnreadCount(chatId, userId);
    }

    @Override
    public Integer getTotalUnreadCount(Long userId) {
        if (userId == null) {
            return 0;
        }

        return chatMemberMapper.selectTotalUnreadCount(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearChatHistory(Long chatId, Long userId) {
        log.info("清空聊天记录: chatId={}, userId={}", chatId, userId);

        if (chatId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否是聊天成员
        if (!isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限操作该聊天");
        }

        // 删除聊天消息
        int deletedCount = messageMapper.deleteMessagesByChatId(chatId);
        log.info("删除聊天消息: chatId={}, 删除数量={}", chatId, deletedCount);

        // 重置未读计数
        chatMemberMapper.resetUnreadCount(chatId);

        log.info("清空聊天记录成功: chatId={}", chatId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChat(Long chatId, Long userId) {
        log.info("删除聊天: chatId={}, userId={}", chatId, userId);

        if (chatId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否是聊天成员
        if (!isChatMember(chatId, userId)) {
            throw new BusinessException(403, "无权限操作该聊天");
        }

        // 删除聊天消息
        messageMapper.deleteMessagesByChatId(chatId);

        // 删除聊天成员
        chatMemberMapper.deleteMembersByChatId(chatId);

        // 删除聊天
        int result = chatMapper.deleteById(chatId);
        if (result <= 0) {
            throw new BusinessException(500, "删除聊天失败");
        }

        log.info("删除聊天成功: chatId={}", chatId);
    }

    // ==================== 业务验证 ====================

    @Override
    public boolean isChatMember(Long chatId, Long userId) {
        if (chatId == null || userId == null) {
            return false;
        }

        QueryWrapper<ChatMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("chat_id", chatId).eq("user_id", userId);

        return chatMemberMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean privateChatExists(Long userId1, Long userId2) {
        return getPrivateChatId(userId1, userId2) != null;
    }

    @Override
    public Long getPrivateChatId(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) {
            return null;
        }

        return chatMemberMapper.selectPrivateChatId(userId1, userId2);
    }

    @Override
    public boolean hasChatPermission(Long chatId, Long userId, String operation) {
        if (!isChatMember(chatId, userId)) {
            return false;
        }

        // 简单的权限检查，后续可扩展
        if ("delete".equals(operation)) {
            // 只有群主或管理员可以删除群聊
            ChatMember member = chatMemberMapper.selectMember(chatId, userId);
            return member != null && ("owner".equals(member.getRole()) || "admin".equals(member.getRole()));
        }

        return true;
    }

    // ==================== 批量操作 ====================

    @Override
    public List<Chat> getChatsByIds(List<Long> chatIds, Long userId) {
        if (chatIds == null || chatIds.isEmpty() || userId == null) {
            return new ArrayList<>();
        }

        return chatMapper.selectChatsByIds(chatIds, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUnreadCounts(Long chatId, Long senderId, int delta) {
        log.info("更新未读计数: chatId={}, senderId={}, delta={}", chatId, senderId, delta);

        if (chatId == null || senderId == null) {
            return;
        }

        // 获取聊天成员（除了发送者）
        List<ChatMember> members = chatMemberMapper.selectMembersExceptSender(chatId, senderId);
        
        // 更新每个成员的未读计数
        for (ChatMember member : members) {
            chatMemberMapper.updateUnreadCount(chatId, member.getUserId(), delta);
        }

        log.info("更新未读计数成功: chatId={}, 更新成员数={}", chatId, members.size());
    }

    @Override
    public List<Chat> getActiveChats(Long userId, Integer limit) {
        if (userId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 10;
        }

        return chatMapper.selectActiveChats(userId, limit);
    }
}
