const API_BASE = 'http://localhost:8080/api';

export async function fetchDashboard() {
  const response = await fetch(`${API_BASE}/dashboard`);
  if (!response.ok) {
    throw new Error('加载看板失败');
  }
  return response.json();
}

export async function createTask(payload) {
  const response = await fetch(`${API_BASE}/tasks`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload)
  });

  if (!response.ok) {
    throw new Error('创建发送任务失败');
  }

  return response.json();
}
