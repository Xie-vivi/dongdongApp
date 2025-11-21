package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Field;
import com.xystapp.entity.FieldMember;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 场地服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface FieldService {

    // ==================== 场地创建 ====================

    /**
     * 创建场地
     */
    Field createField(String name, String description, String avatar, Long creatorId);

    /**
     * 更新场地信息
     */
    Field updateField(Long fieldId, String name, String description, String avatar, Long operatorId);

    /**
     * 删除场地
     */
    void deleteField(Long fieldId, Long operatorId);

    // ==================== 场地查询 ====================

    /**
     * 获取场地详情
     */
    Field getFieldById(Long fieldId, Long userId);

    /**
     * 获取场地列表
     */
    IPage<Field> getFieldList(Integer page, Integer size, String keyword);

    /**
     * 获取用户创建的场地列表
     */
    IPage<Field> getUserCreatedFields(Long userId, Integer page, Integer size);

    /**
     * 获取用户加入的场地列表
     */
    IPage<Field> getUserJoinedFields(Long userId, Integer page, Integer size);

    /**
     * 搜索场地
     */
    IPage<Field> searchFields(String keyword, Integer page, Integer size);

    /**
     * 获取推荐场地
     */
    List<Field> getRecommendedFields(Long userId, Integer limit);

    /**
     * 获取热门场地
     */
    List<Field> getPopularFields(Integer limit);

    // ==================== 成员管理 ====================

    /**
     * 加入场地
     */
    FieldMember joinField(Long fieldId, Long userId);

    /**
     * 退出场地
     */
    void leaveField(Long fieldId, Long userId);

    /**
     * 邀请用户加入场地
     */
    FieldMember inviteUserToField(Long fieldId, Long operatorId, Long targetUserId);

    /**
     * 移除场地成员
     */
    void removeMember(Long fieldId, Long operatorId, Long targetUserId);

    /**
     * 更新成员角色
     */
    FieldMember updateMemberRole(Long fieldId, Long operatorId, Long targetUserId, String role);

    /**
     * 获取场地成员列表
     */
    IPage<FieldMember> getFieldMembers(Long fieldId, Integer page, Integer size, String role);

    /**
     * 获取场地成员数量
     */
    Integer getFieldMemberCount(Long fieldId);

    /**
     * 获取用户在场地中的信息
     */
    FieldMember getUserFieldInfo(Long fieldId, Long userId);

    // ==================== 权限验证 ====================

    /**
     * 检查用户是否是场地成员
     */
    boolean isFieldMember(Long fieldId, Long userId);

    /**
     * 检查用户是否是场地管理员
     */
    boolean isFieldAdmin(Long fieldId, Long userId);

    /**
     * 检查用户是否是场地创建者
     */
    boolean isFieldCreator(Long fieldId, Long userId);

    /**
     * 检查用户是否有管理权限
     */
    boolean hasManagePermission(Long fieldId, Long userId);

    /**
     * 检查场地是否存在
     */
    boolean fieldExists(Long fieldId);

    /**
     * 检查用户是否可以加入场地
     */
    boolean canJoinField(Long fieldId, Long userId);

    // ==================== 统计信息 ====================

    /**
     * 更新场地成员数量
     */
    void updateFieldMemberCount(Long fieldId);

    /**
     * 获取场地统计信息
     */
    FieldStats getFieldStats(Long fieldId);

    /**
     * 获取用户场地统计
     */
    UserFieldStats getUserFieldStats(Long userId);

    /**
     * 场地统计类
     */
    class FieldStats {
        private Long totalMembers;
        private Long totalPosts;
        private Long totalActivities;
        private Long newMembersToday;
        private Long newPostsToday;
        private Long newActivitiesToday;

        public FieldStats(Long totalMembers, Long totalPosts, Long totalActivities,
                         Long newMembersToday, Long newPostsToday, Long newActivitiesToday) {
            this.totalMembers = totalMembers;
            this.totalPosts = totalPosts;
            this.totalActivities = totalActivities;
            this.newMembersToday = newMembersToday;
            this.newPostsToday = newPostsToday;
            this.newActivitiesToday = newActivitiesToday;
        }

        public Long getTotalMembers() { return totalMembers; }
        public Long getTotalPosts() { return totalPosts; }
        public Long getTotalActivities() { return totalActivities; }
        public Long getNewMembersToday() { return newMembersToday; }
        public Long getNewPostsToday() { return newPostsToday; }
        public Long getNewActivitiesToday() { return newActivitiesToday; }
    }

    /**
     * 用户场地统计类
     */
    class UserFieldStats {
        private Long createdFields;
        private Long joinedFields;
        private Long managedFields;
        private Long totalMembersInCreatedFields;

        public UserFieldStats(Long createdFields, Long joinedFields, Long managedFields,
                             Long totalMembersInCreatedFields) {
            this.createdFields = createdFields;
            this.joinedFields = joinedFields;
            this.managedFields = managedFields;
            this.totalMembersInCreatedFields = totalMembersInCreatedFields;
        }

        public Long getCreatedFields() { return createdFields; }
        public Long getJoinedFields() { return joinedFields; }
        public Long getManagedFields() { return managedFields; }
        public Long getTotalMembersInCreatedFields() { return totalMembersInCreatedFields; }
    }

    // ==================== 批量操作 ====================

    /**
     * 批量获取场地信息
     */
    List<Field> getFieldsByIds(List<Long> fieldIds, Long userId);

    /**
     * 批量获取场地成员信息
     */
    List<FieldMember> getMembersByFieldIds(List<Long> fieldIds, Long userId);

    /**
     * 批量检查用户权限
     */
    boolean batchCheckManagePermission(List<Long> fieldIds, Long userId);

    // ==================== 业务查询 ====================

    /**
     * 获取场地内容统计
     */
    List<FieldContentStats> getFieldContentStats(List<Long> fieldIds);

    /**
     * 场地内容统计类
     */
    class FieldContentStats {
        private Long fieldId;
        private String fieldName;
        private Long postsCount;
        private Long activitiesCount;
        private LocalDateTime lastActivityAt;

        public FieldContentStats(Long fieldId, String fieldName, Long postsCount,
                                Long activitiesCount, LocalDateTime lastActivityAt) {
            this.fieldId = fieldId;
            this.fieldName = fieldName;
            this.postsCount = postsCount;
            this.activitiesCount = activitiesCount;
            this.lastActivityAt = lastActivityAt;
        }

        public Long getFieldId() { return fieldId; }
        public String getFieldName() { return fieldName; }
        public Long getPostsCount() { return postsCount; }
        public Long getActivitiesCount() { return activitiesCount; }
        public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    }
}
