<template>
  <div class="file-upload">
    <!-- 上传区域 -->
    <div
      class="upload-area"
      :class="{ 'drag-over': dragOver, 'uploading': isUploading }"
      @dragenter="handleDragEnter"
      @dragover="handleDragOver"
      @dragleave="handleDragLeave"
      @drop="handleDrop"
      @click="triggerFileSelect"
    >
      <div class="upload-content">
        <div class="upload-icon">
          <el-icon size="48"><UploadFilled /></el-icon>
        </div>
        <div class="upload-text">
          <h3>拖拽文件到此处上传</h3>
          <p>或者点击选择文件</p>
          <p class="upload-hint">
            支持格式: {{ allowedTypesText }}，单文件最大 {{ formatFileSize(uploadConfig.maxFileSize) }}
          </p>
        </div>
        <div class="upload-button">
          <el-button type="primary" :loading="isUploading">
            选择文件
          </el-button>
        </div>
      </div>
      
      <input
        ref="fileInput"
        type="file"
        multiple
        :accept="allowedTypesText"
        @change="handleFileSelect"
        style="display: none"
      />
    </div>

    <!-- 上传统计 -->
    <div v-if="hasActiveUploads || uploadQueue.length > 0" class="upload-stats">
      <div class="stats-item">
        <span class="stats-label">总文件数:</span>
        <span class="stats-value">{{ uploadStats.totalFiles }}</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">已完成:</span>
        <span class="stats-value">{{ uploadStats.completedFiles }}</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">总大小:</span>
        <span class="stats-value">{{ formatFileSize(uploadStats.totalSize) }}</span>
      </div>
      <div class="stats-item">
        <span class="stats-label">上传速度:</span>
        <span class="stats-value">{{ formatUploadSpeed(uploadStats.speed) }}</span>
      </div>
      
      <div class="overall-progress">
        <el-progress
          :percentage="uploadProgress"
          :stroke-width="8"
          :show-text="true"
        />
      </div>
    </div>

    <!-- 上传队列 -->
    <div v-if="uploadQueue.length > 0 || activeUploads.length > 0" class="upload-queue">
      <div class="queue-header">
        <h4>上传队列</h4>
        <div class="queue-actions">
          <el-button
            v-if="hasActiveUploads"
            link
            type="warning"
            size="small"
            @click="pauseAllUploads"
          >
            <el-icon><VideoPause /></el-icon>
            暂停全部
          </el-button>
          <el-button
            v-if="uploadQueue.some(t => t.status === 'paused')"
            link
            type="success"
            size="small"
            @click="resumeAllUploads"
          >
            <el-icon><VideoPlay /></el-icon>
            恢复全部
          </el-button>
          <el-button
            link
            type="danger"
            size="small"
            @click="clearAllTasks"
          >
            <el-icon><Delete /></el-icon>
            清空队列
          </el-button>
        </div>
      </div>

      <div class="queue-list">
        <!-- 等待中的文件 -->
        <div
          v-for="task in uploadQueue"
          :key="task.id"
          class="upload-item pending"
        >
          <div class="item-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="item-info">
            <div class="item-name">{{ task.name }}</div>
            <div class="item-meta">
              <span class="item-size">{{ formatFileSize(task.size) }}</span>
              <span class="item-status">等待上传</span>
            </div>
          </div>
          <div class="item-actions">
            <el-button
              link
              type="danger"
              size="small"
              @click="cancelUpload(task.id)"
            >
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 上传中的文件 -->
        <div
          v-for="task in activeUploads"
          :key="task.id"
          class="upload-item"
          :class="task.status"
        >
          <div class="item-icon">
            <el-icon v-if="task.status === 'uploading'"><Loading /></el-icon>
            <el-icon v-else-if="task.status === 'paused'"><VideoPause /></el-icon>
            <el-icon v-else-if="task.status === 'failed'"><Warning /></el-icon>
            <el-icon v-else><Check /></el-icon>
          </div>
          <div class="item-info">
            <div class="item-name">{{ task.name }}</div>
            <div class="item-meta">
              <span class="item-size">{{ formatFileSize(task.size) }}</span>
              <span class="item-status">{{ getUploadStatusText(task.status) }}</span>
              <span v-if="task.status === 'uploading'" class="item-progress">
                {{ task.progress }}%
              </span>
            </div>
            <div v-if="task.status === 'uploading'" class="item-progress-bar">
              <el-progress
                :percentage="task.progress"
                :stroke-width="4"
                :show-text="false"
              />
            </div>
            <div v-if="task.status === 'failed'" class="item-error">
              {{ task.error }}
            </div>
          </div>
          <div class="item-actions">
            <el-button
              v-if="task.status === 'uploading'"
              link
              type="warning"
              size="small"
              @click="pauseUpload(task.id)"
            >
              <el-icon><VideoPause /></el-icon>
            </el-button>
            <el-button
              v-if="task.status === 'paused'"
              link
              type="success"
              size="small"
              @click="resumeUpload(task.id)"
            >
              <el-icon><VideoPlay /></el-icon>
            </el-button>
            <el-button
              v-if="task.status === 'failed'"
              link
              type="primary"
              size="small"
              @click="retryUpload(task.id)"
            >
              <el-icon><RefreshRight /></el-icon>
            </el-button>
            <el-button
              link
              type="danger"
              size="small"
              @click="cancelUpload(task.id)"
            >
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 已完成的文件 -->
    <div v-if="completedUploads.length > 0" class="completed-uploads">
      <div class="completed-header">
        <h4>已完成</h4>
        <el-button
          link
          type="primary"
          size="small"
          @click="clearCompletedTasks"
        >
          清空已完成
        </el-button>
      </div>

      <div class="completed-list">
        <div
          v-for="task in completedUploads"
          :key="task.id"
          class="upload-item completed"
        >
          <div class="item-icon">
            <el-icon><Check /></el-icon>
          </div>
          <div class="item-info">
            <div class="item-name">{{ task.name }}</div>
            <div class="item-meta">
              <span class="item-size">{{ formatFileSize(task.size) }}</span>
              <span class="item-status">上传成功</span>
              <span class="item-time">
                耗时 {{ formatDuration(task.endTime - task.startTime) }}
              </span>
            </div>
          </div>
          <div class="item-actions">
            <el-button
              link
              type="primary"
              size="small"
              @click="viewFile(task)"
            >
              <el-icon><View /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传配置对话框 -->
    <el-dialog
      v-model="configDialogVisible"
      title="上传配置"
      width="500px"
    >
      <el-form :model="uploadSettings" label-width="120px">
        <el-form-item label="分片大小">
          <el-select v-model="uploadSettings.chunkSize" style="width: 100%">
            <el-option label="1MB" :value="1 * 1024 * 1024" />
            <el-option label="5MB" :value="5 * 1024 * 1024" />
            <el-option label="10MB" :value="10 * 1024 * 1024" />
          </el-select>
        </el-form-item>
        <el-form-item label="并发上传数">
          <el-input-number
            v-model="uploadSettings.maxConcurrent"
            :min="1"
            :max="5"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="自动重试">
          <el-switch v-model="uploadSettings.autoRetry" />
        </el-form-item>
        <el-form-item label="重试次数">
          <el-input-number
            v-model="uploadSettings.retryCount"
            :min="0"
            :max="5"
            :disabled="!uploadSettings.autoRetry"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveUploadSettings">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  UploadFilled,
  Clock,
  Loading,
  VideoPause,
  VideoPlay,
  Warning,
  Check,
  Close,
  RefreshRight,
  Delete,
  View
} from '@element-plus/icons-vue'
import { useFileUpload } from '@/composables/useFileUpload'

