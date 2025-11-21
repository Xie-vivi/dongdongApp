<template>
  <div class="admin-content-audit">
    <!-- 页面标题和统计 -->
    <div class="page-header">
      <div class="header-left">
        <h2>内容审核</h2>
        <p>管理系统中的帖子、活动和评论内容</p>
      </div>
      <div class="header-stats">
        <div class="stat-card">
          <div class="stat-number">{{ pendingCount }}</div>
          <div class="stat-label">待审核</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ total }}</div>
          <div class="stat-label">总计</div>
        </div>
      </div>
    </div>
    
    <!-- 内容类型切换 -->
    <div class="content-type-tabs">
      <el-radio-group v-model="activeContentType" @change="handleContentTypeChange">
        <el-radio-button label="posts">帖子审核</el-radio-button>
        <el-radio-button label="activities">活动审核</el-radio-button>
        <el-radio-button label="comments">评论审核</el-radio-button>
      </el-radio-group>
    </div>
    
    <!-- 搜索表单 -->
    <SearchForm
      v-model="searchParams"
      :fields="currentConfig.fields"
      :loading="loading"
      @search="handleSearch"
      @reset="handleReset"
    />
    
    <!-- 数据表格 -->
    <DataTable
      :data="contentList"
      :columns="currentConfig.columns"
      :loading="loading"
      :pagination="pagination"
      show-selection
      show-batch-delete
      @refresh="handleRefresh"
      @row-click="handleViewContent"
      @view="handleViewContent"
      @edit="handleEditContent"
      @delete="handleDeleteContent"
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
          @click.stop="handleViewContent(row)"
        >
          查看
        </el-button>
        <el-button
          v-if="row.status === 'pending'"
          link
          type="success"
          size="small"
          @click.stop="handleApprove(row)"
        >
          通过
        </el-button>
        <el-button
          v-if="row.status === 'pending'"
          link
          type="danger"
          size="small"
          @click.stop="handleReject(row)"
        >
          拒绝
        </el-button>
        <el-button
          v-if="row.status === 'rejected'"
          link
          type="warning"
          size="small"
          @click.stop="handleReAudit(row)"
        >
          重新审核
        </el-button>
      </template>
      
      <!-- 自定义状态列 -->
      <template #column-status="{ row }">
        <StatusTag :status="row.status" type="content" />
      </template>
      
      <!-- 自定义作者列 -->
      <template #column-author="{ row }">
        <div class="author-info">
          <el-avatar :size="24" :src="row.user?.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="author-name">{{ row.user?.username || '-' }}</span>
        </div>
      </template>
    </DataTable>
    
    <!-- 批量操作栏 -->
    <div v-if="hasSelection" class="batch-actions">
      <div class="batch-info">
        已选择 {{ selectedContentIds.length }} 项
      </div>
      <div class="batch-buttons">
        <el-button
          type="success"
          @click="handleBatchApprove"
        >
          <el-icon><Check /></el-icon>
          批量通过
        </el-button>
        <el-button
          type="danger"
          @click="handleBatchReject"
        >
          <el-icon><Close /></el-icon>
          批量拒绝
        </el-button>
      </div>
    </div>
    
    <!-- 内容详情对话框 -->
    <ContentDetailDialog
      v-model="showDetailDialog"
      :content="selectedContent"
      :content-type="activeContentType"
      @approve="handleApprove"
      @reject="handleReject"
    />
    
    <!-- 审核拒绝对话框 -->
    <RejectDialog
      v-model="showRejectDialog"
      :content="rejectingContent"
      @confirm="handleRejectConfirm"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Close, User } from '@element-plus/icons-vue'
import { useContentAudit } from '@/composables/useContentAudit'
import DataTable from '@/components/admin/DataTable.vue'
import SearchForm from '@/components/admin/SearchForm.vue'
import StatusTag from '@/components/admin/StatusTag.vue'
import ContentDetailDialog from './components/ContentDetailDialog.vue'
import RejectDialog from './components/RejectDialog.vue'

// 当前激活的内容类型
const activeContentType = ref('posts')

