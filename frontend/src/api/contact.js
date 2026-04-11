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
    if (response.status === 401) {
      localStorage.removeItem('sms_token');
      window.location.reload();
    }
    throw new Error(result.message || '请求失败');
  }
  return result.data;
}

export function fetchContacts() {
  return request('/contacts');
}

export function createContact(payload) {
  return request('/contacts', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateContact(id, payload) {
  return request(`/contacts/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export function deleteContact(id) {
  return request(`/contacts/${id}`, {
    method: 'DELETE'
  });
}

export function searchContacts(keyword) {
  return request(`/contacts/search?keyword=${encodeURIComponent(keyword)}`);
}

export function importContacts(file) {
  const formData = new FormData();
  formData.append('file', file);
  return fetch(`${API_BASE}/contacts/import`, {
    method: 'POST',
    body: formData
  }).then(async (res) => {
    const result = await res.json();
    if (!res.ok || result.success === false) throw new Error(result.message || '导入失败');
    return result.data;
  });
}

export function exportContacts() {
  window.open(`${API_BASE}/contacts/export`, '_blank');
}

export function fetchEmployees(params = {}) {
  const qs = new URLSearchParams(params).toString();
  return request(`/selector/employees?${qs}`);
}

export function fetchDepartments() {
  return request('/selector/departments');
}

export function syncHrData() {
  return request('/hr/sync', { method: 'POST' });
}
