import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { debounce } from 'lodash-es'

// API imports (假设这些API函数已存在)
// import {
//   getUserBehaviorStats,
//   getContentAnalysis,
//   getSystemPerformance,
//   generateReport,
//   downloadReport,
//   getAnalyticsOverview
// } from '@/api/admin/analytics'

/**
 * 数据分析组合式函数
 * 提供用户行为统计、内容分析、系统性能监控、报表管理等功能
 */
export function useAnalytics() {
  // 加载状态
  const loading = ref(false)
  const chartLoading = ref(false)
  
  // 缓存数据
  const cache = reactive(new Map())
  const CACHE_EXPIRY = 5 * 60 * 1000 // 5分钟缓存
  
  // 概览数据
  const overview = ref({
    totalUsers: 0,
    activeUsers: 0,
    totalContent: 0,
    systemHealth: 'good',
    lastUpdate: null
  })
  
  // 用户行为数据
  const userBehavior = ref({
    dailyActiveUsers: [],
    userGrowthTrend: [],
    userActivityHeatmap: [],
    userDeviceStats: [],
    userRegionStats: []
  })
  
  // 内容分析数据
  const contentAnalysis = ref({
    contentTrend: [],
    contentTypeStats: [],
    contentEngagement: [],
    popularContent: []
  })
  
  // 系统性能数据
  const systemPerformance = ref({
    cpuUsage: [],
    memoryUsage: [],
    diskUsage: [],
    networkTraffic: [],
    responseTime: []
  })
  
  // 报表数据
  const reports = ref([])
  const reportGenerating = ref(false)
  
  // 图表实例
  const charts = reactive(new Map())
  
  // 时间范围
  const timeRange = ref('7d') // 7d, 30d, 90d, 1y
  
  // 自定义日期范围
  const customDateRange = ref([])
  
  // 获取缓存键
  const getCacheKey = (type, params = {}) => {
    return `${type}_${JSON.stringify({ timeRange: timeRange.value, ...params })}`
  }
  
  // 检查缓存是否有效
  const isCacheValid = (key) => {
    const cached = cache.get(key)
    if (!cached) return false
    return Date.now() - cached.timestamp < CACHE_EXPIRY
  }
  
  // 设置缓存
  const setCache = (key, data) => {
    cache.set(key, {
      data,
      timestamp: Date.now()
    })
  }
  
  // 获取缓存数据
  const getCache = (key) => {
    const cached = cache.get(key)
    return cached ? cached.data : null
  }
  
  // 获取概览数据
  const fetchOverview = async () => {
    try {
      loading.value = true
      const cacheKey = getCacheKey('overview')
      
      if (isCacheValid(cacheKey)) {
        overview.value = getCache(cacheKey)
        return
      }
      
      // 模拟API调用
      // const response = await getAnalyticsOverview({ timeRange: timeRange.value })
      
      // 模拟数据
      const mockData = {
        totalUsers: 15234,
        activeUsers: 8921,
        totalContent: 45678,
        systemHealth: 'good',
        lastUpdate: new Date().toISOString()
      }
      
      overview.value = mockData
      setCache(cacheKey, mockData)
      
    } catch (error) {
      console.error('获取概览数据失败:', error)
      ElMessage.error('获取概览数据失败')
    } finally {
      loading.value = false
    }
  }
  
  // 获取用户行为统计
  const fetchUserBehavior = async (params = {}) => {
    try {
      chartLoading.value = true
      const cacheKey = getCacheKey('userBehavior', params)
      
      if (isCacheValid(cacheKey)) {
        userBehavior.value = getCache(cacheKey)
        return
      }
      
      // 模拟API调用
      // const response = await getUserBehaviorStats({ timeRange: timeRange.value, ...params })
      
      // 模拟数据
      const mockData = {
        dailyActiveUsers: generateMockDailyData('activeUsers'),
        userGrowthTrend: generateMockDailyData('userGrowth'),
        userActivityHeatmap: generateMockHeatmapData(),
        userDeviceStats: [
          { name: '桌面端', value: 6543, percentage: 45.2 },
          { name: '移动端', value: 5234, percentage: 36.2 },
          { name: '平板', value: 2789, percentage: 19.3 }
        ],
        userRegionStats: [
          { name: '北京', value: 2341 },
          { name: '上海', value: 1987 },
          { name: '广州', value: 1654 },
          { name: '深圳', value: 1432 },
          { name: '杭州', value: 1209 }
        ]
      }
      
      userBehavior.value = mockData
      setCache(cacheKey, mockData)
      
    } catch (error) {
      console.error('获取用户行为数据失败:', error)
      ElMessage.error('获取用户行为数据失败')
    } finally {
      chartLoading.value = false
    }
  }
  
  // 获取内容分析数据
  const fetchContentAnalysis = async (params = {}) => {
    try {
      chartLoading.value = true
      const cacheKey = getCacheKey('contentAnalysis', params)
      
      if (isCacheValid(cacheKey)) {
        contentAnalysis.value = getCache(cacheKey)
        return
      }
      
      // 模拟API调用
      // const response = await getContentAnalysis({ timeRange: timeRange.value, ...params })
      
      // 模拟数据
      const mockData = {
        contentTrend: generateMockDailyData('content'),
        contentTypeStats: [
          { name: '帖子', value: 12345, percentage: 52.3 },
          { name: '活动', value: 6789, percentage: 28.7 },
          { name: '评论', value: 4567, percentage: 19.3 }
        ],
        contentEngagement: generateMockDailyData('engagement'),
        popularContent: [
          { id: 1, title: '热门帖子1', views: 12345, likes: 892, comments: 234 },
          { id: 2, title: '热门活动1', views: 9876, likes: 567, comments: 123 },
          { id: 3, title: '热门帖子2', views: 8765, likes: 445, comments: 98 }
        ]
      }
      
      contentAnalysis.value = mockData
      setCache(cacheKey, mockData)
      
    } catch (error) {
      console.error('获取内容分析数据失败:', error)
      ElMessage.error('获取内容分析数据失败')
    } finally {
      chartLoading.value = false
    }
  }
  
  // 获取系统性能数据
  const fetchSystemPerformance = async (params = {}) => {
    try {
      chartLoading.value = true
      const cacheKey = getCacheKey('systemPerformance', params)
      
      if (isCacheValid(cacheKey)) {
        systemPerformance.value = getCache(cacheKey)
        return
      }
      
      // 模拟API调用
      // const response = await getSystemPerformance({ timeRange: timeRange.value, ...params })
      
      // 模拟数据
      const mockData = {
        cpuUsage: generateMockTimeSeriesData('cpu'),
        memoryUsage: generateMockTimeSeriesData('memory'),
        diskUsage: generateMockTimeSeriesData('disk'),
        networkTraffic: generateMockTimeSeriesData('network'),
        responseTime: generateMockTimeSeriesData('response')
      }
      
      systemPerformance.value = mockData
      setCache(cacheKey, mockData)
      
    } catch (error) {
      console.error('获取系统性能数据失败:', error)
      ElMessage.error('获取系统性能数据失败')
    } finally {
      chartLoading.value = false
    }
  }
  
  // 获取报表列表
  const fetchReports = async () => {
    try {
      loading.value = true
      
      // 模拟API调用
      // const response = await getReports()
      
      // 模拟数据
      const mockData = [
        {
          id: 1,
          name: '用户行为月报',
          type: 'user_behavior',
          status: 'completed',
          createdAt: '2025-11-15T10:30:00Z',
          fileSize: '2.3MB'
        },
        {
          id: 2,
          name: '内容分析周报',
          type: 'content_analysis',
          status: 'generating',
          createdAt: '2025-11-15T09:15:00Z',
          fileSize: '-'
        }
      ]
      
      reports.value = mockData
      
    } catch (error) {
      console.error('获取报表列表失败:', error)
      ElMessage.error('获取报表列表失败')
    } finally {
      loading.value = false
    }
  }
  
  // 生成报表
  const generateReportData = async (reportConfig) => {
    try {
      reportGenerating.value = true
      
      // 模拟API调用
      // const response = await generateReport(reportConfig)
      
      // 模拟生成过程
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      ElMessage.success('报表生成成功')
      await fetchReports() // 刷新报表列表
      
    } catch (error) {
      console.error('生成报表失败:', error)
      ElMessage.error('生成报表失败')
    } finally {
      reportGenerating.value = false
    }
  }
  
  // 下载报表
  const downloadReportData = async (reportId) => {
    try {
      // 模拟API调用
      // const response = await downloadReport(reportId)
      
      // 模拟下载
      ElMessage.success('报表下载成功')
      
    } catch (error) {
      console.error('下载报表失败:', error)
      ElMessage.error('下载报表失败')
    }
  }
  
  // 创建图表
  const createChart = (elementId, option) => {
    try {
      const element = document.getElementById(elementId)
      if (!element) return null
      
      // 销毁已存在的图表
      if (charts.has(elementId)) {
        charts.get(elementId).dispose()
      }
      
      const chart = echarts.init(element)
      chart.setOption(option)
      charts.set(elementId, chart)
      
      // 响应式处理
      const resizeHandler = debounce(() => {
        chart.resize()
      }, 300)
      
      window.addEventListener('resize', resizeHandler)
      
      return {
        chart,
        resizeHandler
      }
    } catch (error) {
      console.error('创建图表失败:', error)
      return null
    }
  }
  
  // 销毁图表
  const destroyChart = (elementId) => {
    if (charts.has(elementId)) {
      charts.get(elementId).dispose()
      charts.delete(elementId)
    }
  }
  
  // 销毁所有图表
  const destroyAllCharts = () => {
    charts.forEach(chart => chart.dispose())
    charts.clear()
  }
  
  // 时间范围变化处理
  const handleTimeRangeChange = (newRange) => {
    timeRange.value = newRange
    // 清除相关缓存
    clearCache()
    // 重新获取数据
    refreshAllData()
  }
  
  // 清除缓存
  const clearCache = () => {
    cache.clear()
  }
  
  // 刷新所有数据
  const refreshAllData = async () => {
    await Promise.all([
      fetchOverview(),
      fetchUserBehavior(),
      fetchContentAnalysis(),
      fetchSystemPerformance(),
      fetchReports()
    ])
  }
  
  // 生成模拟数据函数
  const generateMockDailyData = (type) => {
    const days = timeRange.value === '7d' ? 7 : timeRange.value === '30d' ? 30 : 90
    const data = []
    const now = new Date()
    
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(now)
      date.setDate(date.getDate() - i)
      
      let value = 0
      switch (type) {
        case 'activeUsers':
          value = Math.floor(Math.random() * 1000) + 800
          break
        case 'userGrowth':
          value = Math.floor(Math.random() * 100) + 50
          break
        case 'content':
          value = Math.floor(Math.random() * 200) + 100
          break
        case 'engagement':
          value = Math.floor(Math.random() * 500) + 300
          break
      }
      
      data.push({
        date: date.toISOString().split('T')[0],
        value
      })
    }
    
    return data
  }
  
  const generateMockTimeSeriesData = (type) => {
    const points = 24 * 7 // 一周的小时数据
    const data = []
    const now = new Date()
    
    for (let i = points - 1; i >= 0; i--) {
      const time = new Date(now)
      time.setHours(time.getHours() - i)
      
      let value = 0
      switch (type) {
        case 'cpu':
          value = Math.random() * 80 + 10
          break
        case 'memory':
          value = Math.random() * 60 + 30
          break
        case 'disk':
          value = Math.random() * 40 + 40
          break
        case 'network':
          value = Math.random() * 1000 + 200
          break
        case 'response':
          value = Math.random() * 200 + 50
          break
      }
      
      data.push({
        time: time.toISOString(),
        value: parseFloat(value.toFixed(2))
      })
    }
    
    return data
  }
  
  const generateMockHeatmapData = () => {
    const data = []
    const hours = 24
    const days = 7
    
    for (let day = 0; day < days; day++) {
      for (let hour = 0; hour < hours; hour++) {
        data.push([
          hour,
          day,
          Math.floor(Math.random() * 100)
        ])
      }
    }
    
    return data
  }
  
  // 计算属性
  const totalUsersGrowth = computed(() => {
    // 计算用户增长率
    return 12.5 // 模拟数据
  })
  
  const activeUsersRate = computed(() => {
    if (overview.value.totalUsers === 0) return 0
    return ((overview.value.activeUsers / overview.value.totalUsers) * 100).toFixed(1)
  })
  
  const systemHealthStatus = computed(() => {
    const status = overview.value.systemHealth
    const statusMap = {
      good: { text: '良好', type: 'success' },
      warning: { text: '警告', type: 'warning' },
      error: { text: '异常', type: 'danger' }
    }
    return statusMap[status] || statusMap.good
  })
  
  // 组件挂载时获取数据
  onMounted(() => {
    refreshAllData()
  })
  
  // 组件卸载时清理图表
  onUnmounted(() => {
    destroyAllCharts()
  })
  
  return {
    // 状态
    loading,
    chartLoading,
    reportGenerating,
    timeRange,
    customDateRange,
    
    // 数据
    overview,
    userBehavior,
    contentAnalysis,
    systemPerformance,
    reports,
    
    // 计算属性
    totalUsersGrowth,
    activeUsersRate,
    systemHealthStatus,
    
    // 方法
    fetchOverview,
    fetchUserBehavior,
    fetchContentAnalysis,
    fetchSystemPerformance,
    fetchReports,
    generateReportData,
    downloadReportData,
    createChart,
    destroyChart,
    destroyAllCharts,
    handleTimeRangeChange,
    clearCache,
    refreshAllData
  }
}
