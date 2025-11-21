package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 帖子评论实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("comments")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("post_id")
    private Long postId;

    @TableField("content")
    private String content;

    @TableField("at_user_id")
    private Long atUserId;

    @TableField("reply_to_id")
    private Long replyToId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 关联字段（不存储在数据库中）
    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String nickname;

    @TableField(exist = false)
    private String avatar;

    @TableField(exist = false)
    private String postTitle;

    @TableField(exist = false)
    private String atUsername;

    @TableField(exist = false)
    private String atNickname;

    @TableField(exist = false)
    private String replyToUsername;

    @TableField(exist = false)
    private String replyToContent;

    @TableField(exist = false)
    private Integer replyDepth; // 回复深度

    @TableField(exist = false)
    private Integer replyCount; // 回复数量

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getAtUserId() { return atUserId; }
    public void setAtUserId(Long atUserId) { this.atUserId = atUserId; }

    public Long getReplyToId() { return replyToId; }
    public void setReplyToId(Long replyToId) { this.replyToId = replyToId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getPostTitle() { return postTitle; }
    public void setPostTitle(String postTitle) { this.postTitle = postTitle; }

    public String getAtUsername() { return atUsername; }
    public void setAtUsername(String atUsername) { this.atUsername = atUsername; }

    public String getAtNickname() { return atNickname; }
    public void setAtNickname(String atNickname) { this.atNickname = atNickname; }

    public String getReplyToUsername() { return replyToUsername; }
    public void setReplyToUsername(String replyToUsername) { this.replyToUsername = replyToUsername; }

    public String getReplyToContent() { return replyToContent; }
    public void setReplyToContent(String replyToContent) { this.replyToContent = replyToContent; }

    public Integer getReplyDepth() { return replyDepth; }
    public void setReplyDepth(Integer replyDepth) { this.replyDepth = replyDepth; }

    public Integer getReplyCount() { return replyCount; }
    public void setReplyCount(Integer replyCount) { this.replyCount = replyCount; }
}
