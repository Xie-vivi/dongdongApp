package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.AdminOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理员操作日志Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface AdminOperationLogMapper extends BaseMapper<AdminOperationLog> {

    /**
     * 分页查询操作日志
     */
    IPage<AdminOperationLog> selectOperationLogs(Page<AdminOperationLog> page,
                                                 @Param("operationType") String operationType,
                                                 @Param("targetType") String targetType,
                                                 @Param("adminId") String adminId,
                                                 @Param("timeRange") String timeRange);

    /**
     * 获取操作日志详情
     */
    AdminOperationLog selectOperationLogDetail(@Param("logId") Long logId);

    /**
     * 删除过期日志
     */
    int deleteExpiredLogs(@Param("days") Integer days);

    /**
     * 按时间范围统计操作数量
     */
    Long countOperationsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 按时间范围统计成功操作数量
     */
    Long countSuccessfulOperationsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 按时间范围统计失败操作数量
     */
    Long countFailedOperationsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 按时间范围统计活跃管理员数量
     */
    Long countActiveAdminsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 获取管理员活动统计
     */
    List<AdminOperationLog> selectAdminActivities(@Param("timeRange") String timeRange);

    /**
     * 导出操作日志
     */
    List<AdminOperationLog> selectOperationLogsForExport(@Param("operationType") String operationType,
                                                         @Param("timeRange") String timeRange);

    /**
     * 统计操作类型分布
     */
    List<AdminOperationLog> selectOperationTypeStats(@Param("timeRange") String timeRange);

    /**
     * 统计目标类型分布
     */
    List<AdminOperationLog> selectTargetTypeStats(@Param("timeRange") String timeRange);

    /**
     * 获取管理员操作排行
     */
    List<AdminOperationLog> selectAdminOperationRank(@Param("timeRange") String timeRange,
                                                     @Param("limit") Integer limit);

    /**
     * 获取最近操作日志
     */
    List<AdminOperationLog> selectRecentOperations(@Param("limit") Integer limit);

    /**
     * 按管理员ID查询操作日志
     */
    List<AdminOperationLog> selectOperationsByAdminId(@Param("adminId") Long adminId,
                                                      @Param("limit") Integer limit);

    /**
     * 按目标查询操作日志
     */
    List<AdminOperationLog> selectOperationsByTarget(@Param("targetType") String targetType,
                                                     @Param("targetId") Long targetId,
                                                     @Param("limit") Integer limit);

    /**
     * 统计操作成功率
     */
    Double selectOperationSuccessRate(@Param("timeRange") String timeRange);

    /**
     * 获取操作趋势统计
     */
    List<AdminOperationLog> selectOperationTrend(@Param("timeRange") String timeRange);

    /**
     * 按IP地址统计操作
     */
    List<AdminOperationLog> selectOperationsByIp(@Param("ipAddress") String ipAddress,
                                                 @Param("limit") Integer limit);

    /**
     * 获取异常操作日志
     */
    List<AdminOperationLog> selectErrorOperations(@Param("timeRange") String timeRange,
                                                  @Param("limit") Integer limit);

    /**
     * 统计执行时间
     */
    List<AdminOperationLog> selectExecutionTimeStats(@Param("timeRange") String timeRange);
}
