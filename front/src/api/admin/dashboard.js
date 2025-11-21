/**
 * 仪表盘概览模块API接口
 * 处理系统统计、用户趋势、内容统计、系统健康等数据
 */

import api from './index'

// 模拟数据（作为fallback）
const mockDashboardData = {
  overview: {
    totalUsers: 12345,
    activeUsers: 8932,
    totalPosts: 5678,
    pendingPosts: 23,
    totalActivities: 456,
    pendingActivities: 12,
    totalComments: 34567,
    systemHealth: 95.6
  },
  
  userTrends: {
    dates: ['2024-01-01', '2024-01-02', '2024-01-03', '2024-01-04', '2024-01-05', '2024-01-06', '2024-01-07'],
    newUsers: [120, 132, 101, 134, 90, 230, 210],
    activeUsers: [820, 932, 901, 934, 1290, 1330, 1320],
    retention: [85, 87, 82, 88, 91, 89, 92]
  },
  
  contentStats: {
    categories: ['技术分享', '经验交流', '问答求助', '资源分享', '活动组织'],
    posts: [1234, 987, 876, 654, 432],
    comments: [5678, 4321, 3456, 2345, 1234],
    likes: [12345, 9876, 8765, 6543, 4321]
  },
  
  systemHealth: {
    cpu: 45.6,
    memory: 62.3,
    disk: 78.9,
    network: 23.4,
    database: 12.5,
    cache: 34.7,
    uptime: 2592000, // 30天
    lastUpdate: new Date().toISOString()
  }
}

// 模拟API函数
const mockApi = {
  async getOverview() {
    await new Promise(resolve => setTimeout(resolve, 500))
    return {
      success: true,
      data: mockDashboardData.overview
    }
  },
  
  async getUserTrends(timeRange = '7d') {
    await new Promise(resolve => setTimeout(resolve, 800))
    
    // 根据时间范围返回不同长度的数据
    let data = { ...mockDashboardData.userTrends }
    if (timeRange === '30d') {
      // 生成30天的模拟数据
      data.dates = Array.from({ length: 30 }, (_, i) => {
        const date = new Date()
        date.setDate(date.getDate() - (29 - i))
        return date.toISOString().split('T')[0]
      })
      data.newUsers = Array.from({ length: 30 }, () => Math.floor(Math.random() * 300) + 50)
      data.activeUsers = Array.from({ length: 30 }, () => Math.floor(Math.random() * 1500) + 500)
      data.retention = Array.from({ length: 30 }, () => Math.floor(Math.random() * 20) + 75)
    }
    
    return {
      success: true,
      data
    }
  },
  
  async getContentStats() {
    await new Promise(resolve => setTimeout(resolve, 600))
    return {
      success: true,
      data: mockDashboardData.contentStats
    }
  },
  
  async getSystemHealth() {
    await new Promise(resolve => setTimeout(resolve, 400))
    
    // 模拟实时数据变化
    const health = { ...mockDashboardData.systemHealth }
    health.cpu = Math.random() * 100
    health.memory = Math.random() * 100
    health.disk = Math.random() * 100
    health.network = Math.random() * 100
    health.database = Math.random() * 100
    health.cache = Math.random() * 100
    health.lastUpdate = new Date().toISOString()
    
    return {
      success: true,
      data: health
    }
  }
}

// 数据转换函数
export const transformDashboardData = {
  /**
   * 转换概览数据格式
   */
  overview(data) {
    return {
      totalUsers: data.totalUsers || 0,
      activeUsers: data.activeUsers || 0,
      totalPosts: data.totalPosts || 0,
      pendingPosts: data.pendingPosts || 0,
      totalActivities: data.totalActivities || 0,
      pendingActivities: data.pendingActivities || 0,
      totalComments: data.totalComments || 0,
      systemHealth: data.systemHealth || 0
    }
  },
  
  /**
   * 转换用户趋势数据为ECharts格式
   */
  userTrends(data) {
    return {
      dates: data.dates || [],
      series: {
        newUsers: data.newUsers || [],
        activeUsers: data.activeUsers || [],
        retention: data.retention || []
      }
    }
  },
  
  /**
   * 转换内容统计数据为ECharts格式
   */
  contentStats(data) {
    return {
      categories: data.categories || [],
      series: {
        posts: data.posts || [],
        comments: data.comments || [],
        likes: data.likes || []
      }
    }
  },
  
  /**
   * 转换系统健康数据
   */
  systemHealth(data) {
    return {
      metrics: {
        cpu: data.cpu || 0,
        memory: data.memory || 0,
        disk: data.disk || 0,
        network: data.network || 0,
        database: data.database || 0,
        cache: data.cache || 0
      },
      uptime: data.uptime || 0,
      lastUpdate: data.lastUpdate || new Date().toISOString()
    }
  }
}

