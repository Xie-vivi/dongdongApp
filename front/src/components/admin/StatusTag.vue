<template>
  <el-tag
    :type="tagType"
    :size="size"
    :effect="effect"
    :round="round"
    :hit="hit"
    :closable="closable"
    :disable-transitions="disableTransitions"
    @close="handleClose"
  >
    <el-icon v-if="showIcon" class="status-icon">
      <component :is="iconComponent" />
    </el-icon>
    {{ displayText }}
  </el-tag>
</template>

<script setup>
import { computed } from 'vue'
import {
  Check, Close, Warning, Loading, SuccessFilled,
  WarningFilled, CircleCloseFilled, InfoFilled
} from '@element-plus/icons-vue'

// Props 定义
const props = defineProps({
  // 状态值
  status: {
    type: [String, Number, Boolean],
    required: true
  },
  
  // 状态类型配置
  type: {
    type: String,
    default: 'default' // default, user, activity, content, system
  },
  
  // 自定义配置
  customConfig: {
    type: Object,
    default: () => ({})
  },
  
  // 标签属性
  size: {
    type: String,
    default: 'default'
  },
  effect: {
    type: String,
    default: 'light'
  },
  round: {
    type: Boolean,
    default: false
  },
  hit: {
    type: Boolean,
    default: false
  },
  closable: {
    type: Boolean,
    default: false
  },
  disableTransitions: {
    type: Boolean,
    default: false
  },
  showIcon: {
    type: Boolean,
    default: true
  }
})

// Emits 定义
const emit = defineEmits(['close'])

// 预定义状态配置
const statusConfigs = {
  // 用户状态
  user: {
    active: { type: 'success', text: '启用', icon: Check },
    inactive: { type: 'danger', text: '禁用', icon: Close },
    pending: { type: 'warning', text: '待审核', icon: Warning },
    locked: { type: 'info', text: '锁定', icon: InfoFilled }
  },
  
  // 活动状态
  activity: {
    draft: { type: 'info', text: '草稿', icon: InfoFilled },
    published: { type: 'success', text: '已发布', icon: SuccessFilled },
    ongoing: { type: 'primary', text: '进行中', icon: Loading },
    completed: { type: 'success', text: '已完成', icon: Check },
    cancelled: { type: 'danger', text: '已取消', icon: CircleCloseFilled }
  },
  
  // 内容状态
  content: {
    draft: { type: 'info', text: '草稿', icon: InfoFilled },
    pending: { type: 'warning', text: '待审核', icon: Warning },
    approved: { type: 'success', text: '已通过', icon: Check },
    rejected: { type: 'danger', text: '已拒绝', icon: Close }
  },
  
  // 系统状态
  system: {
    online: { type: 'success', text: '在线', icon: Check },
    offline: { type: 'danger', text: '离线', icon: Close },
    maintenance: { type: 'warning', text: '维护中', icon: Warning },
    error: { type: 'danger', text: '错误', icon: CircleCloseFilled }
  },
  
  // 通用状态
  default: {
    true: { type: 'success', text: '是', icon: Check },
    false: { type: 'danger', text: '否', icon: Close },
    1: { type: 'success', text: '是', icon: Check },
    0: { type: 'danger', text: '否', icon: Close },
    yes: { type: 'success', text: '是', icon: Check },
    no: { type: 'danger', text: '否', icon: Close },
    success: { type: 'success', text: '成功', icon: SuccessFilled },
    error: { type: 'danger', text: '错误', icon: CircleCloseFilled },
    warning: { type: 'warning', text: '警告', icon: Warning },
    info: { type: 'info', text: '信息', icon: InfoFilled }
  }
}

// 计算当前配置
const currentConfig = computed(() => {
  // 如果有自定义配置，优先使用
  if (props.customConfig && props.customConfig[props.status] !== undefined) {
    return props.customConfig[props.status]
  }
  
  // 使用预定义配置
  const typeConfig = statusConfigs[props.type] || statusConfigs.default
  return typeConfig[props.status] || {
    type: 'info',
    text: props.status,
    icon: InfoFilled
  }
})

// 计算标签类型
const tagType = computed(() => currentConfig.value.type)

// 计算显示文本
const displayText = computed(() => currentConfig.value.text)

// 计算图标组件
const iconComponent = computed(() => currentConfig.value.icon)

// 处理关闭
const handleClose = () => {
  emit('close', props.status)
}
</script>

<style scoped>
.status-icon {
  margin-right: 4px;
  font-size: 12px;
}

/* 标签样式优化 */
:deep(.el-tag) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-weight: 500;
}

/* 不同状态的样式 */
:deep(.el-tag--success) {
  background-color: #f0f9ff;
  border-color: #67c23a;
  color: #67c23a;
}

:deep(.el-tag--danger) {
  background-color: #fef0f0;
  border-color: #f56c6c;
  color: #f56c6c;
}

:deep(.el-tag--warning) {
  background-color: #fdf6ec;
  border-color: #e6a23c;
  color: #e6a23c;
}

:deep(.el-tag--info) {
  background-color: #f4f4f5;
  border-color: #909399;
  color: #909399;
}

:deep(.el-tag--primary) {
  background-color: #ecf5ff;
  border-color: #409eff;
  color: #409eff;
}

/* 动画效果 */
:deep(.el-tag) {
  transition: all 0.3s ease;
}

:deep(.el-tag:hover) {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
