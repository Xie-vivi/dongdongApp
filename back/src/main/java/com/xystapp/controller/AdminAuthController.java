package com.xystapp.controller;

import com.xystapp.common.Result;
import com.xystapp.dto.AdminLoginRequest;
import com.xystapp.dto.AdminLoginResponse;
import com.xystapp.entity.Admin;
import com.xystapp.service.AdminAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 管理员认证控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "管理员认证")
@RestController
@RequestMapping("/admin/auth")
@Validated
public class AdminAuthController {

    private static final Logger log = LoggerFactory.getLogger(AdminAuthController.class);

    @Autowired
    private AdminAuthService adminAuthService;

    /**
     * 管理员登录
     */
    @ApiOperation("管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginResponse> login(
            @ApiParam("登录请求") @Valid @RequestBody AdminLoginRequest request,
            HttpServletRequest httpRequest) {
        
        // 设置客户端信息
        request.setIp(getClientIp(httpRequest));
        request.setUserAgent(httpRequest.getHeader("User-Agent"));
        
        try {
            AdminLoginResponse response = adminAuthService.login(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("管理员登录失败: username={}", request.getUsername(), e);
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 管理员登出
     */
    @ApiOperation("管理员登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            adminAuthService.logout(token);
            return Result.success();
        } catch (Exception e) {
            log.error("管理员登出失败", e);
            return Result.error("登出失败：" + e.getMessage());
        }
    }

    /**
     * 刷新令牌
     */
    @ApiOperation("刷新令牌")
    @PostMapping("/refresh")
    public Result<AdminLoginResponse> refreshToken(
            @ApiParam("刷新令牌") @RequestParam String refreshToken) {
        
        try {
            AdminLoginResponse response = adminAuthService.refreshToken(refreshToken);
            return Result.success(response);
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            return Result.error("刷新令牌失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前管理员信息
     */
    @ApiOperation("获取当前管理员信息")
    @GetMapping("/me")
    public Result<AdminLoginResponse> getCurrentAdmin() {
        try {
            Admin admin = adminAuthService.getCurrentAdmin();
            if (admin == null) {
                return Result.error(401, "未登录");
            }

            AdminLoginResponse response = new AdminLoginResponse();
            response.setAdminId(admin.getId());
            response.setUsername(admin.getUsername());
            response.setRealName(admin.getRealName());
            response.setEmail(admin.getEmail());
            response.setAvatar(admin.getAvatar());
            // TODO: 实现角色和权限获取逻辑
            response.setRoles(new java.util.ArrayList<>());
            response.setPermissions(new java.util.ArrayList<>());

            return Result.success(response);
        } catch (Exception e) {
            log.error("获取当前管理员信息失败", e);
            return Result.error("获取信息失败：" + e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PostMapping("/change-password")
    public Result<Void> changePassword(
            @ApiParam("旧密码") @RequestParam String oldPassword,
            @ApiParam("新密码") @RequestParam String newPassword) {
        
        try {
            Admin admin = adminAuthService.getCurrentAdmin();
            if (admin == null) {
                return Result.error(401, "未登录");
            }

            adminAuthService.changePassword(admin.getId(), oldPassword, newPassword);
            return Result.success();
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error("修改密码失败：" + e.getMessage());
        }
    }

    /**
     * 验证令牌
     */
    @ApiOperation("验证令牌")
    @PostMapping("/validate")
    public Result<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            Admin admin = adminAuthService.validateToken(token);
            boolean valid = admin != null;
            return Result.success(valid);
        } catch (Exception e) {
            log.error("验证令牌失败", e);
            return Result.success(false);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
