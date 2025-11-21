package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.dto.ActivityCommentCreateRequest;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.ActivityComment;
import com.xystapp.service.ActivityCommentService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 活动评论控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "活动评论管理")
@RestController
@RequestMapping("/activity-comments")
public class ActivityCommentController {

    private static final Logger log = LoggerFactory.getLogger(ActivityCommentController.class);

    @Autowired
    private ActivityCommentService activityCommentService;

    // ==================== 活动评论 ====================

    /**
     * 创建活动评论
     */
    @ApiOperation("创建活动评论")
    @PostMapping
    public Result<ActivityComment> createComment(@Valid @RequestBody ActivityCommentCreateRequest request) {
        log.info("创建活动评论请求: activityId={}, content={}", request.getActivityId(), request.getContent());
        
        Long userId = SecurityUtils.getCurrentUserId();
        ActivityComment comment = activityCommentService.createComment(request, userId);
        
        return Result.success(comment);
    }

    /**
     * 更新活动评论
     */
    @ApiOperation("更新活动评论")
    @PutMapping("/{commentId}")
    public Result<ActivityComment> updateComment(
            @ApiParam("评论ID") @PathVariable Long commentId,
            @ApiParam("评论内容") @RequestParam String content) {
        log.info("更新活动评论请求: commentId={}", commentId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        ActivityComment comment = activityCommentService.updateComment(commentId, content, userId);
        
        return Result.success(comment);
    }

    /**
     * 删除活动评论
     */
    @ApiOperation("删除活动评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("删除活动评论请求: commentId={}", commentId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        activityCommentService.deleteComment(commentId, userId);
        
        return Result.success();
    }

    /**
     * 获取活动评论详情
     */
    @ApiOperation("获取活动评论详情")
    @GetMapping("/{commentId}")
    public Result<ActivityComment> getCommentById(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("获取活动评论详情: commentId={}", commentId);
        
        ActivityComment comment = activityCommentService.getCommentById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        
        return Result.success(comment);
    }

    /**
     * 获取活动评论列表
     */
    @ApiOperation("获取活动评论列表")
    @GetMapping("/activities/{activityId}")
    public Result<IPage<ActivityComment>> getActivityComments(
            @ApiParam("活动ID") @PathVariable Long activityId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取活动评论列表: activityId={}", activityId);
        
        IPage<ActivityComment> result = activityCommentService.getActivityComments(activityId, page, size);
        return Result.success(result);
    }

    /**
     * 获取回复评论列表
     */
    @ApiOperation("获取回复评论列表")
    @GetMapping("/replies/{parentCommentId}")
    public Result<IPage<ActivityComment>> getReplyComments(
            @ApiParam("父评论ID") @PathVariable Long parentCommentId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取回复评论列表: parentCommentId={}", parentCommentId);
        
        IPage<ActivityComment> result = activityCommentService.getReplyComments(parentCommentId, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户活动评论列表
     */
    @ApiOperation("获取用户活动评论列表")
    @GetMapping("/users/{userId}")
    public Result<IPage<ActivityComment>> getUserComments(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户活动评论列表: userId={}", userId);
        
        IPage<ActivityComment> result = activityCommentService.getUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取@用户活动评论列表
     */
    @ApiOperation("获取@用户活动评论列表")
    @GetMapping("/at-user/{userId}")
    public Result<IPage<ActivityComment>> getAtUserComments(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取@用户活动评论列表: userId={}", userId);
        
        IPage<ActivityComment> result = activityCommentService.getAtUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 搜索活动评论
     */
    @ApiOperation("搜索活动评论")
    @PostMapping("/search")
    public Result<IPage<ActivityComment>> searchComments(@Valid @RequestBody CommentQueryRequest query) {
        log.info("搜索活动评论: {}", query);
        
        IPage<ActivityComment> result = activityCommentService.getActivityCommentsWithQuery(query);
        return Result.success(result);
    }

    // ==================== 业务验证 ====================

    /**
     * 检查活动评论是否存在
     */
    @ApiOperation("检查活动评论是否存在")
    @GetMapping("/{commentId}/exists")
    public Result<Boolean> commentExists(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("检查活动评论是否存在: commentId={}", commentId);
        
        boolean exists = activityCommentService.commentExists(commentId);
        return Result.success(exists);
    }

    /**
     * 检查用户是否是活动评论作者
     */
    @ApiOperation("检查用户是否是活动评论作者")
    @GetMapping("/{commentId}/is-author")
    public Result<Boolean> isCommentAuthor(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("检查用户是否是活动评论作者: commentId={}", commentId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isAuthor = activityCommentService.isCommentAuthor(commentId, userId);
        return Result.success(isAuthor);
    }

    /**
     * 获取回复深度
     */
    @ApiOperation("获取回复深度")
    @GetMapping("/{commentId}/reply-depth")
    public Result<Integer> getReplyDepth(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("获取回复深度: commentId={}", commentId);
        
        Integer depth = activityCommentService.getReplyDepth(commentId);
        return Result.success(depth);
    }

    /**
     * 获取评论回复数量
     */
    @ApiOperation("获取评论回复数量")
    @GetMapping("/{commentId}/reply-count")
    public Result<Integer> getReplyCount(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("获取评论回复数量: commentId={}", commentId);
        
        Integer count = activityCommentService.getReplyCount(commentId);
        return Result.success(count);
    }

    // ==================== 统计信息 ====================

    /**
     * 获取活动评论统计
     */
    @ApiOperation("获取活动评论统计")
    @GetMapping("/stats/{activityId}")
    public Result<ActivityCommentService.ActivityCommentStats> getCommentStats(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("获取活动评论统计: activityId={}", activityId);
        
        ActivityCommentService.ActivityCommentStats stats = activityCommentService.getCommentStats(activityId);
        return Result.success(stats);
    }

    // ==================== 个人相关 ====================

    /**
     * 获取我的活动评论列表
     */
    @ApiOperation("获取我的活动评论列表")
    @GetMapping("/my/comments")
    public Result<IPage<ActivityComment>> getMyComments(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我的活动评论列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<ActivityComment> result = activityCommentService.getUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取@我的活动评论列表
     */
    @ApiOperation("获取@我的活动评论列表")
    @GetMapping("/my/at-comments")
    public Result<IPage<ActivityComment>> getMyAtComments(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取@我的活动评论列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<ActivityComment> result = activityCommentService.getAtUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 检查回复深度是否超限
     */
    @ApiOperation("检查回复深度是否超限")
    @GetMapping("/reply-depth-check/{replyToId}")
    public Result<Boolean> isReplyDepthExceeded(@ApiParam("回复评论ID") @PathVariable Long replyToId) {
        log.info("检查回复深度是否超限: replyToId={}", replyToId);
        
        boolean exceeded = activityCommentService.isReplyDepthExceeded(replyToId);
        return Result.success(exceeded);
    }
}
