package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 管理员更新请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@ApiModel("管理员更新请求")
public class AdminUpdateRequest {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    @ApiModelProperty(value = "邮箱", required = true)
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "手机号")
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phone;

    @ApiModelProperty(value = "真实姓名")
    @Size(max = 100, message = "真实姓名长度不能超过100个字符")
    private String realName;

    @ApiModelProperty(value = "头像URL")
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;

    @ApiModelProperty(value = "角色ID列表")
    private List<Long> roleIds;

    @ApiModelProperty(value = "备注")
    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

    // ==================== Getter和Setter方法 ====================

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public List<Long> getRoleIds() { return roleIds; }
    public void setRoleIds(List<Long> roleIds) { this.roleIds = roleIds; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
