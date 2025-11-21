package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统配置Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    /**
     * 根据配置键获取配置
     */
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 分页查询系统配置
     */
    IPage<SystemConfig> selectConfigsWithPagination(Page<SystemConfig> page, @Param("configKey") String configKey, @Param("configType") String configType);

    /**
     * 获取指定类型的配置列表
     */
    List<SystemConfig> selectConfigsByType(@Param("configType") String configType);

    /**
     * 获取启用的配置列表
     */
    List<SystemConfig> selectEnabledConfigs();

    /**
     * 获取系统配置列表
     */
    List<SystemConfig> selectSystemConfigs();

    /**
     * 批量更新配置状态
     */
    int batchUpdateStatus(@Param("ids") List<Long> ids, @Param("isEnabled") Boolean isEnabled);

    /**
     * 检查配置键是否存在
     */
    boolean existsByConfigKey(@Param("configKey") String configKey, @Param("excludeId") Long excludeId);

    /**
     * 获取配置数量统计
     */
    Long countByType(@Param("configType") String configType);

    /**
     * 获取启用的配置数量
     */
    Long countEnabledConfigs();

    /**
     * 获取系统配置数量
     */
    Long countSystemConfigs();
}
