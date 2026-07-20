import http from './http'
export async function getRepairs(params={}){return (await http.get('/repairs',{params})).data}
export async function getRepairById(id){return (await http.get(`/repairs/${id}`)).data}
export async function createRepair(repair){return (await http.post('/repairs',repair)).data}
export async function updateRepair(id,repair){return (await http.put(`/repairs/${id}`,repair)).data}
export async function deleteRepair(id){await http.delete(`/repairs/${id}`)}
