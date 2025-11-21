package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.PostStar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子收藏Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface PostStarMapper extends BaseMapper<PostStar> {

    /**
     * 获取帖子收藏列表（包含用户信息）
     */
    IPage<PostStar> selectPostStarsWithUserInfo(Page<PostStar> page, @Param("postId") Long postId);

    /**
     * 获取用户收藏的帖子列表（包含帖子信息）
     */
    IPage<PostStar> selectUserStarredPostsWithInfo(Page<PostStar> page, @Param("userId") Long userId);

    /**
     * 统计用户收藏的帖子数量
     */
    Long countUserPostStars(@Param("userId") Long userId);

    /**
     * 获取用户收藏的帖子ID列表
     */
    List<Long> selectStarredPostIds(@Param("userId") Long userId);

    /**
     * 批量检查帖子收藏状态
     */
    List<Long> selectStarredPostIdsByUser(@Param("userId") Long userId, @Param("postIds") List<Long> postIds);
}
