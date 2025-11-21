<template>
  <div class="realtime-data">
    <el-card class="data-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>实时数据监控</h3>
            <span class="subtitle">系统实时运行状态</span>
          </div>
          <div class="header-right">
            <el-switch
              v-model="autoRefresh"
              active-text="自动刷新"
              inactive-text="手动刷新"
              @change="handleAutoRefreshChange"
            />
            <el-button size="small" @click="refreshData">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="data-content">
        <!-- 实时指标卡片 -->
        <div class="metrics-grid">
          <div class="metric-card" v-for="(metric, index) in metrics" :key="index">
            <div class="metric-icon" :style="{ color: metric.color }">
              <el-icon size="24">
                <component :is="metric.icon" />
              </el-icon>
            </div>
            <div class="metric-info">
              <div class="metric-value">
                <el-skeleton v-if="loading" animated>
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 60px; height: 24px;" />
                  </template>
                </el-skeleton>
                <span v-else>{{ metric.value }}</span>
              </div>
              <div class="metric-label">{{ metric.label }}</div>
              <div class="metric-status" :class="metric.statusType">
                <el-icon size="12">
                  <component :is="metric.statusIcon" />
                </el-icon>
                <span>{{ metric.statusText }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 实时图表 -->
        <div class="chart-section">
          <div class="chart-header">
            <h4>系统负载趋势</h4>
            <div class="chart-controls">
              <el-select v-model="chartTimeRange" size="small" @change="updateChartTimeRange">
                <el-option label="最近5分钟" value="5m"></el-option>
                <el-option label="最近15分钟" value="15m"></el-option>
                <el-option label="最近30分钟" value="30m"></el-option>
                <el-option label="最近1小时" value="1h"></el-option>
              </el-select>
            </div>
          </div>
          <div class="chart-container" ref="chartRef">
            <el-skeleton v-if="loading" animated>
              <template #template>
                <el-skeleton-item variant="rect" style="width: 100%; height: 200px;" />
              </template>
            </el-skeleton>
          </div>
        </div>
        
        <!-- 系统服务状态 -->
        <div class="services-section">
          <h4>服务状态</h4>
          <div class="services-list">
            <div 
              class="service-item" 
              v-for="service in services" 
              :key="service.name"
              :class="service.status"
            >
              <div class="service-icon">
                <el-icon>
                  <component :is="service.status === 'running' ? CircleCheck : Warning" />
                </el-icon>
              </div>
              <div class="service-info">
                <div class="service-name">{{ service.name }}</div>
                <div class="service-uptime">运行时间：{{ service.uptime }}</div>
              </div>
              <div class="service-status">
                <el-tag 
                  :type="service.status === 'running' ? 'success' : service.status === 'warning' ? 'warning' : 'danger'"
                  size="small"
                >
                  {{ service.status === 'running' ? '运行中' : service.status === 'warning' ? '警告' : '停止' }}
                </el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  User,
  Monitor,
  Connection,
  HardDrive,
  Warning,
  CircleCheck,
  ArrowUp,
  ArrowDown,
  Minus
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const props = defineProps({
  realTimeData: {
    type: Object,
    default: () => ({})
  },
  systemHealth: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// 自动刷新状态
const autoRefresh = ref(true)

// 图表时间范围
const chartTimeRange = ref('15m')

// 图表引用
const chartRef = ref(null)
let chart = null

// 刷新定时器
let refreshTimer = null

// 格式化数字
const formatNumber = (num) => {
  if (!num && num !== 0) return '0'
  return num.toLocaleString()
}

// 格式化百分比
const formatPercentage = (num) => {
  if (!num && num !== 0) return '0%'
  return `${num.toFixed(1)}%`
}

// 格式化存储大小
const formatStorageSize = (gb) => {
  if (gb < 1) {
    return `${(gb * 1024).toFixed(1)}MB`
  }
  return `${gb.toFixed(1)}GB`
}

// 实时指标数据
const metrics = computed(() => [
  {
    label: '在线用户',
    value: formatNumber(props.realTimeData.onlineUsers || 0),
    icon: User,
    color: '#409EFF',
    statusType: 'positive',
    statusIcon: CircleCheck,
    statusText: '实时'
  },
  {
    label: '系统负载',
    value: formatPercentage(props.realTimeData.systemLoad || 0),
    icon: Monitor,
    color: props.realTimeData.systemLoad > 80 ? '#F56C6C' : props.realTimeData.systemLoad > 60 ? '#E6A23C' : '#67C23A',
    statusType: props.realTimeData.systemLoad > 80 ? 'negative' : props.realTimeData.systemLoad > 60 ? 'warning' : 'positive',
    statusIcon: props.realTimeData.systemLoad > 80 ? ArrowUp : props.realTimeData.systemLoad > 60 ? Minus : CircleCheck,
    statusText: props.realTimeData.systemLoad > 80 ? '高负载' : props.realTimeData.systemLoad > 60 ? '中等' : '正常'
  },
  {
    label: 'CPU使用率',
    value: formatPercentage(props.systemHealth.cpu?.usage || 0),
    icon: Monitor,
    color: props.systemHealth.cpu?.usage > 80 ? '#F56C6C' : props.systemHealth.cpu?.usage > 60 ? '#E6A23C' : '#67C23A',
    statusType: props.systemHealth.cpu?.usage > 80 ? 'negative' : props.systemHealth.cpu?.usage > 60 ? 'warning' : 'positive',
    statusIcon: props.systemHealth.cpu?.usage > 80 ? ArrowUp : props.systemHealth.cpu?.usage > 60 ? Minus : CircleCheck,
    statusText: props.systemHealth.cpu?.usage > 80 ? '高负载' : props.systemHealth.cpu?.usage > 60 ? '中等' : '正常'
  },
  {
    label: '内存使用',
    value: `${formatStorageSize((props.systemHealth.memory?.used || 0) / 1024)}/${formatStorageSize((props.systemHealth.memory?.total || 0) / 1024)}`,
    icon: HardDrive,
    color: props.systemHealth.memory?.usage > 80 ? '#F56C6C' : props.systemHealth.memory?.usage > 60 ? '#E6A23C' : '#67C23A',
    statusType: props.systemHealth.memory?.usage > 80 ? 'negative' : props.systemHealth.memory?.usage > 60 ? 'warning' : 'positive',
    statusIcon: props.systemHealth.memory?.usage > 80 ? ArrowUp : props.systemHealth.memory?.usage > 60 ? Minus : CircleCheck,
    statusText: props.systemHealth.memory?.usage > 80 ? '高负载' : props.systemHealth.memory?.usage > 60 ? '中等' : '正常'
  },
  {
    label: '磁盘使用',
    value: formatPercentage(props.systemHealth.disk?.usage || 0),
    icon: HardDrive,
    color: props.systemHealth.disk?.usage > 80 ? '#F56C6C' : props.systemHealth.disk?.usage > 60 ? '#E6A23C' : '#67C23A',
    statusType: props.systemHealth.disk?.usage > 80 ? 'negative' : props.systemHealth.disk?.usage > 60 ? 'warning' : 'positive',
    statusIcon: props.systemHealth.disk?.usage > 80 ? ArrowUp : props.systemHealth.disk?.usage > 60 ? Minus : CircleCheck,
    statusText: props.systemHealth.disk?.usage > 80 ? '空间不足' : props.systemHealth.disk?.usage > 60 ? '注意' : '正常'
  },
  {
    label: '网络流量',
    value: '正常',
    icon: Connection,
    color: '#67C23A',
    statusType: 'positive',
    statusIcon: CircleCheck,
    statusText: '稳定'
  }
])

// 系统服务数据
const services = computed(() => props.systemHealth.services || [])

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chart = echarts.init(chartRef.value)
  updateChart()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 生成模拟图表数据
const generateChartData = () => {
  const now = new Date()
  const data = []
  const points = chartTimeRange.value === '5m' ? 5 : chartTimeRange.value === '15m' ? 15 : chartTimeRange.value === '30m' ? 30 : 60
  const interval = chartTimeRange.value === '5m' ? 1 : chartTimeRange.value === '15m' ? 1 : chartTimeRange.value === '30m' ? 1 : 1
  
  for (let i = points - 1; i >= 0; i--) {
    const time = new Date(now - i * interval * 60000)
    data.push({
      time: time.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' }),
      value: Math.random() * 30 + 40 + (props.realTimeData.systemLoad || 0) * 0.5
    })
  }
  
  return data
}

// 更新图表
const updateChart = () => {
  if (!chart) return
  
  const data = generateChartData()
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        return `${params[0].axisValue}<br/>系统负载：${params[0].value.toFixed(1)}%`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.time),
      axisLine: {
        lineStyle: {
          color: '#e8e8e8'
        }
      },
      axisLabel: {
        fontSize: 10,
        color: '#666'
      }
    },
    yAxis: {
      type: 'value',
      name: '负载(%)',
      min: 0,
      max: 100,
      axisLine: {
        lineStyle: {
          color: '#e8e8e8'
        }
      },
      axisLabel: {
        fontSize: 10,
        color: '#666'
      },
      splitLine: {
        lineStyle: {
          color: '#f5f5f5',
          type: 'dashed'
        }
      }
    },
    series: [
      {
        name: '系统负载',
        type: 'line',
        data: data.map(item => item.value),
        smooth: true,
        symbol: 'none',
        lineStyle: {
          width: 2,
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#409EFF' },
            { offset: 1, color: '#67C23A' }
          ])
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 处理窗口大小变化
const handleResize = () => {
  chart?.resize()
}

// 处理自动刷新变化
const handleAutoRefreshChange = (enabled) => {
  if (enabled) {
    startAutoRefresh()
  } else {
    stopAutoRefresh()
  }
}

// 开始自动刷新
const startAutoRefresh = () => {
  refreshTimer = setInterval(() => {
    updateChart()
  }, 30000) // 30秒刷新一次
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

// 更新图表时间范围
const updateChartTimeRange = () => {
  updateChart()
}

// 刷新数据
const refreshData = () => {
  updateChart()
  ElMessage.success('实时数据已刷新')
}

// 监听数据变化
watch(() => props.realTimeData, () => {
  nextTick(() => {
    updateChart()
  })
}, { deep: true })

watch(() => props.loading, (loading) => {
  if (!loading) {
    nextTick(() => {
      if (!chart) {
        initChart()
      } else {
        updateChart()
      }
    })
  }
})

// 生命周期
onMounted(() => {
  nextTick(() => {
    if (!props.loading) {
      initChart()
    }
    if (autoRefresh.value) {
      startAutoRefresh()
    }
  })
})

onUnmounted(() => {
  stopAutoRefresh()
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
.realtime-data {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.data-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.data-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.header-left h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.subtitle {
  font-size: 12px;
  color: #909399;
}

.header-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.data-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.metric-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #e9ecef;
  transition: all 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.metric-icon {
  flex-shrink: 0;
}

.metric-info {
  flex: 1;
  min-width: 0;
}

.metric-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.metric-status {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
}

.metric-status.positive {
  color: #67c23a;
}

.metric-status.warning {
  color: #e6a23c;
}

.metric-status.negative {
  color: #f56c6c;
}

.chart-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.chart-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  flex: 1;
  min-height: 200px;
  position: relative;
}

.services-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.services-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #e9ecef;
  transition: all 0.3s ease;
}

.service-item.running {
  border-left-color: #67c23a;
}

.service-item.warning {
  border-left-color: #e6a23c;
}

.service-item.error {
  border-left-color: #f56c6c;
}

.service-icon {
  flex-shrink: 0;
  color: #67c23a;
}

.service-item.warning .service-icon,
.service-item.error .service-icon {
  color: #f56c6c;
}

.service-info {
  flex: 1;
  min-width: 0;
}

.service-name {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.service-uptime {
  font-size: 11px;
  color: #909399;
}

.service-status {
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .metrics-grid {
    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .data-card :deep(.el-card__body) {
    padding: 16px;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }
  
  .metric-card {
    padding: 12px;
  }
  
  .metric-value {
    font-size: 18px;
  }
  
  .chart-container {
    min-height: 180px;
  }
  
  .services-list {
    gap: 6px;
  }
  
  .service-item {
    padding: 10px;
  }
}

/* 加载状态 */
.data-card.loading {
  opacity: 0.7;
}

/* 动画效果 */
.realtime-data {
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

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .header-left h3 {
    color: #e4e7ed;
  }
  
  .metric-value {
    color: #e4e7ed;
  }
  
  .chart-header h4,
  .services-section h4 {
    color: #e4e7ed;
  }
  
  .metric-card,
  .service-item {
    background: #2d2d2d;
  }
}
</style>
