package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子评论Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 获取评论详情（包含用户信息）
     */
    Comment selectCommentWithUserInfo(@Param("commentId") Long commentId);

    /**
     * 获取帖子评论列表（包含用户信息）
     */
    IPage<Comment> selectPostCommentsWithUserInfo(Page<Comment> page, @Param("postId") Long postId);

    /**
     * 根据查询条件获取评论列表（包含用户信息）
     */
    IPage<Comment> selectCommentsWithQuery(Page<Comment> page, @Param("query") CommentQueryRequest query);

    /**
     * 获取回复评论列表（包含用户信息）
     */
    IPage<Comment> selectReplyCommentsWithUserInfo(Page<Comment> page, @Param("parentCommentId") Long parentCommentId);

    /**
     * 获取用户评论列表（包含用户信息和帖子信息）
     */
    IPage<Comment> selectUserCommentsWithUserInfo(Page<Comment> page, @Param("userId") Long userId);

    /**
     * 获取@用户评论列表（包含用户信息）
     */
    IPage<Comment> selectAtUserCommentsWithUserInfo(Page<Comment> page, @Param("atUserId") Long atUserId);

    /**
     * 删除评论及其所有回复
     */
    int deleteCommentAndReplies(@Param("commentId") Long commentId);

    /**
     * 删除帖子的所有评论
     */
    int deleteCommentsByPostId(@Param("postId") Long postId);

    /**
     * 检查用户是否存在
     */
    boolean userExists(@Param("userId") Long userId);

    /**
     * 检查用户是否是评论作者
     */
    boolean isCommentAuthor(@Param("commentId") Long commentId, @Param("userId") Long userId);

    /**
     * 获取回复深度
     */
    Integer getReplyDepth(@Param("commentId") Long commentId);

    /**
     * 获取评论回复数量
     */
    Integer getReplyCount(@Param("commentId") Long commentId);

    /**
     * 统计帖子评论数量
     */
    Long countCommentsByPostId(@Param("postId") Long postId);

    /**
     * 统计帖子今日评论数量
     */
    Long countTodayCommentsByPostId(@Param("postId") Long postId);

    /**
     * 统计帖子回复评论数量
     */
    Long countReplyCommentsByPostId(@Param("postId") Long postId);

    /**
     * 获取评论路径（用于计算回复深度）
     */
    List<Long> getCommentPath(@Param("commentId") Long commentId);

    // ==================== 管理员功能相关方法 ====================

    /**
     * 按时间范围统计评论数量
     */
    Long countCommentsByTimeRange(@Param("timeRange") String timeRange);
}
