<template>
  <div class="log-list">
    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索用户、操作、IP地址..."
            prefix-icon="Search"
            clearable
            style="width: 300px"
            @input="handleSearch"
          />
        </div>

        <div class="filters">
          <el-select
            v-model="filterForm.operationType"
            placeholder="操作类型"
            clearable
            style="width: 120px"
            @change="handleFilter"
          >
            <el-option
              v-for="type in Object.values(OPERATION_TYPES)"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>

          <el-select
            v-model="filterForm.level"
            placeholder="日志级别"
            clearable
            style="width: 100px"
            @change="handleFilter"
          >
            <el-option
              v-for="level in Object.values(LOG_LEVELS)"
              :key="level.value"
              :label="level.label"
              :value="level.value"
            />
          </el-select>

          <el-select
            v-model="filterForm.status"
            placeholder="状态"
            clearable
            style="width: 100px"
            @change="handleFilter"
          >
            <el-option label="成功" value="success" />
            <el-option label="失败" value="failed" />
          </el-select>

          <el-select
            v-model="filterForm.module"
            placeholder="模块"
            clearable
            style="width: 120px"
            @change="handleFilter"
          >
            <el-option label="用户管理" value="用户管理" />
            <el-option label="内容审核" value="内容审核" />
            <el-option label="文件管理" value="文件管理" />
            <el-option label="系统设置" value="系统设置" />
            <el-option label="数据分析" value="数据分析" />
          </el-select>

          <el-date-picker
            v-model="filterForm.dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            size="small"
            style="width: 360px"
            @change="handleFilter"
          />
        </div>
      </div>

      <div class="toolbar-right">
        <div class="selection-info" v-if="hasSelectedLogs">
          <span>已选择 {{ selectedLogsCount }} 条日志</span>
          <el-button
            link
            type="primary"
            size="small"
            @click="clearSelection"
          >
            清空选择
          </el-button>
        </div>

        <div class="batch-actions" v-if="hasSelectedLogs">
          <el-button
            type="success"
            size="small"
            @click="$emit('batch-export')"
          >
            <el-icon><Download /></el-icon>
            批量导出
          </el-button>
        </div>

        <div class="sort-options">
          <el-select
            v-model="sortBy"
            size="small"
            style="width: 120px"
            @change="handleSort"
          >
            <el-option label="创建时间" value="createdAt" />
            <el-option label="用户名" value="username" />
            <el-option label="操作类型" value="operationType" />
            <el-option label="持续时间" value="duration" />
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
          @click="$emit('refresh')"
          :loading="loading"
        >
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <!-- 日志表格 -->
    <div class="log-table-container">
      <el-table
        :data="logs"
        stripe
        style="width: 100%"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        @row-click="handleRowClick"
        height="calc(100vh - 400px)"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column label="时间" width="180" sortable="custom">
          <template #default="{ row }">
            <div class="time-cell">
              <div class="date">{{ formatDate(row.createdAt) }}</div>
              <div class="time">{{ formatTime(row.createdAt) }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="用户" width="120">
          <template #default="{ row }">
            <div class="user-cell">
              <div class="username">{{ row.username }}</div>
              <div class="role">{{ row.userRole }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <div class="operation-cell">
              <div class="operation-type">
                <el-icon>
                  <component :is="getOperationIcon(row.operationType)" />
                </el-icon>
                <span>{{ row.operationName }}</span>
              </div>
              <div class="module">{{ row.module }}</div>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="{ row }">
            <div class="description-cell">
              <span :title="row.description">{{ row.description }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="级别" width="80">
          <template #default="{ row }">
            <el-tag
              :color="getLogLevelInfo(row.level).color"
              effect="light"
              size="small"
            >
              {{ getLogLevelInfo(row.level).label }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="IP地址" width="120">
          <template #default="{ row }">
            <div class="ip-cell">
              <span>{{ row.ip }}</span>
              <el-button
                link
                type="primary"
                size="small"
                @click.stop="showIpLocation(row.ip)"
              >
                定位
              </el-button>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="持续时间" width="100" sortable="custom">
          <template #default="{ row }">
            <span :class="{ 'slow-response': row.duration > 1000 }">
              {{ formatDuration(row.duration) }}
            </span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button
                link
                type="primary"
                size="small"
                @click.stop="viewDetail(row)"
              >
                详情
              </el-button>
              <el-button
                link
                type="success"
                size="small"
                @click.stop="exportSingleLog(row)"
              >
                导出
              </el-button>
              <el-dropdown trigger="click" @click.stop>
                <el-button
                  link
                  type="info"
                  size="small"
                >
                  更多
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="copyLogData(row)">复制数据</el-dropdown-item>
                    <el-dropdown-item @click="traceUser(row)">追踪用户</el-dropdown-item>
                    <el-dropdown-item @click="blockIp(row.ip)" divided>封禁IP</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="logs.length === 0 && !loading" class="empty-state">
      <el-empty description="暂无日志数据">
        <el-button type="primary" @click="$emit('refresh')">
          刷新数据
        </el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination-section">
      <div class="pagination-info">
        <span>共 {{ totalLogs }} 条记录</span>
        <span v-if="hasSelectedLogs">，已选择 {{ selectedLogsCount }} 条</span>
      </div>
      
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[20, 50, 100, 200]"
        :total="totalLogs"
        layout="sizes, prev, pager, next, jumper"
        @size-change="handlePageSizeChange"
        @current-change="handleCurrentPageChange"
      />
    </div>

    <!-- IP定位对话框 -->
    <el-dialog
      v-model="ipLocationVisible"
      title="IP地址定位"
      width="500px"
    >
      <div v-if="ipLocationData" class="ip-location-info">
        <div class="location-item">
          <span class="label">IP地址:</span>
          <span class="value">{{ ipLocationData.ip }}</span>
        </div>
        <div class="location-item">
          <span class="label">地理位置:</span>
          <span class="value">{{ ipLocationData.location }}</span>
        </div>
        <div class="location-item">
          <span class="label">ISP:</span>
          <span class="value">{{ ipLocationData.isp }}</span>
        </div>
        <div class="location-item">
          <span class="label">时区:</span>
          <span class="value">{{ ipLocationData.timezone }}</span>
        </div>
        <div class="location-item">
          <span class="label">风险等级:</span>
          <el-tag :type="getIpRiskType(ipLocationData.risk)">
            {{ ipLocationData.risk }}
          </el-tag>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Search,
  Download,
  Refresh,
  Sort,
  User,
  SwitchButton,
  Plus,
  Edit,
  Delete,
  View,
  Upload
} from '@element-plus/icons-vue'
import { useOperationLogs } from '@/composables/useOperationLogs'

// 定义事件
const emit = defineEmits([
  'refresh',
  'search',
  'filter',
  'sort',
  'page-change',
  'size-change',
  'toggle-selection',
  'toggle-select-all',
  'clear-selection',
  'view-detail',
  'batch-export'
])

// 使用组合式函数
const {
  LOG_LEVELS,
  OPERATION_TYPES,
  loading,
  logs,
  totalLogs,
  currentPage,
  pageSize,
  selectedLogs,
  queryParams,
  hasSelectedLogs,
  selectedLogsCount,
  formatDate,
  formatDuration,
  getLogLevelInfo,
  getOperationTypeInfo,
  getStatusType,
  getStatusText,
  searchLogs,
  filterLogs,
  sortLogs,
  toggleLogSelection,
  toggleSelectAll,
  clearSelection
} = useOperationLogs()

// 响应式数据
const searchKeyword = ref('')
const sortBy = ref('createdAt')
const sortOrder = ref('desc')
const ipLocationVisible = ref(false)
const ipLocationData = ref(null)

// 筛选表单
const filterForm = reactive({
  operationType: '',
  level: '',
  status: '',
  module: '',
  dateRange: []
})

// 获取操作图标
const getOperationIcon = (operationType) => {
  const iconMap = {
    login: User,
    logout: SwitchButton,
    create: Plus,
    update: Edit,
    delete: Delete,
    view: View,
    export: Download,
    upload: Upload
  }
  return iconMap[operationType] || User
}

// 格式化时间
const formatTime = (dateString) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleTimeString()
}

// 获取IP风险类型
const getIpRiskType = (risk) => {
  const riskMap = {
    '低': 'success',
    '中': 'warning',
    '高': 'danger'
  }
  return riskMap[risk] || 'info'
}

// 处理搜索
const handleSearch = (keyword) => {
  searchKeyword.value = keyword
  emit('search', keyword)
}

// 处理筛选
const handleFilter = () => {
  const filters = {
    ...filterForm,
    dateRange: filterForm.dateRange || []
  }
  emit('filter', filters)
}

// 处理排序
const handleSort = () => {
  emit('sort', sortBy.value, sortOrder.value)
}

// 切换排序顺序
const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'asc' ? 'desc' : 'asc'
  handleSort()
}

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedLogs.value = selection
}

