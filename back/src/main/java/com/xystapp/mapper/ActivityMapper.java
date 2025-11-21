package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.dto.ActivityQueryRequest;
import com.xystapp.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 活动Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    /**
     * 分页查询活动列表（包含用户信息）
     */
    IPage<Activity> selectActivitiesWithUser(Page<Activity> page, @Param("query") ActivityQueryRequest query);

    /**
     * 根据ID查询活动详情（包含用户信息）
     */
    Activity selectActivityWithUserById(@Param("id") Long id);

    /**
     * 查询用户的活动列表
     */
    IPage<Activity> selectActivitiesByUserId(Page<Activity> page, @Param("userId") Long userId, @Param("status") String status);

    /**
     * 查询用户报名的活动列表
     */
    IPage<Activity> selectSignedUpActivities(Page<Activity> page, @Param("userId") Long userId);

    /**
     * 更新活动统计信息
     */
    int updateActivityStats(@Param("id") Long id, @Param("likesCount") Integer likesCount, 
                           @Param("commentsCount") Integer commentsCount, @Param("participants") Integer participants);

    /**
     * 批量查询活动信息
     */
    List<Activity> selectActivitiesByIds(@Param("ids") List<Long> ids);

    /**
     * 检查用户是否是活动组织者
     */
    boolean isActivityOwner(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 检查用户是否已报名活动
     */
    boolean isUserSignedUp(@Param("activityId") Long activityId, @Param("userId") Long userId);

    /**
     * 获取活动当前报名人数
     */
    Integer getActivityParticipantCount(@Param("activityId") Long activityId);

    /**
     * 增加活动参与人数
     */
    int incrementParticipants(@Param("activityId") Long activityId);

    /**
     * 减少活动参与人数
     */
    int decrementParticipants(@Param("activityId") Long activityId);

    /**
     * 查询即将开始的活动
     */
    IPage<Activity> selectUpcomingActivities(Page<Activity> page, @Param("days") Integer days);

    /**
     * 查询热门活动（按参与人数排序）
     */
    IPage<Activity> selectPopularActivities(Page<Activity> page, @Param("limit") Integer limit);

    /**
     * 更新活动点赞数
     */
    int updateLikes(@Param("activityId") Long activityId, @Param("increment") int increment);

    // ==================== 搜索相关方法 ====================

    /**
     * 全文搜索活动
     */
    List<Activity> searchActivitiesByKeyword(@Param("keyword") String keyword, 
                                            @Param("page") Integer page, 
                                            @Param("size") Integer size);

    /**
     * 全文搜索活动（带用户信息）
     */
    List<Activity> searchActivitiesWithUserByKeyword(@Param("keyword") String keyword, 
                                                    @Param("page") Integer page, 
                                                    @Param("size") Integer size);

    /**
     * 统计搜索结果数量
     */
    Long countSearchActivitiesByKeyword(@Param("keyword") String keyword);

    /**
     * 按地点搜索活动
     */
    List<Activity> selectActivitiesByLocation(@Param("location") String location, 
                                             @Param("page") Integer page, 
                                             @Param("size") Integer size);

    /**
     * 按类型搜索活动
     */
    List<Activity> selectActivitiesByType(@Param("activityType") String activityType, 
                                         @Param("page") Integer page, 
                                         @Param("size") Integer size);

    /**
     * 按状态搜索活动
     */
    List<Activity> selectActivitiesByStatus(@Param("status") String status, 
                                           @Param("page") Integer page, 
                                           @Param("size") Integer size);

    /**
     * 获取相关活动（基于类型或地点）
     */
    List<Activity> selectRelatedActivities(@Param("activityId") Long activityId, 
                                          @Param("limit") Integer limit);

    /**
     * 更新活动收藏数
     */
    int updateStarsCount(@Param("id") Long id, @Param("delta") int delta);

    /**
     * 更新活动评论数
     */
    int updateCommentsCount(@Param("id") Long id, @Param("delta") int delta);

    // ==================== 管理员功能相关方法 ====================

    /**
     * 获取待审核活动列表
     */
    List<Activity> selectPendingActivities(@Param("page") Integer page, @Param("size") Integer size);

    /**
     * 统计待审核活动数量
     */
    Long countPendingActivities();

    /**
     * 按时间范围统计活动数量
     */
    Long countActivitiesByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 获取热门活动
     */
    List<Activity> selectHotActivities(@Param("limit") Integer limit);

    // ==================== 场地关联相关方法 ====================

    /**
     * 根据场地ID查询活动列表（分页）
     */
    IPage<Activity> selectActivitiesByFieldId(Page<Activity> page, @Param("fieldId") Long fieldId);

    /**
     * 根据标签ID查询活动列表（通过场地关联，分页）
     */
    IPage<Activity> selectActivitiesByTagId(Page<Activity> page, @Param("tagId") Long tagId);

    /**
     * 查询活动详情（包含场地和标签信息）
     */
    Activity selectActivityWithFieldAndTagsById(@Param("id") Long id);

    /**
     * 根据场地ID查询活动列表（包含场地信息）
     */
    List<Activity> selectActivitiesWithFieldByFieldId(@Param("fieldId") Long fieldId, @Param("limit") Integer limit);
}
