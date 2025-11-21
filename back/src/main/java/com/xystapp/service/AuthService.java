package com.xystapp.service;

import com.xystapp.dto.AuthResponse;
import com.xystapp.dto.LoginRequest;
import com.xystapp.dto.PasswordResetRequest;
import com.xystapp.dto.RegisterRequest;

/**
 * 认证服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface AuthService {

    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest request);

    /**
     * 刷新Token
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 验证Token
     */
    boolean validateToken(String token);

    /**
     * 重置密码
     */
    void resetPassword(PasswordResetRequest request);
}
