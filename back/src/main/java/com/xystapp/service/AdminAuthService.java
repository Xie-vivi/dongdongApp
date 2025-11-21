package com.xystapp.service;

import com.xystapp.dto.AdminLoginRequest;
import com.xystapp.dto.AdminLoginResponse;
import com.xystapp.entity.Admin;

import java.util.List;

/**
 * 管理员认证服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     * 
     * @param request 登录请求
     * @return 登录响应
     */
    AdminLoginResponse login(AdminLoginRequest request);

    /**
     * 管理员登出
     * 
     * @param token JWT令牌
     */
    void logout(String token);

    /**
     * 刷新令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 新的登录响应
     */
    AdminLoginResponse refreshToken(String refreshToken);

    /**
     * 验证令牌
     * 
     * @param token JWT令牌
     * @return 管理员信息
     */
    Admin validateToken(String token);

    /**
     * 获取当前登录的管理员
     * 
     * @return 管理员信息
     */
    Admin getCurrentAdmin();

    /**
     * 检查管理员权限
     * 
     * @param adminId 管理员ID
     * @param permission 权限名称
     * @return 是否有权限
     */
    boolean hasPermission(Long adminId, String permission);

    /**
     * 检查管理员角色
     * 
     * @param adminId 管理员ID
     * @param role 角色名称
     * @return 是否有角色
     */
    boolean hasRole(Long adminId, String role);

    /**
     * 修改密码
     * 
     * @param adminId 管理员ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long adminId, String oldPassword, String newPassword);

    /**
     * 重置密码（管理员操作）
     * 
     * @param adminId 目标管理员ID
     * @param newPassword 新密码
     * @param operatorId 操作者ID
     */
    void resetPassword(Long adminId, String newPassword, Long operatorId);

    /**
     * 锁定管理员账户
     * 
     * @param adminId 管理员ID
     * @param reason 锁定原因
     * @param operatorId 操作者ID
     */
    void lockAdmin(Long adminId, String reason, Long operatorId);

    /**
     * 解锁管理员账户
     * 
     * @param adminId 管理员ID
     * @param operatorId 操作者ID
     */
    void unlockAdmin(Long adminId, Long operatorId);

    /**
     * 获取管理员权限列表
     * 
     * @param adminId 管理员ID
     * @return 权限列表
     */
    List<String> getAdminPermissions(Long adminId);
}