// 使用内容审核组合式函数
const {
  loading,
  contentList,
  total,
  searchParams,
  selectedContent,
  currentConfig,
  hasSelection,
  selectedContentIds,
  pendingCount,
  fetchContentList,
  approveContent,
  rejectContent,
  batchApproveContent,
  batchRejectContent,
  searchContent,
  resetSearch,
  handlePageChange: onPageChange,
  handleSizeChange: onSizeChange,
  handleSortChange: onSortChange,
  handleSelectionChange: onSelectionChange
} = useContentAudit(activeContentType.value)

// 对话框状态
const showDetailDialog = ref(false)
const showRejectDialog = ref(false)
const rejectingContent = ref(null)

// 分页配置
const pagination = computed(() => ({
  current: searchParams.page,
  size: searchParams.size,
  total: total
}))

// 处理内容类型切换
const handleContentTypeChange = (type) => {
  activeContentType.value = type
  // 重置搜索并获取新类型的数据
  resetSearch()
}

// 处理搜索
const handleSearch = (params) => {
  searchContent(params)
}

// 处理重置
const handleReset = () => {
  resetSearch()
}

// 处理刷新
const handleRefresh = () => {
  fetchContentList()
}

// 处理查看内容
const handleViewContent = (content) => {
  selectedContent.value = content
  showDetailDialog.value = true
}

// 处理编辑内容
const handleEditContent = (content) => {
  // 根据内容类型跳转到对应的编辑页面
  const routeMap = {
    posts: `/admin/posts/edit/${content.id}`,
    activities: `/admin/activities/edit/${content.id}`,
    comments: `/admin/comments/edit/${content.id}`
  }
  
  const route = routeMap[activeContentType.value]
  if (route) {
    // 这里可以使用 router.push 跳转
    ElMessage.info('编辑功能开发中...')
  }
}

// 处理删除内容
const handleDeleteContent = (content) => {
  // 删除功能需要根据内容类型调用不同的API
  ElMessage.info('删除功能开发中...')
}

// 处理批量删除
const handleBatchDelete = (contentIds) => {
  ElMessage.info('批量删除功能开发中...')
}

// 处理审核通过
const handleApprove = async (content) => {
  await approveContent(content.id)
}

// 处理审核拒绝
const handleReject = (content) => {
  rejectingContent.value = content
  showRejectDialog.value = true
}

// 处理重新审核
const handleReAudit = (content) => {
  // 将状态重置为待审核
  ElMessage.info('重新审核功能开发中...')
}

// 处理批量通过
const handleBatchApprove = async () => {
  await batchApproveContent(selectedContentIds.value)
}

// 处理批量拒绝
const handleBatchReject = () => {
  rejectingContent.value = { ids: selectedContentIds.value }
  showRejectDialog.value = true
}

// 处理拒绝确认
const handleRejectConfirm = async (reason) => {
  if (rejectingContent.value.ids) {
    // 批量拒绝
    await batchRejectContent(rejectingContent.value.ids, reason)
  } else {
    // 单个拒绝
    await rejectContent(rejectingContent.value.id, reason)
  }
  
  showRejectDialog.value = false
  rejectingContent.value = null
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

// 监听内容类型变化，重新获取数据
watch(activeContentType, (newType) => {
  // 切换内容类型时重置搜索并获取新数据
  resetSearch()
}, { immediate: false })
</script>

<style scoped>
.admin-content-audit {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-left h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.header-left p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.header-stats {
  display: flex;
  gap: 16px;
}

.stat-card {
  padding: 16px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
  min-width: 100px;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.content-type-tabs {
  margin-bottom: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 14px;
  color: #303133;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-top: 16px;
}

.batch-info {
  font-size: 14px;
  color: #606266;
}

.batch-buttons {
  display: flex;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-left {
    text-align: center;
  }
  
  .header-stats {
    justify-content: center;
  }
  
  .batch-actions {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .batch-buttons {
    justify-content: center;
  }
}

/* 动画效果 */
.content-type-tabs {
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
