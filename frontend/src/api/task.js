import { request } from './client';

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

export function executeTask(id) {
  return request(`/tasks/${id}/execute`, { method: 'POST' });
}

export function cancelTask(id, reason = '') {
  return request(`/tasks/${id}/cancel`, {
    method: 'POST',
    body: JSON.stringify({ reason })
  });
}

export function fetchTaskRecipients(id) {
  return request(`/tasks/${id}/recipients`);
}
