import { FilterOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, DatePicker, Input, Select, Space, Table, Tag } from 'antd';
import { useState } from 'react';

export default function SendRecordsPage({ records = [] }) {
  const [searchText, setSearchText] = useState('');

  const columns = [
    { title: '任务ID', dataIndex: 'id', width: 120 },
    { title: '任务标题', dataIndex: 'title' },
    { title: '发送渠道', dataIndex: 'channel', width: 100 },
    { title: '预约时间', dataIndex: 'plannedSendTime', width: 160 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => {
        const colorMap = { 已完成: 'green', 待发送: 'blue', 草稿: 'default' };
        return <Tag color={colorMap[status] || 'default'}>{status}</Tag>;
      }
    },
    { title: '覆盖人数', dataIndex: 'recipientCount', width: 100 },
    { title: '成功率', dataIndex: 'successRate', width: 100 },
    { title: '创建人', dataIndex: 'creator', width: 100 }
  ];

  const filtered = records.filter((r) =>
    r.title.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space wrap>
          <Input
            placeholder="搜索任务标题"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            style={{ width: 200 }}
          />
          <Select placeholder="状态筛选" style={{ width: 120 }} allowClear>
            <Select.Option value="已完成">已完成</Select.Option>
            <Select.Option value="待发送">待发送</Select.Option>
            <Select.Option value="草稿">草稿</Select.Option>
          </Select>
          <DatePicker.RangePicker placeholder={['开始日期', '结束日期']} />
          <Button icon={<FilterOutlined />}>筛选</Button>
          <Button icon={<ReloadOutlined />}>刷新</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="发送记录列表">
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
