import request from './request'

export function listArchive(orderId) {
  return request.get('/api/archive/order/' + orderId)
}

export function addArchive(data) {
  return request.post('/api/archive', data)
}
