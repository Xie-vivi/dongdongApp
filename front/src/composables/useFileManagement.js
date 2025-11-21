import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟API函数
const mockApi = {
  // 获取文件列表
  async getFiles(params = {}) {
    await new Promise(resolve => setTimeout(resolve, 500))
    
    const mockFiles = Array.from({ length: 50 }, (_, index) => ({
      id: index + 1,
      name: `file_${index + 1}.${['jpg', 'png', 'pdf', 'doc', 'zip'][index % 5]}`,
      originalName: `示例文件${index + 1}.${['jpg', 'png', 'pdf', 'doc', 'zip'][index % 5]}`,
      size: Math.floor(Math.random() * 10 * 1024 * 1024) + 1024, // 1KB - 10MB
      type: ['image/jpeg', 'image/png', 'application/pdf', 'application/msword', 'application/zip'][index % 5],
      mimeType: ['image', 'image', 'document', 'document', 'archive'][index % 5],
      status: ['active', 'active', 'pending', 'disabled'][index % 4],
      uploadedBy: `用户${Math.floor(Math.random() * 10) + 1}`,
      uploadedAt: new Date(Date.now() - Math.random() * 30 * 24 * 60 * 60 * 1000).toISOString(),
      path: `/uploads/files/${index + 1}`,
      thumbnail: index % 3 === 0 ? `/thumbnails/file_${index + 1}.jpg` : null,
      downloadCount: Math.floor(Math.random() * 1000),
      tags: ['重要', '临时', '备份', '公共'].slice(0, Math.floor(Math.random() * 3) + 1),
      metadata: {
        width: index % 3 === 0 ? Math.floor(Math.random() * 2000) + 800 : null,
        height: index % 3 === 0 ? Math.floor(Math.random() * 2000) + 600 : null,
        duration: index % 4 === 3 ? Math.floor(Math.random() * 3600) : null
      }
    }))
    
    return {
      data: mockFiles,
      total: mockFiles.length,
      page: params.page || 1,
      pageSize: params.pageSize || 20
    }
  },
  
  // 更新文件状态
  async updateFileStatus(fileId, status) {
    await new Promise(resolve => setTimeout(resolve, 300))
    return { success: true }
  },
  
  // 删除文件
  async deleteFile(fileId) {
    await new Promise(resolve => setTimeout(resolve, 500))
    return { success: true }
  },
  
  // 批量删除文件
  async batchDeleteFiles(fileIds) {
    await new Promise(resolve => setTimeout(resolve, 800))
    return { success: true }
  },
  
  // 获取存储统计
  async getStorageStats() {
    await new Promise(resolve => setTimeout(resolve, 300))
    return {
      totalSpace: 100 * 1024 * 1024 * 1024, // 100GB
      usedSpace: 65.7 * 1024 * 1024 * 1024, // 65.7GB
      availableSpace: 34.3 * 1024 * 1024 * 1024, // 34.3GB
      fileCount: 1234,
      uploadCount: 5678,
      downloadCount: 12345,
      lastCleanup: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString()
    }
  },
  
  // 获取文件类型统计
  async getFileTypeStats() {
    await new Promise(resolve => setTimeout(resolve, 300))
    return [
      { name: '图片', value: 456, size: 12.5 * 1024 * 1024 * 1024 },
      { name: '文档', value: 234, size: 8.3 * 1024 * 1024 * 1024 },
      { name: '视频', value: 123, size: 35.2 * 1024 * 1024 * 1024 },
      { name: '音频', value: 89, size: 5.1 * 1024 * 1024 * 1024 },
      { name: '其他', value: 332, size: 4.6 * 1024 * 1024 * 1024 }
    ]
  },
  
  // 清理文件
  async cleanupFiles(options = {}) {
    await new Promise(resolve => setTimeout(resolve, 2000))
    return {
      deletedCount: Math.floor(Math.random() * 50) + 10,
      freedSpace: Math.floor(Math.random() * 1024 * 1024 * 1024) + 100 * 1024 * 1024
    }
  }
}

