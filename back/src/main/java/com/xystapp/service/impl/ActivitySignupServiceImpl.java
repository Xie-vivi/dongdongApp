package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Activity;
import com.xystapp.entity.ActivitySignup;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.ActivityMapper;
import com.xystapp.mapper.ActivitySignupMapper;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.ActivitySignupService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动报名服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class ActivitySignupServiceImpl implements ActivitySignupService {

    private static final Logger log = LoggerFactory.getLogger(ActivitySignupServiceImpl.class);

    @Autowired
    private ActivitySignupMapper activitySignupMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private UserMapper userMapper;

    // ==================== 报名管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignup signupActivity(Long activityId, Long userId, String note) {
        log.info("活动报名: activityId={}, userId={}, note={}", activityId, userId, note);

        // 验证参数
        if (activityId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 验证报名条件
        validateSignupConditions(activityId, userId);

        // 检查是否已报名
        if (isUserSignedUp(activityId, userId)) {
            throw new BusinessException(400, "用户已报名该活动");
        }

        // 创建报名记录
        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setStatus(ActivitySignup.Status.CONFIRMED.getCode()); // 默认直接确认
        signup.setSignupTime(LocalDateTime.now());
        signup.setNote(note);

        int result = activitySignupMapper.insert(signup);
        if (result <= 0) {
            throw new BusinessException(500, "报名失败");
        }

        // 更新活动参与人数
        updateActivityParticipants(activityId);

        log.info("活动报名成功: activityId={}, userId={}", activityId, userId);
        return signup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignup cancelSignup(Long activityId, Long userId) {
        log.info("取消报名: activityId={}, userId={}", activityId, userId);

        // 验证参数
        if (activityId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 验证取消条件
        validateCancelConditions(activityId, userId);

        // 获取报名记录
        ActivitySignup signup = activitySignupMapper.selectByActivityIdAndUserId(activityId, userId);
        if (signup == null) {
            throw new BusinessException(404, "报名记录不存在");
        }

        // 检查状态
        if (ActivitySignup.Status.CANCELLED.getCode().equals(signup.getStatus())) {
            throw new BusinessException(400, "报名已取消");
        }

        // 更新状态
        signup.setStatus(ActivitySignup.Status.CANCELLED.getCode());
        signup.setCancelTime(LocalDateTime.now());

        int result = activitySignupMapper.updateById(signup);
        if (result <= 0) {
            throw new BusinessException(500, "取消报名失败");
        }

        // 更新活动参与人数
        updateActivityParticipants(activityId);

        log.info("取消报名成功: activityId={}, userId={}", activityId, userId);
        return signup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignup approveSignup(Long activityId, Long userId, Long operatorId) {
        log.info("审核通过报名: activityId={}, userId={}, operatorId={}", activityId, userId, operatorId);

        // 验证参数
        if (activityId == null || userId == null || operatorId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 验证审核权限
        validateApprovePermission(activityId, operatorId);

        // 获取报名记录
        ActivitySignup signup = activitySignupMapper.selectByActivityIdAndUserId(activityId, userId);
        if (signup == null) {
            throw new BusinessException(404, "报名记录不存在");
        }

        // 检查状态
        if (!ActivitySignup.Status.PENDING.getCode().equals(signup.getStatus())) {
            throw new BusinessException(400, "只能审核待审核的报名");
        }

        // 更新状态
        signup.setStatus(ActivitySignup.Status.CONFIRMED.getCode());

        int result = activitySignupMapper.updateById(signup);
        if (result <= 0) {
            throw new BusinessException(500, "审核失败");
        }

        // 更新活动参与人数
        updateActivityParticipants(activityId);

        log.info("审核通过报名成功: activityId={}, userId={}", activityId, userId);
        return signup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignup rejectSignup(Long activityId, Long userId, Long operatorId, String reason) {
        log.info("审核拒绝报名: activityId={}, userId={}, operatorId={}, reason={}", activityId, userId, operatorId, reason);

        // 验证参数
        if (activityId == null || userId == null || operatorId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 验证审核权限
        validateApprovePermission(activityId, operatorId);

        // 获取报名记录
        ActivitySignup signup = activitySignupMapper.selectByActivityIdAndUserId(activityId, userId);
        if (signup == null) {
            throw new BusinessException(404, "报名记录不存在");
        }

        // 检查状态
        if (!ActivitySignup.Status.PENDING.getCode().equals(signup.getStatus())) {
            throw new BusinessException(400, "只能审核待审核的报名");
        }

        // 更新状态和备注
        signup.setStatus(ActivitySignup.Status.REJECTED.getCode());
        if (reason != null && !reason.trim().isEmpty()) {
            String existingNote = signup.getNote() != null ? signup.getNote() : "";
            signup.setNote(existingNote + "\n[拒绝理由]: " + reason.trim());
        }

        int result = activitySignupMapper.updateById(signup);
        if (result <= 0) {
            throw new BusinessException(500, "审核失败");
        }

        log.info("审核拒绝报名成功: activityId={}, userId={}", activityId, userId);
        return signup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ActivitySignup> batchApproveSignups(Long activityId, List<Long> userIds, Long operatorId) {
        log.info("批量审核通过报名: activityId={}, userIds={}, operatorId={}", activityId, userIds, operatorId);

        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 验证审核权限
        validateApprovePermission(activityId, operatorId);

        List<ActivitySignup> results = new ArrayList<>();
        for (Long userId : userIds) {
            try {
                ActivitySignup result = approveSignup(activityId, userId, operatorId);
                results.add(result);
            } catch (Exception e) {
                log.warn("批量审核失败: activityId={}, userId={}, error={}", activityId, userId, e.getMessage());
            }
        }

        log.info("批量审核通过报名完成: activityId={}, 成功数量={}", activityId, results.size());
        return results;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ActivitySignup> batchRejectSignups(Long activityId, List<Long> userIds, Long operatorId, String reason) {
        log.info("批量审核拒绝报名: activityId={}, userIds={}, operatorId={}, reason={}", activityId, userIds, operatorId, reason);

        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 验证审核权限
        validateApprovePermission(activityId, operatorId);

        List<ActivitySignup> results = new ArrayList<>();
        for (Long userId : userIds) {
            try {
                ActivitySignup result = rejectSignup(activityId, userId, operatorId, reason);
                results.add(result);
            } catch (Exception e) {
                log.warn("批量审核失败: activityId={}, userId={}, error={}", activityId, userId, e.getMessage());
            }
        }

        log.info("批量审核拒绝报名完成: activityId={}, 成功数量={}", activityId, results.size());
        return results;
    }

    // ==================== 报名查询 ====================

    @Override
    public IPage<ActivitySignup> getActivitySignups(Long activityId, Integer page, Integer size, String status) {
        log.info("获取活动报名列表: activityId={}, page={}, size={}, status={}", activityId, page, size, status);

        if (activityId == null) {
            throw new BusinessException(400, "活动ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ActivitySignup> pageParam = new Page<>(page, size);
        IPage<ActivitySignup> result = activitySignupMapper.selectActivitySignups(pageParam, activityId, status);

        log.info("获取活动报名列表成功: activityId={}, 总数={}", activityId, result.getTotal());
        return result;
    }

    @Override
    public IPage<ActivitySignup> getUserSignups(Long userId, Integer page, Integer size, String status) {
        log.info("获取用户报名列表: userId={}, page={}, size={}, status={}", userId, page, size, status);

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

        Page<ActivitySignup> pageParam = new Page<>(page, size);
        IPage<ActivitySignup> result = activitySignupMapper.selectUserSignups(pageParam, userId, status);

        log.info("获取用户报名列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public ActivitySignup getSignupDetail(Long activityId, Long userId) {
        log.info("获取报名详情: activityId={}, userId={}", activityId, userId);

        if (activityId == null || userId == null) {
            return null;
        }

        ActivitySignup signup = activitySignupMapper.selectSignupDetail(activityId, userId);

        log.info("获取报名详情成功: activityId={}, userId={}", activityId, userId);
        return signup;
    }

    @Override
    public ActivitySignup getUserActivitySignup(Long activityId, Long userId) {
        if (activityId == null || userId == null) {
            return null;
        }

        return activitySignupMapper.selectByActivityIdAndUserId(activityId, userId);
    }

    @Override
    public ActivitySignupStats getActivitySignupStats(Long activityId) {
        log.info("获取活动报名统计: activityId={}", activityId);

        if (activityId == null) {
            throw new BusinessException(400, "活动ID不能为空");
        }

        Long totalSignups = (long) getActivitySignupCount(activityId, null);
        Long confirmedSignups = (long) getActivitySignupCount(activityId, ActivitySignup.Status.CONFIRMED.getCode());
        Long pendingSignups = (long) getActivitySignupCount(activityId, ActivitySignup.Status.PENDING.getCode());
        Long cancelledSignups = (long) getActivitySignupCount(activityId, ActivitySignup.Status.CANCELLED.getCode());
        Long rejectedSignups = (long) getActivitySignupCount(activityId, ActivitySignup.Status.REJECTED.getCode());
        Long todaySignups = (long) getTodaySignupCount(activityId);

        // 获取可用名额
        Activity activity = activityMapper.selectById(activityId);
        Integer availableSlots = null;
        if (activity != null && activity.getMaxParticipants() != null) {
            availableSlots = activity.getMaxParticipants() - confirmedSignups.intValue();
        }

        ActivitySignupStats stats = new ActivitySignupStats(totalSignups, confirmedSignups, pendingSignups,
                                                           cancelledSignups, rejectedSignups, todaySignups, availableSlots);

        log.info("获取活动报名统计成功: activityId={}, total={}, confirmed={}", activityId, totalSignups, confirmedSignups);
        return stats;
    }

    @Override
    public UserSignupStats getUserSignupStats(Long userId) {
        log.info("获取用户报名统计: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Long totalSignups = (long) getUserSignupCount(userId, null);
        Long confirmedSignups = (long) getUserSignupCount(userId, ActivitySignup.Status.CONFIRMED.getCode());
        Long pendingSignups = (long) getUserSignupCount(userId, ActivitySignup.Status.PENDING.getCode());
        Long cancelledSignups = (long) getUserSignupCount(userId, ActivitySignup.Status.CANCELLED.getCode());
        Long rejectedSignups = (long) getUserSignupCount(userId, ActivitySignup.Status.REJECTED.getCode());
        Long thisMonthSignups = activitySignupMapper.countUserSignupsByMonth(userId);

        UserSignupStats stats = new UserSignupStats(totalSignups, confirmedSignups, pendingSignups,
                                                   cancelledSignups, rejectedSignups, thisMonthSignups);

        log.info("获取用户报名统计成功: userId={}, total={}, confirmed={}", userId, totalSignups, confirmedSignups);
        return stats;
    }

    // ==================== 权限验证 ====================

    @Override
    public boolean isUserSignedUp(Long activityId, Long userId) {
        if (activityId == null || userId == null) {
            return false;
        }

        return activitySignupMapper.selectByActivityIdAndUserId(activityId, userId) != null;
    }

    @Override
    public boolean canUserSignup(Long activityId, Long userId) {
        // 简化实现：检查活动是否存在、用户是否已报名、活动是否已满员、是否在报名时间内
        try {
            validateSignupConditions(activityId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canUserCancelSignup(Long activityId, Long userId) {
        try {
            validateCancelConditions(activityId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean hasApprovePermission(Long activityId, Long userId) {
        try {
            validateApprovePermission(activityId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean activityExists(Long activityId) {
        if (activityId == null) {
            return false;
        }

        return activityMapper.selectById(activityId) != null;
    }

    @Override
    public boolean signupExists(Long activityId, Long userId) {
        return isUserSignedUp(activityId, userId);
    }

    // ==================== 业务验证 ====================

    @Override
    public void validateSignupConditions(Long activityId, Long userId) {
        // 检查活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 检查活动状态
        if (!"published".equals(activity.getStatus())) {
            throw new BusinessException(400, "活动未发布，无法报名");
        }

        // 检查用户是否存在
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 检查是否已报名
        if (isUserSignedUp(activityId, userId)) {
            throw new BusinessException(400, "用户已报名该活动");
        }

        // 检查报名截止时间
        if (activity.getSignupDeadline() != null && LocalDateTime.now().isAfter(activity.getSignupDeadline())) {
            throw new BusinessException(400, "报名已截止");
        }

        // 检查人数限制
        if (activity.getMaxParticipants() != null) {
            Integer currentCount = getActivitySignupCount(activityId, ActivitySignup.Status.CONFIRMED.getCode());
            if (currentCount >= activity.getMaxParticipants()) {
                throw new BusinessException(400, "活动报名人数已满");
            }
        }
    }

    @Override
    public void validateCancelConditions(Long activityId, Long userId) {
        // 检查报名是否存在
        ActivitySignup signup = activitySignupMapper.selectByActivityIdAndUserId(activityId, userId);
        if (signup == null) {
            throw new BusinessException(404, "报名记录不存在");
        }

        // 检查状态
        if (ActivitySignup.Status.CANCELLED.getCode().equals(signup.getStatus())) {
            throw new BusinessException(400, "报名已取消");
        }

        // 检查活动时间（活动开始前可以取消）
        Activity activity = activityMapper.selectById(activityId);
        if (activity != null && activity.getDate() != null) {
            // 简化实现：活动当天不能取消
            if (LocalDateTime.now().toLocalDate().isAfter(activity.getDate().minusDays(1))) {
                throw new BusinessException(400, "活动即将开始，无法取消报名");
            }
        }
    }

    @Override
    public void validateApprovePermission(Long activityId, Long operatorId) {
        // 检查活动是否存在
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        // 检查是否是活动创建者
        if (!activity.getUserId().equals(operatorId)) {
            throw new BusinessException(403, "只有活动创建者可以审核报名");
        }
    }

    // ==================== 统计信息 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActivityParticipants(Long activityId) {
        // 计算确认参与人数
        Integer confirmedCount = getActivitySignupCount(activityId, ActivitySignup.Status.CONFIRMED.getCode());
        
        Activity activity = new Activity();
        activity.setId(activityId);
        activity.setParticipants(confirmedCount);
        
        activityMapper.updateById(activity);
    }

    @Override
    public Integer getActivitySignupCount(Long activityId, String status) {
        if (activityId == null) {
            return 0;
        }

        return activitySignupMapper.countByActivityIdAndStatus(activityId, status);
    }

    @Override
    public Integer getUserSignupCount(Long userId, String status) {
        if (userId == null) {
            return 0;
        }

        return activitySignupMapper.countByUserIdAndStatus(userId, status);
    }

    @Override
    public Integer getTodaySignupCount(Long activityId) {
        if (activityId == null) {
            return 0;
        }

        return activitySignupMapper.countTodaySignups(activityId);
    }

    @Override
    public List<SignupTrendStats> getSignupTrend(Long activityId, Integer days) {
        if (activityId == null) {
            return new ArrayList<>();
        }

        if (days == null || days < 1) {
            days = 7;
        }

        return activitySignupMapper.selectSignupTrend(activityId, days);
    }

    // ==================== 批量操作 ====================

    @Override
    public List<ActivitySignup> getSignupsByIds(List<Long> signupIds) {
        if (signupIds == null || signupIds.isEmpty()) {
            return new ArrayList<>();
        }

        return activitySignupMapper.selectSignupsByIds(signupIds);
    }

    @Override
    public List<ActivitySignup> getUserSignupsByActivityIds(List<Long> activityIds, Long userId) {
        if (activityIds == null || activityIds.isEmpty() || userId == null) {
            return new ArrayList<>();
        }

        return activitySignupMapper.selectUserSignupsByActivityIds(activityIds, userId);
    }

    @Override
    public List<SignupStatusInfo> batchCheckSignupStatus(List<Long> activityIds, Long userId) {
        if (activityIds == null || activityIds.isEmpty() || userId == null) {
            return new ArrayList<>();
        }

        return activitySignupMapper.selectBatchSignupStatus(activityIds, userId);
    }

    // ==================== 数据导出 ====================

    @Override
    public List<ActivitySignup> exportActivitySignups(Long activityId, String status) {
        if (activityId == null) {
            return new ArrayList<>();
        }

        return activitySignupMapper.selectExportActivitySignups(activityId, status);
    }

    @Override
    public List<ActivitySignup> exportUserSignups(Long userId, String status) {
        if (userId == null) {
            return new ArrayList<>();
        }

        return activitySignupMapper.selectExportUserSignups(userId, status);
    }

    // ==================== 业务查询 ====================

    @Override
    public List<ActivitySignup> getRecentSignups(Long activityId, Integer limit) {
        if (activityId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 10;
        }

        return activitySignupMapper.selectRecentSignups(activityId, limit);
    }

    @Override
    public List<ActivitySignup> getPendingSignups(Long activityId) {
        if (activityId == null) {
            return new ArrayList<>();
        }

        return activitySignupMapper.selectPendingSignups(activityId);
    }

    @Override
    public Integer getPendingSignupCount(Long activityId) {
        if (activityId == null) {
            return 0;
        }

        return activitySignupMapper.countPendingSignups(activityId);
    }

    @Override
    public List<ActivitySignup> getCreatorPendingSignups(Long creatorId, Integer limit) {
        if (creatorId == null) {
            return new ArrayList<>();
        }

        if (limit == null || limit < 1) {
            limit = 20;
        }

        return activitySignupMapper.selectCreatorPendingSignups(creatorId, limit);
    }
}
