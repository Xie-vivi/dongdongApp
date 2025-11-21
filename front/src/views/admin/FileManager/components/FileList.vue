<template>
  <div class="file-list">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="view-switcher">
          <el-button-group>
            <el-button
              :type="viewMode === 'grid' ? 'primary' : 'default'"
              size="small"
              @click="viewMode = 'grid'"
            >
              <el-icon><Grid /></el-icon>
              网格视图
            </el-button>
            <el-button
              :type="viewMode === 'list' ? 'primary' : 'default'"
              size="small"
              @click="viewMode = 'list'"
            >
              <el-icon><List /></el-icon>
              列表视图
            </el-button>
          </el-button-group>
        </div>

        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索文件名..."
            prefix-icon="Search"
            clearable
            style="width: 300px"
            @input="handleSearch"
          />
        </div>

        <div class="filters">
          <el-select
            v-model="filterType"
            placeholder="文件类型"
            clearable
            style="width: 150px"
            @change="handleFilter"
          >
            <el-option label="图片" value="image" />
            <el-option label="文档" value="document" />
            <el-option label="视频" value="video" />
            <el-option label="音频" value="audio" />
            <el-option label="压缩包" value="archive" />
          </el-select>

          <el-select
            v-model="filterStatus"
            placeholder="状态"
            clearable
            style="width: 120px"
            @change="handleFilter"
          >
            <el-option label="正常" value="active" />
            <el-option label="待审核" value="pending" />
            <el-option label="已禁用" value="disabled" />
          </el-select>

          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            size="small"
            style="width: 240px"
            @change="handleFilter"
          />
        </div>
      </div>

      <div class="toolbar-right">
        <div class="selection-info" v-if="hasSelectedFiles">
          <span>已选择 {{ selectedFilesCount }} 个文件</span>
          <el-button
            link
            type="primary"
            size="small"
            @click="clearSelection"
          >
            清空选择
          </el-button>
        </div>

        <div class="batch-actions" v-if="hasSelectedFiles">
          <el-button
            type="success"
            size="small"
            @click="batchDownloadFiles"
          >
            <el-icon><Download /></el-icon>
            批量下载
          </el-button>
          <el-button
            type="primary"
            size="small"
            @click="batchUpdateFileStatus('active')"
          >
            <el-icon><Check /></el-icon>
            批量启用
          </el-button>
          <el-button
            type="warning"
            size="small"
            @click="batchUpdateFileStatus('disabled')"
          >
            <el-icon><Close /></el-icon>
            批量禁用
          </el-button>
          <el-button
            type="danger"
            size="small"
            @click="batchDeleteFiles"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>

        <div class="sort-options">
          <el-select
            v-model="sortBy"
            size="small"
            style="width: 120px"
            @change="handleSort"
          >
            <el-option label="上传时间" value="uploadedAt" />
            <el-option label="文件名" value="name" />
            <el-option label="文件大小" value="size" />
            <el-option label="下载次数" value="downloadCount" />
          </el-select>
          <el-button
            size="small"
            @click="toggleSortOrder"
          >
            <el-icon>
              <Sort v-if="sortOrder === 'asc'" />
              <Sort v-else />
            </el-icon>
            {{ sortOrder === 'asc' ? '升序' : '降序' }}
          </el-button>
        </div>

        <el-button
          type="primary"
          size="small"
          @click="refreshFiles"
          :loading="loading"
        >
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 网格视图 -->
    <div v-if="viewMode === 'grid'" class="grid-view">
      <div class="files-grid">
        <div
          v-for="file in files"
          :key="file.id"
          class="file-item"
          :class="{ selected: isFileSelected(file) }"
          @click="toggleFileSelection(file)"
          @contextmenu.prevent="showContextMenu($event, file)"
        >
          <div class="file-selection">
            <el-checkbox
              :model-value="isFileSelected(file)"
              @change="toggleFileSelection(file)"
              @click.stop
            />
          </div>

          <div class="file-preview">
            <img
              v-if="file.thumbnail"
              :src="file.thumbnail"
              :alt="file.name"
              class="thumbnail"
              @error="handleImageError"
            />
            <div v-else class="file-icon">
              <el-icon>
                <component :is="getFileTypeIcon(file.mimeType)" />
              </el-icon>
            </div>
          </div>

          <div class="file-info">
            <div class="file-name" :title="file.name">
              {{ file.name }}
            </div>
            <div class="file-meta">
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
              <span class="file-date">{{ formatDate(file.uploadedAt) }}</span>
            </div>
            <div class="file-status">
              <el-tag :type="getFileStatusType(file.status)" size="small">
                {{ getFileStatusText(file.status) }}
              </el-tag>
            </div>
            <div class="file-stats">
              <span class="download-count">
                <el-icon><Download /></el-icon>
                {{ file.downloadCount }}
              </span>
            </div>
          </div>

          <div class="file-actions">
            <el-dropdown trigger="click" @click.stop>
              <el-button
                link
                type="info"
                size="small"
              >
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="viewFile(file)">
                    <el-icon><View /></el-icon>
                    查看
                  </el-dropdown-item>
                  <el-dropdown-item @click="downloadFile(file)">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-dropdown-item>
                  <el-dropdown-item @click="shareFile(file)">
                    <el-icon><Share /></el-icon>
                    分享
                  </el-dropdown-item>
                  <el-dropdown-item @click="editFile(file)">
                    <el-icon><Edit /></el-icon>
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item @click="updateFileStatus(file, file.status === 'active' ? 'disabled' : 'active')" divided>
                    <el-icon><Check v-if="file.status !== 'active'" />
                    <Close v-else /></el-icon>
                    {{ file.status === 'active' ? '禁用' : '启用' }}
                  </el-dropdown-item>
                  <el-dropdown-item @click="deleteFile(file.id)">
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>
    </div>

    <!-- 列表视图 -->
    <div v-else class="list-view">
      <el-table
        :data="files"
        stripe
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleTableSelection"
        @row-contextmenu="showContextMenu"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="文件名" min-width="200">
          <template #default="{ row }">
            <div class="file-name-cell">
              <div class="file-icon-small">
                <el-icon>
                  <component :is="getFileTypeIcon(row.mimeType)" />
                </el-icon>
              </div>
              <div class="file-info-text">
                <div class="file-name-text">{{ row.name }}</div>
                <div class="file-path">{{ row.path }}</div>
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="size" label="大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.size) }}
          </template>
        </el-table-column>

        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            {{ getFileTypeText(row.type) }}
          </template>
        </el-table-column>

        <el-table-column prop="uploadedBy" label="上传者" width="100" />

        <el-table-column prop="uploadedAt" label="上传时间" width="150">
          <template #default="{ row }">
            {{ formatDate(row.uploadedAt) }}
          </template>
        </el-table-column>

        <el-table-column prop="downloadCount" label="下载次数" width="100" />

        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getFileStatusType(row.status)" size="small">
              {{ getFileStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button
                link
                type="primary"
                size="small"
                @click="viewFile(row)"
              >
                查看
              </el-button>
              <el-button
                link
                type="success"
                size="small"
                @click="downloadFile(row)"
              >
                下载
              </el-button>
              <el-dropdown trigger="click">
                <el-button
                  link
                  type="info"
                  size="small"
                >
                  更多
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="shareFile(row)">分享</el-dropdown-item>
                    <el-dropdown-item @click="editFile(row)">编辑</el-dropdown-item>
                    <el-dropdown-item @click="updateFileStatus(row, row.status === 'active' ? 'disabled' : 'active')">
                      {{ row.status === 'active' ? '禁用' : '启用' }}
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteFile(row.id)" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="files.length === 0 && !loading" class="empty-state">
      <el-empty description="暂无文件数据">
        <el-button
          type="primary"
          @click="$emit('show-upload')"
        >
          上传第一个文件
        </el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[20, 50, 100, 200]"
        :total="totalFiles"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handlePageSizeChange"
        @current-change="handleCurrentPageChange"
      />
    </div>

    <!-- 右键菜单 -->
    <div
      v-if="contextMenuVisible"
      class="context-menu"
      :style="contextMenuStyle"
      @click="hideContextMenu"
    >
      <div class="menu-item" @click="viewFile(contextMenuFile)">
        <el-icon><View /></el-icon>
        查看
      </div>
      <div class="menu-item" @click="downloadFile(contextMenuFile)">
        <el-icon><Download /></el-icon>
        下载
      </div>
      <div class="menu-item" @click="shareFile(contextMenuFile)">
        <el-icon><Share /></el-icon>
        分享
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item" @click="editFile(contextMenuFile)">
        <el-icon><Edit /></el-icon>
        编辑
      </div>
      <div class="menu-item" @click="updateFileStatus(contextMenuFile, contextMenuFile.status === 'active' ? 'disabled' : 'active')">
        <el-icon><Check v-if="contextMenuFile.status !== 'active'" />
        <Close v-else /></el-icon>
        {{ contextMenuFile.status === 'active' ? '禁用' : '启用' }}
      </div>
      <div class="menu-divider"></div>
      <div class="menu-item danger" @click="deleteFile(contextMenuFile.id)">
        <el-icon><Delete /></el-icon>
        删除
      </div>
    </div>

    <!-- 文件编辑对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑文件信息"
      width="500px"
    >
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="文件名">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="editForm.description"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="editForm.tags"
            multiple
            filterable
            allow-create
            default-first-option
            style="width: 100%"
          >
            <el-option
              v-for="tag in availableTags"
              :key="tag"
              :label="tag"
              :value="tag"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="正常" value="active" />
            <el-option label="待审核" value="pending" />
            <el-option label="已禁用" value="disabled" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveFileEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Grid,
  List,
  Search,
  Download,
  Check,
  Close,
  Delete,
  Refresh,
  Sort,
  View,
  Share,
  Edit,
  MoreFilled
} from '@element-plus/icons-vue'
import { useFileManagement } from '@/composables/useFileManagement'

// 定义事件
const emit = defineEmits(['show-upload'])

// 使用文件管理组合式函数
const {
  loading,
  files,
  totalFiles,
  currentPage,
  pageSize,
  selectedFiles,
  viewMode,
  hasSelectedFiles,
  selectedFilesCount,
  formatFileSize,
  formatDate,
  getFileTypeIcon,
  getFileStatusType,
  getFileStatusText,
  getFileTypeText,
  fetchFiles,
  refreshFiles,
  searchFiles,
  filterFiles,
  sortFiles,
  toggleFileSelection,
  isFileSelected,
  toggleSelectAll,
  clearSelection,
  downloadFile,
  batchDownloadFiles,
  updateFileStatus,
  batchUpdateFileStatus,
  deleteFile,
  batchDeleteFiles
} = useFileManagement()

// 响应式数据
const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')
const dateRange = ref([])
const sortBy = ref('uploadedAt')
const sortOrder = ref('desc')

// 右键菜单
const contextMenuVisible = ref(false)
const contextMenuStyle = ref({})
const contextMenuFile = ref(null)

// 编辑对话框
const editDialogVisible = ref(false)
const editForm = ref({
  id: '',
  name: '',
  description: '',
  tags: [],
  status: 'active'
})

const availableTags = ref(['重要', '临时', '备份', '公共', '私有', '草稿'])

// 处理搜索
const handleSearch = (keyword) => {
  searchFiles(keyword)
}

// 处理筛选
const handleFilter = () => {
  const filters = {
    type: filterType.value,
    status: filterStatus.value,
    dateRange: dateRange.value
  }
  filterFiles(filters)
}

// 处理排序
const handleSort = () => {
  sortFiles(sortBy.value, sortOrder.value)
}

// 切换排序顺序
const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  sortFiles(sortBy.value, sortOrder.value)
}

