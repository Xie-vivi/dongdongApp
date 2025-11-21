package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.CommentQueryRequest;
import com.xystapp.entity.ActivityComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动评论Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ActivityCommentMapper extends BaseMapper<ActivityComment> {

    /**
     * 获取活动评论详情（包含用户信息）
     */
    ActivityComment selectCommentWithUserInfo(@Param("commentId") Long commentId);

    /**
     * 获取活动评论列表（包含用户信息）
     */
    IPage<ActivityComment> selectActivityCommentsWithUserInfo(Page<ActivityComment> page, @Param("activityId") Long activityId);

    /**
     * 根据查询条件获取评论列表（包含用户信息）
     */
    IPage<ActivityComment> selectCommentsWithQuery(Page<ActivityComment> page, @Param("query") CommentQueryRequest query);

    /**
     * 获取回复评论列表（包含用户信息）
     */
    IPage<ActivityComment> selectReplyCommentsWithUserInfo(Page<ActivityComment> page, @Param("parentCommentId") Long parentCommentId);

    /**
     * 获取用户活动评论列表（包含用户信息和活动信息）
     */
    IPage<ActivityComment> selectUserCommentsWithUserInfo(Page<ActivityComment> page, @Param("userId") Long userId);

    /**
     * 获取@用户活动评论列表（包含用户信息）
     */
    IPage<ActivityComment> selectAtUserCommentsWithUserInfo(Page<ActivityComment> page, @Param("atUserId") Long atUserId);

    /**
     * 删除评论及其所有回复
     */
    int deleteCommentAndReplies(@Param("commentId") Long commentId);

    /**
     * 删除活动的所有评论
     */
    int deleteCommentsByActivityId(@Param("activityId") Long activityId);

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
     * 统计活动评论数量
     */
    Long countCommentsByActivityId(@Param("activityId") Long activityId);

    /**
     * 统计活动今日评论数量
     */
    Long countTodayCommentsByActivityId(@Param("activityId") Long activityId);

    /**
     * 统计活动回复评论数量
     */
    Long countReplyCommentsByActivityId(@Param("activityId") Long activityId);

    /**
     * 获取评论路径（用于计算回复深度）
     */
    List<Long> getCommentPath(@Param("commentId") Long commentId);
}
