package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 浏览记录实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@TableName("view_history")
public class ViewHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 项目类型
     */
    private String itemType;

    /**
     * 项目ID
     */
    private Long itemId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    // ==================== 临时字段（不映射到数据库） ====================

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

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String avatar;

    /**
     * 项目标题
     */
    @TableField(exist = false)
    private String itemTitle;

    /**
     * 项目描述
     */
    @TableField(exist = false)
    private String itemDescription;

    /**
     * 项目图片
     */
    @TableField(exist = false)
    private String itemImage;

    /**
     * 项目作者ID
     */
    @TableField(exist = false)
    private Long itemAuthorId;

    /**
     * 项目作者用户名
     */
    @TableField(exist = false)
    private String itemAuthorUsername;

    /**
     * 项目作者昵称
     */
    @TableField(exist = false)
    private String itemAuthorNickname;

    /**
     * 项目状态
     */
    @TableField(exist = false)
    private String itemStatus;

    /**
     * 项目创建时间
     */
    @TableField(exist = false)
    private LocalDateTime itemCreatedAt;

    /**
     * 项目更新时间
     */
    @TableField(exist = false)
    private LocalDateTime itemUpdatedAt;

    /**
     * 浏览次数（统计用）
     */
    @TableField(exist = false)
    private Integer viewCount;

    /**
     * 是否可以删除
     */
    @TableField(exist = false)
    private Boolean canDelete;

    // ==================== 枚举定义 ====================

    /**
     * 项目类型枚举
     */
    public enum ItemType {
        POST("post", "帖子"),
        ACTIVITY("activity", "活动");

        private final String code;
        private final String description;

        ItemType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static ItemType fromCode(String code) {
            for (ItemType type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Unknown item type code: " + code);
        }
    }

    /**
     * 时间范围枚举
     */
    public enum TimeRange {
        TODAY("today", "今天"),
        YESTERDAY("yesterday", "昨天"),
        WEEK("week", "本周"),
        MONTH("month", "本月"),
        YEAR("year", "今年");

        private final String code;
        private final String description;

        TimeRange(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static TimeRange fromCode(String code) {
            for (TimeRange range : values()) {
                if (range.code.equals(code)) {
                    return range;
                }
            }
            throw new IllegalArgumentException("Unknown time range code: " + code);
        }
    }

    /**
     * 排序方式枚举
     */
    public enum SortBy {
        TIME_DESC("time_desc", "时间倒序"),
        TIME_ASC("time_asc", "时间正序"),
        TITLE_ASC("title_asc", "标题正序"),
        TITLE_DESC("title_desc", "标题倒序");

        private final String code;
        private final String description;

        SortBy(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static SortBy fromCode(String code) {
            for (SortBy sort : values()) {
                if (sort.code.equals(code)) {
                    return sort;
                }
            }
            throw new IllegalArgumentException("Unknown sort code: " + code);
        }
    }

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getItemTitle() { return itemTitle; }
    public void setItemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    public String getItemDescription() { return itemDescription; }
    public void setItemDescription(String itemDescription) { this.itemDescription = itemDescription; }

    public String getItemImage() { return itemImage; }
    public void setItemImage(String itemImage) { this.itemImage = itemImage; }

    public Long getItemAuthorId() { return itemAuthorId; }
    public void setItemAuthorId(Long itemAuthorId) { this.itemAuthorId = itemAuthorId; }

    public String getItemAuthorUsername() { return itemAuthorUsername; }
    public void setItemAuthorUsername(String itemAuthorUsername) { this.itemAuthorUsername = itemAuthorUsername; }

    public String getItemAuthorNickname() { return itemAuthorNickname; }
    public void setItemAuthorNickname(String itemAuthorNickname) { this.itemAuthorNickname = itemAuthorNickname; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public String getItemStatus() { return itemStatus; }
    public void setItemStatus(String itemStatus) { this.itemStatus = itemStatus; }

    public LocalDateTime getItemCreatedAt() { return itemCreatedAt; }
    public void setItemCreatedAt(LocalDateTime itemCreatedAt) { this.itemCreatedAt = itemCreatedAt; }

    public LocalDateTime getItemUpdatedAt() { return itemUpdatedAt; }
    public void setItemUpdatedAt(LocalDateTime itemUpdatedAt) { this.itemUpdatedAt = itemUpdatedAt; }

    public Boolean getCanDelete() { return canDelete; }
    public void setCanDelete(Boolean canDelete) { this.canDelete = canDelete; }
}
