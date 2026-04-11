import { fetchContacts, createContact, updateContact, deleteContact, searchContacts } from '../contact';

const API_BASE = '/api';

function mockFetch(data, ok = true) {
  global.fetch = vi.fn(() =>
    Promise.resolve({
      ok,
      json: () => Promise.resolve(data)
    })
  );
}

describe('contact API', () => {
  beforeEach(() => {
    mockFetch({ success: true, data: [{ id: 1, name: '张三' }] });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  test('fetchContacts calls correct URL', async () => {
    const data = await fetchContacts();
    expect(fetch).toHaveBeenCalledWith(`${API_BASE}/contacts`, expect.objectContaining({ headers: expect.any(Object) }));
    expect(data).toEqual([{ id: 1, name: '张三' }]);
  });

  test('createContact sends POST with body', async () => {
    const payload = { name: '李四', mobile: '13900000000' };
    await createContact(payload);
    expect(fetch).toHaveBeenCalledWith(`${API_BASE}/contacts`, expect.objectContaining({
      method: 'POST',
      body: JSON.stringify(payload)
    }));
  });

  test('updateContact sends PUT with id', async () => {
    const payload = { name: '张三改' };
    await updateContact(1, payload);
    expect(fetch).toHaveBeenCalledWith(`${API_BASE}/contacts/1`, expect.objectContaining({
      method: 'PUT',
      body: JSON.stringify(payload)
    }));
  });

  test('deleteContact sends DELETE', async () => {
    await deleteContact(1);
    expect(fetch).toHaveBeenCalledWith(`${API_BASE}/contacts/1`, expect.objectContaining({
      method: 'DELETE'
    }));
  });

  test('searchContacts sends keyword param', async () => {
    await searchContacts('张');
    expect(fetch).toHaveBeenCalledWith(expect.stringContaining('keyword='), expect.any(Object));
  });

  test('throws on error response', async () => {
    mockFetch({ success: false, message: '服务异常' }, true);
    await expect(fetchContacts()).rejects.toThrow('服务异常');
  });

  test('throws on non-ok HTTP status', async () => {
    mockFetch({ success: true, data: [] }, false);
    await expect(fetchContacts()).rejects.toThrow('请求失败');
  });
});
