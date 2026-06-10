<template>
  <div class="page-container">
    <el-card shadow="hover">
      <div class="page-header">
        <div class="header-title">清运委托管理</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增委托
        </el-button>
      </div>

      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="委托单号/承租人/联系人"
          clearable
          style="width: 240px"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="待接单" :value="0" />
          <el-option label="已接单" :value="1" />
          <el-option label="清运中" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
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
        <el-table-column prop="orderNo" label="委托单号" width="180" />
        <el-table-column prop="wasteNo" label="废弃物编号" width="180" />
        <el-table-column prop="wasteName" label="废弃物名称" width="160" show-overflow-tooltip />
        <el-table-column prop="renterName" label="承租人" width="100" />
        <el-table-column prop="renterPhone" label="承租人电话" width="130" />
        <el-table-column prop="address" label="清运地址" show-overflow-tooltip min-width="180" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="appointmentTime" label="预约时段" width="120" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link v-if="row.status === 0" @click="handleStatus(row, 1)">接单</el-button>
            <el-button type="warning" link v-if="row.status === 1" @click="handleStatus(row, 2)">开始清运</el-button>
            <el-button type="success" link v-if="row.status === 2" @click="handleStatus(row, 3)">完成</el-button>
            <el-button type="danger" link v-if="row.status < 3" @click="handleCancel(row)">取消</el-button>
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
      width="720px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
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
        <el-divider content-position="left">承租人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="承租人姓名">
              <el-input v-model="form.renterName" placeholder="请输入承租人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="承租人电话">
              <el-input v-model="form.renterPhone" placeholder="请输入承租人电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">清运信息</el-divider>
        <el-form-item label="清运地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入清运地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预约日期" prop="appointmentDate">
              <el-date-picker v-model="form.appointmentDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约时段">
              <el-select v-model="form.appointmentTime" placeholder="请选择时段" style="width: 100%">
                <el-option label="上午 09:00-12:00" value="上午 09:00-12:00" />
                <el-option label="下午 14:00-17:00" value="下午 14:00-17:00" />
                <el-option label="全天" value="全天" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="特殊要求">
          <el-input v-model="form.specialRequirement" type="textarea" :rows="2" placeholder="请输入特殊要求（可选）" />
        </el-form-item>
        <el-divider content-position="left" v-if="isEdit">清运人员</el-divider>
        <el-row :gutter="20" v-if="isEdit">
          <el-col :span="12">
            <el-form-item label="清运人员">
              <el-input v-model="form.collectorName" placeholder="请输入清运人员姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="清运电话">
              <el-input v-model="form.collectorPhone" placeholder="请输入清运人员电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" v-if="isEdit">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="待接单" :value="0" />
            <el-option label="已接单" :value="1" />
            <el-option label="清运中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="委托详情" width="720px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="委托单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="废弃物">{{ detailData.wasteName }} ({{ detailData.wasteNo }})</el-descriptions-item>
        <el-descriptions-item label="承租人">{{ detailData.renterName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="承租人电话">{{ detailData.renterPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="清运地址" :span="2">{{ detailData.address }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ detailData.appointmentDate }}</el-descriptions-item>
        <el-descriptions-item label="预约时段">{{ detailData.appointmentTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="特殊要求" :span="2">{{ detailData.specialRequirement || '-' }}</el-descriptions-item>
        <el-descriptions-item label="清运人员">{{ detailData.collectorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="清运电话">{{ detailData.collectorPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="实际清运时间">{{ detailData.actualPickupTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="取消原因" :span="2" v-if="detailData.status === 4">
          {{ detailData.cancelReason || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pickupOrderApi, wasteInfoApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增委托')
const formRef = ref(null)
const isEdit = ref(false)
const wasteList = ref([])
const dateRange = ref([])
const detailData = ref(null)

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  wasteId: null,
  renterName: '',
  renterPhone: '',
  address: '',
  contactPerson: '',
  contactPhone: '',
  appointmentDate: '',
  appointmentTime: '',
  specialRequirement: '',
  collectorName: '',
  collectorPhone: '',
  actualPickupTime: '',
  status: 0,
  cancelReason: ''
})

const rules = {
  wasteId: [{ required: true, message: '请选择废弃物', trigger: 'change' }],
  address: [{ required: true, message: '请输入清运地址', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  appointmentDate: [{ required: true, message: '请选择预约日期', trigger: 'change' }]
}

const getStatusText = (status) => {
  const map = { 0: '待接单', 1: '已接单', 2: '清运中', 3: '已完成', 4: '已取消' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: '', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

const loadWasteList = async () => {
  const res = await wasteInfoApi.list({ pageNum: 1, pageSize: 100 })
  if (res.data) wasteList.value = res.data.list || []
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchForm.keyword,
      status: searchForm.status
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await pickupOrderApi.list(params)
    if (res.data) {
      tableData.value = res.data.list || []
      pagination.total = res.data.total || 0
    }
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = null
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
  dialogTitle.value = '新增委托'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑委托'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = async (row) => {
  const res = await pickupOrderApi.getById(row.id)
  if (res.data) {
    detailData.value = res.data
    detailVisible.value = true
  }
}

const handleStatus = async (row, status) => {
  const msg = status === 1 ? '接单' : status === 2 ? '开始清运' : '完成清运'
  ElMessageBox.confirm(`确定${msg}吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    const data = { ...row, status }
    if (status === 2) data.actualPickupTime = new Date()
    await pickupOrderApi.update(data)
    ElMessage.success('操作成功')
    loadData()
  }).catch(() => {})
}

const handleCancel = (row) => {
  ElMessageBox.prompt('请输入取消原因', '取消委托', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入取消原因'
  }).then(async ({ value }) => {
    await pickupOrderApi.update({ ...row, status: 4, cancelReason: value })
    ElMessage.success('已取消')
    loadData()
  }).catch(() => {})
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    wasteId: null,
    renterName: '',
    renterPhone: '',
    address: '',
    contactPerson: '',
    contactPhone: '',
    appointmentDate: '',
    appointmentTime: '',
    specialRequirement: '',
    collectorName: '',
    collectorPhone: '',
    actualPickupTime: '',
    status: 0,
    cancelReason: ''
  })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await pickupOrderApi.update(form)
    ElMessage.success('更新成功')
  } else {
    await pickupOrderApi.add(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadWasteList()
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
