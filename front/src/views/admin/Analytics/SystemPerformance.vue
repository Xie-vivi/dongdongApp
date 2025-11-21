<template>
  <div class="system-performance">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">系统性能监控</h1>
        <p class="page-description">实时监控系统性能指标，包括CPU、内存、磁盘使用率等</p>
      </div>
      <div class="header-actions">
        <el-switch
          v-model="autoRefresh"
          active-text="自动刷新"
          inactive-text="手动刷新"
          @change="handleAutoRefreshChange"
        />
        
        <el-select
          v-model="refreshInterval"
          :disabled="!autoRefresh"
          style="width: 120px"
        >
          <el-option label="5秒" :value="5" />
          <el-option label="10秒" :value="10" />
          <el-option label="30秒" :value="30" />
          <el-option label="1分钟" :value="60" />
        </el-select>
        
        <el-button
          type="primary"
          :loading="loading"
          @click="refreshData"
        >
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        
        <el-button
          type="success"
          @click="exportData"
        >
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
      </div>
    </div>

    <!-- 系统状态概览 -->
    <div class="system-overview">
      <div class="status-card">
        <div class="status-header">
          <div class="status-icon">
            <el-icon><Monitor /></el-icon>
          </div>
          <div class="status-info">
            <h3>系统状态</h3>
            <el-tag :type="getSystemStatusType()" size="large">
              {{ getSystemStatusText() }}
            </el-tag>
          </div>
        </div>
        <div class="status-metrics">
          <div class="metric-item">
            <span class="metric-label">运行时间</span>
            <span class="metric-value">{{ getUptime() }}</span>
          </div>
          <div class="metric-item">
            <span class="metric-label">负载均衡</span>
            <span class="metric-value">{{ getLoadAverage() }}</span>
          </div>
        </div>
      </div>

      <div class="performance-cards">
        <div class="perf-card">
          <div class="perf-header">
            <div class="perf-icon cpu">
              <el-icon><Cpu /></el-icon>
            </div>
            <div class="perf-info">
              <h4>CPU使用率</h4>
              <div class="perf-value">{{ systemPerformance.cpuUsage }}%</div>
            </div>
          </div>
          <div class="perf-progress">
            <el-progress
              :percentage="systemPerformance.cpuUsage"
              :color="getProgressColor(systemPerformance.cpuUsage)"
              :stroke-width="8"
            />
          </div>
          <div class="perf-details">
            <span>核心数: {{ systemPerformance.cpuCores }}</span>
            <span>频率: {{ systemPerformance.cpuFrequency }}GHz</span>
          </div>
        </div>

        <div class="perf-card">
          <div class="perf-header">
            <div class="perf-icon memory">
              <el-icon><MemoryCard /></el-icon>
            </div>
            <div class="perf-info">
              <h4>内存使用率</h4>
              <div class="perf-value">{{ systemPerformance.memoryUsage }}%</div>
            </div>
          </div>
          <div class="perf-progress">
            <el-progress
              :percentage="systemPerformance.memoryUsage"
              :color="getProgressColor(systemPerformance.memoryUsage)"
              :stroke-width="8"
            />
          </div>
          <div class="perf-details">
            <span>已用: {{ formatMemory(systemPerformance.memoryUsed) }}</span>
            <span>总计: {{ formatMemory(systemPerformance.memoryTotal) }}</span>
          </div>
        </div>

        <div class="perf-card">
          <div class="perf-header">
            <div class="perf-icon disk">
              <el-icon><FolderOpened /></el-icon>
            </div>
            <div class="perf-info">
              <h4>磁盘使用率</h4>
              <div class="perf-value">{{ systemPerformance.diskUsage }}%</div>
            </div>
          </div>
          <div class="perf-progress">
            <el-progress
              :percentage="systemPerformance.diskUsage"
              :color="getProgressColor(systemPerformance.diskUsage)"
              :stroke-width="8"
            />
          </div>
          <div class="perf-details">
            <span>已用: {{ formatMemory(systemPerformance.diskUsed) }}</span>
            <span>总计: {{ formatMemory(systemPerformance.diskTotal) }}</span>
          </div>
        </div>

        <div class="perf-card">
          <div class="perf-header">
            <div class="perf-icon network">
              <el-icon><Connection /></el-icon>
            </div>
            <div class="perf-info">
              <h4>网络流量</h4>
              <div class="perf-value">{{ formatNetwork(systemPerformance.networkTraffic) }}</div>
            </div>
          </div>
          <div class="perf-details">
            <span>上行: {{ formatNetwork(systemPerformance.networkUpload) }}</span>
            <span>下行: {{ formatNetwork(systemPerformance.networkDownload) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 实时监控图表 -->
    <div class="charts-section">
      <div class="chart-row">
        <div class="chart-card large">
          <div class="chart-header">
            <h3>系统性能实时监控</h3>
            <div class="chart-actions">
              <el-radio-group v-model="chartTimeRange" size="small">
                <el-radio-button label="1h">1小时</el-radio-button>
                <el-radio-button label="6h">6小时</el-radio-button>
                <el-radio-button label="24h">24小时</el-radio-button>
                <el-radio-button label="7d">7天</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div
            id="performanceChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3>CPU核心使用率</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showCpuDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="cpuCoresChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>内存分布</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showMemoryDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="memoryDistributionChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3>磁盘分区使用情况</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showDiskDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="diskPartitionsChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>网络流量趋势</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showNetworkDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="networkTrafficChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>
    </div>

    <!-- 性能警报 -->
    <div class="alerts-section">
      <div class="section-header">
        <h3>性能警报</h3>
        <div class="alert-actions">
          <el-button
            type="primary"
            size="small"
            @click="showAlertSettings"
          >
            警报设置
          </el-button>
        </div>
      </div>

      <div v-if="alerts.length > 0" class="alerts-list">
        <div
          v-for="alert in alerts"
          :key="alert.id"
          class="alert-item"
          :class="getAlertClass(alert.level)"
        >
          <div class="alert-icon">
            <el-icon>
              <Warning v-if="alert.level === 'warning'" />
              <CircleCloseFilled v-else-if="alert.level === 'error'" />
              <InfoFilled v-else />
            </el-icon>
          </div>
          <div class="alert-content">
            <div class="alert-title">{{ alert.title }}</div>
            <div class="alert-message">{{ alert.message }}</div>
            <div class="alert-time">{{ formatDateTime(alert.timestamp) }}</div>
          </div>
          <div class="alert-actions">
            <el-button
              link
              type="primary"
              size="small"
              @click="dismissAlert(alert.id)"
            >
              忽略
            </el-button>
          </div>
        </div>
      </div>

      <div v-else class="empty-alerts">
        <el-empty description="暂无性能警报" />
      </div>
    </div>

    <!-- CPU详情对话框 -->
    <el-dialog
      v-model="cpuDetailVisible"
      title="CPU详细信息"
      width="600px"
    >
      <div class="cpu-detail">
        <div class="detail-item">
          <span class="detail-label">CPU型号:</span>
          <span class="detail-value">Intel Core i7-9700K</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">核心数:</span>
          <span class="detail-value">8核16线程</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">基础频率:</span>
          <span class="detail-value">3.6GHz</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">最大频率:</span>
          <span class="detail-value">4.9GHz</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">缓存:</span>
          <span class="detail-value">12MB L3</span>
        </div>
      </div>
    </el-dialog>

    <!-- 内存详情对话框 -->
    <el-dialog
      v-model="memoryDetailVisible"
      title="内存详细信息"
      width="600px"
    >
      <div class="memory-detail">
        <div class="detail-item">
          <span class="detail-label">总内存:</span>
          <span class="detail-value">{{ formatMemory(systemPerformance.memoryTotal) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">已使用:</span>
          <span class="detail-value">{{ formatMemory(systemPerformance.memoryUsed) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">可用内存:</span>
          <span class="detail-value">{{ formatMemory(systemPerformance.memoryTotal - systemPerformance.memoryUsed) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">缓存:</span>
          <span class="detail-value">2.1GB</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">交换分区:</span>
          <span class="detail-value">4.0GB</span>
        </div>
      </div>
    </el-dialog>

    <!-- 磁盘详情对话框 -->
    <el-dialog
      v-model="diskDetailVisible"
      title="磁盘分区详情"
      width="700px"
    >
      <div class="disk-detail">
        <el-table :data="diskPartitions" stripe>
          <el-table-column prop="partition" label="分区" width="100" />
          <el-table-column prop="mountPoint" label="挂载点" width="100" />
          <el-table-column prop="total" label="总容量" width="120">
            <template #default="{ row }">
              {{ formatMemory(row.total) }}
            </template>
          </el-table-column>
          <el-table-column prop="used" label="已使用" width="120">
            <template #default="{ row }">
              {{ formatMemory(row.used) }}
            </template>
          </el-table-column>
          <el-table-column prop="available" label="可用" width="120">
            <template #default="{ row }">
              {{ formatMemory(row.available) }}
            </template>
          </el-table-column>
          <el-table-column prop="usage" label="使用率" width="100">
            <template #default="{ row }">
              {{ row.usage }}%
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 网络详情对话框 -->
    <el-dialog
      v-model="networkDetailVisible"
      title="网络流量详情"
      width="600px"
    >
      <div class="network-detail">
        <div class="detail-item">
          <span class="detail-label">网络接口:</span>
          <span class="detail-value">eth0</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">IP地址:</span>
          <span class="detail-value">192.168.1.100</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">上行速度:</span>
          <span class="detail-value">{{ formatNetwork(systemPerformance.networkUpload) }}/s</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">下行速度:</span>
          <span class="detail-value">{{ formatNetwork(systemPerformance.networkDownload) }}/s</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">总上传量:</span>
          <span class="detail-value">125.6GB</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">总下载量:</span>
          <span class="detail-value">892.3GB</span>
        </div>
      </div>
    </el-dialog>

    <!-- 警报设置对话框 -->
    <el-dialog
      v-model="alertSettingsVisible"
      title="警报设置"
      width="600px"
    >
      <el-form :model="alertSettings" label-width="120px">
        <el-form-item label="CPU使用率">
          <el-input-number
            v-model="alertSettings.cpuThreshold"
            :min="50"
            :max="100"
            :step="5"
          />
          <span class="unit">%</span>
        </el-form-item>
        <el-form-item label="内存使用率">
          <el-input-number
            v-model="alertSettings.memoryThreshold"
            :min="50"
            :max="100"
            :step="5"
          />
          <span class="unit">%</span>
        </el-form-item>
        <el-form-item label="磁盘使用率">
          <el-input-number
            v-model="alertSettings.diskThreshold"
            :min="50"
            :max="100"
            :step="5"
          />
          <span class="unit">%</span>
        </el-form-item>
        <el-form-item label="通知方式">
          <el-checkbox-group v-model="alertSettings.notificationMethods">
            <el-checkbox label="email">邮件</el-checkbox>
            <el-checkbox label="sms">短信</el-checkbox>
            <el-checkbox label="webhook">Webhook</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="alertSettingsVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAlertSettings">保存设置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Monitor,
  Refresh,
  Download,
  Cpu,
  MemoryCard,
  FolderOpened,
  Connection,
  Warning,
  CircleCloseFilled,
  InfoFilled
} from '@element-plus/icons-vue'
import { useAnalytics } from '@/composables/useAnalytics'

// 使用数据分析组合式函数
const {
  loading,
  chartLoading,
  systemPerformance,
  fetchSystemPerformance,
  createChart,
  destroyAllCharts
} = useAnalytics()

// 响应式数据
const autoRefresh = ref(true)
const refreshInterval = ref(10)
const chartTimeRange = ref('1h')
const refreshTimer = ref(null)

// 对话框显示状态
const cpuDetailVisible = ref(false)
const memoryDetailVisible = ref(false)
const diskDetailVisible = ref(false)
const networkDetailVisible = ref(false)
const alertSettingsVisible = ref(false)

// 警报数据
const alerts = ref([
  {
    id: 1,
    level: 'warning',
    title: 'CPU使用率过高',
    message: 'CPU使用率已达到85%，建议检查系统负载',
    timestamp: new Date().toISOString()
  },
  {
    id: 2,
    level: 'info',
    title: '内存使用率正常',
    message: '当前内存使用率为65%，运行状态良好',
    timestamp: new Date(Date.now() - 300000).toISOString()
  }
])

// 磁盘分区数据
const diskPartitions = ref([
  {
    partition: '/dev/sda1',
    mountPoint: '/',
    total: 500 * 1024 * 1024 * 1024,
    used: 250 * 1024 * 1024 * 1024,
    available: 250 * 1024 * 1024 * 1024,
    usage: 50
  },
  {
    partition: '/dev/sda2',
    mountPoint: '/home',
    total: 1000 * 1024 * 1024 * 1024,
    used: 600 * 1024 * 1024 * 1024,
    available: 400 * 1024 * 1024 * 1024,
    usage: 60
  }
])

// 警报设置
const alertSettings = ref({
  cpuThreshold: 80,
  memoryThreshold: 85,
  diskThreshold: 90,
  notificationMethods: ['email']
})

// 获取系统状态类型
const getSystemStatusType = () => {
  const cpu = systemPerformance.value.cpuUsage
  const memory = systemPerformance.value.memoryUsage
  const disk = systemPerformance.value.diskUsage
  
  if (cpu > 90 || memory > 90 || disk > 95) return 'danger'
  if (cpu > 70 || memory > 70 || disk > 80) return 'warning'
  return 'success'
}

// 获取系统状态文本
const getSystemStatusText = () => {
  const type = getSystemStatusType()
  const statusMap = {
    success: '运行正常',
    warning: '性能警告',
    danger: '严重警告'
  }
  return statusMap[type] || '未知'
}

// 获取运行时间
const getUptime = () => {
  return '15天 8小时 32分钟'
}

// 获取负载均衡
const getLoadAverage = () => {
  return '1.2, 1.5, 1.8'
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage > 80) return '#f56c6c'
  if (percentage > 60) return '#e6a23c'
  return '#67c23a'
}

// 格式化内存大小
const formatMemory = (bytes) => {
  if (bytes >= 1024 * 1024 * 1024) {
    return (bytes / (1024 * 1024 * 1024)).toFixed(1) + 'GB'
  }
  if (bytes >= 1024 * 1024) {
    return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
  }
  return bytes + 'B'
}

// 格式化网络流量
const formatNetwork = (bytes) => {
  if (bytes >= 1024 * 1024) {
    return (bytes / (1024 * 1024)).toFixed(1) + 'MB'
  }
  if (bytes >= 1024) {
    return (bytes / 1024).toFixed(1) + 'KB'
  }
  return bytes + 'B'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 获取警报样式类
const getAlertClass = (level) => {
  return `alert-${level}`
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  // 系统性能实时监控图表
  const performanceOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['CPU使用率', '内存使用率', '磁盘使用率']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Array.from({ length: 60 }, (_, i) => {
        const time = new Date(Date.now() - (59 - i) * 60000)
        return time.toLocaleTimeString()
      })
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
        data: Array.from({ length: 60 }, () => Math.floor(Math.random() * 30) + 40),
        smooth: true,
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '内存使用率',
        type: 'line',
        data: Array.from({ length: 60 }, () => Math.floor(Math.random() * 20) + 50),
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        }
      },
      {
        name: '磁盘使用率',
        type: 'line',
        data: Array.from({ length: 60 }, () => Math.floor(Math.random() * 10) + 45),
        smooth: true,
        itemStyle: {
          color: '#e6a23c'
        }
      }
    ]
  }
  
  // CPU核心使用率图表
  const cpuCoresOption = {
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
      data: ['核心1', '核心2', '核心3', '核心4', '核心5', '核心6', '核心7', '核心8']
    },
    yAxis: {
      type: 'value',
      max: 100,
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      name: '使用率',
      type: 'bar',
      data: Array.from({ length: 8 }, () => Math.floor(Math.random() * 40) + 30),
      itemStyle: {
        color: '#409eff'
      }
    }]
  }
  
  // 内存分布图表
  const memoryDistributionOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '内存分布',
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
      data: [
        { value: 6.5, name: '应用程序' },
        { value: 2.1, name: '缓存' },
        { value: 1.2, name: '系统' },
        { value: 4.2, name: '可用' }
      ]
    }]
  }
  
  // 磁盘分区使用情况图表
  const diskPartitionsOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    yAxis: {
      type: 'category',
      data: ['/', '/home', '/var', '/tmp']
    },
    series: [{
      name: '使用率',
      type: 'bar',
      data: [50, 60, 75, 30],
      itemStyle: {
        color: '#e6a23c'
      }
    }]
  }
  
  // 网络流量趋势图表
  const networkTrafficOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['上行', '下行']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Array.from({ length: 24 }, (_, i) => `${i}:00`)
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}MB'
      }
    },
    series: [
      {
        name: '上行',
        type: 'line',
        data: Array.from({ length: 24 }, () => Math.floor(Math.random() * 50) + 10),
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        }
      },
      {
        name: '下行',
        type: 'line',
        data: Array.from({ length: 24 }, () => Math.floor(Math.random() * 100) + 50),
        smooth: true,
        itemStyle: {
          color: '#409eff'
        }
      }
    ]
  }
  
  createChart('performanceChart', performanceOption)
  createChart('cpuCoresChart', cpuCoresOption)
  createChart('memoryDistributionChart', memoryDistributionOption)
  createChart('diskPartitionsChart', diskPartitionsOption)
  createChart('networkTrafficChart', networkTrafficOption)
}

