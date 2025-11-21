package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ActivityStar;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动收藏Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ActivityStarMapper extends BaseMapper<ActivityStar> {

    /**
     * 获取活动收藏列表（包含用户信息）
     */
    IPage<ActivityStar> selectActivityStarsWithUserInfo(Page<ActivityStar> page, @Param("activityId") Long activityId);

    /**
     * 获取用户收藏的活动列表（包含活动信息）
     */
    IPage<ActivityStar> selectUserStarredActivitiesWithInfo(Page<ActivityStar> page, @Param("userId") Long userId);

    /**
     * 统计用户收藏的活动数量
     */
    Long countUserActivityStars(@Param("userId") Long userId);

    /**
     * 获取用户收藏的活动ID列表
     */
    List<Long> selectStarredActivityIds(@Param("userId") Long userId);

    /**
     * 批量检查活动收藏状态
     */
    List<Long> selectStarredActivityIdsByUser(@Param("userId") Long userId, @Param("activityIds") List<Long> activityIds);
}
