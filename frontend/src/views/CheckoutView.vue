<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { checkout } from '../api/orderApi'
import { useAuthStore } from '../stores/auth'
import { useCartStore } from '../stores/cart'

const auth = useAuthStore()
const cart = useCartStore()
const router = useRouter()
const submitting = ref(false)
const error = ref('')
const form = reactive({ recipientName: '', recipientPhone: '', recipientAddress: '', paymentMethod: 'DEMO_CARD', note: '' })
const shippingFee = computed(() => Number(cart.totalAmount) >= 10000 ? 0 : 60)
const total = computed(() => Number(cart.totalAmount) + shippingFee.value)
const money = (value) => new Intl.NumberFormat('zh-TW', { style: 'currency', currency: 'TWD', maximumFractionDigits: 0 }).format(value || 0)

async function submit() {
  error.value = ''
  if (!form.recipientName.trim() || !form.recipientPhone.trim() || !form.recipientAddress.trim()) { error.value = '請完整填寫收件人、電話與地址。'; return }
  if (submitting.value) return
  submitting.value = true
  try {
    const order = await checkout(form)
    cart.reset()
    await router.replace({ path: '/checkout/success', query: { order: order.id, number: order.orderNumber } })
  } catch (e) { error.value = e.message } finally { submitting.value = false }
}

onMounted(async () => {
  form.recipientName = auth.currentUser?.name || ''
  form.recipientPhone = auth.currentUser?.phone || ''
  try { await cart.load() } catch (e) { error.value = e.message }
})
</script>

<template>
  <main class="commerce-page container">
    <div class="page-heading"><div><span class="eyebrow">SECURE CHECKOUT</span><h1>確認訂單</h1><p>本頁付款功能僅供期末展示，不會產生真實扣款。</p></div></div>
    <p v-if="error" class="alert alert-error">{{ error }}</p>
    <section v-if="!cart.loading && !cart.items.length" class="empty-state"><h2>沒有可結帳的商品</h2><RouterLink class="button button-primary" to="/products">返回商品列表</RouterLink></section>
    <form v-else class="checkout-layout" @submit.prevent="submit">
      <section class="card checkout-form"><h2>收件資料</h2><div class="form-grid"><label>收件人<input v-model.trim="form.recipientName" required maxlength="80"></label><label>聯絡電話<input v-model.trim="form.recipientPhone" required maxlength="30"></label><label class="field-full">收件地址<input v-model.trim="form.recipientAddress" required maxlength="255"></label><label class="field-full">付款方式<select v-model="form.paymentMethod"><option value="DEMO_CARD">展示用模擬信用卡</option><option value="CASH_ON_DELIVERY">貨到付款（展示）</option></select></label><label class="field-full">訂單備註<textarea v-model.trim="form.note" maxlength="500" rows="3"></textarea></label></div><p class="checkout-note">「展示用模擬信用卡」只會將訂單標示為已付款，不會連接任何真實金流。</p></section>
      <aside class="order-summary card"><h2>金額摘要</h2><div v-for="item in cart.items" :key="item.id" class="summary-line"><span>{{ item.productName }} × {{ item.quantity }}</span><strong>{{ money(item.subtotal) }}</strong></div><div class="summary-line"><span>運費</span><strong>{{ shippingFee ? money(shippingFee) : '免運' }}</strong></div><div class="summary-line total"><span>應付總額</span><strong>{{ money(total) }}</strong></div><button class="button button-primary button-block" :disabled="submitting || cart.loading">{{ submitting ? '正在建立訂單…' : '確認建立訂單' }}</button><RouterLink class="button button-ghost button-block" to="/cart">返回購物車</RouterLink></aside>
    </form>
  </main>
</template>
