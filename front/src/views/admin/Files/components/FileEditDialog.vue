<template>
  <el-dialog
    v-model="visible"
    title="编辑文件信息"
    width="600px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      v-loading="loading"
    >
      <el-form-item label="文件名" prop="name">
        <el-input v-model="form.name" placeholder="请输入文件名" />
      </el-form-item>

      <el-form-item label="文件分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
          <el-option label="图片" value="image" />
          <el-option label="文档" value="document" />
          <el-option label="视频" value="video" />
          <el-option label="音频" value="audio" />
          <el-option label="其他" value="other" />
        </el-select>
      </el-form-item>

      <el-form-item label="访问权限" prop="permission">
        <el-radio-group v-model="form.permission">
          <el-radio label="public">公开</el-radio>
          <el-radio label="private">私有</el-radio>
          <el-radio label="protected">受保护</el-radio>
        </el-radio-group>
        <div class="form-tip">
          <p>• 公开：所有用户都可以访问</p>
          <p>• 私有：仅上传者可以访问</p>
          <p>• 受保护：需要特定权限才能访问</p>
        </div>
      </el-form-item>

      <el-form-item label="文件描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请输入文件描述"
        />
      </el-form-item>

      <el-form-item label="标签" prop="tags">
        <el-select
          v-model="form.tags"
          multiple
          filterable
          allow-create
          placeholder="请选择或输入标签"
          style="width: 100%"
        >
          <el-option
            v-for="tag in availableTags"
            :key="tag"
            :label="tag"
            :value="tag"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="文件状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
          <el-option label="正常" value="active" />
          <el-option label="隐藏" value="hidden" />
          <el-option label="已删除" value="deleted" />
        </el-select>
      </el-form-item>

      <el-divider content-position="left">文件信息</el-divider>
      
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="文件大小">
            <el-input :value="formatFileSize(file?.size)" readonly />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="文件类型">
            <el-input :value="file?.mimeType" readonly />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="上传时间">
            <el-input :value="formatDate(file?.createdAt)" readonly />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="下载次数">
            <el-input :value="file?.downloadCount || 0" readonly />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="文件路径">
        <el-input :value="file?.url" readonly>
          <template #append>
            <el-button @click="copyUrl" :icon="Link">复制</el-button>
          </template>
        </el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          保存
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Link } from '@element-plus/icons-vue'

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

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  name: '',
  category: '',
  permission: 'public',
  description: '',
  tags: [],
  status: 'active'
})

const rules = {
  name: [
    { required: true, message: '请输入文件名', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择文件分类', trigger: 'change' }
  ],
  permission: [
    { required: true, message: '请选择访问权限', trigger: 'change' }
  ]
}

const availableTags = ref([
  '重要', '临时', '备份', '草稿', '已审核', 
  '公开', '内部', '机密', '归档', '待处理'
])

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.file) {
    nextTick(() => {
      Object.assign(form, {
        name: props.file.name || '',
        category: props.file.category || '',
        permission: props.file.permission || 'public',
        description: props.file.description || '',
        tags: props.file.tags || [],
        status: props.file.status || 'active'
      })
    })
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

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

const copyUrl = async () => {
  if (!props.file?.url) return
  
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
  formRef.value?.clearValidate()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('文件信息更新成功')
    emit('success', { ...form, id: props.file?.id })
    handleClose()
  } catch (error) {
    console.error('更新失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.form-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.form-tip p {
  margin: 2px 0;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}
</style>
