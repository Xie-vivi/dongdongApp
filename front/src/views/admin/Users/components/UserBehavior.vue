<template>
  <div class="user-behavior">
    <div class="behavior-header">
      <div class="header-info">
        <h3>用户行为分析</h3>
        <p>分析用户的活动模式、登录历史和风险指标</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button @click="exportBehaviorData">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="behavior-tabs">
      <!-- 行为概览 -->
      <el-tab-pane label="行为概览" name="overview">
        <div class="behavior-overview">
          <!-- 行为评分卡片 -->
          <div class="behavior-cards">
            <div class="behavior-card">
              <div class="card-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">活跃度评分</div>
                <div class="card-score">{{ behaviorAnalysis.activityLevel }}</div>
                <div class="card-desc">{{ getActivityLevelDesc(behaviorAnalysis.activityLevel) }}</div>
              </div>
            </div>

            <div class="behavior-card">
              <div class="card-icon">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">偏好时段</div>
                <div class="card-score">{{ behaviorAnalysis.preferredTime }}</div>
                <div class="card-desc">最活跃时间段</div>
              </div>
            </div>

            <div class="behavior-card">
              <div class="card-icon">
                <el-icon><Calendar /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">最活跃日</div>
                <div class="card-score">{{ behaviorAnalysis.mostActiveDay }}</div>
                <div class="card-desc">每周活跃规律</div>
              </div>
            </div>

            <div class="behavior-card">
              <div class="card-icon">
                <el-icon><Timer /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">平均会话</div>
                <div class="card-score">{{ behaviorAnalysis.averageSessionDuration }}分钟</div>
                <div class="card-desc">单次使用时长</div>
              </div>
            </div>
          </div>

          <!-- 互动指标 -->
          <div class="interaction-metrics">
            <h4>互动指标</h4>
            <div class="metrics-grid">
              <div class="metric-item">
                <div class="metric-label">跳出率</div>
                <div class="metric-value">{{ behaviorAnalysis.bounceRate }}%</div>
                <el-progress
                  :percentage="behaviorAnalysis.bounceRate"
                  :color="getBounceRateColor(behaviorAnalysis.bounceRate)"
                  :show-text="false"
                  :stroke-width="8"
                />
              </div>

              <div class="metric-item">
                <div class="metric-label">互动率</div>
                <div class="metric-value">{{ behaviorAnalysis.interactionRate }}%</div>
                <el-progress
                  :percentage="behaviorAnalysis.interactionRate"
                  :color="getInteractionRateColor(behaviorAnalysis.interactionRate)"
                  :show-text="false"
                  :stroke-width="8"
                />
              </div>

              <div class="metric-item">
                <div class="metric-label">内容偏好</div>
                <div class="content-preference">
                  <div
                    v-for="(count, type) in behaviorAnalysis.contentPreference"
                    :key="type"
                    class="preference-item"
                  >
                    <span class="preference-label">{{ getContentLabel(type) }}</span>
                    <div class="preference-bar">
                      <div
                        class="preference-fill"
                        :style="{ width: `${count}%`, backgroundColor: getContentColor(type) }"
                      ></div>
                    </div>
                    <span class="preference-count">{{ count }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 活动热力图 -->
          <div class="activity-heatmap">
            <h4>活动热力图</h4>
            <div class="heatmap-container">
              <div class="heatmap-legend">
                <span>低</span>
                <div class="legend-colors">
                  <div class="color-block" v-for="i in 5" :key="i" :class="`level-${i}`"></div>
                </div>
                <span>高</span>
              </div>
              <div class="heatmap-grid">
                <div
                  v-for="(day, dayIndex) in heatmapData"
                  :key="dayIndex"
                  class="heatmap-day"
                >
                  <div class="day-label">{{ day.label }}</div>
                  <div class="day-cells">
                    <div
                      v-for="(hour, hourIndex) in day.hours"
                      :key="hourIndex"
                      class="hour-cell"
                      :class="getHeatmapLevel(hour)"
                      :title="`${hourIndex}:00 - ${hourIndex + 1}:00, 活动次数: ${hour}`"
                    ></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 登录历史 -->
      <el-tab-pane label="登录历史" name="login">
        <div class="login-history">
          <div class="history-filters">
            <el-form inline>
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="loginDateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  @change="filterLoginHistory"
                />
              </el-form-item>
              <el-form-item label="登录状态">
                <el-select v-model="loginStatusFilter" placeholder="选择状态" clearable>
                  <el-option label="成功" value="success" />
                  <el-option label="失败" value="failed" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="filterLoginHistory">筛选</el-button>
                <el-button @click="resetLoginFilter">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <div class="login-stats">
            <div class="stat-item">
              <div class="stat-number">{{ loginHistory.length }}</div>
              <div class="stat-label">总登录次数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ successfulLogins }}</div>
              <div class="stat-label">成功登录</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ failedLogins }}</div>
              <div class="stat-label">失败登录</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ uniqueIPs }}</div>
              <div class="stat-label">不同IP</div>
            </div>
          </div>

          <el-table :data="filteredLoginHistory" stripe max-height="400">
            <el-table-column prop="loginTime" label="登录时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.loginTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="ip" label="IP地址" width="140" />
            <el-table-column prop="location" label="登录地点" />
            <el-table-column prop="userAgent" label="设备信息" min-width="200">
              <template #default="{ row }">
                <div class="device-info">
                  <div class="device-browser">{{ getBrowserInfo(row.userAgent) }}</div>
                  <div class="device-os">{{ getOSInfo(row.userAgent) }}</div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="success" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.success ? 'success' : 'danger'" size="small">
                  {{ row.success ? '成功' : '失败' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="duration" label="在线时长" width="120">
              <template #default="{ row }">
                {{ formatDuration(row.duration) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="{ row }">
                <el-button type="text" size="small" @click="viewLoginDetail(row)">
                  详情
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>

      <!-- 活动时间线 -->
      <el-tab-pane label="活动时间线" name="timeline">
        <div class="activity-timeline">
          <div class="timeline-filters">
            <el-form inline>
              <el-form-item label="活动类型">
                <el-select v-model="activityTypeFilter" placeholder="选择类型" clearable>
                  <el-option label="发布帖子" value="发布帖子" />
                  <el-option label="评论" value="评论" />
                  <el-option label="点赞" value="点赞" />
                  <el-option label="分享" value="分享" />
                  <el-option label="登录" value="登录" />
                  <el-option label="修改资料" value="修改资料" />
                </el-select>
              </el-form-item>
              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="activityDateRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始时间"
                  end-placeholder="结束时间"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="filterActivities">筛选</el-button>
                <el-button @click="resetActivityFilter">重置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <el-timeline>
            <el-timeline-item
              v-for="activity in filteredActivities"
              :key="activity.id"
              :timestamp="formatDate(activity.timestamp)"
              :type="getActivityType(activity.action)"
              :icon="getActivityIcon(activity.action)"
            >
              <div class="activity-item">
                <div class="activity-header">
                  <span class="activity-action">{{ activity.action }}</span>
                  <el-tag :type="getActivityTagType(activity.action)" size="small">
                    {{ getActivityCategory(activity.action) }}
                  </el-tag>
                </div>
                <div class="activity-content">
                  <div class="activity-target">目标: {{ activity.target }}</div>
                  <div class="activity-meta">
                    <span>IP: {{ activity.ip }}</span>
                    <span>设备: {{ getDeviceSummary(activity.userAgent) }}</span>
                  </div>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>

          <div class="timeline-pagination">
            <el-pagination
              v-model:current-page="activityPage"
              v-model:page-size="activityPageSize"
              :page-sizes="[10, 20, 50]"
              :total="totalActivities"
              layout="total, sizes, prev, pager, next"
              @size-change="handleActivitySizeChange"
              @current-change="handleActivityPageChange"
            />
          </div>
        </div>
      </el-tab-pane>

      <!-- 风险分析 -->
      <el-tab-pane label="风险分析" name="risk">
        <div class="risk-analysis">
          <div class="risk-overview">
            <h4>风险概览</h4>
            <div class="risk-cards">
              <div class="risk-card" :class="getRiskCardClass('low')">
                <div class="risk-icon">
                  <el-icon><CircleCheck /></el-icon>
                </div>
                <div class="risk-content">
                  <div class="risk-title">低风险</div>
                  <div class="risk-count">{{ riskIndicators.low || 0 }}</div>
                  <div class="risk-desc">正常行为模式</div>
                </div>
              </div>

              <div class="risk-card" :class="getRiskCardClass('medium')">
                <div class="risk-icon">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="risk-content">
                  <div class="risk-title">中风险</div>
                  <div class="risk-count">{{ riskIndicators.medium || 0 }}</div>
                  <div class="risk-desc">需要关注</div>
                </div>
              </div>

              <div class="risk-card" :class="getRiskCardClass('high')">
                <div class="risk-icon">
                  <el-icon><CircleClose /></el-icon>
                </div>
                <div class="risk-content">
                  <div class="risk-title">高风险</div>
                  <div class="risk-count">{{ riskIndicators.high || 0 }}</div>
                  <div class="risk-desc">需要立即处理</div>
                </div>
              </div>
            </div>
          </div>

          <div class="risk-indicators">
            <h4>风险指标详情</h4>
            <el-table :data="riskIndicatorDetails" stripe>
              <el-table-column prop="type" label="风险类型" width="180">
                <template #default="{ row }">
                  <div class="risk-type">
                    <el-icon :color="getRiskColor(row.severity)">
                      <component :is="getRiskIcon(row.severity)" />
                    </el-icon>
                    {{ getRiskTypeLabel(row.type) }}
                  </div>
                </template>
              </el-table-column>
              <el-table-column prop="severity" label="严重程度" width="120">
                <template #default="{ row }">
                  <el-tag :type="getRiskTagType(row.severity)" size="small">
                    {{ getSeverityLabel(row.severity) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="count" label="发生次数" width="100" />
              <el-table-column prop="description" label="描述" />
              <el-table-column prop="lastOccurrence" label="最后发生" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.lastOccurrence) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button type="primary" size="small" @click="handleRiskIndicator(row)">
                    处理
                  </el-button>
                  <el-button type="info" size="small" @click="viewRiskDetail(row)">
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="risk-recommendations">
            <h4>安全建议</h4>
            <div class="recommendations">
              <el-alert
                v-for="(recommendation, index) in securityRecommendations"
                :key="index"
                :title="recommendation.title"
                :type="recommendation.type"
                :description="recommendation.description"
                show-icon
                :closable="false"
                class="recommendation-item"
              />
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Download,
  TrendCharts,
  Clock,
  Calendar,
  Timer,
  CircleCheck,
  Warning,
  CircleClose,
  Edit,
  View,
  ChatLineRound,
  Star,
  Share,
  User,
  Tools
} from '@element-plus/icons-vue'
import { useUserManagement } from '@/composables/useUserManagement'

// Props
const props = defineProps({
  behaviorData: {
    type: Object,
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['refresh'])

// 使用组合式函数
const { formatDate } = useUserManagement()

// 响应式数据
const activeTab = ref('overview')
const loginDateRange = ref([])
const activityDateRange = ref([])
const loginStatusFilter = ref('')
const activityTypeFilter = ref('')
const activityPage = ref(1)
const activityPageSize = ref(20)

// 模拟数据
const behaviorAnalysis = ref({
  activityLevel: 'high',
  preferredTime: '14:00-16:00',
  mostActiveDay: 'Wednesday',
  averageSessionDuration: 45,
  bounceRate: 23.5,
  interactionRate: 78.2,
  contentPreference: {
    posts: 45,
    comments: 30,
    likes: 25
  }
})

const loginHistory = ref([
  {
    id: 1,
    loginTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    location: '中国 北京市',
    success: true,
    duration: 180
  },
  {
    id: 2,
    loginTime: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.101',
    userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)',
    location: '中国 上海市',
    success: true,
    duration: 45
  },
  {
    id: 3,
    loginTime: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.102',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    location: '中国 深圳市',
    success: false,
    duration: 0
  }
])

const activityTimeline = ref([
  {
    id: 1,
    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
    action: '发布帖子',
    target: '技术分享帖子',
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
  },
  {
    id: 2,
    timestamp: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(),
    action: '评论',
    target: '关于Vue3的讨论',
    ip: '192.168.1.100',
    userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36'
  },
  {
    id: 3,
    timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
    action: '点赞',
    target: '前端开发技巧',
    ip: '192.168.1.101',
    userAgent: 'Mozilla/5.0 (iPhone; CPU iPhone OS 14_0 like Mac OS X)'
  }
])

const riskIndicators = ref({
  low: 0,
  medium: 3,
  high: 1
})

const riskIndicatorDetails = ref([
  {
    type: 'unusual_login_time',
    severity: 'medium',
    count: 3,
    description: '异常登录时间检测',
    lastOccurrence: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString()
  },
  {
    type: 'multiple_ip_login',
    severity: 'low',
    count: 1,
    description: '多IP登录',
    lastOccurrence: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
  },
  {
    type: 'suspicious_activity',
    severity: 'high',
    count: 1,
    description: '可疑活动模式',
    lastOccurrence: new Date(Date.now() - 72 * 60 * 60 * 1000).toISOString()
  }
])

const securityRecommendations = ref([
  {
    title: '建议启用双因素认证',
    type: 'warning',
    description: '检测到异常登录行为，建议启用双因素认证以提高账户安全性。'
  },
  {
    title: '定期更换密码',
    type: 'info',
    description: '建议用户定期更换密码，使用强密码策略。'
  },
  {
    title: '监控异常活动',
    type: 'success',
    description: '已设置异常活动监控，将及时通知管理员。'
  }
])

// 热力图数据
const heatmapData = ref([
  {
    label: '周一',
    hours: Array.from({ length: 24 }, () => Math.floor(Math.random() * 10))
  },
  {
    label: '周二',
    hours: Array.from({ length: 24 }, () => Math.floor(Math.random() * 10))
  },
  {
    label: '周三',
    hours: Array.from({ length: 24 }, () => Math.floor(Math.random() * 10))
  },
  {
    label: '周四',
    hours: Array.from({ length: 24 }, () => Math.floor(Math.random() * 10))
  },
  {
    label: '周五',
    hours: Array.from({ length: 24 }, () => Math.floor(Math.random() * 10))
  },
  {
    label: '周六',
    hours: Array.from({ length: 24 }, () => Math.floor(Math.random() * 10))
  },
  {
    label: '周日',
    hours: Array.from({ length: 24 }, () => Math.floor(Math.random() * 10))
  }
])

// 计算属性
const filteredLoginHistory = computed(() => {
  let filtered = [...loginHistory.value]
  
  if (loginStatusFilter.value) {
    filtered = filtered.filter(login => 
      loginStatusFilter.value === 'success' ? login.success : !login.success
    )
  }
  
  if (loginDateRange.value && loginDateRange.value.length === 2) {
    const [startDate, endDate] = loginDateRange.value
    filtered = filtered.filter(login => {
      const loginDate = new Date(login.loginTime).toISOString().split('T')[0]
      return loginDate >= startDate && loginDate <= endDate
    })
  }
  
  return filtered
})

const filteredActivities = computed(() => {
  let filtered = [...activityTimeline.value]
  
  if (activityTypeFilter.value) {
    filtered = filtered.filter(activity => activity.action === activityTypeFilter.value)
  }
  
  if (activityDateRange.value && activityDateRange.value.length === 2) {
    const [startTime, endTime] = activityDateRange.value
    filtered = filtered.filter(activity => {
      return activity.timestamp >= startTime && activity.timestamp <= endTime
    })
  }
  
  const start = (activityPage.value - 1) * activityPageSize.value
  const end = start + activityPageSize.value
  return filtered.slice(start, end)
})

const totalActivities = computed(() => {
  let filtered = [...activityTimeline.value]
  
  if (activityTypeFilter.value) {
    filtered = filtered.filter(activity => activity.action === activityTypeFilter.value)
  }
  
  return filtered.length
})

const successfulLogins = computed(() => 
  loginHistory.value.filter(login => login.success).length
)

const failedLogins = computed(() => 
  loginHistory.value.filter(login => !login.success).length
)

const uniqueIPs = computed(() => {
  const ips = new Set(loginHistory.value.map(login => login.ip))
  return ips.size
})

// 方法
const getActivityLevelDesc = (level) => {
  const descMap = {
    high: '非常活跃',
    medium: '中等活跃',
    low: '不太活跃'
  }
  return descMap[level] || level
}

const getBounceRateColor = (rate) => {
  if (rate < 30) return '#67c23a'
  if (rate < 50) return '#e6a23c'
  return '#f56c6c'
}

const getInteractionRateColor = (rate) => {
  if (rate > 70) return '#67c23a'
  if (rate > 50) return '#e6a23c'
  return '#f56c6c'
}

const getContentLabel = (type) => {
  const labelMap = {
    posts: '发帖',
    comments: '评论',
    likes: '点赞'
  }
  return labelMap[type] || type
}

const getContentColor = (type) => {
  const colorMap = {
    posts: '#409eff',
    comments: '#67c23a',
    likes: '#e6a23c'
  }
  return colorMap[type] || '#909399'
}

const getHeatmapLevel = (value) => {
  if (value === 0) return 'level-0'
  if (value <= 2) return 'level-1'
  if (value <= 4) return 'level-2'
  if (value <= 6) return 'level-3'
  if (value <= 8) return 'level-4'
  return 'level-5'
}

const getBrowserInfo = (userAgent) => {
  if (userAgent.includes('Chrome')) return 'Chrome'
  if (userAgent.includes('Firefox')) return 'Firefox'
  if (userAgent.includes('Safari')) return 'Safari'
  if (userAgent.includes('Edge')) return 'Edge'
  return '其他浏览器'
}

const getOSInfo = (userAgent) => {
  if (userAgent.includes('Windows')) return 'Windows'
  if (userAgent.includes('Mac')) return 'macOS'
  if (userAgent.includes('iPhone')) return 'iOS'
  if (userAgent.includes('Android')) return 'Android'
  if (userAgent.includes('Linux')) return 'Linux'
  return '其他系统'
}

const getDeviceSummary = (userAgent) => {
  const browser = getBrowserInfo(userAgent)
  const os = getOSInfo(userAgent)
  return `${browser} / ${os}`
}

const getActivityType = (action) => {
  const typeMap = {
    '发布帖子': 'success',
    '评论': 'primary',
    '点赞': 'warning',
    '分享': 'info',
    '登录': 'info',
    '修改资料': 'warning'
  }
  return typeMap[action] || 'info'
}

const getActivityIcon = (action) => {
  const iconMap = {
    '发布帖子': Edit,
    '评论': ChatLineRound,
    '点赞': Star,
    '分享': Share,
    '登录': User,
    '修改资料': Tools
  }
  return iconMap[action] || View
}

const getActivityTagType = (action) => {
  const typeMap = {
    '发布帖子': 'success',
    '评论': 'primary',
    '点赞': 'warning',
    '分享': 'info',
    '登录': 'info',
    '修改资料': 'warning'
  }
  return typeMap[action] || 'info'
}

const getActivityCategory = (action) => {
  const categoryMap = {
    '发布帖子': '内容创建',
    '评论': '互动',
    '点赞': '互动',
    '分享': '互动',
    '登录': '系统',
    '修改资料': '个人'
  }
  return categoryMap[action] || '其他'
}

const formatDuration = (minutes) => {
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分钟`
}

const getRiskCardClass = (level) => {
  return `risk-${level}`
}

const getRiskColor = (severity) => {
  const colorMap = {
    low: '#67c23a',
    medium: '#e6a23c',
    high: '#f56c6c'
  }
  return colorMap[severity] || '#909399'
}

const getRiskIcon = (severity) => {
  const iconMap = {
    low: CircleCheck,
    medium: Warning,
    high: CircleClose
  }
  return iconMap[severity] || Warning
}

const getRiskTagType = (severity) => {
  const typeMap = {
    low: 'success',
    medium: 'warning',
    high: 'danger'
  }
  return typeMap[severity] || 'info'
}

const getRiskTypeLabel = (type) => {
  const labelMap = {
    unusual_login_time: '异常登录时间',
    multiple_ip_login: '多IP登录',
    suspicious_activity: '可疑活动',
    failed_login_attempts: '登录失败次数异常',
    data_access_anomaly: '数据访问异常'
  }
  return labelMap[type] || type
}

const getSeverityLabel = (severity) => {
  const labelMap = {
    low: '低',
    medium: '中',
    high: '高'
  }
  return labelMap[severity] || severity
}

// 操作方法
const refreshData = () => {
  emit('refresh')
  ElMessage.success('数据刷新成功')
}

const exportBehaviorData = () => {
  ElMessage.success('行为数据导出成功')
}

const filterLoginHistory = () => {
  // 筛选逻辑已在计算属性中实现
}

const resetLoginFilter = () => {
  loginDateRange.value = []
  loginStatusFilter.value = ''
}

const filterActivities = () => {
  // 筛选逻辑已在计算属性中实现
}

const resetActivityFilter = () => {
  activityTypeFilter.value = ''
  activityDateRange.value = []
  activityPage.value = 1
}

const handleActivitySizeChange = (size) => {
  activityPageSize.value = size
  activityPage.value = 1
}

const handleActivityPageChange = (page) => {
  activityPage.value = page
}

const viewLoginDetail = (login) => {
  ElMessage.info('登录详情功能开发中...')
}

const handleRiskIndicator = (indicator) => {
  ElMessage.success(`已处理风险指标: ${getRiskTypeLabel(indicator.type)}`)
}

const viewRiskDetail = (indicator) => {
  ElMessage.info('风险详情功能开发中...')
}

// 组件挂载
onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.user-behavior {
  padding: 20px;
}

.behavior-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.header-info h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
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

.behavior-tabs {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

/* 行为概览样式 */
.behavior-overview {
  padding: 20px;
}

.behavior-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.behavior-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.behavior-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 8px;
  color: #fff;
  font-size: 20px;
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.card-score {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.card-desc {
  font-size: 12px;
  color: #606266;
}

.interaction-metrics {
  margin-bottom: 24px;
}

.interaction-metrics h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.metric-item {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.content-preference {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.preference-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.preference-label {
  font-size: 12px;
  color: #606266;
  min-width: 40px;
}

.preference-bar {
  flex: 1;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.preference-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.preference-count {
  font-size: 12px;
  color: #303133;
  min-width: 30px;
  text-align: right;
}

.activity-heatmap h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.heatmap-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.heatmap-legend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 20px;
  font-size: 12px;
  color: #606266;
}

.legend-colors {
  display: flex;
  gap: 2px;
}

.color-block {
  width: 20px;
  height: 12px;
  border-radius: 2px;
}

.color-block.level-1 { background: #e6f7ff; }
.color-block.level-2 { background: #91d5ff; }
.color-block.level-3 { background: #40a9ff; }
.color-block.level-4 { background: #1890ff; }
.color-block.level-5 { background: #0050b3; }

.heatmap-grid {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.heatmap-day {
  display: flex;
  align-items: center;
  gap: 12px;
}

.day-label {
  font-size: 12px;
  color: #606266;
  min-width: 40px;
  text-align: right;
}

.day-cells {
  display: flex;
  gap: 2px;
}

.hour-cell {
  width: 20px;
  height: 12px;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hour-cell:hover {
  transform: scale(1.2);
}

.hour-cell.level-0 { background: #f5f5f5; }
.hour-cell.level-1 { background: #e6f7ff; }
.hour-cell.level-2 { background: #91d5ff; }
.hour-cell.level-3 { background: #40a9ff; }
.hour-cell.level-4 { background: #1890ff; }
.hour-cell.level-5 { background: #0050b3; }

/* 登录历史样式 */
.login-history {
  padding: 20px;
}

.history-filters {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.login-stats {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stat-item {
  text-align: center;
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
}

.device-info {
  line-height: 1.4;
}

.device-browser {
  font-weight: 500;
  color: #303133;
}

.device-os {
  font-size: 12px;
  color: #909399;
}

/* 活动时间线样式 */
.activity-timeline {
  padding: 20px;
}

.timeline-filters {
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.activity-item {
  padding: 8px 0;
}

.activity-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.activity-action {
  font-weight: 600;
  color: #303133;
}

.activity-content {
  font-size: 14px;
  color: #606266;
}

.activity-target {
  margin-bottom: 4px;
}

.activity-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 16px;
}

.timeline-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

/* 风险分析样式 */
.risk-analysis {
  padding: 20px;
}

.risk-overview h4,
.risk-indicators h4,
.risk-recommendations h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.risk-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.risk-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.risk-card.risk-low {
  background: #f6ffed;
  border-color: #b7eb8f;
}

.risk-card.risk-medium {
  background: #fffbe6;
  border-color: #ffe58f;
}

.risk-card.risk-high {
  background: #fff2f0;
  border-color: #ffccc7;
}

.risk-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 8px;
  font-size: 20px;
}

.risk-card.risk-low .risk-icon {
  background: #52c41a;
  color: #fff;
}

.risk-card.risk-medium .risk-icon {
  background: #faad14;
  color: #fff;
}

.risk-card.risk-high .risk-icon {
  background: #ff4d4f;
  color: #fff;
}

.risk-content {
  flex: 1;
}

.risk-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.risk-count {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.risk-desc {
  font-size: 12px;
  color: #606266;
}

.risk-type {
  display: flex;
  align-items: center;
  gap: 8px;
}

.risk-indicators {
  margin-bottom: 24px;
}

.risk-recommendations {
  margin-bottom: 20px;
}

.recommendations {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recommendation-item {
  border-radius: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-behavior {
    padding: 16px;
  }
  
  .behavior-header {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
  
  .behavior-cards {
    grid-template-columns: 1fr;
  }
  
  .metrics-grid {
    grid-template-columns: 1fr;
  }
  
  .risk-cards {
    grid-template-columns: 1fr;
  }
  
  .login-stats {
    flex-direction: column;
    gap: 12px;
  }
}

/* 动画效果 */
.behavior-card,
.metric-item,
.risk-card {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
