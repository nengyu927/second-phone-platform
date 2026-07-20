import http from './http'
export async function getDashboard(){return (await http.get('/dashboard')).data}
export async function getDashboardSummary(){return (await http.get('/dashboard/summary')).data}
export async function getDashboardRecent(){return (await http.get('/dashboard/recent')).data}