// 刷新数据
const refreshData = async () => {
  await fetchSystemPerformance()
  initCharts()
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
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
  }
  
  refreshTimer.value = setInterval(() => {
    refreshData()
  }, refreshInterval.value * 1000)
}

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
    refreshTimer.value = null
  }
}

// 监听刷新间隔变化
const handleRefreshIntervalChange = () => {
  if (autoRefresh.value) {
    startAutoRefresh()
  }
}

// 显示详情对话框
const showCpuDetail = () => {
  cpuDetailVisible.value = true
}

const showMemoryDetail = () => {
  memoryDetailVisible.value = true
}

const showDiskDetail = () => {
  diskDetailVisible.value = true
}

const showNetworkDetail = () => {
  networkDetailVisible.value = true
}

const showAlertSettings = () => {
  alertSettingsVisible.value = true
}

// 忽略警报
const dismissAlert = (alertId) => {
  alerts.value = alerts.value.filter(alert => alert.id !== alertId)
  ElMessage.success('警报已忽略')
}

// 保存警报设置
const saveAlertSettings = () => {
  alertSettingsVisible.value = false
  ElMessage.success('警报设置已保存')
}

// 导出数据
const exportData = () => {
  ElMessage.info('导出功能开发中...')
}

// 组件挂载
onMounted(async () => {
  await fetchSystemPerformance()
  initCharts()
  
  if (autoRefresh.value) {
    startAutoRefresh()
  }
})

