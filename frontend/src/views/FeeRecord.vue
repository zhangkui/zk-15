<template>
  <div class="page-container">
    <el-card shadow="hover">
      <div class="page-header">
        <div class="header-title">清运费用记录</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增费用
        </el-button>
      </div>

      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="费用单号/付款人/发票号"
          clearable
          style="width: 240px"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="待支付" :value="0" />
          <el-option label="已支付" :value="1" />
          <el-option label="已退款" :value="2" />
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
        <el-table-column prop="feeNo" label="费用单号" width="180" />
        <el-table-column prop="wasteName" label="废弃物" width="160" show-overflow-tooltip />
        <el-table-column prop="orderNo" label="委托单号" width="180" />
        <el-table-column prop="baseFee" label="基础费用" width="100" align="right">
          <template #default="{ row }">￥{{ row.baseFee || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="quantityFee" label="按量费用" width="100" align="right">
          <template #default="{ row }">￥{{ row.quantityFee || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="extraFee" label="额外费用" width="100" align="right">
          <template #default="{ row }">￥{{ row.extraFee || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="110" align="right">
          <template #default="{ row }">
            <span style="color: #f56c6c; font-weight: bold">￥{{ row.totalAmount || '0.00' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="payerName" label="付款人" width="100" />
        <el-table-column prop="paymentMethod" label="支付方式" width="100" />
        <el-table-column prop="paymentTime" label="支付时间" width="180" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
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
            <el-form-item label="废弃物" prop="wasteId">
              <el-select v-model="form.wasteId" placeholder="请选择废弃物" filterable style="width: 100%" @change="onWasteChange">
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
        <el-divider content-position="left">费用明细</el-divider>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="基础费用">
              <el-input-number v-model="form.baseFee" :min="0" :precision="2" style="width: 100%" @change="calcTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="按量费用">
              <el-input-number v-model="form.quantityFee" :min="0" :precision="2" style="width: 100%" @change="calcTotal" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="额外费用">
              <el-input-number v-model="form.extraFee" :min="0" :precision="2" style="width: 100%" @change="calcTotal" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="额外费用说明">
          <el-input v-model="form.extraFeeReason" placeholder="请输入额外费用说明（可选）" />
        </el-form-item>
        <el-form-item label="总金额" prop="totalAmount">
          <el-input-number v-model="form.totalAmount" :min="0" :precision="2" style="width: 200px; color: #f56c6c" />
          <span style="margin-left: 10px; color: #909399">（自动计算，可手动调整）</span>
        </el-form-item>
        <el-divider content-position="left">支付信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="付款人">
              <el-input v-model="form.payerName" placeholder="请输入付款人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="支付方式">
              <el-select v-model="form.paymentMethod" placeholder="请选择支付方式" clearable style="width: 100%">
                <el-option label="微信" value="微信" />
                <el-option label="支付宝" value="支付宝" />
                <el-option label="现金" value="现金" />
                <el-option label="转账" value="转账" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="支付时间">
              <el-date-picker v-model="form.paymentTime" type="datetime" placeholder="选择时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发票号">
              <el-input v-model="form.invoiceNo" placeholder="请输入发票号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">待支付</el-radio>
            <el-radio :value="1">已支付</el-radio>
            <el-radio :value="2">已退款</el-radio>
          </el-radio-group>
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

    <el-dialog v-model="detailVisible" title="费用详情" width="680px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="费用单号">{{ detailData.feeNo }}</el-descriptions-item>
        <el-descriptions-item label="废弃物">{{ detailData.wasteName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="委托单号">{{ detailData.orderNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="基础费用">￥{{ detailData.baseFee || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="按量费用">￥{{ detailData.quantityFee || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="额外费用">￥{{ detailData.extraFee || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="总金额" style="color: #f56c6c">￥{{ detailData.totalAmount || '0.00' }}</el-descriptions-item>
        <el-descriptions-item label="额外费用说明" :span="2">{{ detailData.extraFeeReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="付款人">{{ detailData.payerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ detailData.paymentMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detailData.paymentTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发票号">{{ detailData.invoiceNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { feeRecordApi, wasteInfoApi, pickupOrderApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('新增费用')
const formRef = ref(null)
const isEdit = ref(false)
const wasteList = ref([])
const orderList = ref([])
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
  orderId: null,
  categoryId: null,
  baseFee: 0,
  quantityFee: 0,
  extraFee: 0,
  extraFeeReason: '',
  totalAmount: 0,
  paymentMethod: '',
  paymentTime: '',
  payerName: '',
  invoiceNo: '',
  remark: '',
  status: 0
})

const rules = {
  wasteId: [{ required: true, message: '请选择废弃物', trigger: 'change' }],
  totalAmount: [{ required: true, message: '请输入总金额', trigger: 'blur' }]
}

const getStatusText = (status) => {
  const map = { 0: '待支付', 1: '已支付', 2: '已退款' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const calcTotal = () => {
  form.totalAmount = Number(((form.baseFee || 0) + (form.quantityFee || 0) + (form.extraFee || 0)).toFixed(2))
}

const onWasteChange = (id) => {
  const waste = wasteList.value.find(w => w.id === id)
  if (waste && waste.categoryId) {
    form.categoryId = waste.categoryId
  }
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
    const res = await feeRecordApi.list(params)
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
  dialogTitle.value = '新增费用'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑费用'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = async (row) => {
  const res = await feeRecordApi.getById(row.id)
  if (res.data) {
    detailData.value = res.data
    detailVisible.value = true
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除该费用记录吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await feeRecordApi.delete(row.id)
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
    categoryId: null,
    baseFee: 0,
    quantityFee: 0,
    extraFee: 0,
    extraFeeReason: '',
    totalAmount: 0,
    paymentMethod: '',
    paymentTime: '',
    payerName: '',
    invoiceNo: '',
    remark: '',
    status: 0
  })
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await feeRecordApi.update(form)
    ElMessage.success('更新成功')
  } else {
    await feeRecordApi.add(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadWasteList()
  loadOrderList()
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
