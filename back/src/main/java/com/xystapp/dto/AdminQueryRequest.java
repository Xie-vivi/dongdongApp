package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 管理员查询请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@ApiModel("管理员查询请求")
public class AdminQueryRequest {

    @ApiModelProperty(value = "状态筛选")
    private String status;

    @ApiModelProperty(value = "关键词搜索（用户名、真实姓名、邮箱）")
    private String keyword;

    @ApiModelProperty(value = "角色筛选")
    private String role;

    @ApiModelProperty(value = "开始日期")
    private String startDate;

    @ApiModelProperty(value = "结束日期")
    private String endDate;

    @ApiModelProperty(value = "排序字段")
    private String sortBy = "created_at";

    @ApiModelProperty(value = "排序方向")
    private String sortDirection = "desc";

    // ==================== Getter和Setter方法 ====================

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}
