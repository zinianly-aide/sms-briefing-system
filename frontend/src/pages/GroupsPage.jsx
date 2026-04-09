import { PlusOutlined, SearchOutlined, SyncOutlined } from '@ant-design/icons';
import { Button, Card, Input, Space, Table, Tag } from 'antd';
import { useState } from 'react';

export default function GroupsPage({ groups = [] }) {
  const [searchText, setSearchText] = useState('');

  const columns = [
    { title: '群组ID', dataIndex: 'id', width: 120 },
    { title: '群组名称', dataIndex: 'name' },
    { title: '归属部门', dataIndex: 'ownerDept', width: 140 },
    { title: '成员数', dataIndex: 'memberCount', width: 100 },
    { title: '同步方式', dataIndex: 'syncMode', width: 120 },
    { title: '最近同步', dataIndex: 'lastSyncTime', width: 160 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => <Tag color={status === '启用' ? 'green' : 'default'}>{status}</Tag>
    },
    {
      title: '操作',
      width: 140,
      render: () => (
        <Space size={0}>
          <Button type="link" size="small">编辑</Button>
          <Button type="link" size="small" icon={<SyncOutlined />}>同步</Button>
        </Space>
      )
    }
  ];

  const filtered = groups.filter((g) =>
    g.name.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space>
          <Input
            placeholder="搜索群组名称"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ width: 240 }}
          />
          <Button type="primary" icon={<PlusOutlined />}>新建群组</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="群组列表">
        <Table
          rowKey="id"
          dataSource={filtered}
          columns={columns}
          pagination={{ pageSize: 10 }}
        />
      </Card>
    </Space>
  );
}
