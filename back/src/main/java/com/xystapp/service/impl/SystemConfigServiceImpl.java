package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.SystemConfigCreateRequest;
import com.xystapp.dto.SystemConfigUpdateRequest;
import com.xystapp.entity.SystemConfig;
import com.xystapp.mapper.SystemConfigMapper;
import com.xystapp.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigServiceImpl.class);

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String CONFIG_CACHE_KEY = "system:config:";
    private static final String CONFIG_MAP_CACHE_KEY = "system:config:map";
    private static final long CACHE_EXPIRE_TIME = 30; // 30分钟

    @Override
    @Transactional
    public SystemConfig createConfig(SystemConfigCreateRequest request, Long operatorId) {
        log.info("创建系统配置: key={}, type={}, operator={}", 
                request.getConfigKey(), request.getConfigType(), operatorId);

        // 检查配置键是否已存在
        if (!isConfigKeyAvailable(request.getConfigKey(), null)) {
            throw new RuntimeException("配置键已存在: " + request.getConfigKey());
        }

        SystemConfig config = new SystemConfig();
        config.setConfigKey(request.getConfigKey());
        config.setConfigValue(request.getConfigValue());
        config.setConfigType(request.getConfigType());
        config.setDescription(request.getDescription());
        config.setIsEnabled(request.getIsEnabled() != null ? request.getIsEnabled() : true);
        config.setIsSystem(request.getIsSystem() != null ? request.getIsSystem() : false);
        config.setCreatedBy(operatorId);

        int result = systemConfigMapper.insert(config);
        if (result <= 0) {
            throw new RuntimeException("创建系统配置失败");
        }

        // 清除缓存
        clearConfigCache();

        log.info("系统配置创建成功: id={}, key={}", config.getId(), config.getConfigKey());
        return config;
    }

    @Override
    @Transactional
    public SystemConfig updateConfig(Long id, SystemConfigUpdateRequest request, Long operatorId) {
        log.info("更新系统配置: id={}, operator={}", id, operatorId);

        SystemConfig config = getConfigById(id);
        if (config == null) {
            throw new RuntimeException("系统配置不存在: " + id);
        }

        // 检查配置键是否可用
        if (!isConfigKeyAvailable(request.getConfigKey(), id)) {
            throw new RuntimeException("配置键已存在: " + request.getConfigKey());
        }

        // 系统配置不允许修改配置键
        if (config.getIsSystem() && !config.getConfigKey().equals(request.getConfigKey())) {
            throw new RuntimeException("系统配置不允许修改配置键");
        }

        config.setConfigKey(request.getConfigKey());
        config.setConfigValue(request.getConfigValue());
        config.setConfigType(request.getConfigType());
        config.setDescription(request.getDescription());
        config.setIsEnabled(request.getIsEnabled());
        config.setUpdatedBy(operatorId);

        int result = systemConfigMapper.updateById(config);
        if (result <= 0) {
            throw new RuntimeException("更新系统配置失败");
        }

        // 清除缓存
        clearConfigCache();

        log.info("系统配置更新成功: id={}, key={}", config.getId(), config.getConfigKey());
        return config;
    }

    @Override
    @Transactional
    public void deleteConfig(Long id, Long operatorId) {
        log.info("删除系统配置: id={}, operator={}", id, operatorId);

        SystemConfig config = getConfigById(id);
        if (config == null) {
            throw new RuntimeException("系统配置不存在: " + id);
        }

        // 系统配置不允许删除
        if (config.getIsSystem()) {
            throw new RuntimeException("系统配置不允许删除: " + config.getConfigKey());
        }

        int result = systemConfigMapper.deleteById(id);
        if (result <= 0) {
            throw new RuntimeException("删除系统配置失败");
        }

        // 清除缓存
        clearConfigCache();

        log.info("系统配置删除成功: id={}, key={}", id, config.getConfigKey());
    }

    @Override
    public SystemConfig getConfigById(Long id) {
        try {
            return systemConfigMapper.selectById(id);
        } catch (Exception e) {
            log.error("获取系统配置失败: id={}, error={}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public IPage<SystemConfig> getConfigsWithPagination(Page<SystemConfig> page, String configKey, String configType) {
        try {
            return systemConfigMapper.selectConfigsWithPagination(page, configKey, configType);
        } catch (Exception e) {
            log.error("分页查询系统配置失败: configKey={}, configType={}, error={}", 
                    configKey, configType, e.getMessage(), e);
            return new Page<>();
        }
    }

    @Override
    public String getConfigValue(String configKey) {
        try {
            // 先从缓存获取
            String cacheKey = CONFIG_CACHE_KEY + configKey;
            Object cachedValue = redisTemplate.opsForValue().get(cacheKey);
            if (cachedValue != null) {
                return cachedValue.toString();
            }

            // 从数据库获取
            SystemConfig config = systemConfigMapper.selectByConfigKey(configKey);
            if (config != null && config.getIsEnabled()) {
                // 存入缓存
                redisTemplate.opsForValue().set(cacheKey, config.getConfigValue(), 
                        CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
                return config.getConfigValue();
            }

            return null;
        } catch (Exception e) {
            log.error("获取配置值失败: key={}, error={}", configKey, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public SystemConfig getConfigByKey(String configKey) {
        try {
            return systemConfigMapper.selectByConfigKey(configKey);
        } catch (Exception e) {
            log.error("获取系统配置失败: key={}, error={}", configKey, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<SystemConfig> getConfigsByType(String configType) {
        try {
            return systemConfigMapper.selectConfigsByType(configType);
        } catch (Exception e) {
            log.error("获取指定类型配置失败: type={}, error={}", configType, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<SystemConfig> getEnabledConfigs() {
        try {
            return systemConfigMapper.selectEnabledConfigs();
        } catch (Exception e) {
            log.error("获取启用配置失败: error={}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public Map<String, String> getConfigMap() {
        try {
            // 先从缓存获取
            Object cachedMap = redisTemplate.opsForValue().get(CONFIG_MAP_CACHE_KEY);
            if (cachedMap != null) {
                return (Map<String, String>) cachedMap;
            }

            // 从数据库获取
            List<SystemConfig> configs = getEnabledConfigs();
            Map<String, String> configMap = configs.stream()
                    .filter(config -> config.getIsEnabled())
                    .collect(Collectors.toMap(
                            SystemConfig::getConfigKey,
                            SystemConfig::getConfigValue,
                            (existing, replacement) -> existing
                    ));

            // 存入缓存
            redisTemplate.opsForValue().set(CONFIG_MAP_CACHE_KEY, configMap, 
                    CACHE_EXPIRE_TIME, TimeUnit.MINUTES);

            return configMap;
        } catch (Exception e) {
            log.error("获取配置映射失败: error={}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, String> getConfigMapByType(String configType) {
        try {
            List<SystemConfig> configs = getConfigsByType(configType);
            return configs.stream()
                    .filter(config -> config.getIsEnabled())
                    .collect(Collectors.toMap(
                            SystemConfig::getConfigKey,
                            SystemConfig::getConfigValue,
                            (existing, replacement) -> existing
                    ));
        } catch (Exception e) {
            log.error("获取指定类型配置映射失败: type={}, error={}", configType, e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    @Transactional
    public void batchUpdateStatus(List<Long> ids, Boolean isEnabled, Long operatorId) {
        log.info("批量更新配置状态: ids={}, enabled={}, operator={}", ids, isEnabled, operatorId);

        try {
            int result = systemConfigMapper.batchUpdateStatus(ids, isEnabled);
            if (result > 0) {
                // 清除缓存
                clearConfigCache();
                log.info("批量更新配置状态成功: 更新数量={}", result);
            }
        } catch (Exception e) {
            log.error("批量更新配置状态失败: error={}", e.getMessage(), e);
            throw new RuntimeException("批量更新配置状态失败");
        }
    }

    @Override
    public boolean isConfigKeyAvailable(String configKey, Long excludeId) {
        try {
            return !systemConfigMapper.existsByConfigKey(configKey, excludeId);
        } catch (Exception e) {
            log.error("检查配置键可用性失败: key={}, excludeId={}, error={}", 
                    configKey, excludeId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void refreshConfigCache() {
        try {
            clearConfigCache();
            log.info("配置缓存刷新成功");
        } catch (Exception e) {
            log.error("配置缓存刷新失败: error={}", e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> getConfigStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalCount", systemConfigMapper.selectCount(null));
            statistics.put("enabledCount", systemConfigMapper.countEnabledConfigs());
            statistics.put("systemCount", systemConfigMapper.countSystemConfigs());
            
            // 按类型统计
            Map<String, Long> typeStatistics = new HashMap<>();
            typeStatistics.put("system", systemConfigMapper.countByType("system"));
            typeStatistics.put("business", systemConfigMapper.countByType("business"));
            typeStatistics.put("ui", systemConfigMapper.countByType("ui"));
            typeStatistics.put("security", systemConfigMapper.countByType("security"));
            statistics.put("typeStatistics", typeStatistics);

            return statistics;
        } catch (Exception e) {
            log.error("获取配置统计失败: error={}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Boolean getBooleanConfig(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    @Override
    public Integer getIntegerConfig(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("配置值格式错误: key={}, value={}", configKey, value);
            }
        }
        return defaultValue;
    }

    @Override
    public Long getLongConfig(String configKey, Long defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                log.warn("配置值格式错误: key={}, value={}", configKey, value);
            }
        }
        return defaultValue;
    }

    @Override
    public Double getDoubleConfig(String configKey, Double defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                log.warn("配置值格式错误: key={}, value={}", configKey, value);
            }
        }
        return defaultValue;
    }

    @Override
    public String getStringConfig(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    @Override
    @Transactional
    public void initDefaultConfigs() {
        log.info("初始化系统默认配置");

        List<SystemConfigCreateRequest> defaultConfigs = Arrays.asList(
                // 系统配置
                createConfigRequest("site.name", "XYST社交平台", "system", "网站名称", true, true),
                createConfigRequest("site.description", "校园社交平台", "system", "网站描述", true, true),
                createConfigRequest("site.version", "1.0.0", "system", "系统版本", true, true),
                
                // 业务配置
                createConfigRequest("post.max_length", "5000", "business", "帖子最大长度", true, true),
                createConfigRequest("comment.max_length", "1000", "business", "评论最大长度", true, true),
                createConfigRequest("upload.max_file_size", "10", "business", "上传文件大小限制(MB)", true, true),
                
                // UI配置
                createConfigRequest("ui.theme", "default", "ui", "默认主题", true, false),
                createConfigRequest("ui.page_size", "20", "ui", "默认分页大小", true, false),
                
                // 安全配置
                createConfigRequest("security.jwt_expire_hours", "24", "security", "JWT过期时间(小时)", true, true),
                createConfigRequest("security.max_login_attempts", "5", "security", "最大登录尝试次数", true, true),
                createConfigRequest("security.login_lock_minutes", "30", "security", "登录锁定时间(分钟)", true, true)
        );

        for (SystemConfigCreateRequest request : defaultConfigs) {
            if (isConfigKeyAvailable(request.getConfigKey(), null)) {
                createConfig(request, 1L); // 系统初始化，使用系统管理员ID
            }
        }

        log.info("系统默认配置初始化完成");
    }

    @Override
    @Transactional
    public void resetConfigToDefault(String configKey, Long operatorId) {
        log.info("重置配置为默认值: key={}, operator={}", configKey, operatorId);

        SystemConfig config = getConfigByKey(configKey);
        if (config == null) {
            throw new RuntimeException("配置不存在: " + configKey);
        }

        if (!config.getIsSystem()) {
            throw new RuntimeException("只能重置系统配置");
        }

        // 这里可以根据配置键设置默认值
        String defaultValue = getDefaultConfigValue(configKey);
        if (defaultValue == null) {
            throw new RuntimeException("无法获取配置默认值: " + configKey);
        }

        config.setConfigValue(defaultValue);
        config.setUpdatedBy(operatorId);

        int result = systemConfigMapper.updateById(config);
        if (result <= 0) {
            throw new RuntimeException("重置配置失败");
        }

        // 清除缓存
        clearConfigCache();

        log.info("配置重置成功: key={}, defaultValue={}", configKey, defaultValue);
    }

    @Override
    public List<SystemConfig> exportConfigs(List<Long> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return getEnabledConfigs();
            } else {
                return systemConfigMapper.selectBatchIds(ids);
            }
        } catch (Exception e) {
            log.error("导出配置失败: error={}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void importConfigs(List<SystemConfig> configs, Long operatorId) {
        log.info("导入配置: count={}, operator={}", configs.size(), operatorId);

        try {
            for (SystemConfig config : configs) {
                if (isConfigKeyAvailable(config.getConfigKey(), null)) {
                    config.setCreatedBy(operatorId);
                    systemConfigMapper.insert(config);
                } else {
                    log.warn("配置键已存在，跳过: key={}", config.getConfigKey());
                }
            }

            // 清除缓存
            clearConfigCache();

            log.info("配置导入完成");
        } catch (Exception e) {
            log.error("导入配置失败: error={}", e.getMessage(), e);
            throw new RuntimeException("导入配置失败");
        }
    }

    /**
     * 清除配置缓存
     */
    private void clearConfigCache() {
        try {
            // 清除所有配置缓存
            Set<String> keys = redisTemplate.keys(CONFIG_CACHE_KEY + "*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            
            // 清除配置映射缓存
            redisTemplate.delete(CONFIG_MAP_CACHE_KEY);
        } catch (Exception e) {
            log.error("清除配置缓存失败: error={}", e.getMessage(), e);
        }
    }

    /**
     * 创建配置请求的辅助方法
     */
    private SystemConfigCreateRequest createConfigRequest(String configKey, String configValue, 
                                                           String configType, String description, 
                                                           Boolean isEnabled, Boolean isSystem) {
        SystemConfigCreateRequest request = new SystemConfigCreateRequest();
        request.setConfigKey(configKey);
        request.setConfigValue(configValue);
        request.setConfigType(configType);
        request.setDescription(description);
        request.setIsEnabled(isEnabled);
        request.setIsSystem(isSystem);
        return request;
    }

    /**
     * 获取配置默认值
     */
    private String getDefaultConfigValue(String configKey) {
        Map<String, String> defaultValues = new HashMap<>();
        defaultValues.put("site.name", "XYST社交平台");
        defaultValues.put("site.description", "校园社交平台");
        defaultValues.put("site.version", "1.0.0");
        defaultValues.put("post.max_length", "5000");
        defaultValues.put("comment.max_length", "1000");
        defaultValues.put("upload.max_file_size", "10");
        defaultValues.put("ui.theme", "default");
        defaultValues.put("ui.page_size", "20");
        defaultValues.put("security.jwt_expire_hours", "24");
        defaultValues.put("security.max_login_attempts", "5");
        defaultValues.put("security.login_lock_minutes", "30");
        
        return defaultValues.get(configKey);
    }
}
