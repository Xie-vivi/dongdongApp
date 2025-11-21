<template>
  <div class="files-management">
    <div class="page-header">
      <div class="header-left">
        <h2>文件管理</h2>
        <p>管理系统中的文件上传和存储</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="uploadFiles">
          <el-icon><Upload /></el-icon>
          上传文件
        </el-button>
        <el-button @click="refreshFiles">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 存储统计 -->
    <div class="storage-stats">
      <div class="stat-item">
        <div class="stat-icon" style="color: #409eff;">
          <el-icon><Folder /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ formatFileSize(storageStats.totalSize) }}</div>
          <div class="stat-label">总存储空间</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon" style="color: #67c23a;">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ storageStats.totalFiles }}</div>
          <div class="stat-label">文件总数</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon" style="color: #e6a23c;">
          <el-icon><PieChart /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ storageStats.usagePercentage }}%</div>
          <div class="stat-label">使用率</div>
        </div>
      </div>
      <div class="stat-item">
        <div class="stat-icon" style="color: #f56c6c;">
          <el-icon><Warning /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ storageStats.todayUploads }}</div>
          <div class="stat-label">今日上传</div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-form :model="filterForm" :inline="true" class="filter-form">
        <el-form-item label="搜索">
          <el-input
            v-model="filterForm.keyword"
            placeholder="搜索文件名或描述"
            clearable
            style="width: 300px"
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="filterForm.fileType" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
            <el-option label="文档" value="document" />
            <el-option label="音频" value="audio" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="正常" value="active" />
            <el-option label="已禁用" value="disabled" />
            <el-option label="待审核" value="pending" />
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

    <!-- 视图切换 -->
    <div class="view-controls">
      <div class="view-left">
        <el-radio-group v-model="viewMode" @change="handleViewChange">
          <el-radio-button label="grid">
            <el-icon><Grid /></el-icon>
            网格视图
          </el-radio-button>
          <el-radio-button label="list">
            <el-icon><List /></el-icon>
            列表视图
          </el-radio-button>
        </el-radio-group>
      </div>
      <div class="view-right">
        <el-select v-model="sortBy" placeholder="排序方式" style="width: 150px" @change="handleSort">
          <el-option label="上传时间" value="createdAt" />
          <el-option label="文件大小" value="size" />
          <el-option label="文件名" value="name" />
          <el-option label="下载次数" value="downloads" />
        </el-select>
      </div>
    </div>

    <!-- 批量操作 -->
    <div class="batch-actions" v-if="selectedFiles.length > 0">
      <div class="selected-info">
        已选择 {{ selectedFiles.length }} 个文件
      </div>
      <div class="action-buttons">
        <el-button type="success" @click="batchEnable">批量启用</el-button>
        <el-button type="warning" @click="batchDisable">批量禁用</el-button>
        <el-button type="primary" @click="batchDownload">批量下载</el-button>
        <el-button type="danger" @click="batchDelete">批量删除</el-button>
      </div>
    </div>

    <!-- 文件列表/网格 -->
    <div class="files-container" v-loading="loading">
      <!-- 网格视图 -->
      <div v-if="viewMode === 'grid'" class="files-grid">
        <div 
          v-for="file in files" 
          :key="file.id"
          class="file-card"
          :class="{ selected: selectedFiles.includes(file) }"
          @click="toggleFileSelection(file)"
        >
          <div class="file-preview">
            <img v-if="file.type === 'image'" :src="file.thumbnail" :alt="file.name" />
            <div v-else class="file-icon">
              <el-icon size="48">
                <component :is="getFileIcon(file.type)" />
              </el-icon>
            </div>
          </div>
          <div class="file-info">
            <div class="file-name" :title="file.name">{{ file.name }}</div>
            <div class="file-meta">
              <span>{{ formatFileSize(file.size) }}</span>
              <span>{{ formatTime(file.createdAt) }}</span>
            </div>
          </div>
          <div class="file-actions">
            <el-button type="text" size="small" @click.stop="previewFile(file)">
              预览
            </el-button>
            <el-button type="text" size="small" @click.stop="downloadFile(file)">
              下载
            </el-button>
            <el-button type="text" size="small" @click.stop="editFile(file)">
              编辑
            </el-button>
            <el-button type="text" size="small" @click.stop="deleteFile(file)">
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 列表视图 -->
      <div v-else class="files-table">
        <el-table
          :data="files"
          @selection-change="handleSelectionChange"
          stripe
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="文件名" min-width="200">
            <template #default="{ row }">
              <div class="file-name-cell">
                <el-icon class="file-icon">
                  <component :is="getFileIcon(row.type)" />
                </el-icon>
                <span>{{ row.name }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)" size="small">
                {{ getTypeLabel(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="size" label="大小" width="100">
            <template #default="{ row }">
              {{ formatFileSize(row.size) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">
                {{ getStatusLabel(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="downloads" label="下载次数" width="100" />
          <el-table-column prop="uploader" label="上传者" width="120" />
          <el-table-column prop="createdAt" label="上传时间" width="160">
            <template #default="{ row }">
              {{ formatTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="text" size="small" @click="previewFile(row)">
                预览
              </el-button>
              <el-button type="text" size="small" @click="downloadFile(row)">
                下载
              </el-button>
              <el-button type="text" size="small" @click="editFile(row)">
                编辑
              </el-button>
              <el-button type="text" size="small" @click="deleteFile(row)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[12, 24, 48, 96]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 文件上传对话框 -->
    <FileUploadDialog
      v-model="uploadDialogVisible"
      @upload-success="handleUploadSuccess"
    />

    <!-- 文件预览对话框 -->
    <FilePreviewDialog
      v-model="previewDialogVisible"
      :file="selectedFile"
    />

    <!-- 文件编辑对话框 -->
    <FileEditDialog
      v-model="editDialogVisible"
      :file="selectedFile"
      @save="handleSaveFile"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  Refresh,
  Search,
  Folder,
  Document,
  PieChart,
  Warning,
  Grid,
  List,
  Picture,
  VideoPlay,
  Headphones,
  Files
} from '@element-plus/icons-vue'
import FileUploadDialog from './components/FileUploadDialog.vue'
import FilePreviewDialog from './components/FilePreviewDialog.vue'
import FileEditDialog from './components/FileEditDialog.vue'

// 响应式数据
const loading = ref(false)
const files = ref([])
const selectedFiles = ref([])
const selectedFile = ref(null)
const viewMode = ref('grid')
const sortBy = ref('createdAt')
const uploadDialogVisible = ref(false)
const previewDialogVisible = ref(false)
const editDialogVisible = ref(false)

// 存储统计
const storageStats = reactive({
  totalSize: 1024 * 1024 * 1024 * 50, // 50GB
  totalFiles: 1234,
  usagePercentage: 65,
  todayUploads: 23
})

// 筛选表单
const filterForm = reactive({
  keyword: '',
  fileType: '',
  status: '',
  dateRange: null
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: viewMode.value === 'grid' ? 24 : 20,
  total: 0
})

// 模拟数据
const mockFiles = [
  {
    id: 1,
    name: 'vue-logo.png',
    type: 'image',
    size: 24576,
    status: 'active',
    downloads: 156,
    uploader: '张三',
    thumbnail: 'https://via.placeholder.com/150x150',
    url: 'https://example.com/vue-logo.png',
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 2,
    name: 'tutorial-video.mp4',
    type: 'video',
    size: 1024 * 1024 * 50,
    status: 'active',
    downloads: 89,
    uploader: '李四',
    thumbnail: 'https://via.placeholder.com/150x150',
    url: 'https://example.com/tutorial-video.mp4',
    createdAt: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 3,
    name: 'project-document.pdf',
    type: 'document',
    size: 1024 * 1024 * 2,
    status: 'pending',
    downloads: 45,
    uploader: '王五',
    thumbnail: 'https://via.placeholder.com/150x150',
    url: 'https://example.com/project-document.pdf',
    createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
  }
]

// 获取文件图标
const getFileIcon = (type) => {
  const iconMap = {
    image: Picture,
    video: VideoPlay,
    document: Document,
    audio: Headphones
  }
  return iconMap[type] || Files
}

// 获取类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    image: 'success',
    video: 'primary',
    document: 'warning',
    audio: 'info'
  }
  return typeMap[type] || 'info'
}

// 获取类型标签
const getTypeLabel = (type) => {
  const labelMap = {
    image: '图片',
    video: '视频',
    document: '文档',
    audio: '音频'
  }
  return labelMap[type] || '其他'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    active: 'success',
    disabled: 'info',
    pending: 'warning'
  }
  return typeMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    active: '正常',
    disabled: '已禁用',
    pending: '待审核'
  }
  return labelMap[status] || '未知'
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 格式化时间
const formatTime = (timeString) => {
  return new Date(timeString).toLocaleString('zh-CN')
}

// 获取文件列表
const fetchFiles = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 应用筛选
    let filteredFiles = [...mockFiles]
    
    if (filterForm.keyword) {
      filteredFiles = filteredFiles.filter(file => 
        file.name.includes(filterForm.keyword)
      )
    }
    
    if (filterForm.fileType) {
      filteredFiles = filteredFiles.filter(file => file.type === filterForm.fileType)
    }
    
    if (filterForm.status) {
      filteredFiles = filteredFiles.filter(file => file.status === filterForm.status)
    }
    
    // 排序
    filteredFiles.sort((a, b) => {
      if (sortBy.value === 'size') return b.size - a.size
      if (sortBy.value === 'name') return a.name.localeCompare(b.name)
      if (sortBy.value === 'downloads') return b.downloads - a.downloads
      return new Date(b.createdAt) - new Date(a.createdAt)
    })
    
    // 分页
    pagination.total = filteredFiles.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    files.value = filteredFiles.slice(start, end)
    
  } catch (error) {
    ElMessage.error('获取文件列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchFiles()
}

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    keyword: '',
    fileType: '',
    status: '',
    dateRange: null
  })
  pagination.page = 1
  fetchFiles()
}

// 刷新
const refreshFiles = () => {
  fetchFiles()
}

// 视图切换
const handleViewChange = () => {
  pagination.pageSize = viewMode.value === 'grid' ? 24 : 20
  fetchFiles()
}

// 排序
const handleSort = () => {
  fetchFiles()
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedFiles.value = selection
}

// 切换文件选择
const toggleFileSelection = (file) => {
  const index = selectedFiles.value.findIndex(f => f.id === file.id)
  if (index > -1) {
    selectedFiles.value.splice(index, 1)
  } else {
    selectedFiles.value.push(file)
  }
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchFiles()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchFiles()
}

// 上传文件
const uploadFiles = () => {
  uploadDialogVisible.value = true
}

// 预览文件
const previewFile = (file) => {
  selectedFile.value = file
  previewDialogVisible.value = true
}

// 下载文件
const downloadFile = (file) => {
  ElMessage.success(`开始下载 ${file.name}`)
}

// 编辑文件
const editFile = (file) => {
  selectedFile.value = file
  editDialogVisible.value = true
}

// 删除文件
const deleteFile = async (file) => {
  try {
    await ElMessageBox.confirm(`确定要删除文件"${file.name}"吗？`, '确认删除', {
      type: 'warning'
    })
    const index = mockFiles.findIndex(f => f.id === file.id)
    if (index > -1) {
      mockFiles.splice(index, 1)
    }
    ElMessage.success('文件已删除')
    fetchFiles()
  } catch {
    // 用户取消
  }
}

// 批量操作
const batchEnable = async () => {
  try {
    await ElMessageBox.confirm(`确定要启用选中的 ${selectedFiles.value.length} 个文件吗？`, '批量操作')
    selectedFiles.value.forEach(file => {
      file.status = 'active'
    })
    ElMessage.success('批量启用成功')
    fetchFiles()
  } catch {
    // 用户取消
  }
}

const batchDisable = async () => {
  try {
    await ElMessageBox.confirm(`确定要禁用选中的 ${selectedFiles.value.length} 个文件吗？`, '批量操作')
    selectedFiles.value.forEach(file => {
      file.status = 'disabled'
    })
    ElMessage.success('批量禁用成功')
    fetchFiles()
  } catch {
    // 用户取消
  }
}

const batchDownload = () => {
  ElMessage.success(`开始下载 ${selectedFiles.value.length} 个文件`)
}

const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedFiles.value.length} 个文件吗？`, '批量删除', {
      type: 'warning'
    })
    selectedFiles.value.forEach(file => {
      const index = mockFiles.findIndex(f => f.id === file.id)
      if (index > -1) {
        mockFiles.splice(index, 1)
      }
    })
    ElMessage.success('批量删除成功')
    selectedFiles.value = []
    fetchFiles()
  } catch {
    // 用户取消
  }
}

// 处理上传成功
const handleUploadSuccess = () => {
  ElMessage.success('文件上传成功')
  fetchFiles()
}

// 处理保存文件
const handleSaveFile = (file) => {
  const index = mockFiles.findIndex(f => f.id === file.id)
  if (index > -1) {
    mockFiles[index] = { ...mockFiles[index], ...file }
  }
  ElMessage.success('文件信息已更新')
  editDialogVisible.value = false
  fetchFiles()
}

// 组件挂载
onMounted(() => {
  fetchFiles()
})
</script>

<style scoped>
.files-management {
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

.storage-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  color: white;
}

.stat-icon {
  font-size: 24px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
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

.view-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
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

.files-container {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  min-height: 400px;
}

.files-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  padding: 20px;
}

.file-card {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.file-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.file-card.selected {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.file-preview {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  overflow: hidden;
}

.file-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.file-icon {
  color: #909399;
}

.file-info {
  padding: 12px;
}

.file-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
}

.file-actions {
  padding: 8px 12px;
  background: #f8f9fa;
  display: flex;
  gap: 8px;
}

.files-table {
  padding: 20px;
}

.file-name-cell {
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
  .files-management {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .storage-stats {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
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
  
  .view-controls {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .batch-actions {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .files-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
    padding: 16px;
  }
}
</style>
