<template>
  <div class="storage-config">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">存储配置</h1>
        <p class="page-description">管理文件存储策略、配额和清理规则</p>
      </div>
      <div class="header-actions">
        <el-button
          type="primary"
          @click="saveAllConfig"
          :loading="saving"
        >
          <el-icon><Check /></el-icon>
          保存配置
        </el-button>
        <el-button
          type="info"
          @click="resetConfig"
        >
          <el-icon><RefreshLeft /></el-icon>
          重置配置
        </el-button>
      </div>
    </div>

    <!-- 配置选项卡 -->
    <el-tabs v-model="activeTab" class="config-tabs">
      <!-- 基础存储配置 -->
      <el-tab-pane label="基础配置" name="basic">
        <div class="config-section">
          <div class="section-header">
            <h3>存储类型</h3>
            <p>选择文件存储的主要方式</p>
          </div>

          <el-form :model="basicConfig" label-width="140px" class="config-form">
            <el-form-item label="存储方式">
              <el-radio-group v-model="basicConfig.storageType">
                <el-radio label="local">本地存储</el-radio>
                <el-radio label="cloud">云存储</el-radio>
                <el-radio label="hybrid">混合存储</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="存储路径" v-if="basicConfig.storageType !== 'cloud'">
              <el-input
                v-model="basicConfig.storagePath"
                placeholder="请输入存储路径"
              />
            </el-form-item>

            <el-form-item label="云存储配置" v-if="basicConfig.storageType === 'cloud' || basicConfig.storageType === 'hybrid'">
              <el-select v-model="basicConfig.cloudProvider" style="width: 200px; margin-right: 12px">
                <el-option label="阿里云 OSS" value="aliyun" />
                <el-option label="腾讯云 COS" value="tencent" />
                <el-option label="AWS S3" value="aws" />
                <el-option label="七牛云" value="qiniu" />
              </el-select>
              <el-button type="primary" @click="showCloudConfigDialog">
                配置云存储
              </el-button>
            </el-form-item>

            <el-form-item label="文件命名规则">
              <el-select v-model="basicConfig.namingRule" style="width: 200px">
                <el-option label="原始文件名" value="original" />
                <el-option label="时间戳 + 随机数" value="timestamp" />
                <el-option label="UUID" value="uuid" />
                <el-option label="MD5 哈希" value="md5" />
              </el-select>
            </el-form-item>

            <el-form-item label="目录结构">
              <el-input
                v-model="basicConfig.directoryStructure"
                placeholder="例如: /uploads/{year}/{month}/"
              />
              <div class="form-help">
                可用变量: {year}, {month}, {day}, {type}, {user}
              </div>
            </el-form-item>
          </el-form>
        </div>

        <div class="config-section">
          <div class="section-header">
            <h3>存储配额</h3>
            <p>设置存储空间和文件数量限制</p>
          </div>

          <el-form :model="quotaConfig" label-width="140px" class="config-form">
            <el-form-item label="总存储空间">
              <el-input-number
                v-model="quotaConfig.totalSpace"
                :min="1"
                :max="1000"
                style="width: 120px; margin-right: 8px"
              />
              <el-select v-model="quotaConfig.totalSpaceUnit" style="width: 100px">
                <el-option label="GB" value="GB" />
                <el-option label="TB" value="TB" />
              </el-select>
            </el-form-item>

            <el-form-item label="单文件大小限制">
              <el-input-number
                v-model="quotaConfig.maxFileSize"
                :min="1"
                :max="1024"
                style="width: 120px; margin-right: 8px"
              />
              <el-select v-model="quotaConfig.maxFileSizeUnit" style="width: 100px">
                <el-option label="MB" value="MB" />
                <el-option label="GB" value="GB" />
              </el-select>
            </el-form-item>

            <el-form-item label="文件数量限制">
              <el-input-number
                v-model="quotaConfig.maxFileCount"
                :min="1000"
                :max="1000000"
                :step="1000"
                style="width: 200px"
              />
              <span style="margin-left: 8px; color: #909399">个文件</span>
            </el-form-item>

            <el-form-item label="用户配额">
              <el-input-number
                v-model="quotaConfig.userQuota"
                :min="100"
                :max="10240"
                style="width: 120px; margin-right: 8px"
              />
              <el-select v-model="quotaConfig.userQuotaUnit" style="width: 100px">
                <el-option label="MB" value="MB" />
                <el-option label="GB" value="GB" />
              </el-select>
              <span style="margin-left: 8px; color: #909399">每用户</span>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 文件类型配置 -->
      <el-tab-pane label="文件类型" name="filetypes">
        <div class="config-section">
          <div class="section-header">
            <h3>允许的文件类型</h3>
            <p>配置允许上传的文件类型和大小限制</p>
          </div>

          <div class="filetype-config">
            <div class="filetype-list">
              <div
                v-for="(type, index) in fileTypeConfig"
                :key="index"
                class="filetype-item"
              >
                <div class="filetype-info">
                  <el-icon class="filetype-icon">
                    <component :is="getFileTypeIcon(type.type)" />
                  </el-icon>
                  <div class="filetype-details">
                    <div class="filetype-name">{{ type.name }}</div>
                    <div class="filetype-extensions">{{ type.extensions.join(', ') }}</div>
                  </div>
                </div>
                
                <div class="filetype-controls">
                  <el-switch
                    v-model="type.enabled"
                    active-text="允许"
                    inactive-text="禁止"
                  />
                  <el-input-number
                    v-model="type.maxSize"
                    :min="1"
                    :max="1024"
                    size="small"
                    style="width: 80px; margin: 0 8px"
                  />
                  <span style="font-size: 12px; color: #909399">MB</span>
                  <el-button
                    link
                    type="danger"
                    size="small"
                    @click="removeFileType(index)"
                  >
                    删除
                  </el-button>
                </div>
              </div>
            </div>

            <div class="filetype-actions">
              <el-button type="primary" @click="showAddFileTypeDialog">
                <el-icon><Plus /></el-icon>
                添加文件类型
              </el-button>
              <el-button @click="resetFileTypes">
                <el-icon><RefreshLeft /></el-icon>
                恢复默认
              </el-button>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <!-- 缩略图配置 -->
      <el-tab-pane label="缩略图" name="thumbnails">
        <div class="config-section">
          <div class="section-header">
            <h3>缩略图生成</h3>
            <p>配置图片缩略图的生成规则和质量</p>
          </div>

          <el-form :model="thumbnailConfig" label-width="140px" class="config-form">
            <el-form-item label="启用缩略图">
              <el-switch v-model="thumbnailConfig.enabled" />
            </el-form-item>

            <template v-if="thumbnailConfig.enabled">
              <el-form-item label="缩略图尺寸">
                <div class="thumbnail-sizes">
                  <div
                    v-for="(size, index) in thumbnailConfig.sizes"
                    :key="index"
                    class="size-item"
                  >
                    <el-input
                      v-model="size.name"
                      placeholder="尺寸名称"
                      style="width: 100px; margin-right: 8px"
                    />
                    <el-input-number
                      v-model="size.width"
                      :min="50"
                      :max="2000"
                      placeholder="宽度"
                      style="width: 100px; margin-right: 8px"
                    />
                    <span style="margin-right: 8px">×</span>
                    <el-input-number
                      v-model="size.height"
                      :min="50"
                      :max="2000"
                      placeholder="高度"
                      style="width: 100px; margin-right: 8px"
                    />
                    <el-button
                      link
                      type="danger"
                      size="small"
                      @click="removeThumbnailSize(index)"
                    >
                      删除
                    </el-button>
                  </div>
                </div>
                <el-button
                  type="primary"
                  size="small"
                  @click="addThumbnailSize"
                  style="margin-top: 8px"
                >
                  <el-icon><Plus /></el-icon>
                  添加尺寸
                </el-button>
              </el-form-item>

              <el-form-item label="图片质量">
                <el-slider
                  v-model="thumbnailConfig.quality"
                  :min="10"
                  :max="100"
                  :step="10"
                  show-stops
                  style="width: 300px"
                />
                <span style="margin-left: 12px; color: #909399">{{ thumbnailConfig.quality }}%</span>
              </el-form-item>

              <el-form-item label="图片格式">
                <el-radio-group v-model="thumbnailConfig.format">
                  <el-radio label="jpeg">JPEG</el-radio>
                  <el-radio label="png">PNG</el-radio>
                  <el-radio label="webp">WebP</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="裁剪模式">
                <el-select v-model="thumbnailConfig.cropMode" style="width: 200px">
                  <el-option label="保持比例缩放" value="contain" />
                  <el-option label="填充裁剪" value="cover" />
                  <el-option label="拉伸填充" value="fill" />
                  <el-option label="不裁剪" value="none" />
                </el-select>
              </el-form-item>
            </template>
          </el-form>
        </div>
      </el-tab-pane>

      <!-- 清理策略 -->
      <el-tab-pane label="清理策略" name="cleanup">
        <div class="config-section">
          <div class="section-header">
            <h3>自动清理</h3>
            <p>配置文件自动清理规则和计划任务</p>
          </div>

          <el-form :model="cleanupConfig" label-width="140px" class="config-form">
            <el-form-item label="启用自动清理">
              <el-switch v-model="cleanupConfig.enabled" />
            </el-form-item>

            <template v-if="cleanupConfig.enabled">
              <el-form-item label="清理频率">
                <el-select v-model="cleanupConfig.frequency" style="width: 200px">
                  <el-option label="每天" value="daily" />
                  <el-option label="每周" value="weekly" />
                  <el-option label="每月" value="monthly" />
                </el-select>
                <el-time-picker
                  v-model="cleanupConfig.cleanupTime"
                  placeholder="执行时间"
                  style="margin-left: 12px"
                />
              </el-form-item>

              <el-form-item label="文件保留期">
                <el-input-number
                  v-model="cleanupConfig.retentionDays"
                  :min="7"
                  :max="365"
                  style="width: 120px; margin-right: 8px"
                />
                <span style="color: #909399">天</span>
              </el-form-item>

              <el-form-item label="清理规则">
                <div class="cleanup-rules">
                  <el-checkbox v-model="cleanupConfig.deleteExpired">
                    删除过期文件
                  </el-checkbox>
                  <el-checkbox v-model="cleanupConfig.deleteDuplicates">
                    删除重复文件
                  </el-checkbox>
                  <el-checkbox v-model="cleanupConfig.deleteOrphaned">
                    删除孤立文件
                  </el-checkbox>
                  <el-checkbox v-model="cleanupConfig.deleteTemp">
                    删除临时文件
                  </el-checkbox>
                </div>
              </el-form-item>

              <el-form-item label="清理阈值">
                <div class="cleanup-threshold">
                  <div class="threshold-item">
                    <span>存储空间使用超过</span>
                    <el-input-number
                      v-model="cleanupConfig.spaceThreshold"
                      :min="70"
                      :max="95"
                      style="width: 80px; margin: 0 8px"
                    />
                    <span>% 时触发清理</span>
                  </div>
                  <div class="threshold-item">
                    <span>清理到</span>
                    <el-input-number
                      v-model="cleanupConfig.targetUsage"
                      :min="50"
                      :max="80"
                      style="width: 80px; margin: 0 8px"
                    />
                    <span>% 使用率</span>
                  </div>
                </div>
              </el-form-item>
            </template>
          </el-form>
        </div>

        <div class="config-section">
          <div class="section-header">
            <h3>清理历史</h3>
            <p>查看文件清理的执行记录</p>
          </div>

          <div class="cleanup-history">
            <el-table :data="cleanupHistory" stripe>
              <el-table-column prop="time" label="执行时间" width="180" />
              <el-table-column prop="type" label="清理类型" width="120" />
              <el-table-column prop="deletedCount" label="删除文件数" width="120" />
              <el-table-column prop="freedSpace" label="释放空间" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="row.status === 'success' ? 'success' : 'danger'" size="small">
                    {{ row.status === 'success' ? '成功' : '失败' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="message" label="说明" />
            </el-table>
          </div>
        </div>
      </el-tab-pane>

      <!-- 备份策略 -->
      <el-tab-pane label="备份策略" name="backup">
        <div class="config-section">
          <div class="section-header">
            <h3>文件备份</h3>
            <p>配置重要文件的自动备份策略</p>
          </div>

          <el-form :model="backupConfig" label-width="140px" class="config-form">
            <el-form-item label="启用备份">
              <el-switch v-model="backupConfig.enabled" />
            </el-form-item>

            <template v-if="backupConfig.enabled">
              <el-form-item label="备份频率">
                <el-select v-model="backupConfig.frequency" style="width: 200px">
                  <el-option label="每天" value="daily" />
                  <el-option label="每周" value="weekly" />
                  <el-option label="每月" value="monthly" />
                </el-select>
              </el-form-item>

              <el-form-item label="备份范围">
                <div class="backup-scope">
                  <el-checkbox v-model="backupConfig.backupImages">
                    图片文件
                  </el-checkbox>
                  <el-checkbox v-model="backupConfig.backupDocuments">
                    文档文件
                  </el-checkbox>
                  <el-checkbox v-model="backupConfig.backupVideos">
                    视频文件
                  </el-checkbox>
                  <el-checkbox v-model="backupConfig.backupImportant">
                    标记为重要的文件
                  </el-checkbox>
                </div>
              </el-form-item>

              <el-form-item label="备份保留期">
                <el-input-number
                  v-model="backupConfig.retentionDays"
                  :min="7"
                  :max="365"
                  style="width: 120px; margin-right: 8px"
                />
                <span style="color: #909399">天</span>
              </el-form-item>

              <el-form-item label="备份位置">
                <el-radio-group v-model="backupConfig.location">
                  <el-radio label="local">本地备份</el-radio>
                  <el-radio label="cloud">云端备份</el-radio>
                  <el-radio label="both">本地+云端</el-radio>
                </el-radio-group>
              </el-form-item>
            </template>
          </el-form>
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 云存储配置对话框 -->
    <el-dialog
      v-model="cloudConfigDialogVisible"
      title="云存储配置"
      width="600px"
    >
      <el-form :model="cloudConfig" label-width="120px">
        <el-form-item label="Access Key">
          <el-input v-model="cloudConfig.accessKey" show-password />
        </el-form-item>
        <el-form-item label="Secret Key">
          <el-input v-model="cloudConfig.secretKey" show-password />
        </el-form-item>
        <el-form-item label="Bucket 名称">
          <el-input v-model="cloudConfig.bucket" />
        </el-form-item>
        <el-form-item label="区域">
          <el-select v-model="cloudConfig.region" style="width: 100%">
            <el-option label="华东1" value="cn-hangzhou" />
            <el-option label="华北2" value="cn-beijing" />
            <el-option label="华南1" value="cn-shenzhen" />
          </el-select>
        </el-form-item>
        <el-form-item label="自定义域名">
          <el-input v-model="cloudConfig.customDomain" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cloudConfigDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCloudConfig">保存</el-button>
      </template>
    </el-dialog>

    <!-- 添加文件类型对话框 -->
    <el-dialog
      v-model="addFileTypeDialogVisible"
      title="添加文件类型"
      width="500px"
    >
      <el-form :model="newFileType" label-width="100px">
        <el-form-item label="类型名称">
          <el-input v-model="newFileType.name" />
        </el-form-item>
        <el-form-item label="文件扩展名">
          <el-input
            v-model="newFileType.extensions"
            placeholder="用逗号分隔，如: .jpg,.png,.gif"
          />
        </el-form-item>
        <el-form-item label="MIME 类型">
          <el-input v-model="newFileType.mimeType" />
        </el-form-item>
        <el-form-item label="最大大小">
          <el-input-number
            v-model="newFileType.maxSize"
            :min="1"
            :max="1024"
            style="width: 120px"
          />
          <span style="margin-left: 8px">MB</span>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addFileTypeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="addFileType">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Check,
  RefreshLeft,
  Plus,
  Picture,
  Document,
  VideoPlay,
  Headphones,
  FolderOpened,
  Files
} from '@element-plus/icons-vue'

// 响应式数据
const activeTab = ref('basic')
const saving = ref(false)
const cloudConfigDialogVisible = ref(false)
const addFileTypeDialogVisible = ref(false)

// 基础配置
const basicConfig = reactive({
  storageType: 'local',
  storagePath: '/uploads',
  cloudProvider: 'aliyun',
  namingRule: 'timestamp',
  directoryStructure: '/uploads/{year}/{month}/'
})

// 配额配置
const quotaConfig = reactive({
  totalSpace: 100,
  totalSpaceUnit: 'GB',
  maxFileSize: 100,
  maxFileSizeUnit: 'MB',
  maxFileCount: 100000,
  userQuota: 1024,
  userQuotaUnit: 'MB'
})

// 文件类型配置
const fileTypeConfig = ref([
  {
    name: '图片文件',
    type: 'image',
    extensions: ['.jpg', '.jpeg', '.png', '.gif', '.webp'],
    mimeType: 'image/*',
    maxSize: 10,
    enabled: true
  },
  {
    name: '文档文件',
    type: 'document',
    extensions: ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx'],
    mimeType: 'application/*',
    maxSize: 50,
    enabled: true
  },
  {
    name: '视频文件',
    type: 'video',
    extensions: ['.mp4', '.avi', '.mov', '.wmv', '.flv'],
    mimeType: 'video/*',
    maxSize: 500,
    enabled: true
  },
  {
    name: '音频文件',
    type: 'audio',
    extensions: ['.mp3', '.wav', '.flac', '.aac'],
    mimeType: 'audio/*',
    maxSize: 100,
    enabled: true
  },
  {
    name: '压缩文件',
    type: 'archive',
    extensions: ['.zip', '.rar', '.7z', '.tar', '.gz'],
    mimeType: 'application/*',
    maxSize: 200,
    enabled: true
  }
])

// 缩略图配置
const thumbnailConfig = reactive({
  enabled: true,
  sizes: [
    { name: 'small', width: 150, height: 150 },
    { name: 'medium', width: 300, height: 300 },
    { name: 'large', width: 800, height: 600 }
  ],
  quality: 80,
  format: 'jpeg',
  cropMode: 'cover'
})

// 清理配置
const cleanupConfig = reactive({
  enabled: true,
  frequency: 'weekly',
  cleanupTime: null,
  retentionDays: 30,
  deleteExpired: true,
  deleteDuplicates: true,
  deleteOrphaned: true,
  deleteTemp: true,
  spaceThreshold: 85,
  targetUsage: 70
})

// 备份配置
const backupConfig = reactive({
  enabled: false,
  frequency: 'weekly',
  backupImages: true,
  backupDocuments: true,
  backupVideos: false,
  backupImportant: true,
  retentionDays: 90,
  location: 'local'
})

// 云存储配置
const cloudConfig = reactive({
  accessKey: '',
  secretKey: '',
  bucket: '',
  region: 'cn-hangzhou',
  customDomain: ''
})

// 新文件类型
const newFileType = reactive({
  name: '',
  extensions: '',
  mimeType: '',
  maxSize: 10
})

// 清理历史
const cleanupHistory = ref([
  {
    time: '2024-01-15 02:00:00',
    type: '自动清理',
    deletedCount: 45,
    freedSpace: '2.3GB',
    status: 'success',
    message: '清理完成，删除过期和重复文件'
  },
  {
    time: '2024-01-08 02:00:00',
    type: '手动清理',
    deletedCount: 23,
    freedSpace: '1.1GB',
    status: 'success',
    message: '管理员手动触发清理'
  }
])

// 获取文件类型图标
const getFileTypeIcon = (type) => {
  const iconMap = {
    image: Picture,
    document: Document,
    video: VideoPlay,
    audio: Headphones,
    archive: FolderOpened
  }
  return iconMap[type] || Files
}

// 显示云存储配置对话框
const showCloudConfigDialog = () => {
  cloudConfigDialogVisible.value = true
}

// 保存云存储配置
const saveCloudConfig = () => {
  // 验证必填字段
  if (!cloudConfig.accessKey || !cloudConfig.secretKey || !cloudConfig.bucket) {
    ElMessage.warning('请填写完整的云存储配置信息')
    return
  }
  
  cloudConfigDialogVisible.value = false
  ElMessage.success('云存储配置已保存')
}

// 显示添加文件类型对话框
const showAddFileTypeDialog = () => {
  Object.assign(newFileType, {
    name: '',
    extensions: '',
    mimeType: '',
    maxSize: 10
  })
  addFileTypeDialogVisible.value = true
}

// 添加文件类型
const addFileType = () => {
  if (!newFileType.name || !newFileType.extensions) {
    ElMessage.warning('请填写文件类型名称和扩展名')
    return
  }
  
  fileTypeConfig.value.push({
    name: newFileType.name,
    type: newFileType.name.toLowerCase(),
    extensions: newFileType.extensions.split(',').map(ext => ext.trim()),
    mimeType: newFileType.mimeType,
    maxSize: newFileType.maxSize,
    enabled: true
  })
  
  addFileTypeDialogVisible.value = false
  ElMessage.success('文件类型已添加')
}

// 删除文件类型
const removeFileType = (index) => {
  fileTypeConfig.value.splice(index, 1)
}

// 重置文件类型
const resetFileTypes = () => {
  fileTypeConfig.value = [
    {
      name: '图片文件',
      type: 'image',
      extensions: ['.jpg', '.jpeg', '.png', '.gif', '.webp'],
      mimeType: 'image/*',
      maxSize: 10,
      enabled: true
    },
    {
      name: '文档文件',
      type: 'document',
      extensions: ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx'],
      mimeType: 'application/*',
      maxSize: 50,
      enabled: true
    }
  ]
  ElMessage.success('文件类型配置已重置')
}

// 添加缩略图尺寸
const addThumbnailSize = () => {
  thumbnailConfig.sizes.push({
    name: `size_${thumbnailConfig.sizes.length + 1}`,
    width: 200,
    height: 200
  })
}

// 删除缩略图尺寸
const removeThumbnailSize = (index) => {
  thumbnailConfig.sizes.splice(index, 1)
}

// 保存所有配置
const saveAllConfig = async () => {
  saving.value = true
  
  try {
    // 模拟保存配置
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // 这里应该调用API保存配置
    console.log('保存配置:', {
      basic: basicConfig,
      quota: quotaConfig,
      fileTypes: fileTypeConfig.value,
      thumbnail: thumbnailConfig,
      cleanup: cleanupConfig,
      backup: backupConfig
    })
    
    ElMessage.success('配置保存成功')
    
  } catch (error) {
    console.error('保存配置失败:', error)
    ElMessage.error('保存配置失败')
  } finally {
    saving.value = false
  }
}

// 重置配置
const resetConfig = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要重置所有配置吗？这将恢复到默认设置。',
      '重置确认',
      {
        confirmButtonText: '确定重置',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 重置到默认配置
    Object.assign(basicConfig, {
      storageType: 'local',
      storagePath: '/uploads',
      cloudProvider: 'aliyun',
      namingRule: 'timestamp',
      directoryStructure: '/uploads/{year}/{month}/'
    })
    
    Object.assign(quotaConfig, {
      totalSpace: 100,
      totalSpaceUnit: 'GB',
      maxFileSize: 100,
      maxFileSizeUnit: 'MB',
      maxFileCount: 100000,
      userQuota: 1024,
      userQuotaUnit: 'MB'
    })
    
    ElMessage.success('配置已重置')
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置配置失败:', error)
    }
  }
}

