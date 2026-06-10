import request from '@/utils/request'

export const dashboardApi = {
  overview() {
    return request({
      url: '/dashboard/overview',
      method: 'get'
    })
  },
  stats() {
    return request({
      url: '/dashboard/stats',
      method: 'get'
    })
  },
  categoryWasteCount() {
    return request({
      url: '/dashboard/categoryWasteCount',
      method: 'get'
    })
  }
}

export const wasteCategoryApi = {
  list(params) {
    return request({
      url: '/wasteCategory/list',
      method: 'get',
      params
    })
  },
  listAll() {
    return request({
      url: '/wasteCategory/listAll',
      method: 'get'
    })
  },
  getById(id) {
    return request({
      url: `/wasteCategory/${id}`,
      method: 'get'
    })
  },
  add(data) {
    return request({
      url: '/wasteCategory',
      method: 'post',
      data
    })
  },
  update(data) {
    return request({
      url: '/wasteCategory',
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/wasteCategory/${id}`,
      method: 'delete'
    })
  }
}

export const wasteInfoApi = {
  list(params) {
    return request({
      url: '/wasteInfo/list',
      method: 'get',
      params
    })
  },
  getById(id) {
    return request({
      url: `/wasteInfo/${id}`,
      method: 'get'
    })
  },
  add(data) {
    return request({
      url: '/wasteInfo',
      method: 'post',
      data
    })
  },
  update(data) {
    return request({
      url: '/wasteInfo',
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/wasteInfo/${id}`,
      method: 'delete'
    })
  }
}

export const pickupOrderApi = {
  list(params) {
    return request({
      url: '/pickupOrder/list',
      method: 'get',
      params
    })
  },
  getById(id) {
    return request({
      url: `/pickupOrder/${id}`,
      method: 'get'
    })
  },
  add(data) {
    return request({
      url: '/pickupOrder',
      method: 'post',
      data
    })
  },
  update(data) {
    return request({
      url: '/pickupOrder',
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/pickupOrder/${id}`,
      method: 'delete'
    })
  }
}

export const feeRecordApi = {
  list(params) {
    return request({
      url: '/feeRecord/list',
      method: 'get',
      params
    })
  },
  getById(id) {
    return request({
      url: `/feeRecord/${id}`,
      method: 'get'
    })
  },
  add(data) {
    return request({
      url: '/feeRecord',
      method: 'post',
      data
    })
  },
  update(data) {
    return request({
      url: '/feeRecord',
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/feeRecord/${id}`,
      method: 'delete'
    })
  }
}

export const disposalVoucherApi = {
  list(params) {
    return request({
      url: '/disposalVoucher/list',
      method: 'get',
      params
    })
  },
  getById(id) {
    return request({
      url: `/disposalVoucher/${id}`,
      method: 'get'
    })
  },
  add(data) {
    return request({
      url: '/disposalVoucher',
      method: 'post',
      data
    })
  },
  update(data) {
    return request({
      url: '/disposalVoucher',
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/disposalVoucher/${id}`,
      method: 'delete'
    })
  }
}

export const operationLogApi = {
  list(params) {
    return request({
      url: '/operationLog/list',
      method: 'get',
      params
    })
  },
  getById(id) {
    return request({
      url: `/operationLog/${id}`,
      method: 'get'
    })
  }
}

export const fileApi = {
  upload(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  uploadVoucher(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/file/uploadVoucher',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },
  delete(filePath) {
    return request({
      url: '/file/delete',
      method: 'delete',
      params: { filePath }
    })
  }
}
