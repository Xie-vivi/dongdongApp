<template>
  <div class="tags-management">
    <div class="page-header">
      <div class="header-left">
        <h2>标签管理</h2>
        <p>管理系统中的标签分类</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="createTag">
          <el-icon><Plus /></el-icon>
          新建标签
        </el-button>
        <el-button @click="refreshTags">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="搜索">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索标签名称或描述"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filterForm.category" placeholder="全部分类" clearable style="width: 150px">
            <el-option label="内容标签" value="content" />
            <el-option label="用户标签" value="user" />
            <el-option label="活动标签" value="activity" />
            <el-option label="系统标签" value="system" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="启用" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedTags.length > 0">
      <div class="selected-info">
        已选择 {{ selectedTags.length }} 个标签
      </div>
      <div class="action-buttons">
        <el-button type="success" @click="batchEnable">批量启用</el-button>
        <el-button type="warning" @click="batchDisable">批量禁用</el-button>
        <el-button type="danger" @click="batchDelete">批量删除</el-button>
      </div>
    </div>

    <!-- 标签列表 -->
    <div class="tags-table">
      <el-table
        v-loading="loading"
        :data="tags"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="标签名称" min-width="150">
          <template #default="{ row }">
            <el-tag :color="row.color" effect="light">
              {{ row.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="category" label="分类" width="120">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)" size="small">
              {{ getCategoryLabel(row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="usageCount" label="使用次数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'info'" size="small">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="editTag(row)">
              编辑
            </el-button>
            <el-button 
              type="text" 
              size="small" 
              @click="toggleTagStatus(row)"
            >
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="text" size="small" @click="viewTagUsage(row)">
              查看使用
            </el-button>
            <el-button type="text" size="small" @click="deleteTag(row)">
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

    <!-- 标签编辑对话框 -->
    <TagEditDialog
      v-model="editDialogVisible"
      :tag="selectedTag"
      :is-edit="isEdit"
      @save="handleSaveTag"
    />

    <!-- 标签使用情况对话框 -->
    <TagUsageDialog
      v-model="usageDialogVisible"
      :tag="selectedTag"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import TagEditDialog from './components/TagEditDialog.vue'
import TagUsageDialog from './components/TagUsageDialog.vue'

// 响应式数据
const loading = ref(false)
const tags = ref([])
const selectedTags = ref([])
const selectedTag = ref(null)
const editDialogVisible = ref(false)
const usageDialogVisible = ref(false)
const isEdit = ref(false)

// 筛选表单
const filterForm = reactive({
  keyword: '',
  category: '',
  status: ''
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 模拟数据
const mockTags = [
  {
    id: 1,
    name: 'Vue.js',
    description: 'Vue.js框架相关内容',
    category: 'content',
    color: '#4FC08D',
    usageCount: 156,
    status: 'active',
    createdAt: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 2,
    name: '前端开发',
    description: '前端开发技术分享',
    category: 'content',
    color: '#42b883',
    usageCount: 234,
    status: 'active',
    createdAt: new Date(Date.now() - 14 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 3,
    name: '技术分享',
    description: '技术分享活动',
    category: 'activity',
    color: '#ff6b6b',
    usageCount: 89,
    status: 'active',
    createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 4,
    name: '新手',
    description: '新手用户标签',
    category: 'user',
    color: '#feca57',
    usageCount: 445,
    status: 'active',
    createdAt: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 5,
    name: '已废弃',
    description: '已废弃的标签',
    category: 'system',
    color: '#95a5a6',
    usageCount: 0,
    status: 'inactive',
    createdAt: new Date(Date.now() - 60 * 24 * 60 * 60 * 1000).toISOString()
  }
]

// 获取分类类型
const getCategoryType = (category) => {
  const typeMap = {
    content: 'primary',
    user: 'success',
    activity: 'warning',
    system: 'info'
  }
  return typeMap[category] || 'info'
}

// 获取分类标签
const getCategoryLabel = (category) => {
  const labelMap = {
    content: '内容标签',
    user: '用户标签',
    activity: '活动标签',
    system: '系统标签'
  }
  return labelMap[category] || '其他'
}

// 格式化时间
const formatTime = (timeString) => {
  return new Date(timeString).toLocaleString('zh-CN')
}

// 获取标签列表
const fetchTags = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 应用筛选
    let filteredTags = [...mockTags]
    
    if (filterForm.keyword) {
      filteredTags = filteredTags.filter(tag => 
        tag.name.includes(filterForm.keyword) || 
        tag.description.includes(filterForm.keyword)
      )
    }
    
    if (filterForm.category) {
      filteredTags = filteredTags.filter(tag => tag.category === filterForm.category)
    }
    
    if (filterForm.status) {
      filteredTags = filteredTags.filter(tag => tag.status === filterForm.status)
    }
    
    // 分页
    pagination.total = filteredTags.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    tags.value = filteredTags.slice(start, end)
    
  } catch (error) {
    ElMessage.error('获取标签列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchTags()
}

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    keyword: '',
    category: '',
    status: ''
  })
  pagination.page = 1
  fetchTags()
}

// 刷新
const refreshTags = () => {
  fetchTags()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedTags.value = selection
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchTags()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchTags()
}

// 创建标签
const createTag = () => {
  selectedTag.value = {
    name: '',
    description: '',
    category: 'content',
    color: '#409eff'
  }
  isEdit.value = false
  editDialogVisible.value = true
}

// 编辑标签
const editTag = (tag) => {
  selectedTag.value = { ...tag }
  isEdit.value = true
  editDialogVisible.value = true
}

// 切换标签状态
const toggleTagStatus = async (tag) => {
  try {
    const newStatus = tag.status === 'active' ? '禁用' : '启用'
    await ElMessageBox.confirm(`确定要${newStatus}标签"${tag.name}"吗？`, '确认操作')
    tag.status = tag.status === 'active' ? 'inactive' : 'active'
    ElMessage.success(`标签已${newStatus}`)
    fetchTags()
  } catch {
    // 用户取消
  }
}

// 查看标签使用情况
const viewTagUsage = (tag) => {
  selectedTag.value = tag
  usageDialogVisible.value = true
}

// 删除标签
const deleteTag = async (tag) => {
  try {
    await ElMessageBox.confirm(`确定要删除标签"${tag.name}"吗？`, '确认删除', {
      type: 'warning'
    })
    const index = mockTags.findIndex(t => t.id === tag.id)
    if (index > -1) {
      mockTags.splice(index, 1)
    }
    ElMessage.success('标签已删除')
    fetchTags()
  } catch {
    // 用户取消
  }
}

// 批量操作
const batchEnable = async () => {
  try {
    await ElMessageBox.confirm(`确定要启用选中的 ${selectedTags.value.length} 个标签吗？`, '批量操作')
    selectedTags.value.forEach(tag => {
      tag.status = 'active'
    })
    ElMessage.success('批量启用成功')
    fetchTags()
  } catch {
    // 用户取消
  }
}

const batchDisable = async () => {
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedTags.value.length} 个标签吗？`, '批量操作')
    selectedTags.value.forEach(tag => {
      tag.status = 'inactive'
    })
    ElMessage.success('批量禁用成功')
    fetchTags()
  } catch {
    // 用户取消
  }
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedTags.value.length} 个标签吗？`, '批量删除', {
      type: 'warning'
    })
    selectedTags.value.forEach(tag => {
      const index = mockTags.findIndex(t => t.id === tag.id)
      if (index > -1) {
        mockTags.splice(index, 1)
      }
    })
    ElMessage.success('批量删除成功')
    fetchTags()
  } catch {
    // 用户取消
  }
}

// 处理保存标签
const handleSaveTag = (tag) => {
  if (isEdit.value) {
    // 编辑现有标签
    const index = mockTags.findIndex(t => t.id === tag.id)
    if (index > -1) {
      mockTags[index] = { ...mockTags[index], ...tag }
    }
    ElMessage.success('标签已更新')
  } else {
    // 创建新标签
    const newTag = {
      ...tag,
      id: Math.max(...mockTags.map(t => t.id)) + 1,
      usageCount: 0,
      status: 'active',
      createdAt: new Date().toISOString()
    }
    mockTags.unshift(newTag)
    ElMessage.success('标签已创建')
  }
  editDialogVisible.value = false
  fetchTags()
}

// 组件挂载
onMounted(() => {
  fetchTags()
})
</script>

<style scoped>
.tags-management {
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

.tags-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.pagination {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .tags-management {
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
