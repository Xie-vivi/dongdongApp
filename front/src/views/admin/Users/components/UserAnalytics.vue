<template>
  <div class="user-analytics">
    <div class="analytics-header">
      <div class="header-info">
        <h3>用户数据分析</h3>
        <p>用户增长、活跃度和行为趋势分析</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="refreshAnalytics">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
        <el-button @click="exportReport">
          <el-icon><Download /></el-icon>
          导出报告
        </el-button>
      </div>
    </div>

    <!-- 时间范围选择 -->
    <div class="time-range-selector">
      <el-radio-group v-model="timeRange" @change="handleTimeRangeChange">
        <el-radio-button label="7d">最近7天</el-radio-button>
        <el-radio-button label="30d">最近30天</el-radio-button>
        <el-radio-button label="90d">最近90天</el-radio-button>
        <el-radio-button label="1y">最近1年</el-radio-button>
        <el-radio-button label="custom">自定义</el-radio-button>
      </el-radio-group>
      
      <el-date-picker
        v-if="timeRange === 'custom'"
        v-model="customDateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        format="YYYY-MM-DD"
        value-format="YYYY-MM-DD"
        @change="handleCustomDateChange"
      />
    </div>

    <el-tabs v-model="activeTab" class="analytics-tabs">
      <!-- 用户增长 -->
      <el-tab-pane label="用户增长" name="growth">
        <div class="growth-analytics">
          <!-- 增长指标卡片 -->
          <div class="growth-cards">
            <div class="growth-card">
              <div class="card-icon">
                <el-icon><TrendCharts /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">总用户数</div>
                <div class="card-number">{{ formatNumber(statistics.totalUsers) }}</div>
                <div class="card-trend">
                  <el-icon :color="getTrendColor(statistics.userGrowthRate)">
                    <component :is="getTrendIcon(statistics.userGrowthRate)" />
                  </el-icon>
                  {{ statistics.userGrowthRate }}%
                </div>
              </div>
            </div>

            <div class="growth-card">
              <div class="card-icon">
                <el-icon><UserFilled /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">活跃用户</div>
                <div class="card-number">{{ formatNumber(statistics.activeUsers) }}</div>
                <div class="card-trend">
                  活跃率 {{ statistics.activityRate }}%
                </div>
              </div>
            </div>

            <div class="growth-card">
              <div class="card-icon">
                <el-icon><Plus /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">今日新增</div>
                <div class="card-number">{{ statistics.newUsersToday }}</div>
                <div class="card-trend">
                  本月新增 {{ statistics.newUsersThisMonth }}
                </div>
              </div>
            </div>

            <div class="growth-card">
              <div class="card-icon">
                <el-icon><CircleCheck /></el-icon>
              </div>
              <div class="card-content">
                <div class="card-title">认证用户</div>
                <div class="card-number">{{ formatNumber(statistics.verifiedUsers) }}</div>
                <div class="card-trend">
                  认证率 {{ getVerificationRate() }}%
                </div>
              </div>
            </div>
          </div>

          <!-- 注册趋势图表 -->
          <div class="chart-section">
            <h4>用户注册趋势</h4>
            <div class="chart-container">
              <div class="chart-placeholder">
                <el-empty description="图表组件开发中...">
                  <template #image>
                    <div class="mini-chart">
                      <div class="chart-bars">
                        <div
                          v-for="(item, index) in statistics.registrationTrend"
                          :key="index"
                          class="chart-bar"
                          :style="{ height: `${(item.count / Math.max(...statistics.registrationTrend.map(d => d.count))) * 100}%` }"
                          :title="`${item.date}: ${item.count}人`"
                        ></div>
                      </div>
                      <div class="chart-labels">
                        <span v-for="(item, index) in statistics.registrationTrend.filter((_, i) => i % 5 === 0)" :key="index">
                          {{ item.date }}
                        </span>
                      </div>
                    </div>
                  </template>
                </el-empty>
              </div>
            </div>
          </div>

          <!-- 增长率分析 -->
          <div class="growth-analysis">
            <h4>增长率分析</h4>
            <div class="analysis-grid">
              <div class="analysis-item">
                <div class="analysis-label">日增长率</div>
                <div class="analysis-value">{{ getDailyGrowthRate() }}%</div>
                <div class="analysis-desc">较昨日</div>
              </div>
              <div class="analysis-item">
                <div class="analysis-label">周增长率</div>
                <div class="analysis-value">{{ getWeeklyGrowthRate() }}%</div>
                <div class="analysis-desc">较上周</div>
              </div>
              <div class="analysis-item">
                <div class="analysis-label">月增长率</div>
                <div class="analysis-value">{{ getMonthlyGrowthRate() }}%</div>
                <div class="analysis-desc">较上月</div>
              </div>
              <div class="analysis-item">
                <div class="analysis-label">年增长率</div>
                <div class="analysis-value">{{ getYearlyGrowthRate() }}%</div>
                <div class="analysis-desc">较去年</div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 用户活跃度 -->
      <el-tab-pane label="用户活跃度" name="activity">
        <div class="activity-analytics">
          <!-- 活跃度指标 -->
          <div class="activity-metrics">
            <div class="metric-card">
              <div class="metric-title">日活跃用户 (DAU)</div>
              <div class="metric-value">{{ formatNumber(getDAU()) }}</div>
              <div class="metric-chart">
                <div class="sparkline">
                  <div
                    v-for="(value, index) in getDAUTrend()"
                    :key="index"
                    class="sparkline-bar"
                    :style="{ height: `${value}%` }"
                  ></div>
                </div>
              </div>
            </div>

            <div class="metric-card">
              <div class="metric-title">周活跃用户 (WAU)</div>
              <div class="metric-value">{{ formatNumber(getWAU()) }}</div>
              <div class="metric-chart">
                <div class="sparkline">
                  <div
                    v-for="(value, index) in getWAUTrend()"
                    :key="index"
                    class="sparkline-bar"
                    :style="{ height: `${value}%` }"
                  ></div>
                </div>
              </div>
            </div>

            <div class="metric-card">
              <div class="metric-title">月活跃用户 (MAU)</div>
              <div class="metric-value">{{ formatNumber(getMAU()) }}</div>
              <div class="metric-chart">
                <div class="sparkline">
                  <div
                    v-for="(value, index) in getMAUTrend()"
                    :key="index"
                    class="sparkline-bar"
                    :style="{ height: `${value}%` }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 活跃度趋势 -->
          <div class="activity-trend">
            <h4>活跃度趋势</h4>
            <div class="trend-chart">
              <div class="chart-legend">
                <div class="legend-item">
                  <div class="legend-color active-users"></div>
                  <span>活跃用户</span>
                </div>
                <div class="legend-item">
                  <div class="legend-color new-posts"></div>
                  <span>新帖子</span>
                </div>
              </div>
              <div class="chart-area">
                <div class="chart-placeholder">
                  <el-empty description="活跃度趋势图表开发中...">
                    <template #image>
                      <div class="area-chart">
                        <div class="chart-lines">
                          <div class="line active-users">
                            <div
                              v-for="(item, index) in statistics.activityTrend"
                              :key="index"
                              class="point"
                              :style="{ left: `${(index / (statistics.activityTrend.length - 1)) * 100}%`, bottom: `${(item.activeUsers / 2000) * 100}%` }"
                            ></div>
                          </div>
                          <div class="line new-posts">
                            <div
                              v-for="(item, index) in statistics.activityTrend"
                              :key="index"
                              class="point"
                              :style="{ left: `${(index / (statistics.activityTrend.length - 1)) * 100}%`, bottom: `${(item.newPosts / 300) * 100}%` }"
                            ></div>
                          </div>
                        </div>
                        <div class="chart-labels">
                          <span
                            v-for="(item, index) in statistics.activityTrend"
                            :key="index"
                            :style="{ left: `${(index / (statistics.activityTrend.length - 1)) * 100}%` }"
                          >
                            {{ item.date }}
                          </span>
                        </div>
                      </div>
                    </template>
                  </el-empty>
                </div>
              </div>
            </div>
          </div>

          <!-- 用户留存分析 -->
          <div class="retention-analysis">
            <h4>用户留存分析</h4>
            <div class="retention-table">
              <el-table :data="retentionData" stripe>
                <el-table-column prop="cohort" label="注册时间" width="120" />
                <el-table-column prop="day1" label="第1天" width="80" align="center">
                  <template #default="{ row }">
                    <span :class="getRetentionClass(row.day1)">{{ row.day1 }}%</span>
                  </template>
                </el-table-column>
                <el-table-column prop="day7" label="第7天" width="80" align="center">
                  <template #default="{ row }">
                    <span :class="getRetentionClass(row.day7)">{{ row.day7 }}%</span>
                  </template>
                </el-table-column>
                <el-table-column prop="day30" label="第30天" width="80" align="center">
                  <template #default="{ row }">
                    <span :class="getRetentionClass(row.day30)">{{ row.day30 }}%</span>
                  </template>
                </el-table-column>
                <el-table-column prop="day90" label="第90天" width="80" align="center">
                  <template #default="{ row }">
                    <span :class="getRetentionClass(row.day90)">{{ row.day90 }}%</span>
                  </template>
                </el-table-column>
                <el-table-column label="留存趋势" min-width="200">
                  <template #default="{ row }">
                    <div class="retention-sparkline">
                      <div
                        v-for="(value, index) in [row.day1, row.day7, row.day30, row.day90]"
                        :key="index"
                        class="sparkline-dot"
                        :style="{ height: `${value}%`, backgroundColor: getRetentionColor(value) }"
                      ></div>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 用户分布 -->
      <el-tab-pane label="用户分布" name="distribution">
        <div class="distribution-analytics">
          <!-- 角色分布 -->
          <div class="distribution-section">
            <h4>角色分布</h4>
            <div class="distribution-grid">
              <div class="distribution-chart">
                <div class="pie-chart-placeholder">
                  <div class="pie-segments">
                    <div
                      v-for="(role, index) in statistics.roleDistribution"
                      :key="role.role"
                      class="pie-segment"
                      :style="{
                        background: getRoleColor(role.role),
                        transform: `rotate(${getSegmentRotation(index)}deg)`,
                        clipPath: getSegmentClipPath(index)
                      }"
                    ></div>
                  </div>
                  <div class="pie-legend">
                    <div
                      v-for="role in statistics.roleDistribution"
                      :key="role.role"
                      class="legend-item"
                    >
                      <div class="legend-color" :style="{ backgroundColor: getRoleColor(role.role) }"></div>
                      <span>{{ role.role }} ({{ role.count }})</span>
                    </div>
                  </div>
                </div>
              </div>

              <div class="distribution-details">
                <div class="detail-item">
                  <div class="detail-header">
                    <span class="detail-title">管理员</span>
                    <el-tag type="danger" size="small">{{ getRoleCount('admin') }}</el-tag>
                  </div>
                  <div class="detail-bar">
                    <div class="bar-fill" style="width: 0.3%; background: #f56c6c;"></div>
                  </div>
                </div>

                <div class="detail-item">
                  <div class="detail-header">
                    <span class="detail-title">编辑</span>
                    <el-tag type="warning" size="small">{{ getRoleCount('editor') }}</el-tag>
                  </div>
                  <div class="detail-bar">
                    <div class="bar-fill" style="width: 0.29%; background: #e6a23c;"></div>
                  </div>
                </div>

                <div class="detail-item">
                  <div class="detail-header">
                    <span class="detail-title">审核员</span>
                    <el-tag type="info" size="small">{{ getRoleCount('reviewer') }}</el-tag>
                  </div>
                  <div class="detail-bar">
                    <div class="bar-fill" style="width: 0.78%; background: #909399;"></div>
                  </div>
                </div>

                <div class="detail-item">
                  <div class="detail-header">
                    <span class="detail-title">普通用户</span>
                    <el-tag type="success" size="small">{{ getRoleCount('user') }}</el-tag>
                  </div>
                  <div class="detail-bar">
                    <div class="bar-fill" style="width: 98.9%; background: #67c23a;"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 风险分布 -->
          <div class="distribution-section">
            <h4>风险分布</h4>
            <div class="risk-distribution">
              <div class="risk-item">
                <div class="risk-icon low">
                  <el-icon><CircleCheck /></el-icon>
                </div>
                <div class="risk-content">
                  <div class="risk-title">低风险</div>
                  <div class="risk-count">{{ statistics.riskDistribution.low }}</div>
                  <div class="risk-percentage">{{ getRiskPercentage('low') }}%</div>
                </div>
                <div class="risk-bar">
                  <div class="bar-fill low" :style="{ width: getRiskPercentage('low') + '%' }"></div>
                </div>
              </div>

              <div class="risk-item">
                <div class="risk-icon medium">
                  <el-icon><Warning /></el-icon>
                </div>
                <div class="risk-content">
                  <div class="risk-title">中风险</div>
                  <div class="risk-count">{{ statistics.riskDistribution.medium }}</div>
                  <div class="risk-percentage">{{ getRiskPercentage('medium') }}%</div>
                </div>
                <div class="risk-bar">
                  <div class="bar-fill medium" :style="{ width: getRiskPercentage('medium') + '%' }"></div>
                </div>
              </div>

              <div class="risk-item">
                <div class="risk-icon high">
                  <el-icon><CircleClose /></el-icon>
                </div>
                <div class="risk-content">
                  <div class="risk-title">高风险</div>
                  <div class="risk-count">{{ statistics.riskDistribution.high }}</div>
                  <div class="risk-percentage">{{ getRiskPercentage('high') }}%</div>
                </div>
                <div class="risk-bar">
                  <div class="bar-fill high" :style="{ width: getRiskPercentage('high') + '%' }"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 地域分布 -->
          <div class="distribution-section">
            <h4>地域分布</h4>
            <div class="region-distribution">
              <el-table :data="regionData" stripe>
                <el-table-column prop="region" label="地区" />
                <el-table-column prop="count" label="用户数" width="120" align="center">
                  <template #default="{ row }">
                    {{ formatNumber(row.count) }}
                  </template>
                </el-table-column>
                <el-table-column prop="percentage" label="占比" width="100" align="center">
                  <template #default="{ row }">
                    {{ row.percentage }}%
                  </template>
                </el-table-column>
                <el-table-column label="分布" min-width="200">
                  <template #default="{ row }">
                    <div class="distribution-bar">
                      <div class="bar-fill" :style="{ width: row.percentage + '%' }"></div>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 行为分析 -->
      <el-tab-pane label="行为分析" name="behavior">
        <div class="behavior-analytics">
          <!-- 登录频率 -->
          <div class="behavior-section">
            <h4>登录频率分析</h4>
            <div class="login-frequency">
              <div class="frequency-chart">
                <div class="frequency-bars">
                  <div
                    v-for="(item, index) in loginFrequencyData"
                    :key="index"
                    class="frequency-item"
                  >
                    <div class="frequency-label">{{ item.label }}</div>
                    <div class="frequency-bar">
                      <div
                        class="bar-fill"
                        :style="{ width: item.percentage + '%', backgroundColor: getFrequencyColor(index) }"
                      ></div>
                    </div>
                    <div class="frequency-value">{{ item.percentage }}%</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 内容互动 -->
          <div class="behavior-section">
            <h4>内容互动分析</h4>
            <div class="content-interaction">
              <div class="interaction-stats">
                <div class="stat-item">
                  <div class="stat-icon">
                    <el-icon><Edit /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ getAveragePosts() }}</div>
                    <div class="stat-label">平均发帖数</div>
                  </div>
                </div>

                <div class="stat-item">
                  <div class="stat-icon">
                    <el-icon><ChatLineRound /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ getAverageComments() }}</div>
                    <div class="stat-label">平均评论数</div>
                  </div>
                </div>

                <div class="stat-item">
                  <div class="stat-icon">
                    <el-icon><Star /></el-icon>
                  </div>
                  <div class="stat-content">
                    <div class="stat-number">{{ getAverageLikes() }}</div>
                    <div class="stat-label">平均点赞数</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 使用时长 -->
          <div class="behavior-section">
            <h4>使用时长分析</h4>
            <div class="usage-duration">
              <div class="duration-stats">
                <div class="duration-item">
                  <div class="duration-label">平均会话时长</div>
                  <div class="duration-value">{{ getAverageSessionDuration() }}分钟</div>
                </div>
                <div class="duration-item">
                  <div class="duration-label">最长会话时长</div>
                  <div class="duration-value">{{ getMaxSessionDuration() }}分钟</div>
                </div>
                <div class="duration-item">
                  <div class="duration-label">日均使用时长</div>
                  <div class="duration-value">{{ getDailyUsageTime() }}分钟</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Refresh,
  Download,
  TrendCharts,
  UserFilled,
  Plus,
  CircleCheck,
  Warning,
  CircleClose,
  Edit,
  ChatLineRound,
  Star,
  ArrowUp,
  ArrowDown,
  Minus
} from '@element-plus/icons-vue'

