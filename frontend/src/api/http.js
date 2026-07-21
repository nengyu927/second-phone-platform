import axios from 'axios'

const statusMessages = {
  400: '輸入資料不正確，請檢查後再試。',
  401: '登入已失效，請重新登入。',
  403: '您的帳號沒有執行此操作的權限。',
  404: '找不到指定的資料。',
  409: '資料狀態衝突，請重新整理後再試。',
  500: '伺服器暫時無法處理要求，請稍後再試。'
}
const http = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || '/api', timeout: Number(import.meta.env.VITE_API_TIMEOUT || 10000) })
http.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
http.interceptors.response.use((response) => response, (error) => {
  if (!error.response) return Promise.reject(new Error('目前無法連線到伺服器，請確認網路與後端服務。'))
  if (error.response.status === 401) window.dispatchEvent(new CustomEvent('auth:unauthorized'))
  if (error.response.status === 403) window.dispatchEvent(new CustomEvent('auth:forbidden'))
  const data = error.response.data
  const fields = data?.fieldErrors ? Object.values(data.fieldErrors).join('、') : ''
  const normalized = new Error(fields || data?.message || statusMessages[error.response.status] || `API 發生錯誤（HTTP ${error.response.status}）`)
  normalized.status = error.response.status
  normalized.response = error.response
  return Promise.reject(normalized)
})
export default http
