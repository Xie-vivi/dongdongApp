package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.*;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.*;
import com.xystapp.service.SearchService;
import com.xystapp.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class SearchServiceImpl implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FieldMapper fieldMapper;

    @Autowired
    private SearchHistoryMapper searchHistoryMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // Redis缓存键前缀
    private static final String CACHE_PREFIX = "search:";
    private static final String HOT_KEYWORDS_KEY = "search:hot_keywords";
    private static final String SEARCH_SUGGESTIONS_KEY = "search:suggestions:";
    private static final int DEFAULT_CACHE_TTL = 300; // 5分钟

    // ==================== 综合搜索 ====================

    @Override
    public SearchResult searchAll(String keyword, Integer page, Integer size, Long userId) {
        log.info("执行综合搜索: keyword={}, page={}, size={}, userId={}", keyword, page, size, userId);

        long startTime = System.currentTimeMillis();

        // 参数验证
        validateSearchParameters(keyword, page, size);

        // 检查缓存
        String cacheKey = buildCacheKey("all", keyword, page, size);
        SearchResult cachedResult = getCachedResult(cacheKey);
        if (cachedResult != null) {
            log.info("从缓存获取搜索结果: keyword={}", keyword);
            return cachedResult;
        }

        try {
            // 并行搜索各种类型的内容
            List<Post> posts = searchPostsInternal(keyword, page, size);
            List<Activity> activities = searchActivitiesInternal(keyword, page, size);
            List<User> users = searchUsersInternal(keyword, page, size);
            List<Field> fields = searchFieldsInternal(keyword, page, size);

            // 计算总结果数
            long totalCount = posts.size() + activities.size() + users.size() + fields.size();

            // 统计各类型结果数
            Map<String, Long> typeCounts = new HashMap<>();
            typeCounts.put("post", (long) posts.size());
            typeCounts.put("activity", (long) activities.size());
            typeCounts.put("user", (long) users.size());
            typeCounts.put("field", (long) fields.size());

            // 创建搜索结果
            SearchResult result = new SearchResult(posts, activities, users, fields, 
                    totalCount, typeCounts, System.currentTimeMillis() - startTime, keyword, "all");

            // 缓存结果
            cacheResult(cacheKey, result);

            // 记录搜索历史
            recordSearchHistory(userId, keyword, "all", (int) totalCount);

            log.info("综合搜索完成: keyword={}, totalCount={}, time={}ms", 
                    keyword, totalCount, result.getSearchTime());

            return result;

        } catch (Exception e) {
            log.error("综合搜索失败: keyword={}", keyword, e);
            throw new BusinessException(500, "搜索失败：" + e.getMessage());
        }
    }

    @Override
    public SearchResult searchPosts(String keyword, Integer page, Integer size, Long userId) {
        log.info("搜索帖子: keyword={}, page={}, size={}, userId={}", keyword, page, size, userId);

        long startTime = System.currentTimeMillis();
        validateSearchParameters(keyword, page, size);

        // 检查缓存
        String cacheKey = buildCacheKey("post", keyword, page, size);
        SearchResult cachedResult = getCachedResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        try {
            List<Post> posts = searchPostsInternal(keyword, page, size);
            
            SearchResult result = new SearchResult(posts, Collections.emptyList(), 
                    Collections.emptyList(), Collections.emptyList(), 
                    (long) posts.size(), 
                    Collections.singletonMap("post", (long) posts.size()),
                    System.currentTimeMillis() - startTime, keyword, "post");

            // 缓存结果
            cacheResult(cacheKey, result);

            // 记录搜索历史
            recordSearchHistory(userId, keyword, "post", posts.size());

            return result;

        } catch (Exception e) {
            log.error("帖子搜索失败: keyword={}", keyword, e);
            throw new BusinessException(500, "帖子搜索失败：" + e.getMessage());
        }
    }

    @Override
    public SearchResult searchActivities(String keyword, Integer page, Integer size, Long userId) {
        log.info("搜索活动: keyword={}, page={}, size={}, userId={}", keyword, page, size, userId);

        long startTime = System.currentTimeMillis();
        validateSearchParameters(keyword, page, size);

        String cacheKey = buildCacheKey("activity", keyword, page, size);
        SearchResult cachedResult = getCachedResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        try {
            List<Activity> activities = searchActivitiesInternal(keyword, page, size);
            
            SearchResult result = new SearchResult(Collections.emptyList(), activities, 
                    Collections.emptyList(), Collections.emptyList(), 
                    (long) activities.size(), 
                    Collections.singletonMap("activity", (long) activities.size()),
                    System.currentTimeMillis() - startTime, keyword, "activity");

            cacheResult(cacheKey, result);
            recordSearchHistory(userId, keyword, "activity", activities.size());

            return result;

        } catch (Exception e) {
            log.error("活动搜索失败: keyword={}", keyword, e);
            throw new BusinessException(500, "活动搜索失败：" + e.getMessage());
        }
    }

    @Override
    public SearchResult searchUsers(String keyword, Integer page, Integer size, Long userId) {
        log.info("搜索用户: keyword={}, page={}, size={}, userId={}", keyword, page, size, userId);

        long startTime = System.currentTimeMillis();
        validateSearchParameters(keyword, page, size);

        String cacheKey = buildCacheKey("user", keyword, page, size);
        SearchResult cachedResult = getCachedResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        try {
            List<User> users = searchUsersInternal(keyword, page, size);
            
            SearchResult result = new SearchResult(Collections.emptyList(), Collections.emptyList(), 
                    users, Collections.emptyList(), 
                    (long) users.size(), 
                    Collections.singletonMap("user", (long) users.size()),
                    System.currentTimeMillis() - startTime, keyword, "user");

            cacheResult(cacheKey, result);
            recordSearchHistory(userId, keyword, "user", users.size());

            return result;

        } catch (Exception e) {
            log.error("用户搜索失败: keyword={}", keyword, e);
            throw new BusinessException(500, "用户搜索失败：" + e.getMessage());
        }
    }

    @Override
    public SearchResult searchFields(String keyword, Integer page, Integer size, Long userId) {
        log.info("搜索场地: keyword={}, page={}, size={}, userId={}", keyword, page, size, userId);

        long startTime = System.currentTimeMillis();
        validateSearchParameters(keyword, page, size);

        String cacheKey = buildCacheKey("field", keyword, page, size);
        SearchResult cachedResult = getCachedResult(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        try {
            List<Field> fields = searchFieldsInternal(keyword, page, size);
            
            SearchResult result = new SearchResult(Collections.emptyList(), Collections.emptyList(), 
                    Collections.emptyList(), fields, 
                    (long) fields.size(), 
                    Collections.singletonMap("field", (long) fields.size()),
                    System.currentTimeMillis() - startTime, keyword, "field");

            cacheResult(cacheKey, result);
            recordSearchHistory(userId, keyword, "field", fields.size());

            return result;

        } catch (Exception e) {
            log.error("场地搜索失败: keyword={}", keyword, e);
            throw new BusinessException(500, "场地搜索失败：" + e.getMessage());
        }
    }

    // ==================== 高级搜索 ====================

    @Override
    public SearchResult advancedSearch(SearchRequest searchRequest, Long userId) {
        log.info("执行高级搜索: keyword={}, type={}, filters={}", 
                searchRequest.getKeyword(), searchRequest.getSearchType(), searchRequest.getFilters());

        // TODO: 实现高级搜索逻辑
        throw new BusinessException(501, "高级搜索功能暂未实现");
    }

    @Override
    public SearchResult searchPostsByTags(List<String> tags, Integer page, Integer size, Long userId) {
        log.info("按标签搜索帖子: tags={}, page={}, size={}", tags, page, size);

        if (tags == null || tags.isEmpty()) {
            throw new BusinessException(400, "标签列表不能为空");
        }

        try {
            // 使用MyBatis Plus查询包含指定标签的帖子
            QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
            for (String tag : tags) {
                queryWrapper.like("tags", tag);
            }
            queryWrapper.eq("status", "published")
                       .orderByDesc("created_at");

            Page<Post> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
            IPage<Post> postPage = postMapper.selectPage(pageParam, queryWrapper);

            SearchResult result = new SearchResult(postPage.getRecords(), Collections.emptyList(), 
                    Collections.emptyList(), Collections.emptyList(), 
                    postPage.getTotal(), 
                    Collections.singletonMap("post", postPage.getTotal()),
                    0L, String.join(",", tags), "post");

            return result;

        } catch (Exception e) {
            log.error("按标签搜索帖子失败: tags={}", tags, e);
            throw new BusinessException(500, "按标签搜索帖子失败：" + e.getMessage());
        }
    }

    @Override
    public SearchResult searchActivitiesByLocation(String location, Integer page, Integer size, Long userId) {
        log.info("按地点搜索活动: location={}, page={}, size={}", location, page, size);

        if (location == null || location.trim().isEmpty()) {
            throw new BusinessException(400, "地点不能为空");
        }

        try {
            List<Activity> activities = activityMapper.selectActivitiesByLocation(location, page, size);
            
            SearchResult result = new SearchResult(Collections.emptyList(), activities, 
                    Collections.emptyList(), Collections.emptyList(), 
                    (long) activities.size(), 
                    Collections.singletonMap("activity", (long) activities.size()),
                    0L, location, "activity");

            return result;

        } catch (Exception e) {
            log.error("按地点搜索活动失败: location={}", location, e);
            throw new BusinessException(500, "按地点搜索活动失败：" + e.getMessage());
        }
    }

    @Override
    public SearchResult searchUsersBySchool(String school, Integer page, Integer size, Long userId) {
        log.info("按学校搜索用户: school={}, page={}, size={}", school, page, size);

        if (school == null || school.trim().isEmpty()) {
            throw new BusinessException(400, "学校不能为空");
        }

        try {
            List<User> users = userMapper.selectUsersBySchool(school, page, size);
            
            SearchResult result = new SearchResult(Collections.emptyList(), Collections.emptyList(), 
                    users, Collections.emptyList(), 
                    (long) users.size(), 
                    Collections.singletonMap("user", (long) users.size()),
                    0L, school, "user");

            return result;

        } catch (Exception e) {
            log.error("按学校搜索用户失败: school={}", school, e);
            throw new BusinessException(500, "按学校搜索用户失败：" + e.getMessage());
        }
    }

    @Override
    public SearchResult searchFieldsByType(String fieldType, Integer page, Integer size, Long userId) {
        log.info("按类型搜索场地: fieldType={}, page={}, size={}", fieldType, page, size);

        if (fieldType == null || fieldType.trim().isEmpty()) {
            throw new BusinessException(400, "场地类型不能为空");
        }

        try {
            List<Field> fields = fieldMapper.selectFieldsByType(fieldType, page, size);
            
            SearchResult result = new SearchResult(Collections.emptyList(), Collections.emptyList(), 
                    Collections.emptyList(), fields, 
                    (long) fields.size(), 
                    Collections.singletonMap("field", (long) fields.size()),
                    0L, fieldType, "field");

            return result;

        } catch (Exception e) {
            log.error("按类型搜索场地失败: fieldType={}", fieldType, e);
            throw new BusinessException(500, "按类型搜索场地失败：" + e.getMessage());
        }
    }

    // ==================== 搜索建议 ====================

    @Override
    public List<String> getSearchSuggestions(String keyword, String searchType, Integer limit) {
        log.info("获取搜索建议: keyword={}, type={}, limit={}", keyword, searchType, limit);

        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 检查缓存
        String cacheKey = SEARCH_SUGGESTIONS_KEY + searchType + ":" + keyword;
        List<String> cachedSuggestions = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedSuggestions != null) {
            return cachedSuggestions;
        }

        try {
            List<String> suggestions = searchHistoryMapper.selectSearchSuggestions(
                    keyword, searchType, limit != null ? limit : 10);

            // 缓存建议结果（较短的TTL）
            redisTemplate.opsForValue().set(cacheKey, suggestions, 60, TimeUnit.SECONDS);

            return suggestions;

        } catch (Exception e) {
            log.error("获取搜索建议失败: keyword={}", keyword, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getHotSearchKeywords(String searchType, Integer limit) {
        log.info("获取热门搜索关键词: type={}, limit={}", searchType, limit);

        // 检查缓存
        String cacheKey = HOT_KEYWORDS_KEY + ":" + searchType;
        List<String> cachedKeywords = (List<String>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedKeywords != null) {
            return cachedKeywords;
        }

        try {
            List<SearchHistoryMapper.HotKeywordStats> hotStats = searchHistoryMapper.selectHotKeywords(
                    searchType, 7, limit != null ? limit : 10);

            List<String> keywords = hotStats.stream()
                    .map(SearchHistoryMapper.HotKeywordStats::getKeyword)
                    .collect(Collectors.toList());

            // 缓存热门关键词（较长的TTL）
            redisTemplate.opsForValue().set(cacheKey, keywords, 1800, TimeUnit.SECONDS); // 30分钟

            return keywords;

        } catch (Exception e) {
            log.error("获取热门搜索关键词失败: type={}", searchType, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getUserSearchHistory(Long userId, String searchType, Integer limit) {
        log.info("获取用户搜索历史: userId={}, type={}, limit={}", userId, searchType, limit);

        try {
            return searchHistoryMapper.selectUserKeywords(userId, searchType, limit);
        } catch (Exception e) {
            log.error("获取用户搜索历史失败: userId={}", userId, e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getRelatedKeywords(String keyword, Integer limit) {
        log.info("获取相关搜索词: keyword={}, limit={}", keyword, limit);

        try {
            return searchHistoryMapper.selectRelatedKeywords(keyword, limit != null ? limit : 10);
        } catch (Exception e) {
            log.error("获取相关搜索词失败: keyword={}", keyword, e);
            return Collections.emptyList();
        }
    }

    // ==================== 搜索历史管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordSearchHistory(Long userId, String keyword, String searchType, Integer resultCount) {
        try {
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setUserId(userId);
            searchHistory.setKeyword(keyword);
            searchHistory.setSearchType(searchType);
            searchHistory.setResultCount(resultCount);
            searchHistory.setSearchIp(SecurityUtils.getClientIp());
            searchHistory.setUserAgent(SecurityUtils.getUserAgent());

            searchHistoryMapper.insert(searchHistory);

            log.debug("记录搜索历史: userId={}, keyword={}, type={}, count={}", 
                    userId, keyword, searchType, resultCount);

        } catch (Exception e) {
            log.error("记录搜索历史失败: userId={}, keyword={}", userId, keyword, e);
            // 搜索历史记录失败不影响主流程
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean clearUserSearchHistory(Long userId) {
        try {
            int deletedCount = searchHistoryMapper.deleteByUserId(userId);
            
            // 清除相关缓存
            clearUserSearchCache(userId);
            
            log.info("清除用户搜索历史: userId={}, count={}", userId, deletedCount);
            return deletedCount > 0;

        } catch (Exception e) {
            log.error("清除用户搜索历史失败: userId={}", userId, e);
            throw new BusinessException(500, "清除搜索历史失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSearchHistory(Long historyId, Long userId) {
        try {
            int deletedCount = searchHistoryMapper.deleteByIds(Collections.singletonList(historyId), userId);
            
            // 清除相关缓存
            clearSearchCache();
            
            log.info("删除搜索历史: historyId={}, userId={}", historyId, userId);
            return deletedCount > 0;

        } catch (Exception e) {
            log.error("删除搜索历史失败: historyId={}, userId={}", historyId, userId, e);
            throw new BusinessException(500, "删除搜索历史失败");
        }
    }

    @Override
    public IPage<SearchHistory> getUserSearchHistoryList(Long userId, Integer page, Integer size) {
        try {
            Page<SearchHistory> pageParam = new Page<>(page != null ? page : 1, size != null ? size : 10);
            return searchHistoryMapper.selectUserSearchHistory(pageParam, userId);
        } catch (Exception e) {
            log.error("获取用户搜索历史列表失败: userId={}", userId, e);
            throw new BusinessException(500, "获取搜索历史失败");
        }
    }

    // ==================== 搜索统计 ====================

    @Override
    public SearchStats getSearchStats(Long userId) {
        try {
            SearchHistoryMapper.SearchStats stats = searchHistoryMapper.selectSearchStats(userId);
            
            return new SearchStats(
                    stats.getTotalSearches(),
                    stats.getTodaySearches(),
                    stats.getUniqueKeywords(),
                    stats.getAvgResultCount(),
                    Collections.emptyMap() // TODO: 实现类型统计
            );

        } catch (Exception e) {
            log.error("获取搜索统计失败: userId={}", userId, e);
            throw new BusinessException(500, "获取搜索统计失败");
        }
    }

    @Override
    public List<HotKeywordStats> getHotKeywordStats(String searchType, Integer days, Integer limit) {
        try {
            List<SearchHistoryMapper.HotKeywordStats> stats = searchHistoryMapper.selectHotKeywords(
                    searchType, days, limit);

            return stats.stream()
                    .map(s -> new HotKeywordStats(s.getKeyword(), s.getSearchCount(), 
                            s.getAvgResultCount(), s.getLastSearchTime()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取热门关键词统计失败: type={}", searchType, e);
            throw new BusinessException(500, "获取热门关键词统计失败");
        }
    }

    @Override
    public List<SearchTrendStats> getSearchTrendStats(Integer days) {
        try {
            List<SearchHistoryMapper.SearchTrendStats> stats = searchHistoryMapper.selectSearchTrend(days);

            return stats.stream()
                    .map(s -> new SearchTrendStats(s.getDate(), s.getSearchCount(), 
                            s.getUniqueUsers(), Collections.emptyMap()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取搜索趋势统计失败: days={}", days, e);
            throw new BusinessException(500, "获取搜索趋势统计失败");
        }
    }

    @Override
    public List<UserSearchStats> getUserSearchStats(Integer days, Integer limit) {
        try {
            List<SearchHistoryMapper.UserSearchStats> stats = searchHistoryMapper.selectUserSearchStats(days, limit);

            return stats.stream()
                    .map(s -> new UserSearchStats(s.getUserId(), s.getUsername(), s.getNickname(), 
                            s.getSearchCount(), s.getLastSearchTime(), Collections.emptyList()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("获取用户搜索统计失败: days={}", days, e);
            throw new BusinessException(500, "获取用户搜索统计失败");
        }
    }

    // ==================== 搜索索引管理 ====================

    @Override
    public boolean rebuildSearchIndex(String indexType) {
        // TODO: 实现搜索索引重建
        throw new BusinessException(501, "搜索索引重建功能暂未实现");
    }

    @Override
    public boolean updateSearchIndex(String indexType, Long itemId) {
        // TODO: 实现搜索索引更新
        throw new BusinessException(501, "搜索索引更新功能暂未实现");
    }

    @Override
    public boolean deleteSearchIndex(String indexType, Long itemId) {
        // TODO: 实现搜索索引删除
        throw new BusinessException(501, "搜索索引删除功能暂未实现");
    }

    @Override
    public Map<String, IndexStatus> getIndexStatus() {
        // TODO: 实现索引状态查询
        throw new BusinessException(501, "索引状态查询功能暂未实现");
    }

    // ==================== 搜索配置 ====================

    @Override
    public SearchConfig getSearchConfig() {
        // TODO: 实现搜索配置查询
        throw new BusinessException(501, "搜索配置查询功能暂未实现");
    }

    @Override
    public boolean updateSearchConfig(SearchConfig searchConfig) {
        // TODO: 实现搜索配置更新
        throw new BusinessException(501, "搜索配置更新功能暂未实现");
    }

    @Override
    public boolean resetSearchConfig() {
        // TODO: 实现搜索配置重置
        throw new BusinessException(501, "搜索配置重置功能暂未实现");
    }

    // ==================== 业务查询 ====================

    @Override
    public SearchResult getRecommendedContent(Long userId, Integer limit) {
        // TODO: 实现推荐内容
        throw new BusinessException(501, "推荐内容功能暂未实现");
    }

    @Override
    public SearchResult getSimilarContent(String contentType, Long contentId, Integer limit) {
        // TODO: 实现相似内容
        throw new BusinessException(501, "相似内容功能暂未实现");
    }

    @Override
    public SearchResult getLatestContent(String contentType, Integer page, Integer size, Long userId) {
        // TODO: 实现最新内容
        throw new BusinessException(501, "最新内容功能暂未实现");
    }

    @Override
    public SearchResult getPopularContent(String contentType, Integer page, Integer size, Long userId) {
        // TODO: 实现热门内容
        throw new BusinessException(501, "热门内容功能暂未实现");
    }

    // ==================== 私有工具方法 ====================

    /**
     * 验证搜索参数
     */
    private void validateSearchParameters(String keyword, Integer page, Integer size) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException(400, "搜索关键词不能为空");
        }

        if (keyword.length() > 100) {
            throw new BusinessException(400, "搜索关键词长度不能超过100个字符");
        }

        if (page != null && page < 1) {
            throw new BusinessException(400, "页码必须大于0");
        }

        if (size != null && (size < 1 || size > 50)) {
            throw new BusinessException(400, "每页大小必须在1-50之间");
        }
    }

    /**
     * 内部搜索帖子方法
     */
    private List<Post> searchPostsInternal(String keyword, Integer page, Integer size) {
        // 使用FULLTEXT搜索，标题权重3，内容权重1
        return postMapper.searchPostsByKeyword(keyword, page, size);
    }

    /**
     * 内部搜索活动方法
     */
    private List<Activity> searchActivitiesInternal(String keyword, Integer page, Integer size) {
        return activityMapper.searchActivitiesByKeyword(keyword, page, size);
    }

    /**
     * 内部搜索用户方法
     */
    private List<User> searchUsersInternal(String keyword, Integer page, Integer size) {
        return userMapper.searchUsersByKeyword(keyword, page, size);
    }

    /**
     * 内部搜索场地方法
     */
    private List<Field> searchFieldsInternal(String keyword, Integer page, Integer size) {
        return fieldMapper.searchFieldsByKeyword(keyword, page, size);
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String searchType, String keyword, Integer page, Integer size) {
        return String.format("%s%s:%s:%d:%d", CACHE_PREFIX, searchType, keyword, 
                page != null ? page : 1, size != null ? size : 10);
    }

    /**
     * 获取缓存的搜索结果
     */
    private SearchResult getCachedResult(String cacheKey) {
        try {
            return (SearchResult) redisTemplate.opsForValue().get(cacheKey);
        } catch (Exception e) {
            log.warn("获取搜索缓存失败: key={}", cacheKey, e);
            return null;
        }
    }

    /**
     * 缓存搜索结果
     */
    private void cacheResult(String cacheKey, SearchResult result) {
        try {
            redisTemplate.opsForValue().set(cacheKey, result, DEFAULT_CACHE_TTL, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("缓存搜索结果失败: key={}", cacheKey, e);
        }
    }

    /**
     * 清除搜索缓存
     */
    private void clearSearchCache() {
        try {
            Set<String> keys = redisTemplate.keys(CACHE_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
        } catch (Exception e) {
            log.warn("清除搜索缓存失败", e);
        }
    }

    /**
     * 清除用户搜索相关缓存
     */
    private void clearUserSearchCache(Long userId) {
        try {
            // 清除热门搜索缓存
            redisTemplate.delete(HOT_KEYWORDS_KEY + ":all");
            redisTemplate.delete(HOT_KEYWORDS_KEY + ":post");
            redisTemplate.delete(HOT_KEYWORDS_KEY + ":activity");
            redisTemplate.delete(HOT_KEYWORDS_KEY + ":user");
            redisTemplate.delete(HOT_KEYWORDS_KEY + ":field");
        } catch (Exception e) {
            log.warn("清除用户搜索缓存失败: userId={}", userId, e);
        }
    }
}
