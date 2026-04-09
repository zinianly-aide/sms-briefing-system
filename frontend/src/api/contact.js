const API_BASE = 'http://localhost:8080/api';

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
    ...options
  });

  const result = await response.json();
  if (!response.ok || result.success === false) {
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
