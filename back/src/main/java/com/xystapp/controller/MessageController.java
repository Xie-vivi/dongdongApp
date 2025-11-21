package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.dto.MessageSendRequest;
import com.xystapp.entity.Message;
import com.xystapp.service.MessageService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 消息控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "消息管理")
@RestController
@RequestMapping("/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    // ==================== 消息发送 ====================

    /**
     * 发送消息
     */
    @ApiOperation("发送消息")
    @PostMapping
    public Result<Message> sendMessage(@Valid @RequestBody MessageSendRequest request) {
        log.info("发送消息请求: chatId={}, contentType={}", request.getChatId(), request.getContentType());
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Message message = messageService.sendMessage(request, currentUserId);
        
        return Result.success(message);
    }

    /**
     * 发送文本消息
     */
    @ApiOperation("发送文本消息")
    @PostMapping("/text")
    public Result<Message> sendTextMessage(
            @ApiParam("聊天ID") @RequestParam Long chatId,
            @ApiParam("消息内容") @RequestParam String content) {
        log.info("发送文本消息请求: chatId={}, content={}", chatId, content);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Message message = messageService.sendTextMessage(chatId, content, currentUserId);
        
        return Result.success(message);
    }

    /**
     * 发送图片消息
     */
    @ApiOperation("发送图片消息")
    @PostMapping("/image")
    public Result<Message> sendImageMessage(
            @ApiParam("聊天ID") @RequestParam Long chatId,
            @ApiParam("图片URL") @RequestParam String imageUrl) {
        log.info("发送图片消息请求: chatId={}, imageUrl={}", chatId, imageUrl);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Message message = messageService.sendImageMessage(chatId, imageUrl, currentUserId);
        
        return Result.success(message);
    }

    /**
     * 撤回消息
     */
    @ApiOperation("撤回消息")
    @PostMapping("/{messageId}/recall")
    public Result<Void> recallMessage(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("撤回消息请求: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        messageService.recallMessage(messageId, currentUserId);
        
        return Result.success();
    }

    /**
     * 删除消息
     */
    @ApiOperation("删除消息")
    @DeleteMapping("/{messageId}")
    public Result<Void> deleteMessage(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("删除消息请求: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        messageService.deleteMessage(messageId, currentUserId);
        
        return Result.success();
    }

    // ==================== 消息查询 ====================

    /**
     * 获取聊天消息列表
     */
    @ApiOperation("获取聊天消息列表")
    @GetMapping("/chat/{chatId}")
    public Result<IPage<Message>> getChatMessages(
            @ApiParam("聊天ID") @PathVariable Long chatId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取聊天消息列表: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<Message> result = messageService.getChatMessages(chatId, currentUserId, page, size);
        
        return Result.success(result);
    }

    /**
     * 获取消息详情
     */
    @ApiOperation("获取消息详情")
    @GetMapping("/{messageId}")
    public Result<Message> getMessageById(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("获取消息详情: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Message message = messageService.getMessageById(messageId, currentUserId);
        if (message == null) {
            return Result.error(404, "消息不存在");
        }
        
        return Result.success(message);
    }

    /**
     * 获取最新消息
     */
    @ApiOperation("获取最新消息")
    @GetMapping("/chat/{chatId}/latest")
    public Result<Message> getLatestMessage(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("获取最新消息: chatId={}", chatId);
        
        Message message = messageService.getLatestMessage(chatId);
        return Result.success(message);
    }

    /**
     * 获取未读消息
     */
    @ApiOperation("获取未读消息")
    @GetMapping("/chat/{chatId}/unread")
    public Result<List<Message>> getUnreadMessages(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("获取未读消息: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Message> messages = messageService.getUnreadMessages(chatId, currentUserId);
        
        return Result.success(messages);
    }

    /**
     * 搜索消息
     */
    @ApiOperation("搜索消息")
    @GetMapping("/chat/{chatId}/search")
    public Result<IPage<Message>> searchMessages(
            @ApiParam("聊天ID") @PathVariable Long chatId,
            @ApiParam("关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("搜索消息: chatId={}, keyword={}", chatId, keyword);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<Message> result = messageService.searchMessages(chatId, currentUserId, keyword, page, size);
        
        return Result.success(result);
    }

    // ==================== 消息状态 ====================

    /**
     * 标记消息已读
     */
    @ApiOperation("标记消息已读")
    @PostMapping("/{messageId}/read")
    public Result<Void> markMessageAsRead(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("标记消息已读: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        messageService.markMessageAsRead(messageId, currentUserId);
        
        return Result.success();
    }

    /**
     * 标记多条消息已读
     */
    @ApiOperation("标记多条消息已读")
    @PostMapping("/batch-read")
    public Result<Void> markMessagesAsRead(@ApiParam("消息ID列表") @RequestBody List<Long> messageIds) {
        log.info("批量标记消息已读: messageIds={}", messageIds);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        messageService.markMessagesAsRead(messageIds, currentUserId);
        
        return Result.success();
    }

    /**
     * 获取消息已读状态
     */
    @ApiOperation("获取消息已读状态")
    @GetMapping("/{messageId}/is-read")
    public Result<Boolean> isMessageRead(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("获取消息已读状态: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isRead = messageService.isMessageRead(messageId, currentUserId);
        
        return Result.success(isRead);
    }

    /**
     * 获取消息已读用户列表
     */
    @ApiOperation("获取消息已读用户列表")
    @GetMapping("/{messageId}/read-users")
    public Result<List<Long>> getReadUserIds(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("获取消息已读用户列表: messageId={}", messageId);
        
        List<Long> userIds = messageService.getReadUserIds(messageId);
        return Result.success(userIds);
    }

    // ==================== 业务验证 ====================

    /**
     * 检查消息是否存在
     */
    @ApiOperation("检查消息是否存在")
    @GetMapping("/{messageId}/exists")
    public Result<Boolean> messageExists(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("检查消息是否存在: messageId={}", messageId);
        
        boolean exists = messageService.messageExists(messageId);
        return Result.success(exists);
    }

    /**
     * 检查用户是否是消息发送者
     */
    @ApiOperation("检查用户是否是消息发送者")
    @GetMapping("/{messageId}/is-sender")
    public Result<Boolean> isMessageSender(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("检查用户是否是消息发送者: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isSender = messageService.isMessageSender(messageId, currentUserId);
        
        return Result.success(isSender);
    }

    /**
     * 检查用户是否有权限查看消息
     */
    @ApiOperation("检查用户是否有权限查看消息")
    @GetMapping("/{messageId}/can-view")
    public Result<Boolean> canViewMessage(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("检查用户是否有权限查看消息: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean canView = messageService.canViewMessage(messageId, currentUserId);
        
        return Result.success(canView);
    }

    /**
     * 检查消息是否可以撤回
     */
    @ApiOperation("检查消息是否可以撤回")
    @GetMapping("/{messageId}/can-recall")
    public Result<Boolean> canRecallMessage(@ApiParam("消息ID") @PathVariable Long messageId) {
        log.info("检查消息是否可以撤回: messageId={}", messageId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean canRecall = messageService.canRecallMessage(messageId, currentUserId);
        
        return Result.success(canRecall);
    }

    // ==================== 批量操作 ====================

    /**
     * 删除聊天消息
     */
    @ApiOperation("删除聊天消息")
    @DeleteMapping("/chat/{chatId}")
    public Result<Void> deleteChatMessages(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("删除聊天消息: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        messageService.deleteChatMessages(chatId, currentUserId);
        
        return Result.success();
    }

    /**
     * 批量获取消息信息
     */
    @ApiOperation("批量获取消息信息")
    @PostMapping("/batch")
    public Result<List<Message>> getMessagesByIds(@ApiParam("消息ID列表") @RequestBody List<Long> messageIds) {
        log.info("批量获取消息信息: messageIds={}", messageIds);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Message> messages = messageService.getMessagesByIds(messageIds, currentUserId);
        
        return Result.success(messages);
    }

    // ==================== 统计信息 ====================

    /**
     * 获取聊天消息统计
     */
    @ApiOperation("获取聊天消息统计")
    @GetMapping("/chat/{chatId}/stats")
    public Result<MessageService.MessageStats> getMessageStats(@ApiParam("聊天ID") @PathVariable Long chatId) {
        log.info("获取聊天消息统计: chatId={}", chatId);
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        MessageService.MessageStats stats = messageService.getMessageStats(chatId, currentUserId);
        
        return Result.success(stats);
    }
}
