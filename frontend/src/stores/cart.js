import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { addCartItem, clearCart, getCart, removeCartItem, updateCartItem } from '../api/cartApi'

export const useCartStore=defineStore('cart',()=>{
  const cart=ref({id:null,items:[],itemCount:0,totalAmount:0}),loading=ref(false)
  const items=computed(()=>cart.value.items||[]),itemCount=computed(()=>cart.value.itemCount||0),totalAmount=computed(()=>cart.value.totalAmount||0)
  function setCart(value){cart.value=value||{id:null,items:[],itemCount:0,totalAmount:0}}
  async function load(){loading.value=true;try{setCart(await getCart());return cart.value}finally{loading.value=false}}
  async function add(productId,quantity=1){setCart(await addCartItem({productId,quantity}));return cart.value}
  async function update(itemId,quantity){setCart(await updateCartItem(itemId,quantity))}
  async function remove(itemId){setCart(await removeCartItem(itemId))}
  async function clear(){await clearCart();setCart(null)}
  function reset(){setCart(null)}
  window.addEventListener('auth:unauthorized',reset)
  return{cart,items,itemCount,totalAmount,loading,load,add,update,remove,clear,reset}
})
