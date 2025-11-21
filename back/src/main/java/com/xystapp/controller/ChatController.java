package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.Chat;
import com.xystapp.entity.ChatMember;
import com.xystapp.service.ChatService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 聊天控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "聊天管理")
@RestController
@RequestMapping("/chats")
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    // ==================== 私聊管理 ====================

    /**
     * 获取或创建私聊
     */
    @ApiOperation("获取或创建私聊")
    @PostMapping("/private")
    public Result<Chat> getOrCreatePrivateChat(@ApiParam("对方用户ID") @RequestParam @NotNull Long userId) {
        log.info("获取或创建私聊请求: userId={}", userId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Chat chat = chatService.getOrCreatePrivateChat(currentUserId, userId);
        
        return Result.success(chat);
    }

    /**
     * 获取私聊详情
     */
    @ApiOperation("获取私聊详情")
    @GetMapping("/{chatId}")
    public Result<Chat> getPrivateChat(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("获取私聊详情: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Chat chat = chatService.getPrivateChat(chatId, currentUserId);
        
        return Result.success(chat);
    }

    /**
     * 获取用户聊天列表
     */
    @ApiOperation("获取用户聊天列表")
    @GetMapping("/list")
    public Result<IPage<Chat>> getUserChatList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户聊天列表");
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<Chat> result = chatService.getUserChatList(currentUserId, page, size);
        
        return Result.success(result);
    }

    /**
     * 获取活跃聊天列表
     */
    @ApiOperation("获取活跃聊天列表")
    @GetMapping("/active")
    public Result<List<Chat>> getActiveChats(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取活跃聊天列表: limit={}", limit);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Chat> result = chatService.getActiveChats(currentUserId, limit);
        
        return Result.success(result);
    }

    // ==================== 聊天成员管理 ====================

    /**
     * 获取聊天成员列表
     */
    @ApiOperation("获取聊天成员列表")
    @GetMapping("/{chatId}/members")
    public Result<List<ChatMember>> getChatMembers(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("获取聊天成员列表: chatId={}", chatId);
        
        List<ChatMember> members = chatService.getChatMembers(chatId);
        return Result.success(members);
    }

    /**
     * 获取私聊中的对方用户信息
     */
    @ApiOperation("获取私聊中的对方用户信息")
    @GetMapping("/{chatId}/other-user")
    public Result<ChatMember> getOtherUserInPrivateChat(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("获取私聊中的对方用户信息: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        ChatMember otherUser = chatService.getOtherUserInPrivateChat(chatId, currentUserId);
        
        return Result.success(otherUser);
    }

    // ==================== 聊天操作 ====================

    /**
     * 标记聊天为已读
     */
    @ApiOperation("标记聊天为已读")
    @PostMapping("/{chatId}/read")
    public Result<Void> markChatAsRead(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("标记聊天为已读: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        chatService.markChatAsRead(chatId, currentUserId);
        
        return Result.success();
    }

    /**
     * 获取未读消息数
     */
    @ApiOperation("获取未读消息数")
    @GetMapping("/{chatId}/unread-count")
    public Result<Integer> getUnreadCount(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("获取未读消息数: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer unreadCount = chatService.getUnreadCount(chatId, currentUserId);
        
        return Result.success(unreadCount);
    }

    /**
     * 获取用户总未读消息数
     */
    @ApiOperation("获取用户总未读消息数")
    @GetMapping("/total-unread")
    public Result<Integer> getTotalUnreadCount() {
        log.info("获取用户总未读消息数");
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer totalUnread = chatService.getTotalUnreadCount(currentUserId);
        
        return Result.success(totalUnread);
    }

    /**
     * 清空聊天记录
     */
    @ApiOperation("清空聊天记录")
    @DeleteMapping("/{chatId}/history")
    public Result<Void> clearChatHistory(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("清空聊天记录: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        chatService.clearChatHistory(chatId, currentUserId);
        
        return Result.success();
    }

    /**
     * 删除聊天
     */
    @ApiOperation("删除聊天")
    @DeleteMapping("/{chatId}")
    public Result<Void> deleteChat(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("删除聊天: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        chatService.deleteChat(chatId, currentUserId);
        
        return Result.success();
    }

    // ==================== 业务验证 ====================

    /**
     * 检查用户是否是聊天成员
     */
    @ApiOperation("检查用户是否是聊天成员")
    @GetMapping("/{chatId}/is-member")
    public Result<Boolean> isChatMember(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("检查用户是否是聊天成员: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isMember = chatService.isChatMember(chatId, currentUserId);
        
        return Result.success(isMember);
    }

    /**
     * 检查私聊是否存在
     */
    @ApiOperation("检查私聊是否存在")
    @GetMapping("/private/exists")
    public Result<Boolean> privateChatExists(@ApiParam("对方用户ID") @RequestParam @NotNull Long userId) {
        log.info("检查私聊是否存在: userId={}", userId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean exists = chatService.privateChatExists(currentUserId, userId);
        
        return Result.success(exists);
    }

    /**
     * 获取私聊ID
     */
    @ApiOperation("获取私聊ID")
    @GetMapping("/private/id")
    public Result<Long> getPrivateChatId(@ApiParam("对方用户ID") @RequestParam @NotNull Long userId) {
        log.info("获取私聊ID: userId={}", userId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Long chatId = chatService.getPrivateChatId(currentUserId, userId);
        
        return Result.success(chatId);
    }

    /**
     * 检查用户是否有权限操作聊天
     */
    @ApiOperation("检查用户是否有权限操作聊天")
    @GetMapping("/{chatId}/permission")
    public Result<Boolean> hasChatPermission(
            @ApiParam("聊天ID") @PathVariable Long chatId,
            @ApiParam("操作类型") @RequestParam String operation) {
        log.info("检查用户是否有权限操作聊天: chatId={}, operation={}", chatId, operation);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean hasPermission = chatService.hasChatPermission(chatId, currentUserId, operation);
        
        return Result.success(hasPermission);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量获取聊天信息
     */
    @ApiOperation("批量获取聊天信息")
    @PostMapping("/batch")
    public Result<List<Chat>> getChatsByIds(@ApiParam("聊天ID列表") @RequestBody List<Long> chatIds) {
        log.info("批量获取聊天信息: chatIds={}", chatIds);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Chat> chats = chatService.getChatsByIds(chatIds, currentUserId);
        
        return Result.success(chats);
    }
}
