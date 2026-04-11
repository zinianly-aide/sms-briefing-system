import { render, screen } from '@testing-library/react';
import DashboardPage from '../DashboardPage';

describe('DashboardPage', () => {
  const dashboard = {
    totalContacts: 10,
    activeGroups: 3,
    templateCount: 5,
    totalTasks: 8,
    pendingTasks: 2,
    groups: [{ id: 1, name: '销售群', ownerDept: '销售部', memberCount: 10, tags: ['销售'], lastSyncTime: '2026-04-10' }],
    templates: [{ id: 1, name: '预警模板', category: '预警', content: '请注意安全', status: '启用中', owner: '运营' }],
    tasks: [{ id: 1, title: '暴雨提醒', channel: '短信', status: '待发送', recipientCount: 10, creator: '张三' }]
  };

  test('renders stat cards and overview', () => {
    render(<DashboardPage dashboard={dashboard} onTaskCreated={() => {}} />);
    // These titles appear in both StatCard and overview section
    expect(screen.getAllByText('联系人总量').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('启用群组').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('模板数量').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('待发送任务').length).toBeGreaterThanOrEqual(1);
  });

  test('renders stat values', () => {
    render(<DashboardPage dashboard={dashboard} onTaskCreated={() => {}} />);
    // 10 appears in stat card + overview
    expect(screen.getAllByText('10').length).toBeGreaterThanOrEqual(2);
    // 3 appears in stat card + overview
    expect(screen.getAllByText('3').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('5').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('2').length).toBeGreaterThanOrEqual(1);
  });

  test('renders quick task form', () => {
    render(<DashboardPage dashboard={dashboard} onTaskCreated={() => {}} />);
    expect(screen.getByText('快速创建简讯任务')).toBeInTheDocument();
    expect(screen.getByText('创建任务')).toBeInTheDocument();
  });

  test('renders data tables', () => {
    render(<DashboardPage dashboard={dashboard} onTaskCreated={() => {}} />);
    expect(screen.getByText('销售群')).toBeInTheDocument();
    expect(screen.getByText('预警模板')).toBeInTheDocument();
    expect(screen.getByText('暴雨提醒')).toBeInTheDocument();
  });

  test('shows empty state when no dashboard data', () => {
    render(<DashboardPage dashboard={null} onTaskCreated={() => {}} />);
    expect(screen.getByText('暂无数据')).toBeInTheDocument();
  });

  test('shows empty state for tables when data is empty', () => {
    const emptyDashboard = {
      totalContacts: 0,
      activeGroups: 0,
      templateCount: 0,
      totalTasks: 0,
      pendingTasks: 0,
      groups: [],
      templates: [],
      tasks: []
    };
    render(<DashboardPage dashboard={emptyDashboard} onTaskCreated={() => {}} />);
    // Three empty tables with "暂无" description
    const emptyTexts = screen.getAllByText(/暂无/);
    expect(emptyTexts.length).toBeGreaterThanOrEqual(3);
  });
});
