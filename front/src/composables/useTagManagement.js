/**
 * 标签管理组合式函数
 * 统一管理标签的增删改查、状态管理、批量操作等功能
 */

import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { tagsApi } from '@/api/admin/tags'

// 全局状态管理
const globalTagState = ref({
  isLoading: false,
  error: null,
  lastUpdated: null,
  isUsingFallbackData: false
})

// 缓存数据（用于优雅降级）
const cachedData = reactive({
  tagList: null,
  categories: null,
  tagDetail: {}
})

/**
 * 标签管理组合式函数
 */
export function useTagManagement() {
  // 响应式状态
  const isLoading = computed(() => globalTagState.value.isLoading)
  const error = computed(() => globalTagState.value.error)
  const lastUpdated = computed(() => globalTagState.value.lastUpdated)
  const isUsingFallbackData = computed(() => globalTagState.value.isUsingFallbackData)
  
  // 数据状态
  const tagsList = ref(null)
  const categories = ref([])
  const tagDetail = ref(null)
  const tagUsageStats = ref(null)
  
  // 查询参数
  const queryParams = reactive({
    search: '',
    category: '',
    status: '',
    sortBy: 'createdAt',
    sortOrder: 'desc',
    page: 1,
    pageSize: 10
  })
  
  // 选中项
  const selectedTags = ref([])
  
  /**
   * 获取标签列表
   * @param {Object} params - 查询参数
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 获取结果
   */
  const getTagsList = async (params = {}, forceRefresh = false) => {
    try {
      globalTagState.value.isLoading = true
      globalTagState.value.error = null
      
      const mergedParams = { ...queryParams, ...params }
      
      // 检查缓存
      const cacheKey = JSON.stringify(mergedParams)
      if (!forceRefresh && cachedData.tagList && cachedData.tagList[cacheKey]) {
        tagsList.value = cachedData.tagList[cacheKey]
        return { success: true, data: cachedData.tagList[cacheKey], fromCache: true }
      }
      
      const result = await tagsApi.getTags(mergedParams)
      
      if (result.success) {
        tagsList.value = result.data
        
        // 缓存结果
        if (!cachedData.tagList) {
          cachedData.tagList = {}
        }
        cachedData.tagList[cacheKey] = result.data
        globalTagState.value.lastUpdated = new Date().toISOString()
        
        return { success: true, data: result.data, fromCache: false }
      } else {
        throw new Error(result.message || '获取标签列表失败')
      }
    } catch (error) {
      globalTagState.value.error = error.message
      
      // 使用缓存数据
      const cacheKey = JSON.stringify({ ...queryParams, ...params })
      if (cachedData.tagList && cachedData.tagList[cacheKey]) {
        tagsList.value = cachedData.tagList[cacheKey]
        globalTagState.value.isUsingFallbackData = true
        ElMessage.warning('使用缓存的标签数据')
        return { success: true, data: cachedData.tagList[cacheKey], fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取标签列表失败')
      return { success: false, message: error.message }
    } finally {
      globalTagState.value.isLoading = false
    }
  }
  
  /**
   * 创建标签
   * @param {Object} tagData - 标签数据
   * @returns {Promise} 创建结果
   */
  const createTag = async (tagData) => {
    try {
      globalTagState.value.isLoading = true
      globalTagState.value.error = null
      
      // 验证必填字段
      if (!tagData.name || !tagData.name.trim()) {
        throw new Error('标签名称不能为空')
      }
      
      if (!tagData.category) {
        throw new Error('请选择标签分类')
      }
      
      const result = await tagsApi.createTag({
        name: tagData.name.trim(),
        description: tagData.description?.trim() || '',
        category: tagData.category,
        color: tagData.color || '#409EFF',
        icon: tagData.icon || 'Document',
        status: tagData.status || 'active'
      })
      
      if (result.success) {
        ElMessage.success('标签创建成功')
        
        // 清除缓存，强制刷新列表
        cachedData.tagList = null
        await getTagsList({}, true)
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '创建标签失败')
      }
    } catch (error) {
      globalTagState.value.error = error.message
      ElMessage.error(error.message || '创建标签失败')
      return { success: false, message: error.message }
    } finally {
      globalTagState.value.isLoading = false
    }
  }
  
  /**
   * 更新标签
   * @param {number} id - 标签ID
   * @param {Object} tagData - 标签数据
   * @returns {Promise} 更新结果
   */
  const updateTag = async (id, tagData) => {
    try {
      globalTagState.value.isLoading = true
      globalTagState.value.error = null
      
      // 验证必填字段
      if (!tagData.name || !tagData.name.trim()) {
        throw new Error('标签名称不能为空')
      }
      
      if (!tagData.category) {
        throw new Error('请选择标签分类')
      }
      
      const result = await tagsApi.updateTag(id, {
        name: tagData.name.trim(),
        description: tagData.description?.trim() || '',
        category: tagData.category,
        color: tagData.color,
        icon: tagData.icon,
        status: tagData.status
      })
      
      if (result.success) {
        ElMessage.success('标签更新成功')
        
        // 清除缓存，强制刷新列表
        cachedData.tagList = null
        cachedData.tagDetail[id] = null
        await getTagsList({}, true)
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '更新标签失败')
      }
    } catch (error) {
      globalTagState.value.error = error.message
      ElMessage.error(error.message || '更新标签失败')
      return { success: false, message: error.message }
    } finally {
      globalTagState.value.isLoading = false
    }
  }
  
  /**
   * 删除标签
   * @param {number} id - 标签ID
   * @param {boolean} confirm - 是否显示确认对话框
   * @returns {Promise} 删除结果
   */
  const deleteTag = async (id, confirm = true) => {
    try {
      if (confirm) {
        await ElMessageBox.confirm(
          '确定要删除这个标签吗？删除后无法恢复。',
          '确认删除',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
      }
      
      globalTagState.value.isLoading = true
      globalTagState.value.error = null
      
      const result = await tagsApi.deleteTag(id)
      
      if (result.success) {
        ElMessage.success('标签删除成功')
        
        // 清除缓存，强制刷新列表
        cachedData.tagList = null
        cachedData.tagDetail[id] = null
        await getTagsList({}, true)
        
        return { success: true }
      } else {
        throw new Error(result.message || '删除标签失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        globalTagState.value.error = error.message
        ElMessage.error(error.message || '删除标签失败')
      }
      return { success: false, message: error.message }
    } finally {
      globalTagState.value.isLoading = false
    }
  }
  
  /**
   * 获取标签详情
   * @param {number} id - 标签ID
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 获取结果
   */
  const getTagDetail = async (id, forceRefresh = false) => {
    try {
      globalTagState.value.isLoading = true
      globalTagState.value.error = null
      
      // 检查缓存
      if (!forceRefresh && cachedData.tagDetail[id]) {
        tagDetail.value = cachedData.tagDetail[id]
        return { success: true, data: cachedData.tagDetail[id], fromCache: true }
      }
      
      const result = await tagsApi.getTagById(id)
      
      if (result.success) {
        tagDetail.value = result.data
        cachedData.tagDetail[id] = result.data
        
        return { success: true, data: result.data, fromCache: false }
      } else {
        throw new Error(result.message || '获取标签详情失败')
      }
    } catch (error) {
      globalTagState.value.error = error.message
      
      if (cachedData.tagDetail[id]) {
        tagDetail.value = cachedData.tagDetail[id]
        globalTagState.value.isUsingFallbackData = true
        return { success: true, data: cachedData.tagDetail[id], fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取标签详情失败')
      return { success: false, message: error.message }
    } finally {
      globalTagState.value.isLoading = false
    }
  }
  
  /**
   * 获取标签分类
   * @param {boolean} forceRefresh - 是否强制刷新
   * @returns {Promise} 获取结果
   */
  const getTagCategories = async (forceRefresh = false) => {
    try {
      globalTagState.value.error = null
      
      // 检查缓存
      if (!forceRefresh && cachedData.categories) {
        categories.value = cachedData.categories
        return { success: true, data: cachedData.categories, fromCache: true }
      }
      
      const result = await tagsApi.getTagCategories()
      
      if (result.success) {
        categories.value = result.data
        cachedData.categories = result.data
        
        return { success: true, data: result.data, fromCache: false }
      } else {
        throw new Error(result.message || '获取标签分类失败')
      }
    } catch (error) {
      globalTagState.value.error = error.message
      
      if (cachedData.categories) {
        categories.value = cachedData.categories
        globalTagState.value.isUsingFallbackData = true
        return { success: true, data: cachedData.categories, fromCache: true, isFallback: true }
      }
      
      ElMessage.error(error.message || '获取标签分类失败')
      return { success: false, message: error.message }
    }
  }
  
  /**
   * 批量更新标签状态
   * @param {Array} ids - 标签ID数组
   * @param {string} status - 状态
   * @returns {Promise} 更新结果
   */
  const batchUpdateStatus = async (ids, status) => {
    try {
      if (!ids || ids.length === 0) {
        throw new Error('请选择要操作的标签')
      }
      
      globalTagState.value.isLoading = true
      globalTagState.value.error = null
      
      const result = await tagsApi.batchUpdateStatus(ids, status)
      
      if (result.success) {
        ElMessage.success(`成功更新 ${result.data.updatedCount} 个标签的状态`)
        
        // 清除缓存，强制刷新列表
        cachedData.tagList = null
        await getTagsList({}, true)
        
        // 清除选中项
        selectedTags.value = []
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '批量更新状态失败')
      }
    } catch (error) {
      globalTagState.value.error = error.message
      ElMessage.error(error.message || '批量更新状态失败')
      return { success: false, message: error.message }
    } finally {
      globalTagState.value.isLoading = false
    }
  }
  
  /**
   * 批量删除标签
   * @param {Array} ids - 标签ID数组
   * @returns {Promise} 删除结果
   */
  const batchDelete = async (ids) => {
    try {
      if (!ids || ids.length === 0) {
        throw new Error('请选择要删除的标签')
      }
      
      await ElMessageBox.confirm(
        `确定要删除选中的 ${ids.length} 个标签吗？删除后无法恢复。`,
        '确认批量删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      globalTagState.value.isLoading = true
      globalTagState.value.error = null
      
      const result = await tagsApi.batchDelete(ids)
      
      if (result.success) {
        const { deletedCount, failedDeletes } = result.data
        
        if (deletedCount > 0) {
          ElMessage.success(`成功删除 ${deletedCount} 个标签`)
        }
        
        if (failedDeletes && failedDeletes.length > 0) {
          const failedNames = failedDeletes.map(item => item.name).join('、')
          ElMessage.warning(`以下标签删除失败：${failedNames}`)
        }
        
        // 清除缓存，强制刷新列表
        cachedData.tagList = null
        await getTagsList({}, true)
        
        // 清除选中项
        selectedTags.value = []
        
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '批量删除失败')
      }
    } catch (error) {
      if (error !== 'cancel') {
        globalTagState.value.error = error.message
        ElMessage.error(error.message || '批量删除失败')
      }
      return { success: false, message: error.message }
    } finally {
      globalTagState.value.isLoading = false
    }
  }
  
  /**
   * 获取标签使用统计
   * @param {number} id - 标签ID
   * @returns {Promise} 统计结果
   */
  const getTagUsageStats = async (id) => {
    try {
      globalTagState.value.error = null
      
      const result = await tagsApi.getTagUsageStats(id)
      
      if (result.success) {
        tagUsageStats.value = result.data
        return { success: true, data: result.data }
      } else {
        throw new Error(result.message || '获取标签使用统计失败')
      }
    } catch (error) {
      globalTagState.value.error = error.message
      ElMessage.error(error.message || '获取标签使用统计失败')
      return { success: false, message: error.message }
    }
  }
  
  /**
   * 搜索标签
   * @param {string} keyword - 搜索关键词
   */
  const searchTags = (keyword) => {
    queryParams.search = keyword
    queryParams.page = 1
    getTagsList()
  }
  
  /**
   * 重置查询参数
   */
  const resetQueryParams = () => {
    Object.assign(queryParams, {
      search: '',
      category: '',
      status: '',
      sortBy: 'createdAt',
      sortOrder: 'desc',
      page: 1,
      pageSize: 10
    })
    selectedTags.value = []
  }
  
  /**
   * 切换排序
   * @param {string} sortBy - 排序字段
   */
  const toggleSort = (sortBy) => {
    if (queryParams.sortBy === sortBy) {
      queryParams.sortOrder = queryParams.sortOrder === 'asc' ? 'desc' : 'asc'
    } else {
      queryParams.sortBy = sortBy
      queryParams.sortOrder = 'desc'
    }
    getTagsList()
  }
  
  /**
   * 处理分页变化
   * @param {number} page - 页码
   * @param {number} pageSize - 每页大小
   */
  const handlePageChange = (page, pageSize) => {
    queryParams.page = page
    queryParams.pageSize = pageSize
    getTagsList()
  }
  
  /**
   * 清除错误状态
   */
  const clearError = () => {
    globalTagState.value.error = null
  }
  
  /**
   * 重置所有数据
   */
  const resetData = () => {
    tagsList.value = null
    categories.value = []
    tagDetail.value = null
    tagUsageStats.value = null
    selectedTags.value = []
    
    Object.keys(cachedData.tagDetail).forEach(key => {
      cachedData.tagDetail[key] = null
    })
    
    cachedData.tagList = null
    cachedData.categories = null
    
    globalTagState.value.error = null
    globalTagState.value.isUsingFallbackData = false
    
    resetQueryParams()
  }
  
  // 监听查询参数变化
  watch(queryParams, () => {
    getTagsList()
  }, { deep: true })
  
  return {
    // 状态
    isLoading,
    error,
    lastUpdated,
    isUsingFallbackData,
    
    // 数据
    tagsList,
    categories,
    tagDetail,
    tagUsageStats,
    queryParams,
    selectedTags,
    
    // 方法
    getTagsList,
    createTag,
    updateTag,
    deleteTag,
    getTagDetail,
    getTagCategories,
    batchUpdateStatus,
    batchDelete,
    getTagUsageStats,
    
    // 工具方法
    searchTags,
    resetQueryParams,
    toggleSort,
    handlePageChange,
    clearError,
    resetData
  }
}

// 导出全局状态
export { globalTagState, cachedData }

export default useTagManagement
