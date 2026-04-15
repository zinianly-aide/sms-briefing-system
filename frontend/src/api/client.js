const API_BASE = import.meta.env.VITE_API_BASE || '/api';

export function authHeaders() {
  const token = localStorage.getItem('sms_token');
  return token ? { Authorization: `Bearer ${token}` } : {};
}

function isPlainObject(value) {
  return value !== null && typeof value === 'object' && !Array.isArray(value);
}

export function normalizeResult(result, fallback = null) {
  if (result == null) return fallback;
  if (Array.isArray(result)) return result;
  if (!isPlainObject(result)) return result;

  if ('data' in result) {
    return result.data ?? fallback;
  }

  if (Array.isArray(result.list)) {
    return result;
  }

  return result;
}

export async function request(path, options = {}, fallback = null) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...authHeaders(),
      ...(options.headers || {}),
    },
    ...options,
  });

  const contentType = response.headers?.get?.('content-type') || 'application/json';
  const result = contentType.includes('application/json') ? await response.json() : null;
  if (!response.ok || result?.success === false) {
    if (response.status === 401) {
      localStorage.removeItem('sms_token');
      localStorage.removeItem('sms_user');
      window.location.reload();
    }
    throw new Error(result?.message || '请求失败');
  }
  return normalizeResult(result, fallback);
}

export { API_BASE };
