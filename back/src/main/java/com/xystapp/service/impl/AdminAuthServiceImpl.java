package com.xystapp.service.impl;

import com.xystapp.dto.AdminLoginRequest;
import com.xystapp.dto.AdminLoginResponse;
import com.xystapp.entity.Admin;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.AdminMapper;
import com.xystapp.service.AdminAuthService;
import com.xystapp.utils.JwtUtils;
import com.xystapp.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员认证服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class AdminAuthServiceImpl implements AdminAuthService {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthServiceImpl.class);

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_HOURS = 24;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminLoginResponse login(AdminLoginRequest request) {
        log.info("管理员登录尝试: username={}, ip={}", request.getUsername(), request.getIp());

        // 查找管理员
        Admin admin = adminMapper.selectByUsername(request.getUsername());
        if (admin == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查账户状态
        checkAccountStatus(admin);

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            handleFailedLogin(admin);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 登录成功，重置失败计数
        handleSuccessfulLogin(admin, request.getIp());

        // 生成JWT令牌
        String token = generateToken(admin);
        String refreshToken = generateRefreshToken(admin);

        // 构建响应
        AdminLoginResponse response = new AdminLoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setAdminId(admin.getId());
        response.setUsername(admin.getUsername());
        response.setRealName(admin.getRealName());
        response.setEmail(admin.getEmail());
        response.setAvatar(admin.getAvatar());
        response.setRoles(getAdminRoles(admin.getId()));
        response.setPermissions(getAdminPermissions(admin.getId()));
        response.setExpiresIn(jwtUtils.getExpiration() / 1000); // 转换为秒

        log.info("管理员登录成功: username={}, adminId={}", admin.getUsername(), admin.getId());
        return response;
    }

    @Override
    public void logout(String token) {
        try {
            String username = jwtUtils.extractUsername(token);
            log.info("管理员登出: username={}", username);
            
            // 这里可以将token加入黑名单或记录登出日志
            // 暂时只记录日志
        } catch (Exception e) {
            log.warn("登出时解析token失败", e);
        }
    }

    @Override
    public AdminLoginResponse refreshToken(String refreshToken) {
        try {
            String username = jwtUtils.extractUsername(refreshToken);
            Admin admin = adminMapper.selectByUsername(username);
            
            if (admin == null || !admin.isActive()) {
                throw new BusinessException(401, "无效的刷新令牌");
            }

            // 生成新的令牌
            String newToken = generateToken(admin);
            String newRefreshToken = generateRefreshToken(admin);

            AdminLoginResponse response = new AdminLoginResponse();
            response.setToken(newToken);
            response.setRefreshToken(newRefreshToken);
            response.setExpiresIn(jwtUtils.getExpiration() / 1000);

            log.info("刷新令牌成功: username={}", username);
            return response;

        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            throw new BusinessException(401, "刷新令牌失败");
        }
    }

    @Override
    public Admin validateToken(String token) {
        try {
            String username = jwtUtils.extractUsername(token);
            if (jwtUtils.validateToken(token)) {
                return adminMapper.selectByUsername(username);
            }
        } catch (Exception e) {
            log.warn("验证token失败", e);
        }
        return null;
    }

    @Override
    public Admin getCurrentAdmin() {
        // 从SecurityContext获取当前管理员信息
        String username = SecurityUtils.getCurrentUsername();
        if (username == null) {
            return null;
        }
        return adminMapper.selectByUsername(username);
    }

    @Override
    public boolean hasPermission(Long adminId, String permission) {
        List<String> permissions = adminMapper.selectAdminPermissions(adminId);
        return permissions.contains(permission) || permissions.contains("admin:super");
    }

    @Override
    public boolean hasRole(Long adminId, String role) {
        List<String> roles = adminMapper.selectAdminRoles(adminId);
        return roles.contains(role) || roles.contains("SUPER_ADMIN");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long adminId, String oldPassword, String newPassword) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, admin.getPasswordHash())) {
            throw new BusinessException(400, "原密码错误");
        }

        // 更新密码
        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        adminMapper.updateById(admin);

        log.info("管理员修改密码成功: adminId={}", adminId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long adminId, String newPassword, Long operatorId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 更新密码
        admin.setPasswordHash(passwordEncoder.encode(newPassword));
        adminMapper.updateById(admin);

        log.info("管理员重置密码: targetId={}, operatorId={}", adminId, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockAdmin(Long adminId, String reason, Long operatorId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        // 不能锁定超级管理员
        if (hasRole(adminId, "SUPER_ADMIN")) {
            throw new BusinessException(403, "不能锁定超级管理员");
        }

        admin.setStatus(Admin.Status.LOCKED.getCode());
        admin.setLockedUntil(LocalDateTime.now().plusHours(LOCK_DURATION_HOURS));
        adminMapper.updateById(admin);

        log.info("管理员账户被锁定: targetId={}, reason={}, operatorId={}", adminId, reason, operatorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlockAdmin(Long adminId, Long operatorId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(404, "管理员不存在");
        }

        admin.setStatus(Admin.Status.ACTIVE.getCode());
        admin.setLockedUntil(null);
        admin.setFailedLoginCount(0);
        adminMapper.updateById(admin);

        log.info("管理员账户被解锁: targetId={}, operatorId={}", adminId, operatorId);
    }

    /**
     * 检查账户状态
     */
    private void checkAccountStatus(Admin admin) {
        if (!admin.isActive()) {
            throw new BusinessException(403, "账户已被停用");
        }

        if (admin.isLocked()) {
            throw new BusinessException(403, "账户已被锁定");
        }
    }

    /**
     * 处理登录失败
     */
    private void handleFailedLogin(Admin admin) {
        admin.setFailedLoginCount(admin.getFailedLoginCount() + 1);
        
        if (admin.shouldLock(MAX_FAILED_ATTEMPTS)) {
            admin.setStatus(Admin.Status.LOCKED.getCode());
            admin.setLockedUntil(LocalDateTime.now().plusHours(LOCK_DURATION_HOURS));
            log.warn("管理员账户因多次登录失败被锁定: username={}", admin.getUsername());
        }
        
        adminMapper.updateById(admin);
    }

    /**
     * 处理登录成功
     */
    private void handleSuccessfulLogin(Admin admin, String ip) {
        admin.setLastLoginAt(LocalDateTime.now());
        admin.setLastLoginIp(ip);
        admin.setLoginCount(admin.getLoginCount() + 1);
        admin.setFailedLoginCount(0);
        
        if (admin.isLocked()) {
            admin.setStatus(Admin.Status.ACTIVE.getCode());
            admin.setLockedUntil(null);
        }
        
        adminMapper.updateById(admin);
    }

    /**
     * 生成JWT令牌
     */
    private String generateToken(Admin admin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("roles", getAdminRoles(admin.getId()));
        claims.put("type", "admin");
        
        return jwtUtils.generateToken(admin.getUsername(), claims);
    }

    /**
     * 生成刷新令牌
     */
    private String generateRefreshToken(Admin admin) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("type", "refresh");
        
        // 刷新令牌有效期更长
        return jwtUtils.generateToken(admin.getUsername(), claims, 7 * 24 * 60 * 60 * 1000L); // 7天
    }

    /**
     * 获取管理员角色列表
     */
    private List<String> getAdminRoles(Long adminId) {
        return adminMapper.selectAdminRoles(adminId);
    }

    /**
     * 获取管理员权限列表
     */
    @Override
    public List<String> getAdminPermissions(Long adminId) {
        return adminMapper.selectAdminPermissions(adminId);
    }
}
