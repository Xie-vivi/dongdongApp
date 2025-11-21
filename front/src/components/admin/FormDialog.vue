<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    :fullscreen="fullscreen"
    :top="top"
    :modal="modal"
    :modal-class="modalClass"
    :append-to-body="appendToBody"
    :close-on-click-modal="closeOnClickModal"
    :close-on-press-escape="closeOnPressEscape"
    :show-close="showClose"
    :before-close="handleBeforeClose"
    :destroy-on-close="destroyOnClose"
    @open="handleOpen"
    @opened="handleOpened"
    @close="handleClose"
    @closed="handleClosed"
  >
    <!-- 表单内容 -->
    <el-form
      ref="formRef"
      :model="internalFormData"
      :rules="rules"
      :label-width="labelWidth"
      :label-position="labelPosition"
      :size="size"
      :disabled="disabled"
      :hide-required-asterisk="hideRequiredAsterisk"
      :show-message="showMessage"
      :inline-message="inlineMessage"
      :status-icon="statusIcon"
      :validate-on-rule-change="validateOnRuleChange"
    >
      <div class="form-content">
        <!-- 表单字段 -->
        <template v-for="field in fields" :key="field.prop">
          <!-- 输入框 -->
          <el-form-item
            v-if="field.type === 'input'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-input
              v-model="internalFormData[field.prop]"
              :type="field.inputType || 'text'"
              :placeholder="field.placeholder || `请输入${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :readonly="field.readonly"
              :maxlength="field.maxlength"
              :show-word-limit="field.showWordLimit"
              :rows="field.rows"
              :autosize="field.autosize"
              @blur="handleFieldBlur(field.prop, $event)"
              @focus="handleFieldFocus(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 文本域 -->
          <el-form-item
            v-else-if="field.type === 'textarea'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-input
              v-model="internalFormData[field.prop]"
              type="textarea"
              :placeholder="field.placeholder || `请输入${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :readonly="field.readonly"
              :maxlength="field.maxlength"
              :show-word-limit="field.showWordLimit"
              :rows="field.rows || 4"
              :autosize="field.autosize"
              @blur="handleFieldBlur(field.prop, $event)"
              @focus="handleFieldFocus(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 密码输入框 -->
          <el-form-item
            v-else-if="field.type === 'password'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-input
              v-model="internalFormData[field.prop]"
              type="password"
              :placeholder="field.placeholder || `请输入${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :readonly="field.readonly"
              :show-password="field.showPassword !== false"
              @blur="handleFieldBlur(field.prop, $event)"
              @focus="handleFieldFocus(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 数字输入框 -->
          <el-form-item
            v-else-if="field.type === 'number'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-input-number
              v-model="internalFormData[field.prop]"
              :placeholder="field.placeholder || `请输入${field.label}`"
              :disabled="field.disabled || disabled"
              :readonly="field.readonly"
              :min="field.min"
              :max="field.max"
              :step="field.step"
              :step-strictly="field.stepStrictly"
              :precision="field.precision"
              :controls-position="field.controlsPosition"
              style="width: 100%"
              @blur="handleFieldBlur(field.prop, $event)"
              @focus="handleFieldFocus(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 选择器 -->
          <el-form-item
            v-else-if="field.type === 'select'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-select
              v-model="internalFormData[field.prop]"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :multiple="field.multiple"
              :filterable="field.filterable"
              :allow-create="field.allowCreate"
              :remote="field.remote"
              :remote-method="field.remoteMethod"
              :loading="field.loading"
              :no-match-text="field.noMatchText"
              :no-data-text="field.noDataText"
              style="width: 100%"
              @change="handleFieldChange(field.prop, $event)"
            >
              <el-option
                v-for="option in field.options"
                :key="option.value"
                :label="option.label"
                :value="option.value"
                :disabled="option.disabled"
              >
                <slot v-if="field.optionSlot" :name="`option-${field.prop}`" :option="option" />
              </el-option>
            </el-select>
          </el-form-item>
          
          <!-- 单选框组 -->
          <el-form-item
            v-else-if="field.type === 'radio'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-radio-group
              v-model="internalFormData[field.prop]"
              :disabled="field.disabled || disabled"
              :size="field.size"
              :text-color="field.textColor"
              :fill="field.fill"
              @change="handleFieldChange(field.prop, $event)"
            >
              <el-radio
                v-for="option in field.options"
                :key="option.value"
                :label="option.value"
                :disabled="option.disabled"
                :border="field.border"
              >
                {{ option.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
          
          <!-- 复选框组 -->
          <el-form-item
            v-else-if="field.type === 'checkbox'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-checkbox-group
              v-model="internalFormData[field.prop]"
              :disabled="field.disabled || disabled"
              :size="field.size"
              :min="field.min"
              :max="field.max"
              :text-color="field.textColor"
              :fill="field.fill"
              @change="handleFieldChange(field.prop, $event)"
            >
              <el-checkbox
                v-for="option in field.options"
                :key="option.value"
                :label="option.value"
                :disabled="option.disabled"
                :border="field.border"
                :true-label="option.trueLabel"
                :false-label="option.falseLabel"
              >
                {{ option.label }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
          
          <!-- 开关 -->
          <el-form-item
            v-else-if="field.type === 'switch'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-switch
              v-model="internalFormData[field.prop]"
              :disabled="field.disabled || disabled"
              :size="field.size"
              :active-icon="field.activeIcon"
              :inactive-icon="field.inactiveIcon"
              :active-text="field.activeText"
              :inactive-text="field.inactiveText"
              :active-value="field.activeValue"
              :inactive-value="field.inactiveValue"
              :active-color="field.activeColor"
              :inactive-color="field.inactiveColor"
              :width="field.width"
              @change="handleFieldChange(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 滑块 -->
          <el-form-item
            v-else-if="field.type === 'slider'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-slider
              v-model="internalFormData[field.prop]"
              :disabled="field.disabled || disabled"
              :min="field.min"
              :max="field.max"
              :step="field.step"
              :show-stops="field.showStops"
              :show-tooltip="field.showTooltip"
              :range="field.range"
              :vertical="field.vertical"
              :height="field.height"
              :marks="field.marks"
              @change="handleFieldChange(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 日期选择器 -->
          <el-form-item
            v-else-if="field.type === 'date'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-date-picker
              v-model="internalFormData[field.prop]"
              :type="field.dateType || 'date'"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :format="field.format"
              :value-format="field.valueFormat"
              :readonly="field.readonly"
              :editable="field.editable"
              style="width: 100%"
              @change="handleFieldChange(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 日期范围选择器 -->
          <el-form-item
            v-else-if="field.type === 'daterange'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-date-picker
              v-model="internalFormData[field.prop]"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :format="field.format"
              :value-format="field.valueFormat"
              :readonly="field.readonly"
              :editable="field.editable"
              style="width: 100%"
              @change="handleFieldChange(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 时间选择器 -->
          <el-form-item
            v-else-if="field.type === 'time'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-time-picker
              v-model="internalFormData[field.prop]"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :format="field.format"
              :value-format="field.valueFormat"
              :readonly="field.readonly"
              style="width: 100%"
              @change="handleFieldChange(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 时间范围选择器 -->
          <el-form-item
            v-else-if="field.type === 'timerange'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-time-picker
              v-model="internalFormData[field.prop]"
              is-range
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :format="field.format"
              :value-format="field.valueFormat"
              :readonly="field.readonly"
              style="width: 100%"
              @change="handleFieldChange(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 图片上传 -->
          <el-form-item
            v-else-if="field.type === 'image'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-upload
              :action="field.action || '/api/upload'"
              :headers="field.headers || {}"
              :data="field.data || {}"
              :name="field.name || 'file'"
              :with-credentials="field.withCredentials"
              :multiple="field.multiple"
              :accept="field.accept || 'image/*'"
              :list-type="field.listType || 'picture-card'"
              :auto-upload="field.autoUpload !== false"
              :disabled="field.disabled || disabled"
              :limit="field.limit"
              :file-list="internalFormData[field.prop] || []"
              :on-preview="handlePicturePreview"
              :on-remove="(file, fileList) => handleRemove(field.prop, file, fileList)"
              :on-success="(response, file, fileList) => handleSuccess(field.prop, response, file, fileList)"
              :on-error="handleError"
              :on-progress="handleProgress"
              :on-change="(file, fileList) => handleChange(field.prop, file, fileList)"
              :before-upload="field.beforeUpload"
              :before-remove="field.beforeRemove"
              :http-request="field.httpRequest"
            >
              <el-icon><Plus /></el-icon>
              <template #tip>
                <div class="el-upload__tip" v-if="field.tip">
                  {{ field.tip }}
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <!-- 文件上传 -->
          <el-form-item
            v-else-if="field.type === 'file'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-upload
              :action="field.action || '/api/upload'"
              :headers="field.headers || {}"
              :data="field.data || {}"
              :name="field.name || 'file'"
              :with-credentials="field.withCredentials"
              :multiple="field.multiple"
              :accept="field.accept"
              :list-type="field.listType || 'text'"
              :auto-upload="field.autoUpload !== false"
              :disabled="field.disabled || disabled"
              :limit="field.limit"
              :file-list="internalFormData[field.prop] || []"
              :on-remove="(file, fileList) => handleRemove(field.prop, file, fileList)"
              :on-success="(response, file, fileList) => handleSuccess(field.prop, response, file, fileList)"
              :on-error="handleError"
              :on-change="(file, fileList) => handleChange(field.prop, file, fileList)"
              :before-upload="field.beforeUpload"
              :before-remove="field.beforeRemove"
              :http-request="field.httpRequest"
            >
              <el-button type="primary">
                <el-icon><Upload /></el-icon>
                点击上传
              </el-button>
              <template #tip>
                <div class="el-upload__tip" v-if="field.tip">
                  {{ field.tip }}
                </div>
              </template>
            </el-upload>
          </el-form-item>
          
          <!-- 级联选择器 -->
          <el-form-item
            v-else-if="field.type === 'cascader'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <el-cascader
              v-model="internalFormData[field.prop]"
              :options="field.options"
              :placeholder="field.placeholder || `请选择${field.label}`"
              :clearable="field.clearable !== false"
              :disabled="field.disabled || disabled"
              :filterable="field.filterable"
              :props="field.props"
              :separator="field.separator"
              :show-all-levels="field.showAllLevels !== false"
              :collapse-tags="field.collapseTags"
              :collapse-tags-tooltip="field.collapseTagsTooltip"
              :max-collapse-tags="field.maxCollapseTags"
              style="width: 100%"
              @change="handleFieldChange(field.prop, $event)"
            />
          </el-form-item>
          
          <!-- 自定义插槽 -->
          <el-form-item
            v-else-if="field.type === 'slot'"
            :label="field.label"
            :prop="field.prop"
            :required="field.required"
          >
            <slot :name="`field-${field.prop}`" :field="field" :form-data="internalFormData" />
          </el-form-item>
        </template>
      </div>
    </el-form>
    
    <!-- 底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <slot name="footer">
          <el-button @click="handleCancel">
            {{ cancelText }}
          </el-button>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleConfirm"
          >
            {{ confirmText }}
          </el-button>
        </slot>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, nextTick } from 'vue'
import { Plus, Upload } from '@element-plus/icons-vue'

// Props 定义
const props = defineProps({
  // 对话框配置
  modelValue: {
    type: Boolean,
    default: false
  },
  title: {
    type: String,
    default: '表单'
  },
  width: {
    type: String,
    default: '600px'
  },
  fullscreen: {
    type: Boolean,
    default: false
  },
  top: {
    type: String,
    default: '15vh'
  },
  modal: {
    type: Boolean,
    default: true
  },
  modalClass: String,
  appendToBody: {
    type: Boolean,
    default: true
  },
  closeOnClickModal: {
    type: Boolean,
    default: false
  },
  closeOnPressEscape: {
    type: Boolean,
    default: true
  },
  showClose: {
    type: Boolean,
    default: true
  },
  destroyOnClose: {
    type: Boolean,
    default: false
  },
  
  // 表单配置
  fields: {
    type: Array,
    required: true
  },
  labelWidth: {
    type: String,
    default: '100px'
  },
  labelPosition: {
    type: String,
    default: 'right'
  },
  size: {
    type: String,
    default: 'default'
  },
  disabled: {
    type: Boolean,
    default: false
  },
  hideRequiredAsterisk: {
    type: Boolean,
    default: false
  },
  showMessage: {
    type: Boolean,
    default: true
  },
  inlineMessage: {
    type: Boolean,
    default: false
  },
  statusIcon: {
    type: Boolean,
    default: false
  },
  validateOnRuleChange: {
    type: Boolean,
    default: true
  },
  
  // 按钮配置
  confirmText: {
    type: String,
    default: '确定'
  },
  cancelText: {
    type: String,
    default: '取消'
  },
  loading: {
    type: Boolean,
    default: false
  },
  
  // 数据配置
  formData: {
    type: Object,
    default: () => ({})
  }
})

// Emits 定义
const emit = defineEmits([
  'update:modelValue',
  'confirm',
  'cancel',
  'open',
  'opened',
  'close',
  'closed',
  'field-change',
  'field-blur',
  'field-focus'
])

// 表单引用
const formRef = ref(null)

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 内部表单数据
const internalFormData = reactive({})

// 表单验证规则
const rules = computed(() => {
  const rules = {}
  props.fields.forEach(field => {
    if (field.rules) {
      rules[field.prop] = field.rules
    }
  })
  return rules
})

// 初始化表单数据
const initFormData = () => {
  props.fields.forEach(field => {
    if (internalFormData[field.prop] === undefined) {
      if (props.formData && props.formData[field.prop] !== undefined) {
        internalFormData[field.prop] = props.formData[field.prop]
      } else if (field.defaultValue !== undefined) {
        internalFormData[field.prop] = field.defaultValue
      } else {
        // 根据字段类型设置默认值
        switch (field.type) {
          case 'checkbox':
          case 'select':
            internalFormData[field.prop] = field.multiple ? [] : ''
            break
          case 'switch':
            internalFormData[field.prop] = field.inactiveValue || false
            break
          case 'number':
            internalFormData[field.prop] = field.min || 0
            break
          case 'daterange':
          case 'timerange':
            internalFormData[field.prop] = []
            break
          case 'image':
          case 'file':
            internalFormData[field.prop] = []
            break
          default:
            internalFormData[field.prop] = ''
        }
      }
    }
  })
}

// 监听外部数据变化
watch(
  () => props.formData,
  (newVal) => {
    if (newVal) {
      Object.assign(internalFormData, newVal)
    }
  },
  { deep: true, immediate: true }
)

// 处理确认
const handleConfirm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    emit('confirm', { ...internalFormData })
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 处理取消
const handleCancel = () => {
  emit('cancel')
  visible.value = false
}

// 处理对话框关闭前
const handleBeforeClose = (done) => {
  emit('close')
  done()
}

// 处理对话框打开
const handleOpen = () => {
  emit('open')
}

// 处理对话框已打开
const handleOpened = () => {
  emit('opened')
}

// 处理对话框关闭
const handleClose = () => {
  emit('close')
}

// 处理对话框已关闭
const handleClosed = () => {
  emit('closed')
  // 重置表单
  resetForm()
}

// 处理字段变化
const handleFieldChange = (prop, value) => {
  emit('field-change', prop, value, internalFormData)
}

// 处理字段失焦
const handleFieldBlur = (prop, event) => {
  emit('field-blur', prop, event, internalFormData)
}

// 处理字段聚焦
const handleFieldFocus = (prop, event) => {
  emit('field-focus', prop, event, internalFormData)
}

// 处理图片预览
const handlePicturePreview = (file) => {
  // 可以使用 el-image 的预览功能
  console.log('预览图片:', file)
}

// 处理文件移除
const handleRemove = (prop, file, fileList) => {
  internalFormData[prop] = fileList
}

// 处理上传成功
const handleSuccess = (prop, response, file, fileList) => {
  internalFormData[prop] = fileList
}

// 处理上传错误
const handleError = (error, file, fileList) => {
  console.error('上传失败:', error)
}

// 处理上传进度
const handleProgress = (event, file, fileList) => {
  console.log('上传进度:', event, file)
}

// 处理文件变化
const handleChange = (prop, file, fileList) => {
  if (props.autoUpload === false) {
    internalFormData[prop] = fileList
  }
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

// 验证指定字段
const validateField = (prop) => {
  return formRef.value?.validateField(prop)
}

// 清除验证
const clearValidate = (props) => {
  formRef.value?.clearValidate(props)
}

// 设置表单数据
const setFormData = (data) => {
  Object.assign(internalFormData, data)
}

// 获取表单数据
const getFormData = () => {
  return { ...internalFormData }
}

// 暴露方法
defineExpose({
  internalFormData,
  formRef,
  resetForm,
  validateForm,
  validateField,
  clearValidate,
  setFormData,
  getFormData
})

// 组件挂载时初始化
initFormData()
</script>

<style scoped>
.form-content {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 12px;
}

.form-content::-webkit-scrollbar {
  width: 6px;
}

.form-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.form-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.dialog-footer {
  text-align: right;
}

/* 表单项样式优化 */
:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-input),
:deep(.el-select),
:deep(.el-date-picker),
:deep(.el-time-picker),
:deep(.el-cascader) {
  width: 100%;
}

:deep(.el-input-number) {
  width: 100%;
}

:deep(.el-upload) {
  width: 100%;
}

:deep(.el-upload__tip) {
  color: #909399;
  font-size: 12px;
  margin-top: 8px;
  line-height: 1.4;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-content {
    max-height: 50vh;
    padding-right: 8px;
  }
  
  :deep(.el-form-item__label) {
    width: 80px !important;
    text-align: left;
  }
}

/* 动画效果 */
:deep(.el-dialog) {
  border-radius: 8px;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 16px;
  border-bottom: 1px solid #ebeef5;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-dialog__footer) {
  padding: 16px 20px 20px;
  border-top: 1px solid #ebeef5;
}
</style>
