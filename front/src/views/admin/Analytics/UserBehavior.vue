<template>
  <div class="user-behavior">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户行为统计</h1>
        <p class="page-description">分析用户活跃度、增长趋势、设备分布等行为数据</p>
      </div>
      <div class="header-actions">
        <el-select
          v-model="timeRange"
          placeholder="选择时间范围"
          @change="handleTimeRangeChange"
        >
          <el-option label="最近7天" value="7d" />
          <el-option label="最近30天" value="30d" />
          <el-option label="最近90天" value="90d" />
          <el-option label="最近1年" value="1y" />
        </el-select>
        
        <el-button
          type="primary"
          :loading="chartLoading"
          @click="fetchUserBehavior"
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

    <!-- 统计概览卡片 -->
    <div class="stats-overview">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><User /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(getTotalActiveUsers()) }}</div>
          <div class="stat-label">总活跃用户</div>
          <div class="stat-trend positive">
            <el-icon><TrendCharts /></el-icon>
            +12.5%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><UserFilled /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(getAverageActiveUsers()) }}</div>
          <div class="stat-label">日均活跃用户</div>
          <div class="stat-trend positive">
            <el-icon><TrendCharts /></el-icon>
            +8.3%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ getAverageSessionTime() }}分钟</div>
          <div class="stat-label">平均会话时长</div>
          <div class="stat-trend negative">
            <el-icon><TrendCharts /></el-icon>
            -2.1%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Monitor /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ getPeakActiveTime() }}</div>
          <div class="stat-label">活跃高峰时段</div>
          <div class="stat-trend">稳定</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 用户活跃趋势 -->
      <div class="chart-row">
        <div class="chart-card large">
          <div class="chart-header">
            <h3>用户活跃趋势</h3>
            <div class="chart-actions">
              <el-radio-group v-model="activeTrendType" size="small">
                <el-radio-button label="daily">日活跃</el-radio-button>
                <el-radio-button label="weekly">周活跃</el-radio-button>
                <el-radio-button label="monthly">月活跃</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div
            id="activeTrendChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <!-- 用户增长趋势 -->
      <div class="chart-row">
        <div class="chart-card large">
          <div class="chart-header">
            <h3>用户增长趋势</h3>
            <div class="chart-actions">
              <el-radio-group v-model="growthTrendType" size="small">
                <el-radio-button label="new">新增用户</el-radio-button>
                <el-radio-button label="total">累计用户</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div
            id="growthTrendChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <!-- 设备分布和地区分布 -->
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3>设备类型分布</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showDeviceDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="deviceChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>用户地区分布</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showRegionDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="regionChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <!-- 用户活跃热力图 -->
      <div class="chart-row">
        <div class="chart-card large">
          <div class="chart-header">
            <h3>用户活跃热力图</h3>
            <div class="chart-legend">
              <span class="legend-item">低活跃度</span>
              <div class="legend-gradient"></div>
              <span class="legend-item">高活跃度</span>
            </div>
          </div>
          <div
            id="heatmapChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>
    </div>

    <!-- 详细数据表格 -->
    <div class="data-table-section">
      <div class="section-header">
        <h3>详细数据</h3>
        <div class="table-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户数据"
            prefix-icon="Search"
            clearable
            style="width: 200px"
          />
          <el-button
            type="primary"
            @click="exportTableData"
          >
            导出表格
          </el-button>
        </div>
      </div>

      <el-table
        :data="filteredTableData"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="activeUsers" label="活跃用户" width="120">
          <template #default="{ row }">
            {{ formatNumber(row.activeUsers) }}
          </template>
        </el-table-column>
        <el-table-column prop="newUsers" label="新增用户" width="120">
          <template #default="{ row }">
            {{ formatNumber(row.newUsers) }}
          </template>
        </el-table-column>
        <el-table-column prop="sessionDuration" label="平均会话时长" width="140">
          <template #default="{ row }">
            {{ row.sessionDuration }}分钟
          </template>
        </el-table-column>
        <el-table-column prop="pageViews" label="页面浏览量" width="120">
          <template #default="{ row }">
            {{ formatNumber(row.pageViews) }}
          </template>
        </el-table-column>
        <el-table-column prop="bounceRate" label="跳出率" width="100">
          <template #default="{ row }">
            {{ row.bounceRate }}%
          </template>
        </el-table-column>
        <el-table-column label="趋势" width="80">
          <template #default="{ row }">
            <el-icon
              v-if="row.trend === 'up'"
              class="trend-icon up"
            >
              <TrendCharts />
            </el-icon>
            <el-icon
              v-else-if="row.trend === 'down'"
              class="trend-icon down"
            >
              <TrendCharts />
            </el-icon>
            <span v-else class="trend-icon stable">—</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalTableData"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageSizeChange"
          @current-change="handleCurrentPageChange"
        />
      </div>
    </div>

    <!-- 设备详情对话框 -->
    <el-dialog
      v-model="deviceDetailVisible"
      title="设备类型详情"
      width="600px"
    >
      <div class="device-detail">
        <div
          v-for="device in userBehavior.userDeviceStats"
          :key="device.name"
          class="device-item"
        >
          <div class="device-info">
            <div class="device-name">{{ device.name }}</div>
            <div class="device-stats">
              <span>{{ formatNumber(device.value) }} 用户</span>
              <span>{{ device.percentage }}%</span>
            </div>
          </div>
          <el-progress
            :percentage="device.percentage"
            :stroke-width="8"
          />
        </div>
      </div>
    </el-dialog>

    <!-- 地区详情对话框 -->
    <el-dialog
      v-model="regionDetailVisible"
      title="用户地区分布详情"
      width="600px"
    >
      <div class="region-detail">
        <div
          v-for="region in userBehavior.userRegionStats"
          :key="region.name"
          class="region-item"
        >
          <div class="region-name">{{ region.name }}</div>
          <div class="region-users">{{ formatNumber(region.value) }} 用户</div>
          <div class="region-percentage">
            {{ ((region.value / getTotalActiveUsers()) * 100).toFixed(1) }}%
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import {
  User,
  UserFilled,
  Clock,
  Monitor,
  Refresh,
  TrendCharts,
  Download
} from '@element-plus/icons-vue'
import { useAnalytics } from '@/composables/useAnalytics'

