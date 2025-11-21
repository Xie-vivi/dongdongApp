package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.UserFollow;

import java.util.List;

/**
 * 用户关注服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface FollowService {

    /**
     * 关注用户
     */
    void followUser(Long followingId, Long followerId);

    /**
     * 取消关注
     */
    void unfollowUser(Long followingId, Long followerId);

    /**
     * 检查是否已关注
     */
    boolean isFollowing(Long followerId, Long followingId);

    /**
     * 获取关注列表（我关注的人）
     */
    IPage<UserFollow> getFollowingList(Long userId, Integer page, Integer size);

    /**
     * 获取粉丝列表（关注我的人）
     */
    IPage<UserFollow> getFollowersList(Long userId, Integer page, Integer size);

    /**
     * 获取互相关注列表
     */
    IPage<UserFollow> getMutualFollowList(Long userId, Integer page, Integer size);

    /**
     * 获取用户关注统计
     */
    UserFollowStats getUserFollowStats(Long userId);

    /**
     * 批量检查关注状态
     */
    List<Long> getFollowingIds(Long userId);

    /**
     * 获取关注关系类
     */
    class UserFollowStats {
        private Long followingCount;  // 关注数
        private Long followersCount;  // 粉丝数
        private Long mutualFollowCount; // 互关数

        public UserFollowStats(Long followingCount, Long followersCount, Long mutualFollowCount) {
            this.followingCount = followingCount;
            this.followersCount = followersCount;
            this.mutualFollowCount = mutualFollowCount;
        }

        public Long getFollowingCount() { return followingCount; }
        public Long getFollowersCount() { return followersCount; }
        public Long getMutualFollowCount() { return mutualFollowCount; }
    }
}
