<template>
  <div class="user-detail">
    <el-tabs v-model="activeTab" class="detail-tabs">
      <!-- 基本信息 -->
      <el-tab-pane label="基本信息" name="basic">
        <div class="basic-info">
          <div class="user-profile">
            <div class="avatar-section">
              <el-avatar :src="user.avatar" :size="120" />
              <div class="avatar-actions">
                <el-button size="small" @click="changeAvatar">更换头像</el-button>
              </div>
            </div>
            
            <div class="profile-info">
              <div class="user-title">
                <h3>{{ user.username }}</h3>
                <el-tag :type="getStatusTagType(user.status)" size="large">
                  <el-icon class="status-icon">
                    <component :is="getUserStatusInfo(user.status).icon" />
                  </el-icon>
                  {{ user.statusName }}
                </el-tag>
              </div>
              
              <div class="user-meta">
                <div class="meta-item">
                  <span class="label">用户ID:</span>
                  <span class="value">{{ user.id }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">真实姓名:</span>
                  <span class="value">{{ user.realName }}</span>
                  <el-button
                    v-if="!showFullData"
                    type="text"
                    size="small"
                    @click="toggleFullData"
                  >
                    <el-icon><View /></el-icon>
                    显示完整信息
                  </el-button>
                </div>
                <div class="meta-item">
                  <span class="label">用户角色:</span>
                  <el-tag :type="getRoleTagType(user.role)">{{ user.roleName }}</el-tag>
                </div>
                <div class="meta-item">
                  <span class="label">认证状态:</span>
                  <el-icon v-if="user.verified" color="#67c23a" size="16">
                    <CircleCheck />
                  </el-icon>
                  <el-icon v-else color="#f56c6c" size="16">
                    <CircleClose />
                  </el-icon>
                  <span>{{ user.verified ? '已认证' : '未认证' }}</span>
                </div>
                <div class="meta-item">
                  <span class="label">风险等级:</span>
                  <el-tag :type="getRiskLevelType(user.riskLevel)">
                    {{ getRiskLevelText(user.riskLevel) }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>

          <div class="contact-info">
            <h4>联系方式</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="邮箱">
                <div class="contact-item">
                  <el-icon><Message /></el-icon>
                  {{ user.email }}
                  <el-button
                    v-if="!showFullData"
                    type="text"
                    size="small"
                    @click="toggleFullData"
                  >
                    显示完整邮箱
                  </el-button>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="手机号">
                <div class="contact-item">
                  <el-icon><Phone /></el-icon>
                  {{ user.phone }}
                  <el-button
                    v-if="!showFullData"
                    type="text"
                    size="small"
                    @click="toggleFullData"
                  >
                    显示完整手机号
                  </el-button>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="地址" :span="2">
                <div class="contact-item">
                  <el-icon><Location /></el-icon>
                  {{ user.address }}
                  <el-button
                    v-if="!showFullData"
                    type="text"
                    size="small"
                    @click="toggleFullData"
                  >
                    显示完整地址
                  </el-button>
                </div>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="personal-info">
            <h4>个人信息</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="身份证号">
                <div class="sensitive-item">
                  {{ user.idCard }}
                  <el-button
                    v-if="!showFullData"
                    type="text"
                    size="small"
                    @click="toggleFullData"
                  >
                    显示完整身份证号
                  </el-button>
                </div>
              </el-descriptions-item>
              <el-descriptions-item label="出生日期">
                {{ user.birthDate }}
              </el-descriptions-item>
              <el-descriptions-item label="性别">
                {{ user.gender === 'male' ? '男' : '女' }}
              </el-descriptions-item>
              <el-descriptions-item label="职业">
                {{ user.occupation }}
              </el-descriptions-item>
              <el-descriptions-item label="教育程度">
                {{ user.education }}
              </el-descriptions-item>
              <el-descriptions-item label="兴趣爱好">
                <el-tag
                  v-for="interest in user.interests"
                  :key="interest"
                  size="small"
                  class="interest-tag"
                >
                  {{ interest }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="个人签名" :span="2">
                {{ user.signature }}
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="account-info">
            <h4>账户信息</h4>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="注册时间">
                {{ formatDate(user.registrationTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="最后登录">
                {{ formatDate(user.lastLoginTime) }}
              </el-descriptions-item>
              <el-descriptions-item label="登录次数">
                {{ user.loginCount }} 次
              </el-descriptions-item>
              <el-descriptions-item label="发帖数量">
                {{ user.postCount }} 篇
              </el-descriptions-item>
              <el-descriptions-item label="活跃度评分">
                <el-progress
                  :percentage="user.activityScore"
                  :color="getActivityColor(user.activityScore)"
                  :show-text="true"
                  :stroke-width="8"
                />
              </el-descriptions-item>
              <el-descriptions-item label="账户状态">
                <el-tag :type="user.banned ? 'danger' : 'success'">
                  {{ user.banned ? '已封禁' : '正常' }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>

          <div class="admin-notes">
            <h4>管理员备注</h4>
            <el-input
              v-model="localNotes"
              type="textarea"
              :rows="4"
              placeholder="添加管理员备注..."
              @blur="saveNotes"
            />
          </div>
        </div>
      </el-tab-pane>

      <!-- 活动数据 -->
      <el-tab-pane label="活动数据" name="activity">
        <div class="activity-data">
          <div class="activity-overview">
            <h4>活动概览</h4>
            <div class="overview-cards">
              <div class="overview-card">
                <div class="card-icon">
                  <el-icon><Timer /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-number">{{ user.loginCount }}</div>
                  <div class="card-label">总登录次数</div>
                </div>
              </div>
              
              <div class="overview-card">
                <div class="card-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-number">{{ user.postCount }}</div>
                  <div class="card-label">发布帖子</div>
                </div>
              </div>
              
              <div class="overview-card">
                <div class="card-icon">
                  <el-icon><ChatLineRound /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-number">{{ Math.floor(user.postCount * 2.5) }}</div>
                  <div class="card-label">评论数量</div>
                </div>
              </div>
              
              <div class="overview-card">
                <div class="card-icon">
                  <el-icon><Star /></el-icon>
                </div>
                <div class="card-content">
                  <div class="card-number">{{ Math.floor(user.postCount * 5.8) }}</div>
                  <div class="card-label">获得点赞</div>
                </div>
              </div>
            </div>
          </div>

          <div class="activity-chart">
            <h4>活动趋势</h4>
            <div class="chart-placeholder">
              <el-empty description="活动趋势图表开发中...">
                <el-button type="primary">查看详细数据</el-button>
              </el-empty>
            </div>
          </div>

          <div class="recent-activities">
            <h4>最近活动</h4>
            <el-timeline>
              <el-timeline-item
                v-for="activity in recentActivities"
                :key="activity.id"
                :timestamp="formatDate(activity.timestamp)"
                :type="getActivityType(activity.type)"
              >
                <div class="activity-item">
                  <div class="activity-title">{{ activity.title }}</div>
                  <div class="activity-description">{{ activity.description }}</div>
                  <div class="activity-meta">
                    <span>IP: {{ activity.ip }}</span>
                    <span>设备: {{ activity.device }}</span>
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </el-tab-pane>

      <!-- 安全分析 -->
      <el-tab-pane label="安全分析" name="security">
        <div class="security-analysis">
          <div class="security-overview">
            <h4>安全概览</h4>
            <div class="security-cards">
              <div class="security-card">
                <div class="card-header">
                  <el-icon><ShoppingBag /></el-icon>
                  <span>账户安全</span>
                </div>
                <div class="card-score">
                  <el-progress
                    type="circle"
                    :percentage="securityScore"
                    :color="getSecurityColor(securityScore)"
                    :width="80"
                  />
                </div>
                <div class="card-status">
                  {{ getSecurityStatus(securityScore) }}
                </div>
              </div>

              <div class="security-card">
                <div class="card-header">
                  <el-icon><Warning /></el-icon>
                  <span>风险指标</span>
                </div>
                <div class="risk-indicators">
                  <div class="indicator">
                    <span>异常登录</span>
                    <el-tag type="warning" size="small">3次</el-tag>
                  </div>
                  <div class="indicator">
                    <span>多IP登录</span>
                    <el-tag type="info" size="small">1次</el-tag>
                  </div>
                  <div class="indicator">
                    <span>密码强度</span>
                    <el-tag type="success" size="small">强</el-tag>
                  </div>
                </div>
              </div>

              <div class="security-card">
                <div class="card-header">
                  <el-icon><Lock /></el-icon>
                  <span>隐私设置</span>
                </div>
                <div class="privacy-settings">
                  <div class="setting">
                    <span>邮箱公开</span>
                    <el-switch v-model="privacySettings.emailPublic" />
                  </div>
                  <div class="setting">
                    <span>手机公开</span>
                    <el-switch v-model="privacySettings.phonePublic" />
                  </div>
                  <div class="setting">
                    <span>地址公开</span>
                    <el-switch v-model="privacySettings.addressPublic" />
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="login-history">
            <h4>登录历史</h4>
            <el-table :data="loginHistory" stripe>
              <el-table-column prop="loginTime" label="登录时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.loginTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="ip" label="IP地址" width="140" />
              <el-table-column prop="location" label="登录地点" />
              <el-table-column prop="device" label="设备信息" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.success ? 'success' : 'danger'" size="small">
                    {{ row.success ? '成功' : '失败' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="duration" label="在线时长" width="100">
                <template #default="{ row }">
                  {{ formatDuration(row.duration) }}
                </template>
              </el-table-column>
            </el-table>
          </div>

          <div class="security-events">
            <h4>安全事件</h4>
            <el-timeline>
              <el-timeline-item
                v-for="event in securityEvents"
                :key="event.id"
                :timestamp="formatDate(event.timestamp)"
                :type="getEventType(event.severity)"
              >
                <div class="event-item">
                  <div class="event-title">{{ event.title }}</div>
                  <div class="event-description">{{ event.description }}</div>
                  <div class="event-status">
                    <el-tag :type="getEventTagType(event.status)" size="small">
                      {{ event.status }}
                    </el-tag>
                  </div>
                </div>
              </el-timeline-item>
            </el-timeline>
          </div>
        </div>
      </el-tab-pane>

      <!-- 数据导出 -->
      <el-tab-pane label="数据导出" name="export">
        <div class="data-export">
          <div class="export-info">
            <h4>数据导出说明</h4>
            <el-alert
              title="GDPR合规说明"
              type="info"
              description="根据GDPR法规，用户有权获取其个人数据的完整副本。导出的数据包含所有个人信息、活动记录和相关设置。"
              :closable="false"
              show-icon
            />
          </div>

          <div class="export-options">
            <h4>导出选项</h4>
            <el-form :model="exportForm" label-width="120px">
              <el-form-item label="导出格式">
                <el-radio-group v-model="exportForm.format">
                  <el-radio label="json">JSON格式</el-radio>
                  <el-radio label="csv">CSV格式</el-radio>
                  <el-radio label="excel">Excel格式</el-radio>
                  <el-radio label="pdf">PDF格式</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="数据范围">
                <el-checkbox-group v-model="exportForm.dataTypes">
                  <el-checkbox label="basic">基本信息</el-checkbox>
                  <el-checkbox label="activity">活动数据</el-checkbox>
                  <el-checkbox label="login">登录记录</el-checkbox>
                  <el-checkbox label="posts">发布内容</el-checkbox>
                  <el-checkbox label="comments">评论记录</el-checkbox>
                  <el-checkbox label="settings">账户设置</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item label="时间范围">
                <el-date-picker
                  v-model="exportForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                />
              </el-form-item>

              <el-form-item label="包含敏感数据">
                <el-switch
                  v-model="exportForm.includeSensitive"
                  active-text="包含"
                  inactive-text="不包含"
                />
                <div class="form-tip">
                  仅在有合法权限时才包含敏感数据
                </div>
              </el-form-item>
            </el-form>
          </div>

          <div class="export-actions">
            <el-button type="primary" @click="exportUserData" :loading="exporting">
              <el-icon><Download /></el-icon>
              开始导出
            </el-button>
            <el-button @click="previewExportData">
              <el-icon><View /></el-icon>
              预览数据
            </el-button>
          </div>

          <div class="export-history" v-if="exportHistory.length > 0">
            <h4>导出历史</h4>
            <el-table :data="exportHistory" stripe>
              <el-table-column prop="exportTime" label="导出时间" width="180">
                <template #default="{ row }">
                  {{ formatDate(row.exportTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="format" label="格式" width="100" />
              <el-table-column prop="size" label="文件大小" width="120" />
              <el-table-column prop="recordCount" label="记录数" width="100" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'completed' ? 'success' : 'warning'" size="small">
                    {{ row.status === 'completed' ? '已完成' : '处理中' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button
                    v-if="row.status === 'completed'"
                    type="primary"
                    size="small"
                    @click="downloadExport(row)"
                  >
                    下载
                  </el-button>
                  <el-button
                    type="danger"
                    size="small"
                    @click="deleteExport(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="editUser">
        <el-icon><Edit /></el-icon>
        编辑用户
      </el-button>
      <el-button type="warning" @click="updateUserStatus">
        <el-icon><Tools /></el-icon>
        更新状态
      </el-button>
      <el-button type="info" @click="resetPassword">
        <el-icon><Key /></el-icon>
        重置密码
      </el-button>
      <el-button type="success" @click="exportUserData">
        <el-icon><Download /></el-icon>
        导出数据
      </el-button>
      <el-button type="danger" @click="deleteUser">
        <el-icon><Delete /></el-icon>
        删除用户
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Message,
  Phone,
  Location,
  CircleCheck,
  CircleClose,
  View,
  Timer,
  Document,
  ChatLineRound,
  Star,
  ShoppingBag,
  Warning,
  Lock,
  Edit,
  Tools,
  Key,
  Download,
  Delete
} from '@element-plus/icons-vue'
import { useUserManagement } from '@/composables/useUserManagement'

// Props
const props = defineProps({
  user: {
    type: Object,
    required: true
  },
  showFullData: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits([
  'edit',
  'update-status',
  'export-data',
  'close'
])

// 使用组合式函数
const {
  formatDate,
  getUserStatusInfo,
  getUserRoleInfo,
  getRiskLevelType,
  getRiskLevelText
} = useUserManagement()

// 响应式数据
const activeTab = ref('basic')
const exporting = ref(false)
const securityScore = ref(85)
const localNotes = ref('')
const privacySettings = reactive({
  emailPublic: false,
  phonePublic: false,
  addressPublic: false
})

const exportForm = reactive({
  format: 'json',
  dataTypes: ['basic', 'activity'],
  dateRange: [],
  includeSensitive: false
})

// 初始化本地备注
watch(() => props.user, (newUser) => {
  if (newUser) {
    localNotes.value = newUser.notes || ''
  }
}, { immediate: true })

// 模拟数据
const recentActivities = ref([
  {
    id: 1,
    title: '发布新帖子',
    description: '发布了关于技术分享的帖子',
    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.100',
    device: 'Chrome / Windows',
    type: 'post'
  },
  {
    id: 2,
    title: '修改个人资料',
    description: '更新了个人签名和头像',
    timestamp: new Date(Date.now() - 5 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.100',
    device: 'Chrome / Windows',
    type: 'profile'
  },
  {
    id: 3,
    title: '登录系统',
    description: '通过网页端登录',
    timestamp: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.100',
    device: 'Chrome / Windows',
    type: 'login'
  }
])

const loginHistory = ref([
  {
    loginTime: new Date(Date.now() - 2 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.100',
    location: '中国 北京市',
    device: 'Chrome / Windows',
    success: true,
    duration: 180
  },
  {
    loginTime: new Date(Date.now() - 24 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.101',
    location: '中国 上海市',
    device: 'Safari / iPhone',
    success: true,
    duration: 45
  },
  {
    loginTime: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString(),
    ip: '192.168.1.102',
    location: '中国 深圳市',
    device: 'Chrome / Windows',
    success: false,
    duration: 0
  }
])

const securityEvents = ref([
  {
    id: 1,
    title: '异常登录检测',
    description: '检测到来自异常地点的登录尝试',
    timestamp: new Date(Date.now() - 48 * 60 * 60 * 1000).toISOString(),
    severity: 'warning',
    status: '已处理'
  },
  {
    id: 2,
    title: '密码修改',
    description: '用户成功修改了登录密码',
    timestamp: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
    severity: 'info',
    status: '正常'
  }
])

const exportHistory = ref([
  {
    id: 1,
    exportTime: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000).toISOString(),
    format: 'JSON',
    size: '2.3MB',
    recordCount: 1250,
    status: 'completed',
    downloadUrl: '/api/exports/user_1_20240108.json'
  }
])

// 计算属性和方法
const getStatusTagType = (status) => {
  const typeMap = {
    active: 'success',
    disabled: 'danger',
    pending: 'warning',
    suspended: 'info'
  }
  return typeMap[status] || 'info'
}

const getRoleTagType = (role) => {
  const typeMap = {
    admin: 'danger',
    editor: 'warning',
    reviewer: 'info',
    user: 'success'
  }
  return typeMap[role] || 'info'
}

const getActivityColor = (score) => {
  if (score >= 80) return '#67c23a'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}

const getSecurityColor = (score) => {
  if (score >= 80) return '#67c23a'
  if (score >= 60) return '#e6a23c'
  return '#f56c6c'
}

const getSecurityStatus = (score) => {
  if (score >= 80) return '安全'
  if (score >= 60) return '中等'
  return '风险'
}

const getActivityType = (type) => {
  const typeMap = {
    post: 'success',
    comment: 'primary',
    login: 'info',
    profile: 'warning'
  }
  return typeMap[type] || 'info'
}

const getEventType = (severity) => {
  const typeMap = {
    danger: 'danger',
    warning: 'warning',
    info: 'info'
  }
  return typeMap[severity] || 'info'
}

const getEventTagType = (status) => {
  const typeMap = {
    '已处理': 'success',
    '正常': 'success',
    '处理中': 'warning',
    '待处理': 'warning'
  }
  return typeMap[status] || 'info'
}

const formatDuration = (minutes) => {
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return `${hours}小时${mins}分钟`
}

// 操作方法
const toggleFullData = () => {
  // 这里应该触发显示完整数据的逻辑
  ElMessage.info('需要管理员权限才能查看完整数据')
}

const changeAvatar = () => {
  ElMessage.info('更换头像功能开发中...')
}

const saveNotes = () => {
  ElMessage.success('备注保存成功')
}

const editUser = () => {
  emit('edit', props.user)
}

const updateUserStatus = () => {
  emit('update-status', props.user)
}

const resetPassword = () => {
  ElMessageBox.confirm(
    `确定要重置用户 ${props.user.username} 的密码吗？`,
    '重置密码',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    ElMessage.success('密码重置成功，新密码已发送到用户邮箱')
  })
}

const exportUserData = () => {
  emit('export-data', props.user.id, exportForm.format)
}

const previewExportData = () => {
  ElMessage.info('数据预览功能开发中...')
}

const downloadExport = (exportRecord) => {
  // 模拟下载
  const link = document.createElement('a')
  link.href = exportRecord.downloadUrl
  link.download = `user_data_${props.user.id}_${exportRecord.format}`
  link.click()
  ElMessage.success('文件下载开始')
}

const deleteExport = (exportRecord) => {
  ElMessageBox.confirm(
    '确定要删除这个导出文件吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = exportHistory.value.findIndex(item => item.id === exportRecord.id)
    if (index > -1) {
      exportHistory.value.splice(index, 1)
    }
    ElMessage.success('导出文件已删除')
  })
}

const deleteUser = () => {
  ElMessageBox.confirm(
    `确定要删除用户 ${props.user.username} 吗？此操作不可恢复！`,
    '删除用户',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    }
  ).then(() => {
    ElMessage.success('用户删除成功')
    emit('close')
  })
}
</script>

<style scoped>
.user-detail {
  padding: 20px;
}

.detail-tabs {
  margin-bottom: 20px;
}

.basic-info {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.user-profile {
  display: flex;
  gap: 24px;
  padding: 24px;
  background: #f8f9fa;
  border-radius: 8px;
}

.avatar-section {
  text-align: center;
}

.avatar-actions {
  margin-top: 12px;
}

.profile-info {
  flex: 1;
}

.user-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.user-title h3 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.status-icon {
  margin-right: 4px;
}

.user-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-item .label {
  color: #606266;
  font-weight: 500;
}

.meta-item .value {
  color: #303133;
}

.contact-info,
.personal-info,
.account-info,
.admin-notes {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.contact-info h4,
.personal-info h4,
.account-info h4,
.admin-notes h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.contact-item,
.sensitive-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.interest-tag {
  margin-right: 8px;
  margin-bottom: 4px;
}

.activity-data {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.activity-overview,
.activity-chart,
.recent-activities {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.activity-overview h4,
.activity-chart h4,
.recent-activities h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.overview-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.card-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  border-radius: 8px;
  color: #fff;
  font-size: 20px;
}

.card-content {
  flex: 1;
}

.card-number {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.card-label {
  font-size: 12px;
  color: #606266;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.activity-item {
  padding: 8px 0;
}

.activity-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.activity-description {
  color: #606266;
  font-size: 14px;
  margin-bottom: 4px;
}

.activity-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 16px;
}

.security-analysis {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.security-overview,
.login-history,
.security-events {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.security-overview h4,
.login-history h4,
.security-events h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.security-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.security-card {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-weight: 600;
  color: #303133;
}

.card-score {
  text-align: center;
  margin-bottom: 12px;
}

.card-status {
  text-align: center;
  font-weight: 600;
  color: #303133;
}

.risk-indicators,
.privacy-settings {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.indicator,
.setting {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
}

.event-item {
  padding: 8px 0;
}

.event-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.event-description {
  color: #606266;
  font-size: 14px;
  margin-bottom: 8px;
}

.data-export {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.export-info,
.export-options,
.export-actions,
.export-history {
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.export-info h4,
.export-options h4,
.export-history h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.export-actions {
  display: flex;
  gap: 12px;
}

.action-buttons {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  justify-content: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-detail {
    padding: 16px;
  }
  
  .user-profile {
    flex-direction: column;
    text-align: center;
  }
  
  .user-meta {
    grid-template-columns: 1fr;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .security-cards {
    grid-template-columns: 1fr;
  }
  
  .action-buttons {
    flex-wrap: wrap;
    justify-content: center;
  }
}

/* 动画效果 */
.overview-card,
.security-card {
  transition: all 0.3s ease;
}

.overview-card:hover,
.security-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
</style>