// 使用数据分析组合式函数
const {
  loading,
  chartLoading,
  timeRange,
  userBehavior,
  fetchUserBehavior,
  createChart,
  destroyAllCharts,
  handleTimeRangeChange
} = useAnalytics()

// 响应式数据
const activeTrendType = ref('daily')
const growthTrendType = ref('new')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const deviceDetailVisible = ref(false)
const regionDetailVisible = ref(false)

// 表格数据
const tableData = ref([])
const totalTableData = ref(0)

// 计算属性
const filteredTableData = computed(() => {
  if (!searchKeyword.value) {
    return tableData.value
  }
  return tableData.value.filter(item =>
    item.date.includes(searchKeyword.value)
  )
})

// 获取总活跃用户数
const getTotalActiveUsers = () => {
  if (!userBehavior.value.dailyActiveUsers.length) return 0
  return userBehavior.value.dailyActiveUsers.reduce((sum, item) => sum + item.value, 0)
}

// 获取平均活跃用户数
const getAverageActiveUsers = () => {
  if (!userBehavior.value.dailyActiveUsers.length) return 0
  const total = getTotalActiveUsers()
  return Math.round(total / userBehavior.value.dailyActiveUsers.length)
}

// 获取平均会话时长
const getAverageSessionTime = () => {
  return 23 // 模拟数据
}

// 获取活跃高峰时段
const getPeakActiveTime = () => {
  return '20:00-22:00'
}

