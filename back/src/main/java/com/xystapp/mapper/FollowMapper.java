package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xystapp.entity.UserFollow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 关注关系Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface FollowMapper extends BaseMapper<UserFollow> {

    /**
     * 获取用户的关注列表
     */
    List<UserFollow> getFollowingList(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取用户的粉丝列表
     */
    List<UserFollow> getFollowerList(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取关注数量
     */
    int getFollowingCount(@Param("userId") Long userId);

    /**
     * 获取粉丝数量
     */
    int getFollowerCount(@Param("userId") Long userId);

    /**
     * 检查是否已关注
     */
    boolean isFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    /**
     * 取消关注
     */
    int unfollow(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
}
