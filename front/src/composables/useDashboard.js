import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { dashboardApi } from '@/api/admin/dashboard'

// 全局状态管理
const globalDashboardState = ref({
  isLoading: false,
  error: null,
  lastUpdated: null,
  isUsingFallbackData: false
})

// 轮询相关状态
const pollingIntervals = reactive({
  realTimeData: null,
  systemHealth: null
})

// 缓存数据（用于优雅降级）
const cachedData = reactive({
  overview: null,
  userTrends: null,
  contentStats: null,
  systemHealth: null,
  realTimeData: null
})

/**
 * 仪表盘数据管理组合式函数
 */
export function useDashboard() {
  // 响应式状态
  const isLoading = computed(() => globalDashboardState.value.isLoading)
  const error = computed(() => globalDashboardState.value.error)
  const lastUpdated = computed(() => globalDashboardState.value.lastUpdated)
  const isUsingFallbackData = computed(() => globalDashboardState.value.isUsingFallbackData)
  
  // 数据状态
  const overviewData = ref(null)
  const userTrendsData = ref(null)
  const contentStatsData = ref(null)
  const systemHealthData = ref(null)
  const realTimeData = ref(null)
  
  /**
   * 获取系统概览统计
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 获取结果
   */
  const getOverviewStatistics = async (forceRefresh = false) => {
    try {
      globalDashboardState.value.isLoading = true
      globalDashboardState.value.error = null
      
      // 如果有缓存且不强制刷新，直接返回
      if (!forceRefresh && cachedData.overview) {
        overviewData.value = cachedData.overview
        return { success: true, data: cachedData.overview, fromCache: true }
      }
      
      const result = await dashboardApi.getOverview()
      
      if (result.success) {
        overviewData.value = result.data
        cachedData.overview = result.data
        globalDashboardState.value.lastUpdated = new Date().toISOString()
        
        return { success: true, data: result.data, fromCache: false }
      } else {
        throw new Error(result.message || '获取概览数据失败')
      }
    } catch (error) {
      globalDashboardState.value.error = error.message
      
      // 如果有缓存数据，使用缓存并显示警告
      if (cachedData.overview) {
        overviewData.value = cachedData.overview
        globalDashboardState.value.isUsingFallbackData = true
        ElMessage.warning('使用缓存数据，请检查网络连接')
        return { success: true, data: cachedData.overview, fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取概览数据失败')
      return { success: false, message: error.message }
    } finally {
      globalDashboardState.value.isLoading = false
    }
  }
  
  /**
   * 获取用户增长趋势
   * @param {string} timeRange - 时间范围 (7d, 30d, 90d)
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 获取结果
   */
  const getUserTrends = async (timeRange = '7d', forceRefresh = false) => {
    try {
      globalDashboardState.value.isLoading = true
      globalDashboardState.value.error = null
      
      const cacheKey = `userTrends_${timeRange}`
      
      // 检查缓存
      if (!forceRefresh && cachedData[cacheKey]) {
        userTrendsData.value = cachedData[cacheKey]
        return { success: true, data: cachedData[cacheKey], fromCache: true }
      }
      
      const result = await dashboardApi.getUserTrends(timeRange)
      
      if (result.success) {
        userTrendsData.value = result.data
        cachedData[cacheKey] = result.data
        globalDashboardState.value.lastUpdated = new Date().toISOString()
        
        return { success: true, data: result.data, fromCache: false }
      } else {
        throw new Error(result.message || '获取用户趋势数据失败')
      }
    } catch (error) {
      globalDashboardState.value.error = error.message
      
      // 使用缓存数据
      const cacheKey = `userTrends_${timeRange}`
      if (cachedData[cacheKey]) {
        userTrendsData.value = cachedData[cacheKey]
        globalDashboardState.value.isUsingFallbackData = true
        ElMessage.warning('使用缓存的用户趋势数据')
        return { success: true, data: cachedData[cacheKey], fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取用户趋势数据失败')
      return { success: false, message: error.message }
    } finally {
      globalDashboardState.value.isLoading = false
    }
  }
  
  /**
   * 获取内容统计数据
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 获取结果
   */
  const getContentStatistics = async (forceRefresh = false) => {
    try {
      globalDashboardState.value.isLoading = true
      globalDashboardState.value.error = null
      
      if (!forceRefresh && cachedData.contentStats) {
        contentStatsData.value = cachedData.contentStats
        return { success: true, data: cachedData.contentStats, fromCache: true }
      }
      
      const result = await dashboardApi.getContentStats()
      
      if (result.success) {
        contentStatsData.value = result.data
        cachedData.contentStats = result.data
        globalDashboardState.value.lastUpdated = new Date().toISOString()
        
        return { success: true, data: result.data, fromCache: false }
      } else {
        throw new Error(result.message || '获取内容统计数据失败')
      }
    } catch (error) {
      globalDashboardState.value.error = error.message
      
      if (cachedData.contentStats) {
        contentStatsData.value = cachedData.contentStats
        globalDashboardState.value.isUsingFallbackData = true
        ElMessage.warning('使用缓存的内容统计数据')
        return { success: true, data: cachedData.contentStats, fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取内容统计数据失败')
      return { success: false, message: error.message }
    } finally {
      globalDashboardState.value.isLoading = false
    }
  }
  
  /**
   * 获取系统健康状态
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 获取结果
   */
  const getSystemHealthStatus = async (forceRefresh = false) => {
    try {
      globalDashboardState.value.error = null
      
      if (!forceRefresh && cachedData.systemHealth) {
        systemHealthData.value = cachedData.systemHealth
        return { success: true, data: cachedData.systemHealth, fromCache: true }
      }
      
      const result = await dashboardApi.getSystemHealth()
      
      if (result.success) {
        systemHealthData.value = result.data
        cachedData.systemHealth = result.data
        globalDashboardState.value.lastUpdated = new Date().toISOString()
        
        return { success: true, data: result.data, fromCache: false }
      } else {
        throw new Error(result.message || '获取系统健康数据失败')
      }
    } catch (error) {
      globalDashboardState.value.error = error.message
      
      if (cachedData.systemHealth) {
        systemHealthData.value = cachedData.systemHealth
        globalDashboardState.value.isUsingFallbackData = true
        return { success: true, data: cachedData.systemHealth, fromCache: true, isFallback: true }
      }
      
      return { success: false, message: error.message }
    }
  }
  
  /**
   * 获取实时数据
   * @returns {Promise} 获取结果
   */
  const getRealTimeStatistics = async () => {
    try {
      globalDashboardState.value.error = null
      
      const result = await dashboardApi.getRealTimeData()
      
      if (result.success) {
        realTimeData.value = result.data
        cachedData.realTimeData = result.data
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '获取实时数据失败')
      }
    } catch (error) {
      globalDashboardState.value.error = error.message
      
      // 实时数据失败时静默处理，不显示错误消息
      console.warn('获取实时数据失败:', error.message)
      return { success: false, message: error.message }
    }
  }
  
  /**
   * 开始实时数据轮询
   * @param {number} interval - 轮询间隔（毫秒）
   */
  const startRealTimePolling = (interval = 30000) => {
    // 清除现有轮询
    stopRealTimePolling()
    
    // 立即获取一次数据
    getRealTimeStatistics()
    
    // 设置轮询
    pollingIntervals.realTimeData = setInterval(() => {
      getRealTimeStatistics()
    }, interval)
  }
  
  /**
   * 开始系统健康状态轮询
   * @param {number} interval - 轮询间隔（毫秒）
   */
  const startSystemHealthPolling = (interval = 60000) => {
    // 清除现有轮询
    stopSystemHealthPolling()
    
    // 立即获取一次数据
    getSystemHealthStatus()
    
    // 设置轮询
    pollingIntervals.systemHealth = setInterval(() => {
      getSystemHealthStatus()
    }, interval)
  }
  
  /**
   * 停止实时数据轮询
   */
  const stopRealTimePolling = () => {
    if (pollingIntervals.realTimeData) {
      clearInterval(pollingIntervals.realTimeData)
      pollingIntervals.realTimeData = null
    }
  }
  
  /**
   * 停止系统健康状态轮询
   */
  const stopSystemHealthPolling = () => {
    if (pollingIntervals.systemHealth) {
      clearInterval(pollingIntervals.systemHealth)
      pollingIntervals.systemHealth = null
    }
  }
  
  /**
   * 停止所有轮询
   */
  const stopAllPolling = () => {
    stopRealTimePolling()
    stopSystemHealthPolling()
  }
  
  /**
   * 刷新所有数据
   * @returns {Promise} 刷新结果
   */
  const refreshAllData = async () => {
    try {
      globalDashboardState.value.isLoading = true
      globalDashboardState.value.isUsingFallbackData = false
      
      const results = await Promise.allSettled([
        getOverviewStatistics(true),
        getUserTrends('7d', true),
        getContentStatistics(true),
        getSystemHealthStatus(true),
        getRealTimeStatistics()
      ])
      
      const failures = results.filter(result => result.status === 'rejected')
      
      if (failures.length > 0) {
        ElMessage.warning(`部分数据刷新失败 (${failures.length}/${results.length})`)
      } else {
        ElMessage.success('所有数据已刷新')
      }
      
      return { 
        success: failures.length === 0, 
        total: results.length, 
        failures: failures.length 
      }
    } catch (error) {
      ElMessage.error('刷新数据失败')
      return { success: false, message: error.message }
    } finally {
      globalDashboardState.value.isLoading = false
    }
  }
  
  /**
   * 清除错误状态
   */
  const clearError = () => {
    globalDashboardState.value.error = null
  }
  
  /**
   * 重置所有数据
   */
  const resetData = () => {
    overviewData.value = null
    userTrendsData.value = null
    contentStatsData.value = null
    systemHealthData.value = null
    realTimeData.value = null
    
    Object.keys(cachedData).forEach(key => {
      cachedData[key] = null
    })
    
    globalDashboardState.value.error = null
    globalDashboardState.value.isUsingFallbackData = false
  }
  
  return {
    // 状态
    isLoading,
    error,
    lastUpdated,
    isUsingFallbackData,
    
    // 数据
    overviewData,
    userTrendsData,
    contentStatsData,
    systemHealthData,
    realTimeData,
    
    // 方法
    getOverviewStatistics,
    getUserTrends,
    getContentStatistics,
    getSystemHealthStatus,
    getRealTimeStatistics,
    
    // 轮询控制
    startRealTimePolling,
    startSystemHealthPolling,
    stopRealTimePolling,
    stopSystemHealthPolling,
    stopAllPolling,
    
    // 工具方法
    refreshAllData,
    clearError,
    resetData
  }
}

// 导出全局状态和轮询控制
export { globalDashboardState, pollingIntervals, cachedData }

export default useDashboard
