package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ViewHistory;
import com.xystapp.service.ViewHistoryService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 浏览记录Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ViewHistoryMapper extends BaseMapper<ViewHistory> {

    /**
     * 根据用户和内容获取浏览记录
     */
    ViewHistory selectByUserAndItem(@Param("userId") Long userId, @Param("itemType") String itemType, @Param("itemId") Long itemId);

    /**
     * 获取浏览记录详情（包含用户和内容信息）
     */
    ViewHistory selectViewHistoryDetail(@Param("historyId") Long historyId);

    /**
     * 获取用户浏览记录列表（包含内容信息）
     */
    IPage<ViewHistory> selectUserViewHistory(Page<ViewHistory> page, @Param("userId") Long userId, 
                                            @Param("itemType") String itemType, @Param("sortBy") String sortBy);

    /**
     * 获取用户指定类型的浏览记录
     */
    IPage<ViewHistory> selectUserViewHistoryByType(Page<ViewHistory> page, @Param("userId") Long userId, 
                                                   @Param("itemType") String itemType, @Param("sortBy") String sortBy);

    /**
     * 获取用户指定时间范围的浏览记录
     */
    IPage<ViewHistory> selectUserViewHistoryByTimeRange(Page<ViewHistory> page, @Param("userId") Long userId, 
                                                       @Param("timeRange") String timeRange);

    /**
     * 获取用户最近浏览记录
     */
    List<ViewHistory> selectRecentViewHistory(@Param("userId") Long userId, @Param("limit") Integer limit);

    // ==================== 统计相关 ====================

    /**
     * 统计用户浏览数量
     */
    Integer countUserViews(@Param("userId") Long userId, @Param("itemType") String itemType);

    /**
     * 统计用户指定时间范围的浏览数量
     */
    Integer countUserViewsByTimeRange(@Param("userId") Long userId, @Param("timeRange") String timeRange);

    /**
     * 统计内容浏览数量
     */
    Integer countItemViews(@Param("itemType") String itemType, @Param("itemId") Long itemId);

    /**
     * 统计内容指定时间范围的浏览数量
     */
    Integer countItemViewsByTimeRange(@Param("itemType") String itemType, @Param("itemId") Long itemId, @Param("timeRange") String timeRange);

    /**
     * 统计用户浏览的唯一内容数量
     */
    Integer countUserUniqueItems(@Param("userId") Long userId);

    /**
     * 统计内容的唯一浏览者数量
     */
    Integer countItemUniqueViewers(@Param("itemType") String itemType, @Param("itemId") Long itemId);

    // ==================== 热门内容相关 ====================

    /**
     * 获取热门内容统计
     */
    List<ViewHistoryService.HotItemStats> selectHotItems(@Param("itemType") String itemType, @Param("limit") Integer limit);

    /**
     * 获取浏览趋势统计
     */
    List<ViewHistoryService.ViewTrendStats> selectUserViewTrend(@Param("userId") Long userId, @Param("timeRange") String timeRange);

    /**
     * 获取内容浏览趋势统计
     */
    List<ViewHistoryService.ViewTrendStats> selectItemViewTrend(@Param("itemType") String itemType, @Param("itemId") Long itemId, @Param("timeRange") String timeRange);

    // ==================== 推荐相关 ====================

    /**
     * 获取推荐内容（基于浏览历史）
     */
    List<ViewHistoryService.RecommendItem> selectRecommendItems(@Param("userId") Long userId, @Param("itemType") String itemType, @Param("limit") Integer limit);

    /**
     * 获取相关内容（基于当前浏览内容）
     */
    List<ViewHistoryService.RecommendItem> selectRelatedItems(@Param("itemType") String itemType, @Param("itemId") Long itemId, @Param("limit") Integer limit);

    /**
     * 获取用户可能感兴趣的内容
     */
    List<ViewHistoryService.RecommendItem> selectUserInterestItems(@Param("userId") Long userId, @Param("limit") Integer limit);

    // ==================== 批量操作 ====================

    /**
     * 批量获取浏览记录
     */
    List<ViewHistory> selectViewHistoriesByIds(@Param("historyIds") List<Long> historyIds);

    /**
     * 批量检查浏览状态
     */
    List<ViewHistoryService.ViewStatusInfo> selectBatchViewStatus(@Param("userId") Long userId, @Param("requests") List<ViewHistoryService.ViewTrackRequest> requests);

    /**
     * 批量获取用户浏览统计
     */
    List<ViewHistoryService.UserViewStats> selectBatchUserViewStats(@Param("userIds") List<Long> userIds);

    // ==================== 数据维护 ====================

    /**
     * 删除用户的所有浏览记录
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除用户指定类型的浏览记录
     */
    int deleteByUserIdAndType(@Param("userId") Long userId, @Param("itemType") String itemType);

    /**
     * 删除过期的浏览记录
     */
    int deleteExpiredViewHistory(@Param("days") Integer days);

    /**
     * 统计过期记录数量
     */
    Long countExpiredViewHistory(@Param("days") Integer days);

    // ==================== 数据导出 ====================

    /**
     * 导出用户浏览记录
     */
    List<ViewHistory> selectExportUserViewHistory(@Param("userId") Long userId, @Param("itemType") String itemType, @Param("timeRange") String timeRange);

    /**
     * 导出热门内容统计
     */
    List<ViewHistoryService.HotItemStats> selectExportHotItems(@Param("itemType") String itemType, @Param("timeRange") String timeRange);

    // ==================== 业务查询 ====================

    /**
     * 获取用户浏览过的内容ID列表
     */
    List<Long> selectUserViewedItemIds(@Param("userId") Long userId, @Param("itemType") String itemType, @Param("limit") Integer limit);

    /**
     * 获取内容浏览者列表
     */
    List<ViewHistory> selectItemViewers(@Param("itemType") String itemType, @Param("itemId") Long itemId, @Param("limit") Integer limit);

    /**
     * 获取用户浏览偏好分析
     */
    ViewHistoryService.ViewPreferenceAnalysis selectUserViewPreference(@Param("userId") Long userId);

    // ==================== 时间范围查询 ====================

    /**
     * 获取指定时间范围内的浏览记录
     */
    List<ViewHistory> selectViewHistoryByTimeRange(@Param("startTime") java.time.LocalDateTime startTime,
                                                   @Param("endTime") java.time.LocalDateTime endTime,
                                                   @Param("itemType") String itemType);

    /**
     * 获取用户指定时间范围内的浏览记录
     */
    List<ViewHistory> selectUserViewHistoryByTimeRange(@Param("userId") Long userId,
                                                       @Param("startTime") java.time.LocalDateTime startTime,
                                                       @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 获取内容指定时间范围内的浏览记录
     */
    List<ViewHistory> selectItemViewHistoryByTimeRange(@Param("itemType") String itemType,
                                                       @Param("itemId") Long itemId,
                                                       @Param("startTime") java.time.LocalDateTime startTime,
                                                       @Param("endTime") java.time.LocalDateTime endTime);

    // ==================== 高级统计 ====================

    /**
     * 获取用户浏览活跃度分析
     */
    ViewActivityAnalysis selectUserViewActivityAnalysis(@Param("userId") Long userId, @Param("days") Integer days);

    /**
     * 获取内容热度分析
     */
    ItemHeatAnalysis selectItemHeatAnalysis(@Param("itemType") String itemType, @Param("itemId") Long itemId, @Param("days") Integer days);

    /**
     * 获取用户兴趣标签分析
     */
    List<String> selectUserInterestTags(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取用户常浏览的作者列表
     */
    List<Long> selectUserFrequentAuthors(@Param("userId") Long userId, @Param("limit") Integer limit);

    // ==================== 内部类定义 ====================

    /**
     * 浏览活跃度分析类
     */
    class ViewActivityAnalysis {
        private Long totalViews;
        private Double avgDailyViews;
        private Integer activeDays;
        private Integer maxDailyViews;
        private Integer minDailyViews;
        private String mostActiveHour;

        public ViewActivityAnalysis(Long totalViews, Double avgDailyViews, Integer activeDays,
                                  Integer maxDailyViews, Integer minDailyViews, String mostActiveHour) {
            this.totalViews = totalViews;
            this.avgDailyViews = avgDailyViews;
            this.activeDays = activeDays;
            this.maxDailyViews = maxDailyViews;
            this.minDailyViews = minDailyViews;
            this.mostActiveHour = mostActiveHour;
        }

        public Long getTotalViews() { return totalViews; }
        public Double getAvgDailyViews() { return avgDailyViews; }
        public Integer getActiveDays() { return activeDays; }
        public Integer getMaxDailyViews() { return maxDailyViews; }
        public Integer getMinDailyViews() { return minDailyViews; }
        public String getMostActiveHour() { return mostActiveHour; }
    }

    /**
     * 内容热度分析类
     */
    class ItemHeatAnalysis {
        private Long totalViews;
        private Integer uniqueViewers;
        private Double avgDailyViews;
        private Integer activeDays;
        private String mostActiveHour;
        private java.time.LocalDateTime lastViewTime;

        public ItemHeatAnalysis(Long totalViews, Integer uniqueViewers, Double avgDailyViews,
                              Integer activeDays, String mostActiveHour, java.time.LocalDateTime lastViewTime) {
            this.totalViews = totalViews;
            this.uniqueViewers = uniqueViewers;
            this.avgDailyViews = avgDailyViews;
            this.activeDays = activeDays;
            this.mostActiveHour = mostActiveHour;
            this.lastViewTime = lastViewTime;
        }

        public Long getTotalViews() { return totalViews; }
        public Integer getUniqueViewers() { return uniqueViewers; }
        public Double getAvgDailyViews() { return avgDailyViews; }
        public Integer getActiveDays() { return activeDays; }
        public String getMostActiveHour() { return mostActiveHour; }
        public java.time.LocalDateTime getLastViewTime() { return lastViewTime; }
    }
}
