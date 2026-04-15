import { DeleteOutlined, PlayCircleOutlined, PlusOutlined, ReloadOutlined, SearchOutlined, StopOutlined, TeamOutlined } from '@ant-design/icons';
import { Button, Card, DatePicker, Form, Input, InputNumber, Modal, Popconfirm, Progress, Select, Space, Table, Tag, message } from 'antd';
import dayjs from 'dayjs';
import { useEffect, useState } from 'react';
import { cancelTask, createTask, deleteTask, executeTask, fetchTaskRecipients, fetchTasks, searchTasks, updateTask } from '../api/task';
import { CHANNEL_OPTIONS, TASK_EDITABLE_STATUSES, TASK_STATUS_OPTIONS, getChannelLabel, getRecipientStatusMeta, getTaskStatusMeta } from '../constants/domain';

const defaultForm = {
  title: '',
  channel: 'sms',
  plannedSendTime: '',
  status: 'pending',
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
  const [recipientModal, setRecipientModal] = useState({ open: false, taskId: null, taskTitle: '' });
  const [recipients, setRecipients] = useState([]);

  const loadRecords = async (keyword) => {
    try {
      setLoading(true);
      const data = keyword ? await searchTasks(keyword) : await fetchTasks();
      setRecords(data?.list || data || []);
    } catch (err) {
      message.error(err.message || '加载任务失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRecords();
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
        plannedSendTime: values.plannedSendTime ? dayjs(values.plannedSendTime).format('YYYY-MM-DDTHH:mm:ss') : '',
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
      loadRecords(searchText);
    } catch (err) {
      if (err?.errorFields) return;
      message.error(err.message || '保存发送任务失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteTask(id);
      message.success('发送任务已删除');
      loadRecords(searchText);
    } catch (err) {
      message.error(err.message || '删除发送任务失败');
    }
  };

  const handleExecute = async (id) => {
    try {
      await executeTask(id);
      message.success('任务已执行');
      loadRecords(searchText);
    } catch (err) {
      message.error(err.message || '执行任务失败');
    }
  };

  const handleCancel = async (id) => {
    try {
      await cancelTask(id);
      message.success('任务已取消');
      loadRecords(searchText);
    } catch (err) {
      message.error(err.message || '取消任务失败');
    }
  };

  const openRecipients = async (record) => {
    try {
      const data = await fetchTaskRecipients(record.id);
      setRecipients(data || []);
      setRecipientModal({ open: true, taskId: record.id, taskTitle: record.title });
    } catch (err) {
      message.error(err.message || '加载接收人明细失败');
    }
  };

  const safeRecords = Array.isArray(records) ? records : [];
  const safeRecipients = Array.isArray(recipients) ? recipients : [];

  const columns = [
    { title: '任务标题', dataIndex: 'title' },
    { title: '发送渠道', dataIndex: 'channel', width: 100, render: (channel) => getChannelLabel(channel) },
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
        const meta = getTaskStatusMeta(status);
        return <Tag color={meta.color}>{meta.label}</Tag>;
      }
    },
    { title: '覆盖人数', dataIndex: 'recipientCount', width: 100 },
    {
      title: '成功率',
      dataIndex: 'successRate',
      width: 140,
      render: (rate) => {
        if (rate === '—' || !rate) return <span style={{ color: '#bfbfbf' }}>-</span>;
        const num = parseFloat(rate);
        if (isNaN(num)) return rate;
        const percent = Math.min(Math.max(num, 0), 100);
        return <Progress percent={percent} size="small" status={percent >= 90 ? 'success' : 'active'} />;
      }
    },
    { title: '创建人', dataIndex: 'creator', width: 100 },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <Space size={0}>
          <Button type="link" size="small" icon={<TeamOutlined />} onClick={() => openRecipients(record)}>明细</Button>
          {TASK_EDITABLE_STATUSES.includes(record.status) && (
            <Button type="link" size="small" icon={<PlayCircleOutlined />} onClick={() => handleExecute(record.id)} style={{ color: '#52c41a' }}>执行</Button>
          )}
          {TASK_EDITABLE_STATUSES.includes(record.status) && (
            <Popconfirm title="确认取消该任务？" onConfirm={() => handleCancel(record.id)}>
              <Button type="link" size="small" icon={<StopOutlined />}>取消</Button>
            </Popconfirm>
          )}
          <Button type="link" size="small" onClick={() => openEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除该任务？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const filtered = safeRecords.filter((r) =>
    !searchText || r.title?.toLowerCase().includes(searchText.toLowerCase()) || r.creator?.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="toolbar-card" bordered={false} size="small">
        <div className="toolbar-inner">
          <div className="toolbar-left">
            <Input
              placeholder="搜索任务标题/创建人"
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onPressEnter={() => loadRecords(searchText)}
              style={{ width: 240 }}
              allowClear
            />
            <Button type="primary" icon={<SearchOutlined />} onClick={() => loadRecords(searchText)}>查询</Button>
            <Button icon={<ReloadOutlined />} onClick={() => loadRecords(searchText)}>刷新</Button>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新建任务</Button>
        </div>
      </Card>
      <Card className="soft-card" bordered={false}>
        <Table rowKey="id" loading={loading} dataSource={filtered} columns={columns} pagination={{ pageSize: 10, showSizeChanger: true, showTotal: (total) => `共 ${total} 条` }} size="middle" />
      </Card>

      <Modal title={editing ? '编辑发送任务' : '新建发送任务'} open={open} onOk={handleSubmit} onCancel={() => setOpen(false)} destroyOnClose okText="确认" cancelText="取消" width={600}>
        <Form form={form} layout="vertical" initialValues={defaultForm}>
          <Form.Item label="任务标题" name="title" rules={[{ required: true, message: '请输入任务标题' }]}>
            <Input placeholder="请输入任务标题" />
          </Form.Item>
          <Form.Item label="发送渠道" name="channel">
            <Select options={CHANNEL_OPTIONS} />
          </Form.Item>
          <Form.Item label="预约时间" name="plannedSendTime">
            <DatePicker showTime style={{ width: '100%' }} placeholder="选择预约发送时间" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={TASK_STATUS_OPTIONS.filter((item) => ['draft', 'pending', 'completed'].includes(item.value))} />
          </Form.Item>
          <Form.Item label="覆盖人数" name="recipientCount">
            <InputNumber min={0} style={{ width: '100%' }} placeholder="0" />
          </Form.Item>
          <Form.Item label="创建人" name="creator">
            <Input placeholder="请输入创建人" />
          </Form.Item>
          <Form.Item label="成功率" name="successRate">
            <Input placeholder="例如：95 或 95%" />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`${recipientModal.taskTitle} — 接收人明细`}
        open={recipientModal.open}
        onCancel={() => setRecipientModal({ open: false, taskId: null, taskTitle: '' })}
        footer={<Button onClick={() => setRecipientModal({ open: false, taskId: null, taskTitle: '' })}>关闭</Button>}
        width={600}
      >
        <Table
          rowKey="id"
          dataSource={safeRecipients}
          columns={[
            { title: '姓名', dataIndex: 'name', width: 100 },
            { title: '手机号', dataIndex: 'mobile', width: 140 },
            {
              title: '状态',
              dataIndex: 'status',
              width: 100,
              render: (status) => {
                const meta = getRecipientStatusMeta(status);
                return <Tag color={meta.color}>{meta.label}</Tag>;
              }
            },
            { title: '发送时间', dataIndex: 'sentAt', width: 180 },
            { title: '错误信息', dataIndex: 'errorMsg', ellipsis: true }
          ]}
          pagination={false}
          size="small"
        />
      </Modal>
    </Space>
  );
}
