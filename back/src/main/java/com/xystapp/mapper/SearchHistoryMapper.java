package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.SearchHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 搜索历史Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {

    /**
     * 获取用户搜索历史列表（包含用户信息）
     */
    IPage<SearchHistory> selectUserSearchHistory(Page<SearchHistory> page, @Param("userId") Long userId);

    /**
     * 获取热门搜索关键词
     */
    List<HotKeywordStats> selectHotKeywords(@Param("searchType") String searchType, @Param("days") Integer days, @Param("limit") Integer limit);

    /**
     * 获取用户搜索关键词
     */
    List<String> selectUserKeywords(@Param("userId") Long userId, @Param("searchType") String searchType, @Param("limit") Integer limit);

    /**
     * 获取搜索建议（基于历史搜索）
     */
    List<String> selectSearchSuggestions(@Param("keyword") String keyword, @Param("searchType") String searchType, @Param("limit") Integer limit);

    /**
     * 获取相关搜索词
     */
    List<String> selectRelatedKeywords(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 统计关键词搜索次数
     */
    Long countKeywordSearches(@Param("keyword") String keyword, @Param("searchType") String searchType, @Param("days") Integer days);

    /**
     * 获取搜索统计
     */
    SearchStats selectSearchStats(@Param("userId") Long userId);

    /**
     * 获取搜索趋势统计
     */
    List<SearchTrendStats> selectSearchTrend(@Param("days") Integer days);

    /**
     * 获取用户搜索统计
     */
    List<UserSearchStats> selectUserSearchStats(@Param("days") Integer days, @Param("limit") Integer limit);

    /**
     * 清除用户搜索历史
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 获取用户最近搜索
     */
    List<SearchHistory> selectRecentSearches(@Param("userId") Long userId, @Param("searchType") String searchType, @Param("limit") Integer limit);

    /**
     * 批量删除搜索历史
     */
    int deleteByIds(@Param("historyIds") List<Long> historyIds, @Param("userId") Long userId);

    /**
     * 清理过期搜索历史
     */
    int cleanupExpiredHistory(@Param("days") Integer days);

    // ==================== 统计类定义 ====================

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
     * 搜索统计类
     */
    class SearchStats {
        private Long totalSearches;
        private Long todaySearches;
        private Long uniqueKeywords;
        private Long avgResultCount;

        public SearchStats(Long totalSearches, Long todaySearches, Long uniqueKeywords, Long avgResultCount) {
            this.totalSearches = totalSearches;
            this.todaySearches = todaySearches;
            this.uniqueKeywords = uniqueKeywords;
            this.avgResultCount = avgResultCount;
        }

        public Long getTotalSearches() { return totalSearches; }
        public Long getTodaySearches() { return todaySearches; }
        public Long getUniqueKeywords() { return uniqueKeywords; }
        public Long getAvgResultCount() { return avgResultCount; }
    }

    /**
     * 搜索趋势统计类
     */
    class SearchTrendStats {
        private String date;
        private Long searchCount;
        private Long uniqueUsers;

        public SearchTrendStats(String date, Long searchCount, Long uniqueUsers) {
            this.date = date;
            this.searchCount = searchCount;
            this.uniqueUsers = uniqueUsers;
        }

        public String getDate() { return date; }
        public Long getSearchCount() { return searchCount; }
        public Long getUniqueUsers() { return uniqueUsers; }
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

        public UserSearchStats(Long userId, String username, String nickname, Long searchCount, String lastSearchTime) {
            this.userId = userId;
            this.username = username;
            this.nickname = nickname;
            this.searchCount = searchCount;
            this.lastSearchTime = lastSearchTime;
        }

        public Long getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getNickname() { return nickname; }
        public Long getSearchCount() { return searchCount; }
        public String getLastSearchTime() { return lastSearchTime; }
    }
}
