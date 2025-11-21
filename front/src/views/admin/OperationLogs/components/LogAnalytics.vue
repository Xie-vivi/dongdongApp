<template>
  <div class="log-analytics">
    <!-- 分析概览 -->
    <div class="analytics-overview">
      <div class="overview-cards">
        <div class="stat-card">
          <div class="stat-icon">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ formatNumber(statistics.totalLogs) }}</div>
            <div class="stat-label">总日志数</div>
            <div class="stat-trend">较上月 +23%</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon today">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ formatNumber(statistics.todayLogs) }}</div>
            <div class="stat-label">今日日志</div>
            <div class="stat-trend">较昨日 +8%</div>
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
            <div class="stat-trend">较上月 -12%</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表分析 -->
    <div class="charts-section">
      <div class="chart-row">
        <!-- 操作类型分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>操作类型分布</h3>
            <div class="chart-controls">
              <el-radio-group v-model="operationChartType" size="small">
                <el-radio-button label="pie">饼图</el-radio-button>
                <el-radio-button label="bar">柱状图</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-container">
            <div ref="operationChartRef" class="chart"></div>
          </div>
        </div>

        <!-- 日志级别分布 -->
        <div class="chart-card">
          <div class="chart-header">
            <h3>日志级别分布</h3>
            <div class="chart-legend">
              <div v-for="level in levelDistribution" :key="level.level" class="legend-item">
                <span class="legend-color" :style="{ backgroundColor: level.color }"></span>
                <span class="legend-label">{{ level.label }}</span>
                <span class="legend-value">{{ level.percentage }}%</span>
              </div>
            </div>
          </div>
          <div class="chart-container">
            <div ref="levelChartRef" class="chart"></div>
          </div>
        </div>
      </div>

      <div class="chart-row">
        <!-- 每小时活动趋势 -->
        <div class="chart-card full-width">
          <div class="chart-header">
            <h3>24小时活动趋势</h3>
            <div class="chart-controls">
              <el-select v-model="timeRange" size="small" style="width: 120px" @change="updateTimeRange">
                <el-option label="今天" value="today" />
                <el-option label="昨天" value="yesterday" />
                <el-option label="本周" value="week" />
                <el-option label="本月" value="month" />
              </el-select>
            </div>
          </div>
          <div class="chart-container">
            <div ref="hourlyChartRef" class="chart"></div>
          </div>
        </div>
      </div>

      <div class="chart-row">
        <!-- 每日趋势 -->
        <div class="chart-card full-width">
          <div class="chart-header">
            <h3>日志量趋势分析</h3>
            <div class="chart-controls">
              <el-radio-group v-model="trendChartType" size="small">
                <el-radio-button label="count">日志数量</el-radio-button>
                <el-radio-button label="error">错误趋势</el-radio-button>
                <el-radio-button label="performance">性能趋势</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div class="chart-container">
            <div ref="trendChartRef" class="chart"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详细分析 -->
    <div class="detailed-analysis">
      <div class="analysis-section">
        <h3>热门操作分析</h3>
        <div class="operation-analysis">
          <div class="analysis-table">
            <div class="table-header">
              <div class="header-cell">操作类型</div>
              <div class="header-cell">执行次数</div>
              <div class="header-cell">占比</div>
              <div class="header-cell">趋势</div>
              <div class="header-cell">平均耗时</div>
            </div>
            <div
              v-for="operation in topOperations"
              :key="operation.operation"
              class="table-row"
            >
              <div class="cell operation-cell">
                <el-icon>
                  <component :is="getOperationIcon(operation.operation)" />
                </el-icon>
                <span>{{ getOperationName(operation.operation) }}</span>
              </div>
              <div class="cell">{{ formatNumber(operation.count) }}</div>
              <div class="cell">{{ operation.percentage }}%</div>
              <div class="cell">
                <el-tag :type="getTrendType(operation.trend)" size="small">
                  {{ operation.trend > 0 ? '+' : '' }}{{ operation.trend }}%
                </el-tag>
              </div>
              <div class="cell">{{ formatDuration(operation.avgDuration) }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="analysis-section">
        <h3>用户活跃度分析</h3>
        <div class="user-activity">
          <div class="activity-stats">
            <div class="activity-item">
              <div class="activity-label">活跃用户数</div>
              <div class="activity-value">{{ formatNumber(userActivity.activeUsers) }}</div>
              <div class="activity-trend">较昨日 +5%</div>
            </div>
            <div class="activity-item">
              <div class="activity-label">平均操作次数</div>
              <div class="activity-value">{{ userActivity.avgOperations }}</div>
              <div class="activity-trend">较昨日 +2%</div>
            </div>
            <div class="activity-item">
              <div class="activity-label">最高活跃用户</div>
              <div class="activity-value">{{ userActivity.topUser }}</div>
              <div class="activity-trend">{{ userActivity.topUserOperations }} 次操作</div>
            </div>
          </div>

          <div class="user-ranking">
            <div class="ranking-header">
              <h4>用户操作排行榜</h4>
              <el-button link type="primary" size="small" @click="viewFullRanking">
                查看完整排行
              </el-button>
            </div>
            <div class="ranking-list">
              <div
                v-for="(user, index) in topUsers"
                :key="user.username"
                class="ranking-item"
              >
                <div class="ranking-number">{{ index + 1 }}</div>
                <div class="ranking-avatar">
                  <el-avatar :size="32">{{ user.username.charAt(0) }}</el-avatar>
                </div>
                <div class="ranking-info">
                  <div class="ranking-name">{{ user.username }}</div>
                  <div class="ranking-role">{{ user.role }}</div>
                </div>
                <div class="ranking-stats">
                  <div class="stat-value">{{ user.operations }} 次</div>
                  <div class="stat-trend">+{{ user.growth }}%</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="analysis-section">
        <h3>异常检测报告</h3>
        <div class="anomaly-report">
          <div v-if="anomalyAlerts.length === 0" class="no-anomaly">
            <el-icon><SuccessFilled /></el-icon>
            <span>系统运行正常，未检测到异常</span>
          </div>
          <div v-else class="anomaly-list">
            <div
              v-for="alert in anomalyAlerts"
              :key="alert.type"
              class="anomaly-item"
              :class="alert.severity"
            >
              <div class="anomaly-icon">
                <el-icon><Warning /></el-icon>
              </div>
              <div class="anomaly-content">
                <div class="anomaly-title">{{ getAnomalyTitle(alert.type) }}</div>
                <div class="anomaly-desc">
                  {{ alert.count }} 次异常操作（阈值：{{ alert.threshold }} 次）在 {{ alert.timeWindow }} 内检测到
                </div>
                <div class="anomaly-time">{{ alert.detectedAt }}</div>
              </div>
              <div class="anomaly-actions">
                <el-button
                  :type="alert.severity === 'high' ? 'danger' : 'warning'"
                  size="small"
                  @click="handleAnomaly(alert)"
                >
                  处理
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 导出报告 -->
    <div class="export-section">
      <div class="export-header">
        <h3>分析报告导出</h3>
        <p>生成详细的日志分析报告，支持多种格式</p>
      </div>
      <div class="export-options">
        <el-radio-group v-model="exportFormat" size="small">
          <el-radio-button label="pdf">PDF报告</el-radio-button>
          <el-radio-button label="excel">Excel表格</el-radio-button>
          <el-radio-button label="json">JSON数据</el-radio-button>
        </el-radio-group>
        <el-button
          type="primary"
          @click="exportReport"
          :loading="exportLoading"
        >
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  TrendCharts,
  Clock,
  Warning,
  Timer,
  SuccessFilled,
  Download,
  User,
  SwitchButton,
  Plus,
  Edit,
  Delete,
  View
} from '@element-plus/icons-vue'
import { createChart, destroyAllCharts } from '@/composables/useAnalytics'

