<template>
  <div class="system-health-monitor">
    <el-card class="health-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>系统健康监控</h3>
            <span class="subtitle">系统运行状态和性能指标</span>
          </div>
          <div class="header-right">
            <el-tag :type="getSystemStatusType(systemStatus)" size="small">
              {{ getSystemStatusText(systemStatus) }}
            </el-tag>
            <el-button size="small" @click="refreshHealth">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="health-content">
        <!-- 系统概览 -->
        <div class="overview-section">
          <div class="overview-item" v-for="item in overviewItems" :key="item.key">
            <div class="overview-icon" :style="{ color: item.color }">
              <el-icon size="20">
                <component :is="item.icon" />
              </el-icon>
            </div>
            <div class="overview-info">
              <div class="overview-label">{{ item.label }}</div>
              <div class="overview-value">{{ item.value }}</div>
            </div>
          </div>
        </div>
        
        <!-- 资源使用情况 -->
        <div class="resources-section">
          <h4>资源使用情况</h4>
          <div class="resource-list">
            <div class="resource-item" v-for="resource in resources" :key="resource.key">
              <div class="resource-header">
                <div class="resource-info">
                  <el-icon size="16">
                    <component :is="resource.icon" />
                  </el-icon>
                  <span class="resource-name">{{ resource.name }}</span>
                </div>
                <div class="resource-value">
                  <span class="value-text">{{ resource.value }}</span>
                  <span class="percentage">{{ resource.percentage }}</span>
                </div>
              </div>
              <div class="resource-progress">
                <el-progress 
                  :percentage="resource.usage" 
                  :status="getProgressStatus(resource.usage)"
                  :show-text="false"
                  :stroke-width="8"
                />
              </div>
              <div class="resource-details">
                <span class="detail-text">{{ resource.details }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 服务状态 -->
        <div class="services-section">
          <h4>服务状态</h4>
          <div class="services-grid">
            <div 
              class="service-card" 
              v-for="service in services" 
              :key="service.name"
              :class="service.status"
            >
              <div class="service-header">
                <div class="service-icon">
                  <el-icon size="16">
                    <component :is="getServiceIcon(service.status)" />
                  </el-icon>
                </div>
                <div class="service-name">{{ service.name }}</div>
                <div class="service-status">
                  <el-tag 
                    :type="getServiceTagType(service.status)" 
                    size="small"
                  >
                    {{ getServiceStatusText(service.status) }}
                  </el-tag>
                </div>
              </div>
              <div class="service-info">
                <div class="service-uptime">运行时间：{{ service.uptime }}</div>
                <div class="service-version">版本：{{ service.version }}</div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 系统告警 -->
        <div class="alerts-section" v-if="alerts.length">
          <h4>系统告警</h4>
          <div class="alerts-list">
            <div 
              class="alert-item" 
              v-for="alert in alerts" 
              :key="alert.id"
              :class="alert.level"
            >
              <div class="alert-icon">
                <el-icon size="16">
                  <component :is="getAlertIcon(alert.level)" />
                </el-icon>
              </div>
              <div class="alert-content">
                <div class="alert-title">{{ alert.title }}</div>
                <div class="alert-message">{{ alert.message }}</div>
                <div class="alert-time">{{ formatTime(alert.createdAt) }}</div>
              </div>
              <div class="alert-actions">
                <el-button type="text" size="small" @click="handleAlert(alert)">
                  处理
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Monitor,
  HardDrive,
  Connection,
  CircleCheck,
  Warning,
  Close,
  InfoFilled,
  ArrowUp,
  ArrowDown,
  Minus
} from '@element-plus/icons-vue'