// 真实API接口
export const dashboardApi = {
  /**
   * 获取系统概览统计
   * @returns {Promise} 概览统计数据
   */
  async getOverview() {
    // 检查是否使用mock数据
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getOverview()
    }
    
    try {
      const response = await api.get('/admin/statistics/overview')
      
      if (response.success) {
        return {
          success: true,
          data: transformDashboardData.overview(response.data)
        }
      } else {
        throw new Error(response.message || '获取概览数据失败')
      }
    } catch (error) {
      console.error('获取概览数据API调用失败:', error)
      // 如果真实API失败，fallback到mock数据（开发环境）
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.getOverview()
      }
      throw error
    }
  },

  /**
   * 获取用户趋势数据
   * @param {string} timeRange - 时间范围 (7d, 30d, 90d)
   * @returns {Promise} 用户趋势数据
   */
  async getUserTrends(timeRange = '7d') {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getUserTrends(timeRange)
    }
    
    try {
      const response = await api.get('/admin/statistics/user-trends', {
        params: { timeRange }
      })
      
      if (response.success) {
        return {
          success: true,
          data: transformDashboardData.userTrends(response.data)
        }
      } else {
        throw new Error(response.message || '获取用户趋势数据失败')
      }
    } catch (error) {
      console.error('获取用户趋势数据API调用失败:', error)
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.getUserTrends(timeRange)
      }
      throw error
    }
  },

  /**
   * 获取内容统计数据
   * @returns {Promise} 内容统计数据
   */
  async getContentStats() {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getContentStats()
    }
    
    try {
      const response = await api.get('/admin/statistics/content-stats')
      
      if (response.success) {
        return {
          success: true,
          data: transformDashboardData.contentStats(response.data)
        }
      } else {
        throw new Error(response.message || '获取内容统计数据失败')
      }
    } catch (error) {
      console.error('获取内容统计数据API调用失败:', error)
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.getContentStats()
      }
      throw error
    }
  },

  /**
   * 获取系统健康状态
   * @returns {Promise} 系统健康数据
   */
  async getSystemHealth() {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      return mockApi.getSystemHealth()
    }
    
    try {
      const response = await api.get('/admin/statistics/system-health')
      
      if (response.success) {
        return {
          success: true,
          data: transformDashboardData.systemHealth(response.data)
        }
      } else {
        throw new Error(response.message || '获取系统健康数据失败')
      }
    } catch (error) {
      console.error('获取系统健康数据API调用失败:', error)
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_FALLBACK !== 'false') {
        console.warn('使用mock数据作为fallback')
        return mockApi.getSystemHealth()
      }
      throw error
    }
  },

  /**
   * 获取实时数据（WebSocket或轮询）
   * @returns {Promise} 实时数据
   */
  async getRealTimeData() {
    if (import.meta.env.VITE_USE_MOCK_API === 'true') {
      await new Promise(resolve => setTimeout(resolve, 200))
      return {
        success: true,
        data: {
          onlineUsers: Math.floor(Math.random() * 1000) + 500,
          todayVisits: Math.floor(Math.random() * 5000) + 2000,
          activeSessions: Math.floor(Math.random() * 200) + 100,
          serverLoad: Math.random() * 100,
          responseTime: Math.random() * 500 + 50
        }
      }
    }
    
    try {
      const response = await api.get('/admin/statistics/realtime')
      return response
    } catch (error) {
      console.error('获取实时数据API调用失败:', error)
      throw error
    }
  }
}

// 为了向后兼容，导出单独的函数
export const getOverviewStatistics = dashboardApi.getOverview
export const getUserTrendsData = dashboardApi.getUserTrends
export const getContentStatistics = dashboardApi.getContentStats
export const getSystemHealthStatus = dashboardApi.getSystemHealth
export const getRealTimeStatistics = dashboardApi.getRealTimeData

export default dashboardApi
