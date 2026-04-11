import { render, screen, waitFor } from '@testing-library/react';
import BriefingListPage from '../BriefingListPage';
import * as briefingApi from '../../api/briefing';

vi.mock('react-router-dom', () => ({
  useNavigate: () => vi.fn(),
  useParams: () => ({}),
  useLocation: () => ({ pathname: '/briefings' }),
}));

vi.mock('../../api/briefing', () => ({
  fetchBriefings: vi.fn(),
  searchBriefings: vi.fn(),
  deleteBriefing: vi.fn(),
  cloneBriefing: vi.fn(),
}));

describe('BriefingListPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('renders page with buttons', async () => {
    briefingApi.fetchBriefings.mockResolvedValue([
      { id: 1, title: '测试简讯', status: '草稿', channel: '短信', author: '张三', version: 'V1.0', audience: '1,2' }
    ]);

    render(<BriefingListPage />);

    await waitFor(() => {
      expect(screen.getByText('测试简讯')).toBeInTheDocument();
    });

    expect(screen.getByRole('button', { name: /新建简讯/ })).toBeInTheDocument();
  });

  test('shows empty state when no data', async () => {
    briefingApi.fetchBriefings.mockResolvedValue([]);

    render(<BriefingListPage />);

    await waitFor(() => {
      expect(screen.getByRole('button', { name: /新建简讯/ })).toBeInTheDocument();
    });
  });

  test('calls fetchBriefings on mount', async () => {
    briefingApi.fetchBriefings.mockResolvedValue([]);

    render(<BriefingListPage />);

    await waitFor(() => {
      expect(briefingApi.fetchBriefings).toHaveBeenCalledTimes(1);
    });
  });

  test('renders status tags with correct colors', async () => {
    briefingApi.fetchBriefings.mockResolvedValue([
      { id: 1, title: '简讯1', status: '待审核', channel: '短信', author: 'A', version: 'V1.0', audience: '' },
      { id: 2, title: '简讯2', status: '待发送', channel: '短信', author: 'B', version: 'V1.0', audience: '' },
      { id: 3, title: '简讯3', status: '已发送', channel: '短信', author: 'C', version: 'V1.0', audience: '' },
      { id: 4, title: '简讯4', status: '草稿', channel: '短信', author: 'D', version: 'V1.0', audience: '' }
    ]);

    render(<BriefingListPage />);

    await waitFor(() => {
      expect(screen.getByText('待审核')).toBeInTheDocument();
      expect(screen.getByText('待发送')).toBeInTheDocument();
      expect(screen.getByText('已发送')).toBeInTheDocument();
      expect(screen.getByText('草稿')).toBeInTheDocument();
    });
  });
});
