package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.dto.ActivityCreateRequest;
import com.xystapp.dto.ActivityQueryRequest;
import com.xystapp.dto.ActivityUpdateRequest;
import com.xystapp.entity.Activity;
import com.xystapp.service.ActivityService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 活动控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "活动管理")
@RestController
@RequestMapping("/activities")
@Validated
public class ActivityController {

    private static final Logger log = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    private ActivityService activityService;

    /**
     * 创建活动
     */
    @ApiOperation("创建活动")
    @PostMapping
    public Result<Activity> createActivity(@Valid @RequestBody ActivityCreateRequest request) {
        log.info("创建活动请求: {}", request.getTitle());
        
        Long userId = SecurityUtils.getCurrentUserId();
        Activity activity = activityService.createActivity(request, userId);
        
        return Result.success(activity);
    }

    /**
     * 更新活动
     */
    @ApiOperation("更新活动")
    @PutMapping("/{id}")
    public Result<Activity> updateActivity(
            @ApiParam("活动ID") @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateRequest request) {
        log.info("更新活动请求: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Activity activity = activityService.updateActivity(id, request, userId);
        
        return Result.success(activity);
    }

    /**
     * 删除活动
     */
    @ApiOperation("删除活动")
    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("删除活动请求: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        activityService.deleteActivity(id, userId);
        
        return Result.success();
    }

    /**
     * 获取活动详情
     */
    @ApiOperation("获取活动详情")
    @GetMapping("/{id}")
    public Result<Activity> getActivityById(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("获取活动详情: id={}", id);
        
        Activity activity = activityService.getActivityById(id);
        return Result.success(activity);
    }

    /**
     * 分页查询活动列表
     */
    @ApiOperation("分页查询活动列表")
    @PostMapping("/search")
    public Result<IPage<Activity>> getActivityList(@Valid @RequestBody ActivityQueryRequest query) {
        log.info("查询活动列表: keyword={}, page={}, size={}", 
                query.getKeyword(), query.getPage(), query.getSize());
        
        IPage<Activity> result = activityService.getActivityList(query);
        return Result.success(result);
    }

    /**
     * 获取用户活动列表
     */
    @ApiOperation("获取用户活动列表")
    @GetMapping("/user/{userId}")
    public Result<IPage<Activity>> getUserActivities(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("活动状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户活动列表: userId={}, status={}", userId, status);
        
        IPage<Activity> result = activityService.getUserActivities(userId, status, page, size);
        return Result.success(result);
    }

    /**
     * 发布草稿活动
     */
    @ApiOperation("发布草稿活动")
    @PostMapping("/{id}/publish")
    public Result<Activity> publishDraft(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("发布草稿活动: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Activity activity = activityService.publishDraft(id, userId);
        
        return Result.success(activity);
    }

    /**
     * 将活动设为草稿
     */
    @ApiOperation("将活动设为草稿")
    @PostMapping("/{id}/draft")
    public Result<Activity> setAsDraft(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("将活动设为草稿: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Activity activity = activityService.setAsDraft(id, userId);
        
        return Result.success(activity);
    }

    /**
     * 取消活动
     */
    @ApiOperation("取消活动")
    @PostMapping("/{id}/cancel")
    public Result<Activity> cancelActivity(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("取消活动: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        Activity activity = activityService.cancelActivity(id, userId);
        
        return Result.success(activity);
    }

    /**
     * 报名活动
     */
    @ApiOperation("报名活动")
    @PostMapping("/{id}/signup")
    public Result<Void> signupActivity(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("报名活动: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        activityService.signupActivity(id, userId);
        
        return Result.success();
    }

    /**
     * 取消报名
     */
    @ApiOperation("取消报名")
    @DeleteMapping("/{id}/signup")
    public Result<Void> cancelSignup(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("取消报名: id={}", id);
        
        Long userId = SecurityUtils.getCurrentUserId();
        activityService.cancelSignup(id, userId);
        
        return Result.success();
    }

    /**
     * 获取用户报名的活动列表
     */
    @ApiOperation("获取用户报名的活动列表")
    @GetMapping("/signed-up")
    public Result<IPage<Activity>> getSignedUpActivities(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户报名活动列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Activity> result = activityService.getSignedUpActivities(userId, page, size);
        
        return Result.success(result);
    }

    /**
     * 批量获取活动信息
     */
    @ApiOperation("批量获取活动信息")
    @PostMapping("/batch")
    public Result<List<Activity>> getActivitiesByIds(@RequestBody List<Long> ids) {
        log.info("批量获取活动信息: ids={}", ids);
        
        List<Activity> activities = activityService.getActivitiesByIds(ids);
        return Result.success(activities);
    }

    /**
     * 获取我的活动列表
     */
    @ApiOperation("获取我的活动列表")
    @GetMapping("/my")
    public Result<IPage<Activity>> getMyActivities(
            @ApiParam("活动状态") @RequestParam(required = false) String status,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取我的活动列表: status={}", status);
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Activity> result = activityService.getUserActivities(userId, status, page, size);
        
        return Result.success(result);
    }

    /**
     * 获取即将开始的活动
     */
    @ApiOperation("获取即将开始的活动")
    @GetMapping("/upcoming")
    public Result<IPage<Activity>> getUpcomingActivities(
            @ApiParam("天数") @RequestParam(defaultValue = "7") Integer days,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取即将开始的活动: days={}", days);
        
        IPage<Activity> result = activityService.getUpcomingActivities(days, page, size);
        return Result.success(result);
    }

    /**
     * 获取热门活动
     */
    @ApiOperation("获取热门活动")
    @GetMapping("/popular")
    public Result<IPage<Activity>> getPopularActivities(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取热门活动: limit={}", limit);
        
        IPage<Activity> result = activityService.getPopularActivities(limit, page, size);
        return Result.success(result);
    }

    // ==================== 管理员功能 ====================

    /**
     * 管理员删除活动
     */
    @ApiOperation("管理员删除活动")
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> adminDeleteActivity(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("管理员删除活动: id={}, adminId={}", id, SecurityUtils.getCurrentUserId());
        
        // 管理员可以删除任何活动
        activityService.deleteActivity(id, null); // 传入null表示跳过权限检查
        
        return Result.success();
    }

    /**
     * 管理员审核活动
     */
    @ApiOperation("管理员审核活动")
    @PostMapping("/admin/{id}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Activity> reviewActivity(
            @ApiParam("活动ID") @PathVariable Long id,
            @ApiParam("审核结果") @RequestParam String result,
            @ApiParam("审核理由") @RequestParam(required = false) String reason) {
        log.info("管理员审核活动: id={}, result={}, reason={}", id, result, reason);
        
        // 这里需要实现审核逻辑，暂时返回活动详情
        Activity activity = activityService.getActivityById(id);
        return Result.success(activity);
    }

    /**
     * 管理员推荐活动
     */
    @ApiOperation("管理员推荐活动")
    @PostMapping("/admin/{id}/recommend")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> recommendActivity(
            @ApiParam("活动ID") @PathVariable Long id,
            @ApiParam("是否推荐") @RequestParam Boolean recommended) {
        log.info("管理员推荐活动: id={}, recommended={}", id, recommended);
        
        // 这里需要实现推荐逻辑
        return Result.success(recommended);
    }

    /**
     * 管理员取消活动
     */
    @ApiOperation("管理员取消活动")
    @PostMapping("/admin/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Activity> adminCancelActivity(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("管理员取消活动: id={}", id);
        
        // 管理员可以取消任何活动
        Activity activity = activityService.cancelActivity(id, null);
        return Result.success(activity);
    }

    /**
     * 管理员获取所有活动列表（包括草稿和已删除）
     */
    @ApiOperation("管理员获取所有活动列表")
    @GetMapping("/admin/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<Activity>> adminGetActivityList(
            @ApiParam("查询参数") @Valid ActivityQueryRequest query) {
        log.info("管理员获取活动列表: query={}", query);
        
        // 管理员可以看到所有状态的活动
        IPage<Activity> result = activityService.getActivityList(query);
        return Result.success(result);
    }

    /**
     * 管理员获取草稿列表
     */
    @ApiOperation("管理员获取草稿列表")
    @GetMapping("/admin/drafts")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<Activity>> adminGetDrafts(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("管理员获取活动草稿列表: userId={}", userId);
        
        IPage<Activity> result = activityService.getUserActivities(userId, "draft", page, size);
        return Result.success(result);
    }

    /**
     * 管理员强制发布活动
     */
    @ApiOperation("管理员强制发布活动")
    @PostMapping("/admin/{id}/force-publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Activity> forcePublishActivity(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("管理员强制发布活动: id={}", id);
        
        // 管理员可以强制发布任何草稿活动
        Activity activity = activityService.publishDraft(id, null);
        return Result.success(activity);
    }

    /**
     * 管理员批量审核活动
     */
    @ApiOperation("管理员批量审核活动")
    @PostMapping("/admin/batch-review")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> batchReviewActivities(
            @ApiParam("活动ID列表") @RequestBody List<Long> ids,
            @ApiParam("审核结果") @RequestParam String result,
            @ApiParam("审核理由") @RequestParam(required = false) String reason) {
        log.info("管理员批量审核活动: ids={}, result={}", ids, result);
        
        for (Long id : ids) {
            // 这里实现审核逻辑
            activityService.getActivityById(id);
        }
        
        return Result.success(true);
    }

    // ==================== 草稿管理专用接口 ====================

    /**
     * 获取活动草稿列表（所有用户）
     */
    @ApiOperation("获取活动草稿列表")
    @GetMapping("/drafts")
    public Result<IPage<Activity>> getDrafts(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        log.info("获取活动草稿列表");
        
        Long userId = SecurityUtils.getCurrentUserId();
        IPage<Activity> result = activityService.getUserActivities(userId, "draft", page, size);
        return Result.success(result);
    }

    /**
     * 批量删除活动草稿
     */
    @ApiOperation("批量删除活动草稿")
    @DeleteMapping("/drafts/batch")
    public Result<Boolean> batchDeleteDrafts(@RequestBody List<Long> ids) {
        log.info("批量删除活动草稿: ids={}", ids);
        
        Long userId = SecurityUtils.getCurrentUserId();
        for (Long id : ids) {
            activityService.deleteActivity(id, userId);
        }
        
        return Result.success(true);
    }

    // ==================== 统计功能 ====================

    /**
     * 获取活动统计信息
     */
    @ApiOperation("获取活动统计信息")
    @GetMapping("/stats")
    public Result<ActivityStats> getActivityStats(
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        log.info("获取活动统计信息: userId={}", userId);
        
        if (userId == null) {
            userId = SecurityUtils.getCurrentUserId();
        }
        
        // 这里实现统计逻辑
        ActivityStats stats = new ActivityStats();
        stats.setTotalActivities(0);
        stats.setPublishedActivities(0);
        stats.setDraftActivities(0);
        stats.setCancelledActivities(0);
        stats.setTotalParticipants(0);
        stats.setTotalLikes(0);
        stats.setTotalComments(0);
        
        return Result.success(stats);
    }

    /**
     * 管理员获取活动统计信息
     */
    @ApiOperation("管理员获取活动统计信息")
    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ActivityStats> adminGetActivityStats() {
        log.info("管理员获取活动统计信息");
        
        // 这里实现管理员统计逻辑
        ActivityStats stats = new ActivityStats();
        stats.setTotalActivities(0);
        stats.setPublishedActivities(0);
        stats.setDraftActivities(0);
        stats.setCancelledActivities(0);
        stats.setTotalParticipants(0);
        stats.setTotalLikes(0);
        stats.setTotalComments(0);
        
        return Result.success(stats);
    }

    /**
     * 获取活动报名统计
     */
    @ApiOperation("获取活动报名统计")
    @GetMapping("/{id}/signup-stats")
    public Result<ActivitySignupStats> getActivitySignupStats(@ApiParam("活动ID") @PathVariable Long id) {
        log.info("获取活动报名统计: id={}", id);
        
        // 这里实现报名统计逻辑
        ActivitySignupStats stats = new ActivitySignupStats();
        stats.setActivityId(id);
        stats.setTotalSignups(0);
        stats.setConfirmedSignups(0);
        stats.setPendingSignups(0);
        stats.setCancelledSignups(0);
        
        return Result.success(stats);
    }

    /**
     * 活动统计信息内部类
     */
    public static class ActivityStats {
        private Integer totalActivities;
        private Integer publishedActivities;
        private Integer draftActivities;
        private Integer cancelledActivities;
        private Integer totalParticipants;
        private Integer totalLikes;
        private Integer totalComments;

        // getters and setters
        public Integer getTotalActivities() { return totalActivities; }
        public void setTotalActivities(Integer totalActivities) { this.totalActivities = totalActivities; }
        public Integer getPublishedActivities() { return publishedActivities; }
        public void setPublishedActivities(Integer publishedActivities) { this.publishedActivities = publishedActivities; }
        public Integer getDraftActivities() { return draftActivities; }
        public void setDraftActivities(Integer draftActivities) { this.draftActivities = draftActivities; }
        public Integer getCancelledActivities() { return cancelledActivities; }
        public void setCancelledActivities(Integer cancelledActivities) { this.cancelledActivities = cancelledActivities; }
        public Integer getTotalParticipants() { return totalParticipants; }
        public void setTotalParticipants(Integer totalParticipants) { this.totalParticipants = totalParticipants; }
        public Integer getTotalLikes() { return totalLikes; }
        public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
        public Integer getTotalComments() { return totalComments; }
        public void setTotalComments(Integer totalComments) { this.totalComments = totalComments; }
    }

    /**
     * 活动报名统计内部类
     */
    public static class ActivitySignupStats {
        private Long activityId;
        private Integer totalSignups;
        private Integer confirmedSignups;
        private Integer pendingSignups;
        private Integer cancelledSignups;

        // getters and setters
        public Long getActivityId() { return activityId; }
        public void setActivityId(Long activityId) { this.activityId = activityId; }
        public Integer getTotalSignups() { return totalSignups; }
        public void setTotalSignups(Integer totalSignups) { this.totalSignups = totalSignups; }
        public Integer getConfirmedSignups() { return confirmedSignups; }
        public void setConfirmedSignups(Integer confirmedSignups) { this.confirmedSignups = confirmedSignups; }
        public Integer getPendingSignups() { return pendingSignups; }
        public void setPendingSignups(Integer pendingSignups) { this.pendingSignups = pendingSignups; }
        public Integer getCancelledSignups() { return cancelledSignups; }
        public void setCancelledSignups(Integer cancelledSignups) { this.cancelledSignups = cancelledSignups; }
    }
}
