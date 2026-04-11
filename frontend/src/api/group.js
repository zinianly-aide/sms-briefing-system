const API_BASE = 'http://localhost:8082/api';

function authHeaders() {
  const token = localStorage.getItem('sms_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...authHeaders(), ...(options.headers || {}) },
    ...options
  });

  const result = await response.json();
  if (!response.ok || result.success === false) {
    throw new Error(result.message || '请求失败');
  }
  return result.data;
}

export function fetchGroups() {
  return request('/groups');
}

export function createGroup(payload) {
  return request('/groups', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateGroup(id, payload) {
  return request(`/groups/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export function deleteGroup(id) {
  return request(`/groups/${id}`, {
    method: 'DELETE'
  });
}

export function searchGroups(keyword) {
  return request(`/groups/search?keyword=${encodeURIComponent(keyword)}`);
}

export function fetchGroupMembers(groupId) {
  return request(`/groups/${groupId}/members`);
}

export function addGroupMembers(groupId, contactIds) {
  return request(`/groups/${groupId}/members`, {
    method: 'POST',
    body: JSON.stringify({ contactIds })
  });
}

export function removeGroupMember(groupId, contactId) {
  return request(`/groups/${groupId}/members/${contactId}`, {
    method: 'DELETE'
  });
}
