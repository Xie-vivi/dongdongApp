<template>
  <el-dialog
    v-model="visible"
    title="文件上传"
    width="600px"
    :before-close="handleClose"
  >
    <div class="upload-container">
      <el-upload
        ref="uploadRef"
        class="upload-demo"
        drag
        :action="uploadUrl"
        :headers="uploadHeaders"
        :data="uploadData"
        :before-upload="beforeUpload"
        :on-success="handleSuccess"
        :on-error="handleError"
        :on-progress="handleProgress"
        :file-list="fileList"
        multiple
        :auto-upload="false"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          将文件拖到此处，或<em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持 jpg/png/gif/pdf/doc/docx/xls/xlsx 文件，且不超过 10MB
          </div>
        </template>
      </el-upload>

      <div class="upload-options" v-if="fileList.length > 0">
        <el-form :model="uploadForm" label-width="80px">
          <el-form-item label="文件分类">
            <el-select v-model="uploadForm.category" placeholder="请选择分类">
              <el-option label="图片" value="image" />
              <el-option label="文档" value="document" />
              <el-option label="视频" value="video" />
              <el-option label="音频" value="audio" />
              <el-option label="其他" value="other" />
            </el-select>
          </el-form-item>
          <el-form-item label="访问权限">
            <el-radio-group v-model="uploadForm.permission">
              <el-radio label="public">公开</el-radio>
              <el-radio label="private">私有</el-radio>
              <el-radio label="protected">受保护</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="描述">
            <el-input
              v-model="uploadForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入文件描述"
            />
          </el-form-item>
        </el-form>
      </div>

      <div class="progress-container" v-if="uploading">
        <el-progress
          :percentage="uploadProgress"
          :status="uploadStatus"
          :stroke-width="6"
        />
        <p class="progress-text">{{ progressText }}</p>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button @click="clearFiles" v-if="fileList.length > 0">清空</el-button>
        <el-button
          type="primary"
          @click="submitUpload"
          :loading="uploading"
          :disabled="fileList.length === 0"
        >
          开始上传
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadStatus = ref('')
const uploadRef = ref()
const fileList = ref([])

const uploadForm = reactive({
  category: 'other',
  permission: 'public',
  description: ''
})

const uploadUrl = computed(() => {
  return process.env.VUE_APP_API_BASE_URL + '/api/admin/files/upload'
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('admin_token')
  return {
    'Authorization': `Bearer ${token}`
  }
})

const uploadData = computed(() => {
  return {
    category: uploadForm.category,
    permission: uploadForm.permission,
    description: uploadForm.description
  }
})

const progressText = computed(() => {
  if (uploadProgress.value === 100) {
    return '上传完成'
  } else if (uploading.value) {
    return `上传中... ${uploadProgress.value}%`
  }
  return ''
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (!val) {
    resetForm()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const resetForm = () => {
  fileList.value = []
  uploadProgress.value = 0
  uploadStatus.value = ''
  uploading.value = false
  Object.assign(uploadForm, {
    category: 'other',
    permission: 'public',
    description: ''
  })
  uploadRef.value?.clearFiles()
}

const beforeUpload = (file) => {
  const isValidType = /\.(jpg|jpeg|png|gif|pdf|doc|docx|xls|xlsx)$/i.test(file.name)
  const isValidSize = file.size / 1024 / 1024 < 10

  if (!isValidType) {
    ElMessage.error('文件格式不支持')
    return false
  }
  if (!isValidSize) {
    ElMessage.error('文件大小不能超过 10MB')
    return false
  }

  // 自动设置分类
  if (/\.(jpg|jpeg|png|gif)$/i.test(file.name)) {
    uploadForm.category = 'image'
  } else if (/\.(pdf|doc|docx)$/i.test(file.name)) {
    uploadForm.category = 'document'
  } else if (/\.(xls|xlsx)$/i.test(file.name)) {
    uploadForm.category = 'document'
  }

  return true
}

const handleSuccess = (response, file) => {
  ElMessage.success(`${file.name} 上传成功`)
  uploadProgress.value = 100
  uploadStatus.value = 'success'
  
  setTimeout(() => {
    emit('success', response)
    handleClose()
  }, 1000)
}

const handleError = (error, file) => {
  ElMessage.error(`${file.name} 上传失败`)
  uploadStatus.value = 'exception'
  uploading.value = false
}

const handleProgress = (event, file) => {
  uploadProgress.value = Math.round(event.percent)
}

const submitUpload = () => {
  if (fileList.value.length === 0) {
    ElMessage.warning('请先选择文件')
    return
  }

  uploading.value = true
  uploadProgress.value = 0
  uploadStatus.value = ''
  uploadRef.value.submit()
}

const clearFiles = () => {
  uploadRef.value.clearFiles()
  fileList.value = []
}

const handleClose = () => {
  if (uploading.value) {
    ElMessage.warning('文件上传中，请稍后再关闭')
    return
  }
  visible.value = false
}
</script>

<style scoped>
.upload-container {
  padding: 20px 0;
}

.upload-demo {
  margin-bottom: 20px;
}

.upload-options {
  margin-top: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.progress-container {
  margin-top: 20px;
  text-align: center;
}

.progress-text {
  margin-top: 10px;
  color: #606266;
  font-size: 14px;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-upload-dragger) {
  width: 100%;
}
</style>
