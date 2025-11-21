<template>
  <div class="content-stats-chart">
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>内容统计分析</h3>
            <span class="subtitle">内容分布和月度趋势</span>
          </div>
          <div class="header-right">
            <el-tabs v-model="activeTab" size="small" @tab-change="handleTabChange">
              <el-tab-pane label="分类分布" name="category"></el-tab-pane>
              <el-tab-pane label="月度趋势" name="monthly"></el-tab-pane>
            </el-tabs>
            <el-button size="small" @click="refreshChart">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>
      
      <div class="chart-container" ref="chartRef">
        <el-skeleton v-if="loading" animated>
          <template #template>
            <el-skeleton-item variant="rect" style="width: 100%; height: 320px;" />
          </template>
        </el-skeleton>
      </div>
      
      <!-- 统计信息 -->
      <div class="chart-stats" v-if="!loading && activeTab === 'category'">
        <div class="stat-item">
          <span class="label">总内容数：</span>
          <span class="value">{{ totalContent }}</span>
        </div>
        <div class="stat-item">
          <span class="label">最热门分类：</span>
          <span class="value">{{ topCategory }}</span>
        </div>
        <div class="stat-item">
          <span class="label">分类数量：</span>
          <span class="value">{{ categoryCount }}</span>
        </div>
      </div>
      
      <div class="chart-stats" v-if="!loading && activeTab === 'monthly'">
        <div class="stat-item">
          <span class="label">平均月发帖：</span>
          <span class="value">{{ averageMonthlyPosts }}</span>
        </div>
        <div class="stat-item">
          <span class="label">平均月评论：</span>
          <span class="value">{{ averageMonthlyComments }}</span>
        </div>
        <div class="stat-item">
          <span class="label">平均月点赞：</span>
          <span class="value">{{ averageMonthlyLikes }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const props = defineProps({
  chartData: {
    type: Object,
    default: () => ({ categories: [], counts: [], percentages: [] })
  },
  monthlyData: {
    type: Object,
    default: () => ({ months: [], posts: [], comments: [], likes: [] })
  },
  loading: {
    type: Boolean,
    default: false
  }
})

// 当前激活的标签页
const activeTab = ref('category')

// 图表引用
const chartRef = ref(null)
let chart = null

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

// 计算统计数据
const totalContent = computed(() => {
  if (!props.chartData.counts?.length) return '0'
  return formatNumber(props.chartData.counts.reduce((a, b) => a + b, 0))
})

const topCategory = computed(() => {
  if (!props.chartData.categories?.length || !props.chartData.counts?.length) return '无'
  const maxIndex = props.chartData.counts.indexOf(Math.max(...props.chartData.counts))
  return props.chartData.categories[maxIndex] || '无'
})

const categoryCount = computed(() => {
  return props.chartData.categories?.length || 0
})

const averageMonthlyPosts = computed(() => {
  if (!props.monthlyData.posts?.length) return '0'
  const sum = props.monthlyData.posts.reduce((a, b) => a + b, 0)
  return formatNumber(Math.round(sum / props.monthlyData.posts.length))
})

const averageMonthlyComments = computed(() => {
  if (!props.monthlyData.comments?.length) return '0'
  const sum = props.monthlyData.comments.reduce((a, b) => a + b, 0)
  return formatNumber(Math.round(sum / props.monthlyData.comments.length))
})

const averageMonthlyLikes = computed(() => {
  if (!props.monthlyData.likes?.length) return '0'
  const sum = props.monthlyData.likes.reduce((a, b) => a + b, 0)
  return formatNumber(Math.round(sum / props.monthlyData.likes.length))
})

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chart = echarts.init(chartRef.value)
  updateChart()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 更新分类分布图表
const updateCategoryChart = () => {
  if (!chart || !props.chartData.categories?.length) return
  
  const data = props.chartData.categories.map((category, index) => ({
    name: category,
    value: props.chartData.counts[index] || 0,
    percentage: props.chartData.percentages[index] || 0
  }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        return `
          <div style="text-align: center;">
            <div style="font-weight: 600; margin-bottom: 8px;">${params.name}</div>
            <div>数量：${formatNumber(params.value)}</div>
            <div>占比：${params.data.percentage}%</div>
          </div>
        `
      }
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center',
      textStyle: {
        fontSize: 12,
        color: '#666'
      },
      formatter: (name) => {
        const index = props.chartData.categories.indexOf(name)
        const percentage = props.chartData.percentages[index] || 0
        return `${name} (${percentage}%)`
      }
    },
    series: [
      {
        name: '内容分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold',
            formatter: (params) => {
              return `${params.name}\n${params.data.percentage}%`
            }
          },
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ],
    color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4']
  }
  
  chart.setOption(option)
}

