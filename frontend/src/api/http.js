import axios from 'axios'

const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000
})

http.interceptors.response.use(
  (response) => response,
  (error) => {
    if (!error.response) {
      return Promise.reject(new Error('無法連線後端，請確認 Spring Boot 是否啟動'))
    }

    const data = error.response.data
    const message = data?.detail || data?.message || data?.error
      || `API 請求失敗（HTTP ${error.response.status}）`
    return Promise.reject(new Error(message))
  }
)

export default http
