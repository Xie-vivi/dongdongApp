<template>
  <div class="posts-management">
    <div class="page-header">
      <div class="header-left">
        <h2>帖子管理</h2>
        <p>管理和审核用户发布的帖子内容</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="refreshPosts">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button @click="exportPosts">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="搜索">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索标题、内容或作者"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
            <el-option label="已删除" value="deleted" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filterForm.category" placeholder="全部分类" clearable style="width: 150px">
            <el-option label="技术分享" value="tech" />
            <el-option label="生活日常" value="life" />
            <el-option label="学习笔记" value="study" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedPosts.length > 0">
      <div class="selected-info">
        已选择 {{ selectedPosts.length }} 个帖子
      </div>
      <div class="action-buttons">
        <el-button type="success" @click="batchApprove">批量通过</el-button>
        <el-button type="danger" @click="batchReject">批量拒绝</el-button>
        <el-button type="warning" @click="batchDelete">批量删除</el-button>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div class="posts-table">
      <el-table
        v-loading="loading"
        :data="posts"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="post-title" @click="viewPostDetail(row)">
              {{ row.title }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="120">
          <template #default="{ row }">
            <div class="author-info">
              <el-avatar :size="24" :src="row.authorAvatar" />
              <span>{{ row.author }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ getCategoryLabel(row.category) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="views" label="浏览量" width="100" />
        <el-table-column prop="likes" label="点赞数" width="100" />
        <el-table-column prop="comments" label="评论数" width="100" />
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="viewPostDetail(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="text" 
              size="small" 
              @click="approvePost(row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="text" 
              size="small" 
              @click="rejectPost(row)"
            >
              拒绝
            </el-button>
            <el-button type="text" size="small" @click="editPost(row)">
              编辑
            </el-button>
            <el-button type="text" size="small" @click="deletePost(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 帖子详情对话框 -->
    <ContentDetailDialog
      v-model="detailDialogVisible"
      :post="selectedPost"
      @approve="handleApprove"
      @reject="handleReject"
    />

    <!-- 拒绝理由对话框 -->
    <RejectDialog
      v-model="rejectDialogVisible"
      :post="selectedPost"
      @confirm="handleRejectConfirm"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Download, Search } from '@element-plus/icons-vue'
import ContentDetailDialog from './components/ContentDetailDialog.vue'
import RejectDialog from './components/RejectDialog.vue'

// 响应式数据
const loading = ref(false)
const posts = ref([])
const selectedPosts = ref([])
const selectedPost = ref(null)
const detailDialogVisible = ref(false)
const rejectDialogVisible = ref(false)

// 筛选表单
const filterForm = reactive({
  keyword: '',
  status: '',
  category: '',
  dateRange: null
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 模拟数据
const mockPosts = [
  {
    id: 1,
    title: 'Vue 3 Composition API 最佳实践',
    author: '张三',
    authorAvatar: 'https://via.placeholder.com/24',
    category: 'tech',
    status: 'pending',
    views: 1234,
    likes: 89,
    comments: 23,
    content: '这是一篇关于Vue 3 Composition API的技术分享...',
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 2,
    title: '我的日常生活记录',
    author: '李四',
    authorAvatar: 'https://via.placeholder.com/24',
    category: 'life',
    status: 'approved',
    views: 567,
    likes: 45,
    comments: 12,
    content: '今天是个美好的日子，我想分享一下我的生活...',
    createdAt: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 3,
    title: 'JavaScript学习笔记',
    author: '王五',
    authorAvatar: 'https://via.placeholder.com/24',
    category: 'study',
    status: 'rejected',
    views: 234,
    likes: 12,
    comments: 5,
    content: 'JavaScript是一门很有趣的编程语言...',
    createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
  }
]

// 获取分类标签
const getCategoryLabel = (category) => {
  const categoryMap = {
    tech: '技术分享',
    life: '生活日常',
    study: '学习笔记',
    other: '其他'
  }
  return categoryMap[category] || '其他'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    deleted: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    deleted: '已删除'
  }
  return labelMap[status] || '未知'
}

// 格式化时间
const formatTime = (timeString) => {
  return new Date(timeString).toLocaleString('zh-CN')
}

// 获取帖子列表
const fetchPosts = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 应用筛选
    let filteredPosts = [...mockPosts]
    
    if (filterForm.keyword) {
      filteredPosts = filteredPosts.filter(post => 
        post.title.includes(filterForm.keyword) || 
        post.author.includes(filterForm.keyword)
      )
    }
    
    if (filterForm.status) {
      filteredPosts = filteredPosts.filter(post => post.status === filterForm.status)
    }
    
    if (filterForm.category) {
      filteredPosts = filteredPosts.filter(post => post.category === filterForm.category)
    }
    
    // 分页
    pagination.total = filteredPosts.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    posts.value = filteredPosts.slice(start, end)
    
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchPosts()
}

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    keyword: '',
    status: '',
    category: '',
    dateRange: null
  })
  pagination.page = 1
  fetchPosts()
}

