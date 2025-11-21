package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.AdminCreateRequest;
import com.xystapp.dto.AdminUpdateRequest;
import com.xystapp.dto.AdminQueryRequest;
import com.xystapp.entity.Admin;

import java.util.List;

/**
 * 管理员管理服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface AdminManagementService {

    /**
     * 创建管理员
     * 
     * @param request 创建请求
     * @param operatorId 操作者ID
     * @return 管理员信息
     */
    Admin createAdmin(AdminCreateRequest request, Long operatorId);

    /**
     * 更新管理员信息
     * 
     * @param id 管理员ID
     * @param request 更新请求
     * @param operatorId 操作者ID
     * @return 管理员信息
     */
    Admin updateAdmin(Long id, AdminUpdateRequest request, Long operatorId);

    /**
     * 删除管理员
     * 
     * @param id 管理员ID
     * @param operatorId 操作者ID
     */
    void deleteAdmin(Long id, Long operatorId);

    /**
     * 获取管理员详情
     * 
     * @param id 管理员ID
     * @return 管理员详细信息
     */
    Admin getAdminById(Long id);

    /**
     * 分页查询管理员列表
     * 
     * @param page 分页参数
     * @param request 查询请求
     * @return 管理员列表
     */
    IPage<Admin> getAdminList(Page<Admin> page, AdminQueryRequest request);

    /**
     * 启用管理员
     * 
     * @param id 管理员ID
     * @param operatorId 操作者ID
     */
    void enableAdmin(Long id, Long operatorId);

    /**
     * 禁用管理员
     * 
     * @param id 管理员ID
     * @param operatorId 操作者 ID
     */
    void disableAdmin(Long id, Long operatorId);

    /**
     * 重置管理员密码
     * 
     * @param id 管理员ID
     * @param newPassword 新密码
     * @param operatorId 操作者ID
     */
    void resetAdminPassword(Long id, String newPassword, Long operatorId);

    /**
     * 分配角色
     * 
     * @param adminId 管理员ID
     * @param roleIds 角色ID列表
     * @param operatorId 操作者ID
     */
    void assignRoles(Long adminId, List<Long> roleIds, Long operatorId);

    /**
     * 移除角色
     * 
     * @param adminId 管理员ID
     * @param roleIds 角色ID列表
     * @param operatorId 操作者ID
     */
    void removeRoles(Long adminId, List<Long> roleIds, Long operatorId);

    /**
     * 获取管理员角色列表
     * 
     * @param adminId 管理员ID
     * @return 角色列表
     */
    List<String> getAdminRoles(Long adminId);

    /**
     * 获取管理员权限列表
     * 
     * @param adminId 管理员ID
     * @return 权限列表
     */
    List<String> getAdminPermissions(Long adminId);

    /**
     * 批量操作管理员状态
     * 
     * @param adminIds 管理员ID列表
     * @param status 状态
     * @param operatorId 操作者ID
     */
    void batchUpdateStatus(List<Long> adminIds, String status, Long operatorId);

    /**
     * 获取管理员统计信息
     * 
     * @return 统计信息
     */
    AdminStats getAdminStats();

    /**
     * 检查用户名是否可用
     * 
     * @param username 用户名
     * @param excludeId 排除的ID
     * @return 是否可用
     */
    boolean isUsernameAvailable(String username, Long excludeId);

    /**
     * 检查邮箱是否可用
     * 
     * @param email 邮箱
     * @param excludeId 排除的ID
     * @return 是否可用
     */
    boolean isEmailAvailable(String email, Long excludeId);

    /**
     * 获取最近登录的管理员列表
     * 
     * @param days 天数
     * @param limit 限制数量
     * @return 管理员列表
     */
    List<Admin> getRecentLogins(Integer days, Integer limit);

    /**
     * 管理员统计信息内部类
     */
    class AdminStats {
        private Integer totalAdmins;
        private Integer activeAdmins;
        private Integer inactiveAdmins;
        private Integer lockedAdmins;
        private Integer todayLogins;
        private Integer thisWeekLogins;
        private Integer thisMonthLogins;

        // getters and setters
        public Integer getTotalAdmins() { return totalAdmins; }
        public void setTotalAdmins(Integer totalAdmins) { this.totalAdmins = totalAdmins; }
        public Integer getActiveAdmins() { return activeAdmins; }
        public void setActiveAdmins(Integer activeAdmins) { this.activeAdmins = activeAdmins; }
        public Integer getInactiveAdmins() { return inactiveAdmins; }
        public void setInactiveAdmins(Integer inactiveAdmins) { this.inactiveAdmins = inactiveAdmins; }
        public Integer getLockedAdmins() { return lockedAdmins; }
        public void setLockedAdmins(Integer lockedAdmins) { this.lockedAdmins = lockedAdmins; }
        public Integer getTodayLogins() { return todayLogins; }
        public void setTodayLogins(Integer todayLogins) { this.todayLogins = todayLogins; }
        public Integer getThisWeekLogins() { return thisWeekLogins; }
        public void setThisWeekLogins(Integer thisWeekLogins) { this.thisWeekLogins = thisWeekLogins; }
        public Integer getThisMonthLogins() { return thisMonthLogins; }
        public void setThisMonthLogins(Integer thisMonthLogins) { this.thisMonthLogins = thisMonthLogins; }
    }
}
