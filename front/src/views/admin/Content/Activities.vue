<template>
  <div class="activities-management">
    <div class="page-header">
      <div class="header-left">
        <h2>活动管理</h2>
        <p>管理和审核用户发布的活动内容</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="refreshActivities">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
        <el-button @click="exportActivities">
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
            placeholder="搜索标题、内容或组织者"
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
            <el-option label="已结束" value="finished" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="filterForm.type" placeholder="全部类型" clearable style="width: 150px">
            <el-option label="线下活动" value="offline" />
            <el-option label="线上活动" value="online" />
            <el-option label="混合活动" value="hybrid" />
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
    <div class="batch-actions" v-if="selectedActivities.length > 0">
      <div class="selected-info">
        已选择 {{ selectedActivities.length }} 个活动
      </div>
      <div class="action-buttons">
        <el-button type="success" @click="batchApprove">批量通过</el-button>
        <el-button type="danger" @click="batchReject">批量拒绝</el-button>
        <el-button type="warning" @click="batchCancel">批量取消</el-button>
      </div>
    </div>

    <!-- 活动列表 -->
    <div class="activities-table">
      <el-table
        v-loading="loading"
        :data="activities"
        @selection-change="handleSelectionChange"
        stripe
        style="width: 100%"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="活动标题" min-width="200">
          <template #default="{ row }">
            <div class="activity-title" @click="viewActivityDetail(row)">
              {{ row.title }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="organizer" label="组织者" width="120">
          <template #default="{ row }">
            <div class="organizer-info">
              <el-avatar :size="24" :src="row.organizerAvatar" />
              <span>{{ row.organizer }}</span>
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
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="participants" label="参与人数" width="100">
          <template #default="{ row }">
            {{ row.participants }}/{{ row.maxParticipants }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="发布时间" width="160">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="viewActivityDetail(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="text" 
              size="small" 
              @click="approveActivity(row)"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="text" 
              size="small" 
              @click="rejectActivity(row)"
            >
              拒绝
            </el-button>
            <el-button type="text" size="small" @click="editActivity(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 'approved'"
              type="text" 
              size="small" 
              @click="cancelActivity(row)"
            >
              取消
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

    <!-- 活动详情对话框 -->
    <ActivityDetailDialog
      v-model="detailDialogVisible"
      :activity="selectedActivity"
      @approve="handleApprove"
      @reject="handleReject"
      @cancel="handleCancel"
    />

    <!-- 拒绝理由对话框 -->
    <RejectDialog
      v-model="rejectDialogVisible"
      :activity="selectedActivity"
      @confirm="handleRejectConfirm"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Download, Search } from '@element-plus/icons-vue'
import ActivityDetailDialog from './components/ActivityDetailDialog.vue'
import RejectDialog from './components/RejectDialog.vue'

// 响应式数据
const loading = ref(false)
const activities = ref([])
const selectedActivities = ref([])
const selectedActivity = ref(null)
const detailDialogVisible = ref(false)
const rejectDialogVisible = ref(false)

// 筛选表单
const filterForm = reactive({
  keyword: '',
  status: '',
  type: '',
  dateRange: null
})

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

// 模拟数据
const mockActivities = [
  {
    id: 1,
    title: 'Vue 3 技术分享会',
    organizer: '张三',
    organizerAvatar: 'https://via.placeholder.com/24',
    type: 'online',
    status: 'pending',
    participants: 45,
    maxParticipants: 100,
    description: '分享Vue 3的最新特性和最佳实践',
    location: '线上直播',
    startTime: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(Date.now() + 3 * 24 * 60 * 60 * 1000 + 2 * 60 * 60 * 1000).toISOString(),
    createdAt: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 2,
    title: '前端开发者线下聚会',
    organizer: '李四',
    organizerAvatar: 'https://via.placeholder.com/24',
    type: 'offline',
    status: 'approved',
    participants: 28,
    maxParticipants: 50,
    description: '前端开发者技术交流和社交活动',
    location: '北京市朝阳区',
    startTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000 + 4 * 60 * 60 * 1000).toISOString(),
    createdAt: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString()
  },
  {
    id: 3,
    title: 'JavaScript 编程马拉松',
    organizer: '王五',
    organizerAvatar: 'https://via.placeholder.com/24',
    type: 'hybrid',
    status: 'rejected',
    participants: 0,
    maxParticipants: 200,
    description: '48小时编程挑战赛',
    location: '线上+线下',
    startTime: new Date(Date.now() + 14 * 24 * 60 * 60 * 1000).toISOString(),
    endTime: new Date(Date.now() + 16 * 24 * 60 * 60 * 1000).toISOString(),
    createdAt: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000).toISOString()
  }
]

// 获取类型标签类型
const getTypeTagType = (type) => {
  const typeMap = {
    online: 'success',
    offline: 'primary',
    hybrid: 'warning'
  }
  return typeMap[type] || 'info'
}

