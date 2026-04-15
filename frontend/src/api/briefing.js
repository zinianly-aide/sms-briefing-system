import { request } from './client';

export function fetchBriefings() {
  return request('/briefings?page=1&pageSize=1000', {}, []);
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
  return request(`/briefings/search?keyword=${encodeURIComponent(keyword)}&page=1&pageSize=1000`, {}, []);
}

export function cloneBriefing(id) {
  return request(`/briefings/${id}/clone`, { method: 'POST' });
}
