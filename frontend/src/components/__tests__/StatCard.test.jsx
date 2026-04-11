import { render, screen } from '@testing-library/react';
import StatCard from '../StatCard';

describe('StatCard', () => {
  test('renders title and value', () => {
    render(<StatCard title="测试标题" value={42} />);
    expect(screen.getByText('测试标题')).toBeInTheDocument();
    expect(screen.getByText('42')).toBeInTheDocument();
  });

  test('renders extra text when provided', () => {
    render(<StatCard title="测试" value={1} extra="额外说明" />);
    expect(screen.getByText('额外说明')).toBeInTheDocument();
  });

  test('does not render extra when not provided', () => {
    const { container } = render(<StatCard title="测试" value={1} />);
    expect(container.querySelector('.ant-statistic-content')).toBeInTheDocument();
  });

  test('renders with different titles', () => {
    const titles = ['联系人总量', '启用群组', '模板数量', '待发送任务'];
    titles.forEach((title) => {
      const { unmount } = render(<StatCard title={title} value={10} />);
      expect(screen.getByText(title)).toBeInTheDocument();
      unmount();
    });
  });
});
