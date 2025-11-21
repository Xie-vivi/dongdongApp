import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 敏感数据字段列表
const SENSITIVE_FIELDS = [
  'password',
  'token',
  'accessToken',
  'refreshToken',
  'secret',
  'key',
  'authorization',
  'cookie',
  'session',
  'csrf'
]

// 日志级别定义
const LOG_LEVELS = {
  INFO: { value: 'info', label: '信息', color: '#409eff' },
  WARN: { value: 'warn', label: '警告', color: '#e6a23c' },
  ERROR: { value: 'error', label: '错误', color: '#f56c6c' },
  DEBUG: { value: 'debug', label: '调试', color: '#909399' }
}

// 操作类型定义
const OPERATION_TYPES = {
  LOGIN: { value: 'login', label: '登录', icon: 'User' },
  LOGOUT: { value: 'logout', label: '登出', icon: 'SwitchButton' },
  CREATE: { value: 'create', label: '创建', icon: 'Plus' },
  UPDATE: { value: 'update', label: '更新', icon: 'Edit' },
  DELETE: { value: 'delete', label: '删除', icon: 'Delete' },
  VIEW: { value: 'view', label: '查看', icon: 'View' },
  EXPORT: { value: 'export', label: '导出', icon: 'Download' },
  UPLOAD: { value: 'upload', label: '上传', icon: 'Upload' },
  DOWNLOAD: { value: 'download', label: '下载', icon: 'Download' },
  SEARCH: { value: 'search', label: '搜索', icon: 'Search' }
}

