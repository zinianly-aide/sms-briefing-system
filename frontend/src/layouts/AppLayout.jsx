import { DashboardOutlined, LogoutOutlined, TeamOutlined, UsergroupAddOutlined, AppstoreOutlined, SendOutlined, ThunderboltOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Layout, Menu, Space, Typography } from 'antd';

const { Header, Sider, Content } = Layout;

const items = [
  { key: 'dashboard', icon: <DashboardOutlined />, label: '运营看板' },
  { key: 'contacts', icon: <TeamOutlined />, label: '联系人管理' },
  { key: 'groups', icon: <UsergroupAddOutlined />, label: '群组管理' },
  { key: 'templates', icon: <AppstoreOutlined />, label: '模板管理' },
  { key: 'briefing', icon: <SendOutlined />, label: '简讯录入' },
  { key: 'tasks', icon: <ThunderboltOutlined />, label: '发送任务' },
  { key: 'settings', icon: <SettingOutlined />, label: '系统设置' }
];

export default function AppLayout({ children, activeKey = 'dashboard', username, onNavigate, onLogout }) {
  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider width={220} theme="light" className="app-sider">
        <div className="brand-block">
          <Typography.Title level={4} style={{ margin: 0 }}>
            SMS Briefing
          </Typography.Title>
          <Typography.Text>短信简讯管理平台</Typography.Text>
        </div>
        <Menu mode="inline" selectedKeys={[activeKey]} items={items} onClick={({ key }) => onNavigate?.(key)} />
        <div className="sider-footer">
          <Space style={{ width: '100%', justifyContent: 'center' }}>
            <UserOutlined />
            <span>{username || '用户'}</span>
            <Button type="text" size="small" icon={<LogoutOutlined />} onClick={onLogout} style={{ color: '#ff4d4f', padding: 0 }}>退出</Button>
          </Space>
        </div>
      </Sider>
      <Layout>
        <Header className="app-header">
          <Typography.Text type="secondary">统一简讯、群组、任务编排</Typography.Text>
          <Typography.Title level={3} style={{ margin: 0 }}>
            短信简讯业务驾驶舱
          </Typography.Title>
        </Header>
        <Content className="app-content">{children}</Content>
      </Layout>
    </Layout>
  );
}
