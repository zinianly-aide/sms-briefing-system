import { DownloadOutlined, PlusOutlined, SearchOutlined, UploadOutlined } from '@ant-design/icons';
import { Button, Card, Input, Select, Space, Table, Tag } from 'antd';
import { useState } from 'react';

export default function ContactsPage({ contacts = [] }) {
  const [searchText, setSearchText] = useState('');

  const columns = [
    { title: '联系人ID', dataIndex: 'id', width: 120 },
    { title: '姓名', dataIndex: 'name', width: 100 },
    { title: '手机号', dataIndex: 'phone', width: 120 },
    { title: '部门', dataIndex: 'dept', width: 140 },
    { title: '所属群组', dataIndex: 'group', width: 160 },
    { title: '数据来源', dataIndex: 'source', width: 100 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 80,
      render: (status) => <Tag color={status === '有效' ? 'green' : 'default'}>{status}</Tag>
    },
    {
      title: '操作',
      width: 100,
      render: () => <Button type="link" size="small">编辑</Button>
    }
  ];

  const filtered = contacts.filter((c) =>
    c.name.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space wrap>
          <Input
            placeholder="搜索联系人姓名"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ width: 200 }}
          />
          <Select placeholder="部门筛选" style={{ width: 140 }} allowClear />
          <Select placeholder="来源筛选" style={{ width: 140 }} allowClear />
          <Button type="primary" icon={<PlusOutlined />}>新建联系人</Button>
          <Button icon={<UploadOutlined />}>批量导入</Button>
          <Button icon={<DownloadOutlined />}>导出</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="通讯录列表">
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
