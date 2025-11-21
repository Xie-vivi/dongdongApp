package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理员Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查询管理员
     * 
     * @param username 用户名
     * @return 管理员信息
     */
    Admin selectByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询管理员
     * 
     * @param email 邮箱
     * @return 管理员信息
     */
    Admin selectByEmail(@Param("email") String email);

    /**
     * 获取管理员角色列表
     * 
     * @param adminId 管理员ID
     * @return 角色名称列表
     */
    List<String> selectAdminRoles(@Param("adminId") Long adminId);

    /**
     * 获取管理员权限列表
     * 
     * @param adminId 管理员ID
     * @return 权限名称列表
     */
    List<String> selectAdminPermissions(@Param("adminId") Long adminId);

    /**
     * 分页查询管理员列表（包含角色信息）
     * 
     * @param page 分页参数
     * @param status 状态筛选
     * @param keyword 关键词搜索
     * @return 管理员列表
     */
    IPage<Admin> selectAdminListWithRoles(Page<Admin> page, 
                                          @Param("status") String status,
                                          @Param("keyword") String keyword);

    /**
     * 获取管理员详细信息（包含角色和权限）
     * 
     * @param adminId 管理员ID
     * @return 管理员详细信息
     */
    Admin selectAdminDetailById(@Param("adminId") Long adminId);

    /**
     * 统计管理员数量
     * 
     * @param status 状态筛选
     * @return 管理员数量
     */
    Integer countAdminsByStatus(@Param("status") String status);

    /**
     * 获取最近登录的管理员列表
     * 
     * @param days 天数
     * @param limit 限制数量
     * @return 管理员列表
     */
    List<Admin> selectRecentLogins(@Param("days") Integer days, 
                                   @Param("limit") Integer limit);

    /**
     * 获取登录次数统计
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 登录统计
     */
    List<AdminLoginStats> selectLoginStats(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * 更新管理员登录信息
     * 
     * @param adminId 管理员ID
     * @param ip IP地址
     * @return 更新结果
     */
    int updateLoginInfo(@Param("adminId") Long adminId, @Param("ip") String ip);

    /**
     * 批量更新管理员状态
     * 
     * @param adminIds 管理员ID列表
     * @param status 状态
     * @return 更新结果
     */
    int batchUpdateStatus(@Param("adminIds") List<Long> adminIds, 
                          @Param("status") String status);

    /**
     * 检查用户名是否存在
     * 
     * @param username 用户名
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean checkUsernameExists(@Param("username") String username, 
                                @Param("excludeId") Long excludeId);

    /**
     * 检查邮箱是否存在
     * 
     * @param email 邮箱
     * @param excludeId 排除的ID
     * @return 是否存在
     */
    boolean checkEmailExists(@Param("email") String email, 
                             @Param("excludeId") Long excludeId);

    /**
     * 管理员登录统计内部类
     */
    class AdminLoginStats {
        private String date;
        private Integer loginCount;
        private Integer uniqueAdmins;

        // getters and setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public Integer getLoginCount() { return loginCount; }
        public void setLoginCount(Integer loginCount) { this.loginCount = loginCount; }
        public Integer getUniqueAdmins() { return uniqueAdmins; }
        public void setUniqueAdmins(Integer uniqueAdmins) { this.uniqueAdmins = uniqueAdmins; }
    }
}