// 组件卸载
onUnmounted(() => {
  stopAutoRefresh()
  destroyAllCharts()
})
</script>

<style scoped>
.system-performance {
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

.system-overview {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  margin-bottom: 32px;
}

.status-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.status-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 12px;
  font-size: 24px;
  color: #fff;
}

.status-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.status-metrics {
  display: flex;
  gap: 32px;
}

.metric-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-label {
  font-size: 12px;
  color: #606266;
}

.metric-value {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.performance-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.perf-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.perf-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.perf-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.perf-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  font-size: 20px;
  color: #fff;
}

.perf-icon.cpu {
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
}

.perf-icon.memory {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.perf-icon.disk {
  background: linear-gradient(135deg, #e6a23c 0%, #f7ba2a 100%);
}

.perf-icon.network {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.perf-info h4 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.perf-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.perf-progress {
  margin-bottom: 12px;
}

.perf-details {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.charts-section {
  margin-bottom: 32px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-row .chart-card.large {
  grid-column: 1 / -1;
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
  height: 400px;
  padding: 20px;
}

.alerts-section {
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

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.alerts-list {
  display: grid;
  gap: 12px;
}

.alert-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 16px;
  border-radius: 8px;
  border-left: 4px solid;
}

.alert-item.alert-info {
  background: #f0f9ff;
  border-left-color: #409eff;
}

.alert-item.alert-warning {
  background: #fdf6ec;
  border-left-color: #e6a23c;
}

.alert-item.alert-error {
  background: #fef0f0;
  border-left-color: #f56c6c;
}

.alert-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 14px;
  color: #fff;
}

.alert-item.alert-info .alert-icon {
  background: #409eff;
}

.alert-item.alert-warning .alert-icon {
  background: #e6a23c;
}

.alert-item.alert-error .alert-icon {
  background: #f56c6c;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.alert-message {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.alert-time {
  font-size: 11px;
  color: #909399;
}

.alert-actions {
  display: flex;
  align-items: center;
}

.empty-alerts {
  text-align: center;
  padding: 40px 20px;
}

.cpu-detail,
.memory-detail,
.network-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 500;
  color: #303133;
}

.detail-value {
  color: #606266;
}

.unit {
  margin-left: 8px;
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .system-performance {
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
  
  .performance-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    height: 300px;
  }
  
  .status-metrics {
    flex-direction: column;
    gap: 16px;
  }
  
  .perf-details {
    flex-direction: column;
    gap: 4px;
  }
}

/* 动画效果 */
.perf-card,
.chart-card {
  animation: fadeInUp 0.5s ease-out;
}

.perf-card:nth-child(1) { animation-delay: 0.1s; }
.perf-card:nth-child(2) { animation-delay: 0.2s; }
.perf-card:nth-child(3) { animation-delay: 0.3s; }
.perf-card:nth-child(4) { animation-delay: 0.4s; }

.chart-card:nth-child(1) { animation-delay: 0.2s; }
.chart-card:nth-child(2) { animation-delay: 0.3s; }
.chart-card:nth-child(3) { animation-delay: 0.4s; }
.chart-card:nth-child(4) { animation-delay: 0.5s; }

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
