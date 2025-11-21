import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 模拟上传API
const mockUploadApi = {
  // 获取上传配置
  async getUploadConfig() {
    return {
      maxFileSize: 100 * 1024 * 1024, // 100MB
      allowedTypes: [
        'image/jpeg',
        'image/png',
        'image/gif',
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/zip',
        'video/mp4',
        'audio/mpeg'
      ],
      chunkSize: 5 * 1024 * 1024, // 5MB
      maxConcurrentUploads: 3,
      thumbnailSizes: [
        { name: 'small', width: 150, height: 150 },
        { name: 'medium', width: 300, height: 300 },
        { name: 'large', width: 800, height: 600 }
      ]
    }
  },
  
  // 初始化分片上传
  async initChunkUpload(fileInfo) {
    await new Promise(resolve => setTimeout(resolve, 200))
    return {
      uploadId: `upload_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
      chunkCount: Math.ceil(fileInfo.size / 5 * 1024 * 1024)
    }
  },
  
  // 上传分片
  async uploadChunk(uploadId, chunkIndex, chunkData, onProgress) {
    await new Promise(resolve => {
      setTimeout(() => {
        // 模拟上传进度
        if (onProgress) {
          onProgress(chunkIndex + 1)
        }
        resolve({
          success: true,
          chunkIndex,
          etag: `etag_${chunkIndex}`
        })
      }, Math.random() * 1000 + 500) // 500-1500ms 随机延迟
    })
    
    return { success: true }
  },
  
  // 完成分片上传
  async completeChunkUpload(uploadId, fileInfo) {
    await new Promise(resolve => setTimeout(resolve, 300))
    return {
      success: true,
      fileId: `file_${Date.now()}`,
      fileUrl: `/uploads/files/${fileInfo.name}`,
      thumbnailUrl: fileInfo.type.startsWith('image/') ? `/thumbnails/${fileInfo.name}` : null
    }
  },
  
  // 简单上传（小文件）
  async simpleUpload(file, onProgress) {
    await new Promise(resolve => {
      let progress = 0
      const interval = setInterval(() => {
        progress += Math.random() * 20
        if (progress >= 100) {
          progress = 100
          clearInterval(interval)
        }
        
        if (onProgress) {
          onProgress(Math.round(progress))
        }
        
        if (progress >= 100) {
          resolve({
            success: true,
            fileId: `file_${Date.now()}`,
            fileUrl: `/uploads/files/${file.name}`,
            thumbnailUrl: file.type.startsWith('image/') ? `/thumbnails/${file.name}` : null
          })
        }
      }, 200)
    })
    
    return { success: true }
  }
}

export function useFileUpload() {
  // 响应式状态
  const uploading = ref(false)
  const uploadQueue = ref([])
  const activeUploads = ref([])
  const completedUploads = ref([])
  const uploadConfig = ref({})
  const dragOver = ref(false)
  
  // 上传统计
  const uploadStats = reactive({
    totalFiles: 0,
    completedFiles: 0,
    failedFiles: 0,
    totalSize: 0,
    uploadedSize: 0,
    speed: 0 // KB/s
  })
  
  // 计算属性
  const isUploading = computed(() => uploading.value)
  
  const hasActiveUploads = computed(() => activeUploads.value.length > 0)
  
  const uploadProgress = computed(() => {
    if (uploadStats.totalSize === 0) return 0
    return Math.round((uploadStats.uploadedSize / uploadStats.totalSize) * 100)
  })
  
  const canUploadMore = computed(() => {
    return activeUploads.value.length < (uploadConfig.value.maxConcurrentUploads || 3)
  })
  
  // 格式化文件大小
  const formatFileSize = (bytes) => {
    if (bytes === 0) return '0 B'
    
    const k = 1024
    const sizes = ['B', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }
  
  // 格式化上传速度
  const formatUploadSpeed = (speed) => {
    if (speed === 0) return '0 B/s'
    
    const k = 1024
    const sizes = ['B/s', 'KB/s', 'MB/s']
    const i = Math.floor(Math.log(speed) / Math.log(k))
    
    return parseFloat((speed / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
  }
  
  // 验证文件
  const validateFile = (file) => {
    const config = uploadConfig.value
    
    // 检查文件大小
    if (file.size > config.maxFileSize) {
      throw new Error(`文件大小超过限制 (${formatFileSize(config.maxFileSize)})`)
    }
    
    // 检查文件类型
    if (!config.allowedTypes.includes(file.type)) {
      throw new Error('不支持的文件类型')
    }
    
    return true
  }
  
  // 获取上传配置
  const fetchUploadConfig = async () => {
    try {
      const config = await mockUploadApi.getUploadConfig()
      uploadConfig.value = config
      return config
    } catch (error) {
      console.error('获取上传配置失败:', error)
      throw error
    }
  }
  
  // 创建上传任务
  const createUploadTask = (file) => {
    const taskId = `task_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
    
    return {
      id: taskId,
      file,
      name: file.name,
      size: file.size,
      type: file.type,
      status: 'pending', // pending, uploading, paused, completed, failed
      progress: 0,
      speed: 0,
      uploadedSize: 0,
      startTime: null,
      endTime: null,
      error: null,
      uploadId: null,
      chunkCount: 0,
      completedChunks: 0,
      result: null
    }
  }
  
  // 分片上传
  const chunkedUpload = async (task) => {
    try {
      const config = uploadConfig.value
      const chunkSize = config.chunkSize
      const chunks = Math.ceil(task.file.size / chunkSize)
      
      // 初始化分片上传
      const initResult = await mockUploadApi.initChunkUpload({
        name: task.file.name,
        size: task.file.size,
        type: task.file.type
      })
      
      task.uploadId = initResult.uploadId
      task.chunkCount = initResult.chunkCount
      
      // 上传分片
      for (let i = 0; i < chunks; i++) {
        if (task.status === 'paused') {
          break
        }
        
        const start = i * chunkSize
        const end = Math.min(start + chunkSize, task.file.size)
        const chunk = task.file.slice(start, end)
        
        await mockUploadApi.uploadChunk(
          task.uploadId,
          i,
          chunk,
          (chunkIndex) => {
            task.completedChunks = chunkIndex
            task.progress = Math.round((chunkIndex / chunks) * 100)
            task.uploadedSize = chunkIndex * chunkSize
            updateUploadStats()
          }
        )
      }
      
      // 完成上传
      if (task.status !== 'paused') {
        const completeResult = await mockUploadApi.completeChunkUpload(
          task.uploadId,
          {
            name: task.file.name,
            size: task.file.size,
            type: task.file.type
          }
        )
        
        task.result = completeResult
        task.status = 'completed'
        task.progress = 100
        task.endTime = Date.now()
      }
      
    } catch (error) {
      task.status = 'failed'
      task.error = error.message
      console.error('分片上传失败:', error)
    }
  }
  
  // 简单上传（小文件）
  const simpleUpload = async (task) => {
    try {
      await mockUploadApi.simpleUpload(
        task.file,
        (progress) => {
          task.progress = progress
          task.uploadedSize = Math.round((progress / 100) * task.file.size)
          updateUploadStats()
        }
      )
      
      task.status = 'completed'
      task.endTime = Date.now()
      
    } catch (error) {
      task.status = 'failed'
      task.error = error.message
      console.error('简单上传失败:', error)
    }
  }
  
  // 开始上传任务
  const startUploadTask = async (task) => {
    task.status = 'uploading'
    task.startTime = Date.now()
    
    const config = uploadConfig.value
    const useChunkedUpload = task.file.size > config.chunkSize
    
    if (useChunkedUpload) {
      await chunkedUpload(task)
    } else {
      await simpleUpload(task)
    }
    
    // 处理上传完成
    if (task.status === 'completed') {
      const index = activeUploads.value.findIndex(t => t.id === task.id)
      if (index > -1) {
        activeUploads.value.splice(index, 1)
        completedUploads.value.push(task)
      }
      
      updateUploadStats()
      ElMessage.success(`${task.name} 上传完成`)
    }
  }
  
  // 处理文件队列
  const processUploadQueue = async () => {
    if (!canUploadMore.value || uploadQueue.value.length === 0) {
      return
    }
    
    const task = uploadQueue.value.shift()
    activeUploads.value.push(task)
    
    // 异步开始上传
    startUploadTask(task)
    
    // 继续处理队列
    if (canUploadMore.value && uploadQueue.value.length > 0) {
      setTimeout(processUploadQueue, 100)
    }
  }
  
  // 更新上传统计
  const updateUploadStats = () => {
    uploadStats.totalFiles = uploadQueue.value.length + activeUploads.value.length + completedUploads.value.length
    uploadStats.completedFiles = completedUploads.value.length
    uploadStats.failedFiles = uploadQueue.value.filter(t => t.status === 'failed').length + 
                            activeUploads.value.filter(t => t.status === 'failed').length
    
    // 计算总大小和已上传大小
    const allTasks = [...uploadQueue.value, ...activeUploads.value, ...completedUploads.value]
    uploadStats.totalSize = allTasks.reduce((sum, task) => sum + task.size, 0)
    uploadStats.uploadedSize = allTasks.reduce((sum, task) => sum + task.uploadedSize, 0)
    
    // 计算平均上传速度
    const activeTasks = activeUploads.value.filter(t => t.status === 'uploading')
    if (activeTasks.length > 0) {
      uploadStats.speed = activeTasks.reduce((sum, task) => sum + task.speed, 0) / activeTasks.length
    } else {
      uploadStats.speed = 0
    }
  }
  
  // 添加文件到上传队列
  const addFilesToQueue = async (files) => {
    if (!uploadConfig.value.allowedTypes) {
      await fetchUploadConfig()
    }
    
    const validFiles = []
    const invalidFiles = []
    
    for (const file of files) {
      try {
        validateFile(file)
        const task = createUploadTask(file)
        uploadQueue.value.push(task)
        validFiles.push(file)
      } catch (error) {
        invalidFiles.push({ file, error: error.message })
      }
    }
    
    // 显示错误信息
    if (invalidFiles.length > 0) {
      const errorMessages = invalidFiles.map(item => 
        `${item.file.name}: ${item.error}`
      ).join('\n')
      
      ElMessageBox.alert(
        `以下文件无法上传:\n${errorMessages}`,
        '文件验证失败',
        { type: 'warning' }
      )
    }
    
    // 开始处理上传队列
    if (validFiles.length > 0) {
      uploading.value = true
      updateUploadStats()
      processUploadQueue()
      
      ElMessage.success(`已添加 ${validFiles.length} 个文件到上传队列`)
    }
    
    return { validFiles, invalidFiles }
  }
  
  // 暂停上传
  const pauseUpload = (taskId) => {
    const task = activeUploads.value.find(t => t.id === taskId)
    if (task && task.status === 'uploading') {
      task.status = 'paused'
    }
  }
  
  // 恢复上传
  const resumeUpload = (taskId) => {
    const task = activeUploads.value.find(t => t.id === taskId)
    if (task && task.status === 'paused') {
      startUploadTask(task)
    }
  }
  
  // 取消上传
  const cancelUpload = async (taskId) => {
    try {
      await ElMessageBox.confirm(
        '确定要取消这个上传任务吗？',
        '取消确认',
        {
          confirmButtonText: '确定取消',
          cancelButtonText: '继续上传',
          type: 'warning'
        }
      )
      
      // 从队列中移除
      const queueIndex = uploadQueue.value.findIndex(t => t.id === taskId)
      if (queueIndex > -1) {
        uploadQueue.value.splice(queueIndex, 1)
      }
      
      // 从活动中移除
      const activeIndex = activeUploads.value.findIndex(t => t.id === taskId)
      if (activeIndex > -1) {
        activeUploads.value.splice(activeIndex, 1)
      }
      
      updateUploadStats()
      ElMessage.success('上传任务已取消')
      
    } catch (error) {
      if (error !== 'cancel') {
        console.error('取消上传失败:', error)
      }
    }
  }
  
  // 重试上传
  const retryUpload = (taskId) => {
    const task = [...uploadQueue.value, ...activeUploads.value].find(t => t.id === taskId)
    if (task && task.status === 'failed') {
      task.status = 'pending'
      task.progress = 0
      task.uploadedSize = 0
      task.error = null
      task.completedChunks = 0
      
      // 重新加入队列
      if (!uploadQueue.value.includes(task)) {
        uploadQueue.value.push(task)
      }
      
      processUploadQueue()
    }
  }
  
  // 清空完成的任务
  const clearCompletedTasks = () => {
    completedUploads.value = []
    updateUploadStats()
  }
  
  // 清空所有任务
  const clearAllTasks = async () => {
    try {
      await ElMessageBox.confirm(
        '确定要清空所有上传任务吗？',
        '清空确认',
        {
          confirmButtonText: '确定清空',
          cancelButtonText: '取消',
          type: 'warning'
        }
      )
      
      uploadQueue.value = []
      activeUploads.value = []
      completedUploads.value = []
      uploading.value = false
      
      updateUploadStats()
      ElMessage.success('所有上传任务已清空')
      
    } catch (error) {
      if (error !== 'cancel') {
        console.error('清空任务失败:', error)
      }
    }
  }
  
  // 处理拖拽事件
  const handleDragEnter = (e) => {
    e.preventDefault()
    dragOver.value = true
  }
  
  const handleDragOver = (e) => {
    e.preventDefault()
    dragOver.value = true
  }
  
  const handleDragLeave = (e) => {
    e.preventDefault()
    dragOver.value = false
  }
  
  const handleDrop = (e) => {
    e.preventDefault()
    dragOver.value = false
    
    const files = Array.from(e.dataTransfer.files)
    if (files.length > 0) {
      addFilesToQueue(files)
    }
  }
  
  // 处理文件选择
  const handleFileSelect = (e) => {
    const files = Array.from(e.target.files)
    if (files.length > 0) {
      addFilesToQueue(files)
    }
    
    // 清空input值，允许重复选择同一文件
    e.target.value = ''
  }
  
  // 初始化
  const init = async () => {
    await fetchUploadConfig()
  }
  
  return {
    // 状态
    uploading,
    uploadQueue,
    activeUploads,
    completedUploads,
    uploadConfig,
    dragOver,
    uploadStats,
    
    // 计算属性
    isUploading,
    hasActiveUploads,
    uploadProgress,
    canUploadMore,
    
    // 方法
    formatFileSize,
    formatUploadSpeed,
    validateFile,
    fetchUploadConfig,
    addFilesToQueue,
    pauseUpload,
    resumeUpload,
    cancelUpload,
    retryUpload,
    clearCompletedTasks,
    clearAllTasks,
    handleDragEnter,
    handleDragOver,
    handleDragLeave,
    handleDrop,
    handleFileSelect,
    init
  }
}
