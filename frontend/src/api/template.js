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

export function fetchTemplates() {
  return request('/templates');
}

export function createTemplate(payload) {
  return request('/templates', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateTemplate(id, payload) {
  return request(`/templates/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export function deleteTemplate(id) {
  return request(`/templates/${id}`, {
    method: 'DELETE'
  });
}

export function searchTemplates(keyword) {
  return request(`/templates/search?keyword=${encodeURIComponent(keyword)}`);
}
