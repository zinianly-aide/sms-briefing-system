import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Input, Space, Table, Tag } from 'antd';
import { useState } from 'react';

export default function TemplatesPage({ templates = [] }) {
  const [searchText, setSearchText] = useState('');

  const columns = [
    { title: '模板ID', dataIndex: 'id', width: 120 },
    { title: '模板名称', dataIndex: 'name' },
    { title: '分类', dataIndex: 'category', width: 120 },
    { title: '模板内容', dataIndex: 'content', ellipsis: true },
    { title: '更新时间', dataIndex: 'updatedAt', width: 160 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => <Tag color={status === '启用中' ? 'green' : 'gold'}>{status}</Tag>
    },
    { title: '维护人', dataIndex: 'owner', width: 120 }
  ];

  const filtered = templates.filter((t) =>
    t.name.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space>
          <Input
            placeholder="搜索模板名称"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ width: 240 }}
          />
          <Button type="primary" icon={<PlusOutlined />}>新建模板</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="模板列表">
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