// 定义事件
const emit = defineEmits(['upload-complete', 'upload-error'])

// 使用文件上传组合式函数
const {
  uploading,
  uploadQueue,
  activeUploads,
  completedUploads,
  uploadConfig,
  dragOver,
  uploadStats,
  isUploading,
  hasActiveUploads,
  uploadProgress,
  canUploadMore,
  formatFileSize,
  formatUploadSpeed,
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
} = useFileUpload()

// 响应式数据
const fileInput = ref(null)
const configDialogVisible = ref(false)
const uploadSettings = ref({
  chunkSize: 5 * 1024 * 1024,
  maxConcurrent: 3,
  autoRetry: true,
  retryCount: 3
})

// 计算属性
const allowedTypesText = computed(() => {
  if (!uploadConfig.value.allowedTypes) return '所有文件'
  
  const typeMap = {
    'image/jpeg': 'JPG',
    'image/png': 'PNG',
    'image/gif': 'GIF',
    'application/pdf': 'PDF',
    'application/msword': 'DOC',
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document': 'DOCX',
    'application/zip': 'ZIP',
    'video/mp4': 'MP4',
    'audio/mpeg': 'MP3'
  }
  
  return uploadConfig.value.allowedTypes
    .map(type => typeMap[type] || type)
    .join(', ')
})

// 触发文件选择
const triggerFileSelect = () => {
  fileInput.value?.click()
}

// 获取上传状态文本
const getUploadStatusText = (status) => {
  const statusMap = {
    pending: '等待上传',
    uploading: '上传中',
    paused: '已暂停',
    completed: '已完成',
    failed: '上传失败'
  }
  return statusMap[status] || status
}

// 格式化持续时间
const formatDuration = (ms) => {
  if (!ms) return '0秒'
  
  const seconds = Math.floor(ms / 1000)
  const minutes = Math.floor(seconds / 60)
  const hours = Math.floor(minutes / 60)
  
  if (hours > 0) {
    return `${hours}小时${minutes % 60}分钟`
  }
  if (minutes > 0) {
    return `${minutes}分钟${seconds % 60}秒`
  }
  return `${seconds}秒`
}

