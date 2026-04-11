import { BellOutlined, FileTextOutlined, SafetyCertificateOutlined, SendOutlined } from '@ant-design/icons';
import { Card, Statistic } from 'antd';

const iconMap = {
  '联系人总量': <TeamOutlined />,
  '启用群组': <UsergroupAddOutlined />,
  '模板数量': <FileTextOutlined />,
  '待发送任务': <SendOutlined />
};

const colorMap = {
  '联系人总量': { bg: 'linear-gradient(135deg, #e6f4ff 0%, #f0f5ff 100%)', color: '#1677ff' },
  '启用群组': { bg: 'linear-gradient(135deg, #f6ffed 0%, #fcffe6 100%)', color: '#52c41a' },
  '模板数量': { bg: 'linear-gradient(135deg, #fff7e6 0%, #fffbe6 100%)', color: '#fa8c16' },
  '待发送任务': { bg: 'linear-gradient(135deg, #f9f0ff 0%, #fff0f6 100%)', color: '#722ed1' }
};

import { TeamOutlined, UsergroupAddOutlined } from '@ant-design/icons';

export default function StatCard({ title, value, suffix, extra }) {
  const theme = colorMap[title] || { bg: '#f5f7fb', color: '#1677ff' };

  return (
    <Card className="stat-card" bordered={false} style={{ background: theme.bg }}>
      <Statistic
        title={title}
        value={value}
        suffix={suffix}
        prefix={<span style={{ color: theme.color, fontSize: 22, marginRight: 4 }}>{getIcon(title)}</span>}
        valueStyle={{ color: theme.color, fontWeight: 700 }}
      />
      {extra ? (
        <div style={{ fontSize: 12, color: '#8c8c8c', marginTop: 4 }}>{extra}</div>
      ) : null}
    </Card>
  );
}

function getIcon(title) {
  if (title?.includes('联系人') || title?.includes('Contact')) return <TeamOutlined />;
  if (title?.includes('群组') || title?.includes('Group')) return <UsergroupAddOutlined />;
  if (title?.includes('模板') || title?.includes('Template')) return <FileTextOutlined />;
  if (title?.includes('任务') || title?.includes('Task')) return <SendOutlined />;
  return <BellOutlined />;
}