// 定义事件
const emit = defineEmits(['refresh'])

// 定义属性
const props = defineProps({
  statistics: {
    type: Object,
    required: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// 响应式数据
const operationChartType = ref('pie')
const trendChartType = ref('count')
const timeRange = ref('today')
const exportFormat = ref('pdf')
const exportLoading = ref(false)

// 图表引用
const operationChartRef = ref(null)
const levelChartRef = ref(null)
const hourlyChartRef = ref(null)
const trendChartRef = ref(null)

// 计算属性
const topOperations = computed(() => {
  return props.statistics.topOperations || []
})

const levelDistribution = computed(() => {
  const levels = props.statistics.levelDistribution || []
  const colorMap = {
    info: '#409eff',
    warn: '#e6a23c',
    error: '#f56c6c',
    debug: '#909399'
  }
  
  return levels.map(level => ({
    ...level,
    color: colorMap[level.level] || '#909399'
  }))
})

const anomalyAlerts = computed(() => {
  return props.statistics.anomalyAlerts || []
})

// 模拟用户活跃度数据
const userActivity = reactive({
  activeUsers: 156,
  avgOperations: 12.5,
  topUser: '管理员1',
  topUserOperations: 89
})

// 模拟热门用户数据
const topUsers = ref([
  { username: '管理员1', role: '管理员', operations: 89, growth: 12 },
  { username: '编辑员3', role: '编辑', operations: 67, growth: 8 },
  { username: '审核员2', role: '审核员', operations: 45, growth: -3 },
  { username: '用户15', role: '普通用户', operations: 34, growth: 15 },
  { username: '编辑员8', role: '编辑', operations: 28, growth: 5 }
])

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

// 格式化持续时间
const formatDuration = (ms) => {
  if (!ms) return '-'
  if (ms < 1000) return `${ms}ms`
  return `${(ms / 1000).toFixed(2)}s`
}

// 获取操作图标
const getOperationIcon = (operation) => {
  const iconMap = {
    login: User,
    logout: SwitchButton,
    create: Plus,
    update: Edit,
    delete: Delete,
    view: View
  }
  return iconMap[operation] || View
}

// 获取操作名称
const getOperationName = (operation) => {
  const nameMap = {
    login: '登录',
    logout: '登出',
    create: '创建',
    update: '更新',
    delete: '删除',
    view: '查看'
  }
  return nameMap[operation] || operation
}

// 获取趋势类型
const getTrendType = (trend) => {
  if (trend > 10) return 'success'
  if (trend > 0) return 'info'
  return 'danger'
}

// 获取异常标题
const getAnomalyTitle = (type) => {
  const titleMap = {
    failed_login: '登录失败次数异常',
    unusual_access: '异常访问模式',
    high_error_rate: '错误率过高',
    suspicious_activity: '可疑操作行为'
  }
  return titleMap[type] || type
}

// 创建操作类型分布图
const createOperationChart = () => {
  if (!operationChartRef.value || !props.statistics.topOperations) return

  const data = props.statistics.topOperations.map(item => ({
    name: getOperationName(item.operation),
    value: item.count,
    percentage: item.percentage
  }))

  const chartOption = operationChartType.value === 'pie' ? {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: data
    }]
  } : {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      type: 'bar',
      data: data.map(item => item.value),
      itemStyle: {
        color: '#409eff'
      }
    }]
  }

  createChart(operationChartRef.value, chartOption)
}

