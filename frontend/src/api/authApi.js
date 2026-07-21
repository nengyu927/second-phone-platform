import http from './http'
export const loginRequest = async (payload) => (await http.post('/auth/login', payload)).data
export const registerRequest = async (payload) => (await http.post('/auth/register', payload)).data
export const getCurrentUser = async () => (await http.get('/auth/me')).data
export const getProfile = async () => (await http.get('/member/profile')).data
export const updateProfile = async (payload) => (await http.put('/member/profile', payload)).data
