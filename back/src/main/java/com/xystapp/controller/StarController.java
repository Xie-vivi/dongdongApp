package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.ActivityStar;
import com.xystapp.entity.PostStar;
import com.xystapp.service.StarService;
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
 * 收藏控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "收藏管理")
@RestController
@RequestMapping("/stars")
public class StarController {

    private static final Logger log = LoggerFactory.getLogger(StarController.class);

    @Autowired
    private StarService starService;

    // ==================== 帖子收藏 ====================

    /**
     * 收藏帖子
     */
    @ApiOperation("收藏帖子")
    @PostMapping("/posts/{postId}")
    public Result<Void> starPost(@ApiParam("帖子ID") @PathVariable Long postId) {
        log.info("收藏帖子请求: postId={}", postId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        starService.starPost(postId, userId);
        
        return Result.success();
    }

    /**
     * 取消收藏帖子
     */
    @ApiOperation("取消收藏帖子")
    @DeleteMapping("/posts/{postId}")
    public Result<Void> unstarPost(@ApiParam("帖子ID") @PathVariable Long postId) {
        log.info("取消收藏帖子请求: postId={}", postId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        starService.unstarPost(postId, userId);
        
        return Result.success();
    }

    /**
     * 检查是否已收藏帖子
     */
    @ApiOperation("检查是否已收藏帖子")
    @GetMapping("/posts/{postId}/check")
    public Result<Boolean> isPostStarred(@ApiParam("帖子ID") @PathVariable Long postId) {
        log.info("检查帖子收藏状态: postId={}", postId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isStarred = starService.isPostStarred(postId, userId);
        
        return Result.success(isStarred);
    }

    /**
     * 获取帖子收藏列表
     */
    @ApiOperation("获取帖子收藏列表")
    @GetMapping("/posts/{postId}/list")
    public Result<IPage<PostStar>> getPostStarList(
            @ApiParam("帖子ID") @PathVariable Long postId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取帖子收藏列表: postId={}", postId);
        
        IPage<PostStar> result = starService.getPostStarList(postId, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户收藏的帖子列表
     */
    @ApiOperation("获取用户收藏的帖子列表")
    @GetMapping("/posts/user/{userId}")
    public Result<IPage<PostStar>> getUserStarredPosts(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户收藏的帖子列表: userId={}", userId);
        
        IPage<PostStar> result = starService.getUserStarredPosts(userId, page, size);
        return Result.success(result);
    }

    // ==================== 活动收藏 ====================

    /**
     * 收藏活动
     */
    @ApiOperation("收藏活动")
    @PostMapping("/activities/{activityId}")
    public Result<Void> starActivity(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("收藏活动请求: activityId={}", activityId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        starService.starActivity(activityId, userId);
        
        return Result.success();
    }

    /**
     * 取消收藏活动
     */
    @ApiOperation("取消收藏活动")
    @DeleteMapping("/activities/{activityId}")
    public Result<Void> unstarActivity(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("取消收藏活动请求: activityId={}", activityId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        starService.unstarActivity(activityId, userId);
        
        return Result.success();
    }

    /**
     * 检查是否已收藏活动
     */
    @ApiOperation("检查是否已收藏活动")
    @GetMapping("/activities/{activityId}/check")
    public Result<Boolean> isActivityStarred(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查活动收藏状态: activityId={}", activityId);
        
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isStarred = starService.isActivityStarred(activityId, userId);
        
        return Result.success(isStarred);
    }

    /**
     * 获取活动收藏列表
     */
    @ApiOperation("获取活动收藏列表")
    @GetMapping("/activities/{activityId}/list")
    public Result<IPage<ActivityStar>> getActivityStarList(
            @ApiParam("活动ID") @PathVariable Long activityId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取活动收藏列表: activityId={}", activityId);
        
        IPage<ActivityStar> result = starService.getActivityStarList(activityId, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户收藏的活动列表
     */
    @ApiOperation("获取用户收藏的活动列表")
    @GetMapping("/activities/user/{userId}")
    public Result<IPage<ActivityStar>> getUserStarredActivities(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户收藏的活动列表: userId={}", userId);
        
        IPage<ActivityStar> result = starService.getUserStarredActivities(userId, page, size);
        return Result.success(result);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量检查帖子收藏状态
     */
    @ApiOperation("批量检查帖子收藏状态")
    @GetMapping("/posts/starred-ids")
    public Result<List<Long>> getStarredPostIds() {
        log.info("获取用户收藏的帖子ID列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> starredIds = starService.getStarredPostIds(userId);
        
        return Result.success(starredIds);
    }

    /**
     * 批量检查活动收藏状态
     */
    @ApiOperation("批量检查活动收藏状态")
    @GetMapping("/activities/starred-ids")
    public Result<List<Long>> getStarredActivityIds() {
        log.info("获取用户收藏的活动ID列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> starredIds = starService.getStarredActivityIds(userId);
        
        return Result.success(starredIds);
    }

    /**
     * 获取收藏统计
     */
    @ApiOperation("获取收藏统计")
    @GetMapping("/stats/{userId}")
    public Result<StarService.StarStats> getStarStats(@ApiParam("用户ID") @PathVariable Long userId) {
        log.info("获取用户收藏统计: userId={}", userId);
        
        StarService.StarStats stats = starService.getStarStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取我的收藏统计
     */
    @ApiOperation("获取我的收藏统计")
    @GetMapping("/my/stats")
    public Result<StarService.StarStats> getMyStarStats() {
        log.info("获取我的收藏统计");
        
        Long userId = SecurityUtils.getCurrentUserId();
        StarService.StarStats stats = starService.getStarStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取我收藏的帖子列表
     */
    @ApiOperation("获取我收藏的帖子列表")
    @GetMapping("/my/posts")
    public Result<IPage<PostStar>> getMyStarredPosts(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我收藏的帖子列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<PostStar> result = starService.getUserStarredPosts(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取我收藏的活动列表
     */
    @ApiOperation("获取我收藏的活动列表")
    @GetMapping("/my/activities")
    public Result<IPage<ActivityStar>> getMyStarredActivities(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我收藏的活动列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<ActivityStar> result = starService.getUserStarredActivities(userId, page, size);
        return Result.success(result);
    }
}
