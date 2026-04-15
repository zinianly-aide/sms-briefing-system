import { Alert, Spin, message } from 'antd';
import { Suspense, lazy, useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { fetchDashboard } from './api/dashboard';
import { AuthProvider, useAuth } from './context/AuthContext';
import AppLayout from './layouts/AppLayout';

const LoginPage = lazy(() => import('./pages/LoginPage'));
const ContactsPage = lazy(() => import('./pages/ContactsPage'));
const DashboardPage = lazy(() => import('./pages/DashboardPage'));
const GroupsPage = lazy(() => import('./pages/GroupsPage'));
const TemplatesPage = lazy(() => import('./pages/TemplatesPage'));
const SendRecordsPage = lazy(() => import('./pages/SendRecordsPage'));
const BriefingEditorPage = lazy(() => import('./pages/BriefingEditorPage'));
const BriefingDetailPage = lazy(() => import('./pages/BriefingDetailPage'));
const BriefingListPage = lazy(() => import('./pages/BriefingListPage'));
const SettingsPage = lazy(() => import('./pages/SettingsPage'));

function RouteLoading() {
  return <div className="loading-wrap"><Spin size="large" /></div>;
}

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
      <Suspense fallback={<RouteLoading />}>
        <Routes>
          <Route path="/" element={loading ? <RouteLoading /> : <DashboardPage dashboard={dashboard} onTaskCreated={loadDashboard} />} />
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
      </Suspense>
    </AppLayout>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Suspense fallback={<RouteLoading />}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/*" element={<ProtectedLayout />} />
          </Routes>
        </Suspense>
      </AuthProvider>
    </BrowserRouter>
  );
}
