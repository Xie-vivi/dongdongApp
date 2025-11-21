<template>
  <div class="analytics-overview">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">数据分析</h1>
        <p class="page-description">全面的数据分析和报表功能</p>
      </div>
      <div class="header-actions">
        <el-select
          v-model="timeRange"
          placeholder="选择时间范围"
          @change="handleTimeRangeChange"
        >
          <el-option label="最近7天" value="7d" />
          <el-option label="最近30天" value="30d" />
          <el-option label="最近90天" value="90d" />
          <el-option label="最近1年" value="1y" />
          <el-option label="自定义" value="custom" />
        </el-select>
        
        <el-button
          type="primary"
          :loading="loading"
          @click="refreshAllData"
        >
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 自定义日期范围选择器 -->
    <div v-if="timeRange === 'custom'" class="custom-date-range">
      <el-date-picker
        v-model="customDateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        @change="handleCustomDateChange"
      />
    </div>

    <!-- 概览统计卡片 -->
    <div class="overview-cards">
      <div class="stat-card">
        <div class="stat-icon users">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(overview.totalUsers) }}</div>
          <div class="stat-label">总用户数</div>
          <div class="stat-growth positive">
            <el-icon><TrendCharts /></el-icon>
            +{{ totalUsersGrowth }}%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon active">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(overview.activeUsers) }}</div>
          <div class="stat-label">活跃用户</div>
          <div class="stat-rate">{{ activeUsersRate }}% 活跃率</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon content">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(overview.totalContent) }}</div>
          <div class="stat-label">内容总数</div>
          <div class="stat-growth positive">
            <el-icon><TrendCharts /></el-icon>
            +8.3%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon health">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ systemHealthStatus.text }}</div>
          <div class="stat-label">系统健康</div>
          <el-tag :type="systemHealthStatus.type" size="small">
            {{ systemHealthStatus.text }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 快速访问卡片 -->
    <div class="quick-access">
      <h2 class="section-title">数据分析模块</h2>
      <div class="access-grid">
        <router-link
          to="/admin/analytics/user-behavior"
          class="access-card"
        >
          <div class="card-icon">
            <el-icon><DataAnalysis /></el-icon>
          </div>
          <div class="card-content">
            <h3>用户行为统计</h3>
            <p>分析用户活跃度、增长趋势、设备分布等</p>
            <div class="card-stats">
              <span class="stat">{{ formatNumber(overview.activeUsers) }} 活跃用户</span>
              <span class="stat">{{ totalUsersGrowth }}% 增长率</span>
            </div>
          </div>
          <div class="card-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </router-link>

        <router-link
          to="/admin/analytics/content-analysis"
          class="access-card"
        >
          <div class="card-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="card-content">
            <h3>内容数据分析</h3>
            <p>内容发布趋势、类型分布、用户参与度分析</p>
            <div class="card-stats">
              <span class="stat">{{ formatNumber(overview.totalContent) }} 内容总数</span>
              <span class="stat">+8.3% 月增长</span>
            </div>
          </div>
          <div class="card-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </router-link>

        <router-link
          to="/admin/analytics/system-performance"
          class="access-card"
        >
          <div class="card-icon">
            <el-icon><Monitor /></el-icon>
          </div>
          <div class="card-content">
            <h3>系统性能监控</h3>
            <p>CPU、内存、磁盘使用率，网络流量监控</p>
            <div class="card-stats">
              <span class="stat">系统状态：{{ systemHealthStatus.text }}</span>
              <span class="stat">实时监控</span>
            </div>
          </div>
          <div class="card-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </router-link>

        <router-link
          to="/admin/analytics/reports"
          class="access-card"
        >
          <div class="card-icon">
            <el-icon><Files /></el-icon>
          </div>
          <div class="card-content">
            <h3>报表管理</h3>
            <p>生成、下载和管理各类数据报表</p>
            <div class="card-stats">
              <span class="stat">{{ reports.length }} 个报表</span>
              <span class="stat">支持导出</span>
            </div>
          </div>
          <div class="card-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </router-link>
      </div>
    </div>

    <!-- 快速图表预览 -->
    <div class="chart-preview">
      <h2 class="section-title">数据概览</h2>
      <div class="chart-grid">
        <div class="chart-card">
          <div class="chart-header">
            <h3>用户活跃趋势</h3>
            <el-button
              link
              type="primary"
              @click="$router.push('/admin/analytics/user-behavior')"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="userActivityChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>内容发布趋势</h3>
            <el-button
              link
              type="primary"
              @click="$router.push('/admin/analytics/content-analysis')"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="contentTrendChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>系统性能概览</h3>
            <el-button
              link
              type="primary"
              @click="$router.push('/admin/analytics/system-performance')"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="systemPerformanceChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>
    </div>

    <!-- 最近报表 -->
    <div class="recent-reports">
      <div class="section-header">
        <h2 class="section-title">最近报表</h2>
        <el-button
          link
          type="primary"
          @click="$router.push('/admin/analytics/reports')"
        >
          查看全部
        </el-button>
      </div>
      
      <div v-if="reports.length > 0" class="reports-list">
        <div
          v-for="report in reports.slice(0, 5)"
          :key="report.id"
          class="report-item"
        >
          <div class="report-icon">
            <el-icon><Files /></el-icon>
          </div>
          <div class="report-content">
            <div class="report-name">{{ report.name }}</div>
            <div class="report-meta">
              <span class="report-type">{{ getReportTypeText(report.type) }}</span>
              <span class="report-date">{{ formatDateTime(report.createdAt) }}</span>
              <span class="report-size">{{ report.fileSize }}</span>
            </div>
          </div>
          <div class="report-status">
            <el-tag
              :type="getReportStatusType(report.status)"
              size="small"
            >
              {{ getReportStatusText(report.status) }}
            </el-tag>
          </div>
          <div class="report-actions">
            <el-button
              v-if="report.status === 'completed'"
              link
              type="primary"
              @click="downloadReportData(report.id)"
            >
              下载
            </el-button>
          </div>
        </div>
      </div>
      
      <div v-else class="empty-reports">
        <el-empty description="暂无报表数据" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import {
  User,
  UserFilled,
  Document,
  Monitor,
  Refresh,
  TrendCharts,
  DataAnalysis,
  ArrowRight,
  Files
} from '@element-plus/icons-vue'
import { useAnalytics } from '@/composables/useAnalytics'

// 使用数据分析组合式函数
const {
  loading,
  chartLoading,
  timeRange,
  customDateRange,
  overview,
  reports,
  totalUsersGrowth,
  activeUsersRate,
  systemHealthStatus,
  fetchOverview,
  fetchUserBehavior,
  fetchContentAnalysis,
  fetchSystemPerformance,
  fetchReports,
  downloadReportData,
  createChart,
  destroyAllCharts,
  handleTimeRangeChange,
  refreshAllData
} = useAnalytics()

// 处理自定义日期变化
const handleCustomDateChange = () => {
  // 这里可以添加自定义日期范围的逻辑
  refreshAllData()
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toLocaleString()
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 获取报表类型文本
const getReportTypeText = (type) => {
  const typeMap = {
    user_behavior: '用户行为',
    content_analysis: '内容分析',
    system_performance: '系统性能',
    custom: '自定义'
  }
  return typeMap[type] || type
}

// 获取报表状态类型
const getReportStatusType = (status) => {
  const typeMap = {
    generating: 'warning',
    completed: 'success',
    failed: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取报表状态文本
const getReportStatusText = (status) => {
  const textMap = {
    generating: '生成中',
    completed: '已完成',
    failed: '生成失败'
  }
  return textMap[status] || status
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  // 用户活跃趋势图表
  const userActivityOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '活跃用户',
      type: 'line',
      data: [820, 932, 901, 934, 1290, 1330, 1320],
      smooth: true,
      itemStyle: {
        color: '#409eff'
      }
    }]
  }
  
  // 内容发布趋势图表
  const contentTrendOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '发布内容',
      type: 'bar',
      data: [120, 200, 150, 80, 70, 110, 130],
      itemStyle: {
        color: '#67c23a'
      }
    }]
  }
  
  // 系统性能概览图表
  const systemPerformanceOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['CPU使用率', '内存使用率']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00']
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: 'CPU使用率',
        type: 'line',
        data: [30, 25, 45, 60, 55, 40, 35],
        smooth: true,
        itemStyle: {
          color: '#e6a23c'
        }
      },
      {
        name: '内存使用率',
        type: 'line',
        data: [50, 45, 55, 65, 60, 50, 45],
        smooth: true,
        itemStyle: {
          color: '#f56c6c'
        }
      }
    ]
  }
  
  createChart('userActivityChart', userActivityOption)
  createChart('contentTrendChart', contentTrendOption)
  createChart('systemPerformanceChart', systemPerformanceOption)
}