// 处理分页
const handlePageSizeChange = (size) => {
  pageSize.value = size
}

const handleCurrentPageChange = (page) => {
  currentPage.value = page
}

// 处理表格选择
const handleTableSelection = (selection) => {
  selectedFiles.value = selection
}

// 显示右键菜单
const showContextMenu = (event, file) => {
  event.preventDefault()
  contextMenuFile.value = file
  contextMenuStyle.value = {
    left: `${event.clientX}px`,
    top: `${event.clientY}px`
  }
  contextMenuVisible.value = true
}

// 隐藏右键菜单
const hideContextMenu = () => {
  contextMenuVisible.value = false
}

// 查看文件
const viewFile = (file) => {
  if (file.type.startsWith('image/')) {
    // 图片预览
    window.open(file.path, '_blank')
  } else {
    // 其他文件类型下载
    downloadFile(file)
  }
  hideContextMenu()
}

// 分享文件
const shareFile = (file) => {
  const shareUrl = `${window.location.origin}/files/${file.id}`
  
  if (navigator.clipboard) {
    navigator.clipboard.writeText(shareUrl).then(() => {
      ElMessage.success('文件链接已复制到剪贴板')
    })
  } else {
    ElMessage.info('分享链接: ' + shareUrl)
  }
  hideContextMenu()
}

