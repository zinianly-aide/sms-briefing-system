import { request, API_BASE, normalizeResult } from './client';

export function fetchContacts() {
  return request('/contacts?page=1&pageSize=1000', {}, []);
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
  return request(`/contacts/search?keyword=${encodeURIComponent(keyword)}&page=1&pageSize=1000`);
}

export function importContacts(file) {
  const formData = new FormData();
  formData.append('file', file);
  return fetch(`${API_BASE}/contacts/import`, {
    method: 'POST',
    body: formData
  }).then(async (res) => {
    const result = await res.json();
    if (!res.ok || result?.success === false) throw new Error(result?.message || '导入失败');
    return normalizeResult(result, { success: 0, fail: 0, errors: [] });
  });
}

export function exportContacts() {
  window.open(`${API_BASE}/contacts/export`, '_blank');
}

export function fetchEmployees(params = {}) {
  const qs = new URLSearchParams(params).toString();
  return request(`/selector/employees?${qs}`, {}, { list: [], total: 0 });
}

export function fetchDepartments() {
  return request('/selector/departments', {}, []);
}

export function syncHrData() {
  return request('/hr/sync', { method: 'POST' });
}
