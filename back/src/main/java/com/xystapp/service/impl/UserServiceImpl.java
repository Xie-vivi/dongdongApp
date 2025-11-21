package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.UserProfileUpdateRequest;
import com.xystapp.dto.UserSearchRequest;
import com.xystapp.entity.User;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.UserService;
import com.xystapp.utils.CosUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CosUtils cosUtils;

    @Override
    public User getUserByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public User getUserByUid(String uid) {
        User user = userMapper.selectByUid(uid);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public User updateUserProfile(String username, UserProfileUpdateRequest request) {
        // 查询用户
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 更新字段
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (StringUtils.hasText(request.getGender())) {
            user.setGender(request.getGender());
        }
        if (StringUtils.hasText(request.getBio())) {
            user.setBio(request.getBio());
        }
        if (StringUtils.hasText(request.getSchool())) {
            user.setSchool(request.getSchool());
        }
        if (StringUtils.hasText(request.getLocation())) {
            user.setLocation(request.getLocation());
        }
        if (StringUtils.hasText(request.getMbti())) {
            user.setMbti(request.getMbti());
        }
        if (request.getShowGender() != null) {
            user.setShowGender(request.getShowGender());
        }
        if (request.getShowAge() != null) {
            user.setShowAge(request.getShowAge());
        }
        if (request.getShowMbti() != null) {
            user.setShowMbti(request.getShowMbti());
        }

        user.setUpdatedAt(LocalDateTime.now());

        // 保存更新
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(500, "更新失败，请稍后重试");
        }

        // 不返回密码
        user.setPassword(null);
        return user;
    }

    @Override
    public IPage<User> searchUsers(UserSearchRequest request) {
        Page<User> page = new Page<>(request.getPage(), request.getSize());
        IPage<User> userPage = userMapper.selectUserPage(page, request.getKeyword());
        
        // 不返回密码
        userPage.getRecords().forEach(user -> user.setPassword(null));
        
        return userPage;
    }

    @Override
    public void updateUserAvatar(String username, String avatarUrl) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 删除旧头像
        if (StringUtils.hasText(user.getAvatar()) && 
            !user.getAvatar().contains("default-avatar")) {
            cosUtils.deleteFile(user.getAvatar());
        }

        // 更新新头像
        user.setAvatar(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(500, "头像更新失败");
        }
    }

    @Override
    public void updateUserBackground(String username, String backgroundUrl) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 删除旧背景图
        if (StringUtils.hasText(user.getBackground()) && 
            !user.getBackground().contains("default-background")) {
            cosUtils.deleteFile(user.getBackground());
        }

        // 更新新背景图
        user.setBackground(backgroundUrl);
        user.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(500, "背景图更新失败");
        }
    }

    @Override
    public void applySchoolCertification(String username, String school, MultipartFile certificate) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        try {
            // 更新学校信息
            user.setSchool(school);
            user.setSchoolCertified(false); // 待审核状态
            user.setUpdatedAt(LocalDateTime.now());
            
            int result = userMapper.updateById(user);
            if (result <= 0) {
                throw new BusinessException(500, "认证申请提交失败");
            }

            // 如果有认证文件，上传到COS
            if (certificate != null && !certificate.isEmpty()) {
                String certificateUrl = cosUtils.uploadFile(certificate, "certificates");
                log.info("认证文件上传成功: {}", certificateUrl);
                // TODO: 保存认证文件URL到数据库
            }

        } catch (Exception e) {
            log.error("校园认证申请失败: {}", e.getMessage());
            throw new BusinessException(500, "认证申请提交失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("followersCount", user.getFollowersCount());
        stats.put("followingCount", user.getFollowingCount());
        stats.put("fieldCount", user.getFieldCount());
        stats.put("isCertified", user.getIsCertified());
        stats.put("schoolCertified", user.getSchoolCertified());
        
        // TODO: 添加更多统计信息，如帖子数、活动数等
        
        return stats;
    }

    @Override
    public List<User> getUsersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<User> users = userMapper.selectUserByIds(ids);
        // 不返回密码
        users.forEach(user -> user.setPassword(null));
        
        return users;
    }

    @Override
    public void updateUserStatus(Long userId, String status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(500, "状态更新失败");
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username);
    }

    @Override
    public boolean existsByUid(String uid) {
        return userMapper.existsByUid(uid);
    }
}
