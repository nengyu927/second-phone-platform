import http from './http'
export async function submitTradeIn(payload){return (await http.post('/member/trade-ins',payload)).data}
export async function getMyTradeIns(){return (await http.get('/member/trade-ins')).data}
export async function getMyTradeIn(id){return (await http.get(`/member/trade-ins/${id}`)).data}
export async function respondTradeIn(id,accept){return (await http.post(`/member/trade-ins/${id}/${accept?'accept':'reject'}`)).data}
export async function getAdminTradeIns(){return (await http.get('/admin/trade-ins')).data}
export async function getAdminTradeIn(id){return (await http.get(`/admin/trade-ins/${id}`)).data}
export async function updateAdminTradeIn(id,payload){return (await http.patch(`/admin/trade-ins/${id}`,payload)).data}
