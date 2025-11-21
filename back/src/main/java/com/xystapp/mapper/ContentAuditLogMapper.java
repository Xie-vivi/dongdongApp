package com.xystapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xystapp.entity.ContentAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 内容审核日志Mapper接口
 * 
 * @author XYST Team
 * @version 1.0.0
 */
@Mapper
public interface ContentAuditLogMapper extends BaseMapper<ContentAuditLog> {

    /**
     * 分页查询内容审核日志
     */
    IPage<ContentAuditLog> selectContentAuditLogs(Page<ContentAuditLog> page,
                                                  @Param("contentType") String contentType,
                                                  @Param("action") String action,
                                                  @Param("adminId") String adminId,
                                                  @Param("timeRange") String timeRange);

    /**
     * 获取内容审核日志详情
     */
    ContentAuditLog selectContentAuditLogDetail(@Param("logId") Long logId);

    /**
     * 按内容类型统计审核数量
     */
    List<ContentAuditLog> selectAuditStatsByContentType(@Param("timeRange") String timeRange);

    /**
     * 按审核动作统计数量
     */
    List<ContentAuditLog> selectAuditStatsByAction(@Param("timeRange") String timeRange);

    /**
     * 按管理员统计审核数量
     */
    List<ContentAuditLog> selectAuditStatsByAdmin(@Param("timeRange") String timeRange);

    /**
     * 获取审核趋势统计
     */
    List<ContentAuditLog> selectAuditTrend(@Param("timeRange") String timeRange);

    /**
     * 按内容ID查询审核历史
     */
    List<ContentAuditLog> selectAuditHistoryByContent(@Param("contentType") String contentType,
                                                      @Param("contentId") Long contentId);

    /**
     * 按管理员查询审核记录
     */
    List<ContentAuditLog> selectAuditsByAdmin(@Param("adminId") Long adminId,
                                              @Param("limit") Integer limit);

    /**
     * 获取最近审核记录
     */
    List<ContentAuditLog> selectRecentAudits(@Param("limit") Integer limit);

    /**
     * 导出内容审核日志
     */
    List<ContentAuditLog> selectContentAuditLogsForExport(@Param("contentType") String contentType,
                                                          @Param("action") String action,
                                                          @Param("timeRange") String timeRange);

    /**
     * 统计审核总数
     */
    Long countAuditsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 统计通过审核数量
     */
    Long countApprovedAuditsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 统计拒绝审核数量
     */
    Long countRejectedAuditsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 统计删除操作数量
     */
    Long countDeletedAuditsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 统计隐藏操作数量
     */
    Long countHiddenAuditsByTimeRange(@Param("timeRange") String timeRange);

    /**
     * 获取审核效率统计
     */
    List<ContentAuditLog> selectAuditEfficiencyStats(@Param("timeRange") String timeRange);

    /**
     * 获取热门审核内容
     */
    List<ContentAuditLog> selectHotAuditContent(@Param("contentType") String contentType,
                                                @Param("limit") Integer limit);

    /**
     * 按理由分组统计
     */
    List<ContentAuditLog> selectAuditReasonStats(@Param("timeRange") String timeRange);

    /**
     * 获取重复审核内容
     */
    List<ContentAuditLog> selectRepeatedAuditContent(@Param("timeRange") String timeRange);

    /**
     * 统计管理员审核工作量
     */
    List<ContentAuditLog> selectAdminWorkloadStats(@Param("timeRange") String timeRange);

    /**
     * 获取审核质量统计
     */
    List<ContentAuditLog> selectAuditQualityStats(@Param("timeRange") String timeRange);

    /**
     * 按时间段统计审核分布
     */
    List<ContentAuditLog> selectAuditTimeDistribution(@Param("timeRange") String timeRange);

    /**
     * 获取待处理审核数量
     */
    Long countPendingAudits(@Param("contentType") String contentType);

    /**
     * 删除过期审核日志
     */
    int deleteExpiredAuditLogs(@Param("days") Integer days);

    /**
     * 批量插入审核日志
     */
    int batchInsertAuditLogs(@Param("logs") List<ContentAuditLog> logs);

    /**
     * 获取审核操作排行
     */
    List<ContentAuditLog> selectAuditActionRank(@Param("timeRange") String timeRange,
                                                @Param("limit") Integer limit);

    /**
     * 获取审核内容类型分布
     */
    List<ContentAuditLog> selectContentTypeDistribution(@Param("timeRange") String timeRange);

    /**
     * 统计审核响应时间
     */
    List<ContentAuditLog> selectAuditResponseTimeStats(@Param("timeRange") String timeRange);

    /**
     * 获取审核异常记录
     */
    List<ContentAuditLog> selectAbnormalAuditRecords(@Param("timeRange") String timeRange,
                                                     @Param("limit") Integer limit);
}
