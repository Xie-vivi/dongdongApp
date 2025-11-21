package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xystapp.entity.PostLike;
import com.xystapp.entity.ActivityLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 点赞Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface LikeMapper extends BaseMapper<PostLike> {

    /**
     * 获取用户点赞的帖子列表
     */
    List<PostLike> getLikedPosts(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取帖子点赞列表
     */
    List<PostLike> getPostLikes(@Param("postId") Long postId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取点赞数量
     */
    int getLikeCount(@Param("targetId") Long targetId, @Param("targetType") String targetType);

    /**
     * 检查是否已点赞
     */
    boolean isLiked(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetType") String targetType);

    /**
     * 取消点赞
     */
    int unlike(@Param("userId") Long userId, @Param("targetId") Long targetId, @Param("targetType") String targetType);

    /**
     * 批量获取点赞状态
     */
    List<Integer> getLikeStatus(@Param("userId") Long userId, @Param("targetIds") List<Long> targetIds, @Param("targetType") String targetType);
}
