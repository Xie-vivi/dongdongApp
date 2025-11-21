<template>
  <div class="search-form">
    <el-form
      ref="formRef"
      :model="formData"
      :inline="inline"
      :label-width="labelWidth"
      :size="size"
      @submit.prevent="handleSearch"
    >
      <div class="form-content">
        <!-- 搜索字段 -->
        <template v-for="field in fields" :key="field.prop">
          <!-- 输入框 -->
          <el-form-item
            v-if="field.type === 'input'"
            :label="field.label"
            :prop="field.prop"
          >
            <el-input
              v-model="formData[field.prop]"
              :placeholder="field.placeholder || `请输入${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          
          <!-- 选择器 -->
          <el-form-item
            v-else-if="field.type === 'select'"
            :label="field.label"
            :prop="field.prop"
          >
            <el-select
              v-model="formData[field.prop]"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              :multiple="field.multiple"
              :filterable="field.filterable"
              style="width: 100%"
            >
              <el-option
                v-for="option in field.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
                :disabled="option.disabled"
              />
            </el-select>
          </el-form-item>
          
          <!-- 日期选择器 -->
          <el-form-item
            v-else-if="field.type === 'date'"
            :label="field.label"
            :prop="field.prop"
          >
            <el-date-picker
              v-model="formData[field.prop]"
              :type="field.dateType || 'date'"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              :format="field.format"
              :value-format="field.valueFormat"
              style="width: 100%"
            />
          </el-form-item>
          
          <!-- 日期范围选择器 -->
          <el-form-item
            v-else-if="field.type === 'daterange'"
            :label="field.label"
            :prop="field.prop"
          >
            <el-date-picker
              v-model="formData[field.prop]"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              :format="field.format"
              :value-format="field.valueFormat"
              style="width: 100%"
            />
          </el-form-item>
          
          <!-- 数字输入框 -->
          <el-form-item
            v-else-if="field.type === 'number'"
            :label="field.label"
            :prop="field.prop"
          >
            <el-input-number
              v-model="formData[field.prop]"
              :placeholder="field.placeholder || `请输入${field.label}`"
              :disabled="field.disabled"
              :min="field.min"
              :max="field.max"
              :step="field.step"
              :precision="field.precision"
              style="width: 100%"
            />
          </el-form-item>
          
          <!-- 级联选择器 -->
          <el-form-item
            v-else-if="field.type === 'cascader'"
            :label="field.label"
            :prop="field.prop"
          >
            <el-cascader
              v-model="formData[field.prop]"
              :options="field.options"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled"
              :filterable="field.filterable"
              style="width: 100%"
            />
          </el-form-item>
          
          <!-- 自定义插槽 -->
          <el-form-item
            v-else-if="field.type === 'slot'"
            :label="field.label"
            :prop="field.prop"
          >
            <slot :name="`field-${field.prop}`" :field="field" :form-data="formData" />
          </el-form-item>
        </template>
        
        <!-- 操作按钮 -->
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleSearch"
          >
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
          <el-button
            v-if="showExpand && hasHiddenFields"
            type="text"
            @click="toggleExpand"
          >
            {{ expanded ? '收起' : '展开' }}
            <el-icon>
              <ArrowUp v-if="expanded" />
              <ArrowDown v-else />
            </el-icon>
          </el-button>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { Search, RefreshLeft, ArrowUp, ArrowDown } from '@element-plus/icons-vue'

// Props 定义
const props = defineProps({
  // 表单字段配置
  fields: {
    type: Array,
    required: true
  },
  
  // 表单配置
  inline: {
    type: Boolean,
    default: true
  },
  labelWidth: {
    type: String,
    default: '80px'
  },
  size: {
    type: String,
    default: 'default'
  },
  
  // 功能配置
  loading: {
    type: Boolean,
    default: false
  },
  showExpand: {
    type: Boolean,
    default: false
  },
  defaultExpandedRows: {
    type: Number,
    default: 2
  },
  
  // 初始数据
  modelValue: {
    type: Object,
    default: () => ({})
  }
})

