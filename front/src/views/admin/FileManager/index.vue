<template>
  <div class="file-manager">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">文件管理</h1>
        <p class="page-description">管理平台文件上传、存储和分发</p>
      </div>
      <div class="header-actions">
        <el-button
          type="primary"
          @click="showUploadDialog"
        >
          <el-icon><Upload /></el-icon>
          上传文件
        </el-button>
        
        <el-button
          type="success"
          @click="showStorageConfig"
        >
          <el-icon><Setting /></el-icon>
          存储配置
        </el-button>
        
        <el-button
          type="warning"
          @click="handleCleanup"
          :loading="loading"
        >
          <el-icon><Delete /></el-icon>
          清理文件
        </el-button>
      </div>
    </div>

    <!-- 存储统计卡片 -->
    <div class="storage-stats">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><FolderOpened /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formattedStorageStats.totalSpace }}</div>
          <div class="stat-label">总存储空间</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon used">
          <el-icon><Files /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formattedStorageStats.usedSpace }}</div>
          <div class="stat-label">已使用空间</div>
          <div class="stat-trend">{{ formattedStorageStats.usagePercentage }}%</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon available">
          <el-icon><Coin /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formattedStorageStats.availableSpace }}</div>
          <div class="stat-label">可用空间</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Document /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ storageStats.fileCount || 0 }}</div>
          <div class="stat-label">文件总数</div>
          <div class="stat-trend">本月新增 156 个</div>
        </div>
      </div>
    </div>

    <!-- 快速导航 -->
    <div class="quick-nav">
      <div class="nav-card" @click="navigateToUpload">
        <div class="nav-icon">
          <el-icon><Upload /></el-icon>
        </div>
        <div class="nav-content">
          <h4>文件上传</h4>
          <p>支持拖拽上传，批量处理</p>
        </div>
        <div class="nav-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="nav-card" @click="navigateToFileList">
        <div class="nav-icon">
          <el-icon><List /></el-icon>
        </div>
        <div class="nav-content">
          <h4>文件列表</h4>
          <p>查看和管理所有文件</p>
        </div>
        <div class="nav-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="nav-card" @click="navigateToStorageConfig">
        <div class="nav-icon">
          <el-icon><Setting /></el-icon>
        </div>
        <div class="nav-content">
          <h4>存储配置</h4>
          <p>配置存储策略和限制</p>
        </div>
        <div class="nav-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>

      <div class="nav-card" @click="navigateToThumbnails">
        <div class="nav-icon">
          <el-icon><Picture /></el-icon>
        </div>
        <div class="nav-content">
          <h4>缩略图管理</h4>
          <p>管理图片缩略图生成</p>
        </div>
        <div class="nav-arrow">
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </div>

    <!-- 文件类型分布图表 -->
    <div class="charts-section">
      <div class="chart-row">
        <div class="chart-card">
          <div class="chart-header">
            <h3>文件类型分布</h3>
            <el-button
              link
              type="primary"
              size="small"
              @click="refreshFileTypeStats"
            >
              刷新
            </el-button>
          </div>
          <div
            id="fileTypeChart"
            class="chart-container"
            v-loading="loading"
          />
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>存储使用趋势</h3>
            <el-date-picker
              v-model="trendDateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              size="small"
              @change="refreshStorageTrend"
            />
          </div>
          <div
            id="storageTrendChart"
            class="chart-container"
            v-loading="loading"
          />
        </div>
      </div>
    </div>

    <!-- 最近上传的文件 -->
    <div class="recent-files">
      <div class="section-header">
        <h3>最近上传的文件</h3>
        <div class="section-actions">
          <el-button
            link
            type="primary"
            size="small"
            @click="navigateToFileList"
          >
            查看全部
          </el-button>
        </div>
      </div>

      <div class="files-grid">
        <div
          v-for="file in recentFiles"
          :key="file.id"
          class="file-item"
          @click="viewFileDetail(file)"
        >
          <div class="file-preview">
            <img
              v-if="file.thumbnail"
              :src="file.thumbnail"
              :alt="file.name"
              class="thumbnail"
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
          </div>
          
          <div class="file-actions">
            <el-dropdown trigger="click">
              <el-button
                link
                type="info"
                size="small"
              >
                <el-icon><MoreFilled /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="downloadFile(file)">
                    <el-icon><Download /></el-icon>
                    下载
                  </el-dropdown-item>
                  <el-dropdown-item @click="shareFile(file)">
                    <el-icon><Share /></el-icon>
                    分享
                  </el-dropdown-item>
                  <el-dropdown-item @click="updateFileStatus(file, 'disabled')" divided>
                    <el-icon><Close /></el-icon>
                    禁用
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

      <!-- 空状态 -->
      <div v-if="recentFiles.length === 0" class="empty-state">
        <el-empty description="暂无文件">
          <el-button
            type="primary"
            @click="showUploadDialog"
          >
            上传第一个文件
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="文件上传"
      width="800px"
      :before-close="handleUploadDialogClose"
    >
      <FileUpload
        @upload-complete="handleUploadComplete"
        @upload-error="handleUploadError"
      />
    </el-dialog>

    <!-- 文件详情对话框 -->
    <el-dialog
      v-model="fileDetailVisible"
      title="文件详情"
      width="600px"
    >
      <div v-if="selectedFile" class="file-detail">
        <div class="detail-preview">
          <img
            v-if="selectedFile.thumbnail"
            :src="selectedFile.thumbnail"
            :alt="selectedFile.name"
            class="detail-thumbnail"
          />
          <div v-else class="detail-file-icon">
            <el-icon size="48">
              <component :is="getFileTypeIcon(selectedFile.mimeType)" />
            </el-icon>
          </div>
        </div>
        
        <div class="detail-info">
          <div class="detail-item">
            <span class="detail-label">文件名:</span>
            <span class="detail-value">{{ selectedFile.name }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">文件大小:</span>
            <span class="detail-value">{{ formatFileSize(selectedFile.size) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">文件类型:</span>
            <span class="detail-value">{{ getFileTypeText(selectedFile.type) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">上传者:</span>
            <span class="detail-value">{{ selectedFile.uploadedBy }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">上传时间:</span>
            <span class="detail-value">{{ formatDate(selectedFile.uploadedAt) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">下载次数:</span>
            <span class="detail-value">{{ selectedFile.downloadCount }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">状态:</span>
            <el-tag :type="getFileStatusType(selectedFile.status)">
              {{ getFileStatusText(selectedFile.status) }}
            </el-tag>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Upload,
  Setting,
  Delete,
  FolderOpened,
  Files,
  Coin,
  Document,
  List,
  ArrowRight,
  Picture,
  Download,
  Share,
  Close,
  MoreFilled
} from '@element-plus/icons-vue'
import { useFileManagement } from '@/composables/useFileManagement'
import { useFileUpload } from '@/composables/useFileUpload'
import { useAnalytics } from '@/composables/useAnalytics'
import FileUpload from './components/FileUpload.vue'

// 使用组合式函数
const router = useRouter()
const {
  loading,
  files,
  storageStats,
  fileTypeStats,
  formattedStorageStats,
  formatFileSize,
  formatDate,
  getFileTypeIcon,
  getFileStatusType,
  getFileStatusText,
  getFileTypeText,
  fetchFiles,
  fetchStorageStats,
  fetchFileTypeStats,
  cleanupFiles,
  updateFileStatus,
  deleteFile,
  downloadFile
} = useFileManagement()

const {
  uploadDialogVisible,
  handleUploadDialogClose
} = useFileUpload()

const {
  createChart,
  destroyAllCharts
} = useAnalytics()

// 响应式数据
const trendDateRange = ref([])
const fileDetailVisible = ref(false)
const selectedFile = ref(null)

// 计算属性
const recentFiles = computed(() => {
  return files.value
    .slice()
    .sort((a, b) => new Date(b.uploadedAt) - new Date(a.uploadedAt))
    .slice(0, 8)
})

// 显示上传对话框
const showUploadDialog = () => {
  uploadDialogVisible.value = true
}

// 显示存储配置
const showStorageConfig = () => {
  router.push('/admin/file-manager/storage-config')
}

// 导航函数
const navigateToUpload = () => {
  router.push('/admin/file-manager/upload')
}

const navigateToFileList = () => {
  router.push('/admin/file-manager/files')
}

const navigateToStorageConfig = () => {
  router.push('/admin/file-manager/storage-config')
}

const navigateToThumbnails = () => {
  router.push('/admin/file-manager/thumbnails')
}

// 处理文件清理
const handleCleanup = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要执行文件清理吗？这将删除过期和重复的文件。',
      '清理确认',
      {
        confirmButtonText: '确定清理',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await cleanupFiles({
      deleteExpired: true,
      deleteDuplicates: true,
      deleteOrphaned: true
    })
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('文件清理失败:', error)
    }
  }
}

// 刷新文件类型统计
const refreshFileTypeStats = async () => {
  await fetchFileTypeStats()
  initFileTypeChart()
}

// 刷新存储趋势
const refreshStorageTrend = async () => {
  // 模拟获取趋势数据
  initStorageTrendChart()
}

// 查看文件详情
const viewFileDetail = (file) => {
  selectedFile.value = file
  fileDetailVisible.value = true
}

// 分享文件
const shareFile = (file) => {
  // 复制文件链接到剪贴板
  const shareUrl = `${window.location.origin}/files/${file.id}`
  
  if (navigator.clipboard) {
    navigator.clipboard.writeText(shareUrl).then(() => {
      ElMessage.success('文件链接已复制到剪贴板')
    })
  } else {
    ElMessage.info('分享链接: ' + shareUrl)
  }
}

// 处理上传完成
const handleUploadComplete = (files) => {
  uploadDialogVisible.value = false
  ElMessage.success(`成功上传 ${files.length} 个文件`)
  
  // 刷新文件列表和统计
  fetchFiles()
  fetchStorageStats()
}

// 处理上传错误
const handleUploadError = (error) => {
  console.error('上传失败:', error)
  ElMessage.error('文件上传失败')
}

// 初始化文件类型图表
const initFileTypeChart = async () => {
  await nextTick()
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '文件类型',
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '20',
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: fileTypeStats.value.map(item => ({
        name: item.name,
        value: item.value
      }))
    }]
  }
  
  createChart('fileTypeChart', option)
}

// 初始化存储趋势图表
const initStorageTrendChart = async () => {
  await nextTick()
  
  const option = {
    title: {
      show: false
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['已使用空间', '上传文件数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: Array.from({ length: 30 }, (_, i) => {
        const date = new Date()
        date.setDate(date.getDate() - (29 - i))
        return date.toLocaleDateString()
      })
    },
    yAxis: [
      {
        type: 'value',
        name: '存储空间(GB)',
        axisLabel: {
          formatter: '{value}GB'
        }
      },
      {
        type: 'value',
        name: '文件数',
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '已使用空间',
        type: 'line',
        data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 20) + 50),
        smooth: true,
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '上传文件数',
        type: 'bar',
        yAxisIndex: 1,
        data: Array.from({ length: 30 }, () => Math.floor(Math.random() * 50) + 10),
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }
  
  createChart('storageTrendChart', option)
}

// 组件挂载
onMounted(async () => {
  await Promise.all([
    fetchFiles({ pageSize: 20 }),
    fetchStorageStats(),
    fetchFileTypeStats()
  ])
  
  initFileTypeChart()
  initStorageTrendChart()
})
</script>

<style scoped>
.file-manager {
  padding: 24px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.header-content {
  flex: 1;
}

.page-title {
  margin: 0 0 8px 0;
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.page-description {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.storage-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 8px;
  font-size: 20px;
  color: #fff;
}

.stat-icon.used {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
}

.stat-icon.available {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-trend {
  font-size: 12px;
  color: #909399;
}

.quick-nav {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.nav-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.nav-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: #f0f9ff;
  border-radius: 8px;
  color: #409eff;
  font-size: 20px;
}

.nav-content {
  flex: 1;
}

.nav-content h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.nav-content p {
  margin: 0;
  font-size: 12px;
  color: #606266;
}

.nav-arrow {
  color: #c0c4cc;
  font-size: 16px;
}

.charts-section {
  margin-bottom: 32px;
}

.chart-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #ebeef5;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  height: 300px;
  padding: 20px;
}

.recent-files {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.files-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.file-item {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.file-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
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

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.file-detail {
  display: flex;
  gap: 24px;
  padding: 20px 0;
}

.detail-preview {
  flex-shrink: 0;
}

.detail-thumbnail {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-file-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 120px;
  height: 120px;
  background: #f8f9fa;
  border-radius: 8px;
  color: #c0c4cc;
}

.detail-info {
  flex: 1;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #ebeef5;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-label {
  font-weight: 500;
  color: #303133;
}

.detail-value {
  color: #606266;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .file-manager {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: flex-end;
    flex-wrap: wrap;
  }
  
  .storage-stats {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .quick-nav {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .chart-row {
    grid-template-columns: 1fr;
  }
  
  .files-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
  
  .file-detail {
    flex-direction: column;
    gap: 16px;
  }
}

/* 动画效果 */
.stat-card,
.nav-card,
.chart-card,
.file-item {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

.nav-card:nth-child(1) { animation-delay: 0.2s; }
.nav-card:nth-child(2) { animation-delay: 0.3s; }
.nav-card:nth-child(3) { animation-delay: 0.4s; }
.nav-card:nth-child(4) { animation-delay: 0.5s; }

.chart-card:nth-child(1) { animation-delay: 0.3s; }
.chart-card:nth-child(2) { animation-delay: 0.4s; }

.file-item:nth-child(1) { animation-delay: 0.1s; }
.file-item:nth-child(2) { animation-delay: 0.2s; }
.file-item:nth-child(3) { animation-delay: 0.3s; }
.file-item:nth-child(4) { animation-delay: 0.4s; }

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
