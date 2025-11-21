package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员角色关联Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<Object> {

    /**
     * 分配角色给管理员
     * 
     * @param adminId 管理员ID
     * @param roleId 角色ID
     * @param assignedBy 分配者ID
     * @return 影响行数
     */
    int assignRole(@Param("adminId") Long adminId, 
                   @Param("roleId") Long roleId, 
                   @Param("assignedBy") Long assignedBy);

    /**
     * 批量分配角色给管理员
     * 
     * @param adminId 管理员ID
     * @param roleIds 角色ID列表
     * @param assignedBy 分配者ID
     * @return 影响行数
     */
    int batchAssignRoles(@Param("adminId") Long adminId, 
                        @Param("roleIds") List<Long> roleIds, 
                        @Param("assignedBy") Long assignedBy);

    /**
     * 移除管理员的角色
     * 
     * @param adminId 管理员ID
     * @param roleId 角色ID
     * @return 影响行数
     */
    int removeRole(@Param("adminId") Long adminId, @Param("roleId") Long roleId);

    /**
     * 批量移除管理员的角色
     * 
     * @param adminId 管理员ID
     * @param roleIds 角色ID列表
     * @return 影响行数
     */
    int batchRemoveRoles(@Param("adminId") Long adminId, @Param("roleIds") List<Long> roleIds);

    /**
     * 移除管理员的所有角色
     * 
     * @param adminId 管理员ID
     * @return 影响行数
     */
    int removeAllRoles(@Param("adminId") Long adminId);

    /**
     * 获取管理员的角色ID列表
     * 
     * @param adminId 管理员ID
     * @return 角色ID列表
     */
    List<Long> getAdminRoleIds(@Param("adminId") Long adminId);

    /**
     * 获取角色的管理员ID列表
     * 
     * @param roleId 角色ID
     * @return 管理员ID列表
     */
    List<Long> getRoleAdminIds(@Param("roleId") Long roleId);

    /**
     * 检查管理员是否拥有指定角色
     * 
     * @param adminId 管理员ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    boolean hasRole(@Param("adminId") Long adminId, @Param("roleId") Long roleId);

    /**
     * 统计角色下的管理员数量
     * 
     * @param roleId 角色ID
     * @return 管理员数量
     */
    Integer countAdminsByRole(@Param("roleId") Long roleId);

    /**
     * 更新角色关联状态
     * 
     * @param adminId 管理员ID
     * @param roleId 角色ID
     * @param isActive 是否激活
     * @return 影响行数
     */
    int updateRoleStatus(@Param("adminId") Long adminId, 
                        @Param("roleId") Long roleId, 
                        @Param("isActive") Boolean isActive);

    /**
     * 设置角色过期时间
     * 
     * @param adminId 管理员ID
     * @param roleId 角色ID
     * @param expiresAt 过期时间
     * @return 影响行数
     */
    int setRoleExpiration(@Param("adminId") Long adminId, 
                          @Param("roleId") Long roleId, 
                          @Param("expiresAt") LocalDateTime expiresAt);

    /**
     * 清理过期的角色关联
     * 
     * @return 清理数量
     */
    int cleanupExpiredRoles();

    /**
     * 获取即将过期的角色关联
     * 
     * @param days 天数
     * @return 关联列表
     */
    List<AdminRoleInfo> getExpiringRoles(@Param("days") Integer days);

    /**
     * 管理员角色关联信息内部类
     */
    class AdminRoleInfo {
        private Long adminId;
        private String adminName;
        private Long roleId;
        private String roleName;
        private LocalDateTime assignedAt;
        private LocalDateTime expiresAt;
        private String assignedByName;

        // getters and setters
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getAdminName() { return adminName; }
        public void setAdminName(String adminName) { this.adminName = adminName; }
        public Long getRoleId() { return roleId; }
        public void setRoleId(Long roleId) { this.roleId = roleId; }
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
        public LocalDateTime getAssignedAt() { return assignedAt; }
        public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
        public String getAssignedByName() { return assignedByName; }
        public void setAssignedByName(String assignedByName) { this.assignedByName = assignedByName; }
    }
}