// 生成模拟表格数据
const generateTableData = () => {
  const data = []
  const days = timeRange.value === '7d' ? 7 : timeRange.value === '30d' ? 30 : 90
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    
    data.push({
      date: date.toISOString().split('T')[0],
      activeUsers: Math.floor(Math.random() * 1000) + 800,
      newUsers: Math.floor(Math.random() * 100) + 50,
      sessionDuration: Math.floor(Math.random() * 30) + 15,
      pageViews: Math.floor(Math.random() * 5000) + 3000,
      bounceRate: Math.floor(Math.random() * 30) + 20,
      trend: Math.random() > 0.5 ? 'up' : Math.random() > 0.5 ? 'down' : 'stable'
    })
  }
  
  return data
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  // 用户活跃趋势图表
  const activeTrendOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: userBehavior.value.dailyActiveUsers.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '活跃用户',
      type: 'line',
      data: userBehavior.value.dailyActiveUsers.map(item => item.value),
      smooth: true,
      itemStyle: {
        color: '#409eff'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0, color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1, color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      }
    }]
  }
  
  // 用户增长趋势图表
  const growthTrendOption = {
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
      data: userBehavior.value.userGrowthTrend.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '新增用户',
      type: 'bar',
      data: userBehavior.value.userGrowthTrend.map(item => item.value),
      itemStyle: {
        color: '#67c23a'
      }
    }]
  }
  
  // 设备分布图表
  const deviceOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '设备类型',
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
      data: userBehavior.value.userDeviceStats.map(item => ({
        name: item.name,
        value: item.value
      }))
    }]
  }
  
  // 地区分布图表
  const regionOption = {
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
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: userBehavior.value.userRegionStats.map(item => item.name)
    },
    series: [{
      name: '用户数',
      type: 'bar',
      data: userBehavior.value.userRegionStats.map(item => item.value),
      itemStyle: {
        color: '#e6a23c'
      }
    }]
  }
  
  // 用户活跃热力图
  const heatmapOption = {
    title: {
      show: false
    },
    tooltip: {
      position: 'top'
    },
    grid: {
      height: '50%',
      top: '10%'
    },
    xAxis: {
      type: 'category',
      data: Array.from({ length: 24 }, (_, i) => `${i}:00`),
      splitArea: {
        show: true
      }
    },
    yAxis: {
      type: 'category',
      data: ['周日', '周六', '周五', '周四', '周三', '周二', '周一'],
      splitArea: {
        show: true
      }
    },
    visualMap: {
      min: 0,
      max: 100,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: '15%'
    },
    series: [{
      name: '活跃度',
      type: 'heatmap',
      data: userBehavior.value.userActivityHeatmap,
      label: {
        show: true
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  createChart('activeTrendChart', activeTrendOption)
  createChart('growthTrendChart', growthTrendOption)
  createChart('deviceChart', deviceOption)
  createChart('regionChart', regionOption)
  createChart('heatmapChart', heatmapOption)
}

// 监听数据变化，重新初始化图表
watch(
  () => userBehavior.value,
  () => {
    initCharts()
  },
  { deep: true }
)

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toLocaleString()
}

// 显示设备详情
const showDeviceDetail = () => {
  deviceDetailVisible.value = true
}

// 显示地区详情
const showRegionDetail = () => {
  regionDetailVisible.value = true
}

// 导出数据
const exportData = () => {
  // 模拟导出功能
  console.log('导出用户行为数据')
}

// 导出表格数据
const exportTableData = () => {
  // 模拟导出功能
  console.log('导出表格数据')
}

// 处理分页
const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentPageChange = (page) => {
  currentPage.value = page
}

// 组件挂载
onMounted(async () => {
  await fetchUserBehavior()
  tableData.value = generateTableData()
  totalTableData.value = tableData.value.length
  initCharts()
})
</script>

<style scoped>
.user-behavior {
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

.stats-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
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
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
}

.stat-trend.positive {
  color: #67c23a;
}

.stat-trend.negative {
  color: #f56c6c;
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

.chart-legend {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #606266;
}

.legend-gradient {
  width: 100px;
  height: 12px;
  background: linear-gradient(90deg, #e6f7ff 0%, #1890ff 100%);
  border-radius: 2px;
}

.chart-container {
  height: 400px;
  padding: 20px;
}

.data-table-section {
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

.table-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.trend-icon {
  font-size: 16px;
}

.trend-icon.up {
  color: #67c23a;
  transform: rotate(0deg);
}

.trend-icon.down {
  color: #f56c6c;
  transform: rotate(180deg);
}

.trend-icon.stable {
  color: #909399;
}

.table-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.device-detail,
.region-detail {
  padding: 20px 0;
}

.device-item {
  margin-bottom: 24px;
}

.device-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.device-name {
  font-weight: 500;
  color: #303133;
}

.device-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #606266;
}

.region-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.region-item:last-child {
  border-bottom: none;
}

.region-name {
  font-weight: 500;
  color: #303133;
}

.region-users {
  color: #606266;
}

.region-percentage {
  color: #409eff;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-behavior {
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
  
  .stats-overview {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .chart-container {
    height: 300px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .table-actions {
    width: 100%;
    justify-content: space-between;
  }
}

/* 动画效果 */
.stat-card,
.chart-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

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
