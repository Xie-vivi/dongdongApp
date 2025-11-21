package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 创建评论请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("创建评论请求")
public class CommentCreateRequest {

    @ApiModelProperty(value = "帖子ID", required = true)
    @NotNull(message = "帖子ID不能为空")
    private Long postId;

    @ApiModelProperty(value = "评论内容", required = true)
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000个字符")
    private String content;

    @ApiModelProperty(value = "@用户ID")
    private Long atUserId;

    @ApiModelProperty(value = "回复评论ID")
    private Long replyToId;

    // ==================== Getter和Setter方法 ====================

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAtUserId() {
        return atUserId;
    }

    public void setAtUserId(Long atUserId) {
        this.atUserId = atUserId;
    }

    public Long getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(Long replyToId) {
        this.replyToId = replyToId;
    }
}
