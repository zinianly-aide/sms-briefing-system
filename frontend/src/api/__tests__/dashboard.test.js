import { fetchDashboard, createTask } from '../dashboard';

const API_BASE = '/api';

function mockFetch(data, ok = true) {
  global.fetch = vi.fn(() =>
    Promise.resolve({
      ok,
      json: () => Promise.resolve(data)
    })
  );
}

describe('dashboard API', () => {
  beforeEach(() => {
    mockFetch({ success: true, data: { totalContacts: 5, activeGroups: 2 } });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  test('fetchDashboard calls /api/dashboard', async () => {
    const data = await fetchDashboard();
    expect(fetch).toHaveBeenCalledWith(`${API_BASE}/dashboard`, expect.any(Object));
    expect(data.totalContacts).toBe(5);
  });

  test('createTask sends POST to /api/tasks', async () => {
    const payload = { title: '测试任务', channel: 'sms' };
    await createTask(payload);
    expect(fetch).toHaveBeenCalledWith(`${API_BASE}/tasks`, expect.objectContaining({
      method: 'POST',
      body: JSON.stringify(payload)
    }));
  });

  test('throws on error response', async () => {
    mockFetch({ success: false, message: '加载失败' }, true);
    await expect(fetchDashboard()).rejects.toThrow('加载失败');
  });
});
