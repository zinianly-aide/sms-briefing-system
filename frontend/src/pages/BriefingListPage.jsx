import { DeleteOutlined, EyeOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Input, Popconfirm, Space, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import { deleteBriefing, fetchBriefings, searchBriefings } from '../api/briefing';

export default function BriefingListPage({ onCreate, onView }) {
  const [items, setItems] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [loading, setLoading] = useState(false);

  const loadBriefings = async (keyword) => {
    try {
      setLoading(true);
      const data = keyword ? await searchBriefings(keyword) : await fetchBriefings();
      setItems(data || []);
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

  const columns = [
    { title: '简讯ID', dataIndex: 'id', width: 100 },
    { title: '标题', dataIndex: 'title' },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => <Tag color={status === '待发送' ? 'blue' : 'default'}>{status}</Tag>
    },
    { title: '渠道', dataIndex: 'channel', width: 120 },
    { title: '作者', dataIndex: 'author', width: 120 },
    { title: '版本', dataIndex: 'version', width: 100 },
    { title: '发送对象', dataIndex: 'audience', ellipsis: true },
    {
      title: '操作',
      width: 160,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EyeOutlined />} onClick={() => onView?.(record.id)}>查看</Button>
          <Popconfirm title="确认删除该简讯？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space>
          <Input
            placeholder="搜索简讯标题/作者"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onPressEnter={() => loadBriefings(searchText)}
            style={{ width: 260 }}
          />
          <Button onClick={() => loadBriefings(searchText)}>查询</Button>
          <Button type="primary" onClick={onCreate}>新建简讯</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="简讯列表">
        <Table rowKey="id" loading={loading} dataSource={items} columns={columns} pagination={{ pageSize: 10 }} />
      </Card>
    </Space>
  );
}
