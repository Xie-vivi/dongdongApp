package com.xystapp.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * 发送验证码请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
public class SendVerificationCodeRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @Pattern(regexp = "^(email|phone)$", message = "验证方式只能是email或phone")
    private String type = "email";

    // ==================== Getter和Setter方法 ====================

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
