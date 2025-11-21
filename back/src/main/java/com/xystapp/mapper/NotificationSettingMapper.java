package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xystapp.entity.NotificationSetting;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 通知设置Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface NotificationSettingMapper extends BaseMapper<NotificationSetting> {

    /**
     * 根据用户ID获取通知设置列表
     */
    List<NotificationSetting> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和通知类型获取设置
     */
    NotificationSetting selectByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 批量获取通知设置
     */
    List<NotificationSetting> selectByUserIdAndTypes(@Param("userId") Long userId, @Param("types") List<String> types);

    /**
     * 删除用户的所有通知设置
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除指定的通知设置
     */
    int deleteByUserIdAndType(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 更新通知设置
     */
    int updateNotificationSetting(@Param("userId") Long userId, @Param("type") String type, 
                                  @Param("enabled") Boolean enabled, @Param("pushEnabled") Boolean pushEnabled, 
                                  @Param("emailEnabled") Boolean emailEnabled);

    /**
     * 批量更新通知设置
     */
    int updateMultipleSettings(@Param("userId") Long userId, @Param("settings") List<NotificationSetting> settings);

    /**
     * 检查通知设置是否存在
     */
    boolean settingExists(@Param("userId") Long userId, @Param("type") String type);

    /**
     * 获取启用的通知类型
     */
    List<String> selectEnabledTypes(@Param("userId") Long userId);

    /**
     * 获取启用推送的通知类型
     */
    List<String> selectPushEnabledTypes(@Param("userId") Long userId);

    /**
     * 获取启用邮件的通知类型
     */
    List<String> selectEmailEnabledTypes(@Param("userId") Long userId);

    /**
     * 统计用户启用的通知类型数量
     */
    Integer countEnabledTypes(@Param("userId") Long userId);

    /**
     * 创建默认通知设置
     */
    int createDefaultSettings(@Param("userId") Long userId);

    /**
     * 重置通知设置为默认值
     */
    int resetToDefaults(@Param("userId") Long userId);
}
