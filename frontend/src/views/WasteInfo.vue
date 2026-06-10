<template>
  <div class="page-container">
    <el-card shadow="hover">
      <div class="page-header">
        <div class="header-title">废弃物信息管理</div>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          登记废弃物
        </el-button>
      </div>

      <div class="search-bar">
        <el-input
          v-model="searchForm.keyword"
          placeholder="废弃物名称/编号/承租人"
          clearable
          style="width: 240px"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="待登记" :value="0" />
          <el-option label="已登记" :value="1" />
          <el-option label="已委托" :value="2" />
          <el-option label="清运中" :value="3" />
          <el-option label="已完成" :value="4" />
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
        <el-table-column prop="wasteNo" label="废弃物编号" width="180" />
        <el-table-column prop="wasteName" label="废弃物名称" width="180" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column label="数量" width="120" align="center">
          <template #default="{ row }">
            {{ row.quantity }} {{ row.unit }}
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="重量(kg)" width="100" align="right" />
        <el-table-column prop="renterName" label="承租人" width="100" />
        <el-table-column prop="renterPhone" label="联系电话" width="130" />
        <el-table-column prop="address" label="地址" show-overflow-tooltip min-width="180" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="登记时间" width="180" />
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
      width="720px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="废弃物名称" prop="wasteName">
              <el-input v-model="form.wasteName" placeholder="请输入废弃物名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%" @change="onCategoryChange">
                <el-option
                  v-for="item in categoryList"
                  :key="item.id"
                  :label="item.categoryName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="计量单位">
              <el-input v-model="form.unit" placeholder="如:件、kg" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="重量(kg)">
              <el-input-number v-model="form.weight" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入废弃物描述" />
        </el-form-item>
        <el-form-item label="照片上传">
          <el-upload
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :on-change="handlePhotoUpload"
            :file-list="photoFileList"
            multiple
            :limit="5"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-divider content-position="left">承租人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="承租人姓名">
              <el-input v-model="form.renterName" placeholder="请输入承租人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.renterPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="房屋地址">
          <el-input v-model="form.address" placeholder="请输入房屋地址" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="退房日期">
              <el-date-picker v-model="form.checkOutDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="待登记" :value="0" />
                <el-option label="已登记" :value="1" />
                <el-option label="已委托" :value="2" />
                <el-option label="清运中" :value="3" />
                <el-option label="已完成" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="废弃物详情" width="720px">
      <el-descriptions :column="2" border v-if="detailData">
        <el-descriptions-item label="废弃物编号">{{ detailData.wasteNo }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detailData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="废弃物名称" :span="2">{{ detailData.wasteName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ detailData.quantity }} {{ detailData.unit }}</el-descriptions-item>
        <el-descriptions-item label="重量">{{ detailData.weight }} kg</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ detailData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="承租人">{{ detailData.renterName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.renterPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房屋地址" :span="2">{{ detailData.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退房日期">{{ detailData.checkOutDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="登记时间" :span="2">{{ detailData.createdTime }}</el-descriptions-item>
        <el-descriptions-item label="照片" :span="2" v-if="detailData.photos">
          <div class="photo-preview">
            <el-image
              v-for="(photo, index) in detailData.photos.split(',')"
              :key="index"
              :src="photo"
              :preview-src-list="detailData.photos.split(',')"
              fit="cover"
              style="width: 100px; height: 100px; margin-right: 10px; border-radius: 4px"
            />
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { wasteInfoApi, wasteCategoryApi, fileApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('登记废弃物')
const formRef = ref(null)
const isEdit = ref(false)
const categoryList = ref([])
const dateRange = ref([])
const photoFileList = ref([])
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
  wasteNo: '',
  categoryId: null,
  wasteName: '',
  quantity: 0,
  unit: '',
  weight: 0,
  description: '',
  photos: '',
  renterName: '',
  renterPhone: '',
  address: '',
  checkOutDate: '',
  status: 0
})

const rules = {
  wasteName: [{ required: true, message: '请输入废弃物名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const getStatusText = (status) => {
  const map = { 0: '待登记', 1: '已登记', 2: '已委托', 3: '清运中', 4: '已完成' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: 'primary', 3: '', 4: 'success' }
  return map[status] || 'info'
}

const loadCategories = async () => {
  const res = await wasteCategoryApi.listAll()
  if (res.data) categoryList.value = res.data
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
    const res = await wasteInfoApi.list(params)
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

const onCategoryChange = (id) => {
  const category = categoryList.value.find(c => c.id === id)
  if (category) form.unit = category.unit
}

const handlePhotoUpload = async (uploadFile) => {
  const res = await fileApi.upload(uploadFile.raw)
  if (res.data) {
    const photos = form.photos ? form.photos.split(',') : []
    photos.push(res.data.fileUrl)
    form.photos = photos.join(',')
  }
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '登记废弃物'
  photoFileList.value = []
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑废弃物'
  Object.assign(form, row)
  photoFileList.value = []
  dialogVisible.value = true
}

const handleView = async (row) => {
  const res = await wasteInfoApi.getById(row.id)
  if (res.data) {
    detailData.value = res.data
    detailVisible.value = true
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除废弃物"${row.wasteName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await wasteInfoApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const resetForm = () => {
  formRef.value?.resetFields()
  Object.assign(form, {
    id: null,
    wasteNo: '',
    categoryId: null,
    wasteName: '',
    quantity: 0,
    unit: '',
    weight: 0,
    description: '',
    photos: '',
    renterName: '',
    renterPhone: '',
    address: '',
    checkOutDate: '',
    status: 0
  })
  photoFileList.value = []
}

const submitForm = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await wasteInfoApi.update(form)
    ElMessage.success('更新成功')
  } else {
    await wasteInfoApi.add(form)
    ElMessage.success('登记成功')
  }
  dialogVisible.value = false
  loadData()
}

onMounted(() => {
  loadCategories()
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

  .photo-preview {
    display: flex;
    flex-wrap: wrap;
  }
}
</style>