// 创建日志级别分布图
const createLevelChart = () => {
  if (!levelChartRef.value || !props.statistics.levelDistribution) return

  const data = props.statistics.levelDistribution.map(item => ({
    name: item.level,
    value: item.count,
    percentage: item.percentage
  }))

  const chartOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    series: [{
      type: 'pie',
      radius: '60%',
      data: data,
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }

  createChart(levelChartRef.value, chartOption)
}

// 创建每小时活动趋势图
const createHourlyChart = () => {
  if (!hourlyChartRef.value || !props.statistics.hourlyActivity) return

  const data = props.statistics.hourlyActivity.map(item => ({
    hour: item.hour,
    count: item.count
  }))

  const chartOption = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => `${item.hour}:00`)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      type: 'line',
      data: data.map(item => item.count),
      smooth: true,
      areaStyle: {
        opacity: 0.3
      },
      itemStyle: {
        color: '#409eff'
      }
    }]
  }

  createChart(hourlyChartRef.value, chartOption)
}

// 创建趋势图
const createTrendChart = () => {
  if (!trendChartRef.value || !props.statistics.dailyTrend) return

  const data = props.statistics.dailyTrend.map(item => ({
    date: item.date,
    count: item.count,
    errorCount: item.errorCount
  }))

  let series = []

  if (trendChartType.value === 'count') {
    series = [{
      type: 'line',
      data: data.map(item => item.count),
      smooth: true,
      name: '日志数量',
      itemStyle: {
        color: '#409eff'
      }
    }]
  } else if (trendChartType.value === 'error') {
    series = [{
      type: 'line',
      data: data.map(item => item.errorCount),
      smooth: true,
      name: '错误数量',
      itemStyle: {
        color: '#f56c6c'
      }
    }]
  } else {
    series = [{
      type: 'line',
      data: data.map(item => Math.random() * 1000 + 500),
      smooth: true,
      name: '平均响应时间',
      itemStyle: {
        color: '#67c23a'
      }
    }]
  }

  const chartOption = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: series
  }

  createChart(trendChartRef.value, chartOption)
}

// 更新时间范围
const updateTimeRange = () => {
  // 这里可以根据时间范围重新获取数据
  emit('refresh')
}

// 查看完整排行
const viewFullRanking = () => {
  ElMessage.info('跳转到用户排行榜页面')
}

