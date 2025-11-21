<template>
  <el-dialog
    v-model="visible"
    :title="file ? file.name : '文件预览'"
    width="80%"
    :before-close="handleClose"
    class="file-preview-dialog"
  >
    <div class="preview-container" v-loading="loading">
      <div class="file-info" v-if="file">
        <el-descriptions :column="3" border>
          <el-descriptions-item label="文件名">{{ file.name }}</el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ formatFileSize(file.size) }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">{{ file.mimeType }}</el-descriptions-item>
          <el-descriptions-item label="上传时间">{{ formatDate(file.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="上传者">{{ file.uploader }}</el-descriptions-item>
          <el-descriptions-item label="下载次数">{{ file.downloadCount || 0 }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <div class="preview-content" v-if="file">
        <!-- 图片预览 -->
        <div v-if="isImage" class="image-preview">
          <el-image
            :src="file.url"
            :alt="file.name"
            fit="contain"
            :preview-src-list="[file.url]"
            style="max-width: 100%; max-height: 500px;"
          />
        </div>

        <!-- 视频预览 -->
        <div v-else-if="isVideo" class="video-preview">
          <video
            :src="file.url"
            controls
            style="max-width: 100%; max-height: 500px;"
          >
            您的浏览器不支持视频播放
          </video>
        </div>

        <!-- 音频预览 -->
        <div v-else-if="isAudio" class="audio-preview">
          <audio :src="file.url" controls style="width: 100%;">
            您的浏览器不支持音频播放
          </audio>
        </div>

        <!-- PDF预览 -->
        <div v-else-if="isPdf" class="pdf-preview">
          <iframe
            :src="file.url"
            style="width: 100%; height: 600px; border: none;"
          ></iframe>
        </div>

        <!-- 文本文件预览 -->
        <div v-else-if="isText" class="text-preview">
          <el-scrollbar height="500px">
            <pre class="text-content">{{ textContent }}</pre>
          </el-scrollbar>
        </div>

        <!-- 不支持预览的文件 -->
        <div v-else class="unsupported-preview">
          <el-empty description="该文件类型不支持预览">
            <template #image>
              <el-icon size="100"><document /></el-icon>
            </template>
            <el-button type="primary" @click="downloadFile">
              <el-icon><download /></el-icon>
              下载文件
            </el-button>
          </el-empty>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="downloadFile" v-if="file">
          <el-icon><download /></el-icon>
          下载
        </el-button>
        <el-button type="success" @click="copyUrl" v-if="file">
          <el-icon><link /></el-icon>
          复制链接
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, Download, Link } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  file: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const loading = ref(false)
const textContent = ref('')

const isImage = computed(() => {
  if (!props.file) return false
  return /^image\//.test(props.file.mimeType)
})

const isVideo = computed(() => {
  if (!props.file) return false
  return /^video\//.test(props.file.mimeType)
})

const isAudio = computed(() => {
  if (!props.file) return false
  return /^audio\//.test(props.file.mimeType)
})

const isPdf = computed(() => {
  if (!props.file) return false
  return props.file.mimeType === 'application/pdf'
})

const isText = computed(() => {
  if (!props.file) return false
  return /^text\//.test(props.file.mimeType) || 
         props.file.mimeType === 'application/json' ||
         /\.(txt|md|js|css|html|xml|csv)$/i.test(props.file.name)
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.file && isText.value) {
    loadTextContent()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const loadTextContent = async () => {
  if (!props.file || !isText.value) return
  
  loading.value = true
  try {
    const response = await fetch(props.file.url)
    textContent.value = await response.text()
  } catch (error) {
    ElMessage.error('加载文件内容失败')
    textContent.value = '无法加载文件内容'
  } finally {
    loading.value = false
  }
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

const downloadFile = () => {
  if (!props.file) return
  
  const link = document.createElement('a')
  link.href = props.file.url
  link.download = props.file.name
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  ElMessage.success('开始下载文件')
}

const copyUrl = async () => {
  if (!props.file) return
  
  try {
    await navigator.clipboard.writeText(props.file.url)
    ElMessage.success('链接已复制到剪贴板')
  } catch (error) {
    // 降级方案
    const textArea = document.createElement('textarea')
    textArea.value = props.file.url
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    ElMessage.success('链接已复制到剪贴板')
  }
}

const handleClose = () => {
  visible.value = false
  textContent.value = ''
}
</script>

<style scoped>
.file-preview-dialog {
  --el-dialog-content-font-size: 14px;
}

.preview-container {
  min-height: 400px;
}

.file-info {
  margin-bottom: 20px;
}

.preview-content {
  text-align: center;
}

.image-preview,
.video-preview,
.audio-preview {
  padding: 20px;
}

.pdf-preview {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.text-preview {
  text-align: left;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.text-content {
  padding: 20px;
  margin: 0;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  line-height: 1.5;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.unsupported-preview {
  padding: 40px;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}
</style>
