package com.xystapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 系统配置更新请求DTO
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Data
@ApiModel("系统配置更新请求")
public class SystemConfigUpdateRequest {

    @ApiModelProperty(value = "配置键", required = true, example = "site.name")
    @NotBlank(message = "配置键不能为空")
    @Size(max = 100, message = "配置键长度不能超过100个字符")
    private String configKey;

    @ApiModelProperty(value = "配置值", required = true, example = "XYST社交平台")
    @NotBlank(message = "配置值不能为空")
    @Size(max = 1000, message = "配置值长度不能超过1000个字符")
    private String configValue;

    @ApiModelProperty(value = "配置类型", required = true, example = "system")
    @NotBlank(message = "配置类型不能为空")
    @Size(max = 50, message = "配置类型长度不能超过50个字符")
    private String configType;

    @ApiModelProperty(value = "配置描述", example = "网站名称")
    @Size(max = 500, message = "配置描述长度不能超过500个字符")
    private String description;

    @ApiModelProperty(value = "是否启用", required = true, example = "true")
    @NotNull(message = "是否启用不能为空")
    private Boolean isEnabled;

    // ==================== Getter和Setter方法 ====================

    public String getConfigKey() { return configKey; }
    public void setConfigKey(String configKey) { this.configKey = configKey; }

    public String getConfigValue() { return configValue; }
    public void setConfigValue(String configValue) { this.configValue = configValue; }

    public String getConfigType() { return configType; }
    public void setConfigType(String configType) { this.configType = configType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
}
