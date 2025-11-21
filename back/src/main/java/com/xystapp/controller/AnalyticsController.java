package com.xystapp.controller;

import com.xystapp.common.Result;
import com.xystapp.service.AnalyticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 数据分析控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/analytics")
@Api(tags = "数据分析接口")
public class AnalyticsController {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsController.class);

    @Autowired
    private AnalyticsService analyticsService;

    // ==================== 概览统计 ====================

    @GetMapping("/overview")
    @ApiOperation("获取系统概览统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.SystemOverviewStats> getSystemOverview() {
        try {
            AnalyticsService.SystemOverviewStats stats = analyticsService.getSystemOverviewStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取系统概览统计失败: error={}", e.getMessage(), e);
            return Result.error("获取系统概览统计失败");
        }
    }

    @GetMapping("/realtime")
    @ApiOperation("获取实时统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.RealTimeStats> getRealTimeStats() {
        try {
            AnalyticsService.RealTimeStats stats = analyticsService.getRealTimeStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取实时统计失败: error={}", e.getMessage(), e);
            return Result.error("获取实时统计失败");
        }
    }

    // ==================== 用户分析 ====================

    @GetMapping("/users/growth")
    @ApiOperation("获取用户增长趋势")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.UserGrowthStats>> getUserGrowthTrend(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("统计周期: day/week/month") @RequestParam(defaultValue = "day") String period) {
        try {
            List<AnalyticsService.UserGrowthStats> stats = analyticsService.getUserGrowthTrend(startDate, endDate, period);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户增长趋势失败: error={}", e.getMessage(), e);
            return Result.error("获取用户增长趋势失败");
        }
    }

    @GetMapping("/users/activity")
    @ApiOperation("获取用户活跃度分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.UserActivityStats> getUserActivityStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.UserActivityStats stats = analyticsService.getUserActivityStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户活跃度分析失败: error={}", e.getMessage(), e);
            return Result.error("获取用户活跃度分析失败");
        }
    }

    @GetMapping("/users/region")
    @ApiOperation("获取用户地域分布")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.UserRegionStats>> getUserRegionDistribution(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AnalyticsService.UserRegionStats> stats = analyticsService.getUserRegionDistribution(limit);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户地域分布失败: error={}", e.getMessage(), e);
            return Result.error("获取用户地域分布失败");
        }
    }

    @GetMapping("/users/retention")
    @ApiOperation("获取用户留存分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.UserRetentionStats> getUserRetentionAnalysis(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("统计周期") @RequestParam(defaultValue = "day") String period) {
        try {
            AnalyticsService.UserRetentionStats stats = analyticsService.getUserRetentionAnalysis(startDate, endDate, period);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户留存分析失败: error={}", e.getMessage(), e);
            return Result.error("获取用户留存分析失败");
        }
    }

    @GetMapping("/users/{userId}/behavior")
    @ApiOperation("获取用户行为分析")
    @PreAuthorize("hasRole('ADMIN') or @securityUtils.isCurrentUser(#userId)")
    public Result<AnalyticsService.UserBehaviorStats> getUserBehaviorStats(
            @ApiParam("用户ID") @PathVariable Long userId,
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.UserBehaviorStats stats = analyticsService.getUserBehaviorStats(userId, startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户行为分析失败: userId={}, error={}", userId, e.getMessage(), e);
            return Result.error("获取用户行为分析失败");
        }
    }

    // ==================== 内容分析 ====================

    @GetMapping("/content/trend")
    @ApiOperation("获取内容发布趋势")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.ContentTrendStats>> getContentPublishTrend(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("统计周期: day/week/month") @RequestParam(defaultValue = "day") String period,
            @ApiParam("内容类型: post/activity/all") @RequestParam(defaultValue = "all") String contentType) {
        try {
            List<AnalyticsService.ContentTrendStats> stats = analyticsService.getContentPublishTrend(startDate, endDate, period, contentType);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取内容发布趋势失败: error={}", e.getMessage(), e);
            return Result.error("获取内容发布趋势失败");
        }
    }

    @GetMapping("/content/hot")
    @ApiOperation("获取热门内容分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.HotContentStats>> getHotContentAnalysis(
            @ApiParam("内容类型: post/activity") @RequestParam String contentType,
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AnalyticsService.HotContentStats> stats = analyticsService.getHotContentAnalysis(contentType, startDate, endDate, limit);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取热门内容分析失败: error={}", e.getMessage(), e);
            return Result.error("获取热门内容分析失败");
        }
    }

    @GetMapping("/content/interaction")
    @ApiOperation("获取内容互动分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.ContentInteractionStats> getContentInteractionStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("内容类型: post/activity/all") @RequestParam(defaultValue = "all") String contentType) {
        try {
            AnalyticsService.ContentInteractionStats stats = analyticsService.getContentInteractionStats(startDate, endDate, contentType);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取内容互动分析失败: error={}", e.getMessage(), e);
            return Result.error("获取内容互动分析失败");
        }
    }

    @GetMapping("/content/quality")
    @ApiOperation("获取内容质量分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.ContentQualityStats> getContentQualityAnalysis(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.ContentQualityStats stats = analyticsService.getContentQualityAnalysis(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取内容质量分析失败: error={}", e.getMessage(), e);
            return Result.error("获取内容质量分析失败");
        }
    }

    // ==================== 社交分析 ====================

    @GetMapping("/social/trend")
    @ApiOperation("获取社交互动趋势")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.SocialInteractionTrend>> getSocialInteractionTrend(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("统计周期: day/week/month") @RequestParam(defaultValue = "day") String period) {
        try {
            List<AnalyticsService.SocialInteractionTrend> stats = analyticsService.getSocialInteractionTrend(startDate, endDate, period);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取社交互动趋势失败: error={}", e.getMessage(), e);
            return Result.error("获取社交互动趋势失败");
        }
    }

    @GetMapping("/social/follow-stats")
    @ApiOperation("获取关注关系分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.FollowRelationshipStats> getFollowRelationshipStats() {
        try {
            AnalyticsService.FollowRelationshipStats stats = analyticsService.getFollowRelationshipStats();
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取关注关系分析失败: error={}", e.getMessage(), e);
            return Result.error("获取关注关系分析失败");
        }
    }

    @GetMapping("/social/influence-ranking")
    @ApiOperation("获取用户影响力排行")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.UserInfluenceRanking>> getUserInfluenceRanking(
            @ApiParam("排序指标: followers/likes/posts") @RequestParam String metric,
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AnalyticsService.UserInfluenceRanking> rankings = analyticsService.getUserInfluenceRanking(metric, startDate, endDate, limit);
            return Result.success(rankings);
        } catch (Exception e) {
            log.error("获取用户影响力排行失败: error={}", e.getMessage(), e);
            return Result.error("获取用户影响力排行失败");
        }
    }

    @GetMapping("/social/community-activity")
    @ApiOperation("获取社群活跃度分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.CommunityActivityStats> getCommunityActivityStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.CommunityActivityStats stats = analyticsService.getCommunityActivityStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取社群活跃度分析失败: error={}", e.getMessage(), e);
            return Result.error("获取社群活跃度分析失败");
        }
    }

    // ==================== 活动分析 ====================

    @GetMapping("/activities/participation-trend")
    @ApiOperation("获取活动参与趋势")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.ActivityParticipationTrend>> getActivityParticipationTrend(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("统计周期: day/week/month") @RequestParam(defaultValue = "day") String period) {
        try {
            List<AnalyticsService.ActivityParticipationTrend> stats = analyticsService.getActivityParticipationTrend(startDate, endDate, period);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取活动参与趋势失败: error={}", e.getMessage(), e);
            return Result.error("获取活动参与趋势失败");
        }
    }

    @GetMapping("/activities/effectiveness")
    @ApiOperation("获取活动效果分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.ActivityEffectivenessStats> getActivityEffectivenessStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.ActivityEffectivenessStats stats = analyticsService.getActivityEffectivenessStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取活动效果分析失败: error={}", e.getMessage(), e);
            return Result.error("获取活动效果分析失败");
        }
    }

    @GetMapping("/activities/field-usage")
    @ApiOperation("获取场地使用分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.FieldUsageStats> getFieldUsageStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.FieldUsageStats stats = analyticsService.getFieldUsageStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取场地使用分析失败: error={}", e.getMessage(), e);
            return Result.error("获取场地使用分析失败");
        }
    }

    // ==================== 消息分析 ====================

    @GetMapping("/messages/trend")
    @ApiOperation("获取消息发送趋势")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.MessageTrendStats>> getMessageTrend(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("统计周期: day/week/month") @RequestParam(defaultValue = "day") String period) {
        try {
            List<AnalyticsService.MessageTrendStats> stats = analyticsService.getMessageTrend(startDate, endDate, period);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取消息发送趋势失败: error={}", e.getMessage(), e);
            return Result.error("获取消息发送趋势失败");
        }
    }

    @GetMapping("/messages/activity")
    @ApiOperation("获取消息活跃度分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.MessageActivityStats> getMessageActivityStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.MessageActivityStats stats = analyticsService.getMessageActivityStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取消息活跃度分析失败: error={}", e.getMessage(), e);
            return Result.error("获取消息活跃度分析失败");
        }
    }

    @GetMapping("/messages/chat-ranking")
    @ApiOperation("获取聊天热度排行")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.ChatHeatRanking>> getChatHeatRanking(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AnalyticsService.ChatHeatRanking> rankings = analyticsService.getChatHeatRanking(startDate, endDate, limit);
            return Result.success(rankings);
        } catch (Exception e) {
            log.error("获取聊天热度排行失败: error={}", e.getMessage(), e);
            return Result.error("获取聊天热度排行失败");
        }
    }

    // ==================== 搜索分析 ====================

    @GetMapping("/search/behavior")
    @ApiOperation("获取搜索行为分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.SearchBehaviorStats> getSearchBehaviorStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.SearchBehaviorStats stats = analyticsService.getSearchBehaviorStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取搜索行为分析失败: error={}", e.getMessage(), e);
            return Result.error("获取搜索行为分析失败");
        }
    }

    @GetMapping("/search/keyword-ranking")
    @ApiOperation("获取搜索关键词排行")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.KeywordRanking>> getKeywordRanking(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AnalyticsService.KeywordRanking> rankings = analyticsService.getKeywordRanking(startDate, endDate, limit);
            return Result.success(rankings);
        } catch (Exception e) {
            log.error("获取搜索关键词排行失败: error={}", e.getMessage(), e);
            return Result.error("获取搜索关键词排行失败");
        }
    }

    @GetMapping("/search/conversion")
    @ApiOperation("获取搜索转化分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.SearchConversionStats> getSearchConversionStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.SearchConversionStats stats = analyticsService.getSearchConversionStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取搜索转化分析失败: error={}", e.getMessage(), e);
            return Result.error("获取搜索转化分析失败");
        }
    }

    // ==================== 性能分析 ====================

    @GetMapping("/performance/system")
    @ApiOperation("获取系统性能指标")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.SystemPerformanceStats> getSystemPerformanceStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.SystemPerformanceStats stats = analyticsService.getSystemPerformanceStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取系统性能指标失败: error={}", e.getMessage(), e);
            return Result.error("获取系统性能指标失败");
        }
    }

    @GetMapping("/performance/api-calls")
    @ApiOperation("获取API调用统计")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.ApiCallStats>> getApiCallStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AnalyticsService.ApiCallStats> stats = analyticsService.getApiCallStats(startDate, endDate, limit);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取API调用统计失败: error={}", e.getMessage(), e);
            return Result.error("获取API调用统计失败");
        }
    }

    @GetMapping("/performance/error-rate")
    @ApiOperation("获取错误率分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.ErrorRateStats> getErrorRateStats(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.ErrorRateStats stats = analyticsService.getErrorRateStats(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取错误率分析失败: error={}", e.getMessage(), e);
            return Result.error("获取错误率分析失败");
        }
    }

    // ==================== 业务指标 ====================

    @GetMapping("/business/core-metrics")
    @ApiOperation("获取核心业务指标")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.CoreBusinessMetrics> getCoreBusinessMetrics(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.CoreBusinessMetrics metrics = analyticsService.getCoreBusinessMetrics(startDate, endDate);
            return Result.success(metrics);
        } catch (Exception e) {
            log.error("获取核心业务指标失败: error={}", e.getMessage(), e);
            return Result.error("获取核心业务指标失败");
        }
    }

    @GetMapping("/business/user-value")
    @ApiOperation("获取用户价值分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<AnalyticsService.UserValueAnalysis>> getUserValueAnalysis(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<AnalyticsService.UserValueAnalysis> analysis = analyticsService.getUserValueAnalysis(startDate, endDate, limit);
            return Result.success(analysis);
        } catch (Exception e) {
            log.error("获取用户价值分析失败: error={}", e.getMessage(), e);
            return Result.error("获取用户价值分析失败");
        }
    }

    @GetMapping("/business/revenue")
    @ApiOperation("获取收入分析")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.RevenueAnalysisStats> getRevenueAnalysis(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.RevenueAnalysisStats stats = analyticsService.getRevenueAnalysis(startDate, endDate);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取收入分析失败: error={}", e.getMessage(), e);
            return Result.error("获取收入分析失败");
        }
    }

    // ==================== 报表生成 ====================

    @GetMapping("/reports/daily")
    @ApiOperation("生成日报")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.DailyReport> generateDailyReport(
            @ApiParam("报表日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        try {
            AnalyticsService.DailyReport report = analyticsService.generateDailyReport(date);
            return Result.success(report);
        } catch (Exception e) {
            log.error("生成日报失败: date={}, error={}", date, e.getMessage(), e);
            return Result.error("生成日报失败");
        }
    }

    @GetMapping("/reports/weekly")
    @ApiOperation("生成周报")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.WeeklyReport> generateWeeklyReport(
            @ApiParam("开始日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @ApiParam("结束日期") @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        try {
            AnalyticsService.WeeklyReport report = analyticsService.generateWeeklyReport(startDate, endDate);
            return Result.success(report);
        } catch (Exception e) {
            log.error("生成周报失败: error={}", e.getMessage(), e);
            return Result.error("生成周报失败");
        }
    }

    @GetMapping("/reports/monthly")
    @ApiOperation("生成月报")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.MonthlyReport> generateMonthlyReport(
            @ApiParam("年份") @RequestParam Integer year,
            @ApiParam("月份") @RequestParam Integer month) {
        try {
            LocalDate yearDate = LocalDate.of(year, 1, 1);
            AnalyticsService.MonthlyReport report = analyticsService.generateMonthlyReport(yearDate, month);
            return Result.success(report);
        } catch (Exception e) {
            log.error("生成月报失败: error={}", e.getMessage(), e);
            return Result.error("生成月报失败");
        }
    }

    @PostMapping("/reports/custom")
    @ApiOperation("生成自定义报表")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.CustomReport> generateCustomReport(
            @RequestBody AnalyticsService.CustomReportRequest request) {
        try {
            AnalyticsService.CustomReport report = analyticsService.generateCustomReport(request);
            return Result.success(report);
        } catch (Exception e) {
            log.error("生成自定义报表失败: error={}", e.getMessage(), e);
            return Result.error("生成自定义报表失败");
        }
    }

    // ==================== 数据导出 ====================

    @PostMapping("/export/users")
    @ApiOperation("导出用户数据")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<byte[]> exportUserData(@RequestBody AnalyticsService.UserExportRequest request) {
        try {
            byte[] data = analyticsService.exportUserData(request);
            return Result.success(data);
        } catch (Exception e) {
            log.error("导出用户数据失败: error={}", e.getMessage(), e);
            return Result.error("导出用户数据失败");
        }
    }

    @PostMapping("/export/content")
    @ApiOperation("导出内容数据")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<byte[]> exportContentData(@RequestBody AnalyticsService.ContentExportRequest request) {
        try {
            byte[] data = analyticsService.exportContentData(request);
            return Result.success(data);
        } catch (Exception e) {
            log.error("导出内容数据失败: error={}", e.getMessage(), e);
            return Result.error("导出内容数据失败");
        }
    }

    @PostMapping("/export/statistics")
    @ApiOperation("导出统计数据")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<byte[]> exportStatisticsData(@RequestBody AnalyticsService.StatisticsExportRequest request) {
        try {
            byte[] data = analyticsService.exportStatisticsData(request);
            return Result.success(data);
        } catch (Exception e) {
            log.error("导出统计数据失败: error={}", e.getMessage(), e);
            return Result.error("导出统计数据失败");
        }
    }

    // ==================== 预测分析 ====================

    @GetMapping("/prediction/user-growth")
    @ApiOperation("用户增长预测")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.UserGrowthPrediction> predictUserGrowth(
            @ApiParam("预测天数") @RequestParam Integer days) {
        try {
            AnalyticsService.UserGrowthPrediction prediction = analyticsService.predictUserGrowth(days);
            return Result.success(prediction);
        } catch (Exception e) {
            log.error("用户增长预测失败: error={}", e.getMessage(), e);
            return Result.error("用户增长预测失败");
        }
    }

    @GetMapping("/prediction/content-trend")
    @ApiOperation("内容热度预测")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.ContentTrendPrediction> predictContentTrend(
            @ApiParam("内容类型") @RequestParam String contentType,
            @ApiParam("预测天数") @RequestParam Integer days) {
        try {
            AnalyticsService.ContentTrendPrediction prediction = analyticsService.predictContentTrend(contentType, days);
            return Result.success(prediction);
        } catch (Exception e) {
            log.error("内容热度预测失败: error={}", e.getMessage(), e);
            return Result.error("内容热度预测失败");
        }
    }

    @GetMapping("/prediction/activity-participation")
    @ApiOperation("活动参与预测")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<AnalyticsService.ActivityParticipationPrediction> predictActivityParticipation(
            @ApiParam("预测天数") @RequestParam Integer days) {
        try {
            AnalyticsService.ActivityParticipationPrediction prediction = analyticsService.predictActivityParticipation(days);
            return Result.success(prediction);
        } catch (Exception e) {
            log.error("活动参与预测失败: error={}", e.getMessage(), e);
            return Result.error("活动参与预测失败");
        }
    }
}
