<template>
  <el-dialog
    v-model="visible"
    title="用户详情"
    width="800px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="user" class="user-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>头像：</label>
            <el-avatar :size="60" :src="user.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="info-item">
            <label>用户名：</label>
            <span>{{ user.username || '-' }}</span>
          </div>
          <div class="info-item">
            <label>昵称：</label>
            <span>{{ user.nickname || '-' }}</span>
          </div>
          <div class="info-item">
            <label>邮箱：</label>
            <span>{{ user.email || '-' }}</span>
          </div>
          <div class="info-item">
            <label>手机号：</label>
            <span>{{ user.phone || '-' }}</span>
          </div>
          <div class="info-item">
            <label>性别：</label>
            <span>{{ getGenderText(user.gender) }}</span>
          </div>
          <div class="info-item">
            <label>生日：</label>
            <span>{{ user.birthday || '-' }}</span>
          </div>
          <div class="info-item">
            <label>状态：</label>
            <StatusTag :status="user.status" type="user" />
          </div>
        </div>
      </div>
      
      <!-- 统计信息 -->
      <div class="detail-section">
        <h3 class="section-title">统计信息</h3>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">{{ user.followersCount || 0 }}</div>
            <div class="stat-label">粉丝数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ user.followingCount || 0 }}</div>
            <div class="stat-label">关注数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ user.postsCount || 0 }}</div>
            <div class="stat-label">帖子数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ user.activitiesCount || 0 }}</div>
            <div class="stat-label">活动数</div>
          </div>
        </div>
      </div>
      
      <!-- 时间信息 -->
      <div class="detail-section">
        <h3 class="section-title">时间信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>注册时间：</label>
            <span>{{ formatDateTime(user.createdAt) }}</span>
          </div>
          <div class="info-item">
            <label>最后登录：</label>
            <span>{{ formatDateTime(user.lastLoginAt) }}</span>
          </div>
          <div class="info-item">
            <label>更新时间：</label>
            <span>{{ formatDateTime(user.updatedAt) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 个人简介 -->
      <div class="detail-section" v-if="user.bio">
        <h3 class="section-title">个人简介</h3>
        <div class="bio-content">
          {{ user.bio }}
        </div>
      </div>
      
      <!-- 兴趣标签 -->
      <div class="detail-section" v-if="user.tags && user.tags.length > 0">
        <h3 class="section-title">兴趣标签</h3>
        <div class="tags-content">
          <el-tag
            v-for="tag in user.tags"
            :key="tag.id"
            :color="tag.color"
            class="tag-item"
          >
            {{ tag.name }}
          </el-tag>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑用户
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { User, Edit } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  user: {
    type: Object,
    default: null
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue', 'edit'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = {
    male: '男',
    female: '女',
    unknown: '未知'
  }
  return genderMap[gender] || '未知'
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 处理关闭
const handleClose = () => {
  visible.value = false
}

// 处理编辑
const handleEdit = () => {
  emit('edit', props.user)
  handleClose()
}
</script>

<style scoped>
.user-detail {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 12px;
}

.user-detail::-webkit-scrollbar {
  width: 6px;
}

.user-detail::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.detail-section {
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}

.detail-section:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 16px;
  background-color: #409eff;
  border-radius: 2px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-item label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
}

.info-item span {
  color: #303133;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.stat-number {
  font-size: 24px;
  font-weight: 600;
  color: #409eff;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.bio-content {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
}

.tags-content {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  margin: 0;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .info-item label {
    min-width: auto;
  }
}

/* 动画效果 */
.detail-section {
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
