<template>
  <div class="reports-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">报表管理</h1>
        <p class="page-description">生成、下载和管理各类数据报表</p>
      </div>
      <div class="header-actions">
        <el-button
          type="primary"
          @click="showCreateReportDialog"
        >
          <el-icon><Plus /></el-icon>
          创建报表
        </el-button>
        
        <el-button
          type="success"
          @click="exportReportList"
        >
          <el-icon><Download /></el-icon>
          导出列表
        </el-button>
      </div>
    </div>

    <!-- 报表统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Files /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ reports.length }}</div>
          <div class="stat-label">总报表数</div>
          <div class="stat-trend">本月新增 5 个</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Clock /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ getGeneratingReportsCount() }}</div>
          <div class="stat-label">生成中</div>
          <div class="stat-trend">预计 10 分钟完成</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Check /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ getCompletedReportsCount() }}</div>
          <div class="stat-label">已完成</div>
          <div class="stat-trend">成功率 95.8%</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">
          <el-icon><Folder /></el-icon>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ formatNumber(getTotalFileSize()) }}</div>
          <div class="stat-label">总存储空间</div>
          <div class="stat-trend">剩余 2.3GB</div>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <div class="filter-left">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索报表名称"
          prefix-icon="Search"
          clearable
          style="width: 300px"
        />
        
        <el-select
          v-model="filterType"
          placeholder="报表类型"
          clearable
          style="width: 150px"
        >
          <el-option label="用户行为" value="user_behavior" />
          <el-option label="内容分析" value="content_analysis" />
          <el-option label="系统性能" value="system_performance" />
          <el-option label="自定义" value="custom" />
        </el-select>
        
        <el-select
          v-model="filterStatus"
          placeholder="状态"
          clearable
          style="width: 120px"
        >
          <el-option label="生成中" value="generating" />
          <el-option label="已完成" value="completed" />
          <el-option label="生成失败" value="failed" />
        </el-select>
      </div>
      
      <div class="filter-right">
        <el-button-group>
          <el-button
            :type="viewMode === 'list' ? 'primary' : 'default'"
            @click="viewMode = 'list'"
          >
            <el-icon><List /></el-icon>
            列表视图
          </el-button>
          <el-button
            :type="viewMode === 'card' ? 'primary' : 'default'"
            @click="viewMode = 'card'"
          >
            <el-icon><Grid /></el-icon>
            卡片视图
          </el-button>
        </el-button-group>
      </div>
    </div>

    <!-- 报表列表 -->
    <div class="reports-section">
      <!-- 列表视图 -->
      <div v-if="viewMode === 'list'" class="list-view">
        <el-table
          :data="filteredReports"
          stripe
          style="width: 100%"
          v-loading="loading"
        >
          <el-table-column label="报表名称" min-width="200">
            <template #default="{ row }">
              <div class="report-name-cell">
                <el-icon class="report-icon">
                  <Files />
                </el-icon>
                <div class="report-info">
                  <div class="report-title">{{ row.name }}</div>
                  <div class="report-description">{{ getReportDescription(row.type) }}</div>
                </div>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="type" label="类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getReportTypeTagType(row.type)" size="small">
                {{ getReportTypeText(row.type) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getReportStatusType(row.status)" size="small">
                {{ getReportStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createdAt" label="创建时间" width="150">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          
          <el-table-column prop="fileSize" label="文件大小" width="100">
            <template #default="{ row }">
              {{ row.fileSize || '-' }}
            </template>
          </el-table-column>
          
          <el-table-column label="进度" width="120">
            <template #default="{ row }">
              <div v-if="row.status === 'generating'" class="progress-cell">
                <el-progress
                  :percentage="row.progress || 0"
                  :stroke-width="6"
                  :show-text="false"
                />
                <span class="progress-text">{{ row.progress || 0 }}%</span>
              </div>
              <span v-else>-</span>
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  v-if="row.status === 'completed'"
                  link
                  type="primary"
                  size="small"
                  @click="downloadReport(row.id)"
                >
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
                
                <el-button
                  v-if="row.status === 'generating'"
                  link
                  type="warning"
                  size="small"
                  @click="cancelReport(row.id)"
                >
                  <el-icon><Close /></el-icon>
                  取消
                </el-button>
                
                <el-button
                  v-if="row.status === 'failed'"
                  link
                  type="success"
                  size="small"
                  @click="retryReport(row.id)"
                >
                  <el-icon><RefreshRight /></el-icon>
                  重试
                </el-button>
                
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
                      <el-dropdown-item @click="viewReportDetail(row)">
                        <el-icon><View /></el-icon>
                        查看详情
                      </el-dropdown-item>
                      <el-dropdown-item @click="shareReport(row.id)">
                        <el-icon><Share /></el-icon>
                        分享
                      </el-dropdown-item>
                      <el-dropdown-item @click="deleteReport(row.id)" divided>
                        <el-icon><Delete /></el-icon>
                        删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 卡片视图 -->
      <div v-else class="card-view">
        <div class="cards-grid">
          <div
            v-for="report in filteredReports"
            :key="report.id"
            class="report-card"
          >
            <div class="card-header">
              <div class="card-icon">
                <el-icon><Files /></el-icon>
              </div>
              <div class="card-status">
                <el-tag :type="getReportStatusType(report.status)" size="small">
                  {{ getReportStatusText(report.status) }}
                </el-tag>
              </div>
            </div>
            
            <div class="card-content">
              <h4 class="card-title">{{ report.name }}</h4>
              <p class="card-description">{{ getReportDescription(report.type) }}</p>
              
              <div class="card-meta">
                <div class="meta-item">
                  <span class="meta-label">类型:</span>
                  <el-tag :type="getReportTypeTagType(report.type)" size="small">
                    {{ getReportTypeText(report.type) }}
                  </el-tag>
                </div>
                <div class="meta-item">
                  <span class="meta-label">创建时间:</span>
                  <span class="meta-value">{{ formatDateTime(report.createdAt) }}</span>
                </div>
                <div class="meta-item">
                  <span class="meta-label">文件大小:</span>
                  <span class="meta-value">{{ report.fileSize || '-' }}</span>
                </div>
              </div>
              
              <div v-if="report.status === 'generating'" class="card-progress">
                <el-progress
                  :percentage="report.progress || 0"
                  :stroke-width="8"
                />
              </div>
            </div>
            
            <div class="card-actions">
              <el-button
                v-if="report.status === 'completed'"
                type="primary"
                size="small"
                @click="downloadReport(report.id)"
              >
                <el-icon><Download /></el-icon>
                下载
              </el-button>
              
              <el-button
                v-if="report.status === 'generating'"
                type="warning"
                size="small"
                @click="cancelReport(report.id)"
              >
                <el-icon><Close /></el-icon>
                取消
              </el-button>
              
              <el-button
                v-if="report.status === 'failed'"
                type="success"
                size="small"
                @click="retryReport(report.id)"
              >
                <el-icon><RefreshRight /></el-icon>
                重试
              </el-button>
              
              <el-dropdown trigger="click">
                <el-button
                  type="info"
                  size="small"
                  plain
                >
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="viewReportDetail(report)">
                      查看详情
                    </el-dropdown-item>
                    <el-dropdown-item @click="shareReport(report.id)">
                      分享
                    </el-dropdown-item>
                    <el-dropdown-item @click="deleteReport(report.id)" divided>
                      删除
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredReports.length === 0" class="empty-state">
        <el-empty description="暂无报表数据">
          <el-button
            type="primary"
            @click="showCreateReportDialog"
          >
            创建第一个报表
          </el-button>
        </el-empty>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="totalReports"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handlePageSizeChange"
        @current-change="handleCurrentPageChange"
      />
    </div>

    <!-- 创建报表对话框 -->
    <el-dialog
      v-model="createReportVisible"
      title="创建报表"
      width="600px"
      :before-close="handleCreateReportClose"
    >
      <el-form
        ref="createReportFormRef"
        :model="createReportForm"
        :rules="createReportRules"
        label-width="100px"
      >
        <el-form-item label="报表名称" prop="name">
          <el-input
            v-model="createReportForm.name"
            placeholder="请输入报表名称"
          />
        </el-form-item>
        
        <el-form-item label="报表类型" prop="type">
          <el-select
            v-model="createReportForm.type"
            placeholder="选择报表类型"
            style="width: 100%"
          >
            <el-option label="用户行为统计" value="user_behavior" />
            <el-option label="内容数据分析" value="content_analysis" />
            <el-option label="系统性能监控" value="system_performance" />
            <el-option label="自定义报表" value="custom" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="时间范围" prop="timeRange">
          <el-select
            v-model="createReportForm.timeRange"
            placeholder="选择时间范围"
            style="width: 100%"
          >
            <el-option label="最近7天" value="7d" />
            <el-option label="最近30天" value="30d" />
            <el-option label="最近90天" value="90d" />
            <el-option label="最近1年" value="1y" />
            <el-option label="自定义" value="custom" />
          </el-select>
        </el-form-item>
        
        <el-form-item
          v-if="createReportForm.timeRange === 'custom'"
          label="自定义日期"
          prop="customDateRange"
        >
          <el-date-picker
            v-model="createReportForm.customDateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
        
        <el-form-item label="输出格式" prop="format">
          <el-radio-group v-model="createReportForm.format">
            <el-radio label="excel">Excel</el-radio>
            <el-radio label="pdf">PDF</el-radio>
            <el-radio label="csv">CSV</el-radio>
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="包含数据">
          <el-checkbox-group v-model="createReportForm.includeData">
            <el-checkbox label="charts">图表数据</el-checkbox>
            <el-checkbox label="tables">表格数据</el-checkbox>
            <el-checkbox label="statistics">统计摘要</el-checkbox>
            <el-checkbox label="raw_data">原始数据</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="createReportVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="reportGenerating"
          @click="handleCreateReport"
        >
          {{ reportGenerating ? '生成中...' : '创建报表' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 报表详情对话框 -->
    <el-dialog
      v-model="reportDetailVisible"
      title="报表详情"
      width="800px"
    >
      <div v-if="selectedReport" class="report-detail">
        <div class="detail-header">
          <div class="detail-title">
            <h3>{{ selectedReport.name }}</h3>
            <el-tag :type="getReportStatusType(selectedReport.status)">
              {{ getReportStatusText(selectedReport.status) }}
            </el-tag>
          </div>
          <div class="detail-meta">
            <p><strong>类型:</strong> {{ getReportTypeText(selectedReport.type) }}</p>
            <p><strong>创建时间:</strong> {{ formatDateTime(selectedReport.createdAt) }}</p>
            <p><strong>文件大小:</strong> {{ selectedReport.fileSize || '-' }}</p>
            <p><strong>描述:</strong> {{ getReportDescription(selectedReport.type) }}</p>
          </div>
        </div>
        
        <div v-if="selectedReport.status === 'generating'" class="detail-progress">
          <h4>生成进度</h4>
          <el-progress
            :percentage="selectedReport.progress || 0"
            :stroke-width="10"
          />
          <p class="progress-info">预计剩余时间: {{ selectedReport.estimatedTime || '计算中...' }}</p>
        </div>
        
        <div class="detail-actions">
          <el-button
            v-if="selectedReport.status === 'completed'"
            type="primary"
            @click="downloadReport(selectedReport.id)"
          >
            <el-icon><Download /></el-icon>
            下载报表
          </el-button>
          
          <el-button
            v-if="selectedReport.status === 'generating'"
            type="warning"
            @click="cancelReport(selectedReport.id)"
          >
            <el-icon><Close /></el-icon>
            取消生成
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus,
  Download,
  Files,
  Clock,
  Check,
  Folder,
  List,
  Grid,
  Search,
  Close,
  RefreshRight,
  MoreFilled,
  View,
  Share,
  Delete
} from '@element-plus/icons-vue'
import { useAnalytics } from '@/composables/useAnalytics'

// 使用数据分析组合式函数
const {
  loading,
  reportGenerating,
  reports,
  fetchReports,
  generateReportData,
  downloadReportData
} = useAnalytics()

// 响应式数据
const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')
const viewMode = ref('list')
const currentPage = ref(1)
const pageSize = ref(20)
const totalReports = ref(0)

// 创建报表相关
const createReportVisible = ref(false)
const createReportFormRef = ref()
const createReportForm = ref({
  name: '',
  type: '',
  timeRange: '30d',
  customDateRange: [],
  format: 'excel',
  includeData: ['charts', 'tables', 'statistics']
})

const createReportRules = {
  name: [
    { required: true, message: '请输入报表名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择报表类型', trigger: 'change' }
  ],
  timeRange: [
    { required: true, message: '请选择时间范围', trigger: 'change' }
  ],
  format: [
    { required: true, message: '请选择输出格式', trigger: 'change' }
  ]
}

// 报表详情相关
const reportDetailVisible = ref(false)
const selectedReport = ref(null)

// 计算属性
const filteredReports = computed(() => {
  let filtered = reports.value
  
  if (searchKeyword.value) {
    filtered = filtered.filter(report =>
      report.name.toLowerCase().includes(searchKeyword.value.toLowerCase())
    )
  }
  
  if (filterType.value) {
    filtered = filtered.filter(report => report.type === filterType.value)
  }
  
  if (filterStatus.value) {
    filtered = filtered.filter(report => report.status === filterStatus.value)
  }
  
  return filtered
})

// 获取生成中的报表数量
const getGeneratingReportsCount = () => {
  return reports.value.filter(report => report.status === 'generating').length
}

// 获取已完成的报表数量
const getCompletedReportsCount = () => {
  return reports.value.filter(report => report.status === 'completed').length
}

// 获取总文件大小
const getTotalFileSize = () => {
  return 15.7 * 1024 * 1024 // 模拟数据，字节
}

// 获取报表类型文本
const getReportTypeText = (type) => {
  const typeMap = {
    user_behavior: '用户行为',
    content_analysis: '内容分析',
    system_performance: '系统性能',
    custom: '自定义'
  }
  return typeMap[type] || type
}

// 获取报表类型标签类型
const getReportTypeTagType = (type) => {
  const typeMap = {
    user_behavior: 'primary',
    content_analysis: 'success',
    system_performance: 'warning',
    custom: 'info'
  }
  return typeMap[type] || 'info'
}

// 获取报表状态文本
const getReportStatusText = (status) => {
  const statusMap = {
    generating: '生成中',
    completed: '已完成',
    failed: '生成失败'
  }
  return statusMap[status] || status
}

// 获取报表状态类型
const getReportStatusType = (status) => {
  const typeMap = {
    generating: 'warning',
    completed: 'success',
    failed: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取报表描述
const getReportDescription = (type) => {
  const descMap = {
    user_behavior: '包含用户活跃度、增长趋势、设备分布等数据',
    content_analysis: '包含内容发布趋势、类型分布、用户参与度等数据',
    system_performance: '包含CPU、内存、磁盘使用率等性能数据',
    custom: '自定义配置的报表数据'
  }
  return descMap[type] || '暂无描述'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 格式化数字
const formatNumber = (num) => {
  if (num >= 1024 * 1024) {
    return (num / (1024 * 1024)).toFixed(1) + 'MB'
  }
  if (num >= 1024) {
    return (num / 1024).toFixed(1) + 'KB'
  }
  return num + 'B'
}

// 显示创建报表对话框
const showCreateReportDialog = () => {
  createReportVisible.value = true
  createReportForm.value = {
    name: '',
    type: '',
    timeRange: '30d',
    customDateRange: [],
    format: 'excel',
    includeData: ['charts', 'tables', 'statistics']
  }
}

// 处理创建报表
const handleCreateReport = async () => {
  try {
    await createReportFormRef.value.validate()
    
    const reportConfig = {
      ...createReportForm.value,
      name: `${getReportTypeText(createReportForm.value.type)}_${new Date().toLocaleDateString()}`
    }
    
    await generateReportData(reportConfig)
    createReportVisible.value = false
    
  } catch (error) {
    console.error('创建报表失败:', error)
  }
}

// 处理创建报表对话框关闭
const handleCreateReportClose = () => {
  createReportFormRef.value?.resetFields()
  createReportVisible.value = false
}

// 下载报表
const downloadReport = async (reportId) => {
  try {
    await downloadReportData(reportId)
  } catch (error) {
    console.error('下载报表失败:', error)
  }
}

// 取消报表生成
const cancelReport = async (reportId) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消生成该报表吗？',
      '取消确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟取消操作
    ElMessage.success('已取消报表生成')
    await fetchReports()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消报表生成失败:', error)
    }
  }
}

// 重试报表生成
const retryReport = async (reportId) => {
  try {
    ElMessage.success('正在重新生成报表...')
    // 模拟重试操作
    await fetchReports()
    
  } catch (error) {
    console.error('重试报表生成失败:', error)
  }
}

// 查看报表详情
const viewReportDetail = (report) => {
  selectedReport.value = report
  reportDetailVisible.value = true
}

// 分享报表
const shareReport = (reportId) => {
  ElMessage.info('分享功能开发中...')
}

// 删除报表
const deleteReport = async (reportId) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该报表吗？删除后无法恢复！',
      '删除确认',
      {
        confirmButtonText: '确定删除',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟删除操作
    ElMessage.success('报表删除成功')
    await fetchReports()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除报表失败:', error)
    }
  }
}

// 导出报表列表
const exportReportList = () => {
  ElMessage.info('导出功能开发中...')
}

// 处理分页
const handlePageSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

const handleCurrentPageChange = (page) => {
  currentPage.value = page
}

// 组件挂载
onMounted(async () => {
  await fetchReports()
  totalReports.value = reports.value.length
})
</script>

<style scoped>
.reports-management {
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

.stats-cards {
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

.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.reports-section {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.report-name-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.report-icon {
  color: #409eff;
  font-size: 16px;
}

.report-info {
  flex: 1;
}

.report-title {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.report-description {
  font-size: 12px;
  color: #909399;
}

.progress-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.progress-text {
  font-size: 12px;
  color: #606266;
  min-width: 35px;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.card-view {
  margin-top: 20px;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.report-card {
  border: 1px solid #ebeef5;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.report-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: #f0f9ff;
  border-radius: 8px;
  color: #409eff;
  font-size: 18px;
}

.card-content {
  margin-bottom: 20px;
}

.card-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.card-description {
  margin: 0 0 16px 0;
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.card-meta {
  display: grid;
  gap: 8px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.meta-label {
  color: #909399;
}

.meta-value {
  color: #303133;
}

.card-progress {
  margin-top: 16px;
}

.card-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.pagination-section {
  display: flex;
  justify-content: center;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.report-detail {
  padding: 20px 0;
}

.detail-header {
  margin-bottom: 24px;
}

.detail-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-title h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.detail-meta p {
  margin: 8px 0;
  font-size: 14px;
  color: #606266;
}

.detail-progress {
  margin-bottom: 24px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.detail-progress h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.progress-info {
  margin: 12px 0 0 0;
  font-size: 12px;
  color: #909399;
}

.detail-actions {
  display: flex;
  gap: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .reports-management {
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
  
  .stats-cards {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .filter-section {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .filter-left {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .cards-grid {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-wrap: wrap;
  }
  
  .card-actions {
    flex-wrap: wrap;
  }
}

/* 动画效果 */
.stat-card,
.report-card {
  animation: fadeInUp 0.5s ease-out;
}

.stat-card:nth-child(1) { animation-delay: 0.1s; }
.stat-card:nth-child(2) { animation-delay: 0.2s; }
.stat-card:nth-child(3) { animation-delay: 0.3s; }
.stat-card:nth-child(4) { animation-delay: 0.4s; }

.report-card:nth-child(1) { animation-delay: 0.1s; }
.report-card:nth-child(2) { animation-delay: 0.2s; }
.report-card:nth-child(3) { animation-delay: 0.3s; }
.report-card:nth-child(4) { animation-delay: 0.4s; }

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
