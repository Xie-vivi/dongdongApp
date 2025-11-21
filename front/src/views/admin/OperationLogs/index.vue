<template>
  <div class="operation-logs">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">操作日志</h1>
        <p class="page-description">监控系统操作记录，追踪用户行为和系统状态</p>
      </div>
      <div class="header-actions">
        <el-button
          type="primary"
          @click="showExportDialog"
          :loading="exportLoading"
        >
          <el-icon><Download /></el-icon>
          导出日志
        </el-button>
        <el-button
          type="warning"
          @click="handleCleanup"
        >
          <el-icon><Delete /></el-icon>
          清理日志
        </el-button>
        <el-button
          type="info"
          @click="detectAnomalies"
        >
          <el-icon><Warning /></el-icon>
          异常检测
        </el-button>
      </div>
    </div>

    <!-- 统计概览 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(statistics.totalLogs) }}</div>
          <div class="stat-label">总日志数</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon today">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(statistics.todayLogs) }}</div>
          <div class="stat-label">今日日志</div>
          <div class="stat-trend">较昨日 +12%</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon" :class="{ error: statistics.errorRate > 5 }">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ statistics.errorRate }}%</div>
          <div class="stat-label">错误率</div>
          <div class="stat-trend" :class="{ 'trend-up': statistics.errorRate > 3 }">
            {{ statistics.errorRate > 3 ? '偏高' : '正常' }}
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Timer /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatDuration(statistics.avgDuration) }}</div>
          <div class="stat-label">平均响应时间</div>
          <div class="stat-trend">较昨日 -5%</div>
        </div>
      </div>
    </div>

    <!-- 快速筛选 -->
    <div class="quick-filters">
      <div class="filter-group">
        <span class="filter-label">操作类型:</span>
        <el-radio-group v-model="quickFilter.operationType" size="small" @change="applyQuickFilter">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="login">登录</el-radio-button>
          <el-radio-button label="create">创建</el-radio-button>
          <el-radio-button label="update">更新</el-radio-button>
          <el-radio-button label="delete">删除</el-radio-button>
        </el-radio-group>
      </div>

      <div class="filter-group">
        <span class="filter-label">日志级别:</span>
        <el-radio-group v-model="quickFilter.level" size="small" @change="applyQuickFilter">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="info">信息</el-radio-button>
          <el-radio-button label="warn">警告</el-radio-button>
          <el-radio-button label="error">错误</el-radio-button>
        </el-radio-group>
      </div>

      <div class="filter-group">
        <span class="filter-label">状态:</span>
        <el-radio-group v-model="quickFilter.status" size="small" @change="applyQuickFilter">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="success">成功</el-radio-button>
          <el-radio-button label="failed">失败</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 异常警报 -->
    <div v-if="statistics.anomalyAlerts && statistics.anomalyAlerts.length > 0" class="anomaly-alerts">
      <el-alert
        v-for="alert in statistics.anomalyAlerts"
        :key="alert.type"
        :title="`异常警报：${getAnomalyAlertText(alert.type)}`"
        :description="`${alert.count} 次异常操作（阈值：${alert.threshold} 次）在 ${alert.timeWindow} 内检测到`"
        :type="alert.severity === 'high' ? 'error' : 'warning'"
        show-icon
        :closable="false"
        style="margin-bottom: 12px"
      />
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-tabs v-model="activeTab" class="content-tabs">
        <!-- 日志列表 -->
        <el-tab-pane label="日志列表" name="logs">
          <LogList
            :logs="logs"
            :loading="loading"
            :total-logs="totalLogs"
            :current-page="currentPage"
            :page-size="pageSize"
            :selected-logs="selectedLogs"
            :query-params="queryParams"
            @refresh="refreshLogs"
            @search="searchLogs"
            @filter="filterLogs"
            @sort="sortLogs"
            @page-change="handlePageChange"
            @size-change="handleSizeChange"
            @toggle-selection="toggleLogSelection"
            @toggle-select-all="toggleSelectAll"
            @clear-selection="clearSelection"
            @view-detail="handleViewDetail"
            @batch-export="batchExportLogs"
          />
        </el-tab-pane>

        <!-- 日志分析 -->
        <el-tab-pane label="日志分析" name="analytics">
          <LogAnalytics
            :statistics="statistics"
            :loading="statisticsLoading"
            @refresh="fetchStatistics"
          />
        </el-tab-pane>

        <!-- 实时监控 -->
        <el-tab-pane label="实时监控" name="monitor">
          <div class="monitor-section">
            <div class="monitor-header">
              <h3>实时操作监控</h3>
              <div class="monitor-controls">
                <el-switch
                  v-model="realTimeEnabled"
                  active-text="实时监控"
                  @change="toggleRealTime"
                />
                <el-button
                  type="primary"
                  size="small"
                  @click="clearMonitorLogs"
                >
                  清空监控
                </el-button>
              </div>
            </div>

            <div class="monitor-content">
              <div class="monitor-stats">
                <div class="monitor-stat">
                  <span class="stat-label">实时连接数</span>
                  <span class="stat-value">{{ realTimeStats.connections }}</span>
                </div>
                <div class="monitor-stat">
                  <span class="stat-label">今日操作数</span>
                  <span class="stat-value">{{ realTimeStats.todayOperations }}</span>
                </div>
                <div class="monitor-stat">
                  <span class="stat-label">异常操作数</span>
                  <span class="stat-value error">{{ realTimeStats.anomalies }}</span>
                </div>
              </div>

              <div class="real-time-logs">
                <div
                  v-for="log in realTimeLogs"
                  :key="log.id"
                  class="real-time-log-item"
                  :class="log.level"
                >
                  <div class="log-time">{{ formatTime(log.createdAt) }}</div>
                  <div class="log-user">{{ log.username }}</div>
                  <div class="log-operation">{{ log.operationName }}</div>
                  <div class="log-status">
                    <el-tag :type="getStatusType(log.status)" size="small">
                      {{ getStatusText(log.status) }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 日志详情对话框 -->
    <el-dialog
      v-model="logDetailVisible"
      title="日志详情"
      width="900px"
      :before-close="handleCloseDetail"
    >
      <LogDetail
        v-if="logDetail"
        :log="logDetail"
        @close="logDetailVisible = false"
      />
    </el-dialog>

    <!-- 导出对话框 -->
    <el-dialog
      v-model="exportDialogVisible"
      title="导出日志"
      width="500px"
    >
      <el-form :model="exportForm" label-width="100px">
        <el-form-item label="导出范围">
          <el-radio-group v-model="exportForm.range">
            <el-radio label="all">全部日志</el-radio>
            <el-radio label="filtered">当前筛选结果</el-radio>
            <el-radio label="selected">选中日志</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="导出格式">
          <el-select v-model="exportForm.format" style="width: 100%">
            <el-option label="CSV" value="csv" />
            <el-option label="Excel" value="excel" />
            <el-option label="JSON" value="json" />
          </el-select>
        </el-form-item>

        <el-form-item label="包含字段">
          <el-checkbox-group v-model="exportForm.fields">
            <el-checkbox label="timestamp">时间戳</el-checkbox>
            <el-checkbox label="user">用户信息</el-checkbox>
            <el-checkbox label="operation">操作信息</el-checkbox>
            <el-checkbox label="request">请求数据</el-checkbox>
            <el-checkbox label="response">响应数据</el-checkbox>
            <el-checkbox label="ip">IP地址</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <el-form-item label="时间范围">
          <el-date-picker
            v-model="exportForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExport" :loading="exportLoading">
          开始导出
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download,
  Delete,
  Warning,
  Document,
  Clock,
  Timer
} from '@element-plus/icons-vue'
import { useOperationLogs } from '@/composables/useOperationLogs'
import { useAnalytics } from '@/composables/useAnalytics'
import LogList from './components/LogList.vue'
import LogDetail from './components/LogDetail.vue'
import LogAnalytics from './components/LogAnalytics.vue'

