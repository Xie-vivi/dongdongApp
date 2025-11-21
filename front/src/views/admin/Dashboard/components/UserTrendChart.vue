<template>
  <div class="user-trend-chart">
    <el-card class="chart-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <h3>用户增长趋势</h3>
            <span class="subtitle">用户注册和活跃度统计</span>
          </div>
          <div class="header-right">
            <el-button-group size="small">
              <el-button 
                :type="days === 7 ? 'primary' : ''"
                @click="handleDaysChange(7)"
              >
                7天
              </el-button>
              <el-button 
                :type="days === 30 ? 'primary' : ''"
                @click="handleDaysChange(30)"
              >
                30天
              </el-button>
              <el-button 
                :type="days === 90 ? 'primary' : ''"
                @click="handleDaysChange(90)"
              >
                90天
              </el-button>
            </el-button-group>
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
      
      <!-- 图表统计信息 -->
      <div class="chart-stats" v-if="!loading && chartData.dates">
        <div class="stat-item">
          <span class="label">平均日增用户：</span>
          <span class="value">{{ averageNewUsers }}</span>
        </div>
        <div class="stat-item">
          <span class="label">平均活跃用户：</span>
          <span class="value">{{ averageActiveUsers }}</span>
        </div>
        <div class="stat-item">
          <span class="label">增长率：</span>
          <span class="value growth-rate" :class="growthRateType">
            {{ growthRate }}
          </span>
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
    default: () => ({})
  },
  loading: {
    type: Boolean,
    default: false
  },
  days: {
    type: Number,
    default: 30
  }
})

const emit = defineEmits(['days-change'])

// 图表引用
const chartRef = ref(null)
let chart = null

// 格式化数字
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toLocaleString()
}

// 计算统计数据
const averageNewUsers = computed(() => {
  if (!props.chartData.newUsers?.length) return '0'
  const sum = props.chartData.newUsers.reduce((a, b) => a + b, 0)
  return formatNumber(Math.round(sum / props.chartData.newUsers.length))
})

const averageActiveUsers = computed(() => {
  if (!props.chartData.activeUsers?.length) return '0'
  const sum = props.chartData.activeUsers.reduce((a, b) => a + b, 0)
  return formatNumber(Math.round(sum / props.chartData.activeUsers.length))
})

const growthRate = computed(() => {
  if (!props.chartData.totalUsers?.length) return '0%'
  const firstValue = props.chartData.totalUsers[0]
  const lastValue = props.chartData.totalUsers[props.chartData.totalUsers.length - 1]
  if (!firstValue || !lastValue) return '0%'
  const rate = ((lastValue - firstValue) / firstValue * 100).toFixed(1)
  return `${rate > 0 ? '+' : ''}${rate}%`
})

const growthRateType = computed(() => {
  const rate = parseFloat(growthRate.value)
  if (rate > 0) return 'positive'
  if (rate < 0) return 'negative'
  return 'neutral'
})

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  chart = echarts.init(chartRef.value)
  updateChart()
  
  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
}

// 更新图表
const updateChart = () => {
  if (!chart || !props.chartData.dates) return
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      formatter: (params) => {
        let html = `<div style="margin-bottom: 8px; font-weight: 600;">${params[0].axisValue}</div>`
        params.forEach(param => {
          html += `
            <div style="display: flex; align-items: center; margin-bottom: 4px;">
              <span style="display: inline-block; width: 10px; height: 10px; background: ${param.color}; border-radius: 50%; margin-right: 8px;"></span>
              <span style="flex: 1;">${param.seriesName}：</span>
              <span style="font-weight: 600;">${formatNumber(param.value)}</span>
            </div>
          `
        })
        return html
      }
    },
    legend: {
      data: ['新增用户', '活跃用户', '总用户数'],
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
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.chartData.dates,
      axisLine: {
        lineStyle: {
          color: '#e8e8e8'
        }
      },
      axisLabel: {
        fontSize: 11,
        color: '#666',
        rotate: props.days > 30 ? 45 : 0
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '用户数',
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
        name: '新增用户',
        type: 'bar',
        data: props.chartData.newUsers,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ]),
          borderRadius: [4, 4, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' }
            ])
          }
        }
      },
      {
        name: '活跃用户',
        type: 'line',
        data: props.chartData.activeUsers,
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
        name: '总用户数',
        type: 'line',
        data: props.chartData.totalUsers,
        smooth: true,
        symbol: 'diamond',
        symbolSize: 6,
        lineStyle: {
          width: 2,
          color: '#e6a23c',
          type: 'dashed'
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

// 处理窗口大小变化
const handleResize = () => {
  chart?.resize()
}

// 处理天数变化
const handleDaysChange = (days) => {
  emit('days-change', days)
}

// 刷新图表
const refreshChart = () => {
  updateChart()
  ElMessage.success('图表已刷新')
}

// 监听数据变化
watch(() => props.chartData, () => {
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
.user-trend-chart {
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

.growth-rate.positive {
  color: #67c23a;
}

.growth-rate.negative {
  color: #f56c6c;
}

.growth-rate.neutral {
  color: #909399;
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
.user-trend-chart {
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
