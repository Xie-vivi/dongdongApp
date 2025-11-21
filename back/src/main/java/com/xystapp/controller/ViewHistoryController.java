package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.ViewHistory;
import com.xystapp.service.ViewHistoryService;
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
 * 浏览记录控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "浏览记录管理")
@RestController
@RequestMapping("/view-history")
public class ViewHistoryController {

    private static final Logger log = LoggerFactory.getLogger(ViewHistoryController.class);

    @Autowired
    private ViewHistoryService viewHistoryService;

    // ==================== 浏览记录管理 ====================

    /**
     * 记录浏览行为
     */
    @ApiOperation("记录浏览行为")
    @PostMapping("/track")
    public Result<Void> trackView(@ApiParam("内容类型") @RequestParam String itemType,
                                  @ApiParam("内容ID") @RequestParam Long itemId) {
        log.info("记录浏览行为: itemType={}, itemId={}", itemType, itemId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        viewHistoryService.trackView(currentUserId, itemType, itemId);

        return Result.success();
    }

    /**
     * 批量记录浏览行为
     */
    @ApiOperation("批量记录浏览行为")
    @PostMapping("/track/batch")
    public Result<Void> batchTrackViews(@ApiParam("浏览请求列表") @RequestBody List<ViewHistoryService.ViewTrackRequest> requests) {
        log.info("批量记录浏览行为: requestCount={}", requests.size());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        viewHistoryService.batchTrackViews(currentUserId, requests);

        return Result.success();
    }

    /**
     * 删除浏览记录
     */
    @ApiOperation("删除浏览记录")
    @DeleteMapping("/{historyId}")
    public Result<Void> deleteViewHistory(@ApiParam("记录ID") @PathVariable Long historyId) {
        log.info("删除浏览记录: historyId={}", historyId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        viewHistoryService.deleteViewHistory(currentUserId, historyId);

        return Result.success();
    }

    /**
     * 批量删除浏览记录
     */
    @ApiOperation("批量删除浏览记录")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteViewHistory(@ApiParam("记录ID列表") @RequestBody List<Long> historyIds) {
        log.info("批量删除浏览记录: historyIds={}", historyIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        viewHistoryService.batchDeleteViewHistory(currentUserId, historyIds);

        return Result.success();
    }

    /**
     * 清空用户浏览记录
     */
    @ApiOperation("清空用户浏览记录")
    @DeleteMapping("/clear")
    public Result<Void> clearUserViewHistory() {
        log.info("清空用户浏览记录");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        viewHistoryService.clearUserViewHistory(currentUserId);

        return Result.success();
    }

    /**
     * 删除指定类型的浏览记录
     */
    @ApiOperation("删除指定类型的浏览记录")
    @DeleteMapping("/type/{itemType}")
    public Result<Void> deleteViewHistoryByType(@ApiParam("内容类型") @PathVariable String itemType) {
        log.info("删除指定类型的浏览记录: itemType={}", itemType);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        viewHistoryService.deleteViewHistoryByType(currentUserId, itemType);

        return Result.success();
    }

    // ==================== 浏览记录查询 ====================

    /**
     * 获取用户浏览记录列表
     */
    @ApiOperation("获取用户浏览记录列表")
    @GetMapping("/list")
    public Result<IPage<ViewHistory>> getUserViewHistory(@ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                          @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                          @ApiParam("内容类型筛选") @RequestParam(required = false) String itemType,
                                                          @ApiParam("排序方式") @RequestParam(defaultValue = "time_desc") String sortBy) {
        log.info("获取用户浏览记录列表: page={}, size={}, itemType={}, sortBy={}", page, size, itemType, sortBy);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<ViewHistory> result = viewHistoryService.getUserViewHistory(currentUserId, page, size, itemType, sortBy);

        return Result.success(result);
    }

    /**
     * 获取浏览记录详情
     */
    @ApiOperation("获取浏览记录详情")
    @GetMapping("/{historyId}")
    public Result<ViewHistory> getViewHistoryDetail(@ApiParam("记录ID") @PathVariable Long historyId) {
        log.info("获取浏览记录详情: historyId={}", historyId);

        ViewHistory viewHistory = viewHistoryService.getViewHistoryDetail(historyId);

        if (viewHistory == null) {
            return Result.error(404, "浏览记录不存在");
        }

        return Result.success(viewHistory);
    }

    /**
     * 获取用户指定类型的浏览记录
     */
    @ApiOperation("获取用户指定类型的浏览记录")
    @GetMapping("/type/{itemType}")
    public Result<IPage<ViewHistory>> getUserViewHistoryByType(@ApiParam("内容类型") @PathVariable String itemType,
                                                               @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                               @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                               @ApiParam("排序方式") @RequestParam(defaultValue = "time_desc") String sortBy) {
        log.info("获取用户指定类型的浏览记录: itemType={}, page={}, size={}, sortBy={}", itemType, page, size, sortBy);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<ViewHistory> result = viewHistoryService.getUserViewHistoryByType(currentUserId, itemType, page, size, sortBy);

        return Result.success(result);
    }

    /**
     * 获取用户最近浏览记录
     */
    @ApiOperation("获取用户最近浏览记录")
    @GetMapping("/recent")
    public Result<List<ViewHistory>> getRecentViewHistory(@ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取用户最近浏览记录: limit={}", limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ViewHistory> result = viewHistoryService.getRecentViewHistory(currentUserId, limit);

        return Result.success(result);
    }

    /**
     * 获取用户指定时间范围的浏览记录
     */
    @ApiOperation("获取用户指定时间范围的浏览记录")
    @GetMapping("/time-range/{timeRange}")
    public Result<IPage<ViewHistory>> getUserViewHistoryByTimeRange(@ApiParam("时间范围") @PathVariable String timeRange,
                                                                     @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                                     @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户指定时间范围的浏览记录: timeRange={}, page={}, size={}", timeRange, page, size);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<ViewHistory> result = viewHistoryService.getUserViewHistoryByTimeRange(currentUserId, timeRange, page, size);

        return Result.success(result);
    }

    // ==================== 权限验证 ====================

    /**
     * 检查用户是否可以删除浏览记录
     */
    @ApiOperation("检查用户是否可以删除浏览记录")
    @GetMapping("/{historyId}/can-delete")
    public Result<Boolean> canDeleteViewHistory(@ApiParam("记录ID") @PathVariable Long historyId) {
        log.info("检查用户是否可以删除浏览记录: historyId={}", historyId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean canDelete = viewHistoryService.canDeleteViewHistory(currentUserId, historyId);

        return Result.success(canDelete);
    }

    /**
     * 检查浏览记录是否存在
     */
    @ApiOperation("检查浏览记录是否存在")
    @GetMapping("/{historyId}/exists")
    public Result<Boolean> viewHistoryExists(@ApiParam("记录ID") @PathVariable Long historyId) {
        log.info("检查浏览记录是否存在: historyId={}", historyId);

        boolean exists = viewHistoryService.viewHistoryExists(historyId);

        return Result.success(exists);
    }

    /**
     * 检查用户是否浏览过指定内容
     */
    @ApiOperation("检查用户是否浏览过指定内容")
    @GetMapping("/has-viewed")
    public Result<Boolean> hasUserViewedItem(@ApiParam("内容类型") @RequestParam String itemType,
                                             @ApiParam("内容ID") @RequestParam Long itemId) {
        log.info("检查用户是否浏览过指定内容: itemType={}, itemId={}", itemType, itemId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean hasViewed = viewHistoryService.hasUserViewedItem(currentUserId, itemType, itemId);

        return Result.success(hasViewed);
    }

    // ==================== 统计信息 ====================

    /**
     * 获取用户浏览统计
     */
    @ApiOperation("获取用户浏览统计")
    @GetMapping("/stats/user")
    public Result<ViewHistoryService.UserViewStats> getUserViewStats() {
        log.info("获取用户浏览统计");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ViewHistoryService.UserViewStats stats = viewHistoryService.getUserViewStats(currentUserId);

        return Result.success(stats);
    }

    /**
     * 获取内容浏览统计
     */
    @ApiOperation("获取内容浏览统计")
    @GetMapping("/stats/item")
    public Result<ViewHistoryService.ItemViewStats> getItemViewStats(@ApiParam("内容类型") @RequestParam String itemType,
                                                                      @ApiParam("内容ID") @RequestParam Long itemId) {
        log.info("获取内容浏览统计: itemType={}, itemId={}", itemType, itemId);

        ViewHistoryService.ItemViewStats stats = viewHistoryService.getItemViewStats(itemType, itemId);

        return Result.success(stats);
    }

    /**
     * 获取热门内容统计
     */
    @ApiOperation("获取热门内容统计")
    @GetMapping("/stats/hot")
    public Result<List<ViewHistoryService.HotItemStats>> getHotItems(@ApiParam("内容类型") @RequestParam String itemType,
                                                                     @ApiParam("限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("获取热门内容统计: itemType={}, limit={}", itemType, limit);

        List<ViewHistoryService.HotItemStats> result = viewHistoryService.getHotItems(itemType, limit);

        return Result.success(result);
    }

    /**
     * 获取用户浏览趋势
     */
    @ApiOperation("获取用户浏览趋势")
    @GetMapping("/trend/user")
    public Result<List<ViewHistoryService.ViewTrendStats>> getUserViewTrend(@ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        log.info("获取用户浏览趋势: timeRange={}", timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ViewHistoryService.ViewTrendStats> result = viewHistoryService.getUserViewTrend(currentUserId, timeRange);

        return Result.success(result);
    }

    /**
     * 获取内容浏览趋势
     */
    @ApiOperation("获取内容浏览趋势")
    @GetMapping("/trend/item")
    public Result<List<ViewHistoryService.ViewTrendStats>> getItemViewTrend(@ApiParam("内容类型") @RequestParam String itemType,
                                                                            @ApiParam("内容ID") @RequestParam Long itemId,
                                                                            @ApiParam("时间范围") @RequestParam(defaultValue = "week") String timeRange) {
        log.info("获取内容浏览趋势: itemType={}, itemId={}, timeRange={}", itemType, itemId, timeRange);

        List<ViewHistoryService.ViewTrendStats> result = viewHistoryService.getItemViewTrend(itemType, itemId, timeRange);

        return Result.success(result);
    }

    // ==================== 推荐相关 ====================

    /**
     * 获取推荐内容（基于浏览历史）
     */
    @ApiOperation("获取推荐内容")
    @GetMapping("/recommend")
    public Result<List<ViewHistoryService.RecommendItem>> getRecommendItems(@ApiParam("内容类型") @RequestParam(required = false) String itemType,
                                                                             @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取推荐内容: itemType={}, limit={}", itemType, limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ViewHistoryService.RecommendItem> result = viewHistoryService.getRecommendItems(currentUserId, itemType, limit);

        return Result.success(result);
    }

    /**
     * 获取相关内容（基于当前浏览内容）
     */
    @ApiOperation("获取相关内容")
    @GetMapping("/related")
    public Result<List<ViewHistoryService.RecommendItem>> getRelatedItems(@ApiParam("内容类型") @RequestParam String itemType,
                                                                          @ApiParam("内容ID") @RequestParam Long itemId,
                                                                          @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取相关内容: itemType={}, itemId={}, limit={}", itemType, itemId, limit);

        List<ViewHistoryService.RecommendItem> result = viewHistoryService.getRelatedItems(itemType, itemId, limit);

        return Result.success(result);
    }

    /**
     * 获取用户可能感兴趣的内容
     */
    @ApiOperation("获取用户可能感兴趣的内容")
    @GetMapping("/interest")
    public Result<List<ViewHistoryService.RecommendItem>> getUserInterestItems(@ApiParam("限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("获取用户可能感兴趣的内容: limit={}", limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ViewHistoryService.RecommendItem> result = viewHistoryService.getUserInterestItems(currentUserId, limit);

        return Result.success(result);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量获取浏览记录
     */
    @ApiOperation("批量获取浏览记录")
    @PostMapping("/batch")
    public Result<List<ViewHistory>> getViewHistoriesByIds(@ApiParam("记录ID列表") @RequestBody List<Long> historyIds) {
        log.info("批量获取浏览记录: historyIds={}", historyIds);

        List<ViewHistory> result = viewHistoryService.getViewHistoriesByIds(historyIds);

        return Result.success(result);
    }

    /**
     * 批量检查浏览状态
     */
    @ApiOperation("批量检查浏览状态")
    @PostMapping("/batch-check")
    public Result<List<ViewHistoryService.ViewStatusInfo>> batchCheckViewStatus(@ApiParam("浏览请求列表") @RequestBody List<ViewHistoryService.ViewTrackRequest> requests) {
        log.info("批量检查浏览状态: requestCount={}", requests.size());

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ViewHistoryService.ViewStatusInfo> result = viewHistoryService.batchCheckViewStatus(currentUserId, requests);

        return Result.success(result);
    }

    /**
     * 批量获取用户浏览统计
     */
    @ApiOperation("批量获取用户浏览统计")
    @PostMapping("/stats/batch")
    public Result<List<ViewHistoryService.UserViewStats>> batchGetUserViewStats(@ApiParam("用户ID列表") @RequestBody List<Long> userIds) {
        log.info("批量获取用户浏览统计: userIds={}", userIds);

        List<ViewHistoryService.UserViewStats> result = viewHistoryService.batchGetUserViewStats(userIds);

        return Result.success(result);
    }

    // ==================== 数据导出 ====================

    /**
     * 导出用户浏览记录
     */
    @ApiOperation("导出用户浏览记录")
    @GetMapping("/export")
    public Result<List<ViewHistory>> exportUserViewHistory(@ApiParam("内容类型筛选") @RequestParam(required = false) String itemType,
                                                           @ApiParam("时间范围") @RequestParam(required = false) String timeRange) {
        log.info("导出用户浏览记录: itemType={}, timeRange={}", itemType, timeRange);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<ViewHistory> result = viewHistoryService.exportUserViewHistory(currentUserId, itemType, timeRange);

        return Result.success(result);
    }

    /**
     * 导出热门内容统计
     */
    @ApiOperation("导出热门内容统计")
    @GetMapping("/export/hot")
    public Result<List<ViewHistoryService.HotItemStats>> exportHotItems(@ApiParam("内容类型") @RequestParam String itemType,
                                                                        @ApiParam("时间范围") @RequestParam(required = false) String timeRange) {
        log.info("导出热门内容统计: itemType={}, timeRange={}", itemType, timeRange);

        List<ViewHistoryService.HotItemStats> result = viewHistoryService.exportHotItems(itemType, timeRange);

        return Result.success(result);
    }

    // ==================== 业务查询 ====================

    /**
     * 获取用户浏览过的内容ID列表
     */
    @ApiOperation("获取用户浏览过的内容ID列表")
    @GetMapping("/viewed-ids")
    public Result<List<Long>> getUserViewedItemIds(@ApiParam("内容类型") @RequestParam(required = false) String itemType,
                                                   @ApiParam("限制数量") @RequestParam(defaultValue = "100") Integer limit) {
        log.info("获取用户浏览过的内容ID列表: itemType={}, limit={}", itemType, limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Long> result = viewHistoryService.getUserViewedItemIds(currentUserId, itemType, limit);

        return Result.success(result);
    }

    /**
     * 获取内容浏览者列表
     */
    @ApiOperation("获取内容浏览者列表")
    @GetMapping("/viewers")
    public Result<List<ViewHistory>> getItemViewers(@ApiParam("内容类型") @RequestParam String itemType,
                                                    @ApiParam("内容ID") @RequestParam Long itemId,
                                                    @ApiParam("限制数量") @RequestParam(defaultValue = "20") Integer limit) {
        log.info("获取内容浏览者列表: itemType={}, itemId={}, limit={}", itemType, itemId, limit);

        List<ViewHistory> result = viewHistoryService.getItemViewers(itemType, itemId, limit);

        return Result.success(result);
    }

    /**
     * 获取用户浏览偏好分析
     */
    @ApiOperation("获取用户浏览偏好分析")
    @GetMapping("/preference")
    public Result<ViewHistoryService.ViewPreferenceAnalysis> getUserViewPreference() {
        log.info("获取用户浏览偏好分析");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        ViewHistoryService.ViewPreferenceAnalysis result = viewHistoryService.getUserViewPreference(currentUserId);

        return Result.success(result);
    }

    // ==================== 数据维护（管理员接口） ====================

    /**
     * 清理过期浏览记录
     */
    @ApiOperation("清理过期浏览记录")
    @PostMapping("/cleanup")
    public Result<Integer> cleanupExpiredViewHistory(@ApiParam("天数") @RequestParam(defaultValue = "90") Integer days) {
        log.info("清理过期浏览记录: days={}", days);

        // 这里应该添加管理员权限检查
        // if (!SecurityUtils.isAdmin()) {
        //     return Result.error(403, "无管理员权限");
        // }

        int result = viewHistoryService.cleanupExpiredViewHistory(days);

        return Result.success(result);
    }

    /**
     * 获取需要清理的记录数量
     */
    @ApiOperation("获取需要清理的记录数量")
    @GetMapping("/cleanup/count")
    public Result<Long> countExpiredViewHistory(@ApiParam("天数") @RequestParam(defaultValue = "90") Integer days) {
        log.info("获取需要清理的记录数量: days={}", days);

        // 这里应该添加管理员权限检查
        // if (!SecurityUtils.isAdmin()) {
        //     return Result.error(403, "无管理员权限");
        // }

        Long result = viewHistoryService.countExpiredViewHistory(days);

        return Result.success(result);
    }

    /**
     * 更新浏览记录统计
     */
    @ApiOperation("更新浏览记录统计")
    @PostMapping("/statistics/update")
    public Result<Void> updateViewStatistics() {
        log.info("更新浏览记录统计");

        // 这里应该添加管理员权限检查
        // if (!SecurityUtils.isAdmin()) {
        //     return Result.error(403, "无管理员权限");
        // }

        viewHistoryService.updateViewStatistics();

        return Result.success();
    }
}
