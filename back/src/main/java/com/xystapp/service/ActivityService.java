package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.ActivityCreateRequest;
import com.xystapp.dto.ActivityQueryRequest;
import com.xystapp.dto.ActivityUpdateRequest;
import com.xystapp.entity.Activity;
import com.xystapp.entity.ActivitySignup;

import java.util.List;

/**
 * 活动服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface ActivityService {

    /**
     * 创建活动
     */
    Activity createActivity(ActivityCreateRequest request, Long userId);

    /**
     * 更新活动
     */
    Activity updateActivity(Long id, ActivityUpdateRequest request, Long userId);

    /**
     * 删除活动
     */
    void deleteActivity(Long id, Long userId);

    /**
     * 根据ID获取活动详情
     */
    Activity getActivityById(Long id);

    /**
     * 分页查询活动列表
     */
    IPage<Activity> getActivityList(ActivityQueryRequest query);

    /**
     * 获取用户的活动列表
     */
    IPage<Activity> getUserActivities(Long userId, String status, Integer page, Integer size);

    /**
     * 发布草稿活动
     */
    Activity publishDraft(Long id, Long userId);

    /**
     * 将活动设为草稿
     */
    Activity setAsDraft(Long id, Long userId);

    /**
     * 取消活动
     */
    Activity cancelActivity(Long id, Long userId);

    /**
     * 报名活动
     */
    void signupActivity(Long activityId, Long userId);

    /**
     * 取消报名
     */
    void cancelSignup(Long activityId, Long userId);

    /**
     * 获取用户报名的活动列表
     */
    IPage<Activity> getSignedUpActivities(Long userId, Integer page, Integer size);

    /**
     * 批量获取活动信息
     */
    List<Activity> getActivitiesByIds(List<Long> ids);

    /**
     * 更新活动统计信息
     */
    void updateActivityStats(Long activityId, Integer likesCount, Integer commentsCount, Integer participants);

    /**
     * 检查用户是否是活动组织者
     */
    boolean isActivityOwner(Long activityId, Long userId);

    /**
     * 检查用户是否已报名活动
     */
    boolean isUserSignedUp(Long activityId, Long userId);

    /**
     * 获取即将开始的活动
     */
    IPage<Activity> getUpcomingActivities(Integer days, Integer page, Integer size);

    /**
     * 获取热门活动
     */
    IPage<Activity> getPopularActivities(Integer limit, Integer page, Integer size);
}
