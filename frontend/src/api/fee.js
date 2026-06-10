import request from './request'

export function listFee(orderId) {
  return request.get('/api/cleaning-fee/order/' + orderId)
}

export function addFee(data) {
  return request.post('/api/cleaning-fee', data)
}

export function updateFee(data) {
  return request.put('/api/cleaning-fee', data)
}

export function deleteFee(id) {
  return request.delete('/api/cleaning-fee/' + id)
}
