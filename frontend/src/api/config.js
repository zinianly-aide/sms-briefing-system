import { request } from './client';

export function fetchConfigs() {
  return request('/configs');
}

export function createConfig(payload) {
  return request('/configs', { method: 'POST', body: JSON.stringify(payload) });
}

export function updateConfig(id, payload) {
  return request(`/configs/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
}

export function deleteConfig(id) {
  return request(`/configs/${id}`, { method: 'DELETE' });
}
