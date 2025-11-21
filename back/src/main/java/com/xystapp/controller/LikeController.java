package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.ActivityLike;
import com.xystapp.entity.PostLike;
import com.xystapp.service.LikeService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 点赞控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "点赞管理")
@RestController
@RequestMapping("/likes")
public class LikeController {

    private static final Logger log = LoggerFactory.getLogger(LikeController.class);

    @Autowired
    private LikeService likeService;

    // ==================== 帖子点赞 ====================

    /**
     * 点赞帖子
     */
    @ApiOperation("点赞帖子")
    @PostMapping("/posts/{postId}")
    public Result<Void> likePost(@ApiParam("帖子ID") @PathVariable Long postId) {
        log.info("点赞帖子请求: postId={}", postId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        likeService.likePost(postId, userId);
        
        return Result.success();
    }

    /**
     * 取消点赞帖子
     */
    @ApiOperation("取消点赞帖子")
    @DeleteMapping("/posts/{postId}")
    public Result<Void> unlikePost(@ApiParam("帖子ID") @PathVariable Long postId) {
        log.info("取消点赞帖子请求: postId={}", postId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        likeService.unlikePost(postId, userId);
        
        return Result.success();
    }

    /**
     * 检查是否已点赞帖子
     */
    @ApiOperation("检查是否已点赞帖子")
    @GetMapping("/posts/{postId}/check")
    public Result<Boolean> isPostLiked(@ApiParam("帖子ID") @PathVariable Long postId) {
        log.info("检查帖子点赞状态: postId={}", postId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isLiked = likeService.isPostLiked(postId, userId);
        
        return Result.success(isLiked);
    }

    /**
     * 获取帖子点赞列表
     */
    @ApiOperation("获取帖子点赞列表")
    @GetMapping("/posts/{postId}/list")
    public Result<IPage<PostLike>> getPostLikeList(
            @ApiParam("帖子ID") @PathVariable Long postId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取帖子点赞列表: postId={}", postId);
        
        IPage<PostLike> result = likeService.getPostLikeList(postId, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户点赞的帖子列表
     */
    @ApiOperation("获取用户点赞的帖子列表")
    @GetMapping("/posts/user/{userId}")
    public Result<IPage<PostLike>> getUserLikedPosts(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户点赞的帖子列表: userId={}", userId);
        
        IPage<PostLike> result = likeService.getUserLikedPosts(userId, page, size);
        return Result.success(result);
    }

    // ==================== 活动点赞 ====================

    /**
     * 点赞活动
     */
    @ApiOperation("点赞活动")
    @PostMapping("/activities/{activityId}")
    public Result<Void> likeActivity(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("点赞活动请求: activityId={}", activityId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        likeService.likeActivity(activityId, userId);
        
        return Result.success();
    }

    /**
     * 取消点赞活动
     */
    @ApiOperation("取消点赞活动")
    @DeleteMapping("/activities/{activityId}")
    public Result<Void> unlikeActivity(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("取消点赞活动请求: activityId={}", activityId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        likeService.unlikeActivity(activityId, userId);
        
        return Result.success();
    }

    /**
     * 检查是否已点赞活动
     */
    @ApiOperation("检查是否已点赞活动")
    @GetMapping("/activities/{activityId}/check")
    public Result<Boolean> isActivityLiked(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查活动点赞状态: activityId={}", activityId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isLiked = likeService.isActivityLiked(activityId, userId);
        
        return Result.success(isLiked);
    }

    /**
     * 获取活动点赞列表
     */
    @ApiOperation("获取活动点赞列表")
    @GetMapping("/activities/{activityId}/list")
    public Result<IPage<ActivityLike>> getActivityLikeList(
            @ApiParam("活动ID") @PathVariable Long activityId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取活动点赞列表: activityId={}", activityId);
        
        IPage<ActivityLike> result = likeService.getActivityLikeList(activityId, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户点赞的活动列表
     */
    @ApiOperation("获取用户点赞的活动列表")
    @GetMapping("/activities/user/{userId}")
    public Result<IPage<ActivityLike>> getUserLikedActivities(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户点赞的活动列表: userId={}", userId);
        
        IPage<ActivityLike> result = likeService.getUserLikedActivities(userId, page, size);
        return Result.success(result);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量检查帖子点赞状态
     */
    @ApiOperation("批量检查帖子点赞状态")
    @GetMapping("/posts/liked-ids")
    public Result<List<Long>> getLikedPostIds() {
        log.info("获取用户点赞的帖子ID列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> likedIds = likeService.getLikedPostIds(userId);
        
        return Result.success(likedIds);
    }

    /**
     * 批量检查活动点赞状态
     */
    @ApiOperation("批量检查活动点赞状态")
    @GetMapping("/activities/liked-ids")
    public Result<List<Long>> getLikedActivityIds() {
        log.info("获取用户点赞的活动ID列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> likedIds = likeService.getLikedActivityIds(userId);
        
        return Result.success(likedIds);
    }

    /**
     * 获取点赞统计
     */
    @ApiOperation("获取点赞统计")
    @GetMapping("/stats/{userId}")
    public Result<LikeService.LikeStats> getLikeStats(@ApiParam("用户ID") @PathVariable Long userId) {
        log.info("获取用户点赞统计: userId={}", userId);
        
        LikeService.LikeStats stats = likeService.getLikeStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取我的点赞统计
     */
    @ApiOperation("获取我的点赞统计")
    @GetMapping("/my/stats")
    public Result<LikeService.LikeStats> getMyLikeStats() {
        log.info("获取我的点赞统计");
        
        Long userId = SecurityUtils.getCurrentUserId();
        LikeService.LikeStats stats = likeService.getLikeStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取我点赞的帖子列表
     */
    @ApiOperation("获取我点赞的帖子列表")
    @GetMapping("/my/posts")
    public Result<IPage<PostLike>> getMyLikedPosts(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我点赞的帖子列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<PostLike> result = likeService.getUserLikedPosts(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取我点赞的活动列表
     */
    @ApiOperation("获取我点赞的活动列表")
    @GetMapping("/my/activities")
    public Result<IPage<ActivityLike>> getMyLikedActivities(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我点赞的活动列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<ActivityLike> result = likeService.getUserLikedActivities(userId, page, size);
        return Result.success(result);
    }
}