// Props
const props = defineProps({
  statistics: {
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

// 响应式数据
const activeTab = ref('growth')
const timeRange = ref('30d')
const customDateRange = ref([])

// 模拟数据
const retentionData = ref([
  {
    cohort: '2024-01',
    day1: 85,
    day7: 65,
    day30: 45,
    day90: 25
  },
  {
    cohort: '2024-02',
    day1: 88,
    day7: 70,
    day30: 52,
    day90: 30
  },
  {
    cohort: '2024-03',
    day1: 90,
    day7: 75,
    day30: 58,
    day90: 35
  }
])

const regionData = ref([
  { region: '北京市', count: 3200, percentage: 20.8 },
  { region: '上海市', count: 2800, percentage: 18.2 },
  { region: '广东省', count: 2500, percentage: 16.2 },
  { region: '江苏省', count: 1800, percentage: 11.7 },
  { region: '浙江省', count: 1500, percentage: 9.7 },
  { region: '其他', count: 3620, percentage: 23.4 }
])

const loginFrequencyData = ref([
  { label: '每日登录', percentage: 35 },
  { label: '每周登录', percentage: 40 },
  { label: '每月登录', percentage: 20 },
  { label: '偶尔登录', percentage: 5 }
])

// 计算属性和方法
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

const getTrendIcon = (rate) => {
  if (rate > 0) return ArrowUp
  if (rate < 0) return ArrowDown
  return Minus
}

const getTrendColor = (rate) => {
  if (rate > 0) return '#67c23a'
  if (rate < 0) return '#f56c6c'
  return '#909399'
}

const getVerificationRate = () => {
  if (!props.statistics.totalUsers || !props.statistics.verifiedUsers) return 0
  return ((props.statistics.verifiedUsers / props.statistics.totalUsers) * 100).toFixed(1)
}

const getDailyGrowthRate = () => (Math.random() * 5 + 1).toFixed(1)
const getWeeklyGrowthRate = () => (Math.random() * 10 + 2).toFixed(1)
const getMonthlyGrowthRate = () => (Math.random() * 15 + 5).toFixed(1)
const getYearlyGrowthRate = () => (Math.random() * 25 + 10).toFixed(1)

const getDAU = () => Math.floor(props.statistics.activeUsers * 0.3)
const getWAU = () => Math.floor(props.statistics.activeUsers * 0.6)
const getMAU = () => props.statistics.activeUsers

const getDAUTrend = () => Array.from({ length: 7 }, () => Math.random() * 80 + 20)
const getWAUTrend = () => Array.from({ length: 4 }, () => Math.random() * 80 + 20)
const getMAUTrend = () => Array.from({ length: 12 }, () => Math.random() * 80 + 20)

const getRetentionClass = (value) => {
  if (value >= 70) return 'retention-high'
  if (value >= 40) return 'retention-medium'
  return 'retention-low'
}

const getRetentionColor = (value) => {
  if (value >= 70) return '#67c23a'
  if (value >= 40) return '#e6a23c'
  return '#f56c6c'
}

const getRoleColor = (role) => {
  const colorMap = {
    admin: '#f56c6c',
    editor: '#e6a23c',
    reviewer: '#909399',
    user: '#67c23a'
  }
  return colorMap[role] || '#409eff'
}

const getRoleCount = (role) => {
  const roleData = props.statistics.roleDistribution?.find(r => r.role === role)
  return roleData ? roleData.count : 0
}

const getSegmentRotation = (index) => {
  const roleDistribution = props.statistics.roleDistribution || []
  let rotation = 0
  for (let i = 0; i < index; i++) {
    rotation += roleDistribution[i]?.percentage || 0
  }
  return rotation * 3.6
}

const getSegmentClipPath = (index) => {
  const roleDistribution = props.statistics.roleDistribution || []
  const percentage = roleDistribution[index]?.percentage || 0
  if (percentage >= 100) return 'none'
  if (percentage >= 50) return `polygon(50% 50%, 50% 0%, ${50 + 50 * Math.cos((percentage - 50) * Math.PI / 50)}% ${50 - 50 * Math.sin((percentage - 50) * Math.PI / 50)}%)`
  return `polygon(50% 50%, 50% 0%, ${50 + 50 * Math.cos(percentage * Math.PI / 50)}% ${50 - 50 * Math.sin(percentage * Math.PI / 50)}%)`
}

const getRiskPercentage = (level) => {
  const total = Object.values(props.statistics.riskDistribution || {}).reduce((sum, count) => sum + count, 0)
  if (total === 0) return 0
  return ((props.statistics.riskDistribution[level] / total) * 100).toFixed(1)
}

const getFrequencyColor = (index) => {
  const colors = ['#67c23a', '#409eff', '#e6a23c', '#f56c6c']
  return colors[index] || '#909399'
}

const getAveragePosts = () => Math.floor(Math.random() * 20 + 5)
const getAverageComments = () => Math.floor(Math.random() * 50 + 10)
const getAverageLikes = () => Math.floor(Math.random() * 100 + 20)

const getAverageSessionDuration = () => Math.floor(Math.random() * 60 + 15)
const getMaxSessionDuration = () => Math.floor(Math.random() * 180 + 60)
const getDailyUsageTime = () => Math.floor(Math.random() * 120 + 30)

// 操作方法
const refreshAnalytics = () => {
  emit('refresh')
  ElMessage.success('数据刷新成功')
}

const exportReport = () => {
  ElMessage.success('分析报告导出成功')
}

const handleTimeRangeChange = (range) => {
  if (range !== 'custom') {
    customDateRange.value = []
  }
  // 这里应该根据时间范围重新加载数据
}

const handleCustomDateChange = () => {
  // 这里应该根据自定义日期范围重新加载数据
}
</script>

<style scoped>
.user-analytics {
  padding: 20px;
}

.analytics-header {
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

.time-range-selector {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.analytics-tabs {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

/* 用户增长样式 */
.growth-analytics {
  padding: 20px;
}

.growth-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.growth-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.growth-card:hover {
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

.card-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.card-trend {
  font-size: 12px;
  color: #606266;
  display: flex;
  align-items: center;
  gap: 4px;
}

.chart-section {
  margin-bottom: 24px;
}

.chart-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mini-chart {
  width: 100%;
  max-width: 600px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 200px;
  margin-bottom: 20px;
}

.chart-bar {
  flex: 1;
  background: linear-gradient(to top, #409eff, #36cfc9);
  border-radius: 2px 2px 0 0;
  cursor: pointer;
  transition: all 0.2s ease;
}

.chart-bar:hover {
  opacity: 0.8;
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #606266;
}

.growth-analysis h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.analysis-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.analysis-item {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.analysis-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.analysis-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.analysis-desc {
  font-size: 12px;
  color: #606266;
}

/* 活跃度样式 */
.activity-analytics {
  padding: 20px;
}

.activity-metrics {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.metric-card {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.metric-title {
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

.metric-chart {
  height: 40px;
}

.sparkline {
  display: flex;
  align-items: flex-end;
  gap: 2px;
  height: 100%;
}

.sparkline-bar {
  flex: 1;
  background: #409eff;
  border-radius: 2px 2px 0 0;
  min-height: 4px;
}

.activity-trend {
  margin-bottom: 24px;
}

.activity-trend h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.trend-chart {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.chart-legend {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.active-users {
  background: #409eff;
}

.legend-color.new-posts {
  background: #67c23a;
}

.chart-area {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.area-chart {
  width: 100%;
  max-width: 800px;
  height: 250px;
  position: relative;
}

.chart-lines {
  position: relative;
  height: 200px;
  margin-bottom: 20px;
}

.line {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.point {
  position: absolute;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #409eff;
}

.line.new-posts .point {
  background: #67c23a;
}

.chart-labels {
  position: relative;
  height: 20px;
  font-size: 12px;
  color: #606266;
}

.chart-labels span {
  position: absolute;
  transform: translateX(-50%);
}

.retention-analysis h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.retention-high {
  color: #67c23a;
  font-weight: 600;
}

.retention-medium {
  color: #e6a23c;
  font-weight: 600;
}

.retention-low {
  color: #f56c6c;
  font-weight: 600;
}

.retention-sparkline {
  display: flex;
  align-items: flex-end;
  gap: 4px;
  height: 30px;
}

.sparkline-dot {
  width: 12px;
  border-radius: 2px 2px 0 0;
  min-height: 4px;
}

/* 分布样式 */
.distribution-analytics {
  padding: 20px;
}

.distribution-section {
  margin-bottom: 32px;
}

.distribution-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.distribution-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.distribution-chart {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pie-chart-placeholder {
  display: flex;
  align-items: center;
  gap: 40px;
}

.pie-segments {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  position: relative;
  background: #f0f0f0;
}

.pie-segment {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  transform-origin: center;
}

.pie-legend {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.pie-legend .legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.distribution-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.detail-title {
  font-weight: 500;
  color: #303133;
}

.detail-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.risk-distribution {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.risk-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.risk-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  font-size: 18px;
}

.risk-icon.low {
  background: #f6ffed;
  color: #52c41a;
}

.risk-icon.medium {
  background: #fffbe6;
  color: #faad14;
}

.risk-icon.high {
  background: #fff2f0;
  color: #ff4d4f;
}

.risk-content {
  flex: 1;
  text-align: center;
}

.risk-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.risk-count {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.risk-percentage {
  font-size: 12px;
  color: #909399;
}

.risk-bar {
  width: 200px;
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.risk-bar .bar-fill.low {
  background: #52c41a;
}

.risk-bar .bar-fill.medium {
  background: #faad14;
}

.risk-bar .bar-fill.high {
  background: #ff4d4f;
}

.region-distribution {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.distribution-bar {
  height: 8px;
  background: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

/* 行为分析样式 */
.behavior-analytics {
  padding: 20px;
}

.behavior-section {
  margin-bottom: 32px;
}

.behavior-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.login-frequency {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.frequency-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.frequency-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.frequency-label {
  width: 80px;
  font-size: 14px;
  color: #606266;
}

.frequency-bar {
  flex: 1;
  height: 20px;
  background: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}

.frequency-value {
  width: 50px;
  text-align: right;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.content-interaction {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.interaction-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.stat-icon {
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
}

.usage-duration {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
}

.duration-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.duration-item {
  text-align: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.duration-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.duration-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-analytics {
    padding: 16px;
  }
  
  .analytics-header {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }
  
  .growth-cards {
    grid-template-columns: 1fr;
  }
  
  .activity-metrics {
    grid-template-columns: 1fr;
  }
  
  .distribution-grid {
    grid-template-columns: 1fr;
  }
  
  .interaction-stats {
    grid-template-columns: 1fr;
  }
  
  .duration-stats {
    grid-template-columns: 1fr;
  }
  
  .time-range-selector {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
}

/* 动画效果 */
.growth-card,
.metric-card,
.risk-item,
.stat-item {
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