// 模拟API函数
const mockApi = {
  // 获取操作日志列表
  async getOperationLogs(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 300))
    
    const mockLogs = Array.from({ length: 100 }, (_, index) => {
      const operationTypes = Object.values(OPERATION_TYPES)
      const logLevels = Object.values(LOG_LEVELS)
      const randomType = operationTypes[Math.floor(Math.random() * operationTypes.length)]
      const randomLevel = logLevels[Math.floor(Math.random() * logLevels.length)]
      
      return {
        id: index + 1,
        userId: Math.floor(Math.random() * 50) + 1,
        username: `用户${Math.floor(Math.random() * 20) + 1}`,
        userRole: ['管理员', '编辑', '审核员', '普通用户'][Math.floor(Math.random() * 4)],
        operationType: randomType.value,
        operationName: randomType.label,
        module: ['用户管理', '内容审核', '文件管理', '系统设置', '数据分析'][Math.floor(Math.random() * 5)],
        description: `执行${randomType.label}操作`,
        level: randomLevel.value,
        status: Math.random() > 0.1 ? 'success' : 'failed',
        ip: `192.168.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}`,
        userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
        requestData: this.generateMockRequestData(randomType.value),
        responseData: this.generateMockResponseData(randomType.value),
        duration: Math.floor(Math.random() * 2000) + 100,
        errorCode: Math.random() > 0.8 ? `ERR_${Math.floor(Math.random() * 1000)}` : null,
        errorMessage: Math.random() > 0.8 ? '操作失败：权限不足' : null,
        createdAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
        sessionId: `session_${Math.random().toString(36).substr(2, 9)}`
      }
    })
    
    return {
      data: mockLogs,
      total: mockLogs.length,
      page: params.page || 1,
      pageSize: params.pageSize || 20
    }
  },
  
  // 获取日志详情
  async getLogDetail(logId) {
    await new Promise(resolve => setTimeout(resolve, 200))
    
    return {
      id: logId,
      userId: 1,
      username: '管理员1',
      userRole: '管理员',
      operationType: 'update',
      operationName: '更新',
      module: '用户管理',
      description: '更新用户信息',
      level: 'info',
      status: 'success',
      ip: '192.168.1.100',
      userAgent: 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
      requestData: {
        userId: 123,
        updates: {
          username: 'new_username',
          email: 'user@example.com'
        }
      },
      responseData: {
        success: true,
        data: {
          id: 123,
          username: 'new_username',
          email: 'user@example.com',
          updatedAt: '2024-01-15T10:30:00Z'
        }
      },
      duration: 450,
      errorCode: null,
      errorMessage: null,
      createdAt: '2024-01-15T10:30:00Z',
      sessionId: 'session_abc123'
    }
  },
  
  // 获取日志统计
  async getLogStatistics(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 400))
    
    return {
      totalLogs: 15420,
      todayLogs: 234,
      errorRate: 2.3,
      avgDuration: 650,
      topOperations: [
        { operation: 'view', count: 5420, percentage: 35.2 },
        { operation: 'update', count: 3210, percentage: 20.8 },
        { operation: 'create', count: 2890, percentage: 18.7 },
        { operation: 'delete', count: 1560, percentage: 10.1 },
        { operation: 'login', count: 1230, percentage: 8.0 }
      ],
      levelDistribution: [
        { level: 'info', count: 12000, percentage: 77.8 },
        { level: 'warn', count: 2100, percentage: 13.6 },
        { level: 'error', count: 820, percentage: 5.3 },
        { level: 'debug', count: 500, percentage: 3.2 }
      ],
      hourlyActivity: Array.from({ length: 24 }, (_, i) => ({
        hour: i,
        count: Math.floor(Math.random() * 500) + 50
      })),
      dailyTrend: Array.from({ length: 30 }, (_, i) => ({
        date: new Date(Date.now() - (29 - i) * 24 * 60 * 60 * 1000).toLocaleDateString(),
        count: Math.floor(Math.random() * 1000) + 200,
        errorCount: Math.floor(Math.random() * 50) + 5
      })),
      anomalyAlerts: [
        {
          type: 'failed_login',
          count: 15,
          threshold: 10,
          timeWindow: '5分钟',
          severity: 'high'
        },
        {
          type: 'unusual_access',
          count: 8,
          threshold: 5,
          timeWindow: '10分钟',
          severity: 'medium'
        }
      ]
    }
  },
  
  // 导出日志
  async exportLogs(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    return {
      downloadUrl: '/api/admin/operation-logs/download/export_20240115_103000.csv',
      filename: `operation_logs_${new Date().toISOString().slice(0, 19).replace(/:/g, '-')}.csv`,
      size: '2.3MB',
      recordCount: params.filteredCount || 1000
    }
  },
  
  // 清理日志
  async cleanupLogs(options = {}) {
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    return {
      deletedCount: Math.floor(Math.random() * 5000) + 1000,
      freedSpace: '125.6MB',
      retentionDays: options.retentionDays || 30
    }
  },
  
  // 生成模拟请求数据
  generateMockRequestData(operationType) {
    const baseData = {
      timestamp: new Date().toISOString(),
      requestId: `req_${Math.random().toString(36).substr(2, 9)}`
    }
    
    switch (operationType) {
      case 'login':
        return {
          ...baseData,
          username: 'user123',
          password: '******', // 敏感数据已屏蔽
          rememberMe: true
        }
      case 'create':
      case 'update':
        return {
          ...baseData,
          entityType: 'user',
          entityId: Math.floor(Math.random() * 1000),
          data: {
            username: 'new_user',
            email: 'user@example.com'
          }
        }
      default:
        return baseData
    }
  },
  
  // 生成模拟响应数据
  generateMockResponseData(operationType) {
    const baseData = {
      timestamp: new Date().toISOString(),
      requestId: `req_${Math.random().toString(36).substr(2, 9)}`
    }
    
    switch (operationType) {
      case 'login':
        return {
          ...baseData,
          success: true,
          token: '******', // 敏感数据已屏蔽
          user: {
            id: 123,
            username: 'user123'
          }
        }
      case 'create':
      case 'update':
        return {
          ...baseData,
          success: true,
          data: {
            id: Math.floor(Math.random() * 1000),
            updatedAt: new Date().toISOString()
          }
        }
      default:
        return baseData
    }
  }
}

