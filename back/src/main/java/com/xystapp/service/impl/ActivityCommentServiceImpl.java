package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.ActivityCommentCreateRequest;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.ActivityComment;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.ActivityCommentMapper;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.service.ActivityCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * 活动评论服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class ActivityCommentServiceImpl implements ActivityCommentService {

    private static final Logger log = LoggerFactory.getLogger(ActivityCommentServiceImpl.class);

    @Autowired
    private ActivityCommentMapper activityCommentMapper;

    @Autowired
    private ActivityMapper activityMapper;

    private static final int MAX_REPLY_DEPTH = 3; // 最大回复深度

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivityComment createComment(ActivityCommentCreateRequest request, Long userId) {
        log.info("创建活动评论请求: activityId={}, userId={}, content={}", 
                request.getActivityId(), userId, request.getContent());

        // 验证参数
        if (request.getActivityId() == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查活动是否存在
        if (activityMapper.selectById(request.getActivityId()) == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 验证@用户是否存在
        if (request.getAtUserId() != null) {
            if (!activityCommentMapper.userExists(request.getAtUserId())) {
                throw new BusinessException(404, "@用户不存在");
            }
        }

        // 验证回复评论是否存在
        if (request.getReplyToId() != null) {
            if (!commentExists(request.getReplyToId())) {
                throw new BusinessException(404, "回复的评论不存在");
            }
            
            // 检查回复深度
            if (isReplyDepthExceeded(request.getReplyToId())) {
                throw new BusinessException(400, "回复深度超过限制");
            }
        }

        // 创建评论
        ActivityComment comment = new ActivityComment();
        comment.setActivityId(request.getActivityId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setAtUserId(request.getAtUserId());
        comment.setReplyToId(request.getReplyToId());

        int result = activityCommentMapper.insert(comment);
        if (result <= 0) {
            throw new BusinessException(500, "创建评论失败");
        }

        // 更新活动评论数
        activityMapper.updateCommentsCount(request.getActivityId(), 1);

        log.info("创建活动评论成功: commentId={}", comment.getId());
        return comment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivityComment updateComment(Long commentId, String content, Long userId) {
        log.info("更新活动评论请求: commentId={}, userId={}", commentId, userId);

        // 验证参数
        if (commentId == null || userId == null || !StringUtils.hasText(content)) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查评论是否存在
        ActivityComment existingComment = getCommentById(commentId);
        if (existingComment == null) {
            throw new BusinessException(404, "评论不存在");
        }

        // 检查权限
        if (!isCommentAuthor(commentId, userId)) {
            throw new BusinessException(403, "无权限修改该评论");
        }

        // 更新评论
        existingComment.setContent(content);
        int result = activityCommentMapper.updateById(existingComment);
        if (result <= 0) {
            throw new BusinessException(500, "更新评论失败");
        }

        log.info("更新活动评论成功: commentId={}", commentId);
        return existingComment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        log.info("删除活动评论请求: commentId={}, userId={}", commentId, userId);

        // 验证参数
        if (commentId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查评论是否存在
        ActivityComment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }

        // 检查权限（评论作者或活动组织者可以删除）
        if (!isCommentAuthor(commentId, userId) && !isActivityOrganizer(comment.getActivityId(), userId)) {
            throw new BusinessException(403, "无权限删除该评论");
        }

        // 删除评论及其所有回复
        int deletedCount = activityCommentMapper.deleteCommentAndReplies(commentId);
        if (deletedCount <= 0) {
            throw new BusinessException(500, "删除评论失败");
        }

        // 更新活动评论数
        activityMapper.updateCommentsCount(comment.getActivityId(), -deletedCount);

        log.info("删除活动评论成功: commentId={}, 删除数量={}", commentId, deletedCount);
    }

    @Override
    public ActivityComment getCommentById(Long commentId) {
        if (commentId == null) {
            return null;
        }

        ActivityComment comment = activityCommentMapper.selectCommentWithUserInfo(commentId);
        if (comment != null) {
            // 设置回复深度和回复数量
            comment.setReplyDepth(getReplyDepth(commentId));
            comment.setReplyCount(getReplyCount(commentId));
        }

        return comment;
    }

    @Override
    public IPage<ActivityComment> getActivityComments(Long activityId, Integer page, Integer size) {
        log.info("获取活动评论列表: activityId={}", activityId);

        if (activityId == null) {
            throw new BusinessException(400, "活动ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ActivityComment> pageParam = new Page<>(page, size);
        IPage<ActivityComment> result = activityCommentMapper.selectActivityCommentsWithUserInfo(pageParam, activityId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(0); // 顶级评论深度为0
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取活动评论列表成功: activityId={}, 总数={}", activityId, result.getTotal());
        return result;
    }

    @Override
    public IPage<ActivityComment> getActivityCommentsWithQuery(CommentQueryRequest query) {
        log.info("获取活动评论列表（带查询条件）");

        // 设置默认值
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(20);
        }

        Page<ActivityComment> pageParam = new Page<>(query.getPage(), query.getSize());
        IPage<ActivityComment> result = activityCommentMapper.selectCommentsWithQuery(pageParam, query);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取活动评论列表成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    public IPage<ActivityComment> getReplyComments(Long parentCommentId, Integer page, Integer size) {
        log.info("获取回复评论列表: parentCommentId={}", parentCommentId);

        if (parentCommentId == null) {
            throw new BusinessException(400, "父评论ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ActivityComment> pageParam = new Page<>(page, size);
        IPage<ActivityComment> result = activityCommentMapper.selectReplyCommentsWithUserInfo(pageParam, parentCommentId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取回复评论列表成功: parentCommentId={}, 总数={}", parentCommentId, result.getTotal());
        return result;
    }

    @Override
    public IPage<ActivityComment> getUserComments(Long userId, Integer page, Integer size) {
        log.info("获取用户活动评论列表: userId={}", userId);

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

        Page<ActivityComment> pageParam = new Page<>(page, size);
        IPage<ActivityComment> result = activityCommentMapper.selectUserCommentsWithUserInfo(pageParam, userId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取用户活动评论列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public IPage<ActivityComment> getAtUserComments(Long userId, Integer page, Integer size) {
        log.info("获取@用户活动评论列表: userId={}", userId);

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

        Page<ActivityComment> pageParam = new Page<>(page, size);
        IPage<ActivityComment> result = activityCommentMapper.selectAtUserCommentsWithUserInfo(pageParam, userId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取@用户活动评论列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    // ==================== 业务验证 ====================

    @Override
    public boolean commentExists(Long commentId) {
        if (commentId == null) {
            return false;
        }

        QueryWrapper<ActivityComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", commentId);

        return activityCommentMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean isCommentAuthor(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            return false;
        }

        return activityCommentMapper.isCommentAuthor(commentId, userId);
    }

    @Override
    public boolean isActivityOrganizer(Long activityId, Long userId) {
        if (activityId == null || userId == null) {
            return false;
        }

        return activityMapper.isActivityOwner(activityId, userId);
    }

    @Override
    public boolean isReplyDepthExceeded(Long replyToId) {
        Integer depth = getReplyDepth(replyToId);
        return depth >= MAX_REPLY_DEPTH;
    }

    @Override
    public Integer getReplyDepth(Long commentId) {
        if (commentId == null) {
            return 0;
        }

        return activityCommentMapper.getReplyDepth(commentId);
    }

    @Override
    public Integer getReplyCount(Long commentId) {
        if (commentId == null) {
            return 0;
        }

        return activityCommentMapper.getReplyCount(commentId);
    }

    // ==================== 批量操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommentsByActivityId(Long activityId) {
        log.info("批量删除活动评论: activityId={}", activityId);

        if (activityId == null) {
            throw new BusinessException(400, "活动ID不能为空");
        }

        int deletedCount = activityCommentMapper.deleteCommentsByActivityId(activityId);
        log.info("批量删除活动评论成功: activityId={}, 删除数量={}", activityId, deletedCount);
    }

    @Override
    public ActivityCommentStats getCommentStats(Long activityId) {
        log.info("获取活动评论统计: activityId={}", activityId);

        if (activityId == null) {
            throw new BusinessException(400, "活动ID不能为空");
        }

        Long totalCount = activityCommentMapper.countCommentsByActivityId(activityId);
        Long todayCount = activityCommentMapper.countTodayCommentsByActivityId(activityId);
        Long replyCount = activityCommentMapper.countReplyCommentsByActivityId(activityId);

        ActivityCommentStats stats = new ActivityCommentStats(totalCount, todayCount, replyCount);

        log.info("获取活动评论统计成功: activityId={}, total={}, today={}, reply={}", 
                activityId, totalCount, todayCount, replyCount);

        return stats;
    }
}
