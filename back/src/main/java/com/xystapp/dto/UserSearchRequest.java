package com.xystapp.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户搜索请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
public class UserSearchRequest {

    @NotBlank(message = "搜索关键词不能为空")
    @Size(max = 50, message = "搜索关键词长度不能超过50个字符")
    private String keyword;

    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量必须大于0")
    private Integer size = 20;

    private String sortBy = "created_at";

    private String sortOrder = "desc";

    // ==================== Getter和Setter方法 ====================

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
