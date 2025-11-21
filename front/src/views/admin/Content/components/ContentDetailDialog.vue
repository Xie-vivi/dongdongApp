<template>
  <el-dialog
    v-model="visible"
    :title="dialogTitle"
    width="900px"
    append-to-body
    :before-close="handleClose"
  >
    <div v-if="content" class="content-detail">
      <!-- 基本信息 -->
      <div class="detail-section">
        <h3 class="section-title">基本信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <label>内容ID：</label>
            <span>{{ content.id }}</span>
          </div>
          <div class="info-item">
            <label>内容类型：</label>
            <span>{{ contentTypeText }}</span>
          </div>
          <div class="info-item">
            <label>状态：</label>
            <StatusTag :status="content.status" type="content" />
          </div>
          <div class="info-item">
            <label>创建时间：</label>
            <span>{{ formatDateTime(content.createdAt) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 作者信息 -->
      <div class="detail-section">
        <h3 class="section-title">作者信息</h3>
        <div class="author-section">
          <div class="author-avatar">
            <el-avatar :size="60" :src="content.user?.avatar">
              <el-icon><User /></el-icon>
            </el-avatar>
          </div>
          <div class="author-details">
            <div class="author-name">{{ content.user?.username || '-' }}</div>
            <div class="author-nickname">{{ content.user?.nickname || '-' }}</div>
            <div class="author-email">{{ content.user?.email || '-' }}</div>
          </div>
        </div>
      </div>
      
      <!-- 帖子内容 -->
      <div v-if="contentType === 'posts'" class="detail-section">
        <h3 class="section-title">帖子内容</h3>
        <div class="content-section">
          <div class="content-title">{{ content.title }}</div>
          <div class="content-body" v-html="content.content"></div>
          
          <!-- 帖子图片 -->
          <div v-if="content.images && content.images.length > 0" class="content-images">
            <h4>图片附件</h4>
            <div class="image-grid">
              <div
                v-for="(image, index) in content.images"
                :key="index"
                class="image-item"
              >
                <el-image
                  :src="image.url"
                  :preview-src-list="content.images.map(img => img.url)"
                  fit="cover"
                  class="content-image"
                />
              </div>
            </div>
          </div>
          
          <!-- 统计信息 -->
          <div class="content-stats">
            <div class="stat-item">
              <el-icon><View /></el-icon>
              <span>{{ content.viewsCount || 0 }} 浏览</span>
            </div>
            <div class="stat-item">
              <el-icon><Star /></el-icon>
              <span>{{ content.likesCount || 0 }} 点赞</span>
            </div>
            <div class="stat-item">
              <el-icon><ChatLineSquare /></el-icon>
              <span>{{ content.commentsCount || 0 }} 评论</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 活动内容 -->
      <div v-else-if="contentType === 'activities'" class="detail-section">
        <h3 class="section-title">活动内容</h3>
        <div class="content-section">
          <div class="content-title">{{ content.title }}</div>
          <div class="content-body" v-html="content.description"></div>
          
          <!-- 活动信息 -->
          <div class="activity-info">
            <div class="info-row">
              <label>活动时间：</label>
              <span>{{ formatDateTime(content.activityDate) }}</span>
            </div>
            <div class="info-row">
              <label>活动地点：</label>
              <span>{{ content.location || '-' }}</span>
            </div>
            <div class="info-row">
              <label>参与人数：</label>
              <span>{{ content.participantsCount || 0 }} 人</span>
            </div>
            <div class="info-row">
              <label>最大人数：</label>
              <span>{{ content.maxParticipants || '不限' }}</span>
            </div>
          </div>
          
          <!-- 活动图片 -->
          <div v-if="content.images && content.images.length > 0" class="content-images">
            <h4>活动图片</h4>
            <div class="image-grid">
              <div
                v-for="(image, index) in content.images"
                :key="index"
                class="image-item"
              >
                <el-image
                  :src="image.url"
                  :preview-src-list="content.images.map(img => img.url)"
                  fit="cover"
                  class="content-image"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 评论内容 -->
      <div v-else-if="contentType === 'comments'" class="detail-section">
        <h3 class="section-title">评论内容</h3>
        <div class="content-section">
          <div class="comment-content" v-html="content.content"></div>
          
          <!-- 评论的原文 -->
          <div v-if="content.parentContent" class="parent-content">
            <h4>评论的原文</h4>
            <div class="parent-item">
              <div class="parent-title">{{ content.parentContent.title || '无标题' }}</div>
              <div class="parent-excerpt">{{ content.parentContent.excerpt || '无内容' }}</div>
            </div>
          </div>
          
          <!-- 统计信息 -->
          <div class="content-stats">
            <div class="stat-item">
              <el-icon><Star /></el-icon>
              <span>{{ content.likesCount || 0 }} 点赞</span>
            </div>
            <div class="stat-item">
              <el-icon><ChatLineSquare /></el-icon>
              <span>{{ content.repliesCount || 0 }} 回复</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 审核历史 -->
      <div v-if="content.auditHistory && content.auditHistory.length > 0" class="detail-section">
        <h3 class="section-title">审核历史</h3>
        <div class="audit-history">
          <div
            v-for="(history, index) in content.auditHistory"
            :key="index"
            class="history-item"
          >
            <div class="history-action">
              <StatusTag :status="history.action" type="content" />
              <span class="history-time">{{ formatDateTime(history.createdAt) }}</span>
            </div>
            <div class="history-admin">
              操作人：{{ history.admin?.username || '-' }}
            </div>
            <div v-if="history.reason" class="history-reason">
              理由：{{ history.reason }}
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">关闭</el-button>
        <el-button
          v-if="content?.status === 'pending'"
          type="success"
          @click="handleApprove"
        >
          <el-icon><Check /></el-icon>
          通过审核
        </el-button>
        <el-button
          v-if="content?.status === 'pending'"
          type="danger"
          @click="handleReject"
        >
          <el-icon><Close /></el-icon>
          拒绝审核
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'
import { User, View, Star, ChatLineSquare, Check, Close } from '@element-plus/icons-vue'
import StatusTag from '@/components/admin/StatusTag.vue'

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  content: {
    type: Object,
    default: null
  },
  contentType: {
    type: String,
    default: 'posts'
  }
})

