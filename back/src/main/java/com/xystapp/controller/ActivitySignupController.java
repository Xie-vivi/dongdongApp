package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.ActivitySignup;
import com.xystapp.service.ActivitySignupService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 活动报名控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "活动报名管理")
@RestController
@RequestMapping("/activities")
public class ActivitySignupController {

    private static final Logger log = LoggerFactory.getLogger(ActivitySignupController.class);

    @Autowired
    private ActivitySignupService activitySignupService;

    // ==================== 报名管理 ====================

    /**
     * 活动报名
     */
    @ApiOperation("活动报名")
    @PostMapping("/{activityId}/signup")
    public Result<ActivitySignup> signupActivity(@ApiParam("活动ID") @PathVariable Long activityId,
                                                  @ApiParam("报名备注") @RequestParam(required = false) String note) {
        log.info("活动报名: activityId={}, note={}", activityId, note);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ActivitySignup signup = activitySignupService.signupActivity(activityId, currentUserId, note);

        return Result.success(signup);
    }

    /**
     * 取消报名
     */
    @ApiOperation("取消报名")
    @DeleteMapping("/{activityId}/signup")
    public Result<Void> cancelSignup(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("取消报名: activityId={}", activityId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        activitySignupService.cancelSignup(activityId, currentUserId);

        return Result.success();
    }

    /**
     * 审核报名（通过）
     */
    @ApiOperation("审核报名（通过）")
    @PutMapping("/{activityId}/signups/{userId}/approve")
    public Result<ActivitySignup> approveSignup(@ApiParam("活动ID") @PathVariable Long activityId,
                                                 @ApiParam("用户ID") @PathVariable Long userId) {
        log.info("审核通过报名: activityId={}, userId={}", activityId, userId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ActivitySignup signup = activitySignupService.approveSignup(activityId, userId, currentUserId);

        return Result.success(signup);
    }

    /**
     * 审核报名（拒绝）
     */
    @ApiOperation("审核报名（拒绝）")
    @PutMapping("/{activityId}/signups/{userId}/reject")
    public Result<ActivitySignup> rejectSignup(@ApiParam("活动ID") @PathVariable Long activityId,
                                                @ApiParam("用户ID") @PathVariable Long userId,
                                                @ApiParam("拒绝理由") @RequestParam(required = false) String reason) {
        log.info("审核拒绝报名: activityId={}, userId={}, reason={}", activityId, userId, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ActivitySignup signup = activitySignupService.rejectSignup(activityId, userId, currentUserId, reason);

        return Result.success(signup);
    }

    /**
     * 批量审核报名（通过）
     */
    @ApiOperation("批量审核报名（通过）")
    @PostMapping("/{activityId}/signups/batch-approve")
    public Result<List<ActivitySignup>> batchApproveSignups(@ApiParam("活动ID") @PathVariable Long activityId,
                                                            @ApiParam("用户ID列表") @RequestBody List<Long> userIds) {
        log.info("批量审核通过报名: activityId={}, userIds={}", activityId, userIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ActivitySignup> results = activitySignupService.batchApproveSignups(activityId, userIds, currentUserId);

        return Result.success(results);
    }

    /**
     * 批量审核报名（拒绝）
     */
    @ApiOperation("批量审核报名（拒绝）")
    @PostMapping("/{activityId}/signups/batch-reject")
    public Result<List<ActivitySignup>> batchRejectSignups(@ApiParam("活动ID") @PathVariable Long activityId,
                                                           @ApiParam("用户ID列表") @RequestBody List<Long> userIds,
                                                           @ApiParam("拒绝理由") @RequestParam(required = false) String reason) {
        log.info("批量审核拒绝报名: activityId={}, userIds={}, reason={}", activityId, userIds, reason);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ActivitySignup> results = activitySignupService.batchRejectSignups(activityId, userIds, currentUserId, reason);

        return Result.success(results);
    }

    // ==================== 报名查询 ====================

    /**
     * 获取活动报名列表
     */
    @ApiOperation("获取活动报名列表")
    @GetMapping("/{activityId}/signups")
    public Result<IPage<ActivitySignup>> getActivitySignups(@ApiParam("活动ID") @PathVariable Long activityId,
                                                            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                            @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        log.info("获取活动报名列表: activityId={}, page={}, size={}, status={}", activityId, page, size, status);

        IPage<ActivitySignup> result = activitySignupService.getActivitySignups(activityId, page, size, status);

        return Result.success(result);
    }

    /**
     * 获取用户报名列表
     */
    @ApiOperation("获取用户报名列表")
    @GetMapping("/signups/user")
    public Result<IPage<ActivitySignup>> getUserSignups(@ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                        @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                        @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        log.info("获取用户报名列表: page={}, size={}, status={}", page, size, status);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<ActivitySignup> result = activitySignupService.getUserSignups(currentUserId, page, size, status);

        return Result.success(result);
    }

    /**
     * 获取报名详情
     */
    @ApiOperation("获取报名详情")
    @GetMapping("/{activityId}/signups/{userId}")
    public Result<ActivitySignup> getSignupDetail(@ApiParam("活动ID") @PathVariable Long activityId,
                                                  @ApiParam("用户ID") @PathVariable Long userId) {
        log.info("获取报名详情: activityId={}, userId={}", activityId, userId);

        ActivitySignup signup = activitySignupService.getSignupDetail(activityId, userId);

        if (signup == null) {
            return Result.error(404, "报名记录不存在");
        }

        return Result.success(signup);
    }

    /**
     * 获取用户在活动中的报名信息
     */
    @ApiOperation("获取用户在活动中的报名信息")
    @GetMapping("/{activityId}/signup-info")
    public Result<ActivitySignup> getUserActivitySignup(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("获取用户在活动中的报名信息: activityId={}", activityId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ActivitySignup signup = activitySignupService.getUserActivitySignup(activityId, currentUserId);

        return Result.success(signup);
    }

    /**
     * 获取活动报名统计
     */
    @ApiOperation("获取活动报名统计")
    @GetMapping("/{activityId}/signup-stats")
    public Result<ActivitySignupService.ActivitySignupStats> getActivitySignupStats(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("获取活动报名统计: activityId={}", activityId);

        ActivitySignupService.ActivitySignupStats stats = activitySignupService.getActivitySignupStats(activityId);

        return Result.success(stats);
    }

    /**
     * 获取用户报名统计
     */
    @ApiOperation("获取用户报名统计")
    @GetMapping("/signup-stats/user")
    public Result<ActivitySignupService.UserSignupStats> getUserSignupStats() {
        log.info("获取用户报名统计");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ActivitySignupService.UserSignupStats stats = activitySignupService.getUserSignupStats(currentUserId);

        return Result.success(stats);
    }

    // ==================== 权限验证 ====================

    /**
     * 检查用户是否已报名
     */
    @ApiOperation("检查用户是否已报名")
    @GetMapping("/{activityId}/is-signed-up")
    public Result<Boolean> isUserSignedUp(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查用户是否已报名: activityId={}", activityId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isSignedUp = activitySignupService.isUserSignedUp(activityId, currentUserId);

        return Result.success(isSignedUp);
    }

    /**
     * 检查用户是否可以报名
     */
    @ApiOperation("检查用户是否可以报名")
    @GetMapping("/{activityId}/can-signup")
    public Result<Boolean> canUserSignup(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查用户是否可以报名: activityId={}", activityId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean canSignup = activitySignupService.canUserSignup(activityId, currentUserId);

        return Result.success(canSignup);
    }

    /**
     * 检查用户是否可以取消报名
     */
    @ApiOperation("检查用户是否可以取消报名")
    @GetMapping("/{activityId}/can-cancel")
    public Result<Boolean> canUserCancelSignup(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查用户是否可以取消报名: activityId={}", activityId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean canCancel = activitySignupService.canUserCancelSignup(activityId, currentUserId);

        return Result.success(canCancel);
    }

    /**
     * 检查用户是否有审核权限
     */
    @ApiOperation("检查用户是否有审核权限")
    @GetMapping("/{activityId}/can-approve")
    public Result<Boolean> hasApprovePermission(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查用户是否有审核权限: activityId={}", activityId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        hasApprovePermission(activityId, currentUserId);

        return Result.success(true);
    }

    /**
     * 检查活动是否存在
     */
    @ApiOperation("检查活动是否存在")
    @GetMapping("/{activityId}/exists")
    public Result<Boolean> activityExists(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查活动是否存在: activityId={}", activityId);

        boolean exists = activitySignupService.activityExists(activityId);

        return Result.success(exists);
    }

    /**
     * 检查报名是否存在
     */
    @ApiOperation("检查报名是否存在")
    @GetMapping("/{activityId}/signup-exists")
    public Result<Boolean> signupExists(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("检查报名是否存在: activityId={}", activityId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean exists = activitySignupService.signupExists(activityId, currentUserId);

        return Result.success(exists);
    }

    // ==================== 统计信息 ====================

    /**
     * 获取活动报名数量
     */
    @ApiOperation("获取活动报名数量")
    @GetMapping("/{activityId}/signup-count")
    public Result<Integer> getActivitySignupCount(@ApiParam("活动ID") @PathVariable Long activityId,
                                                  @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        log.info("获取活动报名数量: activityId={}, status={}", activityId, status);

        Integer count = activitySignupService.getActivitySignupCount(activityId, status);

        return Result.success(count);
    }

    /**
     * 获取用户报名数量
     */
    @ApiOperation("获取用户报名数量")
    @GetMapping("/signup-count/user")
    public Result<Integer> getUserSignupCount(@ApiParam("状态筛选") @RequestParam(required = false) String status) {
        log.info("获取用户报名数量: status={}", status);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Integer count = activitySignupService.getUserSignupCount(currentUserId, status);

        return Result.success(count);
    }

    /**
     * 获取今日报名数量
     */
    @ApiOperation("获取今日报名数量")
    @GetMapping("/{activityId}/today-signup-count")
    public Result<Integer> getTodaySignupCount(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("获取今日报名数量: activityId={}", activityId);

        Integer count = activitySignupService.getTodaySignupCount(activityId);

        return Result.success(count);
    }

    /**
     * 获取报名趋势统计
     */
    @ApiOperation("获取报名趋势统计")
    @GetMapping("/{activityId}/signup-trend")
    public Result<List<ActivitySignupService.SignupTrendStats>> getSignupTrend(@ApiParam("活动ID") @PathVariable Long activityId,
                                                                               @ApiParam("天数") @RequestParam(defaultValue = "7") Integer days) {
        log.info("获取报名趋势统计: activityId={}, days={}", activityId, days);

        List<ActivitySignupService.SignupTrendStats> trend = activitySignupService.getSignupTrend(activityId, days);

        return Result.success(trend);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量获取报名信息
     */
    @ApiOperation("批量获取报名信息")
    @PostMapping("/signups/batch")
    public Result<List<ActivitySignup>> getSignupsByIds(@ApiParam("报名ID列表") @RequestBody List<Long> signupIds) {
        log.info("批量获取报名信息: signupIds={}", signupIds);

        List<ActivitySignup> signups = activitySignupService.getSignupsByIds(signupIds);

        return Result.success(signups);
    }

    /**
     * 批量获取用户报名信息
     */
    @ApiOperation("批量获取用户报名信息")
    @PostMapping("/signups/user/batch")
    public Result<List<ActivitySignup>> getUserSignupsByActivityIds(@ApiParam("活动ID列表") @RequestBody List<Long> activityIds) {
        log.info("批量获取用户报名信息: activityIds={}", activityIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ActivitySignup> signups = activitySignupService.getUserSignupsByActivityIds(activityIds, currentUserId);

        return Result.success(signups);
    }

    /**
     * 批量检查报名状态
     */
    @ApiOperation("批量检查报名状态")
    @PostMapping("/signups/batch-check")
    public Result<List<ActivitySignupService.SignupStatusInfo>> batchCheckSignupStatus(@ApiParam("活动ID列表") @RequestBody List<Long> activityIds) {
        log.info("批量检查报名状态: activityIds={}", activityIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ActivitySignupService.SignupStatusInfo> statusInfo = activitySignupService.batchCheckSignupStatus(activityIds, currentUserId);

        return Result.success(statusInfo);
    }

    // ==================== 数据导出 ====================

    /**
     * 导出活动报名列表
     */
    @ApiOperation("导出活动报名列表")
    @GetMapping("/{activityId}/signups/export")
    public Result<List<ActivitySignup>> exportActivitySignups(@ApiParam("活动ID") @PathVariable Long activityId,
                                                              @ApiParam("状态筛选") @RequestParam(required = false) String status) {
        log.info("导出活动报名列表: activityId={}, status={}", activityId, status);

        List<ActivitySignup> signups = activitySignupService.exportActivitySignups(activityId, status);

        return Result.success(signups);
    }

    /**
     * 导出用户报名历史
     */
    @ApiOperation("导出用户报名历史")
    @GetMapping("/signups/user/export")
    public Result<List<ActivitySignup>> exportUserSignups(@ApiParam("状态筛选") @RequestParam(required = false) String status) {
        log.info("导出用户报名历史: status={}", status);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ActivitySignup> signups = activitySignupService.exportUserSignups(currentUserId, status);

        return Result.success(signups);
    }

    // ==================== 业务查询 ====================

    /**
     * 获取最近报名的用户
     */
    @ApiOperation("获取最近报名的用户")
    @GetMapping("/{activityId}/recent-signups")
    public Result<List<ActivitySignup>> getRecentSignups(@ApiParam("活动ID") @PathVariable Long activityId,
                                                         @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取最近报名的用户: activityId={}, limit={}", activityId, limit);

        List<ActivitySignup> signups = activitySignupService.getRecentSignups(activityId, limit);

        return Result.success(signups);
    }

    /**
     * 获取待审核的报名
     */
    @ApiOperation("获取待审核的报名")
    @GetMapping("/{activityId}/pending-signups")
    public Result<List<ActivitySignup>> getPendingSignups(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("获取待审核的报名: activityId={}", activityId);

        List<ActivitySignup> signups = activitySignupService.getPendingSignups(activityId);

        return Result.success(signups);
    }

    /**
     * 获取需要处理的报名数量
     */
    @ApiOperation("获取需要处理的报名数量")
    @GetMapping("/{activityId}/pending-count")
    public Result<Integer> getPendingSignupCount(@ApiParam("活动ID") @PathVariable Long activityId) {
        log.info("获取需要处理的报名数量: activityId={}", activityId);

        Integer count = activitySignupService.getPendingSignupCount(activityId);

        return Result.success(count);
    }

    /**
     * 获取活动创建者的待处理报名
     */
    @ApiOperation("获取活动创建者的待处理报名")
    @GetMapping("/signups/creator-pending")
    public Result<List<ActivitySignup>> getCreatorPendingSignups(@ApiParam("限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("获取活动创建者的待处理报名: limit={}", limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ActivitySignup> signups = activitySignupService.getCreatorPendingSignups(currentUserId, limit);

        return Result.success(signups);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查审核权限
     */
    private void hasApprovePermission(Long activityId, Long userId) {
        if (!activitySignupService.hasApprovePermission(activityId, userId)) {
            throw new RuntimeException("没有审核权限");
        }
    }
}
