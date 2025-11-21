package com.xystapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Field;
import com.xystapp.entity.FieldMember;
import com.xystapp.exception.BusinessException;
import com.xystapp.mapper.FieldMapper;
import com.xystapp.mapper.FieldMemberMapper;
import com.xystapp.mapper.UserMapper;
import com.xystapp.service.FieldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 场地服务实现类
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Service
public class FieldServiceImpl implements FieldService {

    private static final Logger log = LoggerFactory.getLogger(FieldServiceImpl.class);

    @Autowired
    private FieldMapper fieldMapper;

    @Autowired
    private FieldMemberMapper fieldMemberMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Field createField(String name, String description, String avatar, Long creatorId) {
        log.info("创建场地: name={}, creatorId={}", name, creatorId);

        // 验证参数
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(400, "场地名称不能为空");
        }
        if (creatorId == null) {
            throw new BusinessException(400, "创建者ID不能为空");
        }

        // 检查用户是否存在
        if (userMapper.selectById(creatorId) == null) {
            throw new BusinessException(404, "创建者不存在");
        }

        // 检查场地名称是否已存在
        QueryWrapper<Field> nameCheckWrapper = new QueryWrapper<>();
        nameCheckWrapper.eq("name", name.trim());
        if (fieldMapper.selectCount(nameCheckWrapper) > 0) {
            throw new BusinessException(400, "场地名称已存在");
        }

        // 创建场地
        Field field = new Field();
        field.setName(name.trim());
        field.setDescription(description);
        field.setAvatar(avatar);
        field.setCreatorId(creatorId);
        field.setMembersCount(1); // 创建者自动成为成员

        int result = fieldMapper.insert(field);
        if (result <= 0) {
            throw new BusinessException(500, "创建场地失败");
        }

        // 创建者自动成为场地管理员
        FieldMember creatorMember = new FieldMember();
        creatorMember.setUserId(creatorId);
        creatorMember.setFieldId(field.getId());
        creatorMember.setRole(FieldMember.Role.ADMIN.getCode());
        creatorMember.setJoinedAt(LocalDateTime.now());

        int memberResult = fieldMemberMapper.insert(creatorMember);
        if (memberResult <= 0) {
            throw new BusinessException(500, "添加创建者为成员失败");
        }

