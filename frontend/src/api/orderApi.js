import http from './http'
export async function checkout(payload){return (await http.post('/orders',payload)).data}
export async function getMyOrders(){return (await http.get('/orders')).data}
export async function getMyOrder(id){return (await http.get(`/orders/${id}`)).data}
export async function cancelMyOrder(id){return (await http.post(`/orders/${id}/cancel`)).data}
export async function getAdminOrders(params={}){return (await http.get('/admin/orders',{params})).data}
export async function getAdminOrder(id){return (await http.get(`/admin/orders/${id}`)).data}
export async function updateAdminOrderStatus(id,status){return (await http.patch(`/admin/orders/${id}/status`,{status})).data}
export async function updateAdminPaymentStatus(id,status){return (await http.patch(`/admin/orders/${id}/payment-status`,{status})).data}
export async function updateAdminShippingStatus(id,status){return (await http.patch(`/admin/orders/${id}/shipping-status`,{status})).data}