export function useFileManagement() {
  // 响应式状态
  const loading = ref(false)
  const files = ref([])
  const totalFiles = ref(0)
  const currentPage = ref(1)
  const pageSize = ref(20)
  const selectedFiles = ref([])
  const storageStats = ref({})
  const fileTypeStats = ref([])
  
  // 查询参数
  const queryParams = reactive({
    keyword: '',
    type: '',
    status: '',
    uploadedBy: '',
    dateRange: [],
    sortBy: 'uploadedAt',
    sortOrder: 'desc'
  })
  
  // 视图模式
  const viewMode = ref('grid') // grid | list
  
  // 计算属性
  const hasSelectedFiles = computed(() => selectedFiles.value.length > 0)
  
  const selectedFilesCount = computed(() => selectedFiles.value.length)
  
  const storageUsagePercentage = computed(() => {
    if (!storageStats.value.totalSpace) return 0
    return Math.round((storageStats.value.usedSpace / storageStats.value.totalSpace) * 100)
  })
  
  const formattedStorageStats = computed(() => {
    if (!storageStats.value.totalSpace) return {}
    
    return {
      totalSpace: formatFileSize(storageStats.value.totalSpace),
      usedSpace: formatFileSize(storageStats.value.usedSpace),
      availableSpace: formatFileSize(storageStats.value.availableSpace),
      usagePercentage: storageUsagePercentage.value
    }
  })
  
  // 格式化文件大小
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 B'
    
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }
  
  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return '-'
    return new Date(dateString).toLocaleString()
  }
  
  // 获取文件类型图标
  const getFileTypeIcon = (mimeType) => {
    const iconMap = {
      image: 'Picture',
      document: 'Document',
      video: 'VideoPlay',
      audio: 'Headphones',
      archive: 'FolderOpened',
      default: 'Files'
    }
    return iconMap[mimeType] || iconMap.default
  }
  
  // 获取文件状态标签类型
  const getFileStatusType = (status) => {
    const typeMap = {
      active: 'success',
      pending: 'warning',
      disabled: 'danger'
    }
    return typeMap[status] || 'info'
  }
  
  // 获取文件状态文本
  const getFileStatusText = (status) => {
    const textMap = {
      active: '正常',
      pending: '待审核',
      disabled: '已禁用'
    }
    return textMap[status] || status
  }
  
  // 获取文件类型文本
  const getFileTypeText = (type) => {
    const textMap = {
      'image/jpeg': 'JPEG图片',
      'image/png': 'PNG图片',
      'application/pdf': 'PDF文档',
      'application/msword': 'Word文档',
      'application/zip': 'ZIP压缩包'
    }
    return textMap[type] || type
  }
  
  // 获取文件列表
  const fetchFiles = async (params = {}) => {
    loading.value = true
    try {
      const requestParams = {
        page: currentPage.value,
        pageSize: pageSize.value,
        ...queryParams,
        ...params
      }
      
      const response = await mockApi.getFiles(requestParams)
      files.value = response.data
      totalFiles.value = response.total
      currentPage.value = response.page
      pageSize.value = response.pageSize
      
    } catch (error) {
      console.error('获取文件列表失败:', error)
      ElMessage.error('获取文件列表失败')
    } finally {
      loading.value = false
    }
  }
  
  // 刷新文件列表
  const refreshFiles = () => {
    fetchFiles()
  }
  
  // 更新文件状态
  const updateFileStatus = async (fileId, status) => {
    try {
      await mockApi.updateFileStatus(fileId, status)
      
      // 更新本地数据
      const file = files.value.find(f => f.id === fileId)
      if (file) {
        file.status = status
      }
      
      ElMessage.success('文件状态更新成功')
      
    } catch (error) {
      console.error('更新文件状态失败:', error)
      ElMessage.error('更新文件状态失败')
    }
  }
  
  // 删除单个文件
  const deleteFile = async (fileId) => {
    try {
      await ElMessageBox.confirm(
        '确定要删除这个文件吗？删除后无法恢复！',
        '删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      await mockApi.deleteFile(fileId)
      
      // 从本地数据中移除
      const index = files.value.findIndex(f => f.id === fileId)
      if (index > -1) {
        files.value.splice(index, 1)
        totalFiles.value--
      }
      
      ElMessage.success('文件删除成功')
      
    } catch (error) {
      if (error !== 'cancel') {
        console.error('删除文件失败:', error)
        ElMessage.error('删除文件失败')
      }
    }
  }
  
  // 批量删除文件
  const batchDeleteFiles = async () => {
    if (selectedFiles.value.length === 0) {
      ElMessage.warning('请先选择要删除的文件')
      return
    }
    
    try {
      await ElMessageBox.confirm(
        `确定要删除选中的 ${selectedFiles.value.length} 个文件吗？删除后无法恢复！`,
        '批量删除确认',
        {
          confirmButtonText: '确定删除',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      const fileIds = selectedFiles.value.map(f => f.id)
      await mockApi.batchDeleteFiles(fileIds)
      
      // 从本地数据中移除
      files.value = files.value.filter(f => !selectedFiles.value.includes(f))
      totalFiles.value -= selectedFiles.value.length
      selectedFiles.value = []
      
      ElMessage.success('批量删除成功')
      
    } catch (error) {
      if (error !== 'cancel') {
        console.error('批量删除失败:', error)
        ElMessage.error('批量删除失败')
      }
    }
  }
  
  // 批量更新文件状态
  const batchUpdateFileStatus = async (status) => {
    if (selectedFiles.value.length === 0) {
      ElMessage.warning('请先选择要操作的文件')
      return
    }
    
    try {
      const promises = selectedFiles.value.map(file => 
        mockApi.updateFileStatus(file.id, status)
      )
      
      await Promise.all(promises)
      
      // 更新本地数据
      selectedFiles.value.forEach(file => {
        const localFile = files.value.find(f => f.id === file.id)
        if (localFile) {
          localFile.status = status
        }
      })
      
      selectedFiles.value = []
      ElMessage.success('批量更新状态成功')
      
    } catch (error) {
      console.error('批量更新状态失败:', error)
      ElMessage.error('批量更新状态失败')
    }
  }
  
  // 获取存储统计
  const fetchStorageStats = async () => {
    try {
      const stats = await mockApi.getStorageStats()
      storageStats.value = stats
    } catch (error) {
      console.error('获取存储统计失败:', error)
      ElMessage.error('获取存储统计失败')
    }
  }
  
  // 获取文件类型统计
  const fetchFileTypeStats = async () => {
    try {
      const stats = await mockApi.getFileTypeStats()
      fileTypeStats.value = stats
    } catch (error) {
      console.error('获取文件类型统计失败:', error)
      ElMessage.error('获取文件类型统计失败')
    }
  }
  
  // 清理文件
  const cleanupFiles = async (options = {}) => {
    try {
      loading.value = true
      const result = await mockApi.cleanupFiles(options)
      
      ElMessage.success(`清理完成，删除了 ${result.deletedCount} 个文件，释放了 ${formatFileSize(result.freedSpace)} 空间`)
      
      // 刷新数据
      await fetchFiles()
      await fetchStorageStats()
      
    } catch (error) {
      console.error('清理文件失败:', error)
      ElMessage.error('清理文件失败')
    } finally {
      loading.value = false
    }
  }
  
  // 搜索文件
  const searchFiles = (keyword) => {
    queryParams.keyword = keyword
    currentPage.value = 1
    fetchFiles()
  }
  
  // 筛选文件
  const filterFiles = (filters) => {
    Object.assign(queryParams, filters)
    currentPage.value = 1
    fetchFiles()
  }
  
  // 排序文件
  const sortFiles = (sortBy, sortOrder = 'desc') => {
    queryParams.sortBy = sortBy
    queryParams.sortOrder = sortOrder
    fetchFiles()
  }
  
  // 选择/取消选择文件
  const toggleFileSelection = (file) => {
    const index = selectedFiles.value.findIndex(f => f.id === file.id)
    if (index > -1) {
      selectedFiles.value.splice(index, 1)
    } else {
      selectedFiles.value.push(file)
    }
  }
  
  // 全选/取消全选
  const toggleSelectAll = () => {
    if (selectedFiles.value.length === files.value.length) {
      selectedFiles.value = []
    } else {
      selectedFiles.value = [...files.value]
    }
  }
  
  // 清空选择
  const clearSelection = () => {
    selectedFiles.value = []
  }
  
  // 下载文件
  const downloadFile = (file) => {
    // 模拟下载
    const link = document.createElement('a')
    link.href = file.path
    link.download = file.originalName
    link.click()
    
    // 更新下载计数
    file.downloadCount++
    
    ElMessage.success('开始下载文件')
  }
  
  // 批量下载
  const batchDownloadFiles = () => {
    if (selectedFiles.value.length === 0) {
      ElMessage.warning('请先选择要下载的文件')
      return
    }
    
    selectedFiles.value.forEach(file => {
      downloadFile(file)
    })
    
    clearSelection()
  }
  
  // 监听查询参数变化
  watch(queryParams, () => {
    fetchFiles()
  }, { deep: true })
  
  // 监听分页变化
  watch([currentPage, pageSize], () => {
    fetchFiles()
  })
  
  return {
    // 状态
    loading,
    files,
    totalFiles,
    currentPage,
    pageSize,
    selectedFiles,
    storageStats,
    fileTypeStats,
    queryParams,
    viewMode,
    
    // 计算属性
    hasSelectedFiles,
    selectedFilesCount,
    storageUsagePercentage,
    formattedStorageStats,
    
    // 方法
    formatFileSize,
    formatDate,
    getFileTypeIcon,
    getFileStatusType,
    getFileStatusText,
    getFileTypeText,
    
    fetchFiles,
    refreshFiles,
    updateFileStatus,
    deleteFile,
    batchDeleteFiles,
    batchUpdateFileStatus,
    fetchStorageStats,
    fetchFileTypeStats,
    cleanupFiles,
    searchFiles,
    filterFiles,
    sortFiles,
    toggleFileSelection,
    toggleSelectAll,
    clearSelection,
    downloadFile,
    batchDownloadFiles
  }
}
