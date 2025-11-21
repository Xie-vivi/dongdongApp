<template>
  <div class="content-analysis">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">内容数据分析</h1>
        <p class="page-description">分析内容发布趋势、类型分布、用户参与度等数据</p>
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
          @click="fetchContentAnalysis"
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
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(getTotalContent()) }}</div>
          <div class="stat-label">内容总数</div>
          <div class="stat-trend positive">
            <el-icon><TrendCharts /></el-icon>
            +8.3%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(getAverageEngagement()) }}</div>
          <div class="stat-label">平均互动量</div>
          <div class="stat-trend positive">
            <el-icon><TrendCharts /></el-icon>
            +12.7%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><View /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(getTotalViews()) }}</div>
          <div class="stat-label">总浏览量</div>
          <div class="stat-trend positive">
            <el-icon><TrendCharts /></el-icon>
            +15.2%
          </div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Star /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ getEngagementRate() }}%</div>
          <div class="stat-label">参与度</div>
          <div class="stat-trend positive">
            <el-icon><TrendCharts /></el-icon>
            +3.4%
          </div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 内容发布趋势 -->
      <div class="chart-row">
        <div class="chart-card large">
          <div class="chart-header">
            <h3>内容发布趋势</h3>
            <div class="chart-actions">
              <el-radio-group v-model="contentTrendType" size="small">
                <el-radio-button label="all">全部内容</el-radio-button>
                <el-radio-button label="posts">帖子</el-radio-button>
                <el-radio-button label="activities">活动</el-radio-button>
                <el-radio-button label="comments">评论</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div
            id="contentTrendChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <!-- 内容类型分布和用户参与度 -->
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3>内容类型分布</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showContentTypeDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="contentTypeChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>用户参与度趋势</h3>
            <div class="chart-actions">
              <el-radio-group v-model="engagementType" size="small">
                <el-radio-button label="likes">点赞</el-radio-button>
                <el-radio-button label="comments">评论</el-radio-button>
                <el-radio-button label="shares">分享</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div
            id="engagementChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <!-- 热门内容排行 -->
      <div class="chart-row">
        <div class="chart-card large">
          <div class="chart-header">
            <h3>热门内容排行</h3>
            <div class="chart-actions">
              <el-radio-group v-model="popularType" size="small">
                <el-radio-button label="views">浏览量</el-radio-button>
                <el-radio-button label="likes">点赞数</el-radio-button>
                <el-radio-button label="comments">评论数</el-radio-button>
              </el-radio-group>
            </div>
          </div>
          <div
            id="popularContentChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>

      <!-- 内容质量分析 -->
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3>内容质量分布</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showQualityDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="qualityChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>发布时段分析</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="showTimeDetail"
            >
              查看详情
            </el-button>
          </div>
          <div
            id="publishTimeChart"
            class="chart-container"
            v-loading="chartLoading"
          />
        </div>
      </div>
    </div>

    <!-- 热门内容列表 -->
    <div class="popular-content-section">
      <div class="section-header">
        <h3>热门内容详情</h3>
        <div class="content-actions">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索内容"
            prefix-icon="Search"
            clearable
            style="width: 200px"
          />
          <el-button
            type="primary"
            @click="exportContentData"
          >
            导出列表
          </el-button>
        </div>
      </div>

      <el-table
        :data="filteredPopularContent"
        stripe
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column label="排名" width="60">
          <template #default="{ $index }">
            <div class="rank-badge" :class="getRankClass($index + 1)">
              {{ $index + 1 }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="content-title">
              <el-link
                type="primary"
                :href="row.url"
                target="_blank"
                :underline="false"
              >
                {{ row.title }}
              </el-link>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getContentTypeTagType(row.type)" size="small">
              {{ getContentTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="views" label="浏览量" width="100">
          <template #default="{ row }">
            {{ formatNumber(row.views) }}
          </template>
        </el-table-column>
        <el-table-column prop="likes" label="点赞数" width="100">
          <template #default="{ row }">
            {{ formatNumber(row.likes) }}
          </template>
        </el-table-column>
        <el-table-column prop="comments" label="评论数" width="100">
          <template #default="{ row }">
            {{ formatNumber(row.comments) }}
          </template>
        </el-table-column>
        <el-table-column prop="engagement" label="参与度" width="100">
          <template #default="{ row }">
            {{ row.engagement }}%
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="120">
          <template #default="{ row }">
            {{ formatDate(row.publishedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              size="small"
              @click="viewContentDetail(row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalContent"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handlePageSizeChange"
          @current-change="handleCurrentPageChange"
        />
      </div>
    </div>

    <!-- 内容类型详情对话框 -->
    <el-dialog
      v-model="contentTypeDetailVisible"
      title="内容类型详情"
      width="600px"
    >
      <div class="content-type-detail">
        <div
          v-for="type in contentAnalysis.contentTypeStats"
          :key="type.name"
          class="type-item"
        >
          <div class="type-info">
            <div class="type-name">{{ type.name }}</div>
            <div class="type-stats">
              <span>{{ formatNumber(type.value) }} 个内容</span>
              <span>{{ type.percentage }}%</span>
            </div>
          </div>
          <el-progress
            :percentage="type.percentage"
            :stroke-width="8"
          />
        </div>
      </div>
    </el-dialog>

    <!-- 内容质量详情对话框 -->
    <el-dialog
      v-model="qualityDetailVisible"
      title="内容质量分析"
      width="600px"
    >
      <div class="quality-detail">
        <div class="quality-stats">
          <div class="quality-item">
            <div class="quality-label">高质量内容</div>
            <div class="quality-value">1,234 (25.6%)</div>
          </div>
          <div class="quality-item">
            <div class="quality-label">中等质量内容</div>
            <div class="quality-value">2,567 (53.3%)</div>
          </div>
          <div class="quality-item">
            <div class="quality-label">低质量内容</div>
            <div class="quality-value">1,012 (21.1%)</div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 发布时段详情对话框 -->
    <el-dialog
      v-model="timeDetailVisible"
      title="发布时段分析"
      width="600px"
    >
      <div class="time-detail">
        <div class="time-stats">
          <div class="time-item">
            <div class="time-label">早晨 (6:00-12:00)</div>
            <div class="time-value">23.4%</div>
          </div>
          <div class="time-item">
            <div class="time-label">下午 (12:00-18:00)</div>
            <div class="time-value">45.6%</div>
          </div>
          <div class="time-item">
            <div class="time-label">晚上 (18:00-24:00)</div>
            <div class="time-value">31.0%</div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, watch } from 'vue'
import {
  Document,
  ChatDotRound,
  View,
  Star,
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
  contentAnalysis,
  fetchContentAnalysis,
  createChart,
  destroyAllCharts,
  handleTimeRangeChange
} = useAnalytics()

// 响应式数据
const contentTrendType = ref('all')
const engagementType = ref('likes')
const popularType = ref('views')
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(20)
const contentTypeDetailVisible = ref(false)
const qualityDetailVisible = ref(false)
const timeDetailVisible = ref(false)

// 热门内容数据
const popularContent = ref([])
const totalContent = ref(0)

// 计算属性
const filteredPopularContent = computed(() => {
  if (!searchKeyword.value) {
    return popularContent.value
  }
  return popularContent.value.filter(item =>
    item.title.toLowerCase().includes(searchKeyword.value.toLowerCase())
  )
})

// 获取总内容数
const getTotalContent = () => {
  if (!contentAnalysis.value.contentTypeStats.length) return 0
  return contentAnalysis.value.contentTypeStats.reduce((sum, item) => sum + item.value, 0)
}

// 获取平均互动量
const getAverageEngagement = () => {
  return 456 // 模拟数据
}

// 获取总浏览量
const getTotalViews = () => {
  return 1234567 // 模拟数据
}

// 获取参与度
const getEngagementRate = () => {
  return 12.5 // 模拟数据
}

// 生成模拟热门内容数据
const generatePopularContent = () => {
  const data = [
    {
      id: 1,
      title: '热门帖子1：如何提高工作效率',
      type: 'posts',
      author: '张三',
      views: 12345,
      likes: 892,
      comments: 234,
      engagement: 8.7,
      publishedAt: '2025-11-15T10:30:00Z',
      url: '#'
    },
    {
      id: 2,
      title: '热门活动1：技术分享会',
      type: 'activities',
      author: '李四',
      views: 9876,
      likes: 567,
      comments: 123,
      engagement: 7.1,
      publishedAt: '2025-11-14T14:20:00Z',
      url: '#'
    },
    {
      id: 3,
      title: '热门帖子2：前端开发最佳实践',
      type: 'posts',
      author: '王五',
      views: 8765,
      likes: 445,
      comments: 98,
      engagement: 6.3,
      publishedAt: '2025-11-13T09:15:00Z',
      url: '#'
    },
    {
      id: 4,
      title: '热门评论1：关于用户体验的思考',
      type: 'comments',
      author: '赵六',
      views: 6543,
      likes: 321,
      comments: 76,
      engagement: 6.1,
      publishedAt: '2025-11-12T16:45:00Z',
      url: '#'
    },
    {
      id: 5,
      title: '热门活动2：产品设计工作坊',
      type: 'activities',
      author: '钱七',
      views: 5432,
      likes: 289,
      comments: 65,
      engagement: 6.5,
      publishedAt: '2025-11-11T11:30:00Z',
      url: '#'
    }
  ]
  
  return data
}

// 初始化图表
const initCharts = async () => {
  await nextTick()
  
  // 内容发布趋势图表
  const contentTrendOption = {
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
      data: ['帖子', '活动', '评论']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: contentAnalysis.value.contentTrend.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '帖子',
        type: 'line',
        data: generateMockTrendData('posts'),
        smooth: true,
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '活动',
        type: 'line',
        data: generateMockTrendData('activities'),
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        }
      },
      {
        name: '评论',
        type: 'line',
        data: generateMockTrendData('comments'),
        smooth: true,
        itemStyle: {
          color: '#e6a23c'
        }
      }
    ]
  }
  
  // 内容类型分布图表
  const contentTypeOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [{
      name: '内容类型',
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
      data: contentAnalysis.value.contentTypeStats.map(item => ({
        name: item.name,
        value: item.value
      }))
    }]
  }
  
  // 用户参与度趋势图表
  const engagementOption = {
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
      data: contentAnalysis.value.contentEngagement.map(item => item.date)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '参与度',
      type: 'bar',
      data: contentAnalysis.value.contentEngagement.map(item => item.value),
      itemStyle: {
        color: '#f56c6c'
      }
    }]
  }
  
  // 热门内容排行图表
  const popularContentOption = {
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
      data: contentAnalysis.value.popularContent.map(item => item.title)
    },
    series: [{
      name: '浏览量',
      type: 'bar',
      data: contentAnalysis.value.popularContent.map(item => item.views),
      itemStyle: {
        color: '#909399'
      }
    }]
  }
  
  // 内容质量分布图表
  const qualityOption = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'item'
    },
    series: [{
      name: '内容质量',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1234, name: '高质量' },
        { value: 2567, name: '中等质量' },
        { value: 1012, name: '低质量' }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }]
  }
  
  // 发布时段分析图表
  const publishTimeOption = {
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
      data: ['早晨', '下午', '晚上']
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [{
      name: '发布比例',
      type: 'bar',
      data: [23.4, 45.6, 31.0],
      itemStyle: {
        color: '#36cfc9'
      }
    }]
  }
  
  createChart('contentTrendChart', contentTrendOption)
  createChart('contentTypeChart', contentTypeOption)
  createChart('engagementChart', engagementOption)
  createChart('popularContentChart', popularContentOption)
  createChart('qualityChart', qualityOption)
  createChart('publishTimeChart', publishTimeOption)
}

