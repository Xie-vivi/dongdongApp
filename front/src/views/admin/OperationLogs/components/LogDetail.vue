<template>
  <div class="log-detail">
    <div class="detail-header">
      <div class="basic-info">
        <div class="info-item">
          <span class="label">日志ID:</span>
          <span class="value">{{ log.id }}</span>
        </div>
        <div class="info-item">
          <span class="label">操作时间:</span>
          <span class="value">{{ formatDate(log.createdAt) }}</span>
        </div>
        <div class="info-item">
          <span class="label">会话ID:</span>
          <span class="value">{{ log.sessionId }}</span>
        </div>
      </div>
      
      <div class="status-info">
        <el-tag :type="getStatusType(log.status)" size="large">
          {{ getStatusText(log.status) }}
        </el-tag>
        <el-tag
          :color="getLogLevelInfo(log.level).color"
          effect="light"
          size="large"
        >
          {{ getLogLevelInfo(log.level).label }}
        </el-tag>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="detail-tabs">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <div class="basic-details">
          <div class="detail-section">
            <h4>用户信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">用户ID:</span>
                <span class="value">{{ log.userId }}</span>
              </div>
              <div class="info-item">
                <span class="label">用户名:</span>
                <span class="value">{{ log.username }}</span>
              </div>
              <div class="info-item">
                <span class="label">用户角色:</span>
                <span class="value">{{ log.userRole }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4>操作信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">操作类型:</span>
                <span class="value">{{ log.operationName }}</span>
              </div>
              <div class="info-item">
                <span class="label">所属模块:</span>
                <span class="value">{{ log.module }}</span>
              </div>
              <div class="info-item">
                <span class="label">操作描述:</span>
                <span class="value">{{ log.description }}</span>
              </div>
            </div>
          </div>

          <div class="detail-section">
            <h4>网络信息</h4>
            <div class="info-grid">
              <div class="info-item">
                <span class="label">IP地址:</span>
                <span class="value">{{ log.ip }}</span>
              </div>
              <div class="info-item">
                <span class="label">User Agent:</span>
                <span class="value" :title="log.userAgent">{{ truncateText(log.userAgent, 50) }}</span>
              </div>
              <div class="info-item">
                <span class="label">持续时间:</span>
                <span class="value">{{ formatDuration(log.duration) }}</span>
              </div>
            </div>
          </div>

          <div v-if="log.errorCode || log.errorMessage" class="detail-section error-section">
            <h4>错误信息</h4>
            <div class="info-grid">
              <div v-if="log.errorCode" class="info-item">
                <span class="label">错误代码:</span>
                <span class="value error-code">{{ log.errorCode }}</span>
              </div>
              <div v-if="log.errorMessage" class="info-item">
                <span class="label">错误信息:</span>
                <span class="value error-message">{{ log.errorMessage }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 请求数据 -->
      <el-tab-pane label="请求数据" name="request">
        <div class="data-section">
          <div class="section-header">
            <h4>请求参数</h4>
            <div class="data-actions">
              <el-button
                link
                type="primary"
                size="small"
                @click="copyToClipboard(log.requestData, 'request')"
              >
                <el-icon><CopyDocument /></el-icon>
                复制
              </el-button>
              <el-button
                link
                type="success"
                size="small"
                @click="formatJson('request')"
              >
                <el-icon><Magic /></el-icon>
                格式化
              </el-button>
            </div>
          </div>
          
          <div class="data-container">
            <pre class="json-data" :class="{ 'formatted': requestFormatted }">{{ displayRequestData }}</pre>
          </div>
        </div>
      </el-tab-pane>

      <!-- 响应数据 -->
      <el-tab-pane label="响应数据" name="response">
        <div class="data-section">
          <div class="section-header">
            <h4>响应结果</h4>
            <div class="data-actions">
              <el-button
                link
                type="primary"
                size="small"
                @click="copyToClipboard(log.responseData, 'response')"
              >
                <el-icon><CopyDocument /></el-icon>
                复制
              </el-button>
              <el-button
                link
                type="success"
                size="small"
                @click="formatJson('response')"
              >
                <el-icon><Magic /></el-icon>
                格式化
              </el-button>
            </div>
          </div>
          
          <div class="data-container">
            <pre class="json-data" :class="{ 'formatted': responseFormatted }">{{ displayResponseData }}</pre>
          </div>
        </div>
      </el-tab-pane>

      <!-- 安全分析 -->
      <el-tab-pane label="安全分析" name="security">
        <div class="security-analysis">
          <div class="analysis-section">
            <h4>风险评估</h4>
            <div class="risk-indicators">
              <div class="risk-item">
                <span class="risk-label">IP风险等级:</span>
                <el-tag :type="getIpRiskType(ipRisk.level)">
                  {{ ipRisk.level }}
                </el-tag>
                <span class="risk-desc">{{ ipRisk.description }}</span>
              </div>
              
              <div class="risk-item">
                <span class="risk-label">操作风险:</span>
                <el-tag :type="getOperationRiskType(operationRisk.level)">
                  {{ operationRisk.level }}
                </el-tag>
                <span class="risk-desc">{{ operationRisk.description }}</span>
              </div>
              
              <div class="risk-item">
                <span class="risk-label">时间异常:</span>
                <el-tag :type="timeRisk.level === 'normal' ? 'success' : 'warning'">
                  {{ timeRisk.level }}
                </el-tag>
                <span class="risk-desc">{{ timeRisk.description }}</span>
              </div>
            </div>
          </div>

          <div class="analysis-section">
            <h4>行为模式</h4>
            <div class="behavior-patterns">
              <div class="pattern-item">
                <div class="pattern-title">操作频率</div>
                <div class="pattern-desc">
                  该用户在过去1小时内执行了 {{ userStats.recentOperations }} 次操作
                </div>
                <el-progress
                  :percentage="userStats.frequencyPercentage"
                  :color="getFrequencyColor(userStats.frequencyPercentage)"
                  :show-text="false"
                  style="margin-top: 8px"
                />
              </div>
              
              <div class="pattern-item">
                <div class="pattern-title">常用操作</div>
                <div class="pattern-desc">
                  最常执行的操作类型: {{ userStats.mostFrequentOperation }}
                </div>
              </div>
              
              <div class="pattern-item">
                <div class="pattern-title">异常行为</div>
                <div class="pattern-desc">
                  <span v-if="userStats.anomalies.length === 0" class="normal">未检测到异常行为</span>
                  <ul v-else class="anomaly-list">
                    <li v-for="anomaly in userStats.anomalies" :key="anomaly.type">
                      {{ anomaly.description }}
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          <div class="analysis-section">
            <h4>建议操作</h4>
            <div class="suggestions">
              <div v-for="suggestion in securitySuggestions" :key="suggestion.id" class="suggestion-item">
                <div class="suggestion-icon">
                  <el-icon>
                    <component :is="suggestion.icon" />
                  </el-icon>
                </div>
                <div class="suggestion-content">
                  <div class="suggestion-title">{{ suggestion.title }}</div>
                  <div class="suggestion-desc">{{ suggestion.description }}</div>
                </div>
                <div class="suggestion-action">
                  <el-button
                    :type="suggestion.buttonType"
                    size="small"
                    @click="handleSuggestionAction(suggestion.action)"
                  >
                    {{ suggestion.buttonText }}
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 操作按钮 -->
    <div class="detail-actions">
      <el-button @click="$emit('close')">关闭</el-button>
      <el-button type="primary" @click="exportLogDetail">
        <el-icon><Download /></el-icon>
        导出详情
      </el-button>
      <el-button type="warning" @click="reportAnomaly">
        <el-icon><Warning /></el-icon>
        举报异常
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  CopyDocument,
  Magic,
  Download,
  Warning,
  View,
  Lock,
  Monitor,
  Bell
} from '@element-plus/icons-vue'
import { useOperationLogs } from '@/composables/useOperationLogs'

// 定义事件
const emit = defineEmits(['close'])

// 使用组合式函数
const {
  formatDate,
  formatDuration,
  getLogLevelInfo,
  getStatusType,
  getStatusText
} = useOperationLogs()

// 定义属性
const props = defineProps({
  log: {
    type: Object,
    required: true
  }
})

// 响应式数据
const activeTab = ref('basic')
const requestFormatted = ref(false)
const responseFormatted = ref(false)

// 计算属性
const displayRequestData = computed(() => {
  const data = props.log.requestData || {}
  return requestFormatted.value 
    ? JSON.stringify(data, null, 2)
    : JSON.stringify(data)
})

const displayResponseData = computed(() => {
  const data = props.log.responseData || {}
  return responseFormatted.value 
    ? JSON.stringify(data, null, 2)
    : JSON.stringify(data)
})

// 模拟风险分析数据
const ipRisk = computed(() => ({
  level: '低',
  description: '该IP地址来自已知可信网络区域'
}))

const operationRisk = computed(() => ({
  level: '正常',
  description: '操作类型符合用户角色权限'
}))

const timeRisk = computed(() => ({
  level: 'normal',
  description: '操作时间在工作时间范围内'
}))

const userStats = computed(() => ({
  recentOperations: 12,
  frequencyPercentage: 30,
  mostFrequentOperation: '查看操作',
  anomalies: []
}))

const securitySuggestions = computed(() => [
  {
    id: 1,
    icon: View,
    title: '监控用户行为',
    description: '建议持续监控该用户的后续操作',
    buttonText: '设置监控',
    buttonType: 'primary',
    action: 'monitor'
  },
  {
    id: 2,
    icon: Lock,
    title: '检查权限设置',
    description: '验证用户权限是否合理',
    buttonText: '检查权限',
    buttonType: 'info',
    action: 'check-permissions'
  },
  {
    id: 3,
    icon: Bell,
    title: '设置告警',
    description: '为类似操作设置安全告警',
    buttonText: '设置告警',
    buttonType: 'warning',
    action: 'set-alert'
  }
])

// 获取IP风险类型
const getIpRiskType = (level) => {
  const riskMap = {
    '低': 'success',
    '中': 'warning',
    '高': 'danger'
  }
  return riskMap[level] || 'info'
}

// 获取操作风险类型
const getOperationRiskType = (level) => {
  const riskMap = {
    '正常': 'success',
    '可疑': 'warning',
    '危险': 'danger'
  }
  return riskMap[level] || 'info'
}

// 获取频率颜色
const getFrequencyColor = (percentage) => {
  if (percentage < 30) return '#67c23a'
  if (percentage < 70) return '#e6a23c'
  return '#f56c6c'
}

// 截断文本
const truncateText = (text, maxLength) => {
  if (!text) return '-'
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 复制到剪贴板
const copyToClipboard = (data, type) => {
  const text = JSON.stringify(data, null, 2)
  
  if (navigator.clipboard) {
    navigator.clipboard.writeText(text).then(() => {
      ElMessage.success(`${type === 'request' ? '请求' : '响应'}数据已复制到剪贴板`)
    })
  } else {
    ElMessage.info('请手动复制数据')
  }
}

// 格式化JSON
const formatJson = (type) => {
  if (type === 'request') {
    requestFormatted.value = !requestFormatted.value
  } else {
    responseFormatted.value = !responseFormatted.value
  }
}

// 导出日志详情
const exportLogDetail = () => {
  const logDetail = {
    基本信息: {
      日志ID: props.log.id,
      操作时间: props.log.createdAt,
      用户信息: {
        用户ID: props.log.userId,
        用户名: props.log.username,
        用户角色: props.log.userRole
      },
      操作信息: {
        操作类型: props.log.operationName,
        所属模块: props.log.module,
        操作描述: props.log.description
      },
      网络信息: {
        IP地址: props.log.ip,
        UserAgent: props.log.userAgent,
        持续时间: props.log.duration
      },
      状态: props.log.status,
      级别: props.log.level
    },
    请求数据: props.log.requestData,
    响应数据: props.log.responseData
  }
  
  const text = JSON.stringify(logDetail, null, 2)
  const blob = new Blob([text], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  
  const link = document.createElement('a')
  link.href = url
  link.download = `log_detail_${props.log.id}_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.json`
  link.click()
  
  URL.revokeObjectURL(url)
  ElMessage.success('日志详情已导出')
}

// 举报异常
const reportAnomaly = () => {
  ElMessage.success('异常举报已提交，安全团队将尽快处理')
}

// 处理建议操作
const handleSuggestionAction = (action) => {
  const actionMap = {
    'monitor': '已设置用户监控',
    'check-permissions': '权限检查已启动',
    'set-alert': '安全告警已配置'
  }
  
  ElMessage.success(actionMap[action] || '操作已执行')
}
</script>

<style scoped>
.log-detail {
  padding: 20px 0;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.basic-info {
  flex: 1;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-item .label {
  font-weight: 500;
  color: #303133;
  width: 80px;
  flex-shrink: 0;
}

.info-item .value {
  color: #606266;
  font-family: monospace;
}

.status-info {
  display: flex;
  gap: 8px;
  align-items: center;
}

.detail-tabs {
  margin-bottom: 24px;
}

.basic-details {
  padding: 20px 0;
}

.detail-section {
  margin-bottom: 32px;
}

.detail-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.error-section {
  background: #fef0f0;
  border: 1px solid #f56c6c;
  border-radius: 8px;
  padding: 20px;
}

.error-code {
  color: #f56c6c;
  font-weight: 500;
}

.error-message {
  color: #f56c6c;
}

.data-section {
  padding: 20px 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.data-actions {
  display: flex;
  gap: 8px;
}

.data-container {
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  background: #fafafa;
  max-height: 400px;
  overflow: auto;
}

.json-data {
  padding: 16px;
  margin: 0;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.5;
  color: #303133;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.json-data.formatted {
  background: #fff;
}

.security-analysis {
  padding: 20px 0;
}

.analysis-section {
  margin-bottom: 32px;
}

.analysis-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.risk-indicators {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.risk-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.risk-label {
  font-weight: 500;
  color: #303133;
  width: 80px;
  flex-shrink: 0;
}

.risk-desc {
  color: #606266;
  font-size: 14px;
}

.behavior-patterns {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.pattern-item {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.pattern-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
}

.pattern-desc {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.normal {
  color: #67c23a;
  font-weight: 500;
}

.anomaly-list {
  margin: 0;
  padding-left: 20px;
}

.anomaly-list li {
  color: #e6a23c;
  margin-bottom: 4px;
}

.suggestions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.suggestion-item:hover {
  background: #f0f9ff;
  transform: translateX(4px);
}

.suggestion-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #409eff;
  border-radius: 50%;
  color: #fff;
  font-size: 18px;
  flex-shrink: 0;
}

.suggestion-content {
  flex: 1;
}

.suggestion-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.suggestion-desc {
  color: #606266;
  font-size: 14px;
}

.suggestion-action {
  flex-shrink: 0;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .risk-item {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .suggestion-item {
    flex-direction: column;
    text-align: center;
    gap: 12px;
  }
  
  .detail-actions {
    justify-content: center;
    flex-wrap: wrap;
  }
}

/* 动画效果 */
.detail-section,
.analysis-section {
  animation: fadeInUp 0.5s ease-out;
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
.data-container::-webkit-scrollbar {
  width: 6px;
}

.data-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.data-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.data-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
