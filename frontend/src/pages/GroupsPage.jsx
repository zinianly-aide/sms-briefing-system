import { DeleteOutlined, PlusOutlined, SearchOutlined, SyncOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Popconfirm, Space, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import { createGroup, deleteGroup, fetchGroups, searchGroups, updateGroup } from '../api/group';

const defaultForm = {
  name: '',
  ownerDept: '',
  memberCount: 0,
  tags: '',
  status: '启用'
};

export default function GroupsPage() {
  const [groups, setGroups] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const loadGroups = async (keyword) => {
    try {
      setLoading(true);
      const data = keyword ? await searchGroups(keyword) : await fetchGroups();
      setGroups(data || []);
    } catch (err) {
      message.error(err.message || '加载群组失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadGroups();
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
      ownerDept: record.ownerDept,
      memberCount: record.memberCount,
      tags: record.tags,
      status: record.status
    });
    setOpen(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      const payload = {
        ...values,
        memberCount: Number(values.memberCount || 0),
        lastSyncTime: new Date().toISOString().slice(0, 19)
      };
      if (editing) {
        await updateGroup(editing.id, { ...editing, ...payload });
        message.success('群组已更新');
      } else {
        await createGroup(payload);
        message.success('群组已创建');
      }
      setOpen(false);
      form.resetFields();
      loadGroups(searchText);
    } catch (err) {
      if (err?.errorFields) return;
      message.error(err.message || '保存群组失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteGroup(id);
      message.success('群组已删除');
      loadGroups(searchText);
    } catch (err) {
      message.error(err.message || '删除群组失败');
    }
  };

  const columns = [
    { title: '群组ID', dataIndex: 'id', width: 100 },
    { title: '群组名称', dataIndex: 'name' },
    { title: '归属部门', dataIndex: 'ownerDept', width: 140 },
    { title: '成员数', dataIndex: 'memberCount', width: 100 },
    { title: '标签', dataIndex: 'tags', width: 160 },
    {
      title: '最近同步',
      dataIndex: 'lastSyncTime',
      width: 180,
      render: (value) => value || '-'
    },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => <Tag color={status === '启用' ? 'green' : 'default'}>{status}</Tag>
    },
    {
      title: '操作',
      width: 160,
      render: (_, record) => (
        <Space size={0}>
          <Button type="link" size="small" onClick={() => openEdit(record)}>编辑</Button>
          <Button type="link" size="small" icon={<SyncOutlined />} onClick={() => loadGroups(searchText)}>同步</Button>
          <Popconfirm title="确认删除该群组？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  const filtered = groups.filter((g) =>
    !searchText || g.name?.toLowerCase().includes(searchText.toLowerCase()) || g.ownerDept?.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space>
          <Input
            placeholder="搜索群组名称/部门"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onPressEnter={() => loadGroups(searchText)}
            style={{ width: 240 }}
          />
          <Button onClick={() => loadGroups(searchText)}>查询</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新建群组</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="群组列表">
        <Table rowKey="id" loading={loading} dataSource={filtered} columns={columns} pagination={{ pageSize: 10 }} />
      </Card>

      <Modal title={editing ? '编辑群组' : '新建群组'} open={open} onOk={handleSubmit} onCancel={() => setOpen(false)} destroyOnClose>
        <Form form={form} layout="vertical" initialValues={defaultForm}>
          <Form.Item label="群组名称" name="name" rules={[{ required: true, message: '请输入群组名称' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="归属部门" name="ownerDept">
            <Input />
          </Form.Item>
          <Form.Item label="成员数" name="memberCount">
            <Input type="number" />
          </Form.Item>
          <Form.Item label="标签" name="tags">
            <Input placeholder="如：销售,华东,核心群组" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
