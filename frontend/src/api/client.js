const API_BASE = import.meta.env.VITE_API_BASE || '/api';

export function authHeaders() {
  const token = localStorage.getItem('sms_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
      ...(options.headers || {}),
    },
    ...options,
  });

  const result = await response.json();
  if (!response.ok || result.success === false) {
    if (response.status === 401) {
      localStorage.removeItem('sms_token');
      localStorage.removeItem('sms_user');
      window.location.reload();
    }
    throw new Error(result.message || '请求失败');
  }
  return result.data;
}

export { API_BASE };
