import request from './request'

export function listCategory() {
  return request.get('/api/category/list')
}

export function getCategory(id) {
  return request.get('/api/category/' + id)
}

export function addCategory(data) {
  return request.post('/api/category', data)
}

export function updateCategory(data) {
  return request.put('/api/category', data)
}

export function deleteCategory(id) {
  return request.delete('/api/category/' + id)
}
