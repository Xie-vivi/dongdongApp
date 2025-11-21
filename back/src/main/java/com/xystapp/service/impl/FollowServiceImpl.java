package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.UserFollow;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.UserFollowMapper;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.FollowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户关注服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class FollowServiceImpl implements FollowService {

    private static final Logger log = LoggerFactory.getLogger(FollowServiceImpl.class);

    @Autowired
    private UserFollowMapper userFollowMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void followUser(Long followingId, Long followerId) {
        log.info("关注用户请求: followerId={}, followingId={}", followerId, followingId);

        // 验证参数
        if (followingId == null || followerId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 不能关注自己
        if (followingId.equals(followerId)) {
            throw new BusinessException(400, "不能关注自己");
        }

        // 检查目标用户是否存在
        if (userMapper.selectById(followingId) == null) {
            throw new BusinessException(404, "目标用户不存在");
        }

        // 检查是否已关注
        if (isFollowing(followerId, followingId)) {
            throw new BusinessException(409, "已经关注了该用户");
        }

        // 创建关注关系
        UserFollow userFollow = new UserFollow();
        userFollow.setFollowerId(followerId);
        userFollow.setFollowingId(followingId);

        int result = userFollowMapper.insert(userFollow);
        if (result <= 0) {
            throw new BusinessException(500, "关注失败");
        }

        // 更新用户关注统计（使用数据库原子操作）
        updateUserFollowCount(followerId, followingId, 1);

        log.info("用户关注成功: followerId={}, followingId={}", followerId, followingId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollowUser(Long followingId, Long followerId) {
        log.info("取消关注请求: followerId={}, followingId={}", followerId, followingId);

        // 验证参数
        if (followingId == null || followerId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 检查是否已关注
        if (!isFollowing(followerId, followingId)) {
            throw new BusinessException(400, "未关注该用户");
        }

        // 删除关注关系
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId)
                   .eq("following_id", followingId);

        int result = userFollowMapper.delete(queryWrapper);
        if (result <= 0) {
            throw new BusinessException(500, "取消关注失败");
        }

        // 更新用户关注统计
        updateUserFollowCount(followerId, followingId, -1);

        log.info("取消关注成功: followerId={}, followingId={}", followerId, followingId);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            return false;
        }

        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("follower_id", followerId)
                   .eq("following_id", followingId);

        return userFollowMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public IPage<UserFollow> getFollowingList(Long userId, Integer page, Integer size) {
        log.info("获取关注列表: userId={}", userId);

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

        Page<UserFollow> pageParam = new Page<>(page, size);
        IPage<UserFollow> result = userFollowMapper.selectFollowingWithUserInfo(pageParam, userId);

        log.info("获取关注列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public IPage<UserFollow> getFollowersList(Long userId, Integer page, Integer size) {
        log.info("获取粉丝列表: userId={}", userId);

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

        Page<UserFollow> pageParam = new Page<>(page, size);
        IPage<UserFollow> result = userFollowMapper.selectFollowersWithUserInfo(pageParam, userId);

        log.info("获取粉丝列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public IPage<UserFollow> getMutualFollowList(Long userId, Integer page, Integer size) {
        log.info("获取互相关注列表: userId={}", userId);

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

        Page<UserFollow> pageParam = new Page<>(page, size);
        IPage<UserFollow> result = userFollowMapper.selectMutualFollowsWithUserInfo(pageParam, userId);

        log.info("获取互相关注列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public UserFollowStats getUserFollowStats(Long userId) {
        log.info("获取用户关注统计: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Long followingCount = userFollowMapper.countFollowing(userId);
        Long followersCount = userFollowMapper.countFollowers(userId);
        Long mutualFollowCount = userFollowMapper.countMutualFollows(userId);

        UserFollowStats stats = new UserFollowStats(followingCount, followersCount, mutualFollowCount);

        log.info("获取用户关注统计成功: userId={}, following={}, followers={}, mutual={}", 
                userId, followingCount, followersCount, mutualFollowCount);

        return stats;
    }

    @Override
    public List<Long> getFollowingIds(Long userId) {
        log.info("获取关注用户ID列表: userId={}", userId);

        if (userId == null) {
            return new ArrayList<>();
        }

        return userFollowMapper.selectFollowingIds(userId);
    }

    /**
     * 更新用户关注统计
     * 
     * @param followerId 关注者ID
     * @param followingId 被关注者ID
     * @param delta 变化量（+1关注，-1取消关注）
     */
    private void updateUserFollowCount(Long followerId, Long followingId, int delta) {
        // 更新关注者的关注数
        userMapper.updateFollowingCount(followerId, delta);
        // 更新被关注者的粉丝数
        userMapper.updateFollowersCount(followingId, delta);
    }
}