// 暂停所有上传
const pauseAllUploads = () => {
  activeUploads.value
    .filter(task => task.status === 'uploading')
    .forEach(task => pauseUpload(task.id))
}

// 恢复所有上传
const resumeAllUploads = () => {
  const pausedTasks = [
    ...uploadQueue.value.filter(task => task.status === 'paused'),
    ...activeUploads.value.filter(task => task.status === 'paused')
  ]
  
  pausedTasks.forEach(task => resumeUpload(task.id))
}

// 查看文件
const viewFile = (task) => {
  if (task.result?.fileUrl) {
    window.open(task.result.fileUrl, '_blank')
  } else {
    ElMessage.info('文件链接暂未生成')
  }
}

// 保存上传设置
const saveUploadSettings = () => {
  // 这里可以保存设置到本地存储或发送到服务器
  localStorage.setItem('uploadSettings', JSON.stringify(uploadSettings.value))
  configDialogVisible.value = false
  ElMessage.success('上传配置已保存')
}

// 加载上传设置
const loadUploadSettings = () => {
  const saved = localStorage.getItem('uploadSettings')
  if (saved) {
    try {
      uploadSettings.value = JSON.parse(saved)
    } catch (error) {
      console.error('加载上传设置失败:', error)
    }
  }
}

// 监听上传完成
const handleUploadComplete = (files) => {
  emit('upload-complete', files)
}

// 监听上传错误
const handleUploadError = (error) => {
  emit('upload-error', error)
}

// 组件挂载
onMounted(async () => {
  await init()
  loadUploadSettings()
})
</script>

<style scoped>
.file-upload {
  padding: 20px 0;
}

.upload-area {
  border: 2px dashed #dcdfe6;
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.upload-area:hover {
  border-color: #409eff;
  background: #f0f9ff;
}

.upload-area.drag-over {
  border-color: #409eff;
  background: #ecf5ff;
  transform: scale(1.02);
}

.upload-area.uploading {
  border-color: #67c23a;
  background: #f0f9ff;
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  color: #c0c4cc;
  transition: color 0.3s ease;
}

.upload-area:hover .upload-icon {
  color: #409eff;
}

.upload-text h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.upload-text p {
  margin: 0 0 4px 0;
  font-size: 14px;
  color: #606266;
}

.upload-hint {
  font-size: 12px !important;
  color: #909399 !important;
}

.upload-stats {
  margin-top: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.stats-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-right: 24px;
  margin-bottom: 12px;
}

.stats-label {
  font-size: 12px;
  color: #909399;
}

.stats-value {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.overall-progress {
  margin-top: 16px;
}

.upload-queue,
.completed-uploads {
  margin-top: 24px;
}

.queue-header,
.completed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.queue-header h4,
.completed-header h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.queue-actions {
  display: flex;
  gap: 8px;
}

.queue-list,
.completed-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.upload-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.upload-item.pending {
  border-left: 4px solid #e6a23c;
}

.upload-item.uploading {
  border-left: 4px solid #409eff;
}

.upload-item.paused {
  border-left: 4px solid #f56c6c;
}

.upload-item.failed {
  border-left: 4px solid #f56c6c;
  background: #fef0f0;
}

.upload-item.completed {
  border-left: 4px solid #67c23a;
  background: #f0f9ff;
}

.item-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 16px;
}

.upload-item.pending .item-icon {
  background: #fdf6ec;
  color: #e6a23c;
}

.upload-item.uploading .item-icon {
  background: #ecf5ff;
  color: #409eff;
}

.upload-item.paused .item-icon {
  background: #fef0f0;
  color: #f56c6c;
}

.upload-item.failed .item-icon {
  background: #fef0f0;
  color: #f56c6c;
}

.upload-item.completed .item-icon {
  background: #f0f9ff;
  color: #67c23a;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #909399;
}

.item-status {
  font-weight: 500;
}

.item-progress {
  color: #409eff !important;
  font-weight: 500 !important;
}

.item-progress-bar {
  margin-top: 8px;
}

.item-error {
  margin-top: 4px;
  font-size: 12px;
  color: #f56c6c;
}

.item-actions {
  display: flex;
  gap: 4px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.upload-item:hover .item-actions {
  opacity: 1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .upload-area {
    padding: 30px 16px;
  }
  
  .upload-text h3 {
    font-size: 16px;
  }
  
  .stats-item {
    display: flex;
    margin-bottom: 8px;
    margin-right: 0;
  }
  
  .queue-header,
  .completed-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .item-meta {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .item-actions {
    opacity: 1;
  }
}

/* 动画效果 */
.upload-item {
  animation: slideInRight 0.3s ease-out;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 加载动画 */
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.upload-item.uploading .item-icon {
  animation: spin 2s linear infinite;
}
</style>
