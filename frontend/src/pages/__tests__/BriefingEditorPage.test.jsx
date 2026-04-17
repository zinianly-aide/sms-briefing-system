import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import BriefingEditorPage from '../BriefingEditorPage';
import * as groupApi from '../../api/group';
import * as templateApi from '../../api/template';

vi.mock('react-router-dom', () => ({
  useNavigate: () => vi.fn(),
}));

vi.mock('../../context/AuthContext', () => ({
  useAuth: () => ({
    user: { username: 'testuser', displayName: '测试用户', role: 'admin' },
    authenticated: true,
    displayName: '测试用户',
    login: vi.fn(),
    logout: vi.fn(),
  }),
  AuthProvider: ({ children }) => children,
}));

vi.mock('../../api/briefing', () => ({
  createBriefing: vi.fn(),
}));

vi.mock('../../api/group', () => ({
  fetchGroups: vi.fn(),
  fetchGroupMembers: vi.fn(),
}));

vi.mock('../../api/template', () => ({
  fetchTemplates: vi.fn(),
}));

describe('BriefingEditorPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    groupApi.fetchGroupMembers.mockResolvedValue([]);
  });

  test('renders template and group options when APIs return paged results', async () => {
    groupApi.fetchGroups.mockResolvedValue({
      list: [{ id: 11, name: '技术团队群' }],
      total: 1,
      page: 1,
      pageSize: 1000,
    });
    templateApi.fetchTemplates.mockResolvedValue({
      list: [{ id: 7, name: '暴雨预警模板', content: '请注意安全' }],
      total: 1,
      page: 1,
      pageSize: 1000,
    });

    render(<BriefingEditorPage />);

    await waitFor(() => {
      expect(groupApi.fetchGroups).toHaveBeenCalledTimes(1);
      expect(templateApi.fetchTemplates).toHaveBeenCalledTimes(1);
    });

    const templateSelect = screen.getByLabelText('选择模板');
    fireEvent.mouseDown(templateSelect);
    expect(await screen.findByText('暴雨预警模板')).toBeInTheDocument();

    const groupSelect = screen.getByLabelText('目标群组');
    fireEvent.mouseDown(groupSelect);
    expect(await screen.findByText('技术团队群')).toBeInTheDocument();
  });
});
