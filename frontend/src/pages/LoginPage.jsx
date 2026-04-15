import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Tabs, Typography, message } from 'antd';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { login, register } from '../api/auth';

const { Title, Text } = Typography;

export default function LoginPage() {
  const [loading, setLoading] = useState(false);
  const { login: setAuth, authenticated } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (values) => {
    try {
      setLoading(true);
      const data = await login(values.username, values.password);
      setAuth(data);
      message.success('登录成功');
      navigate('/', { replace: true });
    } catch (err) {
      message.error(err.message || '登录失败');
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (values) => {
    if (values.password !== values.confirm) {
      message.error('两次密码输入不一致');
      return;
    }
    try {
      setLoading(true);
      await register(values.username, values.password, values.displayName);
      message.success('注册成功，请登录');
      document.querySelector('[data-tabkey="login"]')?.click();
    } catch (err) {
      message.error(err.message || '注册失败');
    } finally {
      setLoading(false);
    }
  };

  if (authenticated) {
    return null;
  }

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh', background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
      <Card style={{ width: 400, boxShadow: '0 8px 24px rgba(0,0,0,0.15)' }} bordered={false}>
        <div style={{ textAlign: 'center', marginBottom: 24 }}>
          <Title level={3} style={{ margin: 0 }}>SMS Briefing</Title>
          <Text type="secondary">短信简讯管理平台</Text>
        </div>
        <Tabs
          centered
          items={[
            {
              key: 'login',
              label: '登录',
              children: (
                <Form onFinish={handleLogin} size="large">
                  <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
                    <Input prefix={<UserOutlined />} placeholder="用户名" />
                  </Form.Item>
                  <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="密码" />
                  </Form.Item>
                  <Form.Item>
                    <Button type="primary" htmlType="submit" loading={loading} block>登录</Button>
                  </Form.Item>
                  <Text type="secondary" style={{ fontSize: 12 }}>默认账号: admin / admin123</Text>
                </Form>
              )
            },
            {
              key: 'register',
              label: '注册',
              children: (
                <Form onFinish={handleRegister} size="large">
                  <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }, { min: 3, max: 32, message: '用户名长度需为3-32位' }]}>
                    <Input prefix={<UserOutlined />} placeholder="用户名" />
                  </Form.Item>
                  <Form.Item name="displayName">
                    <Input placeholder="显示名称（可选）" />
                  </Form.Item>
                  <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }, { min: 8, message: '密码至少8位' }]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="密码（至少8位，需含字母和数字）" />
                  </Form.Item>
                  <Form.Item name="confirm" rules={[{ required: true, message: '请确认密码' }]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="确认密码" />
                  </Form.Item>
                  <Form.Item>
                    <Button type="primary" htmlType="submit" loading={loading} block>注册</Button>
                  </Form.Item>
                </Form>
              )
            }
          ]}
        />
      </Card>
    </div>
  );
}
