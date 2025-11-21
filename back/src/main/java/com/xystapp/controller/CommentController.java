package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.dto.CommentCreateRequest;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.Comment;
import com.xystapp.service.CommentService;
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
 * 评论控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "评论管理")
@RestController
@RequestMapping("/comments")
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    // ==================== 帖子评论 ====================

    /**
     * 创建评论
     */
    @ApiOperation("创建评论")
    @PostMapping
    public Result<Comment> createComment(@Valid @RequestBody CommentCreateRequest request) {
        log.info("创建评论请求: postId={}, content={}", request.getPostId(), request.getContent());
        
        Long userId = SecurityUtils.getCurrentUserId();
        Comment comment = commentService.createComment(request, userId);
        
        return Result.success(comment);
    }

    /**
     * 更新评论
     */
    @ApiOperation("更新评论")
    @PutMapping("/{commentId}")
    public Result<Comment> updateComment(
            @ApiParam("评论ID") @PathVariable Long commentId,
            @ApiParam("评论内容") @RequestParam String content) {
        log.info("更新评论请求: commentId={}", commentId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Comment comment = commentService.updateComment(commentId, content, userId);
        
        return Result.success(comment);
    }

    /**
     * 删除评论
     */
    @ApiOperation("删除评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("删除评论请求: commentId={}", commentId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        commentService.deleteComment(commentId, userId);
        
        return Result.success();
    }

    /**
     * 获取评论详情
     */
    @ApiOperation("获取评论详情")
    @GetMapping("/{commentId}")
    public Result<Comment> getCommentById(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("获取评论详情: commentId={}", commentId);
        
        Comment comment = commentService.getCommentById(commentId);
        if (comment == null) {
            return Result.error(404, "评论不存在");
        }
        
        return Result.success(comment);
    }

    /**
     * 获取帖子评论列表
     */
    @ApiOperation("获取帖子评论列表")
    @GetMapping("/posts/{postId}")
    public Result<IPage<Comment>> getPostComments(
            @ApiParam("帖子ID") @PathVariable Long postId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取帖子评论列表: postId={}", postId);
        
        IPage<Comment> result = commentService.getPostComments(postId, page, size);
        return Result.success(result);
    }

    /**
     * 获取回复评论列表
     */
    @ApiOperation("获取回复评论列表")
    @GetMapping("/replies/{parentCommentId}")
    public Result<IPage<Comment>> getReplyComments(
            @ApiParam("父评论ID") @PathVariable Long parentCommentId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取回复评论列表: parentCommentId={}", parentCommentId);
        
        IPage<Comment> result = commentService.getReplyComments(parentCommentId, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户评论列表
     */
    @ApiOperation("获取用户评论列表")
    @GetMapping("/users/{userId}")
    public Result<IPage<Comment>> getUserComments(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户评论列表: userId={}", userId);
        
        IPage<Comment> result = commentService.getUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取@用户评论列表
     */
    @ApiOperation("获取@用户评论列表")
    @GetMapping("/at-user/{userId}")
    public Result<IPage<Comment>> getAtUserComments(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取@用户评论列表: userId={}", userId);
        
        IPage<Comment> result = commentService.getAtUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 搜索评论
     */
    @ApiOperation("搜索评论")
    @PostMapping("/search")
    public Result<IPage<Comment>> searchComments(@Valid @RequestBody CommentQueryRequest query) {
        log.info("搜索评论: {}", query);
        
        IPage<Comment> result = commentService.getPostCommentsWithQuery(query);
        return Result.success(result);
    }

    // ==================== 业务验证 ====================

    /**
     * 检查评论是否存在
     */
    @ApiOperation("检查评论是否存在")
    @GetMapping("/{commentId}/exists")
    public Result<Boolean> commentExists(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("检查评论是否存在: commentId={}", commentId);
        
        boolean exists = commentService.commentExists(commentId);
        return Result.success(exists);
    }

    /**
     * 检查用户是否是评论作者
     */
    @ApiOperation("检查用户是否是评论作者")
    @GetMapping("/{commentId}/is-author")
    public Result<Boolean> isCommentAuthor(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("检查用户是否是评论作者: commentId={}", commentId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isAuthor = commentService.isCommentAuthor(commentId, userId);
        return Result.success(isAuthor);
    }

    /**
     * 获取回复深度
     */
    @ApiOperation("获取回复深度")
    @GetMapping("/{commentId}/reply-depth")
    public Result<Integer> getReplyDepth(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("获取回复深度: commentId={}", commentId);
        
        Integer depth = commentService.getReplyDepth(commentId);
        return Result.success(depth);
    }

    /**
     * 获取评论回复数量
     */
    @ApiOperation("获取评论回复数量")
    @GetMapping("/{commentId}/reply-count")
    public Result<Integer> getReplyCount(@ApiParam("评论ID") @PathVariable Long commentId) {
        log.info("获取评论回复数量: commentId={}", commentId);
        
        Integer count = commentService.getReplyCount(commentId);
        return Result.success(count);
    }

    // ==================== 统计信息 ====================

    /**
     * 获取评论统计
     */
    @ApiOperation("获取评论统计")
    @GetMapping("/stats/{postId}")
    public Result<CommentService.CommentStats> getCommentStats(@ApiParam("帖子ID") @PathVariable Long postId) {
        log.info("获取评论统计: postId={}", postId);
        
        CommentService.CommentStats stats = commentService.getCommentStats(postId);
        return Result.success(stats);
    }

    // ==================== 个人相关 ====================

    /**
     * 获取我的评论列表
     */
    @ApiOperation("获取我的评论列表")
    @GetMapping("/my/comments")
    public Result<IPage<Comment>> getMyComments(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我的评论列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Comment> result = commentService.getUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取@我的评论列表
     */
    @ApiOperation("获取@我的评论列表")
    @GetMapping("/my/at-comments")
    public Result<IPage<Comment>> getMyAtComments(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取@我的评论列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Comment> result = commentService.getAtUserComments(userId, page, size);
        return Result.success(result);
    }

    /**
     * 检查回复深度是否超限
     */
    @ApiOperation("检查回复深度是否超限")
    @GetMapping("/reply-depth-check/{replyToId}")
    public Result<Boolean> isReplyDepthExceeded(@ApiParam("回复评论ID") @PathVariable Long replyToId) {
        log.info("检查回复深度是否超限: replyToId={}", replyToId);
        
        boolean exceeded = commentService.isReplyDepthExceeded(replyToId);
        return Result.success(exceeded);
    }
}
