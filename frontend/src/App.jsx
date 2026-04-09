import { Alert, Spin, message } from 'antd';
import { useEffect, useState } from 'react';
import { fetchDashboard } from './api/dashboard';
import AppLayout from './layouts/AppLayout';
import DashboardPage from './pages/DashboardPage';

export default function App() {
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState('');

  const loadDashboard = async () => {
    try {
      setLoading(true);
      const data = await fetchDashboard();
      setDashboard(data);
      setError('');
    } catch (err) {
      setError(err.message || '加载失败');
      message.error(err.message || '加载失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadDashboard();
  }, []);

  return (
    <AppLayout>
      {error ? <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} /> : null}
      {loading ? <div className="loading-wrap"><Spin size="large" /></div> : <DashboardPage dashboard={dashboard} onTaskCreated={loadDashboard} />}
    </AppLayout>
  );
}
