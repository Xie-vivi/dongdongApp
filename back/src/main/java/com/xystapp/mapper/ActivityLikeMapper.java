package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivityLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动点赞Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ActivityLikeMapper extends BaseMapper<ActivityLike> {

    /**
     * 获取活动点赞列表（包含用户信息）
     */
    IPage<ActivityLike> selectActivityLikesWithUserInfo(Page<ActivityLike> page, @Param("activityId") Long activityId);

    /**
     * 获取用户点赞的活动列表（包含活动信息）
     */
    IPage<ActivityLike> selectUserLikedActivitiesWithInfo(Page<ActivityLike> page, @Param("userId") Long userId);

    /**
     * 统计用户点赞的活动数量
     */
    Long countUserActivityLikes(@Param("userId") Long userId);

    /**
     * 获取用户点赞的活动ID列表
     */
    List<Long> selectLikedActivityIds(@Param("userId") Long userId);

    /**
     * 批量检查活动点赞状态
     */
    List<Long> selectLikedActivityIdsByUser(@Param("userId") Long userId, @Param("activityIds") List<Long> activityIds);
}