// Emits 定义
const emit = defineEmits(['update:modelValue', 'approve', 'reject'])

// 对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 对话框标题
const dialogTitle = computed(() => {
  const typeMap = {
    posts: '帖子详情',
    activities: '活动详情',
    comments: '评论详情'
  }
  return typeMap[props.contentType] || '内容详情'
})

// 内容类型文本
const contentTypeText = computed(() => {
  const typeMap = {
    posts: '帖子',
    activities: '活动',
    comments: '评论'
  }
  return typeMap[props.contentType] || '未知'
})

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString()
}

// 处理关闭
const handleClose = () => {
  visible.value = false
}

// 处理通过审核
const handleApprove = () => {
  emit('approve', props.content)
}

// 处理拒绝审核
const handleReject = () => {
  emit('reject', props.content)
}
</script>

<style scoped>
.content-detail {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 12px;
}

.content-detail::-webkit-scrollbar {
  width: 6px;
}

.content-detail::-webkit-scrollbar-thumb {
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

.author-section {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.author-details {
  flex: 1;
}

.author-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.author-nickname {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.author-email {
  font-size: 14px;
  color: #909399;
}

.content-section {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.content-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
}

.content-body {
  line-height: 1.6;
  color: #303133;
  margin-bottom: 16px;
}

.content-images h4 {
  margin: 16px 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
}

.image-item {
  border-radius: 8px;
  overflow: hidden;
}

.content-image {
  width: 100%;
  height: 120px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.content-image:hover {
  transform: scale(1.05);
}

.content-stats {
  display: flex;
  gap: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #909399;
}

.activity-info {
  margin-top: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row label {
  font-weight: 500;
  color: #606266;
  min-width: 80px;
  flex-shrink: 0;
}

.info-row span {
  color: #303133;
}

.comment-content {
  line-height: 1.6;
  color: #303133;
  margin-bottom: 16px;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.parent-content h4 {
  margin: 16px 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #606266;
}

.parent-item {
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.parent-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.parent-excerpt {
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
}

.audit-history {
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.history-item {
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  border: 1px solid #ebeef5;
}

.history-item:last-child {
  margin-bottom: 0;
}

.history-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.history-time {
  font-size: 12px;
  color: #909399;
}

.history-admin {
  font-size: 14px;
  color: #606266;
  margin-bottom: 4px;
}

.history-reason {
  font-size: 14px;
  color: #303133;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #409eff;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .author-section {
    flex-direction: column;
    text-align: center;
  }
  
  .content-stats {
    flex-direction: column;
    gap: 8px;
  }
  
  .image-grid {
    grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
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
