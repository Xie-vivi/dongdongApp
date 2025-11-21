package com.xystapp.config;

import com.xystapp.service.MessageService;
import com.xystapp.service.OnlineUserService;
import com.xystapp.service.impl.OnlineUserServiceImpl;
import com.xystapp.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * WebSocket握手拦截器
 * 处理JWT认证和连接管理
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public class WebSocketHandshakeInterceptor implements ChannelInterceptor {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    private final JwtUtils jwtUtils;
    private final MessageService messageService;
    private final OnlineUserService onlineUserService;

    public WebSocketHandshakeInterceptor(JwtUtils jwtUtils, MessageService messageService, OnlineUserService onlineUserService) {
        this.jwtUtils = jwtUtils;
        this.messageService = messageService;
        this.onlineUserService = onlineUserService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 处理连接请求，验证JWT token
            String token = extractTokenFromHeader(accessor);
            
            if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
                String username = jwtUtils.extractUsername(token);
                
                if (StringUtils.hasText(username)) {
                    // 创建用户认证对象
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, null);
                    
                    // 设置用户信息到会话中
                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.info("WebSocket连接认证成功: username={}", username);
                    
                    // 将用户添加到在线用户列表（Redis）
                    try {
                        Long userId = extractUserIdFromToken(token);
                        if (userId != null) {
                            String sessionId = accessor.getSessionId();
                            onlineUserService.userOnline(userId, sessionId);
                            
                            // TODO: 检查是否有离线消息需要推送
                            // checkOfflineMessages(userId, sessionId);
                        }
                    } catch (Exception e) {
                        log.error("添加在线用户失败: username={}, error={}", username, e.getMessage(), e);
                    }
                } else {
                    log.warn("WebSocket连接认证失败: 无法从token中提取用户名");
                }
            } else {
                log.warn("WebSocket连接认证失败: token无效或为空");
            }
        }
        
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            
            if (StompCommand.DISCONNECT.equals(command)) {
                // 处理断开连接
                String username = accessor.getUser() != null ? accessor.getUser().getName() : null;
                
                if (StringUtils.hasText(username)) {
                    log.info("用户断开WebSocket连接: username={}", username);
                    
                    // 将用户从在线用户列表移除（Redis）
                    try {
                        // 从会话中获取用户ID
                        String sessionId = accessor.getSessionId();
                        Long userId = getUserIdFromSession(sessionId);
                        if (userId != null) {
                            onlineUserService.userOffline(userId, sessionId);
                        }
                    } catch (Exception e) {
                        log.error("移除在线用户失败: username={}, error={}", username, e.getMessage(), e);
                    }
                }
            } else if (StompCommand.SUBSCRIBE.equals(command)) {
                // 处理订阅
                String username = accessor.getUser() != null ? accessor.getUser().getName() : null;
                String destination = accessor.getDestination();
                
                log.info("用户订阅: username={}, destination={}", username, destination);
                
                // TODO: 验证用户是否有权限订阅该目标
                // TODO: 将用户添加到相应的订阅列表
            } else if (StompCommand.UNSUBSCRIBE.equals(command)) {
                // 处理取消订阅
                String username = accessor.getUser() != null ? accessor.getUser().getName() : null;
                String destination = accessor.getDestination();
                
                log.info("用户取消订阅: username={}, destination={}", username, destination);
                
                // TODO: 将用户从相应的订阅列表移除
            }
        }
    }

    /**
     * 从请求头中提取JWT token
     */
    private String extractTokenFromHeader(StompHeaderAccessor accessor) {
        // 尝试从Authorization头中获取token
        String authorization = accessor.getFirstNativeHeader("Authorization");
        
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        
        // 尝试从token参数中获取
        String token = accessor.getFirstNativeHeader("token");
        
        if (StringUtils.hasText(token)) {
            return token;
        }
        
        // 尝试从查询参数中获取
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        if (sessionAttributes != null) {
            Object tokenObj = sessionAttributes.get("token");
            if (tokenObj instanceof String) {
                return (String) tokenObj;
            }
        }
        
        return null;
    }

    /**
     * 验证用户是否有权限订阅指定目标
     */
    private boolean hasPermissionToSubscribe(String username, String destination) {
        if (!StringUtils.hasText(username) || !StringUtils.hasText(destination)) {
            return false;
        }
        
        // 验证私聊订阅权限 (/queue/user/{userId})
        if (destination.startsWith("/queue/user/")) {
            String targetUserId = destination.substring("/queue/user/".length());
            // 用户只能订阅自己的私聊队列
            return username.equals(targetUserId);
        }
        
        // 验证群聊订阅权限 (/topic/chat/{chatId})
        if (destination.startsWith("/topic/chat/")) {
            String chatId = destination.substring("/topic/chat/".length());
            try {
                Long chatIdLong = Long.parseLong(chatId);
                // TODO: 检查用户是否是该群聊的成员
                // return messageService.isChatMember(chatIdLong, getUserIdByUsername(username));
                return true; // 暂时返回true，后续实现权限检查
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        // 其他目标暂时不允许订阅
        return false;
    }

    /**
     * 从JWT token中提取用户ID
     */
    private Long extractUserIdFromToken(String token) {
        try {
            return jwtUtils.extractUserId(token);
        } catch (Exception e) {
            log.error("从token中提取用户ID失败: error={}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从会话中获取用户ID
     */
    private Long getUserIdFromSession(String sessionId) {
        try {
            // 通过OnlineUserService获取用户ID
            if (onlineUserService instanceof OnlineUserServiceImpl) {
                return ((OnlineUserServiceImpl) onlineUserService).getUserIdBySession(sessionId);
            }
            return null;
        } catch (Exception e) {
            log.error("从会话中获取用户ID失败: sessionId={}, error={}", sessionId, e.getMessage(), e);
            return null;
        }
    }
}