        log.info("创建场地成功: fieldId={}, creatorId={}", field.getId(), creatorId);
        return field;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Field updateField(Long fieldId, String name, String description, String avatar, Long operatorId) {
        log.info("更新场地信息: fieldId={}, name={}, operatorId={}", fieldId, name, operatorId);

        // 验证参数
        if (fieldId == null || operatorId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查场地是否存在
        Field field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new BusinessException(404, "场地不存在");
        }

        // 检查权限（只有创建者和管理员可以更新）
        if (!hasManagePermission(fieldId, operatorId)) {
            throw new BusinessException(403, "无权限修改场地信息");
        }

        // 检查名称是否重复（如果要修改名称）
        if (name != null && !name.trim().equals(field.getName())) {
            QueryWrapper<Field> nameCheckWrapper = new QueryWrapper<>();
            nameCheckWrapper.eq("name", name.trim());
            nameCheckWrapper.ne("id", fieldId);
            if (fieldMapper.selectCount(nameCheckWrapper) > 0) {
                throw new BusinessException(400, "场地名称已存在");
            }
            field.setName(name.trim());
        }

        // 更新字段
        if (description != null) {
            field.setDescription(description);
        }
        if (avatar != null) {
            field.setAvatar(avatar);
        }
        field.setUpdatedAt(LocalDateTime.now());

        int result = fieldMapper.updateById(field);
        if (result <= 0) {
            throw new BusinessException(500, "更新场地信息失败");
        }

        log.info("更新场地信息成功: fieldId={}", fieldId);
        return field;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteField(Long fieldId, Long operatorId) {
        log.info("删除场地: fieldId={}, operatorId={}", fieldId, operatorId);

        // 验证参数
        if (fieldId == null || operatorId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查场地是否存在
        Field field = fieldMapper.selectById(fieldId);
        if (field == null) {
            throw new BusinessException(404, "场地不存在");
        }

        // 检查权限（只有创建者可以删除）
        if (!field.getCreatorId().equals(operatorId)) {
            throw new BusinessException(403, "只有创建者可以删除场地");
        }

        // 删除所有成员记录
        fieldMemberMapper.deleteByFieldId(fieldId);

        // 删除场地
        int result = fieldMapper.deleteById(fieldId);
        if (result <= 0) {
            throw new BusinessException(500, "删除场地失败");
        }

        log.info("删除场地成功: fieldId={}", fieldId);
    }

    @Override
    public Field getFieldById(Long fieldId, Long userId) {
        log.info("获取场地详情: fieldId={}, userId={}", fieldId, userId);

        if (fieldId == null) {
            return null;
        }

        Field field = fieldMapper.selectFieldWithUserInfo(fieldId);
        if (field == null) {
            return null;
        }

        // 如果提供了用户ID，设置用户相关信息
        if (userId != null) {
            FieldMember memberInfo = fieldMemberMapper.selectByFieldIdAndUserId(fieldId, userId);
            if (memberInfo != null) {
                field.setCurrentUserRole(memberInfo.getRole());
                field.setIsMember(true);
                field.setIsAdmin(FieldMember.Role.ADMIN.getCode().equals(memberInfo.getRole()));
                field.setIsCreator(field.getCreatorId().equals(userId));
            } else {
                field.setIsMember(false);
                field.setIsAdmin(false);
                field.setIsCreator(false);
            }
        }

        // 获取场地统计信息
        field.setPostsCount(fieldMapper.countPostsByFieldId(fieldId).intValue());
        field.setActivitiesCount(fieldMapper.countActivitiesByFieldId(fieldId).intValue());

        log.info("获取场地详情成功: fieldId={}", fieldId);
        return field;
    }

    @Override
    public IPage<Field> getFieldList(Integer page, Integer size, String keyword) {
        log.info("获取场地列表: page={}, size={}, keyword={}", page, size, keyword);

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Field> pageParam = new Page<>(page, size);
        IPage<Field> result = fieldMapper.selectFieldList(pageParam, keyword);

        log.info("获取场地列表成功: 总数={}", result.getTotal());
        return result;
    }

    @Override
    public IPage<Field> getUserCreatedFields(Long userId, Integer page, Integer size) {
        log.info("获取用户创建的场地列表: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Field> pageParam = new Page<>(page, size);
        IPage<Field> result = fieldMapper.selectUserCreatedFields(pageParam, userId);

        log.info("获取用户创建的场地列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public IPage<Field> getUserJoinedFields(Long userId, Integer page, Integer size) {
        log.info("获取用户加入的场地列表: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Field> pageParam = new Page<>(page, size);
        IPage<Field> result = fieldMapper.selectUserJoinedFields(pageParam, userId);

        log.info("获取用户加入的场地列表成功: userId={}, 总数={}", userId, result.getTotal());
        return result;
    }

    @Override
    public IPage<Field> searchFields(String keyword, Integer page, Integer size) {
        log.info("搜索场地: keyword={}, page={}, size={}", keyword, page, size);

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new BusinessException(400, "搜索关键词不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<Field> pageParam = new Page<>(page, size);
        IPage<Field> result = fieldMapper.searchFields(pageParam, keyword.trim());

        log.info("搜索场地成功: keyword={}, 总数={}", keyword, result.getTotal());
        return result;
    }

    @Override
    public List<Field> getRecommendedFields(Long userId, Integer limit) {
        log.info("获取推荐场地: userId={}, limit={}", userId, limit);

        if (limit == null || limit < 1) {
            limit = 10;
        }

        List<Field> fields = fieldMapper.selectRecommendedFields(userId, limit);

        log.info("获取推荐场地成功: userId={}, 数量={}", userId, fields.size());
        return fields;
    }

    @Override
    public List<Field> getPopularFields(Integer limit) {
        log.info("获取热门场地: limit={}", limit);

        if (limit == null || limit < 1) {
            limit = 10;
        }

        List<Field> fields = fieldMapper.selectPopularFields(limit);

        log.info("获取热门场地成功: 数量={}", fields.size());
        return fields;
    }

    // ==================== 成员管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FieldMember joinField(Long fieldId, Long userId) {
        log.info("加入场地: fieldId={}, userId={}", fieldId, userId);

        // 验证参数
        if (fieldId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查场地是否存在
        if (!fieldExists(fieldId)) {
            throw new BusinessException(404, "场地不存在");
        }

        // 检查用户是否已经是成员
        if (isFieldMember(fieldId, userId)) {
            throw new BusinessException(400, "用户已经是场地成员");
        }

        // 检查用户是否可以加入场地
        if (!canJoinField(fieldId, userId)) {
            throw new BusinessException(403, "无法加入该场地");
        }

        // 添加成员
        FieldMember member = new FieldMember();
        member.setUserId(userId);
        member.setFieldId(fieldId);
        member.setRole(FieldMember.Role.MEMBER.getCode());
        member.setJoinedAt(LocalDateTime.now());

        int result = fieldMemberMapper.insert(member);
        if (result <= 0) {
            throw new BusinessException(500, "加入场地失败");
        }

        // 更新场地成员数量
        updateFieldMemberCount(fieldId);

        log.info("加入场地成功: fieldId={}, userId={}", fieldId, userId);
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveField(Long fieldId, Long userId) {
        log.info("退出场地: fieldId={}, userId={}", fieldId, userId);

        // 验证参数
        if (fieldId == null || userId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查用户是否是成员
        if (!isFieldMember(fieldId, userId)) {
            throw new BusinessException(400, "用户不是场地成员");
        }

        // 创建者不能退出自己的场地
        Field field = fieldMapper.selectById(fieldId);
        if (field != null && field.getCreatorId().equals(userId)) {
            throw new BusinessException(400, "创建者不能退出自己的场地");
        }

        // 删除成员记录
        int result = fieldMemberMapper.deleteByFieldIdAndUserId(fieldId, userId);
        if (result <= 0) {
            throw new BusinessException(500, "退出场地失败");
        }

        // 更新场地成员数量
        updateFieldMemberCount(fieldId);

        log.info("退出场地成功: fieldId={}, userId={}", fieldId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FieldMember inviteUserToField(Long fieldId, Long operatorId, Long targetUserId) {
        log.info("邀请用户加入场地: fieldId={}, operatorId={}, targetUserId={}", fieldId, operatorId, targetUserId);

        // 验证参数
        if (fieldId == null || operatorId == null || targetUserId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!hasManagePermission(fieldId, operatorId)) {
            throw new BusinessException(403, "无权限邀请用户");
        }

        // 检查目标用户是否存在
        if (userMapper.selectById(targetUserId) == null) {
            throw new BusinessException(404, "目标用户不存在");
        }

        // 检查目标用户是否已经是成员
        if (isFieldMember(fieldId, targetUserId)) {
            throw new BusinessException(400, "目标用户已经是场地成员");
        }

        // 添加成员
        FieldMember member = new FieldMember();
        member.setUserId(targetUserId);
        member.setFieldId(fieldId);
        member.setRole(FieldMember.Role.MEMBER.getCode());
        member.setJoinedAt(LocalDateTime.now());

        int result = fieldMemberMapper.insert(member);
        if (result <= 0) {
            throw new BusinessException(500, "邀请用户失败");
        }

        // 更新场地成员数量
        updateFieldMemberCount(fieldId);

        log.info("邀请用户加入场地成功: fieldId={}, targetUserId={}", fieldId, targetUserId);
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long fieldId, Long operatorId, Long targetUserId) {
        log.info("移除场地成员: fieldId={}, operatorId={}, targetUserId={}", fieldId, operatorId, targetUserId);

        // 验证参数
        if (fieldId == null || operatorId == null || targetUserId == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限
        if (!hasManagePermission(fieldId, operatorId)) {
            throw new BusinessException(403, "无权限移除成员");
        }

        // 检查目标用户是否是成员
        if (!isFieldMember(fieldId, targetUserId)) {
            throw new BusinessException(400, "目标用户不是场地成员");
        }

        // 不能移除创建者
        Field field = fieldMapper.selectById(fieldId);
        if (field != null && field.getCreatorId().equals(targetUserId)) {
            throw new BusinessException(400, "不能移除场地创建者");
        }

        // 删除成员记录
        int result = fieldMemberMapper.deleteByFieldIdAndUserId(fieldId, targetUserId);
        if (result <= 0) {
            throw new BusinessException(500, "移除成员失败");
        }

        // 更新场地成员数量
        updateFieldMemberCount(fieldId);

        log.info("移除场地成员成功: fieldId={}, targetUserId={}", fieldId, targetUserId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FieldMember updateMemberRole(Long fieldId, Long operatorId, Long targetUserId, String role) {
        log.info("更新成员角色: fieldId={}, operatorId={}, targetUserId={}, role={}", fieldId, operatorId, targetUserId, role);

        // 验证参数
        if (fieldId == null || operatorId == null || targetUserId == null || role == null) {
            throw new BusinessException(400, "参数不能为空");
        }

        // 检查权限（只有创建者可以修改角色）
        if (!isFieldCreator(fieldId, operatorId)) {
            throw new BusinessException(403, "只有创建者可以修改成员角色");
        }

        // 检查目标用户是否是成员
        FieldMember member = fieldMemberMapper.selectByFieldIdAndUserId(fieldId, targetUserId);
        if (member == null) {
            throw new BusinessException(400, "目标用户不是场地成员");
        }

        // 不能修改创建者的角色
        Field field = fieldMapper.selectById(fieldId);
        if (field != null && field.getCreatorId().equals(targetUserId)) {
            throw new BusinessException(400, "不能修改创建者的角色");
        }

        // 更新角色
        member.setRole(role);
        int result = fieldMemberMapper.updateById(member);
        if (result <= 0) {
            throw new BusinessException(500, "更新成员角色失败");
        }

        log.info("更新成员角色成功: fieldId={}, targetUserId={}, role={}", fieldId, targetUserId, role);
        return member;
    }

    @Override
    public IPage<FieldMember> getFieldMembers(Long fieldId, Integer page, Integer size, String role) {
        log.info("获取场地成员列表: fieldId={}, page={}, size={}, role={}", fieldId, page, size, role);

        if (fieldId == null) {
            throw new BusinessException(400, "场地ID不能为空");
        }

        // 设置默认值
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 20;
        }

        Page<FieldMember> pageParam = new Page<>(page, size);
        IPage<FieldMember> result = fieldMemberMapper.selectFieldMembers(pageParam, fieldId, role);

        log.info("获取场地成员列表成功: fieldId={}, 总数={}", fieldId, result.getTotal());
        return result;
    }

    @Override
    public Integer getFieldMemberCount(Long fieldId) {
        if (fieldId == null) {
            return 0;
        }

        return fieldMemberMapper.countByFieldId(fieldId);
    }

    @Override
    public FieldMember getUserFieldInfo(Long fieldId, Long userId) {
        if (fieldId == null || userId == null) {
            return null;
        }

        return fieldMemberMapper.selectFieldMemberWithUserInfo(fieldId, userId);
    }

    // ==================== 权限验证 ====================

    @Override
    public boolean isFieldMember(Long fieldId, Long userId) {
        if (fieldId == null || userId == null) {
            return false;
        }

        return fieldMemberMapper.selectByFieldIdAndUserId(fieldId, userId) != null;
    }

    @Override
    public boolean isFieldAdmin(Long fieldId, Long userId) {
        if (fieldId == null || userId == null) {
            return false;
        }

        FieldMember member = fieldMemberMapper.selectByFieldIdAndUserId(fieldId, userId);
        return member != null && FieldMember.Role.ADMIN.getCode().equals(member.getRole());
    }

    @Override
    public boolean isFieldCreator(Long fieldId, Long userId) {
        if (fieldId == null || userId == null) {
            return false;
        }

        Field field = fieldMapper.selectById(fieldId);
        return field != null && field.getCreatorId().equals(userId);
    }

    @Override
    public boolean hasManagePermission(Long fieldId, Long userId) {
        return isFieldCreator(fieldId, userId) || isFieldAdmin(fieldId, userId);
    }

    @Override
    public boolean fieldExists(Long fieldId) {
        if (fieldId == null) {
            return false;
        }

        return fieldMapper.selectById(fieldId) != null;
    }

    @Override
    public boolean canJoinField(Long fieldId, Long userId) {
        // 简化实现：只要场地存在且用户不是成员就可以加入
        // 可以根据需要添加更多限制（如用户状态、场地限制等）
        return fieldExists(fieldId) && !isFieldMember(fieldId, userId);
    }

    // ==================== 统计信息 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFieldMemberCount(Long fieldId) {
        // 使用原子SQL操作更新成员数量，避免并发问题
        fieldMapper.updateMemberCountAtomic(fieldId);
    }

    @Override
    public FieldStats getFieldStats(Long fieldId) {
        log.info("获取场地统计: fieldId={}", fieldId);

        if (fieldId == null) {
            throw new BusinessException(400, "场地ID不能为空");
        }

        Long totalMembers = (long) getFieldMemberCount(fieldId);
        Long totalPosts = fieldMapper.countPostsByFieldId(fieldId);
        Long totalActivities = fieldMapper.countActivitiesByFieldId(fieldId);
        Long newMembersToday = fieldMemberMapper.countNewMembersByFieldId(fieldId);
        Long newPostsToday = fieldMapper.countTodayPostsByFieldId(fieldId);
        Long newActivitiesToday = fieldMapper.countTodayActivitiesByFieldId(fieldId);

        FieldStats stats = new FieldStats(totalMembers, totalPosts, totalActivities,
                                         newMembersToday, newPostsToday, newActivitiesToday);

        log.info("获取场地统计成功: fieldId={}, totalMembers={}, totalPosts={}, totalActivities={}", 
                fieldId, totalMembers, totalPosts, totalActivities);

        return stats;
    }

    @Override
    public UserFieldStats getUserFieldStats(Long userId) {
        log.info("获取用户场地统计: userId={}", userId);

        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }

        Long createdFields = fieldMapper.countUserCreatedFields(userId);
        Long joinedFields = fieldMemberMapper.countUserJoinedFields(userId);
        Long managedFields = fieldMemberMapper.countUserManagedFields(userId);
        Long totalMembersInCreatedFields = fieldMapper.countTotalMembersInUserCreatedFields(userId);

        UserFieldStats stats = new UserFieldStats(createdFields, joinedFields, managedFields, totalMembersInCreatedFields);

        log.info("获取用户场地统计成功: userId={}, created={}, joined={}, managed={}", 
                userId, createdFields, joinedFields, managedFields);

        return stats;
    }

    // ==================== 批量操作 ====================

    @Override
    public List<Field> getFieldsByIds(List<Long> fieldIds, Long userId) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        return fieldMapper.selectFieldsByIds(fieldIds, userId);
    }

    @Override
    public List<FieldMember> getMembersByFieldIds(List<Long> fieldIds, Long userId) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        return fieldMemberMapper.selectMembersByFieldIds(fieldIds, userId);
    }

    @Override
    public boolean batchCheckManagePermission(List<Long> fieldIds, Long userId) {
        if (fieldIds == null || fieldIds.isEmpty() || userId == null) {
            return false;
        }

        for (Long fieldId : fieldIds) {
            if (!hasManagePermission(fieldId, userId)) {
                return false;
            }
        }
        return true;
    }

    // ==================== 业务查询 ====================

    @Override
    public List<FieldContentStats> getFieldContentStats(List<Long> fieldIds) {
        if (fieldIds == null || fieldIds.isEmpty()) {
            return new ArrayList<>();
        }

        return fieldMapper.selectFieldContentStats(fieldIds);
    }
}
