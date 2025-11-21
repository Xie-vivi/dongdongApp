package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.CommentCreateRequest;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface CommentService {

    // ==================== 帖子评论 ====================

    /**
     * 创建评论
     */
    Comment createComment(CommentCreateRequest request, Long userId);

    /**
     * 更新评论
     */
    Comment updateComment(Long commentId, String content, Long userId);

    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 获取评论详情
     */
    Comment getCommentById(Long commentId);

    /**
     * 获取帖子评论列表
     */
    IPage<Comment> getPostComments(Long postId, Integer page, Integer size);

    /**
     * 获取帖子评论列表（带查询条件）
     */
    IPage<Comment> getPostCommentsWithQuery(CommentQueryRequest query);

    /**
     * 获取回复评论列表
     */
    IPage<Comment> getReplyComments(Long parentCommentId, Integer page, Integer size);

    /**
     * 获取用户评论列表
     */
    IPage<Comment> getUserComments(Long userId, Integer page, Integer size);

    /**
     * 获取@用户的评论列表
     */
    IPage<Comment> getAtUserComments(Long userId, Integer page, Integer size);

    // ==================== 业务验证 ====================

    /**
     * 检查评论是否存在
     */
    boolean commentExists(Long commentId);

    /**
     * 检查用户是否是评论作者
     */
    boolean isCommentAuthor(Long commentId, Long userId);

    /**
     * 检查用户是否是帖子作者
     */
    boolean isPostAuthor(Long postId, Long userId);

    /**
     * 检查回复深度是否超限
     */
    boolean isReplyDepthExceeded(Long replyToId);

    /**
     * 获取回复深度
     */
    Integer getReplyDepth(Long commentId);

    /**
     * 获取评论回复数量
     */
    Integer getReplyCount(Long commentId);

    // ==================== 批量操作 ====================

    /**
     * 批量删除评论（删除帖子时调用）
     */
    void deleteCommentsByPostId(Long postId);

    /**
     * 批量获取评论统计
     */
    CommentStats getCommentStats(Long postId);

    /**
     * 评论统计类
     */
    class CommentStats {
        private Long totalCount;
        private Long todayCount;
        private Long replyCount;

        public CommentStats(Long totalCount, Long todayCount, Long replyCount) {
            this.totalCount = totalCount;
            this.todayCount = todayCount;
            this.replyCount = replyCount;
        }

        public Long getTotalCount() { return totalCount; }
        public Long getTodayCount() { return todayCount; }
        public Long getReplyCount() { return replyCount; }
    }
}
