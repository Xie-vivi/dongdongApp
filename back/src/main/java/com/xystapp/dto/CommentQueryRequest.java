package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 评论查询请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("评论查询请求")
public class CommentQueryRequest {

    @ApiModelProperty(value = "帖子ID")
    private Long postId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "@用户ID")
    private Long atUserId;

    @ApiModelProperty(value = "回复评论ID")
    private Long replyToId;

    @ApiModelProperty(value = "关键词搜索")
    private String keyword;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小", example = "20")
    private Integer size = 20;

    @ApiModelProperty(value = "排序字段", example = "created_at")
    private String sortBy = "created_at";

    @ApiModelProperty(value = "排序方向", example = "desc")
    private String sortOrder = "desc";

    // ==================== Getter和Setter方法 ====================

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getAtUserId() { return atUserId; }
    public void setAtUserId(Long atUserId) { this.atUserId = atUserId; }

    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}
