<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ConfirmModal from '../components/ConfirmModal.vue'
import { useCartStore } from '../stores/cart'

const cart = useCartStore()
const router = useRouter()
const error = ref('')
const busyId = ref(null)
const confirmAction = ref(null)

const money = (value) => new Intl.NumberFormat('zh-TW', { style: 'currency', currency: 'TWD', maximumFractionDigits: 0 }).format(value || 0)

async function change(item, quantity) {
  if (quantity < 1 || quantity > item.availableStock || busyId.value) return
  error.value = ''
  busyId.value = item.id
  try { await cart.update(item.id, quantity) } catch (e) { error.value = e.message } finally { busyId.value = null }
}

async function executeConfirmed() {
  const action = confirmAction.value
  if (!action) return
  busyId.value = action.id || 'all'
  error.value = ''
  try {
    if (action.type === 'clear') await cart.clear()
    else await cart.remove(action.id)
    confirmAction.value = null
  } catch (e) { error.value = e.message } finally { busyId.value = null }
}

onMounted(async () => {
  try { await cart.load() } catch (e) { error.value = e.message }
})
</script>

<template>
  <main class="commerce-page container">
    <div class="page-heading"><div><span class="eyebrow">SHOPPING CART</span><h1>購物車</h1><p>商品價格與庫存會在建立訂單時再次確認。</p></div></div>
    <p v-if="error" class="alert alert-error">{{ error }}</p>
    <div v-if="cart.loading" class="loading-state">正在載入購物車…</div>
    <section v-else-if="!cart.items.length" class="empty-state"><h2>購物車目前是空的</h2><p>前往商品專區，挑選適合您的二手手機。</p><RouterLink class="button button-primary" to="/products">瀏覽商品</RouterLink></section>
    <div v-else class="cart-layout">
      <section class="cart-items card">
        <article v-for="item in cart.items" :key="item.id" class="cart-item">
          <img class="cart-item-image" :src="item.imageUrl || '/images/product-placeholder.svg'" :alt="item.productName">
          <div class="cart-item-main"><span class="muted">{{ item.brand }} {{ item.model }}</span><h2>{{ item.productName }}</h2><p>{{ item.storageCapacity || '容量未標示' }}</p><strong>{{ money(item.unitPrice) }}</strong></div>
          <div class="quantity-control" aria-label="商品數量">
            <button :disabled="busyId === item.id || item.quantity <= 1" @click="change(item, item.quantity - 1)">−</button>
            <span>{{ item.quantity }}</span>
            <button :disabled="busyId === item.id || item.quantity >= item.availableStock" @click="change(item, item.quantity + 1)">＋</button>
          </div>
          <div class="cart-item-total"><strong>{{ money(item.subtotal) }}</strong><button class="text-button danger-text" @click="confirmAction = { type: 'remove', id: item.id }">移除</button></div>
        </article>
        <button class="button button-ghost clear-cart" @click="confirmAction = { type: 'clear' }">清空購物車</button>
      </section>
      <aside class="order-summary card"><h2>訂單摘要</h2><div class="summary-line"><span>商品數量</span><strong>{{ cart.itemCount }} 件</strong></div><div class="summary-line total"><span>商品小計</span><strong>{{ money(cart.totalAmount) }}</strong></div><button class="button button-primary button-block" @click="router.push('/checkout')">前往結帳</button><RouterLink class="button button-ghost button-block" to="/products">繼續購物</RouterLink></aside>
    </div>
    <ConfirmModal :open="Boolean(confirmAction)" title="確認操作" :message="confirmAction?.type === 'clear' ? '確定要清空購物車嗎？' : '確定要移除此商品嗎？'" :busy="Boolean(busyId)" @cancel="confirmAction = null" @confirm="executeConfirmed" />
  </main>
</template>
