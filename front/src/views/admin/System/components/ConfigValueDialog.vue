<template>
  <el-dialog
    v-model="visible"
    title="配置值详情"
    width="600px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="config" class="config-value-detail">
      <!-- 配置信息 -->
      <div class="config-info">
        <div class="info-row">
          <label>配置键：</label>
          <span class="config-key">{{ config.key }}</span>
        </div>
        <div class="info-row">
          <label>数据类型：</label>
          <el-tag :type="getDataTypeTagType(config.dataType)">
            {{ getDataTypeText(config.dataType) }}
          </el-tag>
        </div>
      </div>
      
      <!-- 配置值显示 -->
      <div class="value-display">
        <div class="display-header">
          <span>配置值：</span>
          <div class="header-actions">
            <el-button
              link
              type="primary"
              size="small"
              @click="handleCopyValue"
            >
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleFormatValue"
            >
              <el-icon><Magic /></el-icon>
              格式化
            </el-button>
          </div>
        </div>
        
        <div class="value-content">
          <pre class="value-text">{{ formattedValue }}</pre>
        </div>
      </div>
      
      <!-- 值统计信息 -->
      <div v-if="config.dataType === 'string'" class="value-stats">
        <div class="stat-item">
          <label>字符数：</label>
          <span>{{ config.value ? config.value.length : 0 }}</span>
        </div>
        <div class="stat-item">
          <label>行数：</label>
          <span>{{ config.value ? config.value.split('\n').length : 0 }}</span>
        </div>
      </div>
      
      <div v-else-if="config.dataType === 'json' || config.dataType === 'array'" class="value-stats">
        <div class="stat-item">
          <label>JSON大小：</label>
          <span>{{ config.value ? config.value.length : 0 }} 字节</span>
        </div>
        <div v-if="parsedValue" class="stat-item">
          <label>数据结构：</label>
          <span>{{ getDataStructure(parsedValue) }}</span>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { CopyDocument, Magic } from '@element-plus/icons-vue'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  config: {
    type: Object,
    default: null
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 格式化后的值
const formattedValue = ref('')

// 解析后的值（用于JSON类型）
const parsedValue = ref(null)

// 获取数据类型标签类型
const getDataTypeTagType = (dataType) => {
  const typeMap = {
    string: 'info',
    number: 'success',
    boolean: 'warning',
    json: 'danger',
    array: 'primary'
  }
  return typeMap[dataType] || 'info'
}

// 获取数据类型文本
const getDataTypeText = (dataType) => {
  const typeMap = {
    string: '字符串',
    number: '数字',
    boolean: '布尔值',
    json: 'JSON',
    array: '数组'
  }
  return typeMap[dataType] || dataType
}

// 获取数据结构描述
const getDataStructure = (data) => {
  if (Array.isArray(data)) {
    return `Array(${data.length})`
  } else if (typeof data === 'object' && data !== null) {
    const keys = Object.keys(data)
    return `Object(${keys.length} keys: ${keys.slice(0, 3).join(', ')}${keys.length > 3 ? '...' : ''})`
  }
  return typeof data
}

// 格式化配置值
const formatConfigValue = (value, dataType) => {
  if (!value) return ''
  
  switch (dataType) {
    case 'json':
    case 'array':
      try {
        const parsed = JSON.parse(value)
        parsedValue.value = parsed
        return JSON.stringify(parsed, null, 2)
      } catch {
        parsedValue.value = null
        return value
      }
    case 'boolean':
      return value ? 'true' : 'false'
    default:
      parsedValue.value = null
      return value
  }
}

// 格式化值
const handleFormatValue = () => {
  if (props.config.dataType === 'json' || props.config.dataType === 'array') {
    try {
      const parsed = JSON.parse(props.config.value)
      formattedValue.value = JSON.stringify(parsed, null, 2)
      parsedValue.value = parsed
      ElMessage.success('JSON格式化成功')
    } catch (error) {
      ElMessage.error('JSON格式错误，无法格式化')
    }
  } else {
    ElMessage.info('该数据类型无需格式化')
  }
}

// 处理复制值
const handleCopyValue = async () => {
  try {
    await navigator.clipboard.writeText(formattedValue.value)
    ElMessage.success('配置值已复制到剪贴板')
  } catch (error) {
    // 降级方案
    const textArea = document.createElement('textarea')
    textArea.value = formattedValue.value
    document.body.appendChild(textArea)
    textArea.select()
    document.execCommand('copy')
    document.body.removeChild(textArea)
    ElMessage.success('配置值已复制到剪贴板')
  }
}

// 处理关闭
const handleClose = () => {
  visible.value = false
}

// 监听配置变化
watch(
  () => props.config,
  (newConfig) => {
    if (newConfig) {
      formattedValue.value = formatConfigValue(newConfig.value, newConfig.dataType)
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.config-value-detail {
  padding: 0;
}

.config-info {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
}

.config-key {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
  color: #409eff;
}

.value-display {
  margin-bottom: 20px;
}

.display-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.display-header span {
  font-weight: 500;
  color: #606266;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.value-content {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
}

.value-text {
  margin: 0;
  padding: 16px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  line-height: 1.5;
  color: #303133;
  background: #fff;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 400px;
  overflow-y: auto;
}

.value-stats {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.stat-item:last-child {
  margin-bottom: 0;
}

.stat-item label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
}

.stat-item span {
  color: #303133;
}

.dialog-footer {
  text-align: right;
}

/* 滚动条样式 */
.value-text::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.value-text::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.value-text::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .display-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .header-actions {
    align-self: stretch;
    justify-content: center;
  }
  
  .info-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .info-row label {
    min-width: auto;
  }
}

/* 动画效果 */
.value-display {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