// 更新月度趋势图表
const updateMonthlyChart = () => {
  if (!chart || !props.monthlyData.months?.length) return
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['帖子数', '评论数', '点赞数'],
      top: 10,
      right: 20,
      textStyle: {
        fontSize: 12,
        color: '#666'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '8%',
      top: '15%',
      containLabel: true
    },
    xAxis: [
      {
        type: 'category',
        data: props.monthlyData.months,
        axisPointer: {
          type: 'shadow'
        },
        axisLine: {
          lineStyle: {
            color: '#e8e8e8'
          }
        },
        axisLabel: {
          fontSize: 11,
          color: '#666'
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left',
        axisLine: {
          lineStyle: {
            color: '#e8e8e8'
          }
        },
        axisLabel: {
          fontSize: 11,
          color: '#666',
          formatter: (value) => {
            if (value >= 10000) {
              return `${value / 10000}万`
            }
            return value
          }
        },
        splitLine: {
          lineStyle: {
            color: '#f5f5f5',
            type: 'dashed'
          }
        }
      }
    ],
    series: [
      {
        name: '帖子数',
        type: 'bar',
        data: props.monthlyData.posts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ]),
          borderRadius: [4, 4, 0, 0]
        }
      },
      {
        name: '评论数',
        type: 'line',
        data: props.monthlyData.comments,
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: {
          width: 3,
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#67c23a' },
            { offset: 1, color: '#85ce61' }
          ])
        },
        itemStyle: {
          color: '#67c23a',
          borderColor: '#fff',
          borderWidth: 2
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
            { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
          ])
        }
      },
      {
        name: '点赞数',
        type: 'line',
        data: props.monthlyData.likes,
        smooth: true,
        symbol: 'diamond',
        symbolSize: 6,
        lineStyle: {
          width: 2,
          color: '#e6a23c'
        },
        itemStyle: {
          color: '#e6a23c',
          borderColor: '#fff',
          borderWidth: 2
        }
      }
    ]
  }
  
  chart.setOption(option)
}

// 更新图表
const updateChart = () => {
  if (activeTab.value === 'category') {
    updateCategoryChart()
  } else {
    updateMonthlyChart()
  }
}

// 处理窗口大小变化
const handleResize = () => {
  chart?.resize()
}

// 处理标签页变化
const handleTabChange = (tabName) => {
  activeTab.value = tabName
  nextTick(() => {
    updateChart()
  })
}

// 刷新图表
const refreshChart = () => {
  updateChart()
  ElMessage.success('图表已刷新')
}

// 监听数据变化
watch([() => props.chartData, () => props.monthlyData], () => {
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
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
})
</script>

<style scoped>
.content-stats-chart {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-card :deep(.el-card__body) {
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

.chart-container {
  flex: 1;
  min-height: 320px;
  position: relative;
}

.chart-stats {
  display: flex;
  justify-content: space-around;
  padding: 16px 0 0 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat-item .label {
  font-size: 12px;
  color: #909399;
}

.stat-item .value {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .chart-stats {
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .stat-item {
    flex: 1;
    min-width: 100px;
  }
}

@media (max-width: 768px) {
  .chart-card :deep(.el-card__body) {
    padding: 16px;
  }
  
  .chart-container {
    min-height: 280px;
  }
  
  .chart-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .stat-item {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
  
  .stat-item .label {
    font-size: 11px;
  }
  
  .stat-item .value {
    font-size: 14px;
  }
}

/* 加载状态 */
.chart-card.loading {
  opacity: 0.7;
}

/* 动画效果 */
.content-stats-chart {
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
  
  .subtitle {
    color: #909399;
  }
  
  .stat-item .value {
    color: #e4e7ed;
  }
}
</style>
