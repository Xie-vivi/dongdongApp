package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.time.LocalDate;

/**
 * 活动查询请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("活动查询请求")
public class ActivityQueryRequest {

    @ApiModelProperty(value = "搜索关键词")
    private String keyword;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "活动标签")
    private String tag;

    @ApiModelProperty(value = "活动地点")
    private String location;

    @ApiModelProperty(value = "活动状态", allowableValues = "draft,published,cancelled")
    private String status;

    @ApiModelProperty(value = "开始日期")
    private LocalDate startDate;

    @ApiModelProperty(value = "结束日期")
    private LocalDate endDate;

    @ApiModelProperty(value = "是否显示我报名的活动")
    private Boolean mySignedUp;

    @ApiModelProperty(value = "是否显示即将开始的活动")
    private Boolean upcoming;

    @ApiModelProperty(value = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小", example = "20")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 20;

    @ApiModelProperty(value = "排序字段", allowableValues = "created_at,updated_at,date,likes_count,participants")
    private String sortBy = "date";

    @ApiModelProperty(value = "排序方向", allowableValues = "asc,desc")
    private String sortOrder = "asc";

    // ==================== Getter和Setter方法 ====================

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getMySignedUp() { return mySignedUp; }
    public void setMySignedUp(Boolean mySignedUp) { this.mySignedUp = mySignedUp; }

    public Boolean getUpcoming() { return upcoming; }
    public void setUpcoming(Boolean upcoming) { this.upcoming = upcoming; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
}
