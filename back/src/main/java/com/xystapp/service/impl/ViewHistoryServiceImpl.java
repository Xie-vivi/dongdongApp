package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ViewHistory;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.PostMapper;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.mapper.UserMapper;
import com.xystapp.mapper.ViewHistoryMapper;
import com.xystapp.service.ViewHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 浏览记录服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class ViewHistoryServiceImpl implements ViewHistoryService {

    private static final Logger log = LoggerFactory.getLogger(ViewHistoryServiceImpl.class);

    @Autowired
    private ViewHistoryMapper viewHistoryMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private UserMapper userMapper;

    // ==================== 浏览记录管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void trackView(Long userId, String itemType, Long itemId) {
        log.info("记录浏览行为: userId={}, itemType={}, itemId={}", userId, itemType, itemId);

        // 验证参数
        if (userId == null || itemType == null || itemId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 验证内容是否存在
        if (!itemExists(itemType, itemId)) {
            log.warn("内容不存在，跳过记录: itemType={}, itemId={}", itemType, itemId);
            return;
        }

        // 检查是否已存在记录，如果存在则更新时间，否则插入新记录
        ViewHistory existingRecord = viewHistoryMapper.selectByUserAndItem(userId, itemType, itemId);
        if (existingRecord != null) {
            // 更新浏览时间（避免重复记录）
            existingRecord.setCreatedAt(LocalDateTime.now());
            viewHistoryMapper.updateById(existingRecord);
            log.debug("更新浏览记录: userId={}, itemType={}, itemId={}", userId, itemType, itemId);
        } else {
            // 插入新记录
            ViewHistory viewHistory = new ViewHistory();
            viewHistory.setUserId(userId);
            viewHistory.setItemType(itemType);
            viewHistory.setItemId(itemId);
            viewHistory.setCreatedAt(LocalDateTime.now());

            int result = viewHistoryMapper.insert(viewHistory);
            if (result <= 0) {
                throw new BusinessException(500, "记录浏览失败");
            }
            log.debug("创建浏览记录: userId={}, itemType={}, itemId={}", userId, itemType, itemId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchTrackViews(Long userId, List<ViewTrackRequest> requests) {
        log.info("批量记录浏览行为: userId={}, requestCount={}", userId, requests.size());

        if (userId == null || requests == null || requests.isEmpty()) {
            return;
        }

        for (ViewTrackRequest request : requests) {
            try {
                trackView(userId, request.getItemType(), request.getItemId());
            } catch (Exception e) {
                log.warn("批量记录浏览失败: userId={}, itemType={}, itemId={}, error={}", 
                        userId, request.getItemType(), request.getItemId(), e.getMessage());
            }
        }

        log.info("批量记录浏览完成: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteViewHistory(Long userId, Long historyId) {
        log.info("删除浏览记录: userId={}, historyId={}", userId, historyId);

        // 验证参数
        if (userId == null || historyId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!canDeleteViewHistory(userId, historyId)) {
            throw new BusinessException(403, "无权删除该浏览记录");
        }

        // 删除记录
        int result = viewHistoryMapper.deleteById(historyId);
        if (result <= 0) {
            throw new BusinessException(500, "删除浏览记录失败");
        }

        log.info("删除浏览记录成功: userId={}, historyId={}", userId, historyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteViewHistory(Long userId, List<Long> historyIds) {
        log.info("批量删除浏览记录: userId={}, historyIds={}", userId, historyIds);

        if (userId == null || historyIds == null || historyIds.isEmpty()) {
            return;
        }

        List<Long> validIds = new ArrayList<>();
        for (Long historyId : historyIds) {
            if (canDeleteViewHistory(userId, historyId)) {
                validIds.add(historyId);
            }
        }

        if (!validIds.isEmpty()) {
            viewHistoryMapper.deleteBatchIds(validIds);
        }

        log.info("批量删除浏览记录完成: userId={}, 删除数量={}", userId, validIds.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearUserViewHistory(Long userId) {
        log.info("清空用户浏览记录: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        viewHistoryMapper.deleteByUserId(userId);

        log.info("清空用户浏览记录成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteViewHistoryByType(Long userId, String itemType) {
        log.info("删除指定类型的浏览记录: userId={}, itemType={}", userId, itemType);

        if (userId == null || itemType == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        viewHistoryMapper.deleteByUserIdAndType(userId, itemType);

        log.info("删除指定类型的浏览记录成功: userId={}, itemType={}", userId, itemType);
    }

    // ==================== 浏览记录查询 ====================

    @Override
    public IPage<ViewHistory> getUserViewHistory(Long userId, Integer page, Integer size, String itemType, String sortBy) {
        log.info("获取用户浏览记录: userId={}, page={}, size={}, itemType={}, sortBy={}", userId, page, size, itemType, sortBy);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ViewHistory> pageParam = new Page<>(page, size);
        IPage<ViewHistory> result = viewHistoryMapper.selectUserViewHistory(pageParam, userId, itemType, sortBy);

        log.info("获取用户浏览记录成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public ViewHistory getViewHistoryDetail(Long historyId) {
        log.info("获取浏览记录详情: historyId={}", historyId);

        if (historyId == null) {
            return null;
        }

        ViewHistory viewHistory = viewHistoryMapper.selectViewHistoryDetail(historyId);

        log.info("获取浏览记录详情成功: historyId={}", historyId);
        return viewHistory;
    }

    @Override
    public IPage<ViewHistory> getUserViewHistoryByType(Long userId, String itemType, Integer page, Integer size, String sortBy) {
        log.info("获取用户指定类型的浏览记录: userId={}, itemType={}, page={}, size={}, sortBy={}", userId, itemType, page, size, sortBy);

        if (userId == null || itemType == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ViewHistory> pageParam = new Page<>(page, size);
        IPage<ViewHistory> result = viewHistoryMapper.selectUserViewHistoryByType(pageParam, userId, itemType, sortBy);

        log.info("获取用户指定类型的浏览记录成功: userId={}, itemType={}, 总数={}", userId, itemType, result.getTotal());
        return result;
    }

    @Override
    public List<ViewHistory> getRecentViewHistory(Long userId, Integer limit) {
        log.info("获取用户最近浏览记录: userId={}, limit={}", userId, limit);

        if (userId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 10;
        }

        List<ViewHistory> result = viewHistoryMapper.selectRecentViewHistory(userId, limit);

        log.info("获取用户最近浏览记录成功: userId={}, 数量={}", userId, result.size());
        return result;
    }

    @Override
    public IPage<ViewHistory> getUserViewHistoryByTimeRange(Long userId, String timeRange, Integer page, Integer size) {
        log.info("获取用户指定时间范围的浏览记录: userId={}, timeRange={}, page={}, size={}", userId, timeRange, page, size);

        if (userId == null || timeRange == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ViewHistory> pageParam = new Page<>(page, size);
        IPage<ViewHistory> result = viewHistoryMapper.selectUserViewHistoryByTimeRange(pageParam, userId, timeRange);

        log.info("获取用户指定时间范围的浏览记录成功: userId={}, timeRange={}, 总数={}", userId, timeRange, result.getTotal());
        return result;
    }

    // ==================== 权限验证 ====================

    @Override
    public boolean canDeleteViewHistory(Long userId, Long historyId) {
        if (userId == null || historyId == null) {
            return false;
        }

        ViewHistory viewHistory = viewHistoryMapper.selectById(historyId);
        return viewHistory != null && viewHistory.getUserId().equals(userId);
    }

    @Override
    public boolean viewHistoryExists(Long historyId) {
        if (historyId == null) {
            return false;
        }

        return viewHistoryMapper.selectById(historyId) != null;
    }

    @Override
    public boolean hasUserViewedItem(Long userId, String itemType, Long itemId) {
        if (userId == null || itemType == null || itemId == null) {
            return false;
        }

        return viewHistoryMapper.selectByUserAndItem(userId, itemType, itemId) != null;
    }

    // ==================== 统计信息 ====================

    @Override
    public UserViewStats getUserViewStats(Long userId) {
        log.info("获取用户浏览统计: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Long totalViews = (long) viewHistoryMapper.countUserViews(userId, null);
        Long postViews = (long) viewHistoryMapper.countUserViews(userId, ViewHistory.ItemType.POST.getCode());
        Long activityViews = (long) viewHistoryMapper.countUserViews(userId, ViewHistory.ItemType.ACTIVITY.getCode());
        Long todayViews = (long) viewHistoryMapper.countUserViewsByTimeRange(userId, "today");
        Long thisWeekViews = (long) viewHistoryMapper.countUserViewsByTimeRange(userId, "week");
        Long thisMonthViews = (long) viewHistoryMapper.countUserViewsByTimeRange(userId, "month");
        Integer uniqueItemsViewed = viewHistoryMapper.countUserUniqueItems(userId);

        UserViewStats stats = new UserViewStats(totalViews, postViews, activityViews,
                                               todayViews, thisWeekViews, thisMonthViews, uniqueItemsViewed);

        log.info("获取用户浏览统计成功: userId={}, total={}", userId, totalViews);
        return stats;
    }

    @Override
    public ItemViewStats getItemViewStats(String itemType, Long itemId) {
        log.info("获取内容浏览统计: itemType={}, itemId={}", itemType, itemId);

        if (itemType == null || itemId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        String itemTitle = getItemTitle(itemType, itemId);
        Long totalViews = (long) viewHistoryMapper.countItemViews(itemType, itemId);
        Long todayViews = (long) viewHistoryMapper.countItemViewsByTimeRange(itemType, itemId, "today");
        Long thisWeekViews = (long) viewHistoryMapper.countItemViewsByTimeRange(itemType, itemId, "week");
        Long thisMonthViews = (long) viewHistoryMapper.countItemViewsByTimeRange(itemType, itemId, "month");
        Integer uniqueViewers = viewHistoryMapper.countItemUniqueViewers(itemType, itemId);

        ItemViewStats stats = new ItemViewStats(itemType, itemId, itemTitle,
                                              totalViews, todayViews, thisWeekViews, thisMonthViews, uniqueViewers);

        log.info("获取内容浏览统计成功: itemType={}, itemId={}, total={}", itemType, itemId, totalViews);
        return stats;
    }

    @Override
    public List<HotItemStats> getHotItems(String itemType, Integer limit) {
        log.info("获取热门内容统计: itemType={}, limit={}", itemType, limit);

        if (itemType == null) {
            throw new BusinessException(400, "内容类型不能为空");
        }

        if (limit == null || limit < 1) {
            limit = 20;
        }

        List<HotItemStats> result = viewHistoryMapper.selectHotItems(itemType, limit);

        log.info("获取热门内容统计成功: itemType={}, 数量={}", itemType, result.size());
        return result;
    }

    @Override
    public List<ViewTrendStats> getUserViewTrend(Long userId, String timeRange) {
        log.info("获取用户浏览趋势: userId={}, timeRange={}", userId, timeRange);

        if (userId == null || timeRange == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        List<ViewTrendStats> result = viewHistoryMapper.selectUserViewTrend(userId, timeRange);

        log.info("获取用户浏览趋势成功: userId={}, timeRange={}, 数量={}", userId, timeRange, result.size());
        return result;
    }

    @Override
    public List<ViewTrendStats> getItemViewTrend(String itemType, Long itemId, String timeRange) {
        log.info("获取内容浏览趋势: itemType={}, itemId={}, timeRange={}", itemType, itemId, timeRange);

        if (itemType == null || itemId == null || timeRange == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        List<ViewTrendStats> result = viewHistoryMapper.selectItemViewTrend(itemType, itemId, timeRange);

        log.info("获取内容浏览趋势成功: itemType={}, itemId={}, timeRange={}, 数量={}", itemType, itemId, timeRange, result.size());
        return result;
    }

    // ==================== 推荐相关 ====================

    @Override
    public List<RecommendItem> getRecommendItems(Long userId, String itemType, Integer limit) {
        log.info("获取推荐内容: userId={}, itemType={}, limit={}", userId, itemType, limit);

        if (userId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 10;
        }

        List<RecommendItem> result = viewHistoryMapper.selectRecommendItems(userId, itemType, limit);

        log.info("获取推荐内容成功: userId={}, itemType={}, 数量={}", userId, itemType, result.size());
        return result;
    }

    @Override
    public List<RecommendItem> getRelatedItems(String itemType, Long itemId, Integer limit) {
        log.info("获取相关内容: itemType={}, itemId={}, limit={}", itemType, itemId, limit);

        if (itemType == null || itemId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 10;
        }

        List<RecommendItem> result = viewHistoryMapper.selectRelatedItems(itemType, itemId, limit);

        log.info("获取相关内容成功: itemType={}, itemId={}, 数量={}", itemType, itemId, result.size());
        return result;
    }

    @Override
    public List<RecommendItem> getUserInterestItems(Long userId, Integer limit) {
        log.info("获取用户可能感兴趣的内容: userId={}, limit={}", userId, limit);

        if (userId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 20;
        }

        List<RecommendItem> result = viewHistoryMapper.selectUserInterestItems(userId, limit);

        log.info("获取用户可能感兴趣的内容成功: userId={}, 数量={}", userId, result.size());
        return result;
    }

    // ==================== 数据维护 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupExpiredViewHistory(Integer days) {
        log.info("清理过期浏览记录: days={}", days);

        if (days == null || days < 1) {
            days = 90; // 默认清理90天前的记录
        }

        int result = viewHistoryMapper.deleteExpiredViewHistory(days);

        log.info("清理过期浏览记录完成: days={}, 删除数量={}", days, result);
        return result;
    }

    @Override
    public Long countExpiredViewHistory(Integer days) {
        if (days == null || days < 1) {
            days = 90;
        }

        return viewHistoryMapper.countExpiredViewHistory(days);
    }

    @Override
    public void updateViewStatistics() {
        log.info("更新浏览记录统计");

        // 这里可以实现统计数据的异步更新
        // 例如：更新热门内容缓存、用户兴趣分析等

        log.info("更新浏览记录统计完成");
    }

    // ==================== 批量操作 ====================

    @Override
    public List<ViewHistory> getViewHistoriesByIds(List<Long> historyIds) {
        if (historyIds == null || historyIds.isEmpty()) {
            return new ArrayList<>();
        }

        return viewHistoryMapper.selectViewHistoriesByIds(historyIds);
    }

    @Override
    public List<ViewStatusInfo> batchCheckViewStatus(Long userId, List<ViewTrackRequest> requests) {
        if (userId == null || requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        return viewHistoryMapper.selectBatchViewStatus(userId, requests);
    }

    @Override
    public List<UserViewStats> batchGetUserViewStats(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }

        return viewHistoryMapper.selectBatchUserViewStats(userIds);
    }

    // ==================== 数据导出 ====================

    @Override
    public List<ViewHistory> exportUserViewHistory(Long userId, String itemType, String timeRange) {
        if (userId == null) {
            return new ArrayList<>();
        }

        return viewHistoryMapper.selectExportUserViewHistory(userId, itemType, timeRange);
    }

    @Override
    public List<HotItemStats> exportHotItems(String itemType, String timeRange) {
        if (itemType == null) {
            return new ArrayList<>();
        }

        return viewHistoryMapper.selectExportHotItems(itemType, timeRange);
    }

    // ==================== 业务查询 ====================

    @Override
    public List<Long> getUserViewedItemIds(Long userId, String itemType, Integer limit) {
        if (userId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 100;
        }

        return viewHistoryMapper.selectUserViewedItemIds(userId, itemType, limit);
    }

    @Override
    public List<ViewHistory> getItemViewers(String itemType, Long itemId, Integer limit) {
        if (itemType == null || itemId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 20;
        }

        return viewHistoryMapper.selectItemViewers(itemType, itemId, limit);
    }

    @Override
    public ViewPreferenceAnalysis getUserViewPreference(Long userId) {
        if (userId == null) {
            return null;
        }

        return viewHistoryMapper.selectUserViewPreference(userId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 检查内容是否存在
     */
    private boolean itemExists(String itemType, Long itemId) {
        switch (ViewHistory.ItemType.fromCode(itemType)) {
            case POST:
                return postMapper.selectById(itemId) != null;
            case ACTIVITY:
                return activityMapper.selectById(itemId) != null;
            default:
                return false;
        }
    }

    @Override
    public List<Long> getUserViewedAuthors(Long userId, Integer limit) {
        // TODO: 实现获取用户浏览过的作者列表逻辑
        // 暂时返回空列表，等待Mapper方法实现
        return new java.util.ArrayList<>();
    }

    /**
     * 获取内容标题
     */
    private String getItemTitle(String itemType, Long itemId) {
        switch (ViewHistory.ItemType.fromCode(itemType)) {
            case POST:
                com.xystapp.entity.Post post = postMapper.selectById(itemId);
                return post != null ? post.getTitle() : "未知帖子";
            case ACTIVITY:
                com.xystapp.entity.Activity activity = activityMapper.selectById(itemId);
                return activity != null ? activity.getTitle() : "未知活动";
            default:
                return "未知内容";
        }
    }
}