// 处理行点击
const handleRowClick = (row) => {
  toggleLogSelection(row)
}

// 处理分页变化
const handlePageSizeChange = (size) => {
  emit('size-change', size)
}

const handleCurrentPageChange = (page) => {
  emit('page-change', page)
}

// 查看详情
const viewDetail = (row) => {
  emit('view-detail', row)
}

// 导出单条日志
const exportSingleLog = (log) => {
  // 这里可以实现单条日志导出
  ElMessage.success('单条日志导出功能开发中')
}

// 复制日志数据
const copyLogData = (log) => {
  const logData = JSON.stringify(log, null, 2)
  
  if (navigator.clipboard) {
    navigator.clipboard.writeText(logData).then(() => {
      ElMessage.success('日志数据已复制到剪贴板')
    })
  } else {
    ElMessage.info('请手动复制日志数据')
  }
}

// 追踪用户
const traceUser = (log) => {
  // 这里可以实现用户追踪功能
  ElMessage.info(`追踪用户 ${log.username} 的所有操作`)
}

// 封禁IP
const blockIp = (ip) => {
  ElMessage.warning(`IP地址 ${ip} 已加入封禁列表`)
}

// 显示IP定位
const showIpLocation = async (ip) => {
  // 模拟IP定位数据
  ipLocationData.value = {
    ip: ip,
    location: '中国 北京市 北京市',
    isp: '中国电信',
    timezone: 'UTC+8',
    risk: '低'
  }
  ipLocationVisible.value = true
}

