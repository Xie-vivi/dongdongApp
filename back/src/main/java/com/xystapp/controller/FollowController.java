package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.UserFollow;
import com.xystapp.service.FollowService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户关注控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "用户关注")
@RestController
@RequestMapping("/follows")
public class FollowController {

    private static final Logger log = LoggerFactory.getLogger(FollowController.class);

    @Autowired
    private FollowService followService;

    /**
     * 关注用户
     */
    @ApiOperation("关注用户")
    @PostMapping("/{followingId}")
    public Result<Void> followUser(@ApiParam("被关注用户ID") @PathVariable Long followingId) {
        log.info("关注用户请求: followingId={}", followingId);
        
        Long followerId = SecurityUtils.getCurrentUserId();
        followService.followUser(followingId, followerId);
        
        return Result.success();
    }

    /**
     * 取消关注
     */
    @ApiOperation("取消关注")
    @DeleteMapping("/{followingId}")
    public Result<Void> unfollowUser(@ApiParam("被关注用户ID") @PathVariable Long followingId) {
        log.info("取消关注请求: followingId={}", followingId);
        
        Long followerId = SecurityUtils.getCurrentUserId();
        followService.unfollowUser(followingId, followerId);
        
        return Result.success();
    }

    /**
     * 检查是否已关注
     */
    @ApiOperation("检查是否已关注")
    @GetMapping("/check/{followingId}")
    public Result<Boolean> isFollowing(@ApiParam("被关注用户ID") @PathVariable Long followingId) {
        log.info("检查关注状态: followingId={}", followingId);
        
        Long followerId = SecurityUtils.getCurrentUserId();
        boolean isFollowing = followService.isFollowing(followerId, followingId);
        
        return Result.success(isFollowing);
    }

    /**
     * 获取关注列表
     */
    @ApiOperation("获取关注列表")
    @GetMapping("/following/{userId}")
    public Result<IPage<UserFollow>> getFollowingList(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取关注列表: userId={}", userId);
        
        IPage<UserFollow> result = followService.getFollowingList(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取粉丝列表
     */
    @ApiOperation("获取粉丝列表")
    @GetMapping("/followers/{userId}")
    public Result<IPage<UserFollow>> getFollowersList(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取粉丝列表: userId={}", userId);
        
        IPage<UserFollow> result = followService.getFollowersList(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取互相关注列表
     */
    @ApiOperation("获取互相关注列表")
    @GetMapping("/mutual/{userId}")
    public Result<IPage<UserFollow>> getMutualFollowList(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取互相关注列表: userId={}", userId);
        
        IPage<UserFollow> result = followService.getMutualFollowList(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取用户关注统计
     */
    @ApiOperation("获取用户关注统计")
    @GetMapping("/stats/{userId}")
    public Result<FollowService.UserFollowStats> getUserFollowStats(@ApiParam("用户ID") @PathVariable Long userId) {
        log.info("获取用户关注统计: userId={}", userId);
        
        FollowService.UserFollowStats stats = followService.getUserFollowStats(userId);
        return Result.success(stats);
    }

    /**
     * 获取我的关注列表
     */
    @ApiOperation("获取我的关注列表")
    @GetMapping("/my/following")
    public Result<IPage<UserFollow>> getMyFollowingList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我的关注列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<UserFollow> result = followService.getFollowingList(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取我的粉丝列表
     */
    @ApiOperation("获取我的粉丝列表")
    @GetMapping("/my/followers")
    public Result<IPage<UserFollow>> getMyFollowersList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我的粉丝列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<UserFollow> result = followService.getFollowersList(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取我的互相关注列表
     */
    @ApiOperation("获取我的互相关注列表")
    @GetMapping("/my/mutual")
    public Result<IPage<UserFollow>> getMyMutualFollowList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我的互相关注列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<UserFollow> result = followService.getMutualFollowList(userId, page, size);
        return Result.success(result);
    }

    /**
     * 获取我的关注统计
     */
    @ApiOperation("获取我的关注统计")
    @GetMapping("/my/stats")
    public Result<FollowService.UserFollowStats> getMyFollowStats() {
        log.info("获取我的关注统计");
        
        Long userId = SecurityUtils.getCurrentUserId();
        FollowService.UserFollowStats stats = followService.getUserFollowStats(userId);
        return Result.success(stats);
    }
}
