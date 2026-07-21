<script setup>
import { onMounted, ref } from 'vue'
import { applyProductImageBackfill, previewProductImageBackfill } from '../api/productApi'

const emit = defineEmits(['close', 'applied'])
const loading = ref(true), applying = ref(false), confirming = ref(false), error = ref(''), preview = ref(null), result = ref(null), selectedModels = ref([])

async function load() {
  loading.value = true
  error.value = ''
  try { preview.value = await previewProductImageBackfill(); selectedModels.value = preview.value.models.map(model => model.normalizedModelName) }
  catch (exception) { error.value = exception.message }
  finally { loading.value = false }
}
async function apply() {
  if (!selectedModels.value.length) return
  applying.value = true
  error.value = ''
  try { result.value = await applyProductImageBackfill(selectedModels.value); confirming.value = false; emit('applied', result.value) }
  catch (exception) { error.value = exception.message }
  finally { applying.value = false }
}
onMounted(load)
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <section class="modal backfill-modal" role="dialog" aria-modal="true" aria-label="自動補齊商品圖片預覽">
      <div class="card-header"><div><h2>自動補齊商品圖片</h2><p>目前為只讀預覽，不會下載圖片或修改商品。</p></div><button class="button button-ghost button-small" @click="emit('close')">關閉</button></div>
      <p v-if="error" class="alert error">{{ error }}</p>
      <div v-if="loading" class="loading-row">正在分析 Apple 商品與標準機型…</div>
      <template v-else-if="preview">
        <div class="backfill-stats">
          <div><strong>{{ preview.scannedProducts - preview.skippedProducts }}</strong><span>無圖片商品</span></div>
          <div><strong>{{ preview.uniqueModels }}</strong><span>不同標準機型</span></div>
          <div><strong>{{ preview.matchedModels }}</strong><span>可成功配對</span></div>
          <div><strong>{{ preview.failedModels }}</strong><span>無法辨識／待配對</span></div>
        </div>
        <div class="backfill-list">
          <article v-for="model in preview.models" :key="model.normalizedModelName" class="backfill-item">
            <label><input v-model="selectedModels" type="checkbox" :value="model.normalizedModelName"> 套用此機型</label>
            <img v-if="model.previewUrl" :src="model.previewUrl" :alt="model.normalizedModelName" referrerpolicy="no-referrer">
            <div v-else class="model-placeholder"><b>{{ model.productType }}</b><span>{{ model.normalizedModelName }}</span></div>
            <div class="backfill-detail"><div><h3>{{ model.normalizedModelName }}</h3><span :class="['badge', model.matched ? 'badge-success' : '']">{{ model.matched ? model.sourceType : '統一 placeholder' }}</span></div><p>{{ model.productCount }} 筆商品共用此圖</p><small v-if="model.failureReason">{{ model.failureReason }}</small><a v-if="model.sourcePageUrl" :href="model.sourcePageUrl" target="_blank" rel="noopener noreferrer">檢查圖片來源</a><details><summary>查看商品名稱</summary><ul><li v-for="name in model.productNames" :key="name">{{ name }}</li></ul></details></div>
          </article>
        </div>
        <div v-if="preview.failures?.length" class="backfill-failures"><h3>無法辨識或待配對</h3><ul><li v-for="failure in preview.failures" :key="`${failure.normalizedModelName}-${failure.reason}`"><b>{{ failure.normalizedModelName }}</b>：{{ failure.reason }}</li></ul></div>
        <p v-if="result" class="alert success">正式套用完成：更新 {{ result.updatedProducts }} 筆、略過 {{ result.skippedProducts }} 筆、失敗機型 {{ result.failedModels }} 個。</p>
        <div v-if="confirming" class="backfill-failures"><b>確認正式套用 {{ selectedModels.length }} 個機型？</b><p>系統將開始下載合法來源圖片或建立 placeholder，並寫入目前仍無圖片的商品。</p><div class="modal-actions"><button class="button button-ghost" :disabled="applying" @click="confirming=false">返回檢查</button><button class="button button-primary" :disabled="applying" @click="apply">{{ applying ? '套用中…' : '確認下載並寫入' }}</button></div></div>
        <div v-else><p class="preview-warning">請確認以上型號、候選圖片與勾選範圍，再進入最後確認。</p><button class="button button-primary" :disabled="!selectedModels.length || applying" @click="confirming=true">確認配對並繼續</button></div>
      </template>
    </section>
  </div>
</template>

<style scoped>
.backfill-modal{width:min(960px,100%);max-height:90vh;overflow:auto;text-align:left}.backfill-stats{display:grid;grid-template-columns:repeat(4,1fr);gap:12px;margin-bottom:20px}.backfill-stats div{padding:15px;border:1px solid var(--slate-200);border-radius:12px;background:var(--slate-50)}.backfill-stats strong,.backfill-stats span{display:block}.backfill-stats strong{font-size:1.5rem}.backfill-stats span{color:var(--slate-600);font-size:.8rem}.backfill-list{display:grid;gap:14px}.backfill-item{display:grid;grid-template-columns:150px 1fr;gap:18px;padding:14px;border:1px solid var(--slate-200);border-radius:12px}.backfill-item img,.model-placeholder{width:150px;height:120px;border-radius:10px;object-fit:contain;background:var(--slate-100)}.model-placeholder{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:12px;text-align:center}.model-placeholder b{font-size:1.05rem}.model-placeholder span{color:var(--slate-600);font-size:.78rem}.backfill-detail>div{display:flex;align-items:center;gap:10px}.backfill-detail h3{margin:0}.backfill-detail p{margin:7px 0}.backfill-detail small{display:block;color:var(--red-600)}.backfill-detail a{display:inline-block;margin-top:8px}.backfill-detail details{margin-top:8px}.backfill-detail ul,.backfill-failures ul{margin:7px 0 0;padding-left:20px}.backfill-failures{margin-top:20px;padding:15px;border:1px solid #f7bdc5;border-radius:10px;background:var(--red-100)}.preview-warning{margin:20px 0 0;padding:12px 15px;border-radius:10px;background:#fff8dc;color:#6d5500}@media(max-width:680px){.backfill-stats{grid-template-columns:repeat(2,1fr)}.backfill-item{grid-template-columns:1fr}.backfill-item img,.model-placeholder{width:100%}}
</style>
