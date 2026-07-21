import http from './http'

export async function getProducts(params = {}) {
  return (await http.get('/products', { params })).data
}

export async function getProductById(id) {
  return (await http.get(`/products/${id}`)).data
}

export async function createProduct(product) {
  return (await http.post('/products', product)).data
}

export async function updateProduct(id, product) {
  return (await http.put(`/products/${id}`, product)).data
}

export async function deleteProduct(id) {
  await http.delete(`/products/${id}`)
}

export async function getBrands(admin = false) {
  return (await http.get(admin ? '/admin/brands' : '/brands')).data
}

export async function createBrand(payload) {
  return (await http.post('/admin/brands', payload)).data
}

export async function updateBrand(id, payload) {
  return (await http.put(`/admin/brands/${id}`, payload)).data
}

export async function getCategories(admin = false) {
  return (await http.get(admin ? '/admin/categories' : '/categories')).data
}

export async function createCategory(payload) {
  return (await http.post('/admin/categories', payload)).data
}

export async function updateCategory(id, payload) {
  return (await http.put(`/admin/categories/${id}`, payload)).data
}

export async function getInventoryMovements(params = {}) {
  return (await http.get('/admin/inventory/movements', { params })).data
}

export async function adjustInventory(productId, payload) {
  return (await http.post(`/admin/inventory/products/${productId}/adjustments`, payload)).data
}

export async function addProductImage(productId, payload) {
  return (await http.post(`/admin/products/${productId}/images`, payload)).data
}

export async function deleteProductImage(productId, imageId) {
  await http.delete(`/admin/products/${productId}/images/${imageId}`)
}

export async function uploadProductImage(productId, file, primaryImage = false, sortOrder = 0) {
  const data = new FormData()
  data.append('file', file)
  data.append('primaryImage', String(primaryImage))
  data.append('sortOrder', String(sortOrder))
  return (await http.post(`/admin/products/${productId}/images/upload`, data)).data
}