// 编辑文件
const editFile = (file) => {
  editForm.value = {
    id: file.id,
    name: file.originalName || file.name,
    description: file.description || '',
    tags: file.tags || [],
    status: file.status
  }
  editDialogVisible.value = true
  hideContextMenu()
}

// 保存文件编辑
const saveFileEdit = () => {
  // 这里应该调用API保存文件信息
  ElMessage.success('文件信息已更新')
  editDialogVisible.value = false
  refreshFiles()
}

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.style.display = 'none'
  const fileIcon = event.target.nextElementSibling
  if (fileIcon) {
    fileIcon.style.display = 'flex'
  }
}

// 点击外部隐藏右键菜单
const handleClickOutside = (event) => {
  if (contextMenuVisible.value && !event.target.closest('.context-menu')) {
    hideContextMenu()
  }
}

// 组件挂载
onMounted(() => {
  fetchFiles()
  document.addEventListener('click', handleClickOutside)
})

// 组件卸载
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.file-list {
  padding: 20px 0;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.filters {
  display: flex;
  gap: 8px;
  align-items: center;
}

.selection-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f0f9ff;
  border-radius: 4px;
  font-size: 14px;
  color: #409eff;
}

.batch-actions {
  display: flex;
  gap: 8px;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 4px;
}

.grid-view {
  margin-bottom: 20px;
}

