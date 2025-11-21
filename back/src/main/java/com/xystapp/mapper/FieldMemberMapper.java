package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.FieldMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 场地成员Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface FieldMemberMapper extends BaseMapper<FieldMember> {

    /**
     * 根据场地ID和用户ID获取成员信息
     */
    FieldMember selectByFieldIdAndUserId(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 获取场地成员列表（包含用户信息）
     */
    IPage<FieldMember> selectFieldMembers(Page<FieldMember> page, @Param("fieldId") Long fieldId, @Param("role") String role);

    /**
     * 获取场地成员详情（包含用户信息）
     */
    FieldMember selectFieldMemberWithUserInfo(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 批量获取场地成员信息
     */
    List<FieldMember> selectMembersByFieldIds(@Param("fieldIds") List<Long> fieldIds, @Param("userId") Long userId);

    /**
     * 获取用户加入的场地成员信息
     */
    List<FieldMember> selectUserFieldMembers(@Param("userId") Long userId, @Param("limit") Integer limit);

    // ==================== 成员统计 ====================

    /**
     * 统计场地成员数量
     */
    Integer countByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 统计场地指定角色的成员数量
     */
    Integer countByFieldIdAndRole(@Param("fieldId") Long fieldId, @Param("role") String role);

    /**
     * 统计用户加入的场地数量
     */
    Long countUserJoinedFields(@Param("userId") Long userId);

    /**
     * 统计用户管理的场地数量
     */
    Long countUserManagedFields(@Param("userId") Long userId);

    /**
     * 统计场地今日新增成员数量
     */
    Long countNewMembersByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 统计场地指定时间段内新增成员数量
     */
    Long countNewMembersByFieldIdAndTime(@Param("fieldId") Long fieldId, 
                                        @Param("startTime") java.time.LocalDateTime startTime,
                                        @Param("endTime") java.time.LocalDateTime endTime);

    // ==================== 成员操作 ====================

    /**
     * 根据场地ID删除所有成员
     */
    int deleteByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 根据场地ID和用户ID删除成员
     */
    int deleteByFieldIdAndUserId(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 批量删除场地成员
     */
    int deleteMultipleMembers(@Param("fieldId") Long fieldId, @Param("userIds") List<Long> userIds);

    /**
     * 更新成员角色
     */
    int updateMemberRole(@Param("fieldId") Long fieldId, @Param("userId") Long userId, @Param("role") String role);

    /**
     * 批量更新成员角色
     */
    int updateMultipleMemberRoles(@Param("fieldId") Long fieldId, @Param("userIds") List<Long> userIds, @Param("role") String role);

    // ==================== 业务查询 ====================

    /**
     * 获取场地管理员列表
     */
    List<FieldMember> selectFieldAdmins(@Param("fieldId") Long fieldId);

    /**
     * 获取场地普通成员列表
     */
    List<FieldMember> selectFieldNormalMembers(@Param("fieldId") Long fieldId, @Param("limit") Integer limit);

    /**
     * 获取用户管理的场地成员信息
     */
    List<FieldMember> selectUserManagedFieldMembers(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 获取最近加入的成员
     */
    List<FieldMember> selectRecentMembers(@Param("fieldId") Long fieldId, @Param("limit") Integer limit);

    /**
     * 获取最早加入的成员
     */
    List<FieldMember> selectEarliestMembers(@Param("fieldId") Long fieldId, @Param("limit") Integer limit);

    /**
     * 检查用户是否是场地成员
     */
    boolean isFieldMember(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 检查用户是否是场地管理员
     */
    boolean isFieldAdmin(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 获取成员加入时间
     */
    java.time.LocalDateTime selectJoinedAt(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 获取场地成员加入时间统计
     */
    List<MemberJoinStats> selectMemberJoinStats(@Param("fieldId") Long fieldId, @Param("days") Integer days);

    /**
     * 成员加入统计类
     */
    class MemberJoinStats {
        private String date;
        private Long joinCount;

        public MemberJoinStats(String date, Long joinCount) {
            this.date = date;
            this.joinCount = joinCount;
        }

        public String getDate() { return date; }
        public Long getJoinCount() { return joinCount; }
    }

    // ==================== 权限相关 ====================

    /**
     * 获取用户在所有场地中的角色
     */
    List<FieldMember> selectUserRolesInAllFields(@Param("userId") Long userId);

    /**
     * 获取场地中用户权限高于指定角色的成员列表
     */
    List<FieldMember> selectMembersWithHigherRole(@Param("fieldId") Long fieldId, @Param("excludeRole") String excludeRole);

    /**
     * 检查用户在场地中是否有指定权限
     */
    boolean hasPermission(@Param("fieldId") Long fieldId, @Param("userId") Long userId, @Param("permission") String permission);

    // ==================== 数据维护 ====================

    /**
     * 清理无效的成员记录（用户不存在）
     */
    int cleanupInvalidMembers();

    /**
     * 更新成员最后活跃时间
     */
    int updateLastActiveTime(@Param("fieldId") Long fieldId, @Param("userId") Long userId);

    /**
     * 获取需要清理的成员数量
     */
    Long countMembersToCleanup(@Param("days") Integer days);
}
