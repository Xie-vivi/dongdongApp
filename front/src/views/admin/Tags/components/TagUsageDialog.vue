<template>
  <el-dialog
    v-model="visible"
    :title="`标签使用详情 - ${tag?.name}`"
    width="80%"
    :before-close="handleClose"
  >
    <div class="usage-container" v-loading="loading">
      <!-- 统计概览 -->
      <div class="stats-overview">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic
                title="总使用次数"
                :value="stats.totalUsage"
                :precision="0"
              >
                <template #suffix>
                  <el-icon><trend-charts /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic
                title="关联内容数"
                :value="stats.contentCount"
                :precision="0"
              >
                <template #suffix>
                  <el-icon><document /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic
                title="使用用户数"
                :value="stats.userCount"
                :precision="0"
              >
                <template #suffix>
                  <el-icon><user /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card class="stat-card">
              <el-statistic
                title="本月新增"
                :value="stats.monthlyIncrease"
                :precision="0"
              >
                <template #suffix>
                  <el-icon><arrow-up /></el-icon>
                </template>
              </el-statistic>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 使用趋势图表 -->
      <div class="usage-chart">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>使用趋势</span>
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="7d">近7天</el-radio-button>
                <el-radio-button label="30d">近30天</el-radio-button>
                <el-radio-button label="90d">近90天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="chartRef" style="height: 300px;"></div>
        </el-card>
      </div>

      <!-- 详细使用记录 -->
      <div class="usage-details">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>使用记录</span>
              <div class="header-actions">
                <el-select
                  v-model="filterType"
                  placeholder="筛选类型"
                  size="small"
                  style="width: 120px; margin-right: 10px;"
                >
                  <el-option label="全部" value="" />
                  <el-option label="文章" value="post" />
                  <el-option label="动态" value="moment" />
                  <el-option label="活动" value="activity" />
                  <el-option label="用户" value="user" />
                </el-select>
                <el-button size="small" @click="exportUsageData">
                  <el-icon><download /></el-icon>
                  导出
                </el-button>
              </div>
            </div>
          </template>

          <el-table
            :data="usageList"
            style="width: 100%"
            v-loading="tableLoading"
          >
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="type" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getTypeTagType(row.type)">
                  {{ getTypeLabel(row.type) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" min-width="200">
              <template #default="{ row }">
                <el-link @click="viewContent(row)" type="primary">
                  {{ row.title }}
                </el-link>
              </template>
            </el-table-column>
            <el-table-column prop="author" label="作者" width="120" />
            <el-table-column prop="createdAt" label="创建时间" width="160">
              <template #default="{ row }">
                {{ formatDate(row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusTagType(row.status)">
                  {{ getStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="{ row }">
                <el-button
                  size="small"
                  type="primary"
                  link
                  @click="viewContent(row)"
                >
                  查看
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  link
                  @click="removeTag(row)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-container">
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
        </el-card>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  TrendCharts,
  Document,
  User,
  ArrowUp,
  Download
} from '@element-plus/icons-vue'
import * as echarts from 'echarts'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  tag: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const loading = ref(false)
const tableLoading = ref(false)
const chartRef = ref()
const chartInstance = ref(null)
const chartPeriod = ref('30d')
const filterType = ref('')

const stats = reactive({
  totalUsage: 0,
  contentCount: 0,
  userCount: 0,
  monthlyIncrease: 0
})

const usageList = ref([])
const pagination = reactive({
  page: 1,
  pageSize: 20,
  total: 0
})

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.tag) {
    loadUsageData()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
  if (!val && chartInstance.value) {
    chartInstance.value.dispose()
    chartInstance.value = null
  }
})

watch(chartPeriod, () => {
  if (visible.value) {
    loadChartData()
  }
})

watch(filterType, () => {
  if (visible.value) {
    loadUsageList()
  }
})

const loadUsageData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 模拟数据
    Object.assign(stats, {
      totalUsage: 1234,
      contentCount: 567,
      userCount: 89,
      monthlyIncrease: 45
    })

    await loadUsageList()
    await nextTick()
    await loadChartData()
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadUsageList = async () => {
  tableLoading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟数据
    const mockData = [
      {
        id: 1,
        type: 'post',
        title: '技术分享：Vue 3 组合式API最佳实践',
        author: '张三',
        createdAt: '2024-01-15 10:30:00',
        status: 'published'
      },
      {
        id: 2,
        type: 'moment',
        title: '今天学习了新的前端框架',
        author: '李四',
        createdAt: '2024-01-14 15:20:00',
        status: 'published'
      },
      {
        id: 3,
        type: 'activity',
        title: '前端技术交流会',
        author: '王五',
        createdAt: '2024-01-13 09:15:00',
        status: 'ongoing'
      }
    ]

    usageList.value = mockData
    pagination.total = 50
  } catch (error) {
    ElMessage.error('加载使用记录失败')
  } finally {
    tableLoading.value = false
  }
}

const loadChartData = async () => {
  if (!chartRef.value) return

  if (!chartInstance.value) {
    chartInstance.value = echarts.init(chartRef.value)
  }

  // 模拟图表数据
  const dates = []
  const values = []
  const days = chartPeriod.value === '7d' ? 7 : chartPeriod.value === '30d' ? 30 : 90

  for (let i = days - 1; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' }))
    values.push(Math.floor(Math.random() * 50) + 10)
  }

  const option = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      name: '使用次数',
      type: 'line',
      smooth: true,
      data: values,
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [{
            offset: 0,
            color: 'rgba(64, 158, 255, 0.3)'
          }, {
            offset: 1,
            color: 'rgba(64, 158, 255, 0.1)'
          }]
        }
      }
    }]
  }

  chartInstance.value.setOption(option)
}

const getTypeLabel = (type) => {
  const labels = {
    post: '文章',
    moment: '动态',
    activity: '活动',
    user: '用户'
  }
  return labels[type] || type
}

const getTypeTagType = (type) => {
  const types = {
    post: 'primary',
    moment: 'success',
    activity: 'warning',
    user: 'info'
  }
  return types[type] || ''
}

const getStatusLabel = (status) => {
  const labels = {
    published: '已发布',
    draft: '草稿',
    ongoing: '进行中',
    ended: '已结束'
  }
  return labels[status] || status
}

const getStatusTagType = (status) => {
  const types = {
    published: 'success',
    draft: 'info',
    ongoing: 'warning',
    ended: 'danger'
  }
  return types[status] || ''
}

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

const viewContent = (row) => {
  ElMessage.info(`查看${getTypeLabel(row.type)}：${row.title}`)
}

const removeTag = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要从"${row.title}"中移除此标签吗？`,
      '确认移除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    ElMessage.success('标签移除成功')
    loadUsageList()
  } catch (error) {
    // 用户取消
  }
}

const exportUsageData = () => {
  ElMessage.success('导出功能开发中...')
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.page = 1
  loadUsageList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadUsageList()
}

const handleClose = () => {
  visible.value = false
}

onMounted(() => {
  // 监听窗口大小变化，重新调整图表
  window.addEventListener('resize', () => {
    if (chartInstance.value) {
      chartInstance.value.resize()
    }
  })
})
</script>

<style scoped>
.usage-container {
  padding: 20px 0;
}

.stats-overview {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.usage-chart {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}

:deep(.el-statistic__content) {
  font-size: 24px;
  font-weight: bold;
}

:deep(.el-statistic__title) {
  font-size: 14px;
  color: #909399;
}
</style>
