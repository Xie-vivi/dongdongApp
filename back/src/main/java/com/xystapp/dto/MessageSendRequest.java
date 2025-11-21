package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 发送消息请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("发送消息请求")
public class MessageSendRequest {

    @ApiModelProperty(value = "聊天ID", required = true)
    @NotNull(message = "聊天ID不能为空")
    private Long chatId;

    @ApiModelProperty(value = "消息内容", required = true)
    @NotBlank(message = "消息内容不能为空")
    private String content;

    @ApiModelProperty(value = "消息类型", required = true)
    @NotBlank(message = "消息类型不能为空")
    private String contentType; // text, image

    // ==================== Getter和Setter方法 ====================

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
}
