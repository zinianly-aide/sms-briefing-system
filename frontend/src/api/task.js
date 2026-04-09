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

export function fetchTasks() {
  return request('/tasks');
}

export function createTask(payload) {
  return request('/tasks', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateTask(id, payload) {
  return request(`/tasks/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export function deleteTask(id) {
  return request(`/tasks/${id}`, {
    method: 'DELETE'
  });
}

export function searchTasks(keyword) {
  return request(`/tasks/search?keyword=${encodeURIComponent(keyword)}`);
}
