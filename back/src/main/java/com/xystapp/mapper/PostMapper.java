package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.PostQueryRequest;
import com.xystapp.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 分页查询帖子列表（包含用户信息）
     */
    IPage<Post> selectPostsWithUser(Page<Post> page, @Param("query") PostQueryRequest query);

    /**
     * 根据ID查询帖子详情（包含用户信息）
     */
    Post selectPostWithUserById(@Param("id") Long id);

    /**
     * 查询用户的帖子列表
     */
    IPage<Post> selectPostsByUserId(Page<Post> page, @Param("userId") Long userId, @Param("status") String status);

    /**
     * 更新帖子统计信息
     */
    int updatePostStats(@Param("id") Long id, @Param("likesCount") Integer likesCount, 
                       @Param("starsCount") Integer starsCount, @Param("commentsCount") Integer commentsCount);

    /**
     * 批量查询帖子信息
     */
    List<Post> selectPostsByIds(@Param("ids") List<Long> ids);

    /**
     * 检查用户是否是帖子作者
     */
    boolean isPostOwner(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 更新帖子点赞数
     */
    int updateLikesCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 更新帖子收藏数
     */
    int updateStarsCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 更新帖子评论数
     */
    int updateCommentsCount(@Param("id") Long id, @Param("delta") int delta);

    // ==================== 管理员功能相关方法 ====================

    /**
     * 获取待审核帖子列表
     */
    List<Post> selectPendingPosts(@Param("page") Integer page, @Param("size") Integer size);

    /**
     * 统计待审核帖子数量
     */
    Long countPendingPosts();

    /**
     * 按时间范围统计帖子数量
     */
    Long countPostsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 获取热门帖子
     */
    List<Post> selectHotPosts(@Param("limit") Integer limit);

    // ==================== 搜索相关方法 ====================

    /**
     * 全文搜索帖子
     */
    List<Post> searchPostsByKeyword(@Param("keyword") String keyword, 
                                   @Param("page") Integer page, 
                                   @Param("size") Integer size);

    /**
     * 全文搜索帖子（带用户信息）
     */
    List<Post> searchPostsWithUserByKeyword(@Param("keyword") String keyword, 
                                           @Param("page") Integer page, 
                                           @Param("size") Integer size);

    /**
     * 统计搜索结果数量
     */
    Long countSearchPostsByKeyword(@Param("keyword") String keyword);

    /**
     * 按标签搜索帖子
     */
    List<Post> selectPostsByTags(@Param("tags") List<String> tags, 
                                @Param("page") Integer page, 
                                @Param("size") Integer size);

    /**
     * 按用户搜索帖子
     */
    List<Post> selectPostsByUser(@Param("userId") Long userId, 
                                @Param("page") Integer page, 
                                @Param("size") Integer size);

    /**
     * 获取相关帖子（基于标签或关键词）
     */
    List<Post> selectRelatedPosts(@Param("postId") Long postId, 
                                 @Param("limit") Integer limit);
}
