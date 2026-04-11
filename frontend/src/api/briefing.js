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

export function fetchBriefings() {
  return request('/briefings');
}

export function fetchBriefing(id) {
  return request(`/briefings/${id}`);
}

export function createBriefing(payload) {
  return request('/briefings', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateBriefing(id, payload) {
  return request(`/briefings/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export function deleteBriefing(id) {
  return request(`/briefings/${id}`, {
    method: 'DELETE'
  });
}

export function searchBriefings(keyword) {
  return request(`/briefings/search?keyword=${encodeURIComponent(keyword)}`);
}

export function cloneBriefing(id) {
  return request(`/briefings/${id}/clone`, { method: 'POST' });
}
