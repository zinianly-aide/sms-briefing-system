import { Alert, Spin, message } from 'antd';
import { useEffect, useState } from 'react';
import { fetchDashboard } from './api/dashboard';
import AppLayout from './layouts/AppLayout';
import ContactsPage from './pages/ContactsPage';
import DashboardPage from './pages/DashboardPage';
import GroupsPage from './pages/GroupsPage';

export default function App() {
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState('');
  const [activePage, setActivePage] = useState('contacts');

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
    if (activePage === 'dashboard') {
      loadDashboard();
    } else {
      setLoading(false);
    }
  }, [activePage]);

  const renderPage = () => {
    if (activePage === 'contacts') {
      return <ContactsPage />;
    }
    if (activePage === 'dashboard') {
      return loading ? <div className="loading-wrap"><Spin size="large" /></div> : <DashboardPage dashboard={dashboard} onTaskCreated={loadDashboard} />;
    }
    if (activePage === 'groups') {
      return <GroupsPage />;
    }
    return <ContactsPage />;
  };

  return (
    <AppLayout activeKey={activePage} onNavigate={setActivePage}>
      {error ? <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} /> : null}
      {renderPage()}
    </AppLayout>
  );
}
