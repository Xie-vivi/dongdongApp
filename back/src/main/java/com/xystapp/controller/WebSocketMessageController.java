package com.xystapp.controller;

import com.xystapp.dto.MessageSendRequest;
import com.xystapp.entity.Message;
import com.xystapp.entity.Chat;
import com.xystapp.entity.ChatMember;
import com.xystapp.service.MessageService;
import com.xystapp.service.ChatService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

/**
 * WebSocket消息控制器
 * 处理实时消息的发送和接收
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "WebSocket消息管理")
@Controller
public class WebSocketMessageController {

    private static final Logger log = LoggerFactory.getLogger(WebSocketMessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;

    /**
     * 发送私聊消息
     * 客户端发送到 /app/chat/private/{receiverId}
     */
    @MessageMapping("/chat/private/{receiverId}")
    @ApiOperation("发送私聊消息")
    public void sendPrivateMessage(
            @DestinationVariable Long receiverId,
            @Payload MessageSendRequest request,
            Principal principal) {
        
        log.info("收到私聊消息: sender={}, receiver={}, content={}", 
                principal.getName(), receiverId, request.getContent());

        try {
            Long senderId = SecurityUtils.getCurrentUserId();
            
            // 验证发送者
            if (!principal.getName().equals(String.valueOf(senderId))) {
                log.warn("发送者身份验证失败: principal={}, senderId={}", 
                        principal.getName(), senderId);
                return;
            }

            // 创建私聊或获取现有私聊ID
            Long chatId = getOrCreatePrivateChat(senderId, receiverId);
            if (chatId == null) {
                log.error("无法创建或获取私聊: senderId={}, receiverId={}", senderId, receiverId);
                return;
            }

            // 保存消息到数据库
            request.setChatId(chatId);
            Message message = messageService.sendMessage(request, senderId);

            // 发送给接收者
            messagingTemplate.convertAndSendToUser(
                    String.valueOf(receiverId),
                    "/queue/private",
                    message
            );

            // 发送给发送者（确认消息已发送）
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/private",
                    message
            );

            log.info("私聊消息发送成功: messageId={}, chatId={}", 
                    message.getId(), chatId);

        } catch (Exception e) {
            log.error("发送私聊消息失败: {}", e.getMessage(), e);
            
            // 发送错误消息给发送者
            Message errorMessage = new Message();
            errorMessage.setContent("消息发送失败: " + e.getMessage());
            errorMessage.setContentType("error");
            
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/error",
                    errorMessage
            );
        }
    }

    /**
     * 发送群聊消息
     * 客户端发送到 /app/chat/group/{chatId}
     */
    @MessageMapping("/chat/group/{chatId}")
    @ApiOperation("发送群聊消息")
    public void sendGroupMessage(
            @DestinationVariable Long chatId,
            @Payload MessageSendRequest request,
            Principal principal) {

        log.info("收到群聊消息: sender={}, chatId={}, content={}", 
                principal.getName(), chatId, request.getContent());

        try {
            Long senderId = SecurityUtils.getCurrentUserId();
            
            // 验证发送者
            if (!principal.getName().equals(String.valueOf(senderId))) {
                log.warn("发送者身份验证失败: principal={}, senderId={}", 
                        principal.getName(), senderId);
                return;
            }

            // 验证用户是否是群聊成员
            if (!isChatMember(chatId, senderId)) {
                log.warn("用户不是群聊成员: userId={}, chatId={}", senderId, chatId);
                return;
            }

            // 保存消息到数据库
            request.setChatId(chatId);
            Message message = messageService.sendMessage(request, senderId);

            // 广播消息给群聊所有成员
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatId,
                    message
            );

            log.info("群聊消息发送成功: messageId={}, chatId={}", 
                    message.getId(), chatId);

        } catch (Exception e) {
            log.error("发送群聊消息失败: {}", e.getMessage(), e);
            
            // 发送错误消息给发送者
            Message errorMessage = new Message();
            errorMessage.setContent("消息发送失败: " + e.getMessage());
            errorMessage.setContentType("error");
            
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/error",
                    errorMessage
            );
        }
    }

    /**
     * 处理消息已读状态
     * 客户端发送到 /app/chat/read/{messageId}
     */
    @MessageMapping("/chat/read/{messageId}")
    @ApiOperation("标记消息已读")
    public void markMessageAsRead(
            @DestinationVariable Long messageId,
            Principal principal) {

        log.info("标记消息已读: user={}, messageId={}", principal.getName(), messageId);

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            
            // 验证发送者
            if (!principal.getName().equals(String.valueOf(userId))) {
                log.warn("用户身份验证失败: principal={}, userId={}", 
                        principal.getName(), userId);
                return;
            }

            // TODO: 更新消息已读状态
            // messageService.markMessageAsRead(messageId, userId);

            // 通知发送者消息已被读取
            Message readNotification = new Message();
            readNotification.setId(messageId);
            readNotification.setContentType("read_receipt");
            readNotification.setContent("消息已被读取");

            // 获取消息发送者ID并发送通知
            Long senderId = getMessageSenderId(messageId);
            if (senderId != null) {
                messagingTemplate.convertAndSendToUser(
                        String.valueOf(senderId),
                        "/queue/read",
                        readNotification
                );
            }

            log.info("消息已读状态更新成功: messageId={}, userId={}", messageId, userId);

        } catch (Exception e) {
            log.error("更新消息已读状态失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理用户正在输入状态
     * 客户端发送到 /app/chat/typing/{chatId}
     */
    @MessageMapping("/chat/typing/{chatId}")
    @ApiOperation("用户正在输入")
    public void handleTyping(
            @DestinationVariable Long chatId,
            @Payload TypingMessage typingMessage,
            Principal principal) {

        log.debug("用户正在输入: user={}, chatId={}, isTyping={}", 
                principal.getName(), chatId, typingMessage.isTyping());

        try {
            Long userId = SecurityUtils.getCurrentUserId();
            
            // 验证发送者
            if (!principal.getName().equals(String.valueOf(userId))) {
                return;
            }

            // 验证用户是否是群聊成员
            if (!isChatMember(chatId, userId)) {
                return;
            }

            // 设置输入状态信息
            typingMessage.setUserId(userId);
            typingMessage.setChatId(chatId);

            // 广播输入状态给群聊其他成员
            messagingTemplate.convertAndSend(
                    "/topic/chat/" + chatId + "/typing",
                    typingMessage
            );

        } catch (Exception e) {
            log.error("处理输入状态失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取或创建私聊
     */
    private Long getOrCreatePrivateChat(Long userId1, Long userId2) {
        try {
            Chat chat = chatService.getOrCreatePrivateChat(userId1, userId2);
            return chat != null ? chat.getId() : null;
        } catch (Exception e) {
            log.error("获取或创建私聊失败: userId1={}, userId2={}, error={}", 
                    userId1, userId2, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 检查用户是否是群聊成员
     */
    private boolean isChatMember(Long chatId, Long userId) {
        try {
            List<ChatMember> members = chatService.getChatMembers(chatId);
            return members.stream().anyMatch(member -> member.getUserId().equals(userId));
        } catch (Exception e) {
            log.error("检查群聊成员失败: chatId={}, userId={}, error={}", 
                    chatId, userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 获取消息发送者ID
     */
    private Long getMessageSenderId(Long messageId) {
        try {
            // TODO: 实现获取消息发送者ID的逻辑
            // 可以调用MessageService中的方法，暂时返回null
            // Message message = messageService.getMessageById(messageId);
            // return message != null ? message.getSenderId() : null;
            return null;
        } catch (Exception e) {
            log.error("获取消息发送者ID失败: messageId={}, error={}", 
                    messageId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 输入状态消息类
     */
    public static class TypingMessage {
        private Long userId;
        private Long chatId;
        private boolean typing;
        private String username;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getChatId() { return chatId; }
        public void setChatId(Long chatId) { this.chatId = chatId; }

        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}
