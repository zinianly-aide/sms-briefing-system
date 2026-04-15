import { CopyOutlined, DeleteOutlined, EyeOutlined, PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Input, Popconfirm, Space, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { cloneBriefing, deleteBriefing, fetchBriefings, searchBriefings } from '../api/briefing';
import { getBriefingStatusMeta, getChannelLabel } from '../constants/domain';

export default function BriefingListPage() {
  const navigate = useNavigate();
  const [items, setItems] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [loading, setLoading] = useState(false);

  const loadBriefings = async (keyword) => {
    try {
      setLoading(true);
      const data = keyword ? await searchBriefings(keyword) : await fetchBriefings();
      setItems(data?.list || data || []);
    } catch (err) {
      message.error(err.message || '加载简讯失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadBriefings();
  }, []);

  const handleDelete = async (id) => {
    try {
      await deleteBriefing(id);
      message.success('简讯已删除');
      loadBriefings(searchText);
    } catch (err) {
      message.error(err.message || '删除简讯失败');
    }
  };

  const handleClone = async (id) => {
    try {
      await cloneBriefing(id);
      message.success('简讯已复制');
      loadBriefings(searchText);
    } catch (err) {
      message.error(err.message || '复制简讯失败');
    }
  };

  const columns = [
    { title: '标题', dataIndex: 'title' },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => {
        const meta = getBriefingStatusMeta(status);
        return <Tag color={meta.color}>{meta.label}</Tag>;
      }
    },
    { title: '渠道', dataIndex: 'channel', width: 120, render: (channel) => getChannelLabel(channel) },
    { title: '作者', dataIndex: 'author', width: 120 },
    { title: '版本', dataIndex: 'version', width: 100 },
    { title: '发送对象', dataIndex: 'audience', ellipsis: true, width: 160 },
    {
      title: '操作',
      width: 160,
      render: (_, record) => (
        <Space size={0}>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => navigate(`/briefings/${record.id}`)}>查看</Button>
          <Button type="link" size="small" icon={<CopyOutlined />} onClick={() => handleClone(record.id)}>复制</Button>
          <Popconfirm title="确认删除该简讯？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="toolbar-card" bordered={false} size="small">
        <div className="toolbar-inner">
          <div className="toolbar-left">
            <Input
              placeholder="搜索简讯标题/作者"
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onPressEnter={() => loadBriefings(searchText)}
              style={{ width: 260 }}
              allowClear
            />
            <Button type="primary" icon={<SearchOutlined />} onClick={() => loadBriefings(searchText)}>查询</Button>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => navigate('/briefings/new')}>新建简讯</Button>
        </div>
      </Card>
      <Card className="soft-card" bordered={false}>
        <Table rowKey="id" loading={loading} dataSource={items} columns={columns} pagination={{ pageSize: 10, showSizeChanger: true, showTotal: (total) => `共 ${total} 条` }} size="middle" />
      </Card>
    </Space>
  );
}
