package com.xystapp.config;

import com.xystapp.entity.Admin;
import com.xystapp.service.AdminAuthService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.io.Serializable;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // TODO: 实现JWT认证相关类
    // @Autowired
    // private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    // @Autowired
    // private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AdminAuthService adminAuthService;

    /**
     * 密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: 临时简化配置，允许所有请求，后续实现完整的JWT认证
        http.csrf().disable()
                .authorizeHttpRequests(authz -> authz
                        .anyRequest().permitAll()
                );

        // TODO: 添加JWT过滤器
        // http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 自定义权限评估器
     */
    @Bean
    public PermissionEvaluator customPermissionEvaluator() {
        return new CustomPermissionEvaluator(adminAuthService);
    }

    /**
     * 自定义权限评估器实现
     */
    public static class CustomPermissionEvaluator implements PermissionEvaluator {

        private final AdminAuthService adminAuthService;

        public CustomPermissionEvaluator(AdminAuthService adminAuthService) {
            this.adminAuthService = adminAuthService;
        }

        @Override
        public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            String username = authentication.getName();
            try {
                Admin admin = adminAuthService.getCurrentAdmin();
                if (admin == null) {
                    return false;
                }
                return adminAuthService.hasPermission(admin.getId(), permission.toString());
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            String username = authentication.getName();
            try {
                Admin admin = adminAuthService.getCurrentAdmin();
                if (admin == null) {
                    return false;
                }
                return adminAuthService.hasPermission(admin.getId(), permission.toString());
            } catch (Exception e) {
                return false;
            }
        }
    }
}
