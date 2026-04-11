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

export function fetchDashboard() {
  return request('/dashboard');
}

export function createTask(payload) {
  return request('/tasks', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}
