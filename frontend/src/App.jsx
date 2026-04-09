import { Alert, Spin, message } from 'antd';
import { useEffect, useState } from 'react';
import { fetchDashboard } from './api/dashboard';
import AppLayout from './layouts/AppLayout';
import ContactsPage from './pages/ContactsPage';
import DashboardPage from './pages/DashboardPage';
import GroupsPage from './pages/GroupsPage';
import TemplatesPage from './pages/TemplatesPage';
import SendRecordsPage from './pages/SendRecordsPage';
import BriefingEditorPage from './pages/BriefingEditorPage';
import BriefingDetailPage from './pages/BriefingDetailPage';
import BriefingListPage from './pages/BriefingListPage';

export default function App() {
  const [loading, setLoading] = useState(true);
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState('');
  const [activePage, setActivePage] = useState('contacts');
  const [currentBriefingId, setCurrentBriefingId] = useState(null);
  const [briefingView, setBriefingView] = useState('list');

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
    if (activePage === 'templates') {
      return <TemplatesPage />;
    }
    if (activePage === 'tasks') {
      return <SendRecordsPage />;
    }
    if (activePage === 'briefing') {
      if (briefingView === 'detail' && currentBriefingId) {
        return <BriefingDetailPage briefingId={currentBriefingId} onBack={() => setBriefingView('list')} />;
      }
      if (briefingView === 'editor') {
        return <BriefingEditorPage onCreated={(created) => {
          setCurrentBriefingId(created?.id);
          setBriefingView('detail');
        }} />;
      }
      return <BriefingListPage onCreate={() => setBriefingView('editor')} onView={(id) => {
        setCurrentBriefingId(id);
        setBriefingView('detail');
      }} />;
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
