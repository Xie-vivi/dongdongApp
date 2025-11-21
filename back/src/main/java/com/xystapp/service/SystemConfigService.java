package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.SystemConfigCreateRequest;
import com.xystapp.dto.SystemConfigUpdateRequest;
import com.xystapp.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface SystemConfigService {

    // ==================== 基础CRUD ====================

    /**
     * 创建系统配置
     */
    SystemConfig createConfig(SystemConfigCreateRequest request, Long operatorId);

    /**
     * 更新系统配置
     */
    SystemConfig updateConfig(Long id, SystemConfigUpdateRequest request, Long operatorId);

    /**
     * 删除系统配置
     */
    void deleteConfig(Long id, Long operatorId);

    /**
     * 获取配置详情
     */
    SystemConfig getConfigById(Long id);

    /**
     * 分页查询配置
     */
    IPage<SystemConfig> getConfigsWithPagination(Page<SystemConfig> page, String configKey, String configType);

    // ==================== 配置查询 ====================

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键获取配置
     */
    SystemConfig getConfigByKey(String configKey);

    /**
     * 获取指定类型的配置列表
     */
    List<SystemConfig> getConfigsByType(String configType);

    /**
     * 获取所有启用的配置
     */
    List<SystemConfig> getEnabledConfigs();

    /**
     * 获取配置映射（key-value）
     */
    Map<String, String> getConfigMap();

    /**
     * 获取指定类型的配置映射
     */
    Map<String, String> getConfigMapByType(String configType);

    // ==================== 配置管理 ====================

    /**
     * 批量启用/禁用配置
     */
    void batchUpdateStatus(List<Long> ids, Boolean isEnabled, Long operatorId);

    /**
     * 检查配置键是否可用
     */
    boolean isConfigKeyAvailable(String configKey, Long excludeId);

    /**
     * 刷新配置缓存
     */
    void refreshConfigCache();

    /**
     * 获取配置统计信息
     */
    Map<String, Object> getConfigStatistics();

    // ==================== 类型化配置获取 ====================

    /**
     * 获取布尔类型配置值
     */
    Boolean getBooleanConfig(String configKey, Boolean defaultValue);

    /**
     * 获取整数类型配置值
     */
    Integer getIntegerConfig(String configKey, Integer defaultValue);

    /**
     * 获取长整数类型配置值
     */
    Long getLongConfig(String configKey, Long defaultValue);

    /**
     * 获取浮点数类型配置值
     */
    Double getDoubleConfig(String configKey, Double defaultValue);

    /**
     * 获取字符串类型配置值
     */
    String getStringConfig(String configKey, String defaultValue);

    // ==================== 系统预设配置 ====================

    /**
     * 初始化系统默认配置
     */
    void initDefaultConfigs();

    /**
     * 重置配置为默认值
     */
    void resetConfigToDefault(String configKey, Long operatorId);

    /**
     * 导出配置
     */
    List<SystemConfig> exportConfigs(List<Long> ids);

    /**
     * 导入配置
     */
    void importConfigs(List<SystemConfig> configs, Long operatorId);
}
