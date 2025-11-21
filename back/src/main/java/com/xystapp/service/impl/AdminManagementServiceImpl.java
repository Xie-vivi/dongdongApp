package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.AdminCreateRequest;
import com.xystapp.dto.AdminUpdateRequest;
import com.xystapp.dto.AdminQueryRequest;
import com.xystapp.entity.Admin;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.AdminMapper;
import com.xystapp.service.AdminManagementService;
import com.xystapp.service.AdminAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员管理服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class AdminManagementServiceImpl implements AdminManagementService {

    private static final Logger log = LoggerFactory.getLogger(AdminManagementServiceImpl.class);

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.xystapp.mapper.AdminRoleMapper adminRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Admin createAdmin(AdminCreateRequest request, Long operatorId) {
        log.info("创建管理员: username={}, email={}, operatorId={}", 
                request.getUsername(), request.getEmail(), operatorId);

        // 检查用户名和邮箱是否可用
        if (!isUsernameAvailable(request.getUsername(), null)) {
            throw new BusinessException(400, "用户名已存在");
        }
        if (!isEmailAvailable(request.getEmail(), null)) {
            throw new BusinessException(400, "邮箱已存在");
        }

        // 创建管理员
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
        admin.setRealName(request.getRealName());
        admin.setAvatar(request.getAvatar());
        admin.setStatus(Admin.Status.ACTIVE.getCode());
        admin.setCreatedBy(operatorId);

        int result = adminMapper.insert(admin);
        if (result <= 0) {
            throw new BusinessException(500, "创建管理员失败");
        }

        // 分配角色
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            assignRoles(admin.getId(), request.getRoleIds(), operatorId);
        }

        log.info("管理员创建成功: id={}, username={}", admin.getId(), admin.getUsername());
        return getAdminById(admin.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Admin updateAdmin(Long id, AdminUpdateRequest request, Long operatorId) {
        log.info("更新管理员: id={}, operatorId={}", id, operatorId);

        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 检查用户名和邮箱是否可用
        if (!isUsernameAvailable(request.getUsername(), id)) {
            throw new BusinessException(400, "用户名已存在");
        }
        if (!isEmailAvailable(request.getEmail(), id)) {
            throw new BusinessException(400, "邮箱已存在");
        }

        // 更新基本信息
        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
        admin.setRealName(request.getRealName());
        admin.setAvatar(request.getAvatar());

        int result = adminMapper.updateById(admin);
        if (result <= 0) {
            throw new BusinessException(500, "更新管理员失败");
        }

        // 更新角色
        if (request.getRoleIds() != null) {
            // 先移除所有角色
            removeRoles(id, null, operatorId);
            // 再分配新角色
            if (!request.getRoleIds().isEmpty()) {
                assignRoles(id, request.getRoleIds(), operatorId);
            }
        }

        log.info("管理员更新成功: id={}", id);
        return getAdminById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAdmin(Long id, Long operatorId) {
        log.info("删除管理员: id={}, operatorId={}", id, operatorId);

        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 不能删除超级管理员
        if (adminAuthService.hasRole(id, "SUPER_ADMIN")) {
            throw new BusinessException(403, "不能删除超级管理员");
        }

        // 不能删除自己
        Admin currentAdmin = adminAuthService.getCurrentAdmin();
        if (currentAdmin != null && currentAdmin.getId().equals(id)) {
            throw new BusinessException(403, "不能删除自己");
        }

        int result = adminMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException(500, "删除管理员失败");
        }

        log.info("管理员删除成功: id={}", id);
    }

    @Override
    public Admin getAdminById(Long id) {
        log.info("获取管理员详情: id={}", id);
        return adminMapper.selectAdminDetailById(id);
    }

    @Override
    public IPage<Admin> getAdminList(Page<Admin> page, AdminQueryRequest request) {
        log.info("查询管理员列表: page={}, size={}, status={}, keyword={}", 
                page.getCurrent(), page.getSize(), request.getStatus(), request.getKeyword());

        return adminMapper.selectAdminListWithRoles(page, request.getStatus(), request.getKeyword());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableAdmin(Long id, Long operatorId) {
        log.info("启用管理员: id={}, operatorId={}", id, operatorId);

        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        admin.setStatus(Admin.Status.ACTIVE.getCode());
        admin.setLockedUntil(null);
        admin.setFailedLoginCount(0);

        int result = adminMapper.updateById(admin);
        if (result <= 0) {
            throw new BusinessException(500, "启用管理员失败");
        }

        log.info("管理员启用成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableAdmin(Long id, Long operatorId) {
        log.info("禁用管理员: id={}, operatorId={}", id, operatorId);

        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 不能禁用超级管理员
        if (adminAuthService.hasRole(id, "SUPER_ADMIN")) {
            throw new BusinessException(403, "不能禁用超级管理员");
        }

        // 不能禁用自己
        Admin currentAdmin = adminAuthService.getCurrentAdmin();
        if (currentAdmin != null && currentAdmin.getId().equals(id)) {
            throw new BusinessException(403, "不能禁用自己");
        }

        admin.setStatus(Admin.Status.INACTIVE.getCode());

        int result = adminMapper.updateById(admin);
        if (result <= 0) {
            throw new BusinessException(500, "禁用管理员失败");
        }

        log.info("管理员禁用成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetAdminPassword(Long id, String newPassword, Long operatorId) {
        log.info("重置管理员密码: id={}, operatorId={}", id, operatorId);

        Admin admin = adminMapper.selectById(id);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        adminAuthService.resetPassword(id, newPassword, operatorId);
        log.info("管理员密码重置成功: id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long adminId, List<Long> roleIds, Long operatorId) {
        log.info("分配管理员角色: adminId={}, roleIds={}, operatorId={}", 
                adminId, roleIds, operatorId);

        if (adminId == null || roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException(400, "参数错误");
        }

        // 验证管理员存在
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 批量分配角色
        int result = adminRoleMapper.batchAssignRoles(adminId, roleIds, operatorId);
        if (result <= 0) {
            throw new BusinessException(500, "角色分配失败");
        }

        log.info("管理员角色分配完成: adminId={}, assignedCount={}", adminId, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeRoles(Long adminId, List<Long> roleIds, Long operatorId) {
        log.info("移除管理员角色: adminId={}, roleIds={}, operatorId={}", 
                adminId, roleIds, operatorId);

        if (adminId == null) {
            throw new BusinessException(400, "管理员ID不能为空");
        }

        // 如果没有指定角色ID，则移除所有角色
        if (roleIds == null || roleIds.isEmpty()) {
            int result = adminRoleMapper.removeAllRoles(adminId);
            log.info("管理员所有角色移除完成: adminId={}, removedCount={}", adminId, result);
            return;
        }

        // 验证管理员存在
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 批量移除角色
        int result = adminRoleMapper.batchRemoveRoles(adminId, roleIds);
        log.info("管理员角色移除完成: adminId={}, removedCount={}", adminId, result);
    }

    @Override
    public List<String> getAdminRoles(Long adminId) {
        return adminMapper.selectAdminRoles(adminId);
    }

    @Override
    public List<String> getAdminPermissions(Long adminId) {
        return adminMapper.selectAdminPermissions(adminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateStatus(List<Long> adminIds, String status, Long operatorId) {
        log.info("批量更新管理员状态: adminIds={}, status={}, operatorId={}", 
                adminIds, status, operatorId);

        // 检查状态有效性
        if (!Admin.Status.ACTIVE.getCode().equals(status) && 
            !Admin.Status.INACTIVE.getCode().equals(status)) {
            throw new BusinessException(400, "无效的状态值");
        }

        // 不能操作超级管理员
        for (Long adminId : adminIds) {
            if (adminAuthService.hasRole(adminId, "SUPER_ADMIN")) {
                throw new BusinessException(403, "不能操作超级管理员");
            }
        }

        int result = adminMapper.batchUpdateStatus(adminIds, status);
        if (result <= 0) {
            throw new BusinessException(500, "批量更新状态失败");
        }

        log.info("管理员状态批量更新成功: count={}", result);
    }

    @Override
    public AdminStats getAdminStats() {
        log.info("获取管理员统计信息");

        AdminStats stats = new AdminStats();
        stats.setTotalAdmins(adminMapper.countAdminsByStatus(null));
        stats.setActiveAdmins(adminMapper.countAdminsByStatus(Admin.Status.ACTIVE.getCode()));
        stats.setInactiveAdmins(adminMapper.countAdminsByStatus(Admin.Status.INACTIVE.getCode()));
        stats.setLockedAdmins(adminMapper.countAdminsByStatus(Admin.Status.LOCKED.getCode()));

        // TODO: 实现登录统计
        stats.setTodayLogins(0);
        stats.setThisWeekLogins(0);
        stats.setThisMonthLogins(0);

        return stats;
    }

    @Override
    public boolean isUsernameAvailable(String username, Long excludeId) {
        return !adminMapper.checkUsernameExists(username, excludeId);
    }

    @Override
    public boolean isEmailAvailable(String email, Long excludeId) {
        return !adminMapper.checkEmailExists(email, excludeId);
    }

    @Override
    public List<Admin> getRecentLogins(Integer days, Integer limit) {
        log.info("获取最近登录管理员: days={}, limit={}", days, limit);
        return adminMapper.selectRecentLogins(days, limit);
    }
}
