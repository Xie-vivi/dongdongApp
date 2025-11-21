<template>
  <div class="admin-pagination">
    <div class="pagination-info">
      <span class="info-text">
        显示第 {{ startIndex }} - {{ endIndex }} 条，共 {{ total }} 条数据
      </span>
    </div>
    
    <el-pagination
      :current-page="current"
      :page-size="pageSize"
      :page-sizes="pageSizes"
      :total="total"
      :layout="layout"
      :background="background"
      :small="small"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @prev-click="handlePrevClick"
      @next-click="handleNextClick"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Props 定义
const props = defineProps({
  current: {
    type: Number,
    default: 1
  },
  pageSize: {
    type: Number,
    default: 20
  },
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100]
  },
  total: {
    type: Number,
    required: true
  },
  layout: {
    type: String,
    default: 'sizes, prev, pager, next, jumper'
  },
  background: {
    type: Boolean,
    default: true
  },
  small: {
    type: Boolean,
    default: false
  },
  showInfo: {
    type: Boolean,
    default: true
  }
})

// Emits 定义
const emit = defineEmits([
  'size-change',
  'current-change',
  'prev-click',
  'next-click',
  'change'
])

// 计算显示范围
const startIndex = computed(() => {
  if (props.total === 0) return 0
  return (props.current - 1) * props.pageSize + 1
})

const endIndex = computed(() => {
  const end = props.current * props.pageSize
  return end > props.total ? props.total : end
})

// 处理每页数量变化
const handleSizeChange = (size) => {
  emit('size-change', size)
  emit('change', { current: props.current, pageSize: size })
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  emit('current-change', current)
  emit('change', { current, pageSize: props.pageSize })
}

// 处理上一页
const handlePrevClick = (current) => {
  emit('prev-click', current)
}

// 处理下一页
const handleNextClick = (current) => {
  emit('next-click', current)
}
</script>

<style scoped>
.admin-pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  background: transparent;
}

.pagination-info {
  color: #606266;
  font-size: 14px;
}

.info-text {
  color: #909399;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-pagination {
    flex-direction: column;
    gap: 12px;
    align-items: center;
  }
  
  .pagination-info {
    order: 2;
  }
}

/* 分页器样式优化 */
:deep(.el-pagination) {
  font-size: 14px;
}

:deep(.el-pagination .el-pager li) {
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  border-radius: 4px;
  margin: 0 2px;
}

:deep(.el-pagination .el-pager li.active) {
  background-color: #409eff;
  color: #fff;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next) {
  min-width: 32px;
  height: 32px;
  border-radius: 4px;
}

:deep(.el-pagination .el-select .el-input) {
  width: 110px;
}

:deep(.el-pagination__sizes) {
  margin-right: 16px;
}

:deep(.el-pagination__jump) {
  margin-left: 16px;
}
</style>