// 使用组合式函数
const {
  loading,
  logs,
  totalLogs,
  currentPage,
  pageSize,
  selectedLogs,
  logDetail,
  statistics,
  exportLoading,
  queryParams,
  hasSelectedLogs,
  selectedLogsCount,
  formatDate,
  formatDuration,
  getLogLevelInfo,
  getOperationTypeInfo,
  getStatusType,
  getStatusText,
  fetchLogs,
  refreshLogs,
  fetchLogDetail,
  fetchStatistics,
  exportLogs,
  cleanupLogs,
  searchLogs,
  filterLogs,
  sortLogs,
  toggleLogSelection,
  toggleSelectAll,
  clearSelection,
  batchExportLogs,
  detectAnomalies
} = useOperationLogs()

const { createChart, destroyAllCharts } = useAnalytics()

// 响应式数据
const activeTab = ref('logs')
const statisticsLoading = ref(false)
const logDetailVisible = ref(false)
const exportDialogVisible = ref(false)

// 快速筛选
const quickFilter = reactive({
  operationType: '',
  level: '',
  status: ''
})

// 导出表单
const exportForm = reactive({
  range: 'filtered',
  format: 'csv',
  fields: ['timestamp', 'user', 'operation', 'ip'],
  dateRange: []
})

// 实时监控
const realTimeEnabled = ref(false)
const realTimeLogs = ref([])
const realTimeStats = ref({
  connections: 0,
  todayOperations: 0,
  anomalies: 0
})

let realTimeInterval = null

// 计算属性
const exportButtonText = computed(() => {
  if (exportLoading.value) return '导出中...'
  if (exportForm.range === 'selected') {
    return `导出选中 (${selectedLogsCount.value})`
  }
  return '导出日志'
})

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleTimeString()
}

// 获取异常警报文本
const getAnomalyAlertText = (type) => {
  const alertMap = {
    failed_login: '登录失败次数异常',
    unusual_access: '异常访问模式',
    high_error_rate: '错误率过高',
    suspicious_activity: '可疑操作行为'
  }
  return alertMap[type] || type
}

