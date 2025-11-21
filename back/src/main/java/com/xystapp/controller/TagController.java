package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.common.api.ApiResult;
import com.xystapp.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 标签管理控制器
 * 
 * @author XYST
 * @since 2025-11-16
 */
@Tag(name = "标签管理", description = "标签相关API")
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "分页查询标签", description = "分页查询所有启用的标签")
    @GetMapping("/page")
    public ApiResult<IPage<com.xystapp.entity.Tag>> getTagsPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer size) {
        
        Page<com.xystapp.entity.Tag> page = new Page<>(current, size);
        IPage<com.xystapp.entity.Tag> result = tagService.getActiveTagsPage(page);
        return ApiResult.success(result);
    }

    @Operation(summary = "获取所有标签", description = "获取所有启用的标签列表")
    @GetMapping
    public ApiResult<List<com.xystapp.entity.Tag>> getAllTags() {
        List<com.xystapp.entity.Tag> tags = tagService.lambdaQuery()
                .eq(com.xystapp.entity.Tag::getIsActive, true)
                .orderByAsc(com.xystapp.entity.Tag::getSortOrder)
                .list();
        return ApiResult.success(tags);
    }

    @Operation(summary = "获取热门标签", description = "获取使用频率最高的标签")
    @GetMapping("/popular")
    public ApiResult<List<com.xystapp.entity.Tag>> getPopularTags(
            @Parameter(description = "返回数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        List<com.xystapp.entity.Tag> tags = tagService.getPopularTags(limit);
        return ApiResult.success(tags);
    }

    @Operation(summary = "获取标签使用统计", description = "获取所有标签及其使用次数")
    @GetMapping("/statistics")
    public ApiResult<List<com.xystapp.entity.Tag>> getTagStatistics() {
        List<com.xystapp.entity.Tag> tags = tagService.getTagsWithUsageCount();
        return ApiResult.success(tags);
    }

    @Operation(summary = "根据场地ID查询标签", description = "获取指定场地的所有标签")
    @GetMapping("/field/{fieldId}")
    public ApiResult<List<com.xystapp.entity.Tag>> getTagsByFieldId(
            @Parameter(description = "场地ID") @PathVariable Long fieldId) {
        List<com.xystapp.entity.Tag> tags = tagService.getTagsByFieldId(fieldId);
        return ApiResult.success(tags);
    }

    @Operation(summary = "根据标签ID查询场地", description = "获取指定标签下的所有场地ID")
    @GetMapping("/{tagId}/fields")
    public ApiResult<List<Long>> getFieldIdsByTagId(
            @Parameter(description = "标签ID") @PathVariable Long tagId) {
        List<Long> fieldIds = tagService.getFieldIdsByTagId(tagId);
        return ApiResult.success(fieldIds);
    }

    @Operation(summary = "根据ID查询标签", description = "根据标签ID获取标签详情")
    @GetMapping("/{id}")
    public ApiResult<com.xystapp.entity.Tag> getTagById(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        com.xystapp.entity.Tag tag = tagService.getById(id);
        if (tag == null || !tag.getIsActive()) {
            return ApiResult.error("标签不存在");
        }
        return ApiResult.success(tag);
    }

    @Operation(summary = "根据名称查询标签", description = "根据标签名称获取标签详情")
    @GetMapping("/name/{name}")
    public ApiResult<com.xystapp.entity.Tag> getTagByName(
            @Parameter(description = "标签名称") @PathVariable String name) {
        com.xystapp.entity.Tag tag = tagService.getByName(name);
        if (tag == null || !tag.getIsActive()) {
            return ApiResult.error("标签不存在");
        }
        return ApiResult.success(tag);
    }

    @Operation(summary = "创建标签", description = "创建新的标签")
    @PostMapping
    @PreAuthorize("hasAuthority('tag:create')")
    public ApiResult<com.xystapp.entity.Tag> createTag(@Valid @RequestBody com.xystapp.entity.Tag tag) {
        try {
            com.xystapp.entity.Tag createdTag = tagService.createTag(tag);
            return ApiResult.success(createdTag);
        } catch (IllegalArgumentException e) {
            return ApiResult.error(e.getMessage());
        } catch (Exception e) {
            return ApiResult.error("创建标签失败");
        }
    }

    @Operation(summary = "更新标签", description = "更新标签信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('tag:update')")
    public ApiResult<Boolean> updateTag(
            @Parameter(description = "标签ID") @PathVariable Long id,
            @Valid @RequestBody com.xystapp.entity.Tag tag) {
        try {
            boolean success = tagService.updateTag(id, tag);
            return success ? ApiResult.success(true) : ApiResult.error("更新标签失败");
        } catch (IllegalArgumentException e) {
            return ApiResult.error(e.getMessage());
        } catch (Exception e) {
            return ApiResult.error("更新标签失败");
        }
    }

    @Operation(summary = "删除标签", description = "软删除标签")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('tag:delete')")
    public ApiResult<Boolean> deleteTag(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        boolean success = tagService.deleteTag(id);
        return success ? ApiResult.success(true) : ApiResult.error("删除标签失败");
    }

    @Operation(summary = "为场地分配标签", description = "为指定场地分配标签")
    @PostMapping("/field/{fieldId}/assign")
    @PreAuthorize("hasAuthority('field:tag:assign')")
    public ApiResult<Boolean> assignTagsToField(
            @Parameter(description = "场地ID") @PathVariable Long fieldId,
            @RequestBody List<Long> tagIds) {
        try {
            boolean success = tagService.assignTagsToField(fieldId, tagIds);
            return success ? ApiResult.success(true) : ApiResult.error("分配标签失败");
        } catch (IllegalArgumentException e) {
            return ApiResult.error(e.getMessage());
        } catch (Exception e) {
            return ApiResult.error("分配标签失败");
        }
    }

    @Operation(summary = "移除场地标签", description = "从指定场地移除标签")
    @DeleteMapping("/field/{fieldId}/remove")
    @PreAuthorize("hasAuthority('field:tag:remove')")
    public ApiResult<Boolean> removeTagsFromField(
            @Parameter(description = "场地ID") @PathVariable Long fieldId,
            @RequestBody List<Long> tagIds) {
        boolean success = tagService.removeTagsFromField(fieldId, tagIds);
        return success ? ApiResult.success(true) : ApiResult.error("移除标签失败");
    }
}
