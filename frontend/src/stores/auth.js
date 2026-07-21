import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import { getCurrentUser, loginRequest, registerRequest } from '../api/authApi'

const TOKEN_KEY = 'auth_token'
const USER_KEY = 'auth_user'

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem(USER_KEY) || 'null')
  } catch {
    localStorage.removeItem(USER_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const currentUser = ref(readStoredUser())
  const isAuthenticated = computed(() => Boolean(token.value && currentUser.value))
  const isAdmin = computed(() => currentUser.value?.role === 'ADMIN')
  const isStaff = computed(() => ['STAFF', 'ADMIN'].includes(currentUser.value?.role))

  function persist() {
    if (token.value) localStorage.setItem(TOKEN_KEY, token.value)
    else localStorage.removeItem(TOKEN_KEY)
    if (currentUser.value) localStorage.setItem(USER_KEY, JSON.stringify(currentUser.value))
    else localStorage.removeItem(USER_KEY)
  }

  async function login(payload) {
    const data = await loginRequest(payload)
    token.value = data.token
    currentUser.value = data.user
    persist()
    return data.user
  }

  async function register(payload) {
    await registerRequest(payload)
    return login({ account: payload.username, password: payload.password })
  }

  async function fetchCurrentUser() {
    currentUser.value = await getCurrentUser()
    persist()
    return currentUser.value
  }

  async function restoreSession() {
    if (!token.value) {
      currentUser.value = null
      persist()
      return false
    }
    try {
      await fetchCurrentUser()
      return true
    } catch {
      logout()
      return false
    }
  }

  function setCurrentUser(user) {
    currentUser.value = user
    persist()
  }

  function logout() {
    token.value = ''
    currentUser.value = null
    persist()
  }

  window.addEventListener('auth:unauthorized', logout)

  return {
    token,
    currentUser,
    isAuthenticated,
    isAdmin,
    isStaff,
    login,
    register,
    fetchCurrentUser,
    restoreSession,
    setCurrentUser,
    logout
  }
})
