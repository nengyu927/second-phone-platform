<script setup>
import { reactive, ref } from 'vue'
import { importProductsCsv } from '../api/productApi'

defineProps({ brands: { type: Array, default: () => [] }, categories: { type: Array, default: () => [] } })
const emit = defineEmits(['close', 'imported', 'error'])
const form = reactive({ brandId: '', categoryId: '' })
const file = ref(null), importing = ref(false), result = ref(null)

async function submit() {
  if (!file.value || !form.brandId || !form.categoryId) return
  importing.value = true
  result.value = null
  try {
    result.value = await importProductsCsv(file.value, form.categoryId, form.brandId)
    emit('imported', result.value)
  } catch (error) {
    emit('error', error)
  } finally {
    importing.value = false
  }
}
</script>

<template>
  <div class="modal-backdrop" @click.self="!importing && emit('close')">
    <form class="modal csv-import-modal form-card" @submit.prevent="submit">
      <h2>CSV 批次匯入</h2>
      <p>欄位順序：使用程度、二手等級、商品名稱、備註、價格。檔案須為 UTF-8。</p>
      <label>品牌 *<select v-model="form.brandId" required><option value="" disabled>請選擇品牌</option><option v-for="brand in brands" :key="brand.id" :value="brand.id">{{ brand.name }}</option></select></label>
      <label>分類 *<select v-model="form.categoryId" required><option value="" disabled>請選擇分類</option><option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option></select></label>
      <label>CSV 檔案 *<input type="file" accept=".csv,text/csv" required @change="file=$event.target.files[0] || null"></label>
      <div v-if="result" class="csv-import-result">
        <p><b>共 {{ result.totalCount }} 筆：</b>成功 {{ result.successCount }}、略過 {{ result.skippedCount }}、失敗 {{ result.failedCount }}</p>
        <ul v-if="result.failures?.length"><li v-for="failure in result.failures" :key="`${failure.lineNumber}-${failure.reason}`">第 {{ failure.lineNumber }} 行：{{ failure.reason }}</li></ul>
      </div>
      <div class="modal-actions">
        <button type="button" class="button button-ghost" :disabled="importing" @click="emit('close')">關閉</button>
        <button class="button button-primary" :disabled="importing || !file || !form.brandId || !form.categoryId">{{ importing ? '匯入中…' : '開始匯入' }}</button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.csv-import-modal{width:min(560px,100%);text-align:left}.csv-import-modal>label{margin-top:14px}.csv-import-result{margin-top:18px;padding:12px 15px;border-radius:10px;background:var(--slate-50);border:1px solid var(--slate-200)}.csv-import-result p{margin:0}.csv-import-result ul{max-height:160px;overflow:auto;margin:10px 0 0;padding-left:22px;color:var(--red-600)}
</style>
