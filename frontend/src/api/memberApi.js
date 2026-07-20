import http from './http'

export async function getMembers() {
  return (await http.get('/members')).data
}

export async function getMemberById(id) {
  return (await http.get(`/members/${id}`)).data
}

export async function createMember(member) {
  return (await http.post('/members', member)).data
}

export async function updateMember(id, member) {
  return (await http.put(`/members/${id}`, member)).data
}

export async function deleteMember(id) {
  await http.delete(`/members/${id}`)
}
