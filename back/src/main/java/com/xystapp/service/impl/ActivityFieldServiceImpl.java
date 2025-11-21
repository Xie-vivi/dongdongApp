package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xystapp.entity.Activity;
import com.xystapp.entity.FieldTag;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.mapper.FieldTagMapper;
import com.xystapp.service.ActivityFieldService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动场地关联服务实现类
 * 
 * @author XYST
 * @since 2025-11-16
 */
@Service
@RequiredArgsConstructor
public class ActivityFieldServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityFieldService {

    private static final Logger log = LoggerFactory.getLogger(ActivityFieldServiceImpl.class);

    private final ActivityMapper activityMapper;
    private final FieldTagMapper fieldTagMapper;

    @Override
    public IPage<Activity> getActivitiesByFieldId(Page<Activity> page, Long fieldId) {
        if (fieldId == null) {
            return new Page<>(page.getCurrent(), page.getSize());
        }
        
        return activityMapper.selectActivitiesByFieldId(page, fieldId);
    }

    @Override
    public IPage<Activity> getActivitiesByTagId(Page<Activity> page, Long tagId) {
        if (tagId == null) {
            return new Page<>(page.getCurrent(), page.getSize());
        }
        
        return activityMapper.selectActivitiesByTagId(page, tagId);
    }

    @Override
    public int getActivityCountByFieldId(Long fieldId) {
        if (fieldId == null) {
            return 0;
        }
        
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getFieldId, fieldId)
               .eq(Activity::getStatus, "published");
        
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public int getActivityCountByTagId(Long tagId) {
        if (tagId == null) {
            return 0;
        }
        
        // 通过场地标签关联查询活动数量
        List<FieldTag> fieldTags = fieldTagMapper.selectByTagId(tagId);
        if (fieldTags.isEmpty()) {
            return 0;
        }
        
        List<Long> fieldIds = fieldTags.stream()
                .map(FieldTag::getFieldId)
                .collect(Collectors.toList());
        
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Activity::getFieldId, fieldIds)
               .eq(Activity::getStatus, "published");
        
        return Math.toIntExact(count(wrapper));
    }

    @Override
    public List<Object> getPopularFieldActivities(Integer limit) {
        // 获取热门场地及其活动数量
        // 这里需要自定义SQL查询，暂时返回空列表
        return new ArrayList<>();
    }

    @Override
    public boolean assignFieldToActivity(Long activityId, Long fieldId) {
        if (activityId == null) {
            throw new IllegalArgumentException("活动ID不能为空");
        }
        
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        activity.setFieldId(fieldId);
        boolean success = updateById(activity);
        
        if (success) {
            log.info("为活动 {} 分配场地 {}", activityId, fieldId);
        }
        return success;
    }

    @Override
    public boolean removeFieldFromActivity(Long activityId) {
        if (activityId == null) {
            return false;
        }
        
        Activity activity = getById(activityId);
        if (activity == null) {
            return false;
        }
        
        activity.setFieldId(null);
        boolean success = updateById(activity);
        
        if (success) {
            log.info("移除活动 {} 的场地关联", activityId);
        }
        return success;
    }

    @Override
    public List<Activity> getUpcomingActivitiesByFieldId(Long fieldId, Integer limit) {
        if (fieldId == null) {
            return new ArrayList<>();
        }
        
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getFieldId, fieldId)
               .eq(Activity::getStatus, "published")
               .ge(Activity::getDate, LocalDate.now())
               .orderByAsc(Activity::getDate)
               .orderByAsc(Activity::getTime);
        
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        return list(wrapper);
    }
}
