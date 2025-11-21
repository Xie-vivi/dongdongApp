<template>
  <FormDialog
    v-model="visible"
    :title="isEdit ? '编辑配置' : '新增配置'"
    :fields="formFields"
    :form-data="formData"
    :loading="loading"
    @confirm="handleConfirm"
    @cancel="handleCancel"
    @field-change="handleFieldChange"
  >
    <!-- 自定义配置值字段 -->
    <template #field-value="{ field }">
      <div class="config-value-field">
        <!-- 根据数据类型显示不同的输入控件 -->
        <el-input
          v-if="formData.dataType === 'string'"
          v-model="formData.value"
          type="textarea"
          :rows="4"
          placeholder="请输入配置值"
          maxlength="1000"
          show-word-limit
        />
        <el-input-number
          v-else-if="formData.dataType === 'number'"
          v-model="formData.value"
          :precision="2"
          :step="0.1"
          placeholder="请输入数字"
          style="width: 100%"
        />
        <el-switch
          v-else-if="formData.dataType === 'boolean'"
          v-model="formData.value"
          active-text="true"
          inactive-text="false"
        />
        <el-input
          v-else-if="formData.dataType === 'json' || formData.dataType === 'array'"
          v-model="formData.value"
          type="textarea"
          :rows="6"
          placeholder="请输入有效的JSON格式数据"
          @blur="validateJson"
        />
        <el-input
          v-else
          v-model="formData.value"
          placeholder="请输入配置值"
        />
        
        <!-- JSON格式验证提示 -->
        <div v-if="jsonError" class="json-error">
          <el-icon><Warning /></el-icon>
          {{ jsonError }}
        </div>
        
        <!-- 值预览 -->
        <div v-if="formData.value && !jsonError" class="value-preview">
          <div class="preview-label">预览：</div>
          <div class="preview-content">{{ formatPreviewValue(formData.value, formData.dataType) }}</div>
        </div>
      </div>
    </template>
    
    <!-- 自定义使用说明字段 -->
    <template #field-usage="{ field }">
      <div class="usage-field">
        <el-input
          v-model="formData.usage"
          type="textarea"
          :rows="4"
          placeholder="请输入配置的使用说明（支持HTML格式）"
          maxlength="2000"
          show-word-limit
        />
        <div class="usage-tip">
          <el-icon><InfoFilled /></el-icon>
          支持 HTML 格式，可以添加链接、列表等富文本内容
        </div>
      </div>
    </template>
  </FormDialog>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Warning, InfoFilled } from '@element-plus/icons-vue'
