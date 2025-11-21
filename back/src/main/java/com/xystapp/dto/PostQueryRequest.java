package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 帖子查询请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("帖子查询请求")
public class PostQueryRequest {

    @ApiModelProperty(value = "搜索关键词")
    private String keyword;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "帖子标签")
    private String tag;

    @ApiModelProperty(value = "帖子状态", allowableValues = "draft,published")
    private String status;

    @ApiModelProperty(value = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小", example = "20")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 20;

    @ApiModelProperty(value = "排序字段", allowableValues = "created_at,updated_at,likes_count,comments_count")
    private String sortBy = "created_at";

    @ApiModelProperty(value = "排序方向", allowableValues = "asc,desc")
    private String sortOrder = "desc";

    // ==================== Getter和Setter方法 ====================

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}
