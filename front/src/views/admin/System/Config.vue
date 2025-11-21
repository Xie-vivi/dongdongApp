<template>
  <div class="admin-system-config">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>系统配置</h2>
      <p>管理系统配置参数和设置</p>
    </div>
    
    <!-- 搜索表单 -->
    <SearchForm
      v-model="searchParams"
      :fields="searchFields"
      :loading="loading"
      @search="handleSearch"
      @reset="handleReset"
    />
    
    <!-- 操作工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-button
          type="primary"
          @click="handleCreateConfig"
        >
          <el-icon><Plus /></el-icon>
          新增配置
        </el-button>
        <el-button
          @click="handleExportConfigs"
        >
          <el-icon><Download /></el-icon>
          导出配置
        </el-button>
        <el-upload
          :show-file-list="false"
          :before-upload="beforeImportUpload"
          :on-success="handleImportSuccess"
          :on-error="handleImportError"
          accept=".json"
        >
          <el-button>
            <el-icon><Upload /></el-icon>
            导入配置
          </el-button>
        </el-upload>
      </div>
      <div class="toolbar-right">
        <el-button
          v-if="hasSelection"
          type="danger"
          @click="handleBatchDelete"
        >
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>
    
    <!-- 数据表格 -->
    <DataTable
      :data="configList"
      :columns="tableColumns"
      :loading="loading"
      :pagination="pagination"
      show-selection
      show-batch-delete
      @refresh="handleRefresh"
      @row-click="handleViewConfig"
      @view="handleViewConfig"
      @edit="handleEditConfig"
      @delete="handleDeleteConfig"
      @batch-delete="handleBatchDelete"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    >
      <!-- 自定义操作列 -->
      <template #actions="{ row }">
        <el-button
          link
          type="primary"
          size="small"
          @click.stop="handleViewConfig(row)"
        >
          查看
        </el-button>
        <el-button
          link
          type="primary"
          size="small"
          @click.stop="handleEditConfig(row)"
        >
          编辑
        </el-button>
        <el-button
          link
          type="warning"
          size="small"
          @click.stop="handleToggleStatus(row)"
        >
          {{ row.status === 'active' ? '禁用' : '启用' }}
        </el-button>
        <el-button
          link
          type="danger"
          size="small"
          @click.stop="handleDeleteConfig(row)"
        >
          删除
        </el-button>
      </template>
      
      <!-- 自定义状态列 -->
      <template #column-status="{ row }">
        <StatusTag :status="row.status" type="system" />
      </template>
      
      <!-- 自定义配置值列 -->
      <template #column-value="{ row }">
        <div class="config-value">
          <span class="value-text">{{ formatConfigValue(row.value, row.dataType) }}</span>
          <el-button
            v-if="row.value && String(row.value).length > 50"
            link
            type="primary"
            size="small"
            @click.stop="handleViewFullValue(row)"
          >
            查看完整
          </el-button>
        </div>
      </template>
    </DataTable>
    
    <!-- 配置详情对话框 -->
    <ConfigDetailDialog
      v-model="showDetailDialog"
      :config="selectedConfig"
      :categories="categories"
    />
    
    <!-- 配置编辑对话框 -->
    <ConfigEditDialog
      v-model="showEditDialog"
      :config="editingConfig"
      :categories="categories"
      @success="handleEditSuccess"
    />
    
    <!-- 配置值完整查看对话框 -->
    <ConfigValueDialog
      v-model="showValueDialog"
      :config="viewingConfig"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Download, Upload, Delete } from '@element-plus/icons-vue'
import { useSystemConfig } from '@/composables/useSystemConfig'
import DataTable from '@/components/admin/DataTable.vue'
import SearchForm from '@/components/admin/SearchForm.vue'
import StatusTag from '@/components/admin/StatusTag.vue'
import ConfigDetailDialog from './components/ConfigDetailDialog.vue'
import ConfigEditDialog from './components/ConfigEditDialog.vue'
import ConfigValueDialog from './components/ConfigValueDialog.vue'

// 使用系统配置组合式函数
const {
  loading,
  configList,
  total,
  searchParams,
  selectedConfigs,
  categories,
  searchFields,
  tableColumns,
  hasSelection,
  selectedConfigIds,
  fetchConfigList,
  createConfig,
  updateConfig,
  deleteConfig,
  batchDeleteConfigs,
  toggleConfigStatus,
  exportConfigs,
  importConfigs,
  searchConfigs,
  resetSearch,
  handlePageChange: onPageChange,
  handleSizeChange: onSizeChange,
  handleSortChange: onSortChange,
  handleSelectionChange: onSelectionChange
} = useSystemConfig()

