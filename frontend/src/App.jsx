import { Alert, Spin, message } from 'antd';
import { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { fetchDashboard } from './api/dashboard';
import { AuthProvider, useAuth } from './context/AuthContext';
import AppLayout from './layouts/AppLayout';
import LoginPage from './pages/LoginPage';
import ContactsPage from './pages/ContactsPage';
import DashboardPage from './pages/DashboardPage';
import GroupsPage from './pages/GroupsPage';
import TemplatesPage from './pages/TemplatesPage';
import SendRecordsPage from './pages/SendRecordsPage';
import BriefingEditorPage from './pages/BriefingEditorPage';
import BriefingDetailPage from './pages/BriefingDetailPage';
import BriefingListPage from './pages/BriefingListPage';
import SettingsPage from './pages/SettingsPage';

function ProtectedLayout() {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const { logout, displayName } = useAuth();

  const isActive = (path) => {
    const key = path.replace('/', '') || 'dashboard';
    return location.pathname.startsWith(path);
  };

  const getActiveKey = () => {
    const path = location.pathname;
    if (path.startsWith('/contacts')) return 'contacts';
    if (path.startsWith('/groups')) return 'groups';
    if (path.startsWith('/templates')) return 'templates';
    if (path.startsWith('/briefings')) return 'briefing';
    if (path.startsWith('/tasks')) return 'tasks';
    if (path.startsWith('/settings')) return 'settings';
    return 'dashboard';
  };

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
    if (location.pathname === '/') {
      loadDashboard();
    } else {
      setLoading(false);
    }
  }, [location.pathname]);

  const handleNavigate = (key) => {
    const routes = {
      dashboard: '/',
      contacts: '/contacts',
      groups: '/groups',
      templates: '/templates',
      briefing: '/briefings',
      tasks: '/tasks',
      settings: '/settings',
    };
    navigate(routes[key] || '/');
  };

  return (
    <AppLayout activeKey={getActiveKey()} username={displayName} onNavigate={handleNavigate} onLogout={logout}>
      {error ? <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} /> : null}
      <Routes>
        <Route path="/" element={loading ? <div className="loading-wrap"><Spin size="large" /></div> : <DashboardPage dashboard={dashboard} onTaskCreated={loadDashboard} />} />
        <Route path="/contacts" element={<ContactsPage />} />
        <Route path="/groups" element={<GroupsPage />} />
        <Route path="/templates" element={<TemplatesPage />} />
        <Route path="/briefings" element={<BriefingListPage />} />
        <Route path="/briefings/new" element={<BriefingEditorPage />} />
        <Route path="/briefings/:id" element={<BriefingDetailPage />} />
        <Route path="/tasks" element={<SendRecordsPage />} />
        <Route path="/settings" element={<SettingsPage />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AppLayout>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/*" element={<ProtectedLayout />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
