<template>
  <div>
    <el-card>
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :span="8">
          <el-select v-model="selectedOrderId" placeholder="选择委托单" style="width: 100%" @change="loadCertificates">
            <el-option v-for="o in orderList" :key="o.id" :label="o.orderNo + ' - ' + o.wasteItemName" :value="o.id" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-upload
            :show-file-list="false"
            :before-upload="handleUpload"
            accept="image/*,.pdf"
            :disabled="!selectedOrderId"
          >
            <el-button type="success" :disabled="!selectedOrderId">上传凭证</el-button>
          </el-upload>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8" v-for="item in certList" :key="item.id" style="margin-bottom: 20px">
          <el-card shadow="hover">
            <div style="text-align: center; padding: 10px 0">
              <el-icon :size="40" style="color: #67c23a"><el-icon-document /></el-icon>
              <p style="margin: 8px 0 4px; font-weight: bold">{{ item.fileName }}</p>
              <p style="color: #909399; font-size: 13px; margin: 4px 0">类型：{{ item.fileType }}</p>
              <p style="color: #909399; font-size: 13px; margin: 4px 0">上传时间：{{ item.uploadTime }}</p>
              <p style="color: #606266; font-size: 13px; margin: 4px 0">{{ item.description }}</p>
            </div>
            <div style="text-align: center">
              <el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCertificate, uploadCertificate, deleteCertificate } from '../api/certificate'
import { listAllCleaningOrder } from '../api/cleaningOrder'
import { upload } from '../api/file'

const selectedOrderId = ref('')
const orderList = ref([])
const certList = ref([])

const loadOrders = async () => {
  const res = await listAllCleaningOrder()
  orderList.value = res.data || []
}

const loadCertificates = async () => {
  if (!selectedOrderId.value) {
    certList.value = []
    return
  }
  const res = await listCertificate(selectedOrderId.value)
  certList.value = res.data || []
}

const handleUpload = async (file) => {
  const uploadRes = await upload(file)
  const fileData = uploadRes.data || {}
  const fileUrl = fileData.url || fileData
  await uploadCertificate({
    orderId: selectedOrderId.value,
    fileName: file.name,
    fileType: file.type,
    fileUrl: fileUrl,
    fileSize: file.size,
    certificateType: '清运凭证',
    description: ''
  })
  ElMessage.success('上传成功')
  loadCertificates()
  return false
}

const handleDelete = (item) => {
  ElMessageBox.confirm('确认删除该凭证？', '提示', { type: 'warning' }).then(async () => {
    await deleteCertificate(item.id)
    ElMessage.success('删除成功')
    loadCertificates()
  })
}

onMounted(() => {
  loadOrders()
})
</script>
