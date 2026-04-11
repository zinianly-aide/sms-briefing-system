import { API_BASE } from './client';

export function login(username, password) {
  return fetch(`${API_BASE}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  }).then(async (res) => {
    const result = await res.json();
    if (!res.ok || result.success === false) throw new Error(result.message || '登录失败');
    return result.data;
  });
}

export function register(username, password, displayName) {
  return fetch(`${API_BASE}/auth/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password, displayName })
  }).then(async (res) => {
    const result = await res.json();
    if (!res.ok || result.success === false) throw new Error(result.message || '注册失败');
    return result.data;
  });
}

export function getToken() {
  return localStorage.getItem('sms_token');
}

export function setToken(token) {
  localStorage.setItem('sms_token', token);
}

export function setUserInfo(info) {
  localStorage.setItem('sms_user', JSON.stringify(info));
}

export function getUserInfo() {
  try {
    return JSON.parse(localStorage.getItem('sms_user'));
  } catch {
    return null;
  }
}

export function clearAuth() {
  localStorage.removeItem('sms_token');
  localStorage.removeItem('sms_user');
}

export function isLoggedIn() {
  return !!getToken();
}
