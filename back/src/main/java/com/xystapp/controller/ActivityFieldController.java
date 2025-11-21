package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.common.Result;
import com.xystapp.entity.Activity;
import com.xystapp.service.ActivityFieldService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 活动场地关联控制器
 * 
 * @author XYST
 * @since 2025-11-16
 */
@Tag(name = "活动场地关联", description = "活动与场地关联相关API")
@RestController
@RequestMapping("/api/activities/field")
@RequiredArgsConstructor
public class ActivityFieldController {

    private final ActivityFieldService activityFieldService;

    @Operation(summary = "根据场地ID查询活动", description = "分页查询指定场地的活动列表")
    @GetMapping("/by-field/{fieldId}")
    public Result<IPage<Activity>> getActivitiesByFieldId(
            @Parameter(description = "场地ID") @PathVariable Long fieldId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size) {
        
        Page<Activity> page = new Page<>(current, size);
        IPage<Activity> result = activityFieldService.getActivitiesByFieldId(page, fieldId);
        return Result.success(result);
    }

    @Operation(summary = "根据标签ID查询活动", description = "通过场地关联分页查询指定标签的活动列表")
    @GetMapping("/by-tag/{tagId}")
    public Result<IPage<Activity>> getActivitiesByTagId(
            @Parameter(description = "标签ID") @PathVariable Long tagId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size) {
        
        Page<Activity> page = new Page<>(current, size);
        IPage<Activity> result = activityFieldService.getActivitiesByTagId(page, tagId);
        return Result.success(result);
    }

    @Operation(summary = "获取场地活动数量", description = "统计指定场地的活动数量")
    @GetMapping("/count/by-field/{fieldId}")
    public Result<Integer> getActivityCountByFieldId(
            @Parameter(description = "场地ID") @PathVariable Long fieldId) {
        int count = activityFieldService.getActivityCountByFieldId(fieldId);
        return Result.success(count);
    }

    @Operation(summary = "获取标签活动数量", description = "通过场地关联统计指定标签的活动数量")
    @GetMapping("/count/by-tag/{tagId}")
    public Result<Integer> getActivityCountByTagId(
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        int count = activityFieldService.getActivityCountByTagId(tagId);
        return Result.success(count);
    }

    @Operation(summary = "获取热门场地活动统计", description = "获取热门场地及其活动统计")
    @GetMapping("/popular-fields")
    public Result<List<Object>> getPopularFieldActivities(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<Object> result = activityFieldService.getPopularFieldActivities(limit);
        return Result.success(result);
    }

    @Operation(summary = "为活动分配场地", description = "为指定活动分配场地")
    @PostMapping("/{activityId}/assign/{fieldId}")
    @PreAuthorize("hasAuthority('activity:field:assign')")
    public Result<Boolean> assignFieldToActivity(
            @Parameter(description = "活动ID") @PathVariable Long activityId,
            @Parameter(description = "场地ID") @PathVariable Long fieldId) {
        try {
            boolean success = activityFieldService.assignFieldToActivity(activityId, fieldId);
            return success ? Result.success(true) : Result.error("分配场地失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("分配场地失败");
        }
    }

    @Operation(summary = "移除活动场地关联", description = "移除活动的场地关联")
    @DeleteMapping("/{activityId}/remove-field")
    @PreAuthorize("hasAuthority('activity:field:remove')")
    public Result<Boolean> removeFieldFromActivity(
            @Parameter(description = "活动ID") @PathVariable Long activityId) {
        boolean success = activityFieldService.removeFieldFromActivity(activityId);
        return success ? Result.success(true) : Result.error("移除场地关联失败");
    }

    @Operation(summary = "获取场地即将开始的活动", description = "获取指定场地即将开始的活动列表")
    @GetMapping("/upcoming/{fieldId}")
    public Result<List<Activity>> getUpcomingActivitiesByFieldId(
            @Parameter(description = "场地ID") @PathVariable Long fieldId,
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        List<Activity> activities = activityFieldService.getUpcomingActivitiesByFieldId(fieldId, limit);
        return Result.success(activities);
    }
}
