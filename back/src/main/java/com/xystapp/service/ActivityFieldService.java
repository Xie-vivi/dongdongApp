package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xystapp.entity.Activity;

import java.util.List;

/**
 * 活动场地关联服务接口
 * 
 * @author XYST
 * @since 2025-11-16
 */
public interface ActivityFieldService extends IService<Activity> {

    /**
     * 根据场地ID查询活动列表
     */
    IPage<Activity> getActivitiesByFieldId(Page<Activity> page, Long fieldId);

    /**
     * 根据标签ID查询活动列表（通过场地关联）
     */
    IPage<Activity> getActivitiesByTagId(Page<Activity> page, Long tagId);

    /**
     * 根据场地ID查询活动数量
     */
    int getActivityCountByFieldId(Long fieldId);

    /**
     * 根据标签ID查询活动数量（通过场地关联）
     */
    int getActivityCountByTagId(Long tagId);

    /**
     * 获取热门场地活动统计
     */
    List<Object> getPopularFieldActivities(Integer limit);

    /**
     * 为活动分配场地
     */
    boolean assignFieldToActivity(Long activityId, Long fieldId);

    /**
     * 移除活动的场地关联
     */
    boolean removeFieldFromActivity(Long activityId);

    /**
     * 根据场地获取即将开始的活动
     */
    List<Activity> getUpcomingActivitiesByFieldId(Long fieldId, Integer limit);
}
