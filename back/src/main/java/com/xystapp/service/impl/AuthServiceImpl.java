package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xystapp.dto.AuthResponse;
import com.xystapp.dto.LoginRequest;
import com.xystapp.dto.PasswordResetRequest;
import com.xystapp.dto.RegisterRequest;
import com.xystapp.entity.User;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.AuthService;
import com.xystapp.service.VerificationCodeService;
import com.xystapp.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 检查UID是否已存在
        if (userMapper.existsByUid(request.getUid())) {
            throw new BusinessException(400, "UID已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setUid(request.getUid());
        user.setIsCertified(false);
        user.setSchoolCertified(false);
        user.setShowGender(true);
        user.setShowAge(true);
        user.setShowMbti(true);
        user.setFollowersCount(0);
        user.setFollowingCount(0);
        user.setFieldCount(0);
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new BusinessException(500, "注册失败，请稍后重试");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getUsername());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

        // 构建用户信息
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar(),
                user.getUid(),
                user.getIsCertified()
        );

        return new AuthResponse(token, refreshToken, jwtExpiration, userInfo);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // 进行身份认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 获取认证后的用户信息
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = userMapper.selectByUsername(request.getUsername());

            if (user == null) {
                throw new BusinessException(404, "用户不存在");
            }

            // 生成Token
            String token = jwtUtils.generateToken(user.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

            // 构建用户信息
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    user.getAvatar(),
                    user.getUid(),
                    user.getIsCertified()
            );

            return new AuthResponse(token, refreshToken, jwtExpiration, userInfo);

        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage());
            throw new BusinessException(401, "用户名或密码错误");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            // 验证刷新Token
            if (!jwtUtils.validateToken(refreshToken)) {
                throw new BusinessException(401, "刷新Token无效");
            }

            // 从Token中获取用户名
            String username = jwtUtils.getUsernameFromToken(refreshToken);

            // 查询用户信息
            User user = userMapper.selectByUsername(username);
            if (user == null || !"active".equals(user.getStatus())) {
                throw new BusinessException(401, "用户不存在或已被禁用");
            }

            // 生成新的Token
            String newToken = jwtUtils.generateToken(username);
            String newRefreshToken = jwtUtils.generateRefreshToken(username);

            // 构建用户信息
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    user.getAvatar(),
                    user.getUid(),
                    user.getIsCertified()
            );

            return new AuthResponse(newToken, newRefreshToken, jwtExpiration, userInfo);

        } catch (Exception e) {
            log.error("刷新Token失败: {}", e.getMessage());
            throw new BusinessException(401, "刷新Token失败");
        }
    }

    @Override
    public void logout(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 验证Token并获取用户名
            if (jwtUtils.validateToken(token)) {
                String username = jwtUtils.getUsernameFromToken(token);
                log.info("用户 {} 登出成功", username);
            }
            
            // 这里可以将Token加入黑名单（Redis实现）
            // TODO: 实现Token黑名单功能
            
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            return jwtUtils.validateToken(token);
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        // 验证验证码
        if (!verificationCodeService.verifyCode(request.getUsername(), request.getVerificationCode())) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());

        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(500, "密码重置失败，请稍后重试");
        }

        log.info("用户 {} 密码重置成功", request.getUsername());
    }
}
