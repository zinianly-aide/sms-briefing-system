import { DeleteOutlined, FilterOutlined, ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, DatePicker, Form, Input, Modal, Popconfirm, Select, Space, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import { createTask, deleteTask, fetchTasks, searchTasks, updateTask } from '../api/task';

const defaultForm = {
  title: '',
  channel: '短信',
  plannedSendTime: '',
  status: '待发送',
  recipientCount: 0,
  creator: '',
  successRate: '—'
};

export default function SendRecordsPage() {
  const [records, setRecords] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const loadTasks = async (keyword) => {
    try {
      setLoading(true);
      const data = keyword ? await searchTasks(keyword) : await fetchTasks();
      setRecords(data || []);
    } catch (err) {
      message.error(err.message || '加载发送任务失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTasks();
  }, []);

  const openCreate = () => {
    setEditing(null);
    form.setFieldsValue(defaultForm);
    setOpen(true);
  };

  const openEdit = (record) => {
    setEditing(record);
    form.setFieldsValue({ ...record });
    setOpen(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        recipientCount: Number(values.recipientCount || 0)
      };
      if (editing) {
        await updateTask(editing.id, { ...editing, ...payload });
        message.success('发送任务已更新');
      } else {
        await createTask(payload);
        message.success('发送任务已创建');
      }
      setOpen(false);
      form.resetFields();
      loadTasks(searchText);
    } catch (err) {
      if (err?.errorFields) return;
      message.error(err.message || '保存发送任务失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteTask(id);
      message.success('发送任务已删除');
      loadTasks(searchText);
    } catch (err) {
      message.error(err.message || '删除发送任务失败');
    }
  };

  const columns = [
    { title: '任务ID', dataIndex: 'id', width: 100 },
    { title: '任务标题', dataIndex: 'title' },
    { title: '发送渠道', dataIndex: 'channel', width: 100 },
    {
      title: '预约时间',
      dataIndex: 'plannedSendTime',
      width: 180,
      render: (value) => value || '-'
    },
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
    { title: '创建人', dataIndex: 'creator', width: 100 },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => openEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除该任务？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const filtered = records.filter((r) =>
    !searchText || r.title?.toLowerCase().includes(searchText.toLowerCase()) || r.creator?.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space wrap>
          <Input
            placeholder="搜索任务标题/创建人"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onPressEnter={() => loadTasks(searchText)}
            style={{ width: 220 }}
          />
          <Select placeholder="状态筛选" style={{ width: 120 }} allowClear disabled>
            <Select.Option value="已完成">已完成</Select.Option>
            <Select.Option value="待发送">待发送</Select.Option>
            <Select.Option value="草稿">草稿</Select.Option>
          </Select>
          <DatePicker.RangePicker placeholder={['开始日期', '结束日期']} disabled />
          <Button icon={<FilterOutlined />} disabled>筛选</Button>
          <Button icon={<ReloadOutlined />} onClick={() => loadTasks(searchText)}>刷新</Button>
          <Button type="primary" onClick={openCreate}>新建任务</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="发送记录列表">
        <Table rowKey="id" loading={loading} dataSource={filtered} columns={columns} pagination={{ pageSize: 10 }} />
      </Card>

      <Modal title={editing ? '编辑发送任务' : '新建发送任务'} open={open} onOk={handleSubmit} onCancel={() => setOpen(false)} destroyOnClose>
        <Form form={form} layout="vertical" initialValues={defaultForm}>
          <Form.Item label="任务标题" name="title" rules={[{ required: true, message: '请输入任务标题' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="发送渠道" name="channel">
            <Select options={[{ label: '短信', value: '短信' }, { label: '短信+企微', value: '短信+企微' }]} />
          </Form.Item>
          <Form.Item label="预约时间" name="plannedSendTime">
            <Input placeholder="例如 2026-04-10T09:00:00" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={[{ label: '草稿', value: '草稿' }, { label: '待发送', value: '待发送' }, { label: '已完成', value: '已完成' }]} />
          </Form.Item>
          <Form.Item label="覆盖人数" name="recipientCount">
            <Input type="number" />
          </Form.Item>
          <Form.Item label="创建人" name="creator">
            <Input />
          </Form.Item>
          <Form.Item label="成功率" name="successRate">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
