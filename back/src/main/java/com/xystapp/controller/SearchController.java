package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.entity.SearchHistory;
import com.xystapp.service.SearchService;
import com.xystapp.common.Result;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "搜索管理")
@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    // ==================== 综合搜索 ====================

    @ApiOperation("综合搜索")
    @GetMapping("/all")
    public Result<SearchService.SearchResult> searchAll(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchAll(keyword, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("综合搜索失败: keyword={}", keyword, e);
            return Result.error("搜索失败：" + e.getMessage());
        }
    }

    @ApiOperation("搜索帖子")
    @GetMapping("/posts")
    public Result<SearchService.SearchResult> searchPosts(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchPosts(keyword, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("帖子搜索失败: keyword={}", keyword, e);
            return Result.error("帖子搜索失败：" + e.getMessage());
        }
    }

    @ApiOperation("搜索活动")
    @GetMapping("/activities")
    public Result<SearchService.SearchResult> searchActivities(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchActivities(keyword, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("活动搜索失败: keyword={}", keyword, e);
            return Result.error("活动搜索失败：" + e.getMessage());
        }
    }

    @ApiOperation("搜索用户")
    @GetMapping("/users")
    public Result<SearchService.SearchResult> searchUsers(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchUsers(keyword, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("用户搜索失败: keyword={}", keyword, e);
            return Result.error("用户搜索失败：" + e.getMessage());
        }
    }

    @ApiOperation("搜索场地")
    @GetMapping("/fields")
    public Result<SearchService.SearchResult> searchFields(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchFields(keyword, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("场地搜索失败: keyword={}", keyword, e);
            return Result.error("场地搜索失败：" + e.getMessage());
        }
    }

    // ==================== 高级搜索 ====================

    @ApiOperation("高级搜索")
    @PostMapping("/advanced")
    public Result<SearchService.SearchResult> advancedSearch(
            @ApiParam("搜索请求") @RequestBody SearchService.SearchRequest searchRequest) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.advancedSearch(searchRequest, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("高级搜索失败: request={}", searchRequest, e);
            return Result.error("高级搜索失败：" + e.getMessage());
        }
    }

    @ApiOperation("按标签搜索帖子")
    @GetMapping("/posts/tags")
    public Result<SearchService.SearchResult> searchPostsByTags(
            @ApiParam("标签列表") @RequestParam List<String> tags,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchPostsByTags(tags, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("按标签搜索帖子失败: tags={}", tags, e);
            return Result.error("按标签搜索帖子失败：" + e.getMessage());
        }
    }

    @ApiOperation("按地点搜索活动")
    @GetMapping("/activities/location")
    public Result<SearchService.SearchResult> searchActivitiesByLocation(
            @ApiParam("地点") @RequestParam String location,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchActivitiesByLocation(location, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("按地点搜索活动失败: location={}", location, e);
            return Result.error("按地点搜索活动失败：" + e.getMessage());
        }
    }

    @ApiOperation("按学校搜索用户")
    @GetMapping("/users/school")
    public Result<SearchService.SearchResult> searchUsersBySchool(
            @ApiParam("学校") @RequestParam String school,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchUsersBySchool(school, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("按学校搜索用户失败: school={}", school, e);
            return Result.error("按学校搜索用户失败：" + e.getMessage());
        }
    }

    @ApiOperation("按类型搜索场地")
    @GetMapping("/fields/type")
    public Result<SearchService.SearchResult> searchFieldsByType(
            @ApiParam("场地类型") @RequestParam String fieldType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.searchFieldsByType(fieldType, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("按类型搜索场地失败: fieldType={}", fieldType, e);
            return Result.error("按类型搜索场地失败：" + e.getMessage());
        }
    }

    // ==================== 搜索建议 ====================

    @ApiOperation("获取搜索建议")
    @GetMapping("/suggestions")
    public Result<List<String>> getSearchSuggestions(
            @ApiParam("关键词") @RequestParam String keyword,
            @ApiParam("搜索类型") @RequestParam(defaultValue = "all") String searchType,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<String> suggestions = searchService.getSearchSuggestions(keyword, searchType, limit);
            return Result.success(suggestions);
        } catch (Exception e) {
            log.error("获取搜索建议失败: keyword={}, type={}", keyword, searchType, e);
            return Result.error("获取搜索建议失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取热门搜索关键词")
    @GetMapping("/hot-keywords")
    public Result<List<String>> getHotSearchKeywords(
            @ApiParam("搜索类型") @RequestParam(defaultValue = "all") String searchType,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<String> keywords = searchService.getHotSearchKeywords(searchType, limit);
            return Result.success(keywords);
        } catch (Exception e) {
            log.error("获取热门搜索关键词失败: type={}", searchType, e);
            return Result.error("获取热门搜索关键词失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取用户搜索历史")
    @GetMapping("/history/user")
    public Result<List<String>> getUserSearchHistory(
            @ApiParam("搜索类型") @RequestParam(defaultValue = "all") String searchType,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            List<String> history = searchService.getUserSearchHistory(userId, searchType, limit);
            return Result.success(history);
        } catch (Exception e) {
            log.error("获取用户搜索历史失败: type={}", searchType, e);
            return Result.error("获取用户搜索历史失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取相关搜索词")
    @GetMapping("/related")
    public Result<List<String>> getRelatedKeywords(
            @ApiParam("关键词") @RequestParam String keyword,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<String> keywords = searchService.getRelatedKeywords(keyword, limit);
            return Result.success(keywords);
        } catch (Exception e) {
            log.error("获取相关搜索词失败: keyword={}", keyword, e);
            return Result.error("获取相关搜索词失败：" + e.getMessage());
        }
    }

    // ==================== 搜索历史管理 ====================

    @ApiOperation("清除用户搜索历史")
    @DeleteMapping("/history")
    public Result<Boolean> clearUserSearchHistory() {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            boolean result = searchService.clearUserSearchHistory(userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("清除用户搜索历史失败: userId={}", SecurityUtils.getCurrentUserId(), e);
            return Result.error("清除搜索历史失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除特定搜索历史")
    @DeleteMapping("/history/{historyId}")
    public Result<Boolean> deleteSearchHistory(
            @ApiParam("历史记录ID") @PathVariable Long historyId) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            boolean result = searchService.deleteSearchHistory(historyId, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除搜索历史失败: historyId={}, userId={}", historyId, SecurityUtils.getCurrentUserId(), e);
            return Result.error("删除搜索历史失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取用户搜索历史列表")
    @GetMapping("/history/list")
    public Result<IPage<SearchHistory>> getUserSearchHistoryList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            IPage<SearchHistory> result = searchService.getUserSearchHistoryList(userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取用户搜索历史列表失败: userId={}", SecurityUtils.getCurrentUserId(), e);
            return Result.error("获取搜索历史列表失败：" + e.getMessage());
        }
    }

    // ==================== 搜索统计 ====================

    @ApiOperation("获取搜索统计")
    @GetMapping("/stats")
    public Result<SearchService.SearchStats> getSearchStats() {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchStats stats = searchService.getSearchStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取搜索统计失败: userId={}", SecurityUtils.getCurrentUserId(), e);
            return Result.error("获取搜索统计失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取热门关键词统计")
    @GetMapping("/stats/hot-keywords")
    public Result<List<SearchService.HotKeywordStats>> getHotKeywordStats(
            @ApiParam("搜索类型") @RequestParam(defaultValue = "all") String searchType,
            @ApiParam("统计天数") @RequestParam(defaultValue = "7") Integer days,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<SearchService.HotKeywordStats> stats = searchService.getHotKeywordStats(searchType, days, limit);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取热门关键词统计失败: type={}, days={}", searchType, days, e);
            return Result.error("获取热门关键词统计失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取搜索趋势统计")
    @GetMapping("/stats/trend")
    public Result<List<SearchService.SearchTrendStats>> getSearchTrendStats(
            @ApiParam("统计天数") @RequestParam(defaultValue = "7") Integer days) {
        
        try {
            List<SearchService.SearchTrendStats> stats = searchService.getSearchTrendStats(days);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取搜索趋势统计失败: days={}", days, e);
            return Result.error("获取搜索趋势统计失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取用户搜索统计")
    @GetMapping("/stats/users")
    public Result<List<SearchService.UserSearchStats>> getUserSearchStats(
            @ApiParam("统计天数") @RequestParam(defaultValue = "7") Integer days,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            List<SearchService.UserSearchStats> stats = searchService.getUserSearchStats(days, limit);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取用户搜索统计失败: days={}", days, e);
            return Result.error("获取用户搜索统计失败：" + e.getMessage());
        }
    }

    // ==================== 搜索索引管理 ====================

    @ApiOperation("重建搜索索引")
    @PostMapping("/index/rebuild")
    public Result<Boolean> rebuildSearchIndex(
            @ApiParam("索引类型") @RequestParam String indexType) {
        
        try {
            boolean result = searchService.rebuildSearchIndex(indexType);
            return Result.success(result);
        } catch (Exception e) {
            log.error("重建搜索索引失败: indexType={}", indexType, e);
            return Result.error("重建搜索索引失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新搜索索引")
    @PostMapping("/index/update")
    public Result<Boolean> updateSearchIndex(
            @ApiParam("索引类型") @RequestParam String indexType,
            @ApiParam("项目ID") @RequestParam Long itemId) {
        
        try {
            boolean result = searchService.updateSearchIndex(indexType, itemId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新搜索索引失败: indexType={}, itemId={}", indexType, itemId, e);
            return Result.error("更新搜索索引失败：" + e.getMessage());
        }
    }

    @ApiOperation("删除搜索索引")
    @DeleteMapping("/index")
    public Result<Boolean> deleteSearchIndex(
            @ApiParam("索引类型") @RequestParam String indexType,
            @ApiParam("项目ID") @RequestParam Long itemId) {
        
        try {
            boolean result = searchService.deleteSearchIndex(indexType, itemId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("删除搜索索引失败: indexType={}, itemId={}", indexType, itemId, e);
            return Result.error("删除搜索索引失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取索引状态")
    @GetMapping("/index/status")
    public Result<Map<String, SearchService.IndexStatus>> getIndexStatus() {
        
        try {
            Map<String, SearchService.IndexStatus> status = searchService.getIndexStatus();
            return Result.success(status);
        } catch (Exception e) {
            log.error("获取索引状态失败", e);
            return Result.error("获取索引状态失败：" + e.getMessage());
        }
    }

    // ==================== 搜索配置 ====================

    @ApiOperation("获取搜索配置")
    @GetMapping("/config")
    public Result<SearchService.SearchConfig> getSearchConfig() {
        
        try {
            SearchService.SearchConfig config = searchService.getSearchConfig();
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取搜索配置失败", e);
            return Result.error("获取搜索配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("更新搜索配置")
    @PutMapping("/config")
    public Result<Boolean> updateSearchConfig(
            @ApiParam("搜索配置") @RequestBody SearchService.SearchConfig searchConfig) {
        
        try {
            boolean result = searchService.updateSearchConfig(searchConfig);
            return Result.success(result);
        } catch (Exception e) {
            log.error("更新搜索配置失败: config={}", searchConfig, e);
            return Result.error("更新搜索配置失败：" + e.getMessage());
        }
    }

    @ApiOperation("重置搜索配置")
    @PostMapping("/config/reset")
    public Result<Boolean> resetSearchConfig() {
        
        try {
            boolean result = searchService.resetSearchConfig();
            return Result.success(result);
        } catch (Exception e) {
            log.error("重置搜索配置失败", e);
            return Result.error("重置搜索配置失败：" + e.getMessage());
        }
    }

    // ==================== 业务查询 ====================

    @ApiOperation("获取推荐内容")
    @GetMapping("/recommend")
    public Result<SearchService.SearchResult> getRecommendedContent(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        Long userId = null;
        try {
            userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.getRecommendedContent(userId, limit);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取推荐内容失败: userId={}", userId, e);
            return Result.error("获取推荐内容失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取相似内容")
    @GetMapping("/similar/{contentType}/{contentId}")
    public Result<SearchService.SearchResult> getSimilarContent(
            @ApiParam("内容类型") @PathVariable String contentType,
            @ApiParam("内容ID") @PathVariable Long contentId,
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        try {
            SearchService.SearchResult result = searchService.getSimilarContent(contentType, contentId, limit);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取相似内容失败: type={}, id={}", contentType, contentId, e);
            return Result.error("获取相似内容失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取最新内容")
    @GetMapping("/latest/{contentType}")
    public Result<SearchService.SearchResult> getLatestContent(
            @ApiParam("内容类型") @PathVariable String contentType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.getLatestContent(contentType, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取最新内容失败: type={}", contentType, e);
            return Result.error("获取最新内容失败：" + e.getMessage());
        }
    }

    @ApiOperation("获取热门内容")
    @GetMapping("/popular/{contentType}")
    public Result<SearchService.SearchResult> getPopularContent(
            @ApiParam("内容类型") @PathVariable String contentType,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        try {
            Long userId = SecurityUtils.getCurrentUserId();
            SearchService.SearchResult result = searchService.getPopularContent(contentType, page, size, userId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取热门内容失败: type={}", contentType, e);
            return Result.error("获取热门内容失败：" + e.getMessage());
        }
    }
}