// 刷新
const refreshPosts = () => {
  fetchPosts()
}

// 导出
const exportPosts = () => {
  ElMessage.success('导出功能开发中')
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedPosts.value = selection
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchPosts()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchPosts()
}

// 查看帖子详情
const viewPostDetail = (post) => {
  selectedPost.value = post
  detailDialogVisible.value = true
}

// 通过帖子
const approvePost = async (post) => {
  try {
    await ElMessageBox.confirm('确定要通过这个帖子吗？', '确认操作')
    post.status = 'approved'
    ElMessage.success('帖子已通过')
    fetchPosts()
  } catch {
    // 用户取消
  }
}

// 拒绝帖子
const rejectPost = (post) => {
  selectedPost.value = post
  rejectDialogVisible.value = true
}

// 编辑帖子
const editPost = (post) => {
  ElMessage.info('编辑功能开发中')
}

// 删除帖子
const deletePost = async (post) => {
  try {
    await ElMessageBox.confirm('确定要删除这个帖子吗？', '确认删除', {
      type: 'warning'
    })
    post.status = 'deleted'
    ElMessage.success('帖子已删除')
    fetchPosts()
  } catch {
    // 用户取消
  }
}

// 批量操作
const batchApprove = async () => {
  try {
    await ElMessageBox.confirm(`确定要通过选中的 ${selectedPosts.value.length} 个帖子吗？`, '批量操作')
    selectedPosts.value.forEach(post => {
      post.status = 'approved'
    })
    ElMessage.success('批量操作成功')
    fetchPosts()
  } catch {
    // 用户取消
  }
}

const batchReject = () => {
  ElMessage.info('批量拒绝功能开发中')
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedPosts.value.length} 个帖子吗？`, '批量删除', {
      type: 'warning'
    })
    selectedPosts.value.forEach(post => {
      post.status = 'deleted'
    })
    ElMessage.success('批量删除成功')
    fetchPosts()
  } catch {
    // 用户取消
  }
}

// 处理通过操作
const handleApprove = (post) => {
  post.status = 'approved'
  detailDialogVisible.value = false
  ElMessage.success('帖子已通过')
  fetchPosts()
}

// 处理拒绝操作
const handleReject = (post) => {
  selectedPost.value = post
  detailDialogVisible.value = false
  rejectDialogVisible.value = true
}

// 处理拒绝确认
const handleRejectConfirm = (post, reason) => {
  post.status = 'rejected'
  post.rejectReason = reason
  rejectDialogVisible.value = false
  ElMessage.success('帖子已拒绝')
  fetchPosts()
}

// 组件挂载
onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.posts-management {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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
  color: #909399;
  font-size: 14px;
}

.header-right {
  display: flex;
  gap: 12px;
}

.filter-section {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.filter-form {
  margin: 0;
}

.batch-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #e6f7ff;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  border-left: 4px solid #1890ff;
}

.selected-info {
  color: #1890ff;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.posts-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.post-title {
  color: #409eff;
  cursor: pointer;
  transition: color 0.3s;
}

.post-title:hover {
  color: #66b1ff;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .posts-management {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .filter-section {
    padding: 16px;
  }
  
  .filter-form {
    display: block;
  }
  
  .filter-form .el-form-item {
    margin-bottom: 16px;
    margin-right: 0;
  }
  
  .batch-actions {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
}
</style>