// 生成模拟趋势数据
const generateMockTrendData = (type) => {
  const days = timeRange.value === '7d' ? 7 : timeRange.value === '30d' ? 30 : 90
  const data = []
  
  for (let i = 0; i < days; i++) {
    let value = 0
    switch (type) {
      case 'posts':
        value = Math.floor(Math.random() * 50) + 20
        break
      case 'activities':
        value = Math.floor(Math.random() * 30) + 10
        break
      case 'comments':
        value = Math.floor(Math.random() * 100) + 50
        break
    }
    data.push(value)
  }
  
  return data
}

// 监听数据变化，重新初始化图表
watch(
  () => contentAnalysis.value,
  () => {
    initCharts()
  },
  { deep: true }
)

// 获取排名样式类
const getRankClass = (rank) => {
  if (rank === 1) return 'rank-gold'
  if (rank === 2) return 'rank-silver'
  if (rank === 3) return 'rank-bronze'
  return 'rank-normal'
}

// 获取内容类型标签类型
const getContentTypeTagType = (type) => {
  const typeMap = {
    posts: 'primary',
    activities: 'success',
    comments: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取内容类型文本
const getContentTypeText = (type) => {
  const typeMap = {
    posts: '帖子',
    activities: '活动',
    comments: '评论'
  }
  return typeMap[type] || type
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  }
  return num.toLocaleString()
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString()
}

// 显示内容类型详情
const showContentTypeDetail = () => {
  contentTypeDetailVisible.value = true
}

// 显示质量详情
const showQualityDetail = () => {
  qualityDetailVisible.value = true
}

// 显示时间详情
const showTimeDetail = () => {
  timeDetailVisible.value = true
}

// 查看内容详情
const viewContentDetail = (content) => {
  console.log('查看内容详情:', content)
}

// 导出数据
const exportData = () => {
  console.log('导出内容分析数据')
}

// 导出内容数据
const exportContentData = () => {
  console.log('导出热门内容列表')
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
  await fetchContentAnalysis()
  popularContent.value = generatePopularContent()
  totalContent.value = popularContent.value.length
  initCharts()
})
</script>

<style scoped>
.content-analysis {
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
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
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

.chart-container {
  height: 400px;
  padding: 20px;
}

.popular-content-section {
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

.content-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.rank-badge {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: #fff;
}

.rank-badge.rank-gold {
  background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
  color: #8b6914;
}

.rank-badge.rank-silver {
  background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
  color: #5a5a5a;
}

.rank-badge.rank-bronze {
  background: linear-gradient(135deg, #cd7f32 0%, #e4a853 100%);
  color: #5a3a1a;
}

.rank-badge.rank-normal {
  background: #f0f0f0;
  color: #606266;
}

.content-title {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.table-pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.content-type-detail,
.quality-detail,
.time-detail {
  padding: 20px 0;
}

.type-item {
  margin-bottom: 24px;
}

.type-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.type-name {
  font-weight: 500;
  color: #303133;
}

.type-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #606266;
}

.quality-stats,
.time-stats {
  display: grid;
  gap: 16px;
}

.quality-item,
.time-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.quality-label,
.time-label {
  font-weight: 500;
  color: #303133;
}

.quality-value,
.time-value {
  color: #409eff;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content-analysis {
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
  
  .content-actions {
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
