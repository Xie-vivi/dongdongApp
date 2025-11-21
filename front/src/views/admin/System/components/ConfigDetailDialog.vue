<template>
  <el-dialog
    v-model="visible"
    title="配置详情"
    width="800px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="config" class="config-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>配置ID：</label>
            <span>{{ config.id }}</span>
          </div>
          <div class="info-item">
            <label>配置分类：</label>
            <span>{{ getCategoryName(config.category) }}</span>
          </div>
          <div class="info-item">
            <label>配置键：</label>
            <span class="config-key">{{ config.key }}</span>
          </div>
          <div class="info-item">
            <label>数据类型：</label>
            <el-tag :type="getDataTypeTagType(config.dataType)">
              {{ getDataTypeText(config.dataType) }}
            </el-tag>
          </div>
          <div class="info-item">
            <label>状态：</label>
            <StatusTag :status="config.status" type="system" />
          </div>
          <div class="info-item">
            <label>创建时间：</label>
            <span>{{ formatDateTime(config.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>更新时间：</label>
            <span>{{ formatDateTime(config.updatedAt) }}</span>
          </div>
          <div class="info-item">
            <label>最后修改人：</label>
            <span>{{ config.updatedBy || '-' }}</span>
          </div>
        </div>
      </div>
      
      <!-- 配置描述 -->
      <div v-if="config.description" class="detail-section">
        <h3 class="section-title">配置描述</h3>
        <div class="description-content">
          {{ config.description }}
        </div>
      </div>
      
      <!-- 配置值 -->
      <div class="detail-section">
        <h3 class="section-title">配置值</h3>
        <div class="value-section">
          <div class="value-header">
            <span>当前值：</span>
            <el-button
              v-if="config.value"
              link
              type="primary"
              size="small"
              @click="handleCopyValue"
            >
              <el-icon><CopyDocument /></el-icon>
              复制
            </el-button>
          </div>
          <div class="value-content">
            <pre v-if="config.value" class="value-display">{{ formatConfigValue(config.value, config.dataType) }}</pre>
            <div v-else class="empty-value">暂无配置值</div>
          </div>
        </div>
      </div>
      
      <!-- 配置历史 -->
      <div v-if="config.history && config.history.length > 0" class="detail-section">
        <h3 class="section-title">修改历史</h3>
        <div class="history-timeline">
          <el-timeline>
            <el-timeline-item
              v-for="(item, index) in config.history"
              :key="index"
              :timestamp="formatDateTime(item.createdAt)"
              placement="top"
            >
              <div class="history-item">
                <div class="history-action">
                  <span class="action-text">{{ item.action }}</span>
                  <span class="action-user">{{ item.user }}</span>
                </div>
                <div v-if="item.oldValue !== undefined" class="history-change">
                  <div class="change-label">原值：</div>
                  <div class="change-value">{{ formatConfigValue(item.oldValue, config.dataType) }}</div>
                </div>
                <div v-if="item.newValue !== undefined" class="history-change">
                  <div class="change-label">新值：</div>
                  <div class="change-value">{{ formatConfigValue(item.newValue, config.dataType) }}</div>
                </div>
                <div v-if="item.reason" class="history-reason">
                  <div class="reason-label">修改原因：</div>
                  <div class="reason-text">{{ item.reason }}</div>
                </div>
              </div>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
      
      <!-- 使用说明 -->
      <div v-if="config.usage" class="detail-section">
        <h3 class="section-title">使用说明</h3>
        <div class="usage-content">
          <div v-html="config.usage"></div>
        </div>
      </div>
      
      <!-- 相关配置 -->
      <div v-if="config.relatedConfigs && config.relatedConfigs.length > 0" class="detail-section">
        <h3 class="section-title">相关配置</h3>
        <div class="related-configs">
          <div
            v-for="related in config.relatedConfigs"
            :key="related.id"
            class="related-item"
          >
            <div class="related-key">{{ related.key }}</div>
            <div class="related-desc">{{ related.description }}</div>
            <el-button
              link
              type="primary"
              size="small"
              @click="handleViewRelated(related)"
            >
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑配置
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { ElMessage } from 'element-plus'
import { CopyDocument, Edit } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'

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
const emit = defineEmits(['update:modelValue', 'edit', 'view-related'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 获取分类名称
const getCategoryName = (categoryKey) => {
  const category = props.categories.find(cat => cat.key === categoryKey)
  return category ? category.name : categoryKey
}

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

// 格式化配置值
const formatConfigValue = (value, dataType) => {
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
    default:
      return value
  }
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 处理复制值
const handleCopyValue = async () => {
  try {
    await navigator.clipboard.writeText(props.config.value)
    ElMessage.success('配置值已复制到剪贴板')
  } catch (error) {
    // 降级方案
    const textArea = document.createElement('textarea')
    textArea.value = props.config.value
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

// 处理编辑
const handleEdit = () => {
  emit('edit', props.config)
  handleClose()
}

// 处理查看相关配置
const handleViewRelated = (relatedConfig) => {
  emit('view-related', relatedConfig)
}
</script>

<style scoped>
.config-detail {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 12px;
}

.config-detail::-webkit-scrollbar {
  width: 6px;
}

.config-detail::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background-color: #409eff;
  border-radius: 2px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.info-item label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
  line-height: 1.5;
}

.info-item span {
  color: #303133;
  line-height: 1.5;
  word-break: break-all;
}

.config-key {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
}

.description-content {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  line-height: 1.6;
  color: #303133;
}

.value-section {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.value-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.value-header span {
  font-weight: 500;
  color: #606266;
}

.value-content {
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  overflow: hidden;
}

.value-display {
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
}

.empty-value {
  padding: 16px;
  text-align: center;
  color: #909399;
  font-style: italic;
}

.history-timeline {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.history-item {
  padding: 12px;
  background: #fff;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.history-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.action-text {
  font-weight: 500;
  color: #303133;
}

.action-user {
  font-size: 12px;
  color: #909399;
}

.history-change {
  margin-bottom: 8px;
}

.change-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.change-value {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  color: #606266;
  background: #f5f5f5;
  padding: 4px 8px;
  border-radius: 4px;
  word-break: break-all;
}

.history-reason {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #ebeef5;
}

.reason-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.reason-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.4;
}

.usage-content {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  line-height: 1.6;
  color: #303133;
}

.related-configs {
  display: grid;
  gap: 12px;
}

.related-item {
  padding: 12px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.related-key {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 13px;
  font-weight: 500;
  color: #409eff;
  margin-bottom: 4px;
}

.related-desc {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .info-item label {
    min-width: auto;
  }
  
  .value-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
}

/* 动画效果 */
.detail-section {
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
