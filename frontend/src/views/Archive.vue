<template>
  <div>
    <el-card>
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :span="8">
          <el-select v-model="selectedOrderId" placeholder="选择委托单" style="width: 100%" @change="loadArchives">
            <el-option v-for="o in orderList" :key="o.id" :label="o.orderNo + ' - ' + o.wasteItemName" :value="o.id" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="openDialog" :disabled="!selectedOrderId">新增留档</el-button>
        </el-col>
      </el-row>

      <el-timeline v-if="archiveList.length">
        <el-timeline-item
          v-for="item in archiveList"
          :key="item.id"
          :timestamp="item.createTime"
          placement="top"
        >
          <el-card shadow="hover">
            <h4 style="margin: 0 0 8px">{{ item.archiveType }}</h4>
            <p style="color: #606266; margin: 0">{{ item.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-else description="暂无留档记录" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增留档" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-select v-model="form.archiveType" style="width: 100%">
            <el-option label="委托创建" value="委托创建" />
            <el-option label="清运确认" value="清运确认" />
            <el-option label="清运完成" value="清运完成" />
            <el-option label="费用记录" value="费用记录" />
            <el-option label="凭证上传" value="凭证上传" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listArchive, addArchive } from '../api/archive'
import { listAllCleaningOrder } from '../api/cleaningOrder'

const selectedOrderId = ref('')
const orderList = ref([])
const archiveList = ref([])
const dialogVisible = ref(false)
const form = ref({})

const loadOrders = async () => {
  const res = await listAllCleaningOrder()
  orderList.value = res.data || []
}

const loadArchives = async () => {
  if (!selectedOrderId.value) {
    archiveList.value = []
    return
  }
  const res = await listArchive(selectedOrderId.value)
  archiveList.value = res.data || []
}

const openDialog = () => {
  form.value = { archiveType: '', content: '' }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await addArchive({
    orderId: selectedOrderId.value,
    archiveType: form.value.archiveType,
    content: form.value.content
  })
  ElMessage.success('新增成功')
  dialogVisible.value = false
  loadArchives()
}

onMounted(() => {
  loadOrders()
})
</script>