// 监听搜索关键词变化
watch(searchKeyword, (keyword) => {
  if (keyword === '') {
    handleSearch('')
  }
}, { debounce: 300 })
</script>

<style scoped>
.log-list {
  padding: 0;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  flex-wrap: wrap;
  gap: 16px;
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
  flex-wrap: wrap;
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

.log-table-container {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.time-cell {
  font-size: 12px;
}

.time-cell .date {
  color: #303133;
  font-weight: 500;
  margin-bottom: 2px;
}

.time-cell .time {
  color: #909399;
  font-family: monospace;
}

.user-cell {
  font-size: 12px;
}

.user-cell .username {
  color: #303133;
  font-weight: 500;
  margin-bottom: 2px;
}

.user-cell .role {
  color: #909399;
}

.operation-cell {
  font-size: 12px;
}

.operation-type {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #303133;
  font-weight: 500;
  margin-bottom: 2px;
}

.operation-cell .module {
  color: #909399;
}

.description-cell {
  font-size: 13px;
  color: #606266;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ip-cell {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  font-family: monospace;
}

.slow-response {
  color: #f56c6c;
  font-weight: 500;
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
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-top: 16px;
}

.pagination-info {
  font-size: 14px;
  color: #606266;
}

.ip-location-info {
  padding: 20px 0;
}

.location-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.location-item:last-child {
  border-bottom: none;
}

.location-item .label {
  font-weight: 500;
  color: #303133;
}

.location-item .value {
  color: #606266;
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
  
  .pagination-section {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .table-actions {
    flex-direction: column;
    gap: 4px;
  }
}

/* 表格行悬停效果 */
:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f0f9ff !important;
}

/* 选中行样式 */
:deep(.el-table__row.selected) {
  background-color: #f0f9ff !important;
}

/* 加载状态 */
:deep(.el-loading-mask) {
  background-color: rgba(255, 255, 255, 0.9);
}

/* 动画效果 */
.log-table-container {
  animation: fadeInUp 0.5s ease-out;
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
