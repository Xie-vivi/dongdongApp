package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.PostLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子点赞Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface PostLikeMapper extends BaseMapper<PostLike> {

    /**
     * 获取帖子点赞列表（包含用户信息）
     */
    IPage<PostLike> selectPostLikesWithUserInfo(Page<PostLike> page, @Param("postId") Long postId);

    /**
     * 获取用户点赞的帖子列表（包含帖子信息）
     */
    IPage<PostLike> selectUserLikedPostsWithInfo(Page<PostLike> page, @Param("userId") Long userId);

    /**
     * 统计用户点赞的帖子数量
     */
    Long countUserPostLikes(@Param("userId") Long userId);

    /**
     * 获取用户点赞的帖子ID列表
     */
    List<Long> selectLikedPostIds(@Param("userId") Long userId);

    /**
     * 批量检查帖子点赞状态
     */
    List<Long> selectLikedPostIdsByUser(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);
}
