<template>
  <div class="data-table">
    <!-- 表格工具栏 -->
    <div v-if="showToolbar" class="table-toolbar">
      <div class="toolbar-left">
        <slot name="toolbar-left">
          <el-button 
            v-if="showAdd"
            type="primary" 
            @click="$emit('add')"
          >
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
          <el-button 
            v-if="showBatchDelete && selection.length > 0"
            type="danger" 
            @click="$emit('batch-delete', selection)"
          >
            <el-icon><Delete /></el-icon>
            批量删除 ({{ selection.length }})
          </el-button>
        </slot>
      </div>
      
      <div class="toolbar-right">
        <slot name="toolbar-right">
          <el-button 
            v-if="showRefresh"
            circle
            @click="$emit('refresh')"
          >
            <el-icon><Refresh /></el-icon>
          </el-button>
          <el-button 
            v-if="showExport"
            circle
            @click="$emit('export')"
          >
            <el-icon><Download /></el-icon>
          </el-button>
        </slot>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <el-table
      ref="tableRef"
      v-loading="loading"
      :data="data"
      :height="height"
      :max-height="maxHeight"
      :stripe="stripe"
      :border="border"
      :size="size"
      :show-header="showHeader"
      :highlight-current-row="highlightCurrentRow"
      :row-key="rowKey"
      :default-sort="defaultSort"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @row-click="handleRowClick"
    >
      <!-- 选择列 -->
      <el-table-column
        v-if="showSelection"
        type="selection"
        width="55"
        :selectable="selectable"
        fixed="left"
      />
      
      <!-- 序号列 -->
      <el-table-column
        v-if="showIndex"
        type="index"
        label="序号"
        width="60"
        :index="indexMethod"
        fixed="left"
      />
      
      <!-- 数据列 -->
      <template v-for="column in columns" :key="column.prop">
        <el-table-column
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :min-width="column.minWidth"
          :fixed="column.fixed"
          :sortable="column.sortable"
          :align="column.align || 'left'"
          :show-overflow-tooltip="column.showOverflowTooltip !== false"
        >
          <template #default="scope">
            <!-- 自定义插槽 -->
            <slot 
              :name="`column-${column.prop}`" 
              :row="scope.row" 
              :column="column" 
              :index="scope.$index"
            >
              <!-- 格式化显示 -->
              <span v-if="column.formatter">
                {{ column.formatter(scope.row, column, scope.row[column.prop], scope.$index) }}
              </span>
              <!-- 标签显示 -->
              <el-tag
                v-else-if="column.tag"
                :type="getTagType(scope.row[column.prop], column.tag)"
                :size="column.tagSize || 'small'"
              >
                {{ getTagText(scope.row[column.prop], column.tag) }}
              </el-tag>
              <!-- 图片显示 -->
              <el-image
                v-else-if="column.type === 'image'"
                :src="scope.row[column.prop]"
                :style="{ width: column.imageWidth || '40px', height: column.imageHeight || '40px' }"
                fit="cover"
                :preview-src-list="[scope.row[column.prop]]"
              />
              <!-- 链接显示 -->
              <el-link
                v-else-if="column.type === 'link'"
                type="primary"
                @click="$emit('row-click', scope.row)"
              >
                {{ scope.row[column.prop] }}
              </el-link>
              <!-- 默认文本显示 -->
              <span v-else>{{ scope.row[column.prop] }}</span>
            </slot>
          </template>
        </el-table-column>
      </template>
      
      <!-- 操作列 -->
      <el-table-column
        v-if="showActions"
        label="操作"
        :width="actionWidth"
        :fixed="actionFixed"
        align="center"
      >
        <template #default="scope">
          <slot name="actions" :row="scope.row" :index="scope.$index">
            <el-button
              v-if="showView"
              link
              type="primary"
              size="small"
              @click="$emit('view', scope.row)"
            >
              查看
            </el-button>
            <el-button
              v-if="showEdit"
              link
              type="primary"
              size="small"
              @click="$emit('edit', scope.row)"
            >
              编辑
            </el-button>
            <el-button
              v-if="showDelete"
              link
              type="danger"
              size="small"
              @click="$emit('delete', scope.row)"
            >
              删除
            </el-button>
          </slot>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div v-if="showPagination" class="table-pagination">
      <el-pagination
        :current-page="pagination.current"
        :page-size="pagination.size"
        :page-sizes="pagination.sizes || [10, 20, 50, 100]"
        :total="pagination.total"
        :layout="pagination.layout || 'total, sizes, prev, pager, next, jumper'"
        :background="pagination.background !== false"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Plus, Delete, Refresh, Download } from '@element-plus/icons-vue'

