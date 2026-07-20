<script setup>
import { onMounted, reactive, ref } from 'vue'
import { createProduct, deleteProduct, getProducts, updateProduct } from '../api/productApi'

const products = ref([])
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const editingId = ref(null)
const filters = reactive({ keyword: '', brand: '', status: '' })
const emptyForm = () => ({
  productName: '', brand: '', model: '', storageCapacity: '', color: '',
  conditionLevel: 'GOOD', price: 0, stock: 0, description: '', imageUrl: '', status: 'AVAILABLE'
})
const form = reactive(emptyForm())
const statusLabels = { AVAILABLE: '可販售', SOLD_OUT: '已售完', DISABLED: '停用' }

function activeFilters() {
  return Object.fromEntries(Object.entries(filters).filter(([, value]) => value !== ''))
}

async function loadProducts() {
  loading.value = true
  errorMessage.value = ''
  try {
    products.value = await getProducts(activeFilters())
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    loading.value = false
  }
}

function clearFilters() {
  Object.assign(filters, { keyword: '', brand: '', status: '' })
  loadProducts()
}

function editProduct(product) {
  editingId.value = product.id
  Object.assign(form, {
    productName: product.productName,
    brand: product.brand,
    model: product.model,
    storageCapacity: product.storageCapacity || '',
    color: product.color || '',
    conditionLevel: product.conditionLevel || 'GOOD',
    price: product.price,
    stock: product.stock,
    description: product.description || '',
    imageUrl: product.imageUrl || '',
    status: product.status || 'AVAILABLE'
  })
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function cancelEdit() {
  editingId.value = null
  Object.assign(form, emptyForm())
  errorMessage.value = ''
}

async function submitForm() {
  saving.value = true
  errorMessage.value = ''
  try {
    const payload = { ...form, price: Number(form.price), stock: Number(form.stock) }
    if (editingId.value) await updateProduct(editingId.value, payload)
    else await createProduct(payload)
    cancelEdit()
    await loadProducts()
  } catch (error) {
    errorMessage.value = error.message
  } finally {
    saving.value = false
  }
}

async function removeProduct(product) {
  if (!window.confirm(`確定刪除商品「${product.productName}」？`)) return
  errorMessage.value = ''
  try {
    await deleteProduct(product.id)
    if (editingId.value === product.id) cancelEdit()
    await loadProducts()
  } catch (error) {
    errorMessage.value = error.message
  }
}

function formatPrice(value) {
  return new Intl.NumberFormat('zh-TW', { style: 'currency', currency: 'TWD', maximumFractionDigits: 2 }).format(value)
}

onMounted(loadProducts)
</script>

<template>
  <section class="management-page">
    <div class="page-heading"><div><h2>商品管理</h2><p>管理二手手機商品與庫存。</p></div><button class="secondary" type="button" :disabled="loading" @click="loadProducts">{{ loading ? '載入中...' : '重新整理' }}</button></div>
    <p v-if="errorMessage" class="alert error">{{ errorMessage }}</p>

    <form class="card" @submit.prevent="submitForm">
      <h3>{{ editingId ? `編輯商品 #${editingId}` : '新增商品' }}</h3>
      <div class="form-grid">
        <label>商品名稱 *<input v-model.trim="form.productName" maxlength="150" required /></label>
        <label>品牌 *<input v-model.trim="form.brand" maxlength="50" required /></label>
        <label>型號 *<input v-model.trim="form.model" maxlength="100" required /></label>
        <label>容量<input v-model.trim="form.storageCapacity" maxlength="30" placeholder="例如 128GB" /></label>
        <label>顏色<input v-model.trim="form.color" maxlength="50" /></label>
        <label>商品狀況 *<select v-model="form.conditionLevel"><option>LIKE_NEW</option><option>GOOD</option><option>FAIR</option></select></label>
        <label>價格 *<input v-model.number="form.price" type="number" min="0" step="0.01" required /></label>
        <label>庫存 *<input v-model.number="form.stock" type="number" min="0" step="1" required /></label>
        <label>圖片網址<input v-model.trim="form.imageUrl" type="url" maxlength="500" /></label>
        <label>狀態 *<select v-model="form.status"><option value="AVAILABLE">可販售</option><option value="SOLD_OUT">已售完</option><option value="DISABLED">停用</option></select></label>
        <label class="full-width">商品說明<textarea v-model.trim="form.description" rows="3"></textarea></label>
      </div>
      <div class="form-actions">
        <button class="primary" :disabled="saving">{{ saving ? '儲存中...' : editingId ? '儲存修改' : '新增商品' }}</button>
        <button v-if="editingId" class="secondary" type="button" @click="cancelEdit">取消編輯</button>
      </div>
    </form>

    <form class="card search-bar" @submit.prevent="loadProducts">
      <label>關鍵字<input v-model.trim="filters.keyword" placeholder="搜尋商品名稱" /></label>
      <label>品牌<input v-model.trim="filters.brand" placeholder="例如 Apple" /></label>
      <label>狀態<select v-model="filters.status"><option value="">全部</option><option value="AVAILABLE">可販售</option><option value="SOLD_OUT">已售完</option><option value="DISABLED">停用</option></select></label>
      <button class="primary">搜尋</button><button class="secondary" type="button" @click="clearFilters">清除條件</button>
    </form>

    <div class="card">
      <h3>商品列表</h3>
      <p v-if="loading" class="status-text">資料載入中...</p>
      <p v-else-if="products.length === 0" class="status-text">目前沒有符合條件的商品。</p>
      <div v-else class="table-scroll">
        <table>
          <thead><tr><th>圖片</th><th>ID</th><th>商品名稱</th><th>品牌</th><th>型號</th><th>容量</th><th>顏色</th><th>狀況</th><th>價格</th><th>庫存</th><th>狀態</th><th>操作</th></tr></thead>
          <tbody>
            <tr v-for="product in products" :key="product.id">
              <td><img v-if="product.imageUrl" class="product-thumb" :src="product.imageUrl" :alt="product.productName" @error="$event.target.style.display = 'none'" /><span v-else>-</span></td>
              <td>{{ product.id }}</td><td>{{ product.productName }}</td><td>{{ product.brand }}</td><td>{{ product.model }}</td>
              <td>{{ product.storageCapacity || '-' }}</td><td>{{ product.color || '-' }}</td><td>{{ product.conditionLevel }}</td>
              <td>{{ formatPrice(product.price) }}</td><td>{{ product.stock }}</td><td><span class="badge">{{ statusLabels[product.status] || product.status }}</span></td>
              <td class="row-actions"><button class="secondary small" @click="editProduct(product)">編輯</button><button class="danger small" @click="removeProduct(product)">刪除</button></td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </section>
</template>
