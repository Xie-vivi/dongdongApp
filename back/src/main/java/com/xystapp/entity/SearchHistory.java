package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索历史实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("search_history")
public class SearchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("keyword")
    private String keyword;

    @TableField("search_type")
    private String searchType;

    @TableField("result_count")
    private Integer resultCount;

    @TableField("search_ip")
    private String searchIp;

    @TableField("user_agent")
    private String userAgent;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // ==================== 临时字段（不映射到数据库） ====================

    /**
     * 搜索次数
     */
    @TableField(exist = false)
    private Integer searchCount;

    /**
     * 是否为热门搜索
     */
    @TableField(exist = false)
    private Boolean isHot;

    /**
     * 用户名
     */
    @TableField(exist = false)
    private String username;

    /**
     * 用户昵称
     */
    @TableField(exist = false)
    private String nickname;

    // ==================== 枚举定义 ====================

    /**
     * 搜索类型枚举
     */
    public enum SearchType {
        ALL("all", "全部"),
        POST("post", "帖子"),
        ACTIVITY("activity", "活动"),
        USER("user", "用户"),
        FIELD("field", "场地");

        private final String code;
        private final String description;

        SearchType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static SearchType fromCode(String code) {
            for (SearchType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return ALL;
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 获取搜索类型描述
     */
    public String getSearchTypeDescription() {
        return SearchType.fromCode(searchType).getDescription();
    }

    /**
     * 判断是否为热门搜索（搜索次数>10）
     */
    public Boolean getIsHotSearch() {
        return searchCount != null && searchCount > 10;
    }

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public Integer getResultCount() { return resultCount; }
    public void setResultCount(Integer resultCount) { this.resultCount = resultCount; }

    public String getSearchIp() { return searchIp; }
    public void setSearchIp(String searchIp) { this.searchIp = searchIp; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getSearchCount() { return searchCount; }
    public void setSearchCount(Integer searchCount) { this.searchCount = searchCount; }

    public Boolean getIsHot() { return isHot; }
    public void setIsHot(Boolean isHot) { this.isHot = isHot; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
