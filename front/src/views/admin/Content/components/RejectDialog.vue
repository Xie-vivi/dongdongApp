<template>
  <el-dialog
    v-model="visible"
    title="审核拒绝"
    width="500px"
    append-to-body
    :before-close="handleClose"
  >
    <div class="reject-dialog">
      <!-- 拒绝原因表单 -->
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="80px"
        size="default"
      >
        <el-form-item label="拒绝原因" prop="reason">
          <el-select
            v-model="formData.reasonType"
            placeholder="请选择拒绝原因类型"
            style="width: 100%"
            @change="handleReasonTypeChange"
          >
            <el-option
              v-for="option in reasonOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="详细说明" prop="detail">
          <el-input
            v-model="formData.detail"
            type="textarea"
            :rows="4"
            placeholder="请输入详细的拒绝理由（可选）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        
        <!-- 快速选择常用理由 -->
        <el-form-item label="常用理由">
          <div class="quick-reasons">
            <el-tag
              v-for="reason in quickReasons"
              :key="reason"
              class="reason-tag"
              @click="handleQuickReasonSelect(reason)"
            >
              {{ reason }}
            </el-tag>
          </div>
        </el-form-item>
      </el-form>
      
      <!-- 预览区域 -->
      <div v-if="formData.reasonType || formData.detail" class="preview-section">
        <h4>拒绝理由预览</h4>
        <div class="preview-content">
          <div v-if="formData.reasonType" class="reason-type">
            {{ getReasonTypeText(formData.reasonType) }}
          </div>
          <div v-if="formData.detail" class="reason-detail">
            {{ formData.detail }}
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button
          type="danger"
          :loading="loading"
          @click="handleConfirm"
        >
          确认拒绝
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  content: {
    type: Object,
    default: null
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue', 'confirm'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 表单引用
const formRef = ref(null)

// 加载状态
const loading = ref(false)

// 表单数据
const formData = reactive({
  reasonType: '',
  detail: ''
})

// 表单验证规则
const rules = {
  reasonType: [
    { required: true, message: '请选择拒绝原因类型', trigger: 'change' }
  ]
}

// 拒绝原因选项
const reasonOptions = [
  { label: '内容违规', value: 'violation' },
  { label: '垃圾信息', value: 'spam' },
  { label: '重复内容', value: 'duplicate' },
  { label: '质量不佳', value: 'quality' },
  { label: '不相关内容', value: 'irrelevant' },
  { label: '版权问题', value: 'copyright' },
  { label: '其他原因', value: 'other' }
]

// 常用拒绝理由
const quickReasons = [
  '内容包含违规信息',
  '涉嫌垃圾广告',
  '内容质量不符合要求',
  '与主题不相关',
  '涉嫌侵犯他人版权',
  '存在不当言论',
  '内容重复或抄袭',
  '信息不实或误导'
]

// 获取拒绝原因类型文本
const getReasonTypeText = (type) => {
  const option = reasonOptions.find(item => item.value === type)
  return option ? option.label : type
}

// 处理拒绝原因类型变化
const handleReasonTypeChange = (value) => {
  // 根据类型自动填充一些默认说明
  const defaultDetails = {
    violation: '内容违反了社区规范，包含不当信息',
    spam: '疑似垃圾广告或营销信息',
    duplicate: '内容与已有发布重复',
    quality: '内容质量不符合平台要求',
    irrelevant: '内容与当前主题或板块不相关',
    copyright: '可能存在版权问题或未经授权使用',
    other: '其他需要拒绝的原因'
  }
  
  if (defaultDetails[value] && !formData.detail) {
    formData.detail = defaultDetails[value]
  }
}

// 处理快速理由选择
const handleQuickReasonSelect = (reason) => {
  formData.detail = reason
}

// 处理确认
const handleConfirm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 构建完整的拒绝理由
    const fullReason = getReasonTypeText(formData.reasonType) + 
                      (formData.detail ? `：${formData.detail}` : '')
    
    loading.value = true
    
    // 发送确认事件
    emit('confirm', fullReason)
    
  } catch (error) {
    console.error('表单验证失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理关闭
const handleClose = () => {
  // 重置表单
  formData.reasonType = ''
  formData.detail = ''
  formRef.value?.resetFields()
  
  visible.value = false
}

// 暴露方法
defineExpose({
  formData,
  resetForm: () => {
    formData.reasonType = ''
    formData.detail = ''
    formRef.value?.resetFields()
  }
})
</script>

<style scoped>
.reject-dialog {
  padding: 0;
}

.quick-reasons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.reason-tag {
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 0;
}

.reason-tag:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.preview-section {
  margin-top: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.preview-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.preview-content {
  background: #fff;
  padding: 12px;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.reason-type {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.reason-detail {
  color: #606266;
  line-height: 1.5;
  word-break: break-word;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quick-reasons {
    gap: 6px;
  }
  
  .reason-tag {
    font-size: 12px;
  }
}

/* 动画效果 */
.reason-tag {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 表单样式优化 */
:deep(.el-form-item__label) {
  font-weight: 500;
  color: #606266;
}

:deep(.el-select) {
  width: 100%;
}

:deep(.el-textarea__inner) {
  resize: vertical;
  min-height: 80px;
}

/* 预览区域动画 */
.preview-section {
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    max-height: 200px;
    transform: translateY(0);
  }
}
</style>
