import http from './http'
export async function getCart(){return (await http.get('/cart')).data}
export async function addCartItem(payload){return (await http.post('/cart/items',payload)).data}
export async function updateCartItem(itemId,quantity){return (await http.put(`/cart/items/${itemId}`,{quantity})).data}
export async function removeCartItem(itemId){return (await http.delete(`/cart/items/${itemId}`)).data}
export async function clearCart(){await http.delete('/cart')}
