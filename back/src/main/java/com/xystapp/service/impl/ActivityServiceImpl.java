package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xystapp.dto.ActivityCreateRequest;
import com.xystapp.dto.ActivityQueryRequest;
import com.xystapp.dto.ActivityUpdateRequest;
import com.xystapp.entity.Activity;
import com.xystapp.entity.ActivitySignup;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class ActivityServiceImpl implements ActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private static final int MAX_IMAGES_COUNT = 9;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Activity createActivity(ActivityCreateRequest request, Long userId) {
        log.info("创建活动请求: userId={}, title={}", userId, request.getTitle());

        // 参数验证
        validateActivityRequest(request);

        // 创建活动对象
        Activity activity = new Activity();
        activity.setUserId(userId);
        activity.setTitle(request.getTitle());
        activity.setDescription(request.getDescription());
        activity.setDate(request.getDate());
        activity.setDay(request.getDay());
        activity.setTime(request.getTime());
        activity.setLocation(request.getLocation());
        activity.setTag(request.getTag());
        activity.setMaxParticipants(request.getMaxParticipants());
        activity.setSignupDeadline(request.getSignupDeadline());
        activity.setImage(request.getImage());
        activity.setStatus(request.getStatus() != null ? request.getStatus() : "draft");

        // 处理图片列表
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > MAX_IMAGES_COUNT) {
                throw new BusinessException(400, "图片数量不能超过" + MAX_IMAGES_COUNT + "张");
            }
            activity.setImages(convertImagesToJson(request.getImages()));
        }

        // 设置初始统计信息
        activity.setParticipants(0);
        activity.setLikesCount(0);
        activity.setCommentsCount(0);

        // 保存到数据库
        int result = activityMapper.insert(activity);
        if (result <= 0) {
            throw new BusinessException(500, "创建活动失败");
        }

        log.info("活动创建成功: id={}, userId={}", activity.getId(), userId);
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Activity updateActivity(Long id, ActivityUpdateRequest request, Long userId) {
        log.info("更新活动请求: id={}, userId={}", id, userId);

        // 检查活动是否存在和权限
        Activity existingActivity = activityMapper.selectActivityWithUserById(id);
        if (existingActivity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 管理员操作检查（userId为null表示管理员操作）
        if (userId != null && !existingActivity.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限修改此活动");
        }

        // 检查活动状态是否允许修改
        if ("published".equals(existingActivity.getStatus()) && hasSignups(id)) {
            throw new BusinessException(400, "已有用户报名的活动不能修改");
        }

        // 参数验证
        validateActivityRequest(request);

        // 更新活动信息
        existingActivity.setTitle(request.getTitle());
        existingActivity.setDescription(request.getDescription());
        existingActivity.setDate(request.getDate());
        existingActivity.setDay(request.getDay());
        existingActivity.setTime(request.getTime());
        existingActivity.setLocation(request.getLocation());
        existingActivity.setTag(request.getTag());
        existingActivity.setMaxParticipants(request.getMaxParticipants());
        existingActivity.setSignupDeadline(request.getSignupDeadline());
        existingActivity.setImage(request.getImage());
        
        if (request.getStatus() != null) {
            existingActivity.setStatus(request.getStatus());
        }

        // 处理图片列表
        if (request.getImages() != null) {
            if (request.getImages().size() > MAX_IMAGES_COUNT) {
                throw new BusinessException(400, "图片数量不能超过" + MAX_IMAGES_COUNT + "张");
            }
            existingActivity.setImages(convertImagesToJson(request.getImages()));
        }

        // 更新数据库
        int result = activityMapper.updateById(existingActivity);
        if (result <= 0) {
            throw new BusinessException(500, "更新活动失败");
        }

        log.info("活动更新成功: id={}, userId={}", id, userId);
        return existingActivity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id, Long userId) {
        log.info("删除活动请求: id={}, userId={}", id, userId);

        // 检查活动是否存在和权限
        Activity existingActivity = activityMapper.selectActivityWithUserById(id);
        if (existingActivity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 管理员操作检查（userId为null表示管理员操作）
        if (userId != null && !existingActivity.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限删除此活动");
        }

        // 检查活动状态
        if ("published".equals(existingActivity.getStatus()) && hasSignups(id)) {
            throw new BusinessException(400, "已有用户报名的活动不能删除，请先取消活动");
        }

        // 删除活动
        int result = activityMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(500, "删除活动失败");
        }

        log.info("活动删除成功: id={}, userId={}", id, userId);
    }

    @Override
    public Activity getActivityById(Long id) {
        log.info("获取活动详情: id={}", id);

        Activity activity = activityMapper.selectActivityWithUserById(id);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        return activity;
    }

    @Override
    public IPage<Activity> getActivityList(ActivityQueryRequest query) {
        log.info("查询活动列表: keyword={}, userId={}, tag={}, status={}", 
                query.getKeyword(), query.getUserId(), query.getTag(), query.getStatus());

        // 设置默认值
        if (query.getPage() == null || query.getPage() < 1) {
            query.setPage(1);
        }
        if (query.getSize() == null || query.getSize() < 1) {
            query.setSize(20);
        }
        if (query.getSortBy() == null) {
            query.setSortBy("date");
        }
        if (query.getSortOrder() == null) {
            query.setSortOrder("asc");
        }

        // 只查询已发布的活动（除非指定状态）
        if (query.getStatus() == null) {
            query.setStatus("published");
        }

        Page<Activity> page = new Page<>(query.getPage(), query.getSize());
        IPage<Activity> result = activityMapper.selectActivitiesWithUser(page, query);

        log.info("查询活动列表成功: 总数={}, 当前页数据={}", result.getTotal(), result.getRecords().size());
        return result;
    }

    @Override
    public IPage<Activity> getUserActivities(Long userId, String status, Integer page, Integer size) {
        log.info("获取用户活动列表: userId={}, status={}", userId, status);

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Activity> pageParam = new Page<>(page, size);
        IPage<Activity> result = activityMapper.selectActivitiesByUserId(pageParam, userId, status);

        log.info("获取用户活动列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Activity publishDraft(Long id, Long userId) {
        log.info("发布草稿活动: id={}, userId={}", id, userId);

        // 检查权限
        if (!isActivityOwner(id, userId)) {
            throw new BusinessException(403, "无权限操作此活动");
        }

        Activity activity = activityMapper.selectActivityWithUserById(id);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        if (!"draft".equals(activity.getStatus())) {
            throw new BusinessException(400, "只能发布草稿状态的活动");
        }

        activity.setStatus("published");
        int result = activityMapper.updateById(activity);
        if (result <= 0) {
            throw new BusinessException(500, "发布活动失败");
        }

        log.info("草稿活动发布成功: id={}, userId={}", id, userId);
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Activity setAsDraft(Long id, Long userId) {
        log.info("将活动设为草稿: id={}, userId={}", id, userId);

        // 检查权限
        if (!isActivityOwner(id, userId)) {
            throw new BusinessException(403, "无权限操作此活动");
        }

        Activity activity = activityMapper.selectActivityWithUserById(id);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        if (hasSignups(id)) {
            throw new BusinessException(400, "已有用户报名的活动不能设为草稿");
        }

        activity.setStatus("draft");
        int result = activityMapper.updateById(activity);
        if (result <= 0) {
            throw new BusinessException(500, "设置草稿失败");
        }

        log.info("活动设为草稿成功: id={}, userId={}", id, userId);
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Activity cancelActivity(Long id, Long userId) {
        log.info("取消活动: id={}, userId={}", id, userId);

        // 检查权限
        if (!isActivityOwner(id, userId)) {
            throw new BusinessException(403, "无权限操作此活动");
        }

        Activity activity = activityMapper.selectActivityWithUserById(id);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        if ("cancelled".equals(activity.getStatus())) {
            throw new BusinessException(400, "活动已经是取消状态");
        }

        activity.setStatus("cancelled");
        int result = activityMapper.updateById(activity);
        if (result <= 0) {
            throw new BusinessException(500, "取消活动失败");
        }

        log.info("活动取消成功: id={}, userId={}", id, userId);
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void signupActivity(Long activityId, Long userId) {
        log.info("报名活动: activityId={}, userId={}", activityId, userId);

        // 检查活动是否存在
        Activity activity = activityMapper.selectActivityWithUserById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 检查活动状态
        if (!"published".equals(activity.getStatus())) {
            throw new BusinessException(400, "活动未发布，无法报名");
        }

        // 检查报名截止时间
        if (activity.getSignupDeadline() != null && LocalDateTime.now().isAfter(activity.getSignupDeadline())) {
            throw new BusinessException(400, "报名已截止");
        }

        // 检查是否已报名
        if (isUserSignedUp(activityId, userId)) {
            throw new BusinessException(409, "您已报名此活动");
        }

        // 检查人数限制
        if (activity.getMaxParticipants() != null) {
            int currentCount = activityMapper.getActivityParticipantCount(activityId);
            if (currentCount >= activity.getMaxParticipants()) {
                throw new BusinessException(409, "活动报名人数已满");
            }
        }

        // 创建报名记录
        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setStatus(ActivitySignup.Status.CONFIRMED.getCode());
        signup.setSignupTime(LocalDateTime.now());

        // 这里需要ActivitySignupMapper，暂时简化处理
        // TODO: 实现ActivitySignupMapper和相关的数据库操作

        // 更新活动参与人数
        activityMapper.incrementParticipants(activityId);

        log.info("活动报名成功: activityId={}, userId={}", activityId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSignup(Long activityId, Long userId) {
        log.info("取消报名: activityId={}, userId={}", activityId, userId);

        // 检查是否已报名
        if (!isUserSignedUp(activityId, userId)) {
            throw new BusinessException(400, "您未报名此活动");
        }

        // 检查活动是否允许取消报名
        Activity activity = activityMapper.selectActivityWithUserById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        if (activity.getDate().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException(400, "活动已开始，无法取消报名");
        }

        // 删除报名记录
        // TODO: 实现ActivitySignupMapper和相关的数据库操作

        // 更新活动参与人数
        activityMapper.decrementParticipants(activityId);

        log.info("取消报名成功: activityId={}, userId={}", activityId, userId);
    }

    @Override
    public IPage<Activity> getSignedUpActivities(Long userId, Integer page, Integer size) {
        log.info("获取用户报名活动列表: userId={}", userId);

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Activity> pageParam = new Page<>(page, size);
        IPage<Activity> result = activityMapper.selectSignedUpActivities(pageParam, userId);

        log.info("获取用户报名活动列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public List<Activity> getActivitiesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        log.info("批量获取活动信息: ids={}", ids);
        return activityMapper.selectActivitiesByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityStats(Long activityId, Integer likesCount, Integer commentsCount, Integer participants) {
        log.info("更新活动统计信息: activityId={}, likes={}, comments={}, participants={}", 
                activityId, likesCount, commentsCount, participants);

        int result = activityMapper.updateActivityStats(activityId, likesCount, commentsCount, participants);
        if (result <= 0) {
            throw new BusinessException(500, "更新统计信息失败");
        }
    }

    @Override
    public boolean isActivityOwner(Long activityId, Long userId) {
        // 管理员操作检查（userId为null表示管理员操作）
        if (userId == null) {
            return true;
        }
        return activityMapper.isActivityOwner(activityId, userId);
    }

    @Override
    public boolean isUserSignedUp(Long activityId, Long userId) {
        return activityMapper.isUserSignedUp(activityId, userId);
    }

    @Override
    public IPage<Activity> getUpcomingActivities(Integer days, Integer page, Integer size) {
        log.info("获取即将开始的活动: days={}", days);

        // 设置默认值
        if (days == null || days < 1) {
            days = 7;
        }
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Activity> pageParam = new Page<>(page, size);
        IPage<Activity> result = activityMapper.selectUpcomingActivities(pageParam, days);

        log.info("获取即将开始的活动成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    public IPage<Activity> getPopularActivities(Integer limit, Integer page, Integer size) {
        log.info("获取热门活动: limit={}", limit);

        // 设置默认值
        if (limit == null || limit < 1) {
            limit = 10;
        }
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Activity> pageParam = new Page<>(page, size);
        IPage<Activity> result = activityMapper.selectPopularActivities(pageParam, limit);

        log.info("获取热门活动成功: 总数={}", result.getTotal());
        return result;
    }

    /**
     * 验证活动请求参数
     */
    private void validateActivityRequest(Object request) {
        // 基础验证由@Valid注解处理
        // 这里可以添加额外的业务验证逻辑
    }

    /**
     * 将图片列表转换为JSON字符串
     */
    private String convertImagesToJson(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(images);
        } catch (JsonProcessingException e) {
            log.error("图片列表JSON转换失败", e);
            throw new BusinessException(500, "图片数据处理失败");
        }
    }

    /**
     * 检查活动是否有报名用户
     */
    private boolean hasSignups(Long activityId) {
        Integer count = activityMapper.getActivityParticipantCount(activityId);
        return count != null && count > 0;
    }
}