// 获取类型标签
const getTypeLabel = (type) => {
  const labelMap = {
    online: '线上活动',
    offline: '线下活动',
    hybrid: '混合活动'
  }
  return labelMap[type] || '其他'
}

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    pending: 'warning',
    approved: 'success',
    rejected: 'danger',
    finished: 'info',
    cancelled: 'info'
  }
  return typeMap[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status) => {
  const labelMap = {
    pending: '待审核',
    approved: '已通过',
    rejected: '已拒绝',
    finished: '已结束',
    cancelled: '已取消'
  }
  return labelMap[status] || '未知'
}

// 格式化时间
const formatTime = (timeString) => {
  return new Date(timeString).toLocaleString('zh-CN')
}

// 格式化日期时间
const formatDateTime = (timeString) => {
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取活动列表
const fetchActivities = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 应用筛选
    let filteredActivities = [...mockActivities]
    
    if (filterForm.keyword) {
      filteredActivities = filteredActivities.filter(activity => 
        activity.title.includes(filterForm.keyword) || 
        activity.organizer.includes(filterForm.keyword)
      )
    }
    
    if (filterForm.status) {
      filteredActivities = filteredActivities.filter(activity => activity.status === filterForm.status)
    }
    
    if (filterForm.type) {
      filteredActivities = filteredActivities.filter(activity => activity.type === filterForm.type)
    }
    
    // 分页
    pagination.total = filteredActivities.length
    const start = (pagination.page - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    activities.value = filteredActivities.slice(start, end)
    
  } catch (error) {
    ElMessage.error('获取活动列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  fetchActivities()
}

// 重置筛选
const resetFilter = () => {
  Object.assign(filterForm, {
    keyword: '',
    status: '',
    type: '',
    dateRange: null
  })
  pagination.page = 1
  fetchActivities()
}

// 刷新
const refreshActivities = () => {
  fetchActivities()
}

// 导出
const exportActivities = () => {
  ElMessage.success('导出功能开发中')
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedActivities.value = selection
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  fetchActivities()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  fetchActivities()
}

// 查看活动详情
const viewActivityDetail = (activity) => {
  selectedActivity.value = activity
  detailDialogVisible.value = true
}

// 通过活动
const approveActivity = async (activity) => {
  try {
    await ElMessageBox.confirm('确定要通过这个活动吗？', '确认操作')
    activity.status = 'approved'
    ElMessage.success('活动已通过')
    fetchActivities()
  } catch {
    // 用户取消
  }
}

// 拒绝活动
const rejectActivity = (activity) => {
  selectedActivity.value = activity
  rejectDialogVisible.value = true
}

// 编辑活动
const editActivity = (activity) => {
  ElMessage.info('编辑功能开发中')
}

// 取消活动
const cancelActivity = async (activity) => {
  try {
    await ElMessageBox.confirm('确定要取消这个活动吗？', '确认取消', {
      type: 'warning'
    })
    activity.status = 'cancelled'
    ElMessage.success('活动已取消')
    fetchActivities()
  } catch {
    // 用户取消
  }
}

// 批量操作
const batchApprove = async () => {
  try {
    await ElMessageBox.confirm(`确定要通过选中的 ${selectedActivities.value.length} 个活动吗？`, '批量操作')
    selectedActivities.value.forEach(activity => {
      activity.status = 'approved'
    })
    ElMessage.success('批量操作成功')
    fetchActivities()
  } catch {
    // 用户取消
  }
}

const batchReject = () => {
  ElMessage.info('批量拒绝功能开发中')
}

const batchCancel = async () => {
  try {
    await ElMessageBox.confirm(`确定要取消选中的 ${selectedActivities.value.length} 个活动吗？`, '批量取消', {
      type: 'warning'
    })
    selectedActivities.value.forEach(activity => {
      activity.status = 'cancelled'
    })
    ElMessage.success('批量取消成功')
    fetchActivities()
  } catch {
    // 用户取消
  }
}

// 处理通过操作
const handleApprove = (activity) => {
  activity.status = 'approved'
  detailDialogVisible.value = false
  ElMessage.success('活动已通过')
  fetchActivities()
}

// 处理拒绝操作
const handleReject = (activity) => {
  selectedActivity.value = activity
  detailDialogVisible.value = false
  rejectDialogVisible.value = true
}

// 处理取消操作
const handleCancel = (activity) => {
  activity.status = 'cancelled'
  detailDialogVisible.value = false
  ElMessage.success('活动已取消')
  fetchActivities()
}

// 处理拒绝确认
const handleRejectConfirm = (activity, reason) => {
  activity.status = 'rejected'
  activity.rejectReason = reason
  rejectDialogVisible.value = false
  ElMessage.success('活动已拒绝')
  fetchActivities()
}

// 组件挂载
onMounted(() => {
  fetchActivities()
})
</script>

<style scoped>
.activities-management {
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

.activities-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.activity-title {
  color: #409eff;
  cursor: pointer;
  transition: color 0.3s;
}

.activity-title:hover {
  color: #66b1ff;
}

.organizer-info {
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
  .activities-management {
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
