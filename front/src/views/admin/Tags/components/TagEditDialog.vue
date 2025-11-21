<template>
  <el-dialog
    v-model="visible"
    :title="tag ? '编辑标签' : '新建标签'"
    width="500px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      v-loading="loading"
    >
      <el-form-item label="标签名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入标签名称" />
      </el-form-item>

      <el-form-item label="标签颜色" prop="color">
        <div class="color-picker-container">
          <el-color-picker v-model="form.color" />
          <el-input
            v-model="form.color"
            placeholder="#000000"
            style="margin-left: 10px; flex: 1;"
          />
        </div>
      </el-form-item>

      <el-form-item label="标签分类" prop="category">
        <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
          <el-option label="内容标签" value="content" />
          <el-option label="用户标签" value="user" />
          <el-option label="活动标签" value="activity" />
          <el-option label="系统标签" value="system" />
          <el-option label="自定义标签" value="custom" />
        </el-select>
      </el-form-item>

      <el-form-item label="标签描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="3"
          placeholder="请输入标签描述"
        />
      </el-form-item>

      <el-form-item label="排序权重" prop="sort">
        <el-input-number
          v-model="form.sort"
          :min="0"
          :max="999"
          placeholder="数值越大排序越靠前"
          style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="标签状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="active">启用</el-radio>
          <el-radio label="inactive">禁用</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="是否推荐" prop="isRecommended">
        <el-switch
          v-model="form.isRecommended"
          active-text="推荐"
          inactive-text="普通"
        />
        <div class="form-tip">推荐标签会在标签选择时优先显示</div>
      </el-form-item>

      <el-form-item label="使用限制" prop="usageLimit">
        <el-radio-group v-model="form.usageLimit">
          <el-radio label="public">公开使用</el-radio>
          <el-radio label="admin">仅管理员</el-radio>
          <el-radio label="vip">VIP用户</el-radio>
        </el-radio-group>
      </el-form-item>

      <div v-if="tag" class="tag-stats">
        <el-divider content-position="left">使用统计</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-statistic title="使用次数" :value="tag.usageCount || 0" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="关联内容" :value="tag.contentCount || 0" />
          </el-col>
          <el-col :span="8">
            <el-statistic title="创建时间" :value="formatDate(tag.createdAt)" />
          </el-col>
        </el-row>
      </div>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ tag ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  tag: {
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
  color: '#409EFF',
  category: 'custom',
  description: '',
  sort: 0,
  status: 'active',
  isRecommended: false,
  usageLimit: 'public'
})

const rules = {
  name: [
    { required: true, message: '请输入标签名称', trigger: 'blur' },
    { min: 1, max: 20, message: '标签名称长度在 1 到 20 个字符', trigger: 'blur' }
  ],
  color: [
    { required: true, message: '请选择标签颜色', trigger: 'change' },
    { pattern: /^#[0-9A-Fa-f]{6}$/, message: '请输入正确的颜色值', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择标签分类', trigger: 'change' }
  ]
}

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.tag) {
    nextTick(() => {
      Object.assign(form, {
        name: props.tag.name || '',
        color: props.tag.color || '#409EFF',
        category: props.tag.category || 'custom',
        description: props.tag.description || '',
        sort: props.tag.sort || 0,
        status: props.tag.status || 'active',
        isRecommended: props.tag.isRecommended || false,
        usageLimit: props.tag.usageLimit || 'public'
      })
    })
  } else if (val) {
    resetForm()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const resetForm = () => {
  Object.assign(form, {
    name: '',
    color: '#409EFF',
    category: 'custom',
    description: '',
    sort: 0,
    status: 'active',
    isRecommended: false,
    usageLimit: 'public'
  })
  formRef.value?.clearValidate()
}

const formatDate = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleClose = () => {
  visible.value = false
  resetForm()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success(props.tag ? '标签更新成功' : '标签创建成功')
    emit('success', { ...form, id: props.tag?.id })
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.color-picker-container {
  display: flex;
  align-items: center;
}

.form-tip {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}

.tag-stats {
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-statistic__content) {
  font-size: 14px;
}

:deep(.el-statistic__title) {
  font-size: 12px;
}
</style>
