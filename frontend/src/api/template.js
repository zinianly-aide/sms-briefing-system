import { request } from './client';

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