// Emits 定义
const emit = defineEmits([
  'search',
  'reset',
  'update:modelValue',
  'field-change'
])

// 表单引用
const formRef = ref(null)

// 表单数据
const formData = reactive({})

// 展开状态
const expanded = ref(false)

// 初始化表单数据
const initFormData = () => {
  props.fields.forEach(field => {
    if (formData[field.prop] === undefined) {
      if (field.defaultValue !== undefined) {
        formData[field.prop] = field.defaultValue
      } else {
        // 根据字段类型设置默认值
        switch (field.type) {
          case 'select':
            formData[field.prop] = field.multiple ? [] : ''
            break
          case 'daterange':
            formData[field.prop] = []
            break
          case 'number':
            formData[field.prop] = null
            break
          default:
            formData[field.prop] = ''
        }
      }
    }
  })
}

// 计算是否有隐藏字段
const hasHiddenFields = computed(() => {
  return props.showExpand && props.fields.length > props.defaultExpandedRows
})

// 计算显示的字段
const visibleFields = computed(() => {
  if (!props.showExpand) return props.fields
  
  if (expanded.value) {
    return props.fields
  } else {
    return props.fields.slice(0, props.defaultExpandedRows)
  }
})

// 监听表单数据变化
watch(
  () => formData,
  (newVal) => {
    emit('update:modelValue', { ...newVal })
  },
  { deep: true }
)

// 监听外部数据变化
watch(
  () => props.modelValue,
  (newVal) => {
    Object.assign(formData, newVal)
  },
  { deep: true }
)

// 处理搜索
const handleSearch = () => {
  emit('search', { ...formData })
}

// 处理重置
const handleReset = () => {
  formRef.value?.resetFields()
  initFormData()
  emit('reset')
  emit('search', { ...formData })
}

// 切换展开状态
const toggleExpand = () => {
  expanded.value = !expanded.value
}

// 获取表单数据
const getFormData = () => {
  return { ...formData }
}

// 设置表单数据
const setFormData = (data) => {
  Object.assign(formData, data)
}

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields()
  initFormData()
}

// 验证表单
const validateForm = () => {
  return formRef.value?.validate()
}

// 暴露方法
defineExpose({
  formData,
  getFormData,
  setFormData,
  resetForm,
  validateForm,
  formRef
})

// 组件挂载时初始化
onMounted(() => {
  initFormData()
  
  // 如果有初始值，设置到表单中
  if (props.modelValue && Object.keys(props.modelValue).length > 0) {
    Object.assign(formData, props.modelValue)
  }
})
</script>

<style scoped>
.search-form {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.form-content {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  align-items: flex-end;
}

/* 表单项样式 */
:deep(.el-form-item) {
  margin-bottom: 0;
  flex-shrink: 0;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input),
:deep(.el-select),
:deep(.el-date-picker) {
  width: 200px;
}

:deep(.el-input-number) {
  width: 200px;
}

:deep(.el-cascader) {
  width: 200px;
}

/* 按钮样式 */
:deep(.el-button) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-date-picker),
  :deep(.el-input-number),
  :deep(.el-cascader) {
    width: 180px;
  }
}

@media (max-width: 768px) {
  .search-form {
    padding: 16px;
  }
  
  .form-content {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  :deep(.el-form-item) {
    width: 100%;
  }
  
  :deep(.el-input),
  :deep(.el-select),
  :deep(.el-date-picker),
  :deep(.el-input-number),
  :deep(.el-cascader) {
    width: 100%;
  }
  
  :deep(.el-form-item__label) {
    width: 80px !important;
    text-align: left;
  }
}

/* 动画效果 */
.search-form {
  transition: all 0.3s ease;
}

.form-content {
  transition: all 0.3s ease;
}

/* 展开收起动画 */
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
