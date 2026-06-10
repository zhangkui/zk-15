import request from './request'

export function listCertificate(orderId) {
  return request.get('/api/certificate/order/' + orderId)
}

export function uploadCertificate(data) {
  return request.post('/api/certificate', data)
}

export function deleteCertificate(id) {
  return request.delete('/api/certificate/' + id)
}
