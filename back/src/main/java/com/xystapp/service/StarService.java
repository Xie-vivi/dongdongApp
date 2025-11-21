package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivityStar;
import com.xystapp.entity.PostStar;

import java.util.List;

/**
 * 收藏服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface StarService {

    // ==================== 帖子收藏 ====================

    /**
     * 收藏帖子
     */
    void starPost(Long postId, Long userId);

    /**
     * 取消收藏帖子
     */
    void unstarPost(Long postId, Long userId);

    /**
     * 检查是否已收藏帖子
     */
    boolean isPostStarred(Long postId, Long userId);

    /**
     * 获取帖子收藏列表
     */
    IPage<PostStar> getPostStarList(Long postId, Integer page, Integer size);

    /**
     * 获取用户收藏的帖子列表
     */
    IPage<PostStar> getUserStarredPosts(Long userId, Integer page, Integer size);

    // ==================== 活动收藏 ====================

    /**
     * 收藏活动
     */
    void starActivity(Long activityId, Long userId);

    /**
     * 取消收藏活动
     */
    void unstarActivity(Long activityId, Long userId);

    /**
     * 检查是否已收藏活动
     */
    boolean isActivityStarred(Long activityId, Long userId);

    /**
     * 获取活动收藏列表
     */
    IPage<ActivityStar> getActivityStarList(Long activityId, Integer page, Integer size);

    /**
     * 获取用户收藏的活动列表
     */
    IPage<ActivityStar> getUserStarredActivities(Long userId, Integer page, Integer size);

    // ==================== 批量操作 ====================

    /**
     * 批量检查帖子收藏状态
     */
    List<Long> getStarredPostIds(Long userId);

    /**
     * 批量检查活动收藏状态
     */
    List<Long> getStarredActivityIds(Long userId);

    /**
     * 获取收藏统计
     */
    StarStats getStarStats(Long userId);

    /**
     * 收藏统计类
     */
    class StarStats {
        private Long postStarCount;
        private Long activityStarCount;
        private Long totalStarCount;

        public StarStats(Long postStarCount, Long activityStarCount) {
            this.postStarCount = postStarCount;
            this.activityStarCount = activityStarCount;
            this.totalStarCount = postStarCount + activityStarCount;
        }

        public Long getPostStarCount() { return postStarCount; }
        public Long getActivityStarCount() { return activityStarCount; }
        public Long getTotalStarCount() { return totalStarCount; }
    }
}