// 对话框状态
const showDetailDialog = ref(false)
const showEditDialog = ref(false)
const showValueDialog = ref(false)
const selectedConfig = ref(null)
const editingConfig = ref(null)
const viewingConfig = ref(null)

// 分页配置
const pagination = computed(() => ({
  current: searchParams.page,
  size: searchParams.size,
  total: total
}))

// 格式化配置值显示
const formatConfigValue = (value, dataType) => {
  if (!value) return '-'
  
  const valueStr = String(value)
  
  switch (dataType) {
    case 'json':
      try {
        const parsed = JSON.parse(valueStr)
        return JSON.stringify(parsed, null, 2).substring(0, 100) + '...'
      } catch {
        return valueStr.substring(0, 50) + '...'
      }
    case 'array':
      try {
        const parsed = JSON.parse(valueStr)
        return `Array(${parsed.length})`
      } catch {
        return valueStr.substring(0, 50) + '...'
      }
    case 'boolean':
      return value ? 'true' : 'false'
    default:
      return valueStr.length > 50 ? valueStr.substring(0, 50) + '...' : valueStr
  }
}

// 处理搜索
const handleSearch = (params) => {
  searchConfigs(params)
}

// 处理重置
const handleReset = () => {
  resetSearch()
}

// 处理刷新
const handleRefresh = () => {
  fetchConfigList()
}

// 处理创建配置
const handleCreateConfig = () => {
  editingConfig.value = null
  showEditDialog.value = true
}

// 处理查看配置
const handleViewConfig = (config) => {
  selectedConfig.value = config
  showDetailDialog.value = true
}

// 处理编辑配置
const handleEditConfig = (config) => {
  editingConfig.value = { ...config }
  showEditDialog.value = true
}

// 处理删除配置
const handleDeleteConfig = (config) => {
  deleteConfig(config.id)
}

// 处理批量删除
const handleBatchDelete = () => {
  batchDeleteConfigs(selectedConfigIds.value)
}

// 处理切换状态
const handleToggleStatus = (config) => {
  const newStatus = config.status === 'active' ? 'inactive' : 'active'
  toggleConfigStatus(config.id, newStatus)
}

// 处理导出配置
const handleExportConfigs = async () => {
  if (hasSelection.value) {
    await exportConfigs(selectedConfigIds.value)
  } else {
    await exportConfigs()
  }
}

// 处理导入前验证
const beforeImportUpload = (file) => {
  const isJSON = file.type === 'application/json' || file.name.endsWith('.json')
  const isLt10M = file.size / 1024 / 1024 < 10

  if (!isJSON) {
    ElMessage.error('只能上传 JSON 格式的文件!')
    return false
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

// 处理导入成功
const handleImportSuccess = (response) => {
  if (response.code === 200) {
    ElMessage.success('配置导入成功')
    fetchConfigList()
  } else {
    ElMessage.error('配置导入失败')
  }
}

// 处理导入失败
const handleImportError = () => {
  ElMessage.error('配置导入失败')
}

// 处理查看完整值
const handleViewFullValue = (config) => {
  viewingConfig.value = config
  showValueDialog.value = true
}

// 处理编辑成功
const handleEditSuccess = () => {
  showEditDialog.value = false
  editingConfig.value = null
  fetchConfigList()
}

// 处理分页变化
const handlePageChange = (page) => {
  onPageChange(page)
}

// 处理分页大小变化
const handleSizeChange = (size) => {
  onSizeChange(size)
}

// 处理排序变化
const handleSortChange = (sort) => {
  onSortChange(sort)
}

// 处理选择变化
const handleSelectionChange = (selection) => {
  onSelectionChange(selection)
}
</script>

<style scoped>
.admin-system-config {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.page-header p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.toolbar-left {
  display: flex;
  gap: 12px;
}

.toolbar-right {
  display: flex;
  gap: 12px;
}

.config-value {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.value-text {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  color: #606266;
  word-break: break-all;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .toolbar-left,
  .toolbar-right {
    justify-content: center;
  }
  
  .page-header {
    text-align: center;
  }
  
  .page-header h2 {
    font-size: 20px;
  }
}

/* 上传组件样式 */
:deep(.el-upload) {
  display: inline-block;
}

/* 动画效果 */
.toolbar {
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