// 处理异常
const handleAnomaly = (alert) => {
  ElMessage.warning(`正在处理 ${alert.type} 异常`)
}

// 导出报告
const exportReport = async () => {
  exportLoading.value = true
  
  try {
    // 模拟导出过程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    ElMessage.success(`${exportFormat.value.toUpperCase()} 报告导出成功`)
    
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    exportLoading.value = false
  }
}

// 监听图表类型变化
watch(operationChartType, () => {
  nextTick(() => {
    createOperationChart()
  })
})

watch(trendChartType, () => {
  nextTick(() => {
    createTrendChart()
  })
})

// 监听统计数据变化
watch(() => props.statistics, () => {
  nextTick(() => {
    createOperationChart()
    createLevelChart()
    createHourlyChart()
    createTrendChart()
  })
}, { deep: true })

// 组件挂载
onMounted(() => {
  nextTick(() => {
    createOperationChart()
    createLevelChart()
    createHourlyChart()
    createTrendChart()
  })
})

// 组件卸载
onUnmounted(() => {
  destroyAllCharts()
})
</script>

<style scoped>
.log-analytics {
  padding: 0;
}

.analytics-overview {
  margin-bottom: 24px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
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

.charts-section {
  margin-bottom: 32px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 24px;
  margin-bottom: 24px;
}

.chart-row:last-child {
  margin-bottom: 0;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chart-card.full-width {
  grid-column: 1 / -1;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-legend {
  display: flex;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-label {
  color: #606266;
}

.legend-value {
  color: #303133;
  font-weight: 500;
}

.chart-container {
  padding: 20px;
  height: 300px;
}

.chart {
  width: 100%;
  height: 100%;
}

.detailed-analysis {
  margin-bottom: 32px;
}

.analysis-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
  margin-bottom: 24px;
}

.analysis-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.analysis-table {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  background: #f5f7fa;
  font-weight: 600;
  color: #303133;
}

.header-cell {
  padding: 12px 16px;
  border-right: 1px solid #ebeef5;
}

.header-cell:last-child {
  border-right: none;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  border-bottom: 1px solid #ebeef5;
  transition: background-color 0.3s ease;
}

.table-row:hover {
  background: #f0f9ff;
}

.table-row:last-child {
  border-bottom: none;
}

.cell {
  padding: 12px 16px;
  border-right: 1px solid #ebeef5;
  display: flex;
  align-items: center;
}

.cell:last-child {
  border-right: none;
}

.operation-cell {
  gap: 8px;
}

.user-activity {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.activity-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  text-align: center;
}

.activity-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.activity-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.activity-trend {
  font-size: 12px;
  color: #67c23a;
}

.ranking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.ranking-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.ranking-item:hover {
  background: #f0f9ff;
  transform: translateX(4px);
}

.ranking-number {
  width: 24px;
  height: 24px;
  background: #409eff;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
}

.ranking-info {
  flex: 1;
}

.ranking-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.ranking-role {
  font-size: 12px;
  color: #909399;
}

.ranking-stats {
  text-align: right;
}

.stat-value {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.stat-trend {
  font-size: 12px;
  color: #67c23a;
}

.anomaly-report {
  padding: 20px 0;
}

.no-anomaly {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px;
  color: #67c23a;
  font-size: 16px;
}

.anomaly-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.anomaly-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid #e6a23c;
  background: #fdf6ec;
}

.anomaly-item.high {
  border-left-color: #f56c6c;
  background: #fef0f0;
}

.anomaly-icon {
  flex-shrink: 0;
  color: #e6a23c;
  font-size: 20px;
}

.anomaly-item.high .anomaly-icon {
  color: #f56c6c;
}

.anomaly-content {
  flex: 1;
}

.anomaly-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.anomaly-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 4px;
}

.anomaly-time {
  color: #909399;
  font-size: 12px;
}

.anomaly-actions {
  flex-shrink: 0;
}

.export-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.export-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.export-header p {
  margin: 0 0 20px 0;
  color: #606266;
  font-size: 14px;
}

.export-options {
  display: flex;
  align-items: center;
  gap: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .user-activity {
    grid-template-columns: 1fr;
  }
  
  .table-header,
  .table-row {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .header-cell,
  .cell {
    border-right: none;
    border-bottom: 1px solid #ebeef5;
  }
  
  .ranking-item {
    flex-wrap: wrap;
    text-align: center;
  }
  
  .export-options {
    flex-direction: column;
    align-items: stretch;
  }
}

/* 动画效果 */
.stat-card,
.chart-card,
.analysis-section {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

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
