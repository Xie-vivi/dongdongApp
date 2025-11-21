package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.ActivityCommentCreateRequest;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.ActivityComment;

import java.util.List;

/**
 * 活动评论服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface ActivityCommentService {

    // ==================== 活动评论 ====================

    /**
     * 创建活动评论
     */
    ActivityComment createComment(ActivityCommentCreateRequest request, Long userId);

    /**
     * 更新活动评论
     */
    ActivityComment updateComment(Long commentId, String content, Long userId);

    /**
     * 删除活动评论
     */
    void deleteComment(Long commentId, Long userId);

    /**
     * 获取活动评论详情
     */
    ActivityComment getCommentById(Long commentId);

    /**
     * 获取活动评论列表
     */
    IPage<ActivityComment> getActivityComments(Long activityId, Integer page, Integer size);

    /**
     * 获取活动评论列表（带查询条件）
     */
    IPage<ActivityComment> getActivityCommentsWithQuery(CommentQueryRequest query);

    /**
     * 获取回复评论列表
     */
    IPage<ActivityComment> getReplyComments(Long parentCommentId, Integer page, Integer size);

    /**
     * 获取用户活动评论列表
     */
    IPage<ActivityComment> getUserComments(Long userId, Integer page, Integer size);

    /**
     * 获取@用户的活动评论列表
     */
    IPage<ActivityComment> getAtUserComments(Long userId, Integer page, Integer size);

    // ==================== 业务验证 ====================

    /**
     * 检查活动评论是否存在
     */
    boolean commentExists(Long commentId);

    /**
     * 检查用户是否是活动评论作者
     */
    boolean isCommentAuthor(Long commentId, Long userId);

    /**
     * 检查用户是否是活动组织者
     */
    boolean isActivityOrganizer(Long activityId, Long userId);

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
     * 批量删除活动评论（删除活动时调用）
     */
    void deleteCommentsByActivityId(Long activityId);

    /**
     * 批量获取活动评论统计
     */
    ActivityCommentStats getCommentStats(Long activityId);

    /**
     * 活动评论统计类
     */
    class ActivityCommentStats {
        private Long totalCount;
        private Long todayCount;
        private Long replyCount;

        public ActivityCommentStats(Long totalCount, Long todayCount, Long replyCount) {
            this.totalCount = totalCount;
            this.todayCount = todayCount;
            this.replyCount = replyCount;
        }

        public Long getTotalCount() { return totalCount; }
        public Long getTodayCount() { return todayCount; }
        public Long getReplyCount() { return replyCount; }
    }
}
