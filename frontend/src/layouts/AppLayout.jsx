import { BellOutlined, DashboardOutlined, FileTextOutlined, TeamOutlined } from '@ant-design/icons';
import { Layout, Menu, Space, Typography } from 'antd';

const { Header, Sider, Content } = Layout;

const items = [
  { key: 'dashboard', icon: <DashboardOutlined />, label: '运营看板' },
  { key: 'contacts', icon: <TeamOutlined />, label: '通讯录与群组' },
  { key: 'briefing', icon: <FileTextOutlined />, label: '简讯录入' },
  { key: 'tasks', icon: <BellOutlined />, label: '发送任务' }
];

export default function AppLayout({ children, activeKey = 'dashboard' }) {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider width={240} theme="light" className="app-sider">
        <div className="brand-block">
          <Typography.Title level={4} style={{ margin: 0 }}>
            SMS Briefing
          </Typography.Title>
          <Typography.Text type="secondary">短信简讯管理平台</Typography.Text>
        </div>
        <Menu mode="inline" selectedKeys={[activeKey]} items={items} />
      </Sider>
      <Layout>
        <Header className="app-header">
          <Space direction="vertical" size={0}>
            <Typography.Text type="secondary">统一简讯、群组、任务编排</Typography.Text>
            <Typography.Title level={3} style={{ margin: 0 }}>
              短信简讯业务驾驶舱
            </Typography.Title>
          </Space>
        </Header>
        <Content className="app-content">{children}</Content>
      </Layout>
    </Layout>
  );
}
