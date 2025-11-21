package com.xystapp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.UserProfileUpdateRequest;
import com.xystapp.dto.UserSearchRequest;
import com.xystapp.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 用户服务接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
public interface UserService {

    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据ID获取用户信息
     */
    User getUserById(Long id);

    /**
     * 根据UID获取用户信息
     */
    User getUserByUid(String uid);

    /**
     * 更新用户资料
     */
    User updateUserProfile(String username, UserProfileUpdateRequest request);

    /**
     * 搜索用户
     */
    IPage<User> searchUsers(UserSearchRequest request);

    /**
     * 更新用户头像
     */
    void updateUserAvatar(String username, String avatarUrl);

    /**
     * 更新用户背景图
     */
    void updateUserBackground(String username, String backgroundUrl);

    /**
     * 申请校园认证
     */
    void applySchoolCertification(String username, String school, MultipartFile certificate);

    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStats(Long userId);

    /**
     * 批量获取用户信息
     */
    List<User> getUsersByIds(List<Long> ids);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, String status);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);

    /**
     * 检查UID是否存在
     */
    boolean existsByUid(String uid);
}
