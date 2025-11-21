package com.xystapp.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.common.Result;
import com.xystapp.dto.SystemConfigCreateRequest;
import com.xystapp.dto.SystemConfigUpdateRequest;
import com.xystapp.entity.SystemConfig;
import com.xystapp.service.SystemConfigService;
import com.xystapp.utils.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 系统配置管理控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "系统配置管理")
@RestController
@RequestMapping("/system/configs")
@Validated
public class SystemConfigController {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigController.class);

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 创建系统配置
     */
    @ApiOperation("创建系统配置")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SystemConfig> createConfig(@Valid @RequestBody SystemConfigCreateRequest request) {
        log.info("创建系统配置请求: key={}, type={}", request.getConfigKey(), request.getConfigType());
        
        Long operatorId = SecurityUtils.getCurrentUserId();
        SystemConfig config = systemConfigService.createConfig(request, operatorId);
        
        return Result.success(config);
    }

    /**
     * 更新系统配置
     */
    @ApiOperation("更新系统配置")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SystemConfig> updateConfig(
            @ApiParam("配置ID") @PathVariable Long id,
            @Valid @RequestBody SystemConfigUpdateRequest request) {
        log.info("更新系统配置请求: id={}, key={}", id, request.getConfigKey());
        
        Long operatorId = SecurityUtils.getCurrentUserId();
        SystemConfig config = systemConfigService.updateConfig(id, request, operatorId);
        
        return Result.success(config);
    }

    /**
     * 删除系统配置
     */
    @ApiOperation("删除系统配置")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteConfig(@ApiParam("配置ID") @PathVariable Long id) {
        log.info("删除系统配置请求: id={}", id);
        
        Long operatorId = SecurityUtils.getCurrentUserId();
        systemConfigService.deleteConfig(id, operatorId);
        
        return Result.success();
    }

    /**
     * 获取配置详情
     */
    @ApiOperation("获取配置详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SystemConfig> getConfigById(@ApiParam("配置ID") @PathVariable Long id) {
        SystemConfig config = systemConfigService.getConfigById(id);
        if (config == null) {
            return Result.error("配置不存在");
        }
        return Result.success(config);
    }

    /**
     * 分页查询配置
     */
    @ApiOperation("分页查询配置")
    @PostMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<IPage<SystemConfig>> getConfigsWithPagination(@RequestBody SystemConfigQueryRequest request) {
        log.info("分页查询系统配置: page={}, size={}, key={}, type={}", 
                request.getPage(), request.getSize(), request.getConfigKey(), request.getConfigType());
        
        Page<SystemConfig> page = new Page<>(request.getPage(), request.getSize());
        IPage<SystemConfig> result = systemConfigService.getConfigsWithPagination(
                page, request.getConfigKey(), request.getConfigType());
        
        return Result.success(result);
    }

    /**
     * 根据配置键获取配置值
     */
    @ApiOperation("获取配置值")
    @GetMapping("/value/{configKey}")
    public Result<String> getConfigValue(@ApiParam("配置键") @PathVariable String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        if (value == null) {
            return Result.error("配置不存在或已禁用");
        }
        return Result.success(value);
    }

    /**
     * 根据配置键获取配置详情
     */
    @ApiOperation("获取配置详情")
    @GetMapping("/key/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SystemConfig> getConfigByKey(@ApiParam("配置键") @PathVariable String configKey) {
        SystemConfig config = systemConfigService.getConfigByKey(configKey);
        if (config == null) {
            return Result.error("配置不存在");
        }
        return Result.success(config);
    }

    /**
     * 获取指定类型的配置列表
     */
    @ApiOperation("获取指定类型配置列表")
    @GetMapping("/type/{configType}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<SystemConfig>> getConfigsByType(@ApiParam("配置类型") @PathVariable String configType) {
        List<SystemConfig> configs = systemConfigService.getConfigsByType(configType);
        return Result.success(configs);
    }

    /**
     * 获取所有启用的配置
     */
    @ApiOperation("获取启用配置列表")
    @GetMapping("/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<SystemConfig>> getEnabledConfigs() {
        List<SystemConfig> configs = systemConfigService.getEnabledConfigs();
        return Result.success(configs);
    }

    /**
     * 获取配置映射（key-value）
     */
    @ApiOperation("获取配置映射")
    @GetMapping("/map")
    public Result<Map<String, String>> getConfigMap() {
        Map<String, String> configMap = systemConfigService.getConfigMap();
        return Result.success(configMap);
    }

    /**
     * 获取指定类型的配置映射
     */
    @ApiOperation("获取指定类型配置映射")
    @GetMapping("/map/{configType}")
    public Result<Map<String, String>> getConfigMapByType(@ApiParam("配置类型") @PathVariable String configType) {
        Map<String, String> configMap = systemConfigService.getConfigMapByType(configType);
        return Result.success(configMap);
    }

    /**
     * 批量启用/禁用配置
     */
    @ApiOperation("批量更新配置状态")
    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchUpdateStatus(@RequestBody SystemConfigStatusUpdateRequest request) {
        log.info("批量更新配置状态: ids={}, enabled={}", request.getIds(), request.getIsEnabled());
        
        Long operatorId = SecurityUtils.getCurrentUserId();
        systemConfigService.batchUpdateStatus(request.getIds(), request.getIsEnabled(), operatorId);
        
        return Result.success();
    }

    /**
     * 检查配置键是否可用
     */
    @ApiOperation("检查配置键可用性")
    @GetMapping("/check/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> checkConfigKeyAvailable(
            @ApiParam("配置键") @PathVariable String configKey,
            @ApiParam("排除的配置ID") @RequestParam(required = false) Long excludeId) {
        boolean available = systemConfigService.isConfigKeyAvailable(configKey, excludeId);
        return Result.success(available);
    }

    /**
     * 刷新配置缓存
     */
    @ApiOperation("刷新配置缓存")
    @PostMapping("/refresh-cache")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> refreshConfigCache() {
        systemConfigService.refreshConfigCache();
        return Result.success();
    }

    /**
     * 获取配置统计信息
     */
    @ApiOperation("获取配置统计")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getConfigStatistics() {
        Map<String, Object> statistics = systemConfigService.getConfigStatistics();
        return Result.success(statistics);
    }

    /**
     * 获取类型化配置值
     */
    @ApiOperation("获取布尔配置值")
    @GetMapping("/boolean/{configKey}")
    public Result<Boolean> getBooleanConfig(
            @ApiParam("配置键") @PathVariable String configKey,
            @ApiParam("默认值") @RequestParam(required = false) Boolean defaultValue) {
        Boolean value = systemConfigService.getBooleanConfig(configKey, defaultValue);
        return Result.success(value);
    }

    @ApiOperation("获取整数配置值")
    @GetMapping("/integer/{configKey}")
    public Result<Integer> getIntegerConfig(
            @ApiParam("配置键") @PathVariable String configKey,
            @ApiParam("默认值") @RequestParam(required = false) Integer defaultValue) {
        Integer value = systemConfigService.getIntegerConfig(configKey, defaultValue);
        return Result.success(value);
    }

    @ApiOperation("获取长整数配置值")
    @GetMapping("/long/{configKey}")
    public Result<Long> getLongConfig(
            @ApiParam("配置键") @PathVariable String configKey,
            @ApiParam("默认值") @RequestParam(required = false) Long defaultValue) {
        Long value = systemConfigService.getLongConfig(configKey, defaultValue);
        return Result.success(value);
    }

    @ApiOperation("获取浮点数配置值")
    @GetMapping("/double/{configKey}")
    public Result<Double> getDoubleConfig(
            @ApiParam("配置键") @PathVariable String configKey,
            @ApiParam("默认值") @RequestParam(required = false) Double defaultValue) {
        Double value = systemConfigService.getDoubleConfig(configKey, defaultValue);
        return Result.success(value);
    }

    @ApiOperation("获取字符串配置值")
    @GetMapping("/string/{configKey}")
    public Result<String> getStringConfig(
            @ApiParam("配置键") @PathVariable String configKey,
            @ApiParam("默认值") @RequestParam(required = false) String defaultValue) {
        String value = systemConfigService.getStringConfig(configKey, defaultValue);
        return Result.success(value);
    }

    /**
     * 初始化系统默认配置
     */
    @ApiOperation("初始化默认配置")
    @PostMapping("/init")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> initDefaultConfigs() {
        systemConfigService.initDefaultConfigs();
        return Result.success();
    }

    /**
     * 重置配置为默认值
     */
    @ApiOperation("重置配置为默认值")
    @PostMapping("/reset/{configKey}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> resetConfigToDefault(@ApiParam("配置键") @PathVariable String configKey) {
        Long operatorId = SecurityUtils.getCurrentUserId();
        systemConfigService.resetConfigToDefault(configKey, operatorId);
        return Result.success();
    }

    /**
     * 导出配置
     */
    @ApiOperation("导出配置")
    @PostMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<SystemConfig>> exportConfigs(@RequestBody(required = false) SystemConfigExportRequest request) {
        List<Long> ids = request != null ? request.getIds() : null;
        List<SystemConfig> configs = systemConfigService.exportConfigs(ids);
        return Result.success(configs);
    }

    /**
     * 导入配置
     */
    @ApiOperation("导入配置")
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> importConfigs(@RequestBody List<SystemConfig> configs) {
        log.info("导入配置: count={}", configs.size());
        
        Long operatorId = SecurityUtils.getCurrentUserId();
        systemConfigService.importConfigs(configs, operatorId);
        
        return Result.success();
    }

    // ==================== 内部请求DTO类 ====================

    /**
     * 配置查询请求
     */
    public static class SystemConfigQueryRequest {
        private Integer page = 1;
        private Integer size = 20;
        private String configKey;
        private String configType;

        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }

        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }

        public String getConfigKey() { return configKey; }
        public void setConfigKey(String configKey) { this.configKey = configKey; }

        public String getConfigType() { return configType; }
        public void setConfigType(String configType) { this.configType = configType; }
    }

    /**
     * 配置状态更新请求
     */
    public static class SystemConfigStatusUpdateRequest {
        private List<Long> ids;
        private Boolean isEnabled;

        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }

        public Boolean getIsEnabled() { return isEnabled; }
        public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    }

    /**
     * 配置导出请求
     */
    public static class SystemConfigExportRequest {
        private List<Long> ids;

        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
    }
}
