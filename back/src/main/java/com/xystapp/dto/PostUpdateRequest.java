package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 帖子更新请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("帖子更新请求")
public class PostUpdateRequest {

    @ApiModelProperty(value = "帖子标题", required = true)
    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 200, message = "帖子标题长度不能超过200个字符")
    private String title;

    @ApiModelProperty(value = "帖子副标题")
    @Size(max = 500, message = "帖子副标题长度不能超过500个字符")
    private String subtitle;

    @ApiModelProperty(value = "帖子内容", required = true)
    @NotBlank(message = "帖子内容不能为空")
    @Size(max = 10000, message = "帖子内容长度不能超过10000个字符")
    private String content;

    @ApiModelProperty(value = "帖子图片列表")
    private List<String> images;

    @ApiModelProperty(value = "帖子标签")
    @Size(max = 50, message = "标签长度不能超过50个字符")
    private String tag;

    @ApiModelProperty(value = "帖子状态", allowableValues = "draft,published")
    private String status;

    // ==================== Getter和Setter方法 ====================

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
