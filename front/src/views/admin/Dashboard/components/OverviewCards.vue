<template>
  <div class="overview-cards">
    <el-row :gutter="20">
      <el-col :span="6" v-for="(card, index) in cards" :key="index">
        <el-card class="stat-card" shadow="hover" :class="card.status">
          <div class="stat-content">
            <div class="stat-icon" :style="{ background: card.gradient }">
              <el-icon size="32">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">
                <el-skeleton v-if="loading" animated>
                  <template #template>
                    <el-skeleton-item variant="text" style="width: 80px; height: 32px;" />
                  </template>
                </el-skeleton>
                <span v-else>{{ formatNumber(card.value) }}</span>
              </div>
              <div class="stat-label">{{ card.label }}</div>
              <div class="stat-trend" :class="card.trendType">
                <el-icon v-if="card.trend">
                  <component :is="card.trendIcon" />
                </el-icon>
                <span v-if="card.trend">{{ card.trend }}</span>
                <span v-else class="status-text">{{ card.statusText }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  User,
  Document,
  Calendar,
  Monitor,
  TrendCharts,
  ArrowUp,
  ArrowDown,
  Warning,
  CircleCheck
} from '@element-plus/icons-vue'

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

// 计算卡片数据
const cards = computed(() => [
  {
    label: '总用户数',
    value: props.statistics.totalUsers || 0,
    trend: `+${props.statistics.userGrowthRate || 0}%`,
    trendType: 'positive',
    trendIcon: ArrowUp,
    icon: User,
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    status: 'normal'
  },
  {
    label: '活跃用户',
    value: props.statistics.activeUsers || 0,
    trend: `${formatPercentage((props.statistics.activeUsers / props.statistics.totalUsers) * 100) || '0%'}`,
    trendType: 'positive',
    trendIcon: TrendCharts,
    icon: User,
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    status: 'normal'
  },
  {
    label: '总帖子数',
    value: props.statistics.totalPosts || 0,
    trend: `+${props.statistics.postGrowthRate || 0}%`,
    trendType: 'positive',
    trendIcon: ArrowUp,
    icon: Document,
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    status: 'normal'
  },
  {
    label: '待审核',
    value: props.statistics.pendingReviews || 0,
    trend: props.statistics.pendingReviews > 50 ? '需关注' : '正常',
    trendType: props.statistics.pendingReviews > 50 ? 'warning' : 'positive',
    trendIcon: props.statistics.pendingReviews > 50 ? Warning : CircleCheck,
    icon: Calendar,
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
    status: props.statistics.pendingReviews > 50 ? 'warning' : 'normal'
  },
  {
    label: '今日注册',
    value: props.statistics.todayRegistrations || 0,
    trend: '较昨日',
    trendType: 'neutral',
    trendIcon: TrendCharts,
    icon: User,
    gradient: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
    status: 'normal'
  },
  {
    label: '今日发帖',
    value: props.statistics.todayPosts || 0,
    trend: '较昨日',
    trendType: 'neutral',
    trendIcon: TrendCharts,
    icon: Document,
    gradient: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
    status: 'normal'
  },
  {
    label: '在线用户',
    value: props.statistics.onlineUsers || 0,
    trend: '实时',
    trendType: 'positive',
    trendIcon: CircleCheck,
    icon: User,
    gradient: 'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
    status: 'normal'
  },
  {
    label: '系统负载',
    value: `${formatPercentage(props.statistics.systemLoad) || '0%'}`,
    trend: props.statistics.systemLoad > 80 ? '高负载' : props.statistics.systemLoad > 60 ? '中等' : '正常',
    trendType: props.statistics.systemLoad > 80 ? 'negative' : props.statistics.systemLoad > 60 ? 'warning' : 'positive',
    trendIcon: props.statistics.systemLoad > 80 ? Warning : Monitor,
    icon: Monitor,
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
    status: props.statistics.systemLoad > 80 ? 'error' : props.statistics.systemLoad > 60 ? 'warning' : 'normal'
  }
])
</script>

<style scoped>
.overview-cards {
  padding: 20px;
}

.stat-card {
  height: 120px;
  transition: all 0.3s ease;
  border: none;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.stat-card.warning {
  border-left: 4px solid #e6a23c;
}

.stat-card.error {
  border-left: 4px solid #f56c6c;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: white;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-number {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-trend {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.stat-trend.positive {
  color: #67c23a;
}

.stat-trend.negative {
  color: #f56c6c;
}

.stat-trend.warning {
  color: #e6a23c;
}

.stat-trend.neutral {
  color: #909399;
}

.status-text {
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .overview-cards .el-col {
    margin-bottom: 20px;
  }
  
  .stat-card {
    height: 100px;
  }
  
  .stat-icon {
    width: 50px;
    height: 50px;
    margin-right: 12px;
  }
  
  .stat-number {
    font-size: 24px;
  }
  
  .stat-label {
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .overview-cards {
    padding: 16px;
  }
  
  .overview-cards .el-col {
    margin-bottom: 16px;
  }
  
  .stat-card {
    height: 90px;
  }
  
  .stat-icon {
    width: 45px;
    height: 45px;
    margin-right: 10px;
  }
  
  .stat-number {
    font-size: 20px;
  }
  
  .stat-label {
    font-size: 12px;
  }
  
  .stat-trend {
    font-size: 11px;
  }
}

/* 加载状态 */
.stat-card.loading {
  opacity: 0.7;
}

/* 动画效果 */
.stat-card {
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

/* 骨架屏样式 */
.el-skeleton {
  display: inline-block;
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  .stat-number {
    color: #e4e7ed;
  }
  
  .stat-label {
    color: #909399;
  }
}
</style>
