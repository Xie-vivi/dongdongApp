package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xystapp.entity.FieldTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 场地标签关联Mapper接口
 * 
 * @author XYST
 * @since 2025-11-16
 */
@Mapper
public interface FieldTagMapper extends BaseMapper<FieldTag> {

    /**
     * 根据场地ID查询标签关联
     */
    List<FieldTag> selectByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 根据标签ID查询场地关联
     */
    List<FieldTag> selectByTagId(@Param("tagId") Long tagId);

    /**
     * 删除场地的所有标签关联
     */
    int deleteByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 删除标签的所有场地关联
     */
    int deleteByTagId(@Param("tagId") Long tagId);

    /**
     * 批量插入场地标签关联
     */
    int batchInsert(@Param("fieldTags") List<FieldTag> fieldTags);

    /**
     * 根据标签ID查询场地列表（带场地信息）
     */
    List<FieldTag> selectFieldsWithTagInfo(@Param("tagId") Long tagId);
}
