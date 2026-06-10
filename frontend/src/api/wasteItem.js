import request from './request'

export function listWasteItem(params) {
  return request.get('/api/waste-item/page', { params })
}

export function listAllWasteItem() {
  return request.get('/api/waste-item/list')
}

export function getWasteItem(id) {
  return request.get('/api/waste-item/' + id)
}

export function addWasteItem(data) {
  return request.post('/api/waste-item', data)
}

export function updateWasteItem(data) {
  return request.put('/api/waste-item', data)
}

export function deleteWasteItem(id) {
  return request.delete('/api/waste-item/' + id)
}

export function updateWasteItemStatus(id, status) {
  return request.put(`/api/waste-item/${id}/status?status=${status}`)
}