import FormDialog from '@/components/admin/FormDialog.vue'
import { createSystemConfig, updateSystemConfig } from '@/api/admin'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  config: {
    type: Object,
    default: null
  },
  categories: {
    type: Array,
    default: () => []
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue', 'success'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 是否为编辑模式
const isEdit = computed(() => !!props.config?.id)

// 加载状态
const loading = ref(false)

// JSON验证错误
const jsonError = ref('')

// 表单数据
const formData = reactive({
  category: '',
  key: '',
  value: '',
  dataType: 'string',
  description: '',
  usage: '',
  status: 'active'
})

// 表单字段配置
const formFields = computed(() => [
  {
    prop: 'category',
    label: '配置分类',
    type: 'select',
    placeholder: '请选择配置分类',
    required: true,
    options: computed(() => [
      ...props.categories.map(cat => ({ label: cat.name, value: cat.key }))
    ]),
    rules: [
      { required: true, message: '请选择配置分类', trigger: 'change' }
    ]
  },
  {
    prop: 'key',
    label: '配置键',
    type: 'input',
    placeholder: '请输入配置键',
    required: true,
    rules: [
      { required: true, message: '请输入配置键', trigger: 'blur' },
      { min: 2, max: 100, message: '配置键长度在 2 到 100 个字符', trigger: 'blur' },
      { pattern: /^[a-zA-Z][a-zA-Z0-9._-]*$/, message: '配置键只能包含字母、数字、点、下划线和连字符，且必须以字母开头', trigger: 'blur' }
    ]
  },
  {
    prop: 'dataType',
    label: '数据类型',
    type: 'select',
    placeholder: '请选择数据类型',
    required: true,
    options: [
      { label: '字符串', value: 'string' },
      { label: '数字', value: 'number' },
      { label: '布尔值', value: 'boolean' },
      { label: 'JSON', value: 'json' },
      { label: '数组', value: 'array' }
    ],
    rules: [
      { required: true, message: '请选择数据类型', trigger: 'change' }
    ]
  },
  {
    prop: 'value',
    label: '配置值',
    type: 'slot',
    required: true,
    rules: [
      { required: true, message: '请输入配置值', trigger: 'blur' },
      { validator: validateValue, trigger: 'blur' }
    ]
  },
  {
    prop: 'description',
    label: '配置描述',
    type: 'textarea',
    placeholder: '请输入配置描述',
    rows: 3,
    maxlength: 500,
    showWordLimit: true,
    rules: [
      { required: true, message: '请输入配置描述', trigger: 'blur' }
    ]
  },
  {
    prop: 'usage',
    label: '使用说明',
    type: 'slot'
  },
  {
    prop: 'status',
    label: '状态',
    type: 'select',
    options: [
      { label: '启用', value: 'active' },
      { label: '禁用', value: 'inactive' }
    ]
  }
])

// 监听配置数据变化
watch(
  () => props.config,
  (newConfig) => {
    if (newConfig) {
      // 编辑模式，填充表单数据
      Object.assign(formData, {
        category: newConfig.category || '',
        key: newConfig.key || '',
        value: newConfig.value || '',
        dataType: newConfig.dataType || 'string',
        description: newConfig.description || '',
        usage: newConfig.usage || '',
        status: newConfig.status || 'active'
      })
    } else {
      // 新增模式，重置表单数据
      resetFormData()
    }
  },
  { immediate: true }
)

// 监听数据类型变化
watch(
  () => formData.dataType,
  (newType) => {
    // 当数据类型改变时，清空值和错误
    if (!isEdit.value) {
      formData.value = getDefaultValueType(newType)
    }
    jsonError.value = ''
  }
)

// 重置表单数据
const resetFormData = () => {
  Object.assign(formData, {
    category: '',
    key: '',
    value: '',
    dataType: 'string',
    description: '',
    usage: '',
    status: 'active'
  })
  jsonError.value = ''
}

// 获取默认值类型
const getDefaultValueType = (dataType) => {
  const defaultValues = {
    string: '',
    number: 0,
    boolean: false,
    json: '{}',
    array: '[]'
  }
  return defaultValues[dataType] || ''
}

// 验证JSON格式
const validateJson = () => {
  if (formData.dataType === 'json' || formData.dataType === 'array') {
    try {
      JSON.parse(formData.value)
      jsonError.value = ''
      return true
    } catch (error) {
      jsonError.value = `JSON格式错误: ${error.message}`
      return false
    }
  }
  return true
}

// 验证配置值
function validateValue(rule, value, callback) {
  if (!value && value !== 0 && value !== false) {
    callback(new Error('请输入配置值'))
    return
  }
  
  // 根据数据类型验证
  switch (formData.dataType) {
    case 'json':
    case 'array':
      if (!validateJson()) {
        callback(new Error('请输入有效的JSON格式'))
        return
      }
      break
  }
  
  callback()
}

// 格式化预览值
const formatPreviewValue = (value, dataType) => {
  if (!value) return ''
  
  switch (dataType) {
    case 'json':
    case 'array':
      try {
        const parsed = JSON.parse(value)
        return JSON.stringify(parsed, null, 2)
      } catch {
        return value
      }
    case 'boolean':
      return value ? 'true' : 'false'
    default:
      return value
  }
}

// 处理字段变化
const handleFieldChange = (prop, value) => {
  if (prop === 'dataType') {
    // 数据类型变化时的处理
    if (!isEdit.value) {
      formData.value = getDefaultValueType(value)
    }
    jsonError.value = ''
  }
}

// 处理确认
const handleConfirm = async (data) => {
  try {
    // 验证JSON格式
    if (formData.dataType === 'json' || formData.dataType === 'array') {
      if (!validateJson()) {
        ElMessage.error('请输入有效的JSON格式数据')
        return
      }
    }
    
    loading.value = true
    
    // 构建提交数据
    const submitData = { ...data }
    
    let response
    if (isEdit.value) {
      // 编辑配置
      response = await updateSystemConfig(props.config.id, submitData)
      ElMessage.success('更新配置成功')
    } else {
      // 新增配置
      response = await createSystemConfig(submitData)
      ElMessage.success('创建配置成功')
    }
    
    emit('success', response.data)
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error(error.message || '保存配置失败')
  } finally {
    loading.value = false
  }
}

// 处理取消
const handleCancel = () => {
  visible.value = false
}
</script>

<style scoped>
.config-value-field {
  width: 100%;
}

.json-error {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #fef0f0;
  border: 1px solid #fbc4c4;
  border-radius: 4px;
  color: #f56c6c;
  font-size: 12px;
}

.value-preview {
  margin-top: 12px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.preview-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.preview-content {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  color: #303133;
  background: #fff;
  padding: 8px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 200px;
  overflow-y: auto;
}

.usage-field {
  width: 100%;
}

.usage-tip {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  padding: 8px 12px;
  background: #f0f9ff;
  border: 1px solid #bfdbfe;
  border-radius: 4px;
  color: #409eff;
  font-size: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .json-error,
  .usage-tip {
    font-size: 11px;
  }
  
  .preview-content {
    font-size: 11px;
    max-height: 150px;
  }
}

/* 动画效果 */
.json-error {
  animation: shake 0.3s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}
</style>
