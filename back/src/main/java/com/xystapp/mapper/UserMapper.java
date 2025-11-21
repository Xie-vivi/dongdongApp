package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户数据访问层
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    User selectByUsername(@Param("username") String username);

    /**
     * 根据UID查询用户
     */
    User selectByUid(@Param("uid") String uid);

    /**
     * 分页查询用户列表
     */
    IPage<User> selectUserPage(Page<User> page, @Param("keyword") String keyword);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查UID是否存在
     */
    boolean existsByUid(@Param("uid") String uid);

    /**
     * 更新用户状态
     */
    int updateUserStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 批量查询用户信息
     */
    List<User> selectUserByIds(@Param("ids") List<Long> ids);

    /**
     * 更新用户关注数
     */
    int updateFollowingCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 更新用户粉丝数
     */
    int updateFollowersCount(@Param("id") Long id, @Param("delta") int delta);

    // ==================== 管理员功能相关方法 ====================

    /**
     * 分页查询用户列表（管理员用）
     */
    IPage<User> selectUserListForAdmin(Page<User> page, @Param("keyword") String keyword, 
                                       @Param("role") String role, @Param("status") String status);

    /**
     * 统计活跃用户数量
     */
    Long countActiveUsers();

    /**
     * 按时间范围统计用户数量
     */
    Long countUsersByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 按时间范围统计活跃用户数量
     */
    Long countActiveUsersByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 统计被封禁用户数量
     */
    Long countBannedUsers();

    /**
     * 统计已认证用户数量
     */
    Long countCertifiedUsers();

    /**
     * 获取用户趋势统计
     */
    List<User> selectUserTrends(@Param("timeRange") String timeRange);

    /**
     * 统计待审核用户数量
     */
    Long countPendingUsers();

    /**
     * 获取待审核用户列表
     */
    List<User> selectPendingUsers(@Param("page") Integer page, @Param("size") Integer size);

    /**
     * 导出用户数据
     */
    List<User> selectUsersForExport(@Param("role") String role, @Param("status") String status, 
                                    @Param("timeRange") String timeRange);

    // ==================== 搜索相关方法 ====================

    /**
     * 全文搜索用户
     */
    List<User> searchUsersByKeyword(@Param("keyword") String keyword, 
                                   @Param("page") Integer page, 
                                   @Param("size") Integer size);

    /**
     * 统计搜索结果数量
     */
    Long countSearchUsersByKeyword(@Param("keyword") String keyword);

    /**
     * 按学校搜索用户
     */
    List<User> selectUsersBySchool(@Param("school") String school, 
                                  @Param("page") Integer page, 
                                  @Param("size") Integer size);

    /**
     * 按MBTI搜索用户
     */
    List<User> selectUsersByMbti(@Param("mbti") String mbti, 
                                @Param("page") Integer page, 
                                @Param("size") Integer size);

    /**
     * 按地点搜索用户
     */
    List<User> selectUsersByLocation(@Param("location") String location, 
                                    @Param("page") Integer page, 
                                    @Param("size") Integer size);

    /**
     * 获取相似用户（基于学校、MBTI等）
     */
    List<User> selectSimilarUsers(@Param("userId") Long userId, 
                                 @Param("limit") Integer limit);
}
