<template>
  <div class="dashboard-container">
    <!-- 页面头部 -->
    <div class="dashboard-header">
      <div class="header-info">
        <h1>仪表盘概览</h1>
        <p>系统整体数据概览和快捷操作</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="refreshAllData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 实时数据状态栏 -->
    <div class="realtime-status">
      <div class="status-item">
        <el-icon class="status-icon" :color="getStatusColor(systemStatus)"><CircleCheck /></el-icon>
        <span>系统状态：{{ systemStatus === 'normal' ? '正常' : systemStatus === 'warning' ? '警告' : '异常' }}</span>
      </div>
      <div class="status-item">
        <el-icon class="status-icon"><User /></el-icon>
        <span>在线用户：{{ formatNumber(realTimeData.onlineUsers) }}</span>
      </div>
      <div class="status-item">
        <el-icon class="status-icon"><Monitor /></el-icon>
        <span>系统负载：{{ formatPercentage(realTimeData.systemLoad) }}</span>
      </div>
      <div class="status-item">
        <el-icon class="status-icon"><Clock /></el-icon>
        <span>最后更新：{{ formatTime(realTimeData.lastUpdated) }}</span>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="dashboard-content">
      <!-- 第一行：统计卡片和快捷操作 -->
      <div class="dashboard-row">
        <!-- 系统概览统计卡片 -->
        <div class="dashboard-section overview-cards">
          <OverviewCards :statistics="overviewStats" :loading="loading" />
        </div>

        <!-- 快捷操作入口 -->
        <div class="dashboard-section quick-actions">
          <QuickActions @action="handleQuickAction" />
        </div>
      </div>

      <!-- 第二行：图表和实时数据 -->
      <div class="dashboard-row">
        <!-- 用户增长趋势 -->
        <div class="dashboard-section chart-section">
          <UserTrendChart 
            :chart-data="getChartData('userTrends')" 
            :loading="loading"
            :days="trendDays"
            @days-change="handleDaysChange"
          />
        </div>

        <!-- 实时数据展示 -->
        <div class="dashboard-section realtime-section">
          <RealTimeData 
            :real-time-data="realTimeData"
            :system-health="systemHealth"
            :loading="loading"
          />
        </div>
      </div>

      <!-- 第三行：内容统计和系统健康 -->
      <div class="dashboard-row">
        <!-- 内容统计图表 -->
        <div class="dashboard-section chart-section">
          <ContentStatsChart 
            :chart-data="getChartData('contentByCategory')"
            :monthly-data="getChartData('monthlyTrends')"
            :loading="loading"
          />
        </div>

        <!-- 系统健康监控 -->
        <div class="dashboard-section health-section">
          <SystemHealthMonitor 
            :system-health="systemHealth"
            :system-status="systemStatus"
            :loading="loading"
          />
        </div>
      </div>

      <!-- 第四行：待处理事项 -->
      <div class="dashboard-row">
        <div class="dashboard-section full-width">
          <PendingTasksReminder 
            :pending-tasks="pendingTasks"
            :total-count="totalPendingCount"
            :loading="loading"
            @task-action="handleTaskAction"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Download,
  CircleCheck,
  User,
  Monitor,
  Clock
} from '@element-plus/icons-vue'

// 导入组件
import OverviewCards from './components/OverviewCards.vue'
import QuickActions from './components/QuickActions.vue'
import UserTrendChart from './components/UserTrendChart.vue'
import RealTimeData from './components/RealTimeData.vue'
import ContentStatsChart from './components/ContentStatsChart.vue'
import SystemHealthMonitor from './components/SystemHealthMonitor.vue'
import PendingTasksReminder from './components/PendingTasksReminder.vue'

// 导入组合式函数
import { useDashboard } from '@/composables/useDashboard'

// 使用组合式函数
const {
  loading,
  error,
  overviewStats,
  userTrends,
  contentStats,
  systemHealth,
  pendingTasks,
  realTimeData,
  trendDays,
  systemStatus,
  totalPendingCount,
  fetchOverviewStats,
  fetchUserTrends,
  fetchContentStats,
  fetchSystemHealth,
  fetchPendingTasks,
  refreshAllData,
  updateRealTimeData,
  startAutoRefresh,
  stopAutoRefresh,
  formatNumber,
  formatPercentage,
  formatStorageSize,
  getStatusColor,
  getChartData
} = useDashboard()

const router = useRouter()

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

// 处理快捷操作
const handleQuickAction = (action) => {
  switch (action) {
    case 'userManagement':
      router.push('/admin/users')
      break
    case 'contentReview':
      router.push('/admin/content/audit')
      break
    case 'systemConfig':
      router.push('/admin/system/config')
      break
    case 'dataAnalytics':
      router.push('/admin/analytics')
      break
    case 'permissionManagement':
      router.push('/admin/permissions')
      break
    case 'fileManagement':
      router.push('/admin/files')
      break
    case 'operationLogs':
      router.push('/admin/operation-logs')
      break
    case 'systemHealth':
      router.push('/admin/analytics/system-performance')
      break
    default:
      ElMessage.info(`执行操作：${action}`)
  }
}

// 处理趋势天数变化
const handleDaysChange = (days) => {
  trendDays.value = days
  fetchUserTrends(days)
}

// 处理待处理事项操作
const handleTaskAction = (action, task) => {
  switch (action) {
    case 'review':
      if (task.type === 'post') {
        router.push(`/admin/content/posts`)
      } else if (task.type === 'activity') {
        router.push(`/admin/content/activities`)
      } else if (task.type === 'comment') {
        router.push(`/admin/content/comments`)
      }
      break
    case 'handleAlert':
      ElMessage.info(`处理系统告警：${task.message}`)
      break
    case 'handleReport':
      ElMessage.info(`处理用户举报：${task.content}`)
      break
    default:
      ElMessage.info(`处理任务：${action}`)
  }
}

// 导出报告
const exportReport = () => {
  ElMessage.success('仪表盘报告导出成功')
  // 这里可以实现实际的导出逻辑
}

// 错误处理
if (error.value) {
  ElMessage.error(`数据加载失败：${error.value}`)
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-info h1 {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.header-info p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.realtime-status {
  display: flex;
  gap: 24px;
  margin-bottom: 24px;
  padding: 16px 24px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  align-items: center;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.status-icon {
  font-size: 16px;
}

.dashboard-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
}

.dashboard-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.overview-cards {
  grid-column: span 2;
}

.quick-actions {
  min-width: 300px;
}

.chart-section {
  min-height: 400px;
}

.realtime-section {
  min-height: 400px;
}

.health-section {
  min-height: 400px;
}

.full-width {
  grid-column: span 2;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .dashboard-row {
    grid-template-columns: 1fr;
  }
  
  .overview-cards,
  .full-width {
    grid-column: span 1;
  }
  
  .realtime-status {
    flex-wrap: wrap;
    gap: 16px;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;
  }
  
  .dashboard-header {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }
  
  .header-info h1 {
    font-size: 24px;
  }
  
  .realtime-status {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .dashboard-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

/* 加载状态 */
.dashboard-container.loading {
  opacity: 0.7;
  pointer-events: none;
}

/* 错误状态 */
.dashboard-container.error {
  opacity: 0.5;
}

/* 动画效果 */
.dashboard-section {
  animation: fadeInUp 0.3s ease-out;
}

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

/* 滚动条样式 */
.dashboard-container::-webkit-scrollbar {
  width: 6px;
}

.dashboard-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.dashboard-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.dashboard-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