.files-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.file-item {
  position: relative;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fff;
}

.file-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
}

.file-item.selected {
  border-color: #409eff;
  background: #f0f9ff;
}

.file-selection {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 10;
}

.file-preview {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 120px;
  margin-bottom: 12px;
  border-radius: 4px;
  background: #f8f9fa;
  overflow: hidden;
}

.thumbnail {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.file-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  font-size: 48px;
  color: #c0c4cc;
}

.file-info {
  text-align: center;
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
  align-items: center;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.file-status {
  display: flex;
  justify-content: center;
  margin-bottom: 8px;
}

.file-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #909399;
}

.download-count {
  display: flex;
  align-items: center;
  gap: 2px;
}

.file-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.file-item:hover .file-actions {
  opacity: 1;
}

.list-view {
  margin-bottom: 20px;
}

.file-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.file-icon-small {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #f8f9fa;
  border-radius: 4px;
  color: #409eff;
  font-size: 16px;
}

.file-info-text {
  flex: 1;
}

.file-name-text {
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.file-path {
  font-size: 12px;
  color: #909399;
}

.table-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.context-menu {
  position: fixed;
  z-index: 1000;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 4px 0;
  min-width: 120px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  font-size: 14px;
  color: #303133;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.menu-item:hover {
  background: #f5f7fa;
}

.menu-item.danger {
  color: #f56c6c;
}

.menu-item.danger:hover {
  background: #fef0f0;
}

.menu-divider {
  height: 1px;
  background: #ebeef5;
  margin: 4px 0;
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
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .filters {
    flex-wrap: wrap;
  }
  
  .files-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
  
  .file-actions {
    opacity: 1;
  }
}

/* 动画效果 */
.file-item {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
