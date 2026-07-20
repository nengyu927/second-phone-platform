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
