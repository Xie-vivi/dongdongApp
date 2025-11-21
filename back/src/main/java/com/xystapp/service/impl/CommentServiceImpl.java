package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.CommentCreateRequest;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.Comment;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.CommentMapper;
import com.xystapp.mapper.PostMapper;
import com.xystapp.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 评论服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    private static final int MAX_REPLY_DEPTH = 3; // 最大回复深度

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment createComment(CommentCreateRequest request, Long userId) {
        log.info("创建评论请求: postId={}, userId={}, content={}", 
                request.getPostId(), userId, request.getContent());

        // 验证参数
        if (request.getPostId() == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查帖子是否存在
        if (postMapper.selectById(request.getPostId()) == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        // 验证@用户是否存在
        if (request.getAtUserId() != null) {
            if (!commentMapper.userExists(request.getAtUserId())) {
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
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setAtUserId(request.getAtUserId());
        comment.setReplyToId(request.getReplyToId());

        int result = commentMapper.insert(comment);
        if (result <= 0) {
            throw new BusinessException(500, "创建评论失败");
        }

        // 更新帖子评论数
        postMapper.updateCommentsCount(request.getPostId(), 1);

        log.info("创建评论成功: commentId={}", comment.getId());
        return comment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Comment updateComment(Long commentId, String content, Long userId) {
        log.info("更新评论请求: commentId={}, userId={}", commentId, userId);

        // 验证参数
        if (commentId == null || userId == null || !StringUtils.hasText(content)) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查评论是否存在
        Comment existingComment = getCommentById(commentId);
        if (existingComment == null) {
            throw new BusinessException(404, "评论不存在");
        }

        // 检查权限
        if (!isCommentAuthor(commentId, userId)) {
            throw new BusinessException(403, "无权限修改该评论");
        }

        // 更新评论
        existingComment.setContent(content);
        int result = commentMapper.updateById(existingComment);
        if (result <= 0) {
            throw new BusinessException(500, "更新评论失败");
        }

        log.info("更新评论成功: commentId={}", commentId);
        return existingComment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId, Long userId) {
        log.info("删除评论请求: commentId={}, userId={}", commentId, userId);

        // 验证参数
        if (commentId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查评论是否存在
        Comment comment = getCommentById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }

        // 检查权限（评论作者或帖子作者可以删除）
        if (!isCommentAuthor(commentId, userId) && !isPostAuthor(comment.getPostId(), userId)) {
            throw new BusinessException(403, "无权限删除该评论");
        }

        // 删除评论及其所有回复
        int deletedCount = commentMapper.deleteCommentAndReplies(commentId);
        if (deletedCount <= 0) {
            throw new BusinessException(500, "删除评论失败");
        }

        // 更新帖子评论数
        postMapper.updateCommentsCount(comment.getPostId(), -deletedCount);

        log.info("删除评论成功: commentId={}, 删除数量={}", commentId, deletedCount);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        if (commentId == null) {
            return null;
        }

        Comment comment = commentMapper.selectCommentWithUserInfo(commentId);
        if (comment != null) {
            // 设置回复深度和回复数量
            comment.setReplyDepth(getReplyDepth(commentId));
            comment.setReplyCount(getReplyCount(commentId));
        }

        return comment;
    }

    @Override
    public IPage<Comment> getPostComments(Long postId, Integer page, Integer size) {
        log.info("获取帖子评论列表: postId={}", postId);

        if (postId == null) {
            throw new BusinessException(400, "帖子ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Comment> pageParam = new Page<>(page, size);
        IPage<Comment> result = commentMapper.selectPostCommentsWithUserInfo(pageParam, postId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(0); // 顶级评论深度为0
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取帖子评论列表成功: postId={}, 总数={}", postId, result.getTotal());
        return result;
    }

    @Override
    public IPage<Comment> getPostCommentsWithQuery(CommentQueryRequest query) {
        log.info("获取帖子评论列表（带查询条件）");

        // 设置默认值
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(20);
        }

        Page<Comment> pageParam = new Page<>(query.getPage(), query.getSize());
        IPage<Comment> result = commentMapper.selectCommentsWithQuery(pageParam, query);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取帖子评论列表成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    public IPage<Comment> getReplyComments(Long parentCommentId, Integer page, Integer size) {
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

        Page<Comment> pageParam = new Page<>(page, size);
        IPage<Comment> result = commentMapper.selectReplyCommentsWithUserInfo(pageParam, parentCommentId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取回复评论列表成功: parentCommentId={}, 总数={}", parentCommentId, result.getTotal());
        return result;
    }

    @Override
    public IPage<Comment> getUserComments(Long userId, Integer page, Integer size) {
        log.info("获取用户评论列表: userId={}", userId);

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

        Page<Comment> pageParam = new Page<>(page, size);
        IPage<Comment> result = commentMapper.selectUserCommentsWithUserInfo(pageParam, userId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取用户评论列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public IPage<Comment> getAtUserComments(Long userId, Integer page, Integer size) {
        log.info("获取@用户评论列表: userId={}", userId);

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

        Page<Comment> pageParam = new Page<>(page, size);
        IPage<Comment> result = commentMapper.selectAtUserCommentsWithUserInfo(pageParam, userId);

        // 设置回复深度和回复数量
        result.getRecords().forEach(comment -> {
            comment.setReplyDepth(getReplyDepth(comment.getId()));
            comment.setReplyCount(getReplyCount(comment.getId()));
        });

        log.info("获取@用户评论列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    // ==================== 业务验证 ====================

    @Override
    public boolean commentExists(Long commentId) {
        if (commentId == null) {
            return false;
        }

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", commentId);

        return commentMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean isCommentAuthor(Long commentId, Long userId) {
        if (commentId == null || userId == null) {
            return false;
        }

        return commentMapper.isCommentAuthor(commentId, userId);
    }

    @Override
    public boolean isPostAuthor(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }

        return postMapper.isPostOwner(postId, userId);
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

        return commentMapper.getReplyDepth(commentId);
    }

    @Override
    public Integer getReplyCount(Long commentId) {
        if (commentId == null) {
            return 0;
        }

        return commentMapper.getReplyCount(commentId);
    }

    // ==================== 批量操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCommentsByPostId(Long postId) {
        log.info("批量删除帖子评论: postId={}", postId);

        if (postId == null) {
            throw new BusinessException(400, "帖子ID不能为空");
        }

        int deletedCount = commentMapper.deleteCommentsByPostId(postId);
        log.info("批量删除帖子评论成功: postId={}, 删除数量={}", postId, deletedCount);
    }

    @Override
    public CommentStats getCommentStats(Long postId) {
        log.info("获取评论统计: postId={}", postId);

        if (postId == null) {
            throw new BusinessException(400, "帖子ID不能为空");
        }

        Long totalCount = commentMapper.countCommentsByPostId(postId);
        Long todayCount = commentMapper.countTodayCommentsByPostId(postId);
        Long replyCount = commentMapper.countReplyCommentsByPostId(postId);

        CommentStats stats = new CommentStats(totalCount, todayCount, replyCount);

        log.info("获取评论统计成功: postId={}, total={}, today={}, reply={}", 
                postId, totalCount, todayCount, replyCount);

        return stats;
    }
}
