package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Field;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 场地Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface FieldMapper extends BaseMapper<Field> {

    /**
     * 获取场地详情（包含用户信息）
     */
    Field selectFieldWithUserInfo(@Param("fieldId") Long fieldId);

    /**
     * 获取场地列表（包含用户信息）
     */
    IPage<Field> selectFieldList(Page<Field> page, @Param("keyword") String keyword);

    /**
     * 获取用户创建的场地列表
     */
    IPage<Field> selectUserCreatedFields(Page<Field> page, @Param("userId") Long userId);

    /**
     * 获取用户加入的场地列表
     */
    IPage<Field> selectUserJoinedFields(Page<Field> page, @Param("userId") Long userId);

    /**
     * 搜索场地
     */
    IPage<Field> searchFields(Page<Field> page, @Param("keyword") String keyword);

    /**
     * 获取推荐场地
     */
    List<Field> selectRecommendedFields(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取热门场地
     */
    List<Field> selectPopularFields(@Param("limit") Integer limit);

    /**
     * 批量获取场地信息
     */
    List<Field> selectFieldsByIds(@Param("fieldIds") List<Long> fieldIds, @Param("userId") Long userId);

    // ==================== 统计相关 ====================

    /**
     * 统计场地帖子数量
     */
    Long countPostsByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 统计场地活动数量
     */
    Long countActivitiesByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 统计用户创建的场地数量
     */
    Long countUserCreatedFields(@Param("userId") Long userId);

    /**
     * 统计用户创建场地的总成员数
     */
    Long countTotalMembersInUserCreatedFields(@Param("userId") Long userId);

    /**
     * 统计场地今日帖子数量
     */
    Long countTodayPostsByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 统计场地今日活动数量
     */
    Long countTodayActivitiesByFieldId(@Param("fieldId") Long fieldId);

    // ==================== 业务查询 ====================

    /**
     * 获取场地内容统计
     */
    List<com.xystapp.service.FieldService.FieldContentStats> selectFieldContentStats(@Param("fieldIds") List<Long> fieldIds);

    /**
     * 获取用户相关的场地
     */
    List<Field> selectUserRelatedFields(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取最近活跃的场地
     */
    List<Field> selectRecentActiveFields(@Param("limit") Integer limit);

    /**
     * 获取场地创建者信息
     */
    Field selectFieldWithCreatorInfo(@Param("fieldId") Long fieldId);

    /**
     * 获取场地成员数量排名
     */
    List<Field> selectFieldsByMemberCount(@Param("limit") Integer limit);

    /**
     * 获取场地帖子数量排名
     */
    List<Field> selectFieldsByPostCount(@Param("limit") Integer limit);

    /**
     * 获取场地活动数量排名
     */
    List<Field> selectFieldsByActivityCount(@Param("limit") Integer limit);

    /**
     * 检查场地名称是否存在
     */
    boolean fieldNameExists(@Param("name") String name, @Param("excludeId") Long excludeId);

    /**
     * 获取场地创建时间
     */
    java.time.LocalDateTime selectCreatedAt(@Param("fieldId") Long fieldId);

    /**
     * 更新场地最后活动时间
     */
    int updateLastActivityTime(@Param("fieldId") Long fieldId);

    /**
     * 原子更新场地成员数量
     */
    int updateMemberCountAtomic(@Param("fieldId") Long fieldId);

    /**
     * 获取场地详情（包含统计信息）
     */
    Field selectFieldWithStats(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 获取场地列表（包含统计信息）
     */
    IPage<Field> selectFieldListWithStats(Page<Field> page, @Param("keyword") String keyword, @Param("userId") Long userId);

    /**
     * 根据成员数量获取场地列表
     */
    IPage<Field> selectFieldsByMemberCountRange(Page<Field> page, @Param("minCount") Integer minCount, @Param("maxCount") Integer maxCount);

    /**
     * 获取场地活跃度统计
     */
    List<FieldActivityStats> selectFieldActivityStats(@Param("fieldIds") List<Long> fieldIds, @Param("days") Integer days);

    /**
     * 场地活跃度统计类
     */
    class FieldActivityStats {
        private Long fieldId;
        private String fieldName;
        private Long activityCount;
        private java.time.LocalDateTime lastActivityAt;

        public FieldActivityStats(Long fieldId, String fieldName, Long activityCount, java.time.LocalDateTime lastActivityAt) {
            this.fieldId = fieldId;
            this.fieldName = fieldName;
            this.activityCount = activityCount;
            this.lastActivityAt = lastActivityAt;
        }

        public Long getFieldId() { return fieldId; }
        public String getFieldName() { return fieldName; }
        public Long getActivityCount() { return activityCount; }
        public java.time.LocalDateTime getLastActivityAt() { return lastActivityAt; }
    }

    // ==================== 管理员功能相关方法 ====================

    /**
     * 获取待审核场地列表
     */
    List<Field> selectPendingFields(@Param("page") Integer page, @Param("size") Integer size);

    /**
     * 统计待审核场地数量
     */
    Long countPendingFields();

    /**
     * 按时间范围统计场地数量
     */
    Long countFieldsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 获取热门场地
     */
    List<Field> selectHotFields(@Param("limit") Integer limit);

    // ==================== 搜索相关方法 ====================

    /**
     * 全文搜索场地
     */
    List<Field> searchFieldsByKeyword(@Param("keyword") String keyword, 
                                     @Param("page") Integer page, 
                                     @Param("size") Integer size);

    /**
     * 全文搜索场地（带用户信息）
     */
    List<Field> searchFieldsWithUserByKeyword(@Param("keyword") String keyword, 
                                             @Param("page") Integer page, 
                                             @Param("size") Integer size);

    /**
     * 统计搜索结果数量
     */
    Long countSearchFieldsByKeyword(@Param("keyword") String keyword);

    /**
     * 按类型搜索场地
     */
    List<Field> selectFieldsByType(@Param("fieldType") String fieldType, 
                                  @Param("page") Integer page, 
                                  @Param("size") Integer size);

    /**
     * 按地址搜索场地
     */
    List<Field> selectFieldsByAddress(@Param("address") String address, 
                                     @Param("page") Integer page, 
                                     @Param("size") Integer size);

    /**
     * 按状态搜索场地
     */
    List<Field> selectFieldsByStatus(@Param("status") String status, 
                                    @Param("page") Integer page, 
                                    @Param("size") Integer size);

    /**
     * 获取相关场地（基于类型或地址）
     */
    List<Field> selectRelatedFields(@Param("fieldId") Long fieldId, 
                                   @Param("limit") Integer limit);
}
