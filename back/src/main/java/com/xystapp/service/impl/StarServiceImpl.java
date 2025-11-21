package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivityStar;
import com.xystapp.entity.PostStar;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.ActivityStarMapper;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.mapper.PostStarMapper;
import com.xystapp.mapper.PostMapper;
import com.xystapp.service.StarService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 收藏服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class StarServiceImpl implements StarService {

    private static final Logger log = LoggerFactory.getLogger(StarServiceImpl.class);

    @Autowired
    private PostStarMapper postStarMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ActivityStarMapper activityStarMapper;

    @Autowired
    private ActivityMapper activityMapper;

    // ==================== 帖子收藏 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void starPost(Long postId, Long userId) {
        log.info("收藏帖子请求: postId={}, userId={}", postId, userId);

        // 验证参数
        if (postId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查帖子是否存在
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        // 检查是否已收藏
        if (isPostStarred(postId, userId)) {
            throw new BusinessException(409, "已经收藏了该帖子");
        }

        // 创建收藏记录
        PostStar postStar = new PostStar();
        postStar.setPostId(postId);
        postStar.setUserId(userId);

        int result = postStarMapper.insert(postStar);
        if (result <= 0) {
            throw new BusinessException(500, "收藏失败");
        }

        // 更新帖子收藏数
        postMapper.updateStarsCount(postId, 1);

        log.info("帖子收藏成功: postId={}, userId={}", postId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unstarPost(Long postId, Long userId) {
        log.info("取消收藏帖子请求: postId={}, userId={}", postId, userId);

        // 验证参数
        if (postId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查是否已收藏
        if (!isPostStarred(postId, userId)) {
            throw new BusinessException(400, "未收藏该帖子");
        }

        // 删除收藏记录
        QueryWrapper<PostStar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId)
                   .eq("user_id", userId);

        int result = postStarMapper.delete(queryWrapper);
        if (result <= 0) {
            throw new BusinessException(500, "取消收藏失败");
        }

        // 更新帖子收藏数
        postMapper.updateStarsCount(postId, -1);

        log.info("取消收藏帖子成功: postId={}, userId={}", postId, userId);
    }

    @Override
    public boolean isPostStarred(Long postId, Long userId) {
        if (postId == null || userId == null) {
            return false;
        }

        QueryWrapper<PostStar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("post_id", postId)
                   .eq("user_id", userId);

        return postStarMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public IPage<PostStar> getPostStarList(Long postId, Integer page, Integer size) {
        log.info("获取帖子收藏列表: postId={}", postId);

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

        Page<PostStar> pageParam = new Page<>(page, size);
        IPage<PostStar> result = postStarMapper.selectPostStarsWithUserInfo(pageParam, postId);

        log.info("获取帖子收藏列表成功: postId={}, 总数={}", postId, result.getTotal());
        return result;
    }

    @Override
    public IPage<PostStar> getUserStarredPosts(Long userId, Integer page, Integer size) {
        log.info("获取用户收藏的帖子列表: userId={}", userId);

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

        Page<PostStar> pageParam = new Page<>(page, size);
        IPage<PostStar> result = postStarMapper.selectUserStarredPostsWithInfo(pageParam, userId);

        log.info("获取用户收藏的帖子列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    // ==================== 活动收藏 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void starActivity(Long activityId, Long userId) {
        log.info("收藏活动请求: activityId={}, userId={}", activityId, userId);

        // 验证参数
        if (activityId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查活动是否存在
        if (activityMapper.selectById(activityId) == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 检查是否已收藏
        if (isActivityStarred(activityId, userId)) {
            throw new BusinessException(409, "已经收藏了该活动");
        }

        // 创建收藏记录
        ActivityStar activityStar = new ActivityStar();
        activityStar.setActivityId(activityId);
        activityStar.setUserId(userId);

        int result = activityStarMapper.insert(activityStar);
        if (result <= 0) {
            throw new BusinessException(500, "收藏失败");
        }

        // 更新活动收藏数
        activityMapper.updateStarsCount(activityId, 1);

        log.info("活动收藏成功: activityId={}, userId={}", activityId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unstarActivity(Long activityId, Long userId) {
        log.info("取消收藏活动请求: activityId={}, userId={}", activityId, userId);

        // 验证参数
        if (activityId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查是否已收藏
        if (!isActivityStarred(activityId, userId)) {
            throw new BusinessException(400, "未收藏该活动");
        }

        // 删除收藏记录
        QueryWrapper<ActivityStar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", activityId)
                   .eq("user_id", userId);

        int result = activityStarMapper.delete(queryWrapper);
        if (result <= 0) {
            throw new BusinessException(500, "取消收藏失败");
        }

        // 更新活动收藏数
        activityMapper.updateStarsCount(activityId, -1);

        log.info("取消收藏活动成功: activityId={}, userId={}", activityId, userId);
    }

    @Override
    public boolean isActivityStarred(Long activityId, Long userId) {
        if (activityId == null || userId == null) {
            return false;
        }

        QueryWrapper<ActivityStar> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("activity_id", activityId)
                   .eq("user_id", userId);

        return activityStarMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public IPage<ActivityStar> getActivityStarList(Long activityId, Integer page, Integer size) {
        log.info("获取活动收藏列表: activityId={}", activityId);

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

        Page<ActivityStar> pageParam = new Page<>(page, size);
        IPage<ActivityStar> result = activityStarMapper.selectActivityStarsWithUserInfo(pageParam, activityId);

        log.info("获取活动收藏列表成功: activityId={}, 总数={}", activityId, result.getTotal());
        return result;
    }

    @Override
    public IPage<ActivityStar> getUserStarredActivities(Long userId, Integer page, Integer size) {
        log.info("获取用户收藏的活动列表: userId={}", userId);

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

        Page<ActivityStar> pageParam = new Page<>(page, size);
        IPage<ActivityStar> result = activityStarMapper.selectUserStarredActivitiesWithInfo(pageParam, userId);

        log.info("获取用户收藏的活动列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    // ==================== 批量操作 ====================

    @Override
    public List<Long> getStarredPostIds(Long userId) {
        log.info("获取用户收藏的帖子ID列表: userId={}", userId);

        if (userId == null) {
            return new ArrayList<>();
        }

        return postStarMapper.selectStarredPostIds(userId);
    }

    @Override
    public List<Long> getStarredActivityIds(Long userId) {
        log.info("获取用户收藏的活动ID列表: userId={}", userId);

        if (userId == null) {
            return new ArrayList<>();
        }

        return activityStarMapper.selectStarredActivityIds(userId);
    }

    @Override
    public StarStats getStarStats(Long userId) {
        log.info("获取用户收藏统计: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Long postStarCount = postStarMapper.countUserPostStars(userId);
        Long activityStarCount = activityStarMapper.countUserActivityStars(userId);

        StarStats stats = new StarStats(postStarCount, activityStarCount);

        log.info("获取用户收藏统计成功: userId={}, posts={}, activities={}, total={}", 
                userId, postStarCount, activityStarCount, stats.getTotalStarCount());

        return stats;
    }
}
