package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface SearchService {

    // ==================== 综合搜索 ====================

    /**
     * 综合搜索（搜索所有类型的内容）
     */
    SearchResult searchAll(String keyword, Integer page, Integer size, Long userId);

    /**
     * 搜索帖子
     */
    SearchResult searchPosts(String keyword, Integer page, Integer size, Long userId);

    /**
     * 搜索活动
     */
    SearchResult searchActivities(String keyword, Integer page, Integer size, Long userId);

    /**
     * 搜索用户
     */
    SearchResult searchUsers(String keyword, Integer page, Integer size, Long userId);

    /**
     * 搜索场地
     */
    SearchResult searchFields(String keyword, Integer page, Integer size, Long userId);

    // ==================== 高级搜索 ====================

    /**
     * 高级搜索（带过滤条件）
     */
    SearchResult advancedSearch(SearchRequest searchRequest, Long userId);

    /**
     * 按标签搜索帖子
     */
    SearchResult searchPostsByTags(List<String> tags, Integer page, Integer size, Long userId);

    /**
     * 按地点搜索活动
     */
    SearchResult searchActivitiesByLocation(String location, Integer page, Integer size, Long userId);

    /**
     * 按学校搜索用户
     */
    SearchResult searchUsersBySchool(String school, Integer page, Integer size, Long userId);

    /**
     * 按类型搜索场地
     */
    SearchResult searchFieldsByType(String fieldType, Integer page, Integer size, Long userId);

    // ==================== 搜索建议 ====================

    /**
     * 获取搜索建议（自动补全）
     */
    List<String> getSearchSuggestions(String keyword, String searchType, Integer limit);

    /**
     * 获取热门搜索词
     */
    List<String> getHotSearchKeywords(String searchType, Integer limit);

    /**
     * 获取用户搜索历史
     */
    List<String> getUserSearchHistory(Long userId, String searchType, Integer limit);

    /**
     * 获取相关搜索词
     */
    List<String> getRelatedKeywords(String keyword, Integer limit);

    // ==================== 搜索历史管理 ====================

    /**
     * 记录搜索历史
     */
    void recordSearchHistory(Long userId, String keyword, String searchType, Integer resultCount);

    /**
     * 清除用户搜索历史
     */
    boolean clearUserSearchHistory(Long userId);

    /**
     * 删除特定搜索历史
     */
    boolean deleteSearchHistory(Long historyId, Long userId);

    /**
     * 获取用户搜索历史列表
     */
    IPage<SearchHistory> getUserSearchHistoryList(Long userId, Integer page, Integer size);

    // ==================== 搜索统计 ====================

    /**
     * 获取搜索统计
     */
    SearchStats getSearchStats(Long userId);

    /**
     * 获取热门搜索统计
     */
    List<HotKeywordStats> getHotKeywordStats(String searchType, Integer days, Integer limit);

    /**
     * 获取搜索趋势统计
     */
    List<SearchTrendStats> getSearchTrendStats(Integer days);

    /**
     * 获取用户搜索统计
     */
    List<UserSearchStats> getUserSearchStats(Integer days, Integer limit);

    // ==================== 搜索索引管理 ====================

    /**
     * 重建搜索索引
     */
    boolean rebuildSearchIndex(String indexType);

    /**
     * 更新搜索索引
     */
    boolean updateSearchIndex(String indexType, Long itemId);

    /**
     * 删除搜索索引
     */
    boolean deleteSearchIndex(String indexType, Long itemId);

    /**
     * 获取索引状态
     */
    Map<String, IndexStatus> getIndexStatus();

    // ==================== 搜索配置 ====================

    /**
     * 获取搜索配置
     */
    SearchConfig getSearchConfig();

    /**
     * 更新搜索配置
     */
    boolean updateSearchConfig(SearchConfig searchConfig);

    /**
     * 重置搜索配置
     */
    boolean resetSearchConfig();

    // ==================== 业务查询 ====================

    /**
     * 获取推荐内容（基于用户搜索历史）
     */
    SearchResult getRecommendedContent(Long userId, Integer limit);

    /**
     * 获取相似内容
     */
    SearchResult getSimilarContent(String contentType, Long contentId, Integer limit);

    /**
     * 获取最新内容
     */
    SearchResult getLatestContent(String contentType, Integer page, Integer size, Long userId);

    /**
     * 获取热门内容
     */
    SearchResult getPopularContent(String contentType, Integer page, Integer size, Long userId);

    // ==================== 结果类定义 ====================

    /**
     * 搜索结果类
     */
    class SearchResult {
        private List<Post> posts;
        private List<Activity> activities;
        private List<User> users;
        private List<Field> fields;
        private Long totalCount;
        private Map<String, Long> typeCounts;
        private Long searchTime; // 搜索耗时（毫秒）
        private String keyword;
        private String searchType;

        public SearchResult(List<Post> posts, List<Activity> activities, List<User> users, List<Field> fields,
                           Long totalCount, Map<String, Long> typeCounts, Long searchTime, String keyword, String searchType) {
            this.posts = posts;
            this.activities = activities;
            this.users = users;
            this.fields = fields;
            this.totalCount = totalCount;
            this.typeCounts = typeCounts;
            this.searchTime = searchTime;
            this.keyword = keyword;
            this.searchType = searchType;
        }

        // Getters
        public List<Post> getPosts() { return posts; }
        public List<Activity> getActivities() { return activities; }
        public List<User> getUsers() { return users; }
        public List<Field> getFields() { return fields; }
        public Long getTotalCount() { return totalCount; }
        public Map<String, Long> getTypeCounts() { return typeCounts; }
        public Long getSearchTime() { return searchTime; }
        public String getKeyword() { return keyword; }
        public String getSearchType() { return searchType; }
    }

    /**
     * 搜索请求类
     */
    class SearchRequest {
        private String keyword;
        private String searchType;
        private List<String> tags;
        private String location;
        private String school;
        private String fieldType;
        private String sortBy; // relevance, time, popularity
        private String sortOrder; // asc, desc
        private Integer page;
        private Integer size;
        private Map<String, Object> filters;

        // Getters and Setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public String getSearchType() { return searchType; }
        public void setSearchType(String searchType) { this.searchType = searchType; }
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getSchool() { return school; }
        public void setSchool(String school) { this.school = school; }
        public String getFieldType() { return fieldType; }
        public void setFieldType(String fieldType) { this.fieldType = fieldType; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
        public Map<String, Object> getFilters() { return filters; }
        public void setFilters(Map<String, Object> filters) { this.filters = filters; }
    }

    /**
     * 搜索统计类
     */
    class SearchStats {
        private Long totalSearches;
        private Long todaySearches;
        private Long uniqueKeywords;
        private Long avgResultCount;
        private Map<String, Long> typeStats;

        public SearchStats(Long totalSearches, Long todaySearches, Long uniqueKeywords, 
                          Long avgResultCount, Map<String, Long> typeStats) {
            this.totalSearches = totalSearches;
            this.todaySearches = todaySearches;
            this.uniqueKeywords = uniqueKeywords;
            this.avgResultCount = avgResultCount;
            this.typeStats = typeStats;
        }

        public Long getTotalSearches() { return totalSearches; }
        public Long getTodaySearches() { return todaySearches; }
        public Long getUniqueKeywords() { return uniqueKeywords; }
        public Long getAvgResultCount() { return avgResultCount; }
        public Map<String, Long> getTypeStats() { return typeStats; }
    }

    /**
     * 热门关键词统计类
     */
    class HotKeywordStats {
        private String keyword;
        private Long searchCount;
        private Long avgResultCount;
        private String lastSearchTime;

        public HotKeywordStats(String keyword, Long searchCount, Long avgResultCount, String lastSearchTime) {
            this.keyword = keyword;
            this.searchCount = searchCount;
            this.avgResultCount = avgResultCount;
            this.lastSearchTime = lastSearchTime;
        }

        public String getKeyword() { return keyword; }
        public Long getSearchCount() { return searchCount; }
        public Long getAvgResultCount() { return avgResultCount; }
        public String getLastSearchTime() { return lastSearchTime; }
    }

    /**
     * 搜索趋势统计类
     */
    class SearchTrendStats {
        private String date;
        private Long searchCount;
        private Long uniqueUsers;
        private Map<String, Long> typeDistribution;

        public SearchTrendStats(String date, Long searchCount, Long uniqueUsers, Map<String, Long> typeDistribution) {
            this.date = date;
            this.searchCount = searchCount;
            this.uniqueUsers = uniqueUsers;
            this.typeDistribution = typeDistribution;
        }

        public String getDate() { return date; }
        public Long getSearchCount() { return searchCount; }
        public Long getUniqueUsers() { return uniqueUsers; }
        public Map<String, Long> getTypeDistribution() { return typeDistribution; }
    }

    /**
     * 用户搜索统计类
     */
    class UserSearchStats {
        private Long userId;
        private String username;
        private String nickname;
        private Long searchCount;
        private String lastSearchTime;
        private List<String> topKeywords;

        public UserSearchStats(Long userId, String username, String nickname, Long searchCount, 
                              String lastSearchTime, List<String> topKeywords) {
            this.userId = userId;
            this.username = username;
            this.nickname = nickname;
            this.searchCount = searchCount;
            this.lastSearchTime = lastSearchTime;
            this.topKeywords = topKeywords;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public Long getSearchCount() { return searchCount; }
        public String getLastSearchTime() { return lastSearchTime; }
        public List<String> getTopKeywords() { return topKeywords; }
    }

    /**
     * 索引状态类
     */
    class IndexStatus {
        private String indexType;
        private Long totalDocuments;
        private Long indexedDocuments;
        private String lastUpdateTime;
        private Boolean isHealthy;
        private String status;

        public IndexStatus(String indexType, Long totalDocuments, Long indexedDocuments, 
                          String lastUpdateTime, Boolean isHealthy, String status) {
            this.indexType = indexType;
            this.totalDocuments = totalDocuments;
            this.indexedDocuments = indexedDocuments;
            this.lastUpdateTime = lastUpdateTime;
            this.isHealthy = isHealthy;
            this.status = status;
        }

        public String getIndexType() { return indexType; }
        public Long getTotalDocuments() { return totalDocuments; }
        public Long getIndexedDocuments() { return indexedDocuments; }
        public String getLastUpdateTime() { return lastUpdateTime; }
        public Boolean getIsHealthy() { return isHealthy; }
        public String getStatus() { return status; }
    }

    /**
     * 搜索配置类
     */
    class SearchConfig {
        private Boolean enableFullTextSearch;
        private Integer maxResultsPerPage;
        private Integer maxSearchLength;
        private List<String> stopWords;
        private Map<String, Float> fieldWeights;
        private Boolean enableSearchHistory;
        private Integer historyRetentionDays;

        public SearchConfig(Boolean enableFullTextSearch, Integer maxResultsPerPage, Integer maxSearchLength,
                           List<String> stopWords, Map<String, Float> fieldWeights, Boolean enableSearchHistory,
                           Integer historyRetentionDays) {
            this.enableFullTextSearch = enableFullTextSearch;
            this.maxResultsPerPage = maxResultsPerPage;
            this.maxSearchLength = maxSearchLength;
            this.stopWords = stopWords;
            this.fieldWeights = fieldWeights;
            this.enableSearchHistory = enableSearchHistory;
            this.historyRetentionDays = historyRetentionDays;
        }

        public Boolean getEnableFullTextSearch() { return enableFullTextSearch; }
        public Integer getMaxResultsPerPage() { return maxResultsPerPage; }
        public Integer getMaxSearchLength() { return maxSearchLength; }
        public List<String> getStopWords() { return stopWords; }
        public Map<String, Float> getFieldWeights() { return fieldWeights; }
        public Boolean getEnableSearchHistory() { return enableSearchHistory; }
        public Integer getHistoryRetentionDays() { return historyRetentionDays; }
    }
}
