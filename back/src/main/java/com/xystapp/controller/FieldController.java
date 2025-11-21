package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.xystapp.common.Result;
import com.xystapp.entity.Field;
import com.xystapp.entity.FieldMember;
import com.xystapp.service.FieldService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 场地控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "场地管理")
@RestController
@RequestMapping("/fields")
public class FieldController {

    private static final Logger log = LoggerFactory.getLogger(FieldController.class);

    @Autowired
    private FieldService fieldService;

    // ==================== 场地创建 ====================

    /**
     * 创建场地
     */
    @ApiOperation("创建场地")
    @PostMapping("/create")
    public Result<Field> createField(@ApiParam("场地名称") @RequestParam String name,
                                     @ApiParam("场地描述") @RequestParam(required = false) String description,
                                     @ApiParam("场地头像") @RequestParam(required = false) String avatar) {
        log.info("创建场地: name={}, description={}", name, description);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Field field = fieldService.createField(name, description, avatar, currentUserId);

        return Result.success(field);
    }

    /**
     * 更新场地信息
     */
    @ApiOperation("更新场地信息")
    @PutMapping("/{fieldId}")
    public Result<Field> updateField(@ApiParam("场地ID") @PathVariable Long fieldId,
                                     @ApiParam("场地名称") @RequestParam(required = false) String name,
                                     @ApiParam("场地描述") @RequestParam(required = false) String description,
                                     @ApiParam("场地头像") @RequestParam(required = false) String avatar) {
        log.info("更新场地信息: fieldId={}, name={}", fieldId, name);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Field field = fieldService.updateField(fieldId, name, description, avatar, currentUserId);

        return Result.success(field);
    }

