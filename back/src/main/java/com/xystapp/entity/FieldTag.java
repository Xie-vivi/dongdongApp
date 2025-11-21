package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 场地标签关联实体类
 * 
 * @author XYST
 * @since 2025-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("field_tags")
@Schema(description = "场地标签关联实体")
public class FieldTag implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "关联ID")
    private Long id;

    @TableField("field_id")
    @Schema(description = "场地ID")
    private Long fieldId;

    @TableField("tag_id")
    @Schema(description = "标签ID")
    private Long tagId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    // 关联对象（用于查询结果）
    @TableField(exist = false)
    @Schema(description = "标签信息")
    private Tag tag;

    @TableField(exist = false)
    @Schema(description = "场地信息")
    private Field field;

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFieldId() { return fieldId; }
    public void setFieldId(Long fieldId) { this.fieldId = fieldId; }

    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) { this.tag = tag; }

    public Field getField() { return field; }
    public void setField(Field field) { this.field = field; }
}
