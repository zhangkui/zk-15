<template>
  <div class="page-container">
    <el-card shadow="hover">
      <div class="page-header">
        <div class="header-title">处理凭证管理</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          上传凭证
        </el-button>
      </div>

      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="凭证编号/标题/处理单位"
          clearable
          style="width: 240px"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 280px"
        />
        <el-button type="primary" @click="loadData">
          <el-icon><Search /></el-icon>
          查询
        </el-button>
        <el-button @click="resetSearch">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="voucherNo" label="凭证编号" width="180" />
        <el-table-column prop="voucherTitle" label="凭证标题" width="200" show-overflow-tooltip />
        <el-table-column prop="voucherType" label="凭证类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag>{{ row.voucherType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="wasteName" label="废弃物" width="160" show-overflow-tooltip />
        <el-table-column prop="orderNo" label="委托单号" width="180" />
        <el-table-column prop="disposalUnit" label="处理单位" width="160" show-overflow-tooltip />
        <el-table-column prop="disposalPerson" label="处理人" width="100" />
        <el-table-column prop="disposalTime" label="处理时间" width="180" />
        <el-table-column label="文件" width="100" align="center">
          <template #default="{ row }">
            <el-link v-if="row.filePath" type="primary" :href="row.filePath" target="_blank">
              <el-icon><View /></el-icon>
              查看
            </el-link>
            <span v-else style="color: #909399">无</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.pageSize"
        v-model:current-page="pagination.pageNum"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="680px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="凭证类型" prop="voucherType">
              <el-select v-model="form.voucherType" placeholder="请选择类型" style="width: 100%">
                <el-option label="清运单" value="清运单" />
                <el-option label="处置单" value="处置单" />
                <el-option label="发票" value="发票" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="凭证标题" prop="voucherTitle">
              <el-input v-model="form.voucherTitle" placeholder="请输入凭证标题" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="废弃物" prop="wasteId">
              <el-select v-model="form.wasteId" placeholder="请选择废弃物" filterable style="width: 100%">
                <el-option
                  v-for="item in wasteList"
                  :key="item.id"
                  :label="`${item.wasteNo} - ${item.wasteName}`"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="委托单">
              <el-select v-model="form.orderId" placeholder="请选择委托单" filterable clearable style="width: 100%">
                <el-option
                  v-for="item in orderList"
                  :key="item.id"
                  :label="item.orderNo"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关联费用单">
          <el-select v-model="form.feeId" placeholder="请选择费用单" filterable clearable style="width: 50%">
            <el-option
              v-for="item in feeList"
              :key="item.id"
              :label="item.feeNo"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="凭证内容">
          <el-input v-model="form.voucherContent" type="textarea" :rows="3" placeholder="请输入凭证内容说明" />
        </el-form-item>
        <el-form-item label="上传文件">
          <el-upload
            action="#"
            :auto-upload="false"
            :on-change="handleFileUpload"
            :show-file-list="true"
            :limit="1"
            accept=".jpg,.jpeg,.png,.gif,.bmp,.webp,.pdf,.doc,.docx,.xls,.xlsx,.txt"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">支持图片、PDF、Word、Excel、TXT格式，最大10MB</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-divider content-position="left">处理信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="处理单位">
              <el-input v-model="form.disposalUnit" placeholder="请输入处理单位" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="处理人">
              <el-input v-model="form.disposalPerson" placeholder="请输入处理人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="处理时间">
          <el-date-picker v-model="form.disposalTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 50%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="凭证详情" width="680px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="凭证编号">{{ detailData.voucherNo }}</el-descriptions-item>
        <el-descriptions-item label="凭证类型">
          <el-tag>{{ detailData.voucherType }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="凭证标题" :span="2">{{ detailData.voucherTitle }}</el-descriptions-item>
        <el-descriptions-item label="废弃物">{{ detailData.wasteName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="委托单号">{{ detailData.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="凭证内容" :span="2">{{ detailData.voucherContent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理单位">{{ detailData.disposalUnit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.disposalPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailData.disposalTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ detailData.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="附件" :span="2" v-if="detailData.filePath">
          <el-image
            v-if="isImage(detailData.fileName)"
            :src="detailData.filePath"
            :preview-src-list="[detailData.filePath]"
            fit="cover"
            style="width: 200px; height: 150px; border-radius: 4px"
          />
          <el-link v-else type="primary" :href="detailData.filePath" target="_blank">
            <el-icon><Document /></el-icon>
            {{ detailData.fileName }}
          </el-link>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { disposalVoucherApi, wasteInfoApi, pickupOrderApi, feeRecordApi, fileApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('上传凭证')
const formRef = ref(null)
const isEdit = ref(false)
const wasteList = ref([])
const orderList = ref([])
const feeList = ref([])
const dateRange = ref([])
const detailData = ref(null)

const searchForm = reactive({
  keyword: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  wasteId: null,
  orderId: null,
  feeId: null,
  voucherType: '',
  voucherTitle: '',
  voucherContent: '',
  filePath: '',
  fileName: '',
  fileSize: 0,
  disposalUnit: '',
  disposalPerson: '',
  disposalTime: '',
  remark: ''
})

const rules = {
  voucherType: [{ required: true, message: '请选择凭证类型', trigger: 'change' }],
  voucherTitle: [{ required: true, message: '请输入凭证标题', trigger: 'blur' }],
  wasteId: [{ required: true, message: '请选择废弃物', trigger: 'change' }]
}

const isImage = (fileName) => {
  if (!fileName) return false
  return /\.(jpg|jpeg|png|gif|bmp|webp)$/i.test(fileName)
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await disposalVoucherApi.list(params)
    if (res.data) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

const loadWasteList = async () => {
  const res = await wasteInfoApi.list({ pageNum: 1, pageSize: 100 })
  if (res.data) wasteList.value = res.data.list || []
}

const loadOrderList = async () => {
  const res = await pickupOrderApi.list({ pageNum: 1, pageSize: 100 })
  if (res.data) orderList.value = res.data.list || []
}

const loadFeeList = async () => {
  const res = await feeRecordApi.list({ pageNum: 1, pageSize: 100 })
  if (res.data) feeList.value = res.data.list || []
}

const handleFileUpload = async (uploadFile) => {
  const res = await fileApi.uploadVoucher(uploadFile.raw)
  if (res.data) {
    form.filePath = res.data.fileUrl
    form.fileName = res.data.fileName
    form.fileSize = res.data.fileSize
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  dateRange.value = []
  pagination.pageNum = 1
  loadData()
}

const handleSizeChange = (size) => {
  pagination.pageSize = size
  loadData()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '上传凭证'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑凭证'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = async (row) => {
  const res = await disposalVoucherApi.getById(row.id)
  if (res.data) {
    detailData.value = res.data
    detailVisible.value = true
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除凭证"${row.voucherTitle}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await disposalVoucherApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    wasteId: null,
    orderId: null,
    feeId: null,
    voucherType: '',
    voucherTitle: '',
    voucherContent: '',
    filePath: '',
    fileName: '',
    fileSize: 0,
    disposalUnit: '',
    disposalPerson: '',
    disposalTime: '',
    remark: ''
  })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await disposalVoucherApi.update(form)
    ElMessage.success('更新成功')
  } else {
    await disposalVoucherApi.add(form)
    ElMessage.success('上传成功')
  }
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadWasteList()
  loadOrderList()
  loadFeeList()
  loadData()
})
</script>

<style scoped lang="scss">
.page-container {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .header-title {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
    }
  }

  .search-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 20px;
    flex-wrap: wrap;
  }

  .pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
