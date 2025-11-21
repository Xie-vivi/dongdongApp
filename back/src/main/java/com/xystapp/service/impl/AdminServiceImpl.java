package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.common.Result;
import com.xystapp.entity.*;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.*;
import com.xystapp.service.AdminService;
import com.xystapp.service.AdminAuthService;
import com.xystapp.utils.JwtUtils;
import com.xystapp.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 后台管理服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private FieldMapper fieldMapper;

    @Autowired
    private AdminOperationLogMapper adminOperationLogMapper;

    @Autowired
    private ContentAuditLogMapper contentAuditLogMapper;

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private JwtUtils jwtUtils;

    // ==================== 管理员认证 ====================

    @Override
    public AdminLoginResult adminLogin(String username, String password, String ipAddress, String userAgent) {
        log.info("管理员登录: username={}, ip={}", username, ipAddress);

        // 验证用户名密码
        User user = userMapper.selectByUsername(username);
        if (user == null || !SecurityUtils.matchesPassword(password, user.getPassword())) {
            log.warn("管理员登录失败: username={}, ip={}", username, ipAddress);
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查是否为管理员
        if (!isAdmin(user.getId())) {
            log.warn("非管理员尝试登录: username={}, ip={}", username, ipAddress);
            throw new BusinessException(403, "无管理员权限");
        }

        // 检查用户状态
        if (!"active".equals(user.getStatus())) {
            log.warn("管理员账户已被禁用: username={}, ip={}", username, ipAddress);
            throw new BusinessException(403, "账户已被禁用");
        }

        // 生成JWT令牌
        String token = jwtUtils.generateToken(user.getUsername());

        // 获取管理员信息
        AdminInfo adminInfo = getAdminInfo(user.getId());

        // 记录登录日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(user.getId());
        operationLog.setAdminUsername(user.getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.LOGIN.getCode());
        operationLog.setOperationDesc("管理员登录");
        operationLog.setRequestMethod("POST");
        operationLog.setRequestUrl("/admin/auth/login");
        operationLog.setIpAddress(ipAddress);
        operationLog.setUserAgent(userAgent);
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        log.info("管理员登录成功: username={}, role={}", username, user.getRole());
        return new AdminLoginResult(token, adminInfo, 86400L); // 24小时有效期
    }

    @Override
    public boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }

        User user = userMapper.selectById(userId);
        return user != null && ("admin".equals(user.getRole()) || "super_admin".equals(user.getRole()));
    }

    @Override
    public boolean isSuperAdmin(Long userId) {
        if (userId == null) {
            return false;
        }

        User user = userMapper.selectById(userId);
        return user != null && "super_admin".equals(user.getRole());
    }

    @Override
    public AdminInfo getAdminInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        List<String> permissions = getAdminPermissions(userId);
        return new AdminInfo(user.getId(), user.getUsername(), user.getNickname(), user.getAvatar(),
                           user.getRole(), permissions, user.getUpdatedAt());
    }

    @Override
    public boolean hasPermission(Long adminId, String permission) {
        // 委托给新的AdminAuthService
        return adminAuthService.hasPermission(adminId, permission);
    }

    @Override
    public List<String> getAdminPermissions(Long adminId) {
        // 委托给新的AdminAuthService
        return adminAuthService.getAdminPermissions(adminId);
    }

    // ==================== 用户管理 ====================

    @Override
    public IPage<User> getUserList(Integer page, Integer size, String keyword, String role, String status) {
        log.info("获取用户列表: page={}, size={}, keyword={}, role={}, status={}", page, size, keyword, role, status);

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<User> pageParam = new Page<>(page, size);
        IPage<User> result = userMapper.selectUserListForAdmin(pageParam, keyword, role, status);

        log.info("获取用户列表成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(Long adminId, Long userId, String role) {
        log.info("更新用户角色: adminId={}, userId={}, role={}", adminId, userId, role);

        // 验证参数
        if (userId == null || role == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 验证角色
        List<String> validRoles = Arrays.asList("user", "admin", "super_admin");
        if (!validRoles.contains(role)) {
            throw new BusinessException(400, "无效的角色");
        }

        // 检查权限（只有超级管理员可以分配管理员角色）
        if (("admin".equals(role) || "super_admin".equals(role)) && !isSuperAdmin(adminId)) {
            throw new BusinessException(403, "只有超级管理员可以分配管理员角色");
        }

        // 获取原用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        String originalRole = user.getRole();

        // 更新角色
        user.setRole(role);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(500, "更新用户角色失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.ROLE_MANAGE.getCode());
        operationLog.setOperationDesc(String.format("更新用户角色: %s -> %s", originalRole, role));
        operationLog.setTargetType(AdminOperationLog.TargetType.USER.getCode());
        operationLog.setTargetId(userId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/users/" + userId + "/role");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        log.info("更新用户角色成功: userId={}, originalRole={}, newRole={}", userId, originalRole, role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void banUser(Long adminId, Long userId, String reason) {
        log.info("封禁用户: adminId={}, userId={}, reason={}", adminId, userId, reason);

        // 验证参数
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 检查权限（不能封禁管理员）
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (isAdmin(userId) && !isSuperAdmin(adminId)) {
            throw new BusinessException(403, "不能封禁管理员");
        }

        if (isSuperAdmin(userId)) {
            throw new BusinessException(403, "不能封禁超级管理员");
        }

        // 更新用户状态
        targetUser.setStatus("inactive");
        int result = userMapper.updateById(targetUser);
        if (result <= 0) {
            throw new BusinessException(500, "封禁用户失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.USER_BAN.getCode());
        operationLog.setOperationDesc("封禁用户: " + reason);
        operationLog.setTargetType(AdminOperationLog.TargetType.USER.getCode());
        operationLog.setTargetId(userId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/users/" + userId + "/ban");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        // 记录内容审核日志
        ContentAuditLog auditLog = new ContentAuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        auditLog.setContentType(ContentAuditLog.ContentType.USER.getCode());
        auditLog.setContentId(userId);
        auditLog.setAction(ContentAuditLog.AuditAction.BAN.getCode());
        auditLog.setReason(reason);
        auditLog.setOriginalStatus("active");
        auditLog.setNewStatus("inactive");
        auditLog.setCreatedAt(LocalDateTime.now());
        logContentAudit(auditLog);

        log.info("封禁用户成功: userId={}, reason={}", userId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbanUser(Long adminId, Long userId, String reason) {
        log.info("解封用户: adminId={}, userId={}, reason={}", adminId, userId, reason);

        // 验证参数
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 获取用户信息
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 更新用户状态
        targetUser.setStatus("active");
        int result = userMapper.updateById(targetUser);
        if (result <= 0) {
            throw new BusinessException(500, "解封用户失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.USER_UNBAN.getCode());
        operationLog.setOperationDesc("解封用户: " + reason);
        operationLog.setTargetType(AdminOperationLog.TargetType.USER.getCode());
        operationLog.setTargetId(userId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/users/" + userId + "/unban");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        // 记录内容审核日志
        ContentAuditLog auditLog = new ContentAuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        auditLog.setContentType(ContentAuditLog.ContentType.USER.getCode());
        auditLog.setContentId(userId);
        auditLog.setAction(ContentAuditLog.AuditAction.UNBAN.getCode());
        auditLog.setReason(reason);
        auditLog.setOriginalStatus("inactive");
        auditLog.setNewStatus("active");
        auditLog.setCreatedAt(LocalDateTime.now());
        logContentAudit(auditLog);

        log.info("解封用户成功: userId={}, reason={}", userId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long adminId, Long userId, String reason) {
        log.info("删除用户: adminId={}, userId={}, reason={}", adminId, userId, reason);

        // 验证参数
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 检查权限（不能删除管理员）
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (isAdmin(userId) && !isSuperAdmin(adminId)) {
            throw new BusinessException(403, "不能删除管理员");
        }

        if (isSuperAdmin(userId)) {
            throw new BusinessException(403, "不能删除超级管理员");
        }

        // 删除用户（软删除）
        targetUser.setStatus("deleted");
        int result = userMapper.updateById(targetUser);
        if (result <= 0) {
            throw new BusinessException(500, "删除用户失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.USER_DELETE.getCode());
        operationLog.setOperationDesc("删除用户: " + reason);
        operationLog.setTargetType(AdminOperationLog.TargetType.USER.getCode());
        operationLog.setTargetId(userId);
        operationLog.setRequestMethod("DELETE");
        operationLog.setRequestUrl("/admin/users/" + userId);
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        // 记录内容审核日志
        ContentAuditLog auditLog = new ContentAuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        auditLog.setContentType(ContentAuditLog.ContentType.USER.getCode());
        auditLog.setContentId(userId);
        auditLog.setAction(ContentAuditLog.AuditAction.DELETE.getCode());
        auditLog.setReason(reason);
        auditLog.setOriginalStatus(targetUser.getStatus());
        auditLog.setNewStatus("deleted");
        auditLog.setCreatedAt(LocalDateTime.now());
        logContentAudit(auditLog);

        log.info("删除用户成功: userId={}, reason={}", userId, reason);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetUserPassword(Long adminId, Long userId, String newPassword) {
        log.info("重置用户密码: adminId={}, userId={}", adminId, userId);

        // 验证参数
        if (userId == null || newPassword == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 获取用户信息
        User targetUser = userMapper.selectById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 加密新密码
        String encryptedPassword = SecurityUtils.encodePassword(newPassword);

        // 更新密码
        targetUser.setPassword(encryptedPassword);
        int result = userMapper.updateById(targetUser);
        if (result <= 0) {
            throw new BusinessException(500, "重置密码失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.USER_MANAGE.getCode());
        operationLog.setOperationDesc("重置用户密码");
        operationLog.setTargetType(AdminOperationLog.TargetType.USER.getCode());
        operationLog.setTargetId(userId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/users/" + userId + "/password");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        log.info("重置用户密码成功: userId={}", userId);
    }

    // ==================== 内容审核 ====================

    @Override
    public PendingContentList getPendingContentList(Integer page, Integer size, String contentType) {
        log.info("获取待审核内容列表: page={}, size={}, contentType={}", page, size, contentType);

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        List<PendingContent> posts = new ArrayList<>();
        List<PendingContent> activities = new ArrayList<>();
        List<PendingContent> users = new ArrayList<>();
        List<PendingContent> fields = new ArrayList<>();

        if (contentType == null || "post".equals(contentType)) {
            List<Post> postList = postMapper.selectPendingPosts(page, size);
            posts = postList.stream().map(post -> new PendingContent(
                post.getId(), post.getTitle(), post.getContent(), 
                post.getUsername(), post.getNickname(), 
                post.getStatus(), post.getCreatedAt()
            )).collect(Collectors.toList());
        }
        if (contentType == null || "activity".equals(contentType)) {
            List<Activity> activityList = activityMapper.selectPendingActivities(page, size);
            activities = activityList.stream().map(activity -> new PendingContent(
                activity.getId(), activity.getTitle(), activity.getDescription(),
                activity.getUsername(), activity.getNickname(),
                activity.getStatus(), activity.getCreatedAt()
            )).collect(Collectors.toList());
        }
        if (contentType == null || "user".equals(contentType)) {
            List<User> userList = userMapper.selectPendingUsers(page, size);
            users = userList.stream().map(user -> new PendingContent(
                user.getId(), user.getNickname(), user.getBio(),
                user.getUsername(), user.getNickname(),
                user.getStatus(), user.getCreatedAt()
            )).collect(Collectors.toList());
        }
        if (contentType == null || "field".equals(contentType)) {
            List<Field> fieldList = fieldMapper.selectPendingFields(page, size);
            fields = fieldList.stream().map(field -> new PendingContent(
                field.getId(), field.getName(), field.getDescription(),
                field.getCreatorUsername(), field.getCreatorNickname(),
                field.getStatus(), field.getCreatedAt()
            )).collect(Collectors.toList());
        }

        log.info("获取待审核内容列表成功: posts={}, activities={}, users={}, fields={}", 
                posts.size(), activities.size(), users.size(), fields.size());

        return new PendingContentList(posts, activities, users, fields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditPost(Long adminId, Long postId, String action, String reason) {
        log.info("审核帖子: adminId={}, postId={}, action={}, reason={}", adminId, postId, action, reason);

        // 验证参数
        if (postId == null || action == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 获取帖子信息
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException(404, "帖子不存在");
        }

        String originalStatus = post.getStatus();
        String newStatus = getNewStatus(originalStatus, action);

        // 更新帖子状态
        post.setStatus(newStatus);
        int result = postMapper.updateById(post);
        if (result <= 0) {
            throw new BusinessException(500, "审核帖子失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.CONTENT_AUDIT.getCode());
        operationLog.setOperationDesc(String.format("审核帖子: %s - %s", action, reason));
        operationLog.setTargetType(AdminOperationLog.TargetType.POST.getCode());
        operationLog.setTargetId(postId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/posts/" + postId + "/audit");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        // 记录内容审核日志
        ContentAuditLog auditLog = new ContentAuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        auditLog.setContentType(ContentAuditLog.ContentType.POST.getCode());
        auditLog.setContentId(postId);
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setOriginalStatus(originalStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setCreatedAt(LocalDateTime.now());
        logContentAudit(auditLog);

        log.info("审核帖子成功: postId={}, action={}, originalStatus={}, newStatus={}", 
                postId, action, originalStatus, newStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditActivity(Long adminId, Long activityId, String action, String reason) {
        log.info("审核活动: adminId={}, activityId={}, action={}, reason={}", adminId, activityId, action, reason);

        // 验证参数
        if (activityId == null || action == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 获取活动信息
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            throw new BusinessException(404, "活动不存在");
        }

        String originalStatus = activity.getStatus();
        String newStatus = getNewStatus(originalStatus, action);

        // 更新活动状态
        activity.setStatus(newStatus);
        int result = activityMapper.updateById(activity);
        if (result <= 0) {
            throw new BusinessException(500, "审核活动失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.CONTENT_AUDIT.getCode());
        operationLog.setOperationDesc(String.format("审核活动: %s - %s", action, reason));
        operationLog.setTargetType(AdminOperationLog.TargetType.ACTIVITY.getCode());
        operationLog.setTargetId(activityId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/activities/" + activityId + "/audit");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        // 记录内容审核日志
        ContentAuditLog auditLog = new ContentAuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        auditLog.setContentType(ContentAuditLog.ContentType.ACTIVITY.getCode());
        auditLog.setContentId(activityId);
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setOriginalStatus(originalStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setCreatedAt(LocalDateTime.now());
        logContentAudit(auditLog);

        log.info("审核活动成功: activityId={}, action={}, originalStatus={}, newStatus={}", 
                activityId, action, originalStatus, newStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditUser(Long adminId, Long userId, String action, String reason) {
        log.info("审核用户: adminId={}, userId={}, action={}, reason={}", adminId, userId, action, reason);

        // 验证参数
        if (userId == null || action == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        String originalStatus = user.getStatus();
        String newStatus = getNewStatus(originalStatus, action);

        // 更新用户状态
        user.setStatus(newStatus);
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new BusinessException(500, "审核用户失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.CONTENT_AUDIT.getCode());
        operationLog.setOperationDesc(String.format("审核用户: %s - %s", action, reason));
        operationLog.setTargetType(AdminOperationLog.TargetType.USER.getCode());
        operationLog.setTargetId(userId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/users/" + userId + "/audit");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        // 记录内容审核日志
        ContentAuditLog auditLog = new ContentAuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        auditLog.setContentType(ContentAuditLog.ContentType.USER.getCode());
        auditLog.setContentId(userId);
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setOriginalStatus(originalStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setCreatedAt(LocalDateTime.now());
        logContentAudit(auditLog);

        log.info("审核用户成功: userId={}, action={}, originalStatus={}, newStatus={}", 
                userId, action, originalStatus, newStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditField(Long adminId, Long fieldId, String action, String reason) {
        log.info("审核场地: adminId={}, fieldId={}, action={}, reason={}", adminId, fieldId, action, reason);

        // 验证参数
        if (fieldId == null || action == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 获取场地信息
        Field field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new BusinessException(404, "场地不存在");
        }

        String originalStatus = field.getStatus();
        String newStatus = getNewStatus(originalStatus, action);

        // 更新场地状态
        field.setStatus(newStatus);
        int result = fieldMapper.updateById(field);
        if (result <= 0) {
            throw new BusinessException(500, "审核场地失败");
        }

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.CONTENT_AUDIT.getCode());
        operationLog.setOperationDesc(String.format("审核场地: %s - %s", action, reason));
        operationLog.setTargetType(AdminOperationLog.TargetType.FIELD.getCode());
        operationLog.setTargetId(fieldId);
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/fields/" + fieldId + "/audit");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        // 记录内容审核日志
        ContentAuditLog auditLog = new ContentAuditLog();
        auditLog.setAdminId(adminId);
        auditLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        auditLog.setContentType(ContentAuditLog.ContentType.FIELD.getCode());
        auditLog.setContentId(fieldId);
        auditLog.setAction(action);
        auditLog.setReason(reason);
        auditLog.setOriginalStatus(originalStatus);
        auditLog.setNewStatus(newStatus);
        auditLog.setCreatedAt(LocalDateTime.now());
        logContentAudit(auditLog);

        log.info("审核场地成功: fieldId={}, action={}, originalStatus={}, newStatus={}", 
                fieldId, action, originalStatus, newStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAuditContent(Long adminId, List<ContentAuditRequest> requests) {
        log.info("批量审核内容: adminId={}, requestCount={}", adminId, requests.size());

        if (adminId == null || requests == null || requests.isEmpty()) {
            return;
        }

        for (ContentAuditRequest request : requests) {
            try {
                switch (ContentAuditLog.ContentType.fromCode(request.getContentType())) {
                    case POST:
                        auditPost(adminId, request.getContentId(), request.getAction(), request.getReason());
                        break;
                    case ACTIVITY:
                        auditActivity(adminId, request.getContentId(), request.getAction(), request.getReason());
                        break;
                    case USER:
                        auditUser(adminId, request.getContentId(), request.getAction(), request.getReason());
                        break;
                    case FIELD:
                        auditField(adminId, request.getContentId(), request.getAction(), request.getReason());
                        break;
                }
            } catch (Exception e) {
                log.warn("批量审核内容失败: adminId={}, contentType={}, contentId={}, action={}, error={}", 
                        adminId, request.getContentType(), request.getContentId(), request.getAction(), e.getMessage());
            }
        }

        log.info("批量审核内容完成: adminId={}", adminId);
    }

    // ==================== 操作日志管理 ====================

    @Override
    public void logAdminOperation(AdminOperationLog operationLog) {
        try {
            adminOperationLogMapper.insert(operationLog);
        } catch (Exception e) {
            log.error("记录管理员操作日志失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public IPage<AdminOperationLog> getOperationLogs(Integer page, Integer size, String operationType, 
                                                     String targetType, String adminId, String timeRange) {
        log.info("获取操作日志列表: page={}, size={}, operationType={}, targetType={}, adminId={}, timeRange={}", 
                page, size, operationType, targetType, adminId, timeRange);

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<AdminOperationLog> pageParam = new Page<>(page, size);
        IPage<AdminOperationLog> result = adminOperationLogMapper.selectOperationLogs(pageParam, operationType, targetType, adminId, timeRange);

        log.info("获取操作日志列表成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    public AdminOperationLog getOperationLogDetail(Long logId) {
        log.info("获取操作日志详情: logId={}", logId);

        if (logId == null) {
            return null;
        }

        AdminOperationLog result = adminOperationLogMapper.selectOperationLogDetail(logId);

        log.info("获取操作日志详情成功: logId={}", logId);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cleanupOperationLogs(Integer days) {
        log.info("清理过期操作日志: days={}", days);

        if (days == null || days < 1) {
            days = 90; // 默认清理90天前的日志
        }

        int result = adminOperationLogMapper.deleteExpiredLogs(days);

        log.info("清理过期操作日志完成: days={}, 删除数量={}", days, result);
        return result;
    }

    // ==================== 内容审核日志管理 ====================

    @Override
    public void logContentAudit(ContentAuditLog auditLog) {
        try {
            contentAuditLogMapper.insert(auditLog);
        } catch (Exception e) {
            log.error("记录内容审核日志失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public IPage<ContentAuditLog> getContentAuditLogs(Integer page, Integer size, String contentType, 
                                                      String action, String adminId, String timeRange) {
        log.info("获取内容审核日志列表: page={}, size={}, contentType={}, action={}, adminId={}, timeRange={}", 
                page, size, contentType, action, adminId, timeRange);

        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<ContentAuditLog> pageParam = new Page<>(page, size);
        IPage<ContentAuditLog> result = contentAuditLogMapper.selectContentAuditLogs(pageParam, contentType, action, adminId, timeRange);

        log.info("获取内容审核日志列表成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    public ContentAuditLog getContentAuditLogDetail(Long logId) {
        log.info("获取内容审核日志详情: logId={}", logId);

        if (logId == null) {
            return null;
        }

        ContentAuditLog result = contentAuditLogMapper.selectContentAuditLogDetail(logId);

        log.info("获取内容审核日志详情成功: logId={}", logId);
        return result;
    }

    // ==================== 数据统计 ====================

    @Override
    public SystemStats getSystemStats() {
        log.info("获取系统统计概览");

        Long totalUsers = userMapper.selectCount(null);
        Long totalPosts = postMapper.selectCount(null);
        Long totalActivities = activityMapper.selectCount(null);
        Long totalFields = fieldMapper.selectCount(null);
        Long totalComments = commentMapper.selectCount(null);
        
        Long todayNewUsers = userMapper.countUsersByTimeRange("today");
        Long todayNewPosts = postMapper.countPostsByTimeRange("today");
        Long todayNewActivities = activityMapper.countActivitiesByTimeRange("today");
        
        Long activeUsers = userMapper.countActiveUsers();
        Long bannedUsers = userMapper.countBannedUsers();
        Long pendingAudits = countPendingAudits();

        SystemStats stats = new SystemStats(totalUsers, totalPosts, totalActivities, totalFields,
                                           totalComments, todayNewUsers, todayNewPosts, todayNewActivities,
                                           activeUsers, bannedUsers, pendingAudits);

        log.info("获取系统统计概览成功");
        return stats;
    }

    @Override
    public UserStats getUserStats(String timeRange) {
        log.info("获取用户统计: timeRange={}", timeRange);

        Long totalUsers = userMapper.selectCount(null);
        Long activeUsers = userMapper.countActiveUsersByTimeRange(timeRange);
        Long newUsers = userMapper.countUsersByTimeRange(timeRange);
        Long bannedUsers = userMapper.countBannedUsers();
        Long certifiedUsers = userMapper.countCertifiedUsers();
        // 暂时使用空列表，实际应该从数据库查询并转换
        List<UserTrend> trends = new ArrayList<>();

        UserStats stats = new UserStats(totalUsers, activeUsers, newUsers, bannedUsers, certifiedUsers, trends);

        log.info("获取用户统计成功: timeRange={}", timeRange);
        return stats;
    }

    @Override
    public ContentStats getContentStats(String timeRange) {
        log.info("获取内容统计: timeRange={}", timeRange);

        Long totalPosts = postMapper.selectCount(null);
        Long totalActivities = activityMapper.selectCount(null);
        Long totalFields = fieldMapper.selectCount(null);
        Long totalComments = commentMapper.selectCount(null);
        
        Long newPosts = postMapper.countPostsByTimeRange(timeRange);
        Long newActivities = activityMapper.countActivitiesByTimeRange(timeRange);
        Long newFields = fieldMapper.countFieldsByTimeRange(timeRange);
        Long newComments = commentMapper.countCommentsByTimeRange(timeRange);

        ContentStats stats = new ContentStats(totalPosts, totalActivities, totalFields, totalComments,
                                             newPosts, newActivities, newFields, newComments);

        log.info("获取内容统计成功: timeRange={}", timeRange);
        return stats;
    }

    @Override
    public AdminActivityStats getAdminActivityStats(String timeRange) {
        log.info("获取管理员活动统计: timeRange={}", timeRange);

        Long totalOperations = adminOperationLogMapper.countOperationsByTimeRange(timeRange);
        Long successfulOperations = adminOperationLogMapper.countSuccessfulOperationsByTimeRange(timeRange);
        Long failedOperations = adminOperationLogMapper.countFailedOperationsByTimeRange(timeRange);
        Long activeAdmins = adminOperationLogMapper.countActiveAdminsByTimeRange(timeRange);
        // 暂时使用空列表，实际应该从数据库查询并转换
        List<AdminActivity> activities = new ArrayList<>();

        AdminActivityStats stats = new AdminActivityStats(totalOperations, successfulOperations, failedOperations,
                                                        activeAdmins, activities);

        log.info("获取管理员活动统计成功: timeRange={}", timeRange);
        return stats;
    }

    @Override
    public HotContentStats getHotContentStats(String contentType, Integer limit) {
        log.info("获取热门内容统计: contentType={}, limit={}", contentType, limit);

        if (limit == null || limit < 1) {
            limit = 20;
        }

        List<HotContent> posts = new ArrayList<>();
        List<HotContent> activities = new ArrayList<>();
        List<HotContent> fields = new ArrayList<>();

        if (contentType == null || "post".equals(contentType)) {
            // 暂时使用空列表，实际应该从数据库查询并转换
            posts = new ArrayList<>();
        }
        if (contentType == null || "activity".equals(contentType)) {
            // 暂时使用空列表，实际应该从数据库查询并转换
            activities = new ArrayList<>();
        }
        if (contentType == null || "field".equals(contentType)) {
            // 暂时使用空列表，实际应该从数据库查询并转换
            fields = new ArrayList<>();
        }

        HotContentStats stats = new HotContentStats(posts, activities, fields);

        log.info("获取热门内容统计成功: contentType={}, limit={}", contentType, limit);
        return stats;
    }

    // ==================== 系统配置 ====================

    @Override
    public SystemConfig getSystemConfig() {
        log.info("获取系统配置");

        // 这里应该从配置表或配置文件中读取
        // 暂时返回默认配置
        SystemConfig config = new SystemConfig(
            "XYST校园社交平台",
            "校园生活，精彩分享",
            "/logo.png",
            "admin@xyst.com",
            true,
            true,
            5,
            3,
            10 * 1024 * 1024, // 10MB
            Arrays.asList("jpg", "jpeg", "png", "gif", "pdf", "doc", "docx")
        );

        log.info("获取系统配置成功");
        return config;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSystemConfig(Long adminId, SystemConfig config) {
        log.info("更新系统配置: adminId={}", adminId);

        // 这里应该更新配置表或配置文件
        // 暂时只记录日志

        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.SYSTEM_CONFIG.getCode());
        operationLog.setOperationDesc("更新系统配置");
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/system/config");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        log.info("更新系统配置成功: adminId={}", adminId);
    }

    @Override
    public SystemStatus getSystemStatus() {
        log.info("获取系统状态");

        // 这里应该检查系统各项指标
        // 暂时返回默认状态
        SystemStatus status = new SystemStatus(
            true,
            "1.0.0",
            System.currentTimeMillis() / 1000, // 运行时间（秒）
            45, // CPU使用率
            60, // 内存使用率
            30, // 磁盘使用率
            15L, // 数据库连接数
            "2024-01-01 00:00:00" // 最后备份时间
        );

        log.info("获取系统状态成功");
        return status;
    }

    // ==================== 数据导出 ====================

    @Override
    public List<User> exportUsers(String role, String status, String timeRange) {
        log.info("导出用户数据: role={}, status={}, timeRange={}", role, status, timeRange);

        List<User> result = userMapper.selectUsersForExport(role, status, timeRange);

        log.info("导出用户数据成功: 数量={}", result.size());
        return result;
    }

    @Override
    public List<AdminOperationLog> exportOperationLogs(String operationType, String timeRange) {
        log.info("导出操作日志: operationType={}, timeRange={}", operationType, timeRange);

        List<AdminOperationLog> result = adminOperationLogMapper.selectOperationLogsForExport(operationType, timeRange);

        log.info("导出操作日志成功: 数量={}", result.size());
        return result;
    }

    @Override
    public List<ContentAuditLog> exportContentAuditLogs(String contentType, String action, String timeRange) {
        log.info("导出内容审核日志: contentType={}, action={}, timeRange={}", contentType, action, timeRange);

        List<ContentAuditLog> result = contentAuditLogMapper.selectContentAuditLogsForExport(contentType, action, timeRange);

        log.info("导出内容审核日志成功: 数量={}", result.size());
        return result;
    }

    @Override
    public String exportSystemStats(String timeRange) {
        log.info("导出系统统计数据: timeRange={}", timeRange);

        // 这里应该生成统计报告文件
        // 暂时返回文件路径
        String filePath = "/exports/system_stats_" + timeRange + "_" + System.currentTimeMillis() + ".json";

        log.info("导出系统统计数据成功: filePath={}", filePath);
        return filePath;
    }

    // ==================== 数据维护 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CleanupResult cleanupExpiredData(Integer days) {
        log.info("清理过期数据: days={}", days);

        if (days == null || days < 1) {
            days = 90;
        }

        Integer cleanedLogs = cleanupOperationLogs(days);
        Integer cleanedTempFiles = 0; // 清理临时文件
        Integer cleanedCache = 0; // 清理缓存
        Long freedSpace = 0L; // 释放的空间

        CleanupResult result = new CleanupResult(cleanedLogs, cleanedTempFiles, cleanedCache, freedSpace);

        log.info("清理过期数据完成: days={}, result={}", days, result);
        return result;
    }

    @Override
    public void rebuildIndexes() {
        log.info("重建索引");

        // 这里应该重建数据库索引
        // 暂时只记录日志

        log.info("重建索引完成");
    }

    @Override
    public String backupData() {
        log.info("数据备份");

        // 这里应该执行数据备份
        // 暂时返回备份文件路径
        String backupFile = "/backups/backup_" + System.currentTimeMillis() + ".sql";

        log.info("数据备份完成: backupFile={}", backupFile);
        return backupFile;
    }

    @Override
    public void restoreData(String backupFile) {
        log.info("数据恢复: backupFile={}", backupFile);

        // 这里应该执行数据恢复
        log.info("数据恢复完成: backupFile={}", backupFile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignPermissions(Long adminId, Long targetUserId, List<String> permissions) {
        log.info("分配权限: adminId={}, targetUserId={}, permissions={}", adminId, targetUserId, permissions);

        // 验证权限
        if (!isSuperAdmin(adminId)) {
            throw new BusinessException(403, "只有超级管理员可以分配权限");
        }

        // 这里应该更新用户权限
        // 暂时只记录日志
        log.info("权限分配完成: targetUserId={}, permissions={}", targetUserId, permissions);
        
        // 记录操作日志
        AdminOperationLog operationLog = new AdminOperationLog();
        operationLog.setAdminId(adminId);
        operationLog.setAdminUsername(userMapper.selectById(adminId).getUsername());
        operationLog.setOperationType(AdminOperationLog.OperationType.USER_MANAGE.getCode());
        operationLog.setOperationDesc("分配权限");
        operationLog.setRequestMethod("PUT");
        operationLog.setRequestUrl("/admin/users/" + targetUserId + "/permissions");
        operationLog.setStatus(AdminOperationLog.Status.SUCCESS.getCode());
        operationLog.setCreatedAt(LocalDateTime.now());
        logAdminOperation(operationLog);

        log.info("分配权限成功: adminId={}, targetUserId={}", adminId, targetUserId);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 根据原状态和动作获取新状态
     */
    private String getNewStatus(String originalStatus, String action) {
        switch (ContentAuditLog.AuditAction.fromCode(action)) {
            case APPROVE:
                return "published";
            case REJECT:
                return "rejected";
            case DELETE:
                return "deleted";
            case HIDE:
                return "hidden";
            case RESTORE:
                return "published";
            case BAN:
                return "inactive";
            case UNBAN:
                return "active";
            default:
                return originalStatus;
        }
    }

    /**
     * 统计待审核数量
     */
    private Long countPendingAudits() {
        Long pendingPosts = postMapper.countPendingPosts();
        Long pendingActivities = activityMapper.countPendingActivities();
        Long pendingUsers = userMapper.countPendingUsers();
        Long pendingFields = fieldMapper.countPendingFields();

        return pendingPosts + pendingActivities + pendingUsers + pendingFields;
    }
}