const props = defineProps({
  systemHealth: {
    type: Object,
    default: () => ({})
  },
  systemStatus: {
    type: String,
    default: 'normal'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

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

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取系统状态类型
const getSystemStatusType = (status) => {
  const statusMap = {
    normal: 'success',
    warning: 'warning',
    error: 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取系统状态文本
const getSystemStatusText = (status) => {
  const statusMap = {
    normal: '系统正常',
    warning: '系统警告',
    error: '系统异常'
  }
  return statusMap[status] || '未知状态'
}

// 获取进度条状态
const getProgressStatus = (usage) => {
  if (usage > 80) return 'exception'
  if (usage > 60) return 'warning'
  return 'success'
}

// 获取服务图标
const getServiceIcon = (status) => {
  const iconMap = {
    running: CircleCheck,
    warning: Warning,
    error: Close
  }
  return iconMap[status] || Warning
}

// 获取服务标签类型
const getServiceTagType = (status) => {
  const typeMap = {
    running: 'success',
    warning: 'warning',
    error: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取服务状态文本
const getServiceStatusText = (status) => {
  const textMap = {
    running: '运行中',
    warning: '警告',
    error: '停止'
  }
  return textMap[status] || '未知'
}

// 获取告警图标
const getAlertIcon = (level) => {
  const iconMap = {
    high: Close,
    medium: Warning,
    low: InfoFilled
  }
  return iconMap[level] || InfoFilled
}

// 系统概览数据
const overviewItems = computed(() => [
  {
    key: 'uptime',
    label: '系统运行时间',
    value: props.systemHealth.uptime || '0天0小时',
    icon: Monitor,
    color: '#409EFF'
  },
  {
    key: 'load',
    label: '系统负载',
    value: formatPercentage(props.systemHealth.load || 0),
    icon: Monitor,
    color: props.systemHealth.load > 80 ? '#F56C6C' : props.systemHealth.load > 60 ? '#E6A23C' : '#67C23A'
  },
  {
    key: 'processes',
    label: '运行进程',
    value: formatNumber(props.systemHealth.processes || 0),
    icon: Monitor,
    color: '#909399'
  },
  {
    key: 'connections',
    label: '网络连接',
    value: formatNumber(props.systemHealth.connections || 0),
    icon: Connection,
    color: '#409EFF'
  }
])

// 资源使用情况
const resources = computed(() => [
  {
    key: 'cpu',
    name: 'CPU',
    icon: Monitor,
    value: formatNumber(props.systemHealth.cpu?.cores || 0) + '核',
    percentage: formatPercentage(props.systemHealth.cpu?.usage || 0),
    usage: props.systemHealth.cpu?.usage || 0,
    details: `使用率：${formatPercentage(props.systemHealth.cpu?.usage || 0)} | 温度：${props.systemHealth.cpu?.temperature || 0}°C`
  },
  {
    key: 'memory',
    name: '内存',
    icon: HardDrive,
    value: `${formatStorageSize((props.systemHealth.memory?.used || 0) / 1024)}/${formatStorageSize((props.systemHealth.memory?.total || 0) / 1024)}`,
    percentage: formatPercentage(props.systemHealth.memory?.usage || 0),
    usage: props.systemHealth.memory?.usage || 0,
    details: `总容量：${formatStorageSize((props.systemHealth.memory?.total || 0) / 1024)} | 可用：${formatStorageSize((props.systemHealth.memory?.available || 0) / 1024)}`
  },
  {
    key: 'disk',
    name: '磁盘',
    icon: HardDrive,
    value: `${formatStorageSize((props.systemHealth.disk?.used || 0) / 1024)}/${formatStorageSize((props.systemHealth.disk?.total || 0) / 1024)}`,
    percentage: formatPercentage(props.systemHealth.disk?.usage || 0),
    usage: props.systemHealth.disk?.usage || 0,
    details: `总容量：${formatStorageSize((props.systemHealth.disk?.total || 0) / 1024)} | 可用：${formatStorageSize((props.systemHealth.disk?.available || 0) / 1024)}`
  },
  {
    key: 'network',
    name: '网络',
    icon: Connection,
    value: '正常',
    percentage: '100%',
    usage: 100,
    details: `上传：${formatStorageSize(props.systemHealth.network?.upload || 0)}/s | 下载：${formatStorageSize(props.systemHealth.network?.download || 0)}/s`
  }
])

// 服务状态
const services = computed(() => props.systemHealth.services || [])

// 系统告警
const alerts = computed(() => props.systemHealth.alerts || [])

// 刷新健康状态
const refreshHealth = () => {
  ElMessage.success('系统健康状态已刷新')
}

// 处理告警
const handleAlert = (alert) => {
  ElMessage.info(`处理告警：${alert.title}`)
}
</script>

<style scoped>
.system-health-monitor {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.health-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.health-card :deep(.el-card__body) {
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

.health-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  overflow-y: auto;
}

.overview-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.overview-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #e9ecef;
}

.overview-icon {
  flex-shrink: 0;
}

.overview-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.overview-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.resources-section h4,
.services-section h4,
.alerts-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.resource-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.resource-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #e9ecef;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.resource-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.resource-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.resource-value {
  display: flex;
  align-items: center;
  gap: 8px;
}

.value-text {
  font-size: 13px;
  color: #606266;
}

.percentage {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.resource-progress {
  margin-bottom: 8px;
}

.resource-details {
  font-size: 12px;
  color: #909399;
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.service-card {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #e9ecef;
}

.service-card.running {
  border-left-color: #67c23a;
}

.service-card.warning {
  border-left-color: #e6a23c;
}

.service-card.error {
  border-left-color: #f56c6c;
}

.service-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.service-icon {
  flex-shrink: 0;
  color: #67c23a;
}

.service-card.warning .service-icon,
.service-card.error .service-icon {
  color: #f56c6c;
}

.service-name {
  flex: 1;
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}

.service-info {
  font-size: 11px;
  color: #909399;
  line-height: 1.4;
}

.alerts-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.alert-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #e9ecef;
}

.alert-item.high {
  border-left-color: #f56c6c;
  background: #fef0f0;
}

.alert-item.medium {
  border-left-color: #e6a23c;
  background: #fdf6ec;
}

.alert-item.low {
  border-left-color: #409eff;
  background: #ecf5ff;
}

.alert-icon {
  flex-shrink: 0;
  margin-top: 2px;
  color: #f56c6c;
}

.alert-item.medium .alert-icon {
  color: #e6a23c;
}

.alert-item.low .alert-icon {
  color: #409eff;
}

.alert-content {
  flex: 1;
  min-width: 0;
}

.alert-title {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.alert-message {
  font-size: 12px;
  color: #606266;
  margin-bottom: 4px;
  line-height: 1.4;
}

.alert-time {
  font-size: 11px;
  color: #909399;
}

.alert-actions {
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .overview-section {
    grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
    gap: 12px;
  }
  
  .services-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  }
}

@media (max-width: 768px) {
  .health-card :deep(.el-card__body) {
    padding: 16px;
  }
  
  .overview-section {
    grid-template-columns: 1fr;
    gap: 8px;
  }
  
  .overview-item {
    padding: 12px;
  }
  
  .resource-item {
    padding: 12px;
  }
  
  .service-card {
    padding: 12px;
  }
  
  .services-grid {
    grid-template-columns: 1fr;
  }
  
  .alert-item {
    padding: 10px;
  }
}

/* 动画效果 */
.system-health-monitor {
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
  
  .overview-item,
  .resource-item,
  .service-card {
    background: #2d2d2d;
  }
  
  .overview-value,
  .resource-name,
  .service-name,
  .alert-title {
    color: #e4e7ed;
  }
  
  .resources-section h4,
  .services-section h4,
  .alerts-section h4 {
    color: #e4e7ed;
  }
  
  .alert-item.high {
    background: #4a2e2e;
  }
  
  .alert-item.medium {
    background: #4a3e2e;
  }
  
  .alert-item.low {
    background: #2e3e4a;
  }
}
</style>
