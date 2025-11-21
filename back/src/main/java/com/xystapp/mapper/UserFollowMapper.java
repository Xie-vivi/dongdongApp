package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关注Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface UserFollowMapper extends BaseMapper<UserFollow> {

    /**
     * 获取关注列表（包含用户信息）
     */
    IPage<UserFollow> selectFollowingWithUserInfo(Page<UserFollow> page, @Param("userId") Long userId);

    /**
     * 获取粉丝列表（包含用户信息）
     */
    IPage<UserFollow> selectFollowersWithUserInfo(Page<UserFollow> page, @Param("userId") Long userId);

    /**
     * 获取互相关注列表（包含用户信息）
     */
    IPage<UserFollow> selectMutualFollowsWithUserInfo(Page<UserFollow> page, @Param("userId") Long userId);

    /**
     * 统计关注数量
     */
    Long countFollowing(@Param("userId") Long userId);

    /**
     * 统计粉丝数量
     */
    Long countFollowers(@Param("userId") Long userId);

    /**
     * 统计互相关注数量
     */
    Long countMutualFollows(@Param("userId") Long userId);

    /**
     * 获取关注用户ID列表
     */
    List<Long> selectFollowingIds(@Param("userId") Long userId);

    /**
     * 检查是否互相关注
     */
    boolean isMutualFollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 批量检查关注状态
     */
    List<Long> selectMutualFollowingIds(@Param("userId") Long userId, @Param("targetIds") List<Long> targetIds);
}
