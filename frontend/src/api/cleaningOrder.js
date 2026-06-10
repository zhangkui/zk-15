import request from './request'

export function listCleaningOrder(params) {
  return request.get('/api/cleaning-order/page', { params })
}

export function listAllCleaningOrder() {
  return request.get('/api/cleaning-order/list')
}

export function getCleaningOrder(id) {
  return request.get('/api/cleaning-order/' + id)
}

export function addCleaningOrder(data) {
  return request.post('/api/cleaning-order', data)
}

export function updateCleaningOrder(data) {
  return request.put('/api/cleaning-order', data)
}

export function deleteCleaningOrder(id) {
  return request.delete('/api/cleaning-order/' + id)
}

export function updateCleaningOrderStatus(id, status) {
  return request.put(`/api/cleaning-order/${id}/status?status=${status}`)
}
