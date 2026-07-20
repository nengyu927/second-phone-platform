import http from './http'
export async function getOrders(params={}){return (await http.get('/orders',{params})).data}
export async function getOrderById(id){return (await http.get(`/orders/${id}`)).data}
export async function createOrder(order){return (await http.post('/orders',order)).data}
export async function updateOrder(id,order){return (await http.put(`/orders/${id}`,order)).data}
export async function deleteOrder(id){await http.delete(`/orders/${id}`)}
