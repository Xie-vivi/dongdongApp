package com.xystapp.controller;

import com.xystapp.common.Result;
import com.xystapp.dto.AuthResponse;
import com.xystapp.dto.LoginRequest;
import com.xystapp.dto.PasswordResetRequest;
import com.xystapp.dto.RegisterRequest;
import com.xystapp.dto.SendVerificationCodeRequest;
import com.xystapp.entity.User;
import com.xystapp.service.AuthService;
import com.xystapp.service.VerificationCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 认证控制器
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());
        
        AuthResponse response = authService.register(request);
        
        log.info("用户注册成功: {}", request.getUsername());
        return Result.success("注册成功", response);
    }

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());
        
        AuthResponse response = authService.login(request);
        
        log.info("用户登录成功: {}", request.getUsername());
        return Result.success("登录成功", response);
    }

    /**
     * 刷新Token
     */
    @ApiOperation("刷新Token")
    @PostMapping("/refresh")
    public Result<AuthResponse> refresh(@RequestParam String refreshToken) {
        log.info("Token刷新请求");
        
        AuthResponse response = authService.refreshToken(refreshToken);
        
        log.info("Token刷新成功");
        return Result.success("刷新成功", response);
    }

    /**
     * 用户登出
     */
    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        log.info("用户登出请求");
        
        authService.logout(token);
        
        log.info("用户登出成功");
        return Result.success();
    }

    /**
     * 验证Token
     */
    @ApiOperation("验证Token")
    @GetMapping("/validate")
    public Result<Boolean> validate(@RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        return Result.success(isValid);
    }

    /**
     * 发送验证码
     */
    @ApiOperation("发送验证码")
    @PostMapping("/send-verification-code")
    public Result<Void> sendVerificationCode(@Valid @RequestBody SendVerificationCodeRequest request) {
        log.info("发送验证码请求: {}, 类型: {}", request.getUsername(), request.getType());
        
        verificationCodeService.sendVerificationCode(request.getUsername(), request.getType());
        
        log.info("验证码发送成功: {}", request.getUsername());
        return Result.success();
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        log.info("密码重置请求: {}", request.getUsername());
        
        authService.resetPassword(request);
        
        log.info("密码重置成功: {}", request.getUsername());
        return Result.success();
    }
}
