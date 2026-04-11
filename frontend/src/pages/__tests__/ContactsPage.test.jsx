import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import ContactsPage from '../ContactsPage';
import * as contactApi from '../../api/contact';

vi.mock('../../api/contact', () => ({
  fetchContacts: vi.fn(),
  searchContacts: vi.fn(),
  createContact: vi.fn(),
  updateContact: vi.fn(),
  deleteContact: vi.fn(),
  importContacts: vi.fn(),
  exportContacts: vi.fn(),
  syncHrData: vi.fn(),
  fetchEmployees: vi.fn(),
  fetchDepartments: vi.fn()
}));

vi.mock('../../api/group', () => ({
  fetchGroups: vi.fn(),
  fetchGroupMembers: vi.fn(),
  addGroupMembers: vi.fn(),
  removeGroupMember: vi.fn(),
  createGroup: vi.fn(),
  updateGroup: vi.fn(),
  deleteGroup: vi.fn(),
  searchGroups: vi.fn()
}));

describe('ContactsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders search input and buttons', async () => {
    contactApi.fetchContacts.mockResolvedValue([]);

    render(<ContactsPage />);

    await waitFor(() => {
      expect(screen.getByPlaceholderText('搜索联系人姓名/手机号')).toBeInTheDocument();
    });
    // "新建联系人" appears as button text
    expect(screen.getByRole('button', { name: /新建联系人/ })).toBeInTheDocument();
  });

  test('calls fetchContacts on mount', async () => {
    contactApi.fetchContacts.mockResolvedValue([]);

    render(<ContactsPage />);

    await waitFor(() => {
      expect(contactApi.fetchContacts).toHaveBeenCalledTimes(1);
    });
  });

  test('displays contact data', async () => {
    contactApi.fetchContacts.mockResolvedValue([
      { id: 1, name: '张三', mobile: '13800000000', department: '销售', title: '经理', status: 'active' }
    ]);

    render(<ContactsPage />);

    await waitFor(() => {
      expect(screen.getByText('张三')).toBeInTheDocument();
      expect(screen.getByText('13800000000')).toBeInTheDocument();
    });
  });

  test('shows modal on create button click', async () => {
    contactApi.fetchContacts.mockResolvedValue([]);
    const user = userEvent.setup();

    render(<ContactsPage />);

    const createBtn = screen.getByRole('button', { name: /新建联系人/ });
    await user.click(createBtn);

    await waitFor(() => {
      // Modal title is rendered, use getAllByText since button also contains same text
      expect(screen.getAllByText('新建联系人').length).toBeGreaterThanOrEqual(2);
    });
  });
});