    /**
     * 删除场地
     */
    @ApiOperation("删除场地")
    @DeleteMapping("/{fieldId}")
    public Result<Void> deleteField(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("删除场地: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        fieldService.deleteField(fieldId, currentUserId);

        return Result.success();
    }

    // ==================== 场地查询 ====================

    /**
     * 获取场地详情
     */
    @ApiOperation("获取场地详情")
    @GetMapping("/{fieldId}")
    public Result<Field> getFieldById(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("获取场地详情: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        Field field = fieldService.getFieldById(fieldId, currentUserId);

        if (field == null) {
            return Result.error(404, "场地不存在");
        }

        return Result.success(field);
    }

    /**
     * 获取场地列表
     */
    @ApiOperation("获取场地列表")
    @GetMapping("/list")
    public Result<IPage<Field>> getFieldList(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword) {
        log.info("获取场地列表: page={}, size={}, keyword={}", page, size, keyword);

        IPage<Field> result = fieldService.getFieldList(page, size, keyword);

        return Result.success(result);
    }

    /**
     * 获取用户创建的场地列表
     */
    @ApiOperation("获取用户创建的场地列表")
    @GetMapping("/created")
    public Result<IPage<Field>> getUserCreatedFields(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户创建的场地列表: page={}, size={}", page, size);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<Field> result = fieldService.getUserCreatedFields(currentUserId, page, size);

        return Result.success(result);
    }

    /**
     * 获取用户加入的场地列表
     */
    @ApiOperation("获取用户加入的场地列表")
    @GetMapping("/joined")
    public Result<IPage<Field>> getUserJoinedFields(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("获取用户加入的场地列表: page={}, size={}", page, size);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        IPage<Field> result = fieldService.getUserJoinedFields(currentUserId, page, size);

        return Result.success(result);
    }

    /**
     * 搜索场地
     */
    @ApiOperation("搜索场地")
    @GetMapping("/search")
    public Result<IPage<Field>> searchFields(
            @ApiParam("搜索关键词") @RequestParam String keyword,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size) {
        log.info("搜索场地: keyword={}, page={}, size={}", keyword, page, size);

        IPage<Field> result = fieldService.searchFields(keyword, page, size);

        return Result.success(result);
    }

    /**
     * 获取推荐场地
     */
    @ApiOperation("获取推荐场地")
    @GetMapping("/recommended")
    public Result<List<Field>> getRecommendedFields(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取推荐场地: limit={}", limit);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Field> fields = fieldService.getRecommendedFields(currentUserId, limit);

        return Result.success(fields);
    }

    /**
     * 获取热门场地
     */
    @ApiOperation("获取热门场地")
    @GetMapping("/popular")
    public Result<List<Field>> getPopularFields(
            @ApiParam("限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        log.info("获取热门场地: limit={}", limit);

        List<Field> fields = fieldService.getPopularFields(limit);

        return Result.success(fields);
    }

    // ==================== 成员管理 ====================

    /**
     * 加入场地
     */
    @ApiOperation("加入场地")
    @PostMapping("/{fieldId}/join")
    public Result<FieldMember> joinField(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("加入场地: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        FieldMember member = fieldService.joinField(fieldId, currentUserId);

        return Result.success(member);
    }

    /**
     * 退出场地
     */
    @ApiOperation("退出场地")
    @PostMapping("/{fieldId}/leave")
    public Result<Void> leaveField(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("退出场地: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        fieldService.leaveField(fieldId, currentUserId);

        return Result.success();
    }

    /**
     * 邀请用户加入场地
     */
    @ApiOperation("邀请用户加入场地")
    @PostMapping("/{fieldId}/invite")
    public Result<FieldMember> inviteUserToField(@ApiParam("场地ID") @PathVariable Long fieldId,
                                                 @ApiParam("目标用户ID") @RequestParam Long targetUserId) {
        log.info("邀请用户加入场地: fieldId={}, targetUserId={}", fieldId, targetUserId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        FieldMember member = fieldService.inviteUserToField(fieldId, currentUserId, targetUserId);

        return Result.success(member);
    }

    /**
     * 移除场地成员
     */
    @ApiOperation("移除场地成员")
    @DeleteMapping("/{fieldId}/members/{targetUserId}")
    public Result<Void> removeMember(@ApiParam("场地ID") @PathVariable Long fieldId,
                                     @ApiParam("目标用户ID") @PathVariable Long targetUserId) {
        log.info("移除场地成员: fieldId={}, targetUserId={}", fieldId, targetUserId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        fieldService.removeMember(fieldId, currentUserId, targetUserId);

        return Result.success();
    }

    /**
     * 更新成员角色
     */
    @ApiOperation("更新成员角色")
    @PutMapping("/{fieldId}/members/{targetUserId}/role")
    public Result<FieldMember> updateMemberRole(@ApiParam("场地ID") @PathVariable Long fieldId,
                                                @ApiParam("目标用户ID") @PathVariable Long targetUserId,
                                                @ApiParam("角色") @RequestParam String role) {
        log.info("更新成员角色: fieldId={}, targetUserId={}, role={}", fieldId, targetUserId, role);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        FieldMember member = fieldService.updateMemberRole(fieldId, currentUserId, targetUserId, role);

        return Result.success(member);
    }

    /**
     * 获取场地成员列表
     */
    @ApiOperation("获取场地成员列表")
    @GetMapping("/{fieldId}/members")
    public Result<IPage<FieldMember>> getFieldMembers(@ApiParam("场地ID") @PathVariable Long fieldId,
                                                      @ApiParam("页码") @RequestParam(defaultValue = "1") Integer page,
                                                      @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer size,
                                                      @ApiParam("角色筛选") @RequestParam(required = false) String role) {
        log.info("获取场地成员列表: fieldId={}, page={}, size={}, role={}", fieldId, page, size, role);

        IPage<FieldMember> result = fieldService.getFieldMembers(fieldId, page, size, role);

        return Result.success(result);
    }

    /**
     * 获取场地成员数量
     */
    @ApiOperation("获取场地成员数量")
    @GetMapping("/{fieldId}/members/count")
    public Result<Integer> getFieldMemberCount(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("获取场地成员数量: fieldId={}", fieldId);

        Integer count = fieldService.getFieldMemberCount(fieldId);

        return Result.success(count);
    }

    /**
     * 获取用户在场地中的信息
     */
    @ApiOperation("获取用户在场地中的信息")
    @GetMapping("/{fieldId}/member-info")
    public Result<FieldMember> getUserFieldInfo(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("获取用户在场地中的信息: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        FieldMember member = fieldService.getUserFieldInfo(fieldId, currentUserId);

        return Result.success(member);
    }

    // ==================== 权限验证 ====================

    /**
     * 检查用户是否是场地成员
     */
    @ApiOperation("检查用户是否是场地成员")
    @GetMapping("/{fieldId}/is-member")
    public Result<Boolean> isFieldMember(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("检查用户是否是场地成员: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isMember = fieldService.isFieldMember(fieldId, currentUserId);

        return Result.success(isMember);
    }

    /**
     * 检查用户是否是场地管理员
     */
    @ApiOperation("检查用户是否是场地管理员")
    @GetMapping("/{fieldId}/is-admin")
    public Result<Boolean> isFieldAdmin(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("检查用户是否是场地管理员: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isAdmin = fieldService.isFieldAdmin(fieldId, currentUserId);

        return Result.success(isAdmin);
    }

    /**
     * 检查用户是否是场地创建者
     */
    @ApiOperation("检查用户是否是场地创建者")
    @GetMapping("/{fieldId}/is-creator")
    public Result<Boolean> isFieldCreator(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("检查用户是否是场地创建者: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean isCreator = fieldService.isFieldCreator(fieldId, currentUserId);

        return Result.success(isCreator);
    }

    /**
     * 检查用户是否有管理权限
     */
    @ApiOperation("检查用户是否有管理权限")
    @GetMapping("/{fieldId}/can-manage")
    public Result<Boolean> hasManagePermission(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("检查用户是否有管理权限: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean hasPermission = fieldService.hasManagePermission(fieldId, currentUserId);

        return Result.success(hasPermission);
    }

    /**
     * 检查场地是否存在
     */
    @ApiOperation("检查场地是否存在")
    @GetMapping("/{fieldId}/exists")
    public Result<Boolean> fieldExists(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("检查场地是否存在: fieldId={}", fieldId);

        boolean exists = fieldService.fieldExists(fieldId);

        return Result.success(exists);
    }

    /**
     * 检查用户是否可以加入场地
     */
    @ApiOperation("检查用户是否可以加入场地")
    @GetMapping("/{fieldId}/can-join")
    public Result<Boolean> canJoinField(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("检查用户是否可以加入场地: fieldId={}", fieldId);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean canJoin = fieldService.canJoinField(fieldId, currentUserId);

        return Result.success(canJoin);
    }

    // ==================== 统计信息 ====================

    /**
     * 获取场地统计信息
     */
    @ApiOperation("获取场地统计信息")
    @GetMapping("/{fieldId}/stats")
    public Result<FieldService.FieldStats> getFieldStats(@ApiParam("场地ID") @PathVariable Long fieldId) {
        log.info("获取场地统计信息: fieldId={}", fieldId);

        FieldService.FieldStats stats = fieldService.getFieldStats(fieldId);

        return Result.success(stats);
    }

    /**
     * 获取用户场地统计
     */
    @ApiOperation("获取用户场地统计")
    @GetMapping("/user-stats")
    public Result<FieldService.UserFieldStats> getUserFieldStats() {
        log.info("获取用户场地统计");

        Long currentUserId = SecurityUtils.getCurrentUserId();
        FieldService.UserFieldStats stats = fieldService.getUserFieldStats(currentUserId);

        return Result.success(stats);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量获取场地信息
     */
    @ApiOperation("批量获取场地信息")
    @PostMapping("/batch")
    public Result<List<Field>> getFieldsByIds(@ApiParam("场地ID列表") @RequestBody List<Long> fieldIds) {
        log.info("批量获取场地信息: fieldIds={}", fieldIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Field> fields = fieldService.getFieldsByIds(fieldIds, currentUserId);

        return Result.success(fields);
    }

    /**
     * 批量获取场地成员信息
     */
    @ApiOperation("批量获取场地成员信息")
    @PostMapping("/members/batch")
    public Result<List<FieldMember>> getMembersByFieldIds(@ApiParam("场地ID列表") @RequestBody List<Long> fieldIds) {
        log.info("批量获取场地成员信息: fieldIds={}", fieldIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<FieldMember> members = fieldService.getMembersByFieldIds(fieldIds, currentUserId);

        return Result.success(members);
    }

    /**
     * 批量检查用户权限
     */
    @ApiOperation("批量检查用户权限")
    @PostMapping("/batch-check-permission")
    public Result<Boolean> batchCheckManagePermission(@ApiParam("场地ID列表") @RequestBody List<Long> fieldIds) {
        log.info("批量检查用户权限: fieldIds={}", fieldIds);

        Long currentUserId = SecurityUtils.getCurrentUserId();
        boolean hasPermission = fieldService.batchCheckManagePermission(fieldIds, currentUserId);

        return Result.success(hasPermission);
    }

    // ==================== 业务查询 ====================

    /**
     * 获取场地内容统计
     */
    @ApiOperation("获取场地内容统计")
    @PostMapping("/content-stats")
    public Result<List<FieldService.FieldContentStats>> getFieldContentStats(@ApiParam("场地ID列表") @RequestBody List<Long> fieldIds) {
        log.info("获取场地内容统计: fieldIds={}", fieldIds);

        List<FieldService.FieldContentStats> stats = fieldService.getFieldContentStats(fieldIds);

        return Result.success(stats);
    }
}