// Props 定义
const props = defineProps({
  // 数据相关
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  },
  
  // 表格配置
  columns: {
    type: Array,
    required: true
  },
  height: [String, Number],
  maxHeight: [String, Number],
  stripe: {
    type: Boolean,
    default: true
  },
  border: {
    type: Boolean,
    default: true
  },
  size: {
    type: String,
    default: 'default'
  },
  showHeader: {
    type: Boolean,
    default: true
  },
  highlightCurrentRow: {
    type: Boolean,
    default: false
  },
  rowKey: String,
  defaultSort: Object,
  
  // 列配置
  showSelection: {
    type: Boolean,
    default: false
  },
  showIndex: {
    type: Boolean,
    default: true
  },
  showActions: {
    type: Boolean,
    default: true
  },
  actionWidth: {
    type: [String, Number],
    default: 200
  },
  actionFixed: {
    type: String,
    default: 'right'
  },
  selectable: Function,
  
  // 操作按钮配置
  showAdd: {
    type: Boolean,
    default: true
  },
  showView: {
    type: Boolean,
    default: true
  },
  showEdit: {
    type: Boolean,
    default: true
  },
  showDelete: {
    type: Boolean,
    default: true
  },
  showBatchDelete: {
    type: Boolean,
    default: true
  },
  
  // 工具栏配置
  showToolbar: {
    type: Boolean,
    default: true
  },
  showRefresh: {
    type: Boolean,
    default: true
  },
  showExport: {
    type: Boolean,
    default: false
  },
  
  // 分页配置
  showPagination: {
    type: Boolean,
    default: true
  },
  pagination: {
    type: Object,
    default: () => ({
      current: 1,
      size: 20,
      total: 0
    })
  }
})

// Emits 定义
const emit = defineEmits([
  'add',
  'edit',
  'delete',
  'view',
  'batch-delete',
  'refresh',
  'export',
  'selection-change',
  'sort-change',
  'row-click',
  'size-change',
  'current-change'
])

// 表格引用
const tableRef = ref(null)
const selection = ref([])

// 处理选择变化
const handleSelectionChange = (val) => {
  selection.value = val
  emit('selection-change', val)
}

// 处理排序变化
const handleSortChange = (sort) => {
  emit('sort-change', sort)
}

// 处理行点击
const handleRowClick = (row) => {
  emit('row-click', row)
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  emit('size-change', size)
}

// 处理当前页变化
const handleCurrentChange = (current) => {
  emit('current-change', current)
}

// 序号计算方法
const indexMethod = (index) => {
  return (props.pagination.current - 1) * props.pagination.size + index + 1
}

// 获取标签类型
const getTagType = (value, tagConfig) => {
  if (typeof tagConfig === 'object' && tagConfig.typeMap) {
    return tagConfig.typeMap[value] || 'info'
  }
  return 'info'
}

// 获取标签文本
const getTagText = (value, tagConfig) => {
  if (typeof tagConfig === 'object' && tagConfig.textMap) {
    return tagConfig.textMap[value] || value
  }
  return value
}

// 暴露方法
defineExpose({
  tableRef,
  selection,
  clearSelection: () => tableRef.value?.clearSelection(),
  toggleRowSelection: (row, selected) => {
    tableRef.value?.toggleRowSelection(row, selected)
  }
})
</script>

<style scoped>
.data-table {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  background: #fafafa;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-pagination {
  padding: 16px;
  display: flex;
  justify-content: flex-end;
  border-top: 1px solid #ebeef5;
  background: #fafafa;
}

/* 表格样式优化 */
:deep(.el-table) {
  font-size: 14px;
}

:deep(.el-table th) {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: 600;
}

:deep(.el-table td) {
  padding: 12px 0;
}

:deep(.el-table--border .el-table__cell) {
  border-right: 1px solid #ebeef5;
}

:deep(.el-table__body-wrapper) {
  overflow-x: auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .table-toolbar {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }
  
  .table-pagination {
    justify-content: center;
  }
  
  :deep(.el-pagination) {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
