package com.xystapp.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户关注关系实体类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_follows")
public class UserFollow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("follower_id")
    private Long followerId;

    @TableField("following_id")
    private Long followingId;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    // 关联字段（不存储在数据库中）
    @TableField(exist = false)
    private String followerUsername;

    @TableField(exist = false)
    private String followerNickname;

    @TableField(exist = false)
    private String followerAvatar;

    @TableField(exist = false)
    private String followingUsername;

    @TableField(exist = false)
    private String followingNickname;

    @TableField(exist = false)
    private String followingAvatar;

    @TableField(exist = false)
    private Boolean isFollowingEachOther; // 是否互相关注

    // ==================== Getter和Setter方法 ====================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }

    public Long getFollowingId() { return followingId; }
    public void setFollowingId(Long followingId) { this.followingId = followingId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getFollowerUsername() { return followerUsername; }
    public void setFollowerUsername(String followerUsername) { this.followerUsername = followerUsername; }

    public String getFollowerNickname() { return followerNickname; }
    public void setFollowerNickname(String followerNickname) { this.followerNickname = followerNickname; }

    public String getFollowerAvatar() { return followerAvatar; }
    public void setFollowerAvatar(String followerAvatar) { this.followerAvatar = followerAvatar; }

    public String getFollowingUsername() { return followingUsername; }
    public void setFollowingUsername(String followingUsername) { this.followingUsername = followingUsername; }

    public String getFollowingNickname() { return followingNickname; }
    public void setFollowingNickname(String followingNickname) { this.followingNickname = followingNickname; }

    public String getFollowingAvatar() { return followingAvatar; }
    public void setFollowingAvatar(String followingAvatar) { this.followingAvatar = followingAvatar; }

    public Boolean getIsFollowingEachOther() { return isFollowingEachOther; }
    public void setIsFollowingEachOther(Boolean isFollowingEachOther) { this.isFollowingEachOther = isFollowingEachOther; }
}
