package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivityLike;
import com.xystapp.entity.PostLike;

import java.util.List;

/**
 * 点赞服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface LikeService {

    // ==================== 帖子点赞 ====================

    /**
     * 点赞帖子
     */
    void likePost(Long postId, Long userId);

    /**
     * 取消点赞帖子
     */
    void unlikePost(Long postId, Long userId);

    /**
     * 检查是否已点赞帖子
     */
    boolean isPostLiked(Long postId, Long userId);

    /**
     * 获取帖子点赞列表
     */
    IPage<PostLike> getPostLikeList(Long postId, Integer page, Integer size);

    /**
     * 获取用户点赞的帖子列表
     */
    IPage<PostLike> getUserLikedPosts(Long userId, Integer page, Integer size);

    // ==================== 活动点赞 ====================

    /**
     * 点赞活动
     */
    void likeActivity(Long activityId, Long userId);

    /**
     * 取消点赞活动
     */
    void unlikeActivity(Long activityId, Long userId);

    /**
     * 检查是否已点赞活动
     */
    boolean isActivityLiked(Long activityId, Long userId);

    /**
     * 获取活动点赞列表
     */
    IPage<ActivityLike> getActivityLikeList(Long activityId, Integer page, Integer size);

    /**
     * 获取用户点赞的活动列表
     */
    IPage<ActivityLike> getUserLikedActivities(Long userId, Integer page, Integer size);

    // ==================== 批量操作 ====================

    /**
     * 批量检查帖子点赞状态
     */
    List<Long> getLikedPostIds(Long userId);

    /**
     * 批量检查活动点赞状态
     */
    List<Long> getLikedActivityIds(Long userId);

    /**
     * 获取点赞统计
     */
    LikeStats getLikeStats(Long userId);

    /**
     * 点赞统计类
     */
    class LikeStats {
        private Long postLikeCount;
        private Long activityLikeCount;
        private Long totalLikeCount;

        public LikeStats(Long postLikeCount, Long activityLikeCount) {
            this.postLikeCount = postLikeCount;
            this.activityLikeCount = activityLikeCount;
            this.totalLikeCount = postLikeCount + activityLikeCount;
        }

        public Long getPostLikeCount() { return postLikeCount; }
        public Long getActivityLikeCount() { return activityLikeCount; }
        public Long getTotalLikeCount() { return totalLikeCount; }
    }
}
