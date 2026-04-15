import { request } from './client';

export function fetchDashboard() {
  return request('/dashboard');
}

export function createTask(payload) {
  return request('/tasks', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}