// 应用快速筛选
const applyQuickFilter = () => {
  filterLogs(quickFilter)
}

// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page
}

const handleSizeChange = (size) => {
  pageSize.value = size
}

// 查看日志详情
const handleViewDetail = async (log) => {
  await fetchLogDetail(log.id)
  logDetailVisible.value = true
}

// 关闭详情对话框
const handleCloseDetail = () => {
  logDetailVisible.value = false
  logDetail.value = null
}

// 显示导出对话框
const showExportDialog = () => {
  exportForm.range = hasSelectedLogs.value ? 'selected' : 'filtered'
  exportDialogVisible.value = true
}

// 处理导出
const handleExport = async () => {
  try {
    let exportOptions = {
      format: exportForm.format,
      fields: exportForm.fields,
      dateRange: exportForm.dateRange
    }

    if (exportForm.range === 'selected') {
      exportOptions.logIds = selectedLogs.value.map(log => log.id)
    } else if (exportForm.range === 'filtered') {
      exportOptions.filters = { ...queryParams }
    }

    await exportLogs(exportOptions)
    exportDialogVisible.value = false

  } catch (error) {
    console.error('导出失败:', error)
  }
}

// 处理清理
const handleCleanup = async () => {
  await cleanupLogs({
    retentionDays: 30,
    deleteLevel: 'info'
  })
}

// 切换实时监控
const toggleRealTime = (enabled) => {
  if (enabled) {
    startRealTimeMonitoring()
  } else {
    stopRealTimeMonitoring()
  }
}

// 开始实时监控
const startRealTimeMonitoring = () => {
  realTimeInterval = setInterval(() => {
    // 模拟实时日志数据
    const newLog = {
      id: Date.now(),
      username: `用户${Math.floor(Math.random() * 20) + 1}`,
      operationName: ['登录', '查看', '更新', '删除'][Math.floor(Math.random() * 4)],
      status: Math.random() > 0.1 ? 'success' : 'failed',
      level: Math.random() > 0.8 ? 'error' : 'info',
      createdAt: new Date().toISOString()
    }

    realTimeLogs.value.unshift(newLog)
    if (realTimeLogs.value.length > 50) {
      realTimeLogs.value = realTimeLogs.value.slice(0, 50)
    }

    // 更新统计
    realTimeStats.value.todayOperations++
    if (newLog.status === 'failed') {
      realTimeStats.value.anomalies++
    }
  }, 2000)

  realTimeStats.value.connections = Math.floor(Math.random() * 10) + 5
}

// 停止实时监控
const stopRealTimeMonitoring = () => {
  if (realTimeInterval) {
    clearInterval(realTimeInterval)
    realTimeInterval = null
  }
}

// 清空监控日志
const clearMonitorLogs = () => {
  realTimeLogs.value = []
  realTimeStats.value = {
    connections: 0,
    todayOperations: 0,
    anomalies: 0
  }
}

// 组件挂载
onMounted(async () => {
  await Promise.all([
    fetchLogs(),
    fetchStatistics()
  ])
})

// 组件卸载
onUnmounted(() => {
  stopRealTimeMonitoring()
  destroyAllCharts()
})
</script>

<style scoped>
.operation-logs {
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

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
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
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 8px;
  font-size: 20px;
  color: #fff;
}

.stat-icon.today {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-icon.error {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  color: #909399;
}

.stat-trend.trend-up {
  color: #f56c6c;
}

.quick-filters {
  display: flex;
  gap: 24px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-label {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  white-space: nowrap;
}

.anomaly-alerts {
  margin-bottom: 24px;
}

.main-content {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.content-tabs {
  padding: 20px;
}

.monitor-section {
  padding: 20px 0;
}

.monitor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.monitor-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.monitor-controls {
  display: flex;
  align-items: center;
  gap: 16px;
}

.monitor-content {
  display: flex;
  gap: 24px;
}

.monitor-stats {
  flex-shrink: 0;
  width: 200px;
}

.monitor-stat {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 12px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.stat-value.error {
  color: #f56c6c;
}

.real-time-logs {
  flex: 1;
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #fafafa;
}

.real-time-log-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s ease;
}

.real-time-log-item:hover {
  background: #f0f9ff;
}

.real-time-log-item:last-child {
  border-bottom: none;
}

.real-time-log-item.error {
  border-left: 4px solid #f56c6c;
  background: #fef0f0;
}

.log-time {
  flex-shrink: 0;
  font-size: 12px;
  color: #909399;
  font-family: monospace;
}

.log-user {
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  width: 80px;
}

.log-operation {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.log-status {
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .operation-logs {
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
    flex-wrap: wrap;
  }
  
  .stats-overview {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .quick-filters {
    flex-direction: column;
    gap: 16px;
  }
  
  .monitor-content {
    flex-direction: column;
  }
  
  .monitor-stats {
    width: 100%;
  }
  
  .real-time-log-item {
    flex-wrap: wrap;
    gap: 8px;
  }
}

/* 动画效果 */
.stat-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

.real-time-log-item {
  animation: slideInRight 0.3s ease-out;
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

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}
</style>
