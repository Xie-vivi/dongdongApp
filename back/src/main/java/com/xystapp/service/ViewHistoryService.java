package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ViewHistory;

import java.util.List;

/**
 * 浏览记录服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface ViewHistoryService {

    // ==================== 浏览记录管理 ====================

    /**
     * 记录浏览行为
     */
    void trackView(Long userId, String itemType, Long itemId);

    /**
     * 批量记录浏览行为
     */
    void batchTrackViews(Long userId, List<ViewTrackRequest> requests);

    /**
     * 删除浏览记录
     */
    void deleteViewHistory(Long userId, Long historyId);

    /**
     * 批量删除浏览记录
     */
    void batchDeleteViewHistory(Long userId, List<Long> historyIds);

    /**
     * 清空用户浏览记录
     */
    void clearUserViewHistory(Long userId);

    /**
     * 删除指定类型的浏览记录
     */
    void deleteViewHistoryByType(Long userId, String itemType);

    // ==================== 浏览记录查询 ====================

    /**
     * 获取用户浏览记录列表
     */
    IPage<ViewHistory> getUserViewHistory(Long userId, Integer page, Integer size, String itemType, String sortBy);

    /**
     * 获取用户浏览记录详情
     */
    ViewHistory getViewHistoryDetail(Long historyId);

    /**
     * 获取用户指定类型的浏览记录
     */
    IPage<ViewHistory> getUserViewHistoryByType(Long userId, String itemType, Integer page, Integer size, String sortBy);

    /**
     * 获取用户最近浏览记录
     */
    List<ViewHistory> getRecentViewHistory(Long userId, Integer limit);

    /**
     * 获取用户指定时间范围的浏览记录
     */
    IPage<ViewHistory> getUserViewHistoryByTimeRange(Long userId, String timeRange, Integer page, Integer size);

    // ==================== 权限验证 ====================

    /**
     * 检查用户是否可以删除浏览记录
     */
    boolean canDeleteViewHistory(Long userId, Long historyId);

    /**
     * 检查浏览记录是否存在
     */
    boolean viewHistoryExists(Long historyId);

    /**
     * 检查用户是否浏览过指定内容
     */
    boolean hasUserViewedItem(Long userId, String itemType, Long itemId);

    // ==================== 统计信息 ====================

    /**
     * 获取用户浏览统计
     */
    UserViewStats getUserViewStats(Long userId);

    /**
     * 获取内容浏览统计
     */
    ItemViewStats getItemViewStats(String itemType, Long itemId);

    /**
     * 获取热门内容统计
     */
    List<HotItemStats> getHotItems(String itemType, Integer limit);

    /**
     * 获取用户浏览趋势
     */
    List<ViewTrendStats> getUserViewTrend(Long userId, String timeRange);

    /**
     * 获取内容浏览趋势
     */
    List<ViewTrendStats> getItemViewTrend(String itemType, Long itemId, String timeRange);

    // ==================== 推荐相关 ====================

    /**
     * 获取推荐内容（基于浏览历史）
     */
    List<RecommendItem> getRecommendItems(Long userId, String itemType, Integer limit);

    /**
     * 获取相关内容（基于当前浏览内容）
     */
    List<RecommendItem> getRelatedItems(String itemType, Long itemId, Integer limit);

    /**
     * 获取用户可能感兴趣的内容
     */
    List<RecommendItem> getUserInterestItems(Long userId, Integer limit);

    // ==================== 数据维护 ====================

    /**
     * 清理过期浏览记录
     */
    int cleanupExpiredViewHistory(Integer days);

    /**
     * 获取需要清理的记录数量
     */
    Long countExpiredViewHistory(Integer days);

    /**
     * 更新浏览记录统计
     */
    void updateViewStatistics();

    // ==================== 批量操作 ====================

    /**
     * 批量获取浏览记录
     */
    List<ViewHistory> getViewHistoriesByIds(List<Long> historyIds);

    /**
     * 批量检查浏览状态
     */
    List<ViewStatusInfo> batchCheckViewStatus(Long userId, List<ViewTrackRequest> requests);

    /**
     * 批量获取用户浏览统计
     */
    List<UserViewStats> batchGetUserViewStats(List<Long> userIds);

    // ==================== 数据导出 ====================

    /**
     * 导出用户浏览记录
     */
    List<ViewHistory> exportUserViewHistory(Long userId, String itemType, String timeRange);

    /**
     * 导出热门内容统计
     */
    List<HotItemStats> exportHotItems(String itemType, String timeRange);

    // ==================== 业务查询 ====================

    /**
     * 获取用户浏览过的内容ID列表
     */
    List<Long> getUserViewedItemIds(Long userId, String itemType, Integer limit);

    /**
     * 获取内容浏览者列表
     */
    List<ViewHistory> getItemViewers(String itemType, Long itemId, Integer limit);

    /**
     * 获取用户浏览过的作者列表
     */
    List<Long> getUserViewedAuthors(Long userId, Integer limit);

    /**
     * 获取用户浏览偏好分析
     */
    ViewPreferenceAnalysis getUserViewPreference(Long userId);

    // ==================== 内部类定义 ====================

    /**
     * 浏览跟踪请求类
     */
    public static class ViewTrackRequest {
        private String itemType;
        private Long itemId;

        public ViewTrackRequest(String itemType, Long itemId) {
            this.itemType = itemType;
            this.itemId = itemId;
        }

        public String getItemType() { return itemType; }
        public Long getItemId() { return itemId; }
    }

    /**
     * 用户浏览统计类
     */
    public static class UserViewStats {
        private Long totalViews;
        private Long postViews;
        private Long activityViews;
        private Long todayViews;
        private Long thisWeekViews;
        private Long thisMonthViews;
        private Integer uniqueItemsViewed;

        public UserViewStats(Long totalViews, Long postViews, Long activityViews,
                           Long todayViews, Long thisWeekViews, Long thisMonthViews, Integer uniqueItemsViewed) {
            this.totalViews = totalViews;
            this.postViews = postViews;
            this.activityViews = activityViews;
            this.todayViews = todayViews;
            this.thisWeekViews = thisWeekViews;
            this.thisMonthViews = thisMonthViews;
            this.uniqueItemsViewed = uniqueItemsViewed;
        }

        public Long getTotalViews() { return totalViews; }
        public Long getPostViews() { return postViews; }
        public Long getActivityViews() { return activityViews; }
        public Long getTodayViews() { return todayViews; }
        public Long getThisWeekViews() { return thisWeekViews; }
        public Long getThisMonthViews() { return thisMonthViews; }
        public Integer getUniqueItemsViewed() { return uniqueItemsViewed; }
    }

    /**
     * 内容浏览统计类
     */
    public static class ItemViewStats {
        private String itemType;
        private Long itemId;
        private String itemTitle;
        private Long totalViews;
        private Long todayViews;
        private Long thisWeekViews;
        private Long thisMonthViews;
        private Integer uniqueViewers;

        public ItemViewStats(String itemType, Long itemId, String itemTitle,
                           Long totalViews, Long todayViews, Long thisWeekViews, Long thisMonthViews, Integer uniqueViewers) {
            this.itemType = itemType;
            this.itemId = itemId;
            this.itemTitle = itemTitle;
            this.totalViews = totalViews;
            this.todayViews = todayViews;
            this.thisWeekViews = thisWeekViews;
            this.thisMonthViews = thisMonthViews;
            this.uniqueViewers = uniqueViewers;
        }

        public String getItemType() { return itemType; }
        public Long getItemId() { return itemId; }
        public String getItemTitle() { return itemTitle; }
        public Long getTotalViews() { return totalViews; }
        public Long getTodayViews() { return todayViews; }
        public Long getThisWeekViews() { return thisWeekViews; }
        public Long getThisMonthViews() { return thisMonthViews; }
        public Integer getUniqueViewers() { return uniqueViewers; }
    }

    /**
     * 热门内容统计类
     */
    public static class HotItemStats {
        private String itemType;
        private Long itemId;
        private String itemTitle;
        private String itemDescription;
        private String itemImage;
        private Long viewCount;
        private Integer uniqueViewers;
        private java.time.LocalDateTime lastViewTime;

        public HotItemStats(String itemType, Long itemId, String itemTitle, String itemDescription,
                          String itemImage, Long viewCount, Integer uniqueViewers, java.time.LocalDateTime lastViewTime) {
            this.itemType = itemType;
            this.itemId = itemId;
            this.itemTitle = itemTitle;
            this.itemDescription = itemDescription;
            this.itemImage = itemImage;
            this.viewCount = viewCount;
            this.uniqueViewers = uniqueViewers;
            this.lastViewTime = lastViewTime;
        }

        public String getItemType() { return itemType; }
        public Long getItemId() { return itemId; }
        public String getItemTitle() { return itemTitle; }
        public String getItemDescription() { return itemDescription; }
        public String getItemImage() { return itemImage; }
        public Long getViewCount() { return viewCount; }
        public Integer getUniqueViewers() { return uniqueViewers; }
        public java.time.LocalDateTime getLastViewTime() { return lastViewTime; }
    }

    /**
     * 浏览趋势统计类
     */
    public static class ViewTrendStats {
        private String date;
        private Long viewCount;
        private Integer uniqueUsers;

        public ViewTrendStats(String date, Long viewCount, Integer uniqueUsers) {
            this.date = date;
            this.viewCount = viewCount;
            this.uniqueUsers = uniqueUsers;
        }

        public String getDate() { return date; }
        public Long getViewCount() { return viewCount; }
        public Integer getUniqueUsers() { return uniqueUsers; }
    }

    /**
     * 推荐内容类
     */
    public static class RecommendItem {
        private String itemType;
        private Long itemId;
        private String itemTitle;
        private String itemDescription;
        private String itemImage;
        private Double similarityScore;

        public RecommendItem(String itemType, Long itemId, String itemTitle, String itemDescription,
                           String itemImage, Double similarityScore) {
            this.itemType = itemType;
            this.itemId = itemId;
            this.itemTitle = itemTitle;
            this.itemDescription = itemDescription;
            this.itemImage = itemImage;
            this.similarityScore = similarityScore;
        }

        public String getItemType() { return itemType; }
        public Long getItemId() { return itemId; }
        public String getItemTitle() { return itemTitle; }
        public String getItemDescription() { return itemDescription; }
        public String getItemImage() { return itemImage; }
        public Double getSimilarityScore() { return similarityScore; }
    }

    /**
     * 浏览状态信息类
     */
    public static class ViewStatusInfo {
        private String itemType;
        private Long itemId;
        private boolean hasViewed;
        private java.time.LocalDateTime lastViewTime;

        public ViewStatusInfo(String itemType, Long itemId, boolean hasViewed, java.time.LocalDateTime lastViewTime) {
            this.itemType = itemType;
            this.itemId = itemId;
            this.hasViewed = hasViewed;
            this.lastViewTime = lastViewTime;
        }

        public String getItemType() { return itemType; }
        public Long getItemId() { return itemId; }
        public boolean isHasViewed() { return hasViewed; }
        public java.time.LocalDateTime getLastViewTime() { return lastViewTime; }
    }

    /**
     * 浏览偏好分析类
     */
    public static class ViewPreferenceAnalysis {
        private String preferredItemType;
        private Double preferenceScore;
        private List<String> interestedTags;
        private List<Long> frequentlyViewedAuthors;

        public ViewPreferenceAnalysis(String preferredItemType, Double preferenceScore,
                                    List<String> interestedTags, List<Long> frequentlyViewedAuthors) {
            this.preferredItemType = preferredItemType;
            this.preferenceScore = preferenceScore;
            this.interestedTags = interestedTags;
            this.frequentlyViewedAuthors = frequentlyViewedAuthors;
        }

        public String getPreferredItemType() { return preferredItemType; }
        public Double getPreferenceScore() { return preferenceScore; }
        public List<String> getInterestedTags() { return interestedTags; }
        public List<Long> getFrequentlyViewedAuthors() { return frequentlyViewedAuthors; }
    }
}
