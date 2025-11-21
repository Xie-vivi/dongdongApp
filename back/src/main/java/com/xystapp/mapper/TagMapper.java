package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 标签Mapper接口
 * 
 * @author XYST
 * @since 2025-11-16
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 分页查询启用的标签
     */
    IPage<Tag> selectActiveTagsPage(Page<Tag> page);

    /**
     * 根据场地ID查询标签列表
     */
    List<Tag> selectTagsByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 根据名称查询标签
     */
    Tag selectByName(@Param("name") String name);

    /**
     * 获取标签使用统计（被多少场地使用）
     */
    List<Tag> selectTagsWithUsageCount();

    /**
     * 查询热门标签（按使用次数排序）
     */
    List<Tag> selectPopularTags(@Param("limit") Integer limit);
}
