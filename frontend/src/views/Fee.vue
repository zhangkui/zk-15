<template>
  <div>
    <el-card>
      <el-row :gutter="16" style="margin-bottom: 16px">
        <el-col :span="8">
          <el-select v-model="selectedOrderId" placeholder="选择委托单" style="width: 100%" @change="loadFees">
            <el-option v-for="o in orderList" :key="o.id" :label="o.orderNo + ' - ' + o.wasteItemName" :value="o.id" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="success" @click="openDialog" :disabled="!selectedOrderId">新增费用</el-button>
        </el-col>
      </el-row>

      <el-table :data="feeList" border stripe>
        <el-table-column prop="feeType" label="费用类型" width="150" />
        <el-table-column prop="amount" label="金额(元)" width="120" />
        <el-table-column prop="description" label="说明" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-row style="margin-top: 16px; justify-content: flex-end">
        <el-col :span="6" style="text-align: right">
          <span style="font-size: 16px; font-weight: bold">费用合计：{{ totalAmount }} 元</span>
        </el-col>
      </el-row>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑费用' : '新增费用'" width="500px" destroy-on-close>
      <el-form :model="form" label-width="80px">
        <el-form-item label="费用类型">
          <el-select v-model="form.feeType" style="width: 100%">
            <el-option label="清运费" value="清运费" />
            <el-option label="运输费" value="运输费" />
            <el-option label="处理费" value="处理费" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额(元)">
          <el-input-number v-model="form.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listFee, addFee, updateFee, deleteFee } from '../api/fee'
import { listAllCleaningOrder } from '../api/cleaningOrder'

const selectedOrderId = ref('')
const orderList = ref([])
const feeList = ref([])
const dialogVisible = ref(false)
const form = ref({})

const totalAmount = computed(() => {
  return feeList.value.reduce((sum, item) => sum + (Number(item.amount) || 0), 0).toFixed(2)
})

const loadOrders = async () => {
  const res = await listAllCleaningOrder()
  orderList.value = res.data || []
}

const loadFees = async () => {
  if (!selectedOrderId.value) {
    feeList.value = []
    return
  }
  const res = await listFee(selectedOrderId.value)
  feeList.value = res.data || []
}

const openDialog = () => {
  form.value = { orderId: selectedOrderId.value, feeType: '', amount: 0, description: '' }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (form.value.id) {
    await updateFee(form.value)
  } else {
    form.value.orderId = selectedOrderId.value
    await addFee(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  loadFees()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该费用记录？', '提示', { type: 'warning' }).then(async () => {
    await deleteFee(row.id)
    ElMessage.success('删除成功')
    loadFees()
  })
}

onMounted(() => {
  loadOrders()
})
</script>
