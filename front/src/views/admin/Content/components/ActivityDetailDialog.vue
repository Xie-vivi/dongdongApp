<template>
  <el-dialog
    v-model="visible"
    :title="activity ? '活动详情' : '新建活动'"
    width="800px"
    :before-close="handleClose"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      v-loading="loading"
    >
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="活动标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入活动标题" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="活动类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择活动类型" style="width: 100%">
              <el-option label="线上活动" value="online" />
              <el-option label="线下活动" value="offline" />
              <el-option label="混合活动" value="hybrid" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="开始时间" prop="startTime">
            <el-date-picker
              v-model="form.startTime"
              type="datetime"
              placeholder="选择开始时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="结束时间" prop="endTime">
            <el-date-picker
              v-model="form.endTime"
              type="datetime"
              placeholder="选择结束时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="活动地点" prop="location">
        <el-input v-model="form.location" placeholder="请输入活动地点" />
      </el-form-item>

      <el-form-item label="活动描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          placeholder="请输入活动描述"
        />
      </el-form-item>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item label="参与人数" prop="maxParticipants">
            <el-input-number
              v-model="form.maxParticipants"
              :min="1"
              placeholder="最大参与人数"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="活动状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
              <el-option label="草稿" value="draft" />
              <el-option label="发布" value="published" />
              <el-option label="进行中" value="ongoing" />
              <el-option label="已结束" value="ended" />
              <el-option label="已取消" value="cancelled" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="活动标签" prop="tags">
        <el-select
          v-model="form.tags"
          multiple
          filterable
          allow-create
          placeholder="请选择或输入标签"
          style="width: 100%"
        >
          <el-option
            v-for="tag in availableTags"
            :key="tag.id"
            :label="tag.name"
            :value="tag.name"
          />
        </el-select>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="loading">
          {{ activity ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  activity: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = ref(false)
const loading = ref(false)
const formRef = ref()

const form = reactive({
  title: '',
  type: '',
  startTime: null,
  endTime: null,
  location: '',
  description: '',
  maxParticipants: 50,
  status: 'draft',
  tags: []
})

const rules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择活动类型', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  description: [
    { required: true, message: '请输入活动描述', trigger: 'blur' }
  ]
}

const availableTags = ref([
  { id: 1, name: '技术' },
  { id: 2, name: '娱乐' },
  { id: 3, name: '学习' },
  { id: 4, name: '运动' },
  { id: 5, name: '社交' }
])

watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.activity) {
    nextTick(() => {
      Object.assign(form, {
        ...props.activity,
        startTime: props.activity.startTime ? new Date(props.activity.startTime) : null,
        endTime: props.activity.endTime ? new Date(props.activity.endTime) : null,
        tags: props.activity.tags || []
      })
    })
  } else if (val) {
    resetForm()
  }
})

watch(visible, (val) => {
  emit('update:modelValue', val)
})

const resetForm = () => {
  Object.assign(form, {
    title: '',
    type: '',
    startTime: null,
    endTime: null,
    location: '',
    description: '',
    maxParticipants: 50,
    status: 'draft',
    tags: []
  })
  formRef.value?.clearValidate()
}

const handleClose = () => {
  visible.value = false
  resetForm()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    
    if (form.endTime <= form.startTime) {
      ElMessage.error('结束时间必须晚于开始时间')
      return
    }

    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success(props.activity ? '活动更新成功' : '活动创建成功')
    emit('success', { ...form })
    handleClose()
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  text-align: right;
}
</style>
