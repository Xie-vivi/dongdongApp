package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivityLike;
import com.xystapp.entity.PostLike;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.ActivityLikeMapper;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.mapper.PostLikeMapper;
import com.xystapp.mapper.PostMapper;
import com.xystapp.service.LikeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 点赞服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class LikeServiceImpl implements LikeService {

    private static final Logger log = LoggerFactory.getLogger(LikeServiceImpl.class);

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ActivityLikeMapper activityLikeMapper;

    @Autowired
    private ActivityMapper activityMapper;

    // ==================== 帖子点赞 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likePost(Long postId, Long userId) {
        log.info("点赞帖子请求: postId={}, userId={}", postId, userId);

        // 验证参数
        if (postId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查帖子是否存在
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        // 检查是否已点赞
        if (isPostLiked(postId, userId)) {
            throw new BusinessException(409, "已经点赞了该帖子");
        }

        // 创建点赞记录
        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);

        int result = postLikeMapper.insert(postLike);
        if (result <= 0) {
            throw new BusinessException(500, "点赞失败");
        }

        // 更新帖子点赞数
        postMapper.updateLikesCount(postId, 1);

        log.info("帖子点赞成功: postId={}, userId={}", postId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikePost(Long postId, Long userId) {
        log.info("取消点赞帖子请求: postId={}, userId={}", postId, userId);

        // 验证参数
        if (postId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查是否已点赞
        if (!isPostLiked(postId, userId)) {
            throw new BusinessException(400, "未点赞该帖子");
        }

        // 删除点赞记录
        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId)
                   .eq("user_id", userId);

        int result = postLikeMapper.delete(queryWrapper);
        if (result <= 0) {
            throw new BusinessException(500, "取消点赞失败");
        }

        // 更新帖子点赞数
        postMapper.updateLikesCount(postId, -1);

        log.info("取消点赞帖子成功: postId={}, userId={}", postId, userId);
    }

    @Override
    public boolean isPostLiked(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }

        QueryWrapper<PostLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId)
                   .eq("user_id", userId);

        return postLikeMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public IPage<PostLike> getPostLikeList(Long postId, Integer page, Integer size) {
        log.info("获取帖子点赞列表: postId={}", postId);

        if (postId == null) {
            throw new BusinessException(400, "帖子ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<PostLike> pageParam = new Page<>(page, size);
        IPage<PostLike> result = postLikeMapper.selectPostLikesWithUserInfo(pageParam, postId);

        log.info("获取帖子点赞列表成功: postId={}, 总数={}", postId, result.getTotal());
        return result;
    }

    @Override
    public IPage<PostLike> getUserLikedPosts(Long userId, Integer page, Integer size) {
        log.info("获取用户点赞的帖子列表: userId={}", userId);

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

        Page<PostLike> pageParam = new Page<>(page, size);
        IPage<PostLike> result = postLikeMapper.selectUserLikedPostsWithInfo(pageParam, userId);

        log.info("获取用户点赞的帖子列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    // ==================== 活动点赞 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeActivity(Long activityId, Long userId) {
        log.info("点赞活动请求: activityId={}, userId={}", activityId, userId);

        // 验证参数
        if (activityId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查活动是否存在
        if (activityMapper.selectById(activityId) == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 检查是否已点赞
        if (isActivityLiked(activityId, userId)) {
            throw new BusinessException(409, "已经点赞了该活动");
        }

        // 创建点赞记录
        ActivityLike activityLike = new ActivityLike();
        activityLike.setActivityId(activityId);
        activityLike.setUserId(userId);

        int result = activityLikeMapper.insert(activityLike);
        if (result <= 0) {
            throw new BusinessException(500, "点赞失败");
        }

        // 更新活动点赞数
        activityMapper.updateLikes(activityId, 1);

        log.info("活动点赞成功: activityId={}, userId={}", activityId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeActivity(Long activityId, Long userId) {
        log.info("取消点赞活动请求: activityId={}, userId={}", activityId, userId);

        // 验证参数
        if (activityId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查是否已点赞
        if (!isActivityLiked(activityId, userId)) {
            throw new BusinessException(400, "未点赞该活动");
        }

        // 删除点赞记录
        QueryWrapper<ActivityLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", activityId)
                   .eq("user_id", userId);

        int result = activityLikeMapper.delete(queryWrapper);
        if (result <= 0) {
            throw new BusinessException(500, "取消点赞失败");
        }

        // 更新活动点赞数
        activityMapper.updateLikes(activityId, -1);

        log.info("取消点赞活动成功: activityId={}, userId={}", activityId, userId);
    }

    @Override
    public boolean isActivityLiked(Long activityId, Long userId) {
        if (activityId == null || userId == null) {
            return false;
        }

        QueryWrapper<ActivityLike> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", activityId)
                   .eq("user_id", userId);

        return activityLikeMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public IPage<ActivityLike> getActivityLikeList(Long activityId, Integer page, Integer size) {
        log.info("获取活动点赞列表: activityId={}", activityId);

        if (activityId == null) {
            throw new BusinessException(400, "活动ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ActivityLike> pageParam = new Page<>(page, size);
        IPage<ActivityLike> result = activityLikeMapper.selectActivityLikesWithUserInfo(pageParam, activityId);

        log.info("获取活动点赞列表成功: activityId={}, 总数={}", activityId, result.getTotal());
        return result;
    }

    @Override
    public IPage<ActivityLike> getUserLikedActivities(Long userId, Integer page, Integer size) {
        log.info("获取用户点赞的活动列表: userId={}", userId);

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

        Page<ActivityLike> pageParam = new Page<>(page, size);
        IPage<ActivityLike> result = activityLikeMapper.selectUserLikedActivitiesWithInfo(pageParam, userId);

        log.info("获取用户点赞的活动列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    // ==================== 批量操作 ====================

    @Override
    public List<Long> getLikedPostIds(Long userId) {
        log.info("获取用户点赞的帖子ID列表: userId={}", userId);

        if (userId == null) {
            return new ArrayList<>();
        }

        return postLikeMapper.selectLikedPostIds(userId);
    }

    @Override
    public List<Long> getLikedActivityIds(Long userId) {
        log.info("获取用户点赞的活动ID列表: userId={}", userId);

        if (userId == null) {
            return new ArrayList<>();
        }

        return activityLikeMapper.selectLikedActivityIds(userId);
    }

    @Override
    public LikeStats getLikeStats(Long userId) {
        log.info("获取用户点赞统计: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Long postLikeCount = postLikeMapper.countUserPostLikes(userId);
        Long activityLikeCount = activityLikeMapper.countUserActivityLikes(userId);

        LikeStats stats = new LikeStats(postLikeCount, activityLikeCount);

        log.info("获取用户点赞统计成功: userId={}, posts={}, activities={}, total={}", 
                userId, postLikeCount, activityLikeCount, stats.getTotalLikeCount());

        return stats;
    }
}