// 组件挂载
onMounted(() => {
  // 加载现有配置
  console.log('加载存储配置...')
})
</script>

<style scoped>
.storage-config {
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

.config-tabs {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.config-section {
  padding: 24px;
  border-bottom: 1px solid #ebeef5;
}

.config-section:last-child {
  border-bottom: none;
}

.section-header {
  margin-bottom: 24px;
}

.section-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.section-header p {
  margin: 0;
  font-size: 14px;
  color: #606266;
}

.config-form {
  max-width: 600px;
}

.form-help {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.filetype-config {
  max-width: 800px;
}

.filetype-list {
  margin-bottom: 20px;
}

.filetype-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 12px;
}

.filetype-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filetype-icon {
  font-size: 24px;
  color: #409eff;
}

.filetype-name {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.filetype-extensions {
  font-size: 12px;
  color: #909399;
}

.filetype-controls {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filetype-actions {
  display: flex;
  gap: 12px;
}

.thumbnail-sizes {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.size-item {
  display: flex;
  align-items: center;
}

.cleanup-rules {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cleanup-threshold {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.threshold-item {
  display: flex;
  align-items: center;
}

.cleanup-history {
  max-width: 800px;
}

.backup-scope {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .storage-config {
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
  
  .filetype-item {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .filetype-controls {
    justify-content: space-between;
  }
  
  .size-item {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .threshold-item {
    flex-wrap: wrap;
    gap: 8px;
  }
}

/* 动画效果 */
.config-section {
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