export function useOperationLogs() {
  // 响应式状态
  const loading = ref(false)
  const logs = ref([])
  const totalLogs = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(20)
  const selectedLogs = ref([])
  const logDetail = ref(null)
  const statistics = ref({})
  const exportLoading = ref(false)
  
  // 查询参数
  const queryParams = reactive({
    keyword: '',
    userId: '',
    username: '',
    operationType: '',
    module: '',
    level: '',
    status: '',
    ip: '',
    dateRange: [],
    minDuration: null,
    maxDuration: null,
    sortBy: 'createdAt',
    sortOrder: 'desc'
  })
  
  // 计算属性
  const hasSelectedLogs = computed(() => selectedLogs.value.length > 0)
  
  const selectedLogsCount = computed(() => selectedLogs.value.length)
  
  const filteredLogsCount = computed(() => {
    // 这里应该根据筛选条件计算，暂时返回总数
    return totalLogs.value
  })
  
  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return '-'
    return new Date(dateString).toLocaleString()
  }
  
  // 格式化持续时间
  const formatDuration = (ms) => {
    if (!ms) return '-'
    if (ms < 1000) return `${ms}ms`
    return `${(ms / 1000).toFixed(2)}s`
  }
  
  // 获取日志级别信息
  const getLogLevelInfo = (level) => {
    return LOG_LEVELS[level.toUpperCase()] || LOG_LEVELS.INFO
  }
  
  // 获取操作类型信息
  const getOperationTypeInfo = (type) => {
    return Object.values(OPERATION_TYPES).find(op => op.value === type) || { label: type, icon: 'Document' }
  }
  
  // 获取状态标签类型
  const getStatusType = (status) => {
    const typeMap = {
      success: 'success',
      failed: 'danger',
      pending: 'warning'
    }
    return typeMap[status] || 'info'
  }
  
  // 获取状态文本
  const getStatusText = (status) => {
    const textMap = {
      success: '成功',
      failed: '失败',
      pending: '进行中'
    }
    return textMap[status] || status
  }
  
  // 敏感数据脱敏
  const sanitizeSensitiveData = (data) => {
    if (!data || typeof data !== 'object') return data
    
    const sanitized = Array.isArray(data) ? [] : {}
    
    for (const key in data) {
      const lowerKey = key.toLowerCase()
      
      if (SENSITIVE_FIELDS.some(field => lowerKey.includes(field))) {
        sanitized[key] = '******'
      } else if (typeof data[key] === 'object' && data[key] !== null) {
        sanitized[key] = sanitizeSensitiveData(data[key])
      } else {
        sanitized[key] = data[key]
      }
    }
    
    return sanitized
  }
  
  // 获取操作日志列表
  const fetchLogs = async (params = {}) => {
    loading.value = true
    try {
      const requestParams = {
        page: currentPage.value,
        pageSize: pageSize.value,
        ...queryParams,
        ...params
      }
      
      const response = await mockApi.getOperationLogs(requestParams)
      
      // 对敏感数据进行脱敏处理
      logs.value = response.data.map(log => ({
        ...log,
        requestData: sanitizeSensitiveData(log.requestData),
        responseData: sanitizeSensitiveData(log.responseData)
      }))
      
      totalLogs.value = response.total
      currentPage.value = response.page
      pageSize.value = response.pageSize
      
    } catch (error) {
      console.error('获取操作日志失败:', error)
      ElMessage.error('获取操作日志失败')
    } finally {
      loading.value = false
    }
  }
  
  // 刷新日志列表
  const refreshLogs = () => {
    fetchLogs()
  }
  
  // 获取日志详情
  const fetchLogDetail = async (logId) => {
    try {
      const detail = await mockApi.getLogDetail(logId)
      
      // 对敏感数据进行脱敏处理
      logDetail.value = {
        ...detail,
        requestData: sanitizeSensitiveData(detail.requestData),
        responseData: sanitizeSensitiveData(detail.responseData)
      }
      
    } catch (error) {
      console.error('获取日志详情失败:', error)
      ElMessage.error('获取日志详情失败')
    }
  }
  
  // 获取日志统计
  const fetchStatistics = async (params = {}) => {
    try {
      const stats = await mockApi.getLogStatistics(params)
      statistics.value = stats
    } catch (error) {
      console.error('获取日志统计失败:', error)
      ElMessage.error('获取日志统计失败')
    }
  }
  
  // 导出日志
  const exportLogs = async (options = {}) => {
    exportLoading.value = true
    try {
      const exportParams = {
        ...queryParams,
        ...options,
        filteredCount: filteredLogsCount.value
      }
      
      const result = await mockApi.exportLogs(exportParams)
      
      // 模拟下载
      const link = document.createElement('a')
      link.href = result.downloadUrl
      link.download = result.filename
      link.click()
      
      ElMessage.success(`日志导出成功，共 ${result.recordCount} 条记录`)
      
    } catch (error) {
      console.error('导出日志失败:', error)
      ElMessage.error('导出日志失败')
    } finally {
      exportLoading.value = false
    }
  }
  
  // 清理日志
  const cleanupLogs = async (options = {}) => {
    try {
      await ElMessageBox.confirm(
        '确定要清理日志吗？此操作不可恢复！',
        '清理确认',
        {
          confirmButtonText: '确定清理',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      const result = await mockApi.cleanupLogs(options)
      
      ElMessage.success(`清理完成，删除了 ${result.deletedCount} 条日志，释放了 ${result.freedSpace} 空间`)
      
      // 刷新数据
      await fetchLogs()
      await fetchStatistics()
      
    } catch (error) {
      if (error !== 'cancel') {
        console.error('清理日志失败:', error)
        ElMessage.error('清理日志失败')
      }
    }
  }
  
  // 搜索日志
  const searchLogs = (keyword) => {
    queryParams.keyword = keyword
    currentPage.value = 1
    fetchLogs()
  }
  
  // 筛选日志
  const filterLogs = (filters) => {
    Object.assign(queryParams, filters)
    currentPage.value = 1
    fetchLogs()
  }
  
  // 排序日志
  const sortLogs = (sortBy, sortOrder = 'desc') => {
    queryParams.sortBy = sortBy
    queryParams.sortOrder = sortOrder
    fetchLogs()
  }
  
  // 选择/取消选择日志
  const toggleLogSelection = (log) => {
    const index = selectedLogs.value.findIndex(l => l.id === log.id)
    if (index > -1) {
      selectedLogs.value.splice(index, 1)
    } else {
      selectedLogs.value.push(log)
    }
  }
  
  // 全选/取消全选
  const toggleSelectAll = () => {
    if (selectedLogs.value.length === logs.value.length) {
      selectedLogs.value = []
    } else {
      selectedLogs.value = [...logs.value]
    }
  }
  
  // 清空选择
  const clearSelection = () => {
    selectedLogs.value = []
  }
  
  // 批量导出选中的日志
  const batchExportLogs = async () => {
    if (selectedLogs.value.length === 0) {
      ElMessage.warning('请先选择要导出的日志')
      return
    }
    
    await exportLogs({
      logIds: selectedLogs.value.map(log => log.id)
    })
  }
  
  // 检测异常操作
  const detectAnomalies = async () => {
    // 这里可以实现异常检测逻辑
    try {
      await fetchStatistics()
      
      if (statistics.value.anomalyAlerts && statistics.value.anomalyAlerts.length > 0) {
        statistics.value.anomalyAlerts.forEach(alert => {
          const severity = alert.severity === 'high' ? 'error' : 'warning'
          ElMessage({
            message: `检测到异常操作：${alert.type}，${alert.count} 次（阈值：${alert.threshold}）`,
            type: severity,
            duration: 5000
          })
        })
      }
    } catch (error) {
      console.error('异常检测失败:', error)
    }
  }
  
  // 监听查询参数变化
  watch(queryParams, () => {
    fetchLogs()
  }, { deep: true })
  
  // 监听分页变化
  watch([currentPage, pageSize], () => {
    fetchLogs()
  })
  
  return {
    // 常量
    LOG_LEVELS,
    OPERATION_TYPES,
    
    // 状态
    loading,
    logs,
    totalLogs,
    currentPage,
    pageSize,
    selectedLogs,
    logDetail,
    statistics,
    exportLoading,
    queryParams,
    
    // 计算属性
    hasSelectedLogs,
    selectedLogsCount,
    filteredLogsCount,
    
    // 方法
    formatDate,
    formatDuration,
    getLogLevelInfo,
    getOperationTypeInfo,
    getStatusType,
    getStatusText,
    sanitizeSensitiveData,
    
    fetchLogs,
    refreshLogs,
    fetchLogDetail,
    fetchStatistics,
    exportLogs,
    cleanupLogs,
    searchLogs,
    filterLogs,
    sortLogs,
    toggleLogSelection,
    toggleSelectAll,
    clearSelection,
    batchExportLogs,
    detectAnomalies
  }
}
