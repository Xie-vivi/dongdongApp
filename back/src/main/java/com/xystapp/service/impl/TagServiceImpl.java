package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xystapp.entity.FieldTag;
import com.xystapp.entity.Tag;
import com.xystapp.mapper.FieldTagMapper;
import com.xystapp.mapper.TagMapper;
import com.xystapp.service.TagService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 * 
 * @author XYST
 * @since 2025-11-16
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    private static final Logger log = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagMapper tagMapper;
    private final FieldTagMapper fieldTagMapper;

    @Override
    public IPage<Tag> getActiveTagsPage(Page<Tag> page) {
        return tagMapper.selectActiveTagsPage(page);
    }

    @Override
    public List<Tag> getTagsByFieldId(Long fieldId) {
        if (fieldId == null) {
            return new ArrayList<>();
        }
        return tagMapper.selectTagsByFieldId(fieldId);
    }

    @Override
    public Tag getByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return tagMapper.selectByName(name.trim());
    }

    @Override
    public List<Tag> getTagsWithUsageCount() {
        return tagMapper.selectTagsWithUsageCount();
    }

    @Override
    public List<Tag> getPopularTags(Integer limit) {
        return tagMapper.selectPopularTags(limit != null ? limit : 10);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag createTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("标签信息不能为空");
        }
        
        if (tag.getName() == null || tag.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("标签名称不能为空");
        }

        // 检查名称是否已存在
        Tag existingTag = getByName(tag.getName());
        if (existingTag != null) {
            throw new IllegalArgumentException("标签名称已存在: " + tag.getName());
        }

        // 设置默认值
        tag.setIsActive(true);
        tag.setSortOrder(tag.getSortOrder() != null ? tag.getSortOrder() : 0);
        
        boolean success = save(tag);
        if (!success) {
            throw new RuntimeException("创建标签失败");
        }

        log.info("创建标签成功: {}", tag.getName());
        return tag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTag(Long id, Tag tag) {
        if (id == null) {
            throw new IllegalArgumentException("标签ID不能为空");
        }
        
        Tag existingTag = getById(id);
        if (existingTag == null) {
            throw new IllegalArgumentException("标签不存在");
        }

        // 如果修改了名称，检查新名称是否已存在
        if (tag.getName() != null && !tag.getName().equals(existingTag.getName())) {
            Tag nameCheck = getByName(tag.getName());
            if (nameCheck != null && !nameCheck.getId().equals(id)) {
                throw new IllegalArgumentException("标签名称已存在: " + tag.getName());
            }
        }

        // 更新字段
        if (tag.getName() != null) {
            existingTag.setName(tag.getName());
        }
        if (tag.getColor() != null) {
            existingTag.setColor(tag.getColor());
        }
        if (tag.getDescription() != null) {
            existingTag.setDescription(tag.getDescription());
        }
        if (tag.getSortOrder() != null) {
            existingTag.setSortOrder(tag.getSortOrder());
        }
        if (tag.getIsActive() != null) {
            existingTag.setIsActive(tag.getIsActive());
        }

        boolean success = updateById(existingTag);
        if (success) {
            log.info("更新标签成功: {}", existingTag.getName());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTag(Long id) {
        if (id == null) {
            return false;
        }

        Tag tag = getById(id);
        if (tag == null) {
            return false;
        }

        // 删除相关的场地标签关联
        fieldTagMapper.deleteByTagId(id);

        // 软删除标签
        tag.setIsActive(false);
        boolean success = updateById(tag);
        
        if (success) {
            log.info("删除标签成功: {}", tag.getName());
        }
        return success;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignTagsToField(Long fieldId, List<Long> tagIds) {
        if (fieldId == null) {
            throw new IllegalArgumentException("场地ID不能为空");
        }

        if (CollectionUtils.isEmpty(tagIds)) {
            // 清空所有标签
            fieldTagMapper.deleteByFieldId(fieldId);
            return true;
        }

        // 删除现有关联
        fieldTagMapper.deleteByFieldId(fieldId);

        // 创建新关联
        List<FieldTag> fieldTags = tagIds.stream()
                .map(tagId -> {
                    FieldTag fieldTag = new FieldTag();
                    fieldTag.setFieldId(fieldId);
                    fieldTag.setTagId(tagId);
                    fieldTag.setCreatedAt(LocalDateTime.now());
                    return fieldTag;
                })
                .collect(Collectors.toList());

        int inserted = fieldTagMapper.batchInsert(fieldTags);
        log.info("为场地 {} 分配了 {} 个标签", fieldId, inserted);
        return inserted > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTagsFromField(Long fieldId, List<Long> tagIds) {
        if (fieldId == null || CollectionUtils.isEmpty(tagIds)) {
            return false;
        }

        LambdaQueryWrapper<FieldTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FieldTag::getFieldId, fieldId)
               .in(FieldTag::getTagId, tagIds);

        int deleted = fieldTagMapper.delete(wrapper);
        log.info("从场地 {} 移除了 {} 个标签关联", fieldId, deleted);
        return deleted > 0;
    }

    @Override
    public List<Long> getFieldIdsByTagId(Long tagId) {
        if (tagId == null) {
            return new ArrayList<>();
        }

        List<FieldTag> fieldTags = fieldTagMapper.selectByTagId(tagId);
        return fieldTags.stream()
                .map(FieldTag::getFieldId)
                .collect(Collectors.toList());
    }
}
