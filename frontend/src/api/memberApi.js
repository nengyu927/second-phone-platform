import http from './http'

export async function getMembers(params = {}) {
  return (await http.get('/members', { params })).data
}

export async function getMemberById(id) {
  return (await http.get(`/members/${id}`)).data
}

export async function createMember(member) {
  return (await http.post('/members', { ...member, username: member.account })).data
}

export async function updateMember(id, member) {
  const payload = { ...member, username: member.account }
  if (!payload.password) delete payload.password
  return (await http.put(`/members/${id}`, payload)).data
}

export async function deleteMember(id) {
  await http.delete(`/members/${id}`)
}

export async function getAdminMembers(params = {}) { return (await http.get('/admin/members', { params })).data }
export async function getAdminMember(id) { return (await http.get(`/admin/members/${id}`)).data }
export async function updateMemberStatus(id, status) { return (await http.patch(`/admin/members/${id}/status`, { status })).data }
export async function updateMemberRole(id, role) { return (await http.patch(`/admin/members/${id}/role`, { role })).data }
