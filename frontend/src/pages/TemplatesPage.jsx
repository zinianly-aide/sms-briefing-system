import { DeleteOutlined, PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Popconfirm, Select, Space, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import { createTemplate, deleteTemplate, fetchTemplates, searchTemplates, updateTemplate } from '../api/template';

const defaultForm = {
  name: '',
  category: '',
  content: '',
  status: '启用中',
  owner: ''
};

export default function TemplatesPage() {
  const [templates, setTemplates] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const loadTemplates = async (keyword) => {
    try {
      setLoading(true);
      const data = keyword ? await searchTemplates(keyword) : await fetchTemplates();
      setTemplates(data || []);
    } catch (err) {
      message.error(err.message || '加载模板失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadTemplates();
  }, []);

  const openCreate = () => {
    setEditing(null);
    form.setFieldsValue(defaultForm);
    setOpen(true);
  };

  const openEdit = (record) => {
    setEditing(record);
    form.setFieldsValue({
      name: record.name,
      category: record.category,
      content: record.content,
      status: record.status,
      owner: record.owner
    });
    setOpen(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload = { ...values };
      if (editing) {
        await updateTemplate(editing.id, { ...editing, ...payload });
        message.success('模板已更新');
      } else {
        await createTemplate(payload);
        message.success('模板已创建');
      }
      setOpen(false);
      form.resetFields();
      loadTemplates(searchText);
    } catch (err) {
      if (err?.errorFields) return;
      message.error(err.message || '保存模板失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteTemplate(id);
      message.success('模板已删除');
      loadTemplates(searchText);
    } catch (err) {
      message.error(err.message || '删除模板失败');
    }
  };

  const columns = [
    { title: '模板ID', dataIndex: 'id', width: 100 },
    { title: '模板名称', dataIndex: 'name' },
    { title: '分类', dataIndex: 'category', width: 120 },
    { title: '模板内容', dataIndex: 'content', ellipsis: true },
    {
      title: '更新时间',
      dataIndex: 'updatedAt',
      width: 180,
      render: (value) => value || '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => <Tag color={status === '启用中' ? 'green' : 'gold'}>{status}</Tag>
    },
    { title: '维护人', dataIndex: 'owner', width: 120 },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => openEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除该模板？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const filtered = templates.filter((t) =>
    !searchText || t.name?.toLowerCase().includes(searchText.toLowerCase()) || t.category?.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space>
          <Input
            placeholder="搜索模板名称/分类"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onPressEnter={() => loadTemplates(searchText)}
            style={{ width: 240 }}
          />
          <Button onClick={() => loadTemplates(searchText)}>查询</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新建模板</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="模板列表">
        <Table rowKey="id" loading={loading} dataSource={filtered} columns={columns} pagination={{ pageSize: 10 }} />
      </Card>

      <Modal title={editing ? '编辑模板' : '新建模板'} open={open} onOk={handleSubmit} onCancel={() => setOpen(false)} destroyOnClose width={720}>
        <Form form={form} layout="vertical" initialValues={defaultForm}>
          <Form.Item label="模板名称" name="name" rules={[{ required: true, message: '请输入模板名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="分类" name="category" rules={[{ required: true, message: '请输入分类' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="模板内容" name="content" rules={[{ required: true, message: '请输入模板内容' }]}>
            <Input.TextArea rows={5} />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={[{ label: '启用中', value: '启用中' }, { label: '草稿', value: '草稿' }]} />
          </Form.Item>
          <Form.Item label="维护人" name="owner">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
