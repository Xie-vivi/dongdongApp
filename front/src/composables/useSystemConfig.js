/**
 * 系统配置管理组合式函数
 * 统一管理系统配置的获取、更新、测试、导入导出等功能
 */

import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 全局状态管理
const globalSystemState = ref({
  isLoading: false,
  error: null,
  lastUpdated: null,
  isUsingFallbackData: false
})

// 缓存数据
const cachedData = reactive({
  config: {},
  optimisticConfig: {}
})

/**
 * 系统配置管理组合式函数
 */
export function useSystemConfig() {
  // 响应式状态
  const isLoading = computed(() => globalSystemState.value.isLoading)
  const error = computed(() => globalSystemState.value.error)
  const lastUpdated = computed(() => globalSystemState.value.lastUpdated)
  const isUsingFallbackData = computed(() => globalSystemState.value.isUsingFallbackData)
  
  // 数据状态
  const systemConfig = ref({})
  const activeSection = ref('site')
  const isTestingConnection = ref(false)
  const isImporting = ref(false)
  const isExporting = ref(false)
  
  // 乐观更新状态
  const pendingUpdates = reactive({})
  const hasUnsavedChanges = computed(() => {
    return Object.keys(pendingUpdates).length > 0
  })
  
  /**
   * 获取系统配置
   */
  const getSystemConfig = async (section = 'all', forceRefresh = false) => {
    try {
      globalSystemState.value.isLoading = true
      globalSystemState.value.error = null
      
      // 检查缓存
      if (!forceRefresh && cachedData.config[section]) {
        systemConfig.value = cachedData.config[section]
        return { success: true, data: cachedData.config[section], fromCache: true }
      }
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      const mockData = { siteName: '系统名称', siteTitle: '系统标题' }
      
      systemConfig.value = mockData
      cachedData.config[section] = mockData
      globalSystemState.value.lastUpdated = new Date().toISOString()
      
      return { success: true, data: mockData, fromCache: false }
    } catch (error) {
      globalSystemState.value.error = error.message
      
      // 使用缓存数据
      if (cachedData.config[section]) {
        systemConfig.value = cachedData.config[section]
        globalSystemState.value.isUsingFallbackData = true
        ElMessage.warning('使用缓存的配置数据')
        return { success: true, data: cachedData.config[section], fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取系统配置失败')
      return { success: false, message: error.message }
    } finally {
      globalSystemState.value.isLoading = false
    }
  }
  
  /**
   * 更新系统配置
   */
  const updateSystemConfig = async (section, config, optimistic = true) => {
    try {
      globalSystemState.value.isLoading = true
      globalSystemState.value.error = null
      
      // 乐观更新：立即应用更改
      if (optimistic) {
        const originalConfig = cachedData.config[section] || {}
        cachedData.optimisticConfig[section] = { ...originalConfig }
        cachedData.config[section] = { ...originalConfig, ...config }
        systemConfig.value = cachedData.config[section]
        pendingUpdates[section] = config
      }
      
      // 模拟API调用
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success('配置更新成功')
      
      // 清除乐观更新状态
      delete pendingUpdates[section]
      delete cachedData.optimisticConfig[section]
      globalSystemState.value.lastUpdated = new Date().toISOString()
      
      return { success: true, data: config }
    } catch (error) {
      globalSystemState.value.error = error.message
      
      // 回滚乐观更新
      if (optimistic && cachedData.optimisticConfig[section]) {
        cachedData.config[section] = cachedData.optimisticConfig[section]
        systemConfig.value = cachedData.config[section]
        delete cachedData.optimisticConfig[section]
        delete pendingUpdates[section]
      }
      
      ElMessage.error(error.message || '更新配置失败')
      return { success: false, message: error.message }
    } finally {
      globalSystemState.value.isLoading = false
    }
  }
  
  /**
   * 测试邮件配置
   */
  const testEmailConnection = async (emailConfig) => {
    try {
      isTestingConnection.value = true
      globalSystemState.value.error = null
      
      // 模拟测试
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      ElMessage.success('邮件配置测试成功')
      return { success: true, message: '连接测试成功' }
    } catch (error) {
      globalSystemState.value.error = error.message
      ElMessage.error(error.message || '邮件配置测试失败')
      return { success: false, message: error.message }
    } finally {
      isTestingConnection.value = false
    }
  }
  
  /**
   * 测试存储配置
   */
  const testStorageConnection = async (storageConfig) => {
    try {
      isTestingConnection.value = true
      globalSystemState.value.error = null
      
      // 模拟测试
      await new Promise(resolve => setTimeout(resolve, 2000))
      
      ElMessage.success('存储配置测试成功')
      return { success: true, message: '连接测试成功' }
    } catch (error) {
      globalSystemState.value.error = error.message
      ElMessage.error(error.message || '存储配置测试失败')
      return { success: false, message: error.message }
    } finally {
      isTestingConnection.value = false
    }
  }
  
  /**
   * 重置配置
   */
  const resetSystemConfig = async (section) => {
    try {
      await ElMessageBox.confirm(
        '确定要重置配置项为默认值吗？此操作不可恢复。',
        '确认重置',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      globalSystemState.value.isLoading = true
      globalSystemState.value.error = null
      
      // 模拟重置
      await new Promise(resolve => setTimeout(resolve, 1000))
      const defaultData = { siteName: '默认系统名称', siteTitle: '默认系统标题' }
      
      ElMessage.success('配置已重置为默认值')
      
      // 更新缓存
      cachedData.config[section] = defaultData
      if (activeSection.value === section) {
        systemConfig.value = defaultData
      }
      
      // 清除待更新状态
      delete pendingUpdates[section]
      
      return { success: true, data: defaultData }
    } catch (error) {
      if (error !== 'cancel') {
        globalSystemState.value.error = error.message
        ElMessage.error(error.message || '重置配置失败')
      }
      return { success: false, message: error.message }
    } finally {
      globalSystemState.value.isLoading = false
    }
  }
  
  /**
   * 导出配置
   */
  const exportSystemConfig = async () => {
    try {
      isExporting.value = true
      globalSystemState.value.error = null
      
      // 模拟导出
      await new Promise(resolve => setTimeout(resolve, 1000))
      const configData = cachedData.config
      
      // 创建下载链接
      const blob = new Blob([JSON.stringify(configData, null, 2)], {
        type: 'application/json'
      })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `system-config-${new Date().toISOString().split('T')[0]}.json`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      
      ElMessage.success('配置导出成功')
      return { success: true, data: configData }
    } catch (error) {
      globalSystemState.value.error = error.message
      ElMessage.error(error.message || '导出配置失败')
      return { success: false, message: error.message }
    } finally {
      isExporting.value = false
    }
  }
  
  /**
   * 导入配置
   */
  const importSystemConfig = async (configData) => {
    try {
      await ElMessageBox.confirm(
        '导入配置将覆盖现有设置，确定要继续吗？',
        '确认导入',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      isImporting.value = true
      globalSystemState.value.error = null
      
      // 模拟导入
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      ElMessage.success('配置导入成功')
      
      // 清除缓存，强制刷新
      Object.keys(cachedData.config).forEach(key => {
        delete cachedData.config[key]
      })
      
      // 重新加载配置
      await getSystemConfig('all', true)
      
      return { success: true }
    } catch (error) {
      if (error !== 'cancel') {
        globalSystemState.value.error = error.message
        ElMessage.error(error.message || '导入配置失败')
      }
      return { success: false, message: error.message }
    } finally {
      isImporting.value = false
    }
  }
  
  /**
   * 切换配置节
   */
  const switchSection = async (section) => {
    activeSection.value = section
    await getSystemConfig(section)
  }
  
  /**
   * 保存当前配置节的更改
   */
  const saveCurrentSection = async () => {
    if (!hasUnsavedChanges.value || !pendingUpdates[activeSection.value]) {
      ElMessage.info('没有需要保存的更改')
      return { success: true }
    }
    
    return await updateSystemConfig(activeSection.value, pendingUpdates[activeSection.value])
  }
  
  /**
   * 放弃当前配置节的更改
   */
  const discardCurrentChanges = () => {
    if (pendingUpdates[activeSection.value]) {
      // 回滚到原始配置
      if (cachedData.optimisticConfig[activeSection.value]) {
        cachedData.config[activeSection.value] = cachedData.optimisticConfig[activeSection.value]
        systemConfig.value = cachedData.config[activeSection.value]
      }
      
      delete pendingUpdates[activeSection.value]
      delete cachedData.optimisticConfig[activeSection.value]
      
      ElMessage.info('已放弃未保存的更改')
    }
  }
  
  return {
    // 状态
    isLoading,
    error,
    lastUpdated,
    isUsingFallbackData,
    systemConfig,
    activeSection,
    isTestingConnection,
    isImporting,
    isExporting,
    hasUnsavedChanges,
    
    // 方法
    getSystemConfig,
    updateSystemConfig,
    testEmailConnection,
    testStorageConnection,
    resetSystemConfig,
    exportSystemConfig,
    importSystemConfig,
    switchSection,
    saveCurrentSection,
    discardCurrentChanges
  }
}
