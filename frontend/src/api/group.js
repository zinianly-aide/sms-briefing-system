import { request } from './client';

export function fetchGroups() {
  return request('/groups');
}

export function createGroup(payload) {
  return request('/groups', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export function updateGroup(id, payload) {
  return request(`/groups/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload)
  });
}

export function deleteGroup(id) {
  return request(`/groups/${id}`, {
    method: 'DELETE'
  });
}

export function searchGroups(keyword) {
  return request(`/groups/search?keyword=${encodeURIComponent(keyword)}`);
}

export function fetchGroupMembers(groupId) {
  return request(`/groups/${groupId}/members`);
}

export function addGroupMembers(groupId, contactIds) {
  return request(`/groups/${groupId}/members`, {
    method: 'POST',
    body: JSON.stringify({ contactIds })
  });
}

export function removeGroupMember(groupId, contactId) {
  return request(`/groups/${groupId}/members/${contactId}`, {
    method: 'DELETE'
  });
}
