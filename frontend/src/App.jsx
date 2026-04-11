import { Alert, Button, Spin, message } from 'antd';
import { useEffect, useState } from 'react';
import { fetchDashboard } from './api/dashboard';
import { clearAuth, getUserInfo, isLoggedIn } from './api/auth';
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

export default function App() {
  const [authenticated, setAuthenticated] = useState(isLoggedIn());
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState('');
  const [activePage, setActivePage] = useState('dashboard');
  const [currentBriefingId, setCurrentBriefingId] = useState(null);
  const [briefingView, setBriefingView] = useState('list');

  const userInfo = getUserInfo();

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

  const handleLogin = () => {
    setAuthenticated(true);
    setActivePage('dashboard');
  };

  const handleLogout = () => {
    clearAuth();
    setAuthenticated(false);
    setDashboard(null);
  };

  useEffect(() => {
    if (authenticated && activePage === 'dashboard') {
      loadDashboard();
    } else if (authenticated) {
      setLoading(false);
    }
  }, [activePage, authenticated]);

  if (!authenticated) {
    return <LoginPage onLogin={handleLogin} />;
  }

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
    if (activePage === 'templates') {
      return <TemplatesPage />;
    }
    if (activePage === 'tasks') {
      return <SendRecordsPage />;
    }
    if (activePage === 'settings') {
      return <SettingsPage />;
    }
    if (activePage === 'briefing') {
      if (briefingView === 'detail' && currentBriefingId) {
        return <BriefingDetailPage briefingId={currentBriefingId} onBack={() => setBriefingView('list')} />;
      }
      if (briefingView === 'editor') {
        return <BriefingEditorPage onCreated={(created) => {
          setCurrentBriefingId(created?.id);
          setBriefingView('detail');
        }} onCancel={() => setBriefingView('list')} />;
      }
      return <BriefingListPage onCreate={() => setBriefingView('editor')} onView={(id) => {
        setCurrentBriefingId(id);
        setBriefingView('detail');
      }} />;
    }
    return <ContactsPage />;
  };

  return (
    <AppLayout activeKey={activePage} username={userInfo?.displayName} onNavigate={(key) => {
      setActivePage(key);
      if (key !== 'briefing') {
        setBriefingView('list');
        setCurrentBriefingId(null);
      }
    }} onLogout={handleLogout}>
      {error ? <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} /> : null}
      {renderPage()}
    </AppLayout>
  );
}