// 组件挂载
onMounted(async () => {
  await fetchOverview()
  await fetchReports()
  initCharts()
})
</script>

<style scoped>
.analytics-overview {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.page-description {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.custom-date-range {
  margin-bottom: 24px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  border-radius: 12px;
  font-size: 24px;
  color: #fff;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.content {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.health {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 32px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-growth {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.stat-growth.positive {
  color: #67c23a;
}

.stat-growth.negative {
  color: #f56c6c;
}

.stat-rate {
  font-size: 12px;
  color: #909399;
}

.quick-access {
  margin-bottom: 32px;
}

.section-title {
  margin: 0 0 20px 0;
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.access-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.access-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  text-decoration: none;
  color: inherit;
  transition: all 0.3s ease;
}

.access-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: #f0f9ff;
  border-radius: 8px;
  font-size: 20px;
  color: #409eff;
}

.card-content {
  flex: 1;
}

.card-content h3 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-content p {
  margin: 0 0 12px 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.card-stats {
  display: flex;
  gap: 12px;
}

.card-stats .stat {
  font-size: 11px;
  color: #909399;
}

.card-arrow {
  display: flex;
  align-items: center;
  color: #c0c4cc;
  transition: color 0.3s ease;
}

.access-card:hover .card-arrow {
  color: #409eff;
}

.chart-preview {
  margin-bottom: 32px;
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  height: 300px;
  padding: 20px;
}

.recent-reports {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.reports-list {
  display: grid;
  gap: 12px;
}

.report-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.report-item:hover {
  background: #f0f9ff;
}

.report-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #e1f3ff;
  border-radius: 8px;
  color: #409eff;
}

.report-content {
  flex: 1;
}

.report-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.report-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.report-actions {
  display: flex;
  gap: 8px;
}

.empty-reports {
  text-align: center;
  padding: 40px 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .analytics-overview {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .access-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    height: 250px;
  }
  
  .report-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .report-meta {
    flex-direction: column;
    gap: 4px;
  }
}

/* 动画效果 */
.stat-card,
.access-card,
.chart-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

.access-card:nth-child(1) { animation-delay: 0.2s; }
.access-card:nth-child(2) { animation-delay: 0.3s; }
.access-card:nth-child(3) { animation-delay: 0.4s; }
.access-card:nth-child(4) { animation-delay: 0.5s; }

.chart-card:nth-child(1) { animation-delay: 0.3s; }
.chart-card:nth-child(2) { animation-delay: 0.4s; }
.chart-card:nth-child(3) { animation-delay: 0.5s; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
