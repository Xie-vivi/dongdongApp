package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xystapp.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 * 
 * @author XYST
 * @since 2025-11-16
 */
public interface TagService extends IService<Tag> {

    /**
     * 分页查询启用的标签
     */
    IPage<Tag> getActiveTagsPage(Page<Tag> page);

    /**
     * 根据场地ID查询标签列表
     */
    List<Tag> getTagsByFieldId(Long fieldId);

    /**
     * 根据名称查询标签
     */
    Tag getByName(String name);

    /**
     * 获取标签使用统计
     */
    List<Tag> getTagsWithUsageCount();

    /**
     * 查询热门标签
     */
    List<Tag> getPopularTags(Integer limit);

    /**
     * 创建标签
     */
    Tag createTag(Tag tag);

    /**
     * 更新标签
     */
    boolean updateTag(Long id, Tag tag);

    /**
     * 删除标签（软删除）
     */
    boolean deleteTag(Long id);

    /**
     * 为场地分配标签
     */
    boolean assignTagsToField(Long fieldId, List<Long> tagIds);

    /**
     * 移除场地标签
     */
    boolean removeTagsFromField(Long fieldId, List<Long> tagIds);

    /**
     * 根据标签ID查询场地列表
     */
    List<Long> getFieldIdsByTagId(Long tagId);
}
