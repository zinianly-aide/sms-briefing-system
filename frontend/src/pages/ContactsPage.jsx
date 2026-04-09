import { DeleteOutlined, DownloadOutlined, PlusOutlined, SearchOutlined, UploadOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Popconfirm, Select, Space, Table, Tag, message } from 'antd';
import { useEffect, useMemo, useState } from 'react';
import { createContact, deleteContact, fetchContacts, searchContacts, updateContact } from '../api/contact';

const defaultForm = {
  name: '',
  mobile: '',
  department: '',
  title: '',
  status: 'active'
};

const statusTextMap = {
  active: '有效',
  inactive: '停用'
};

export default function ContactsPage() {
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [department, setDepartment] = useState();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const loadContacts = async (keyword) => {
    try {
      setLoading(true);
      const data = keyword ? await searchContacts(keyword) : await fetchContacts();
      setContacts(data || []);
    } catch (err) {
      message.error(err.message || '加载联系人失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadContacts();
  }, []);

  const departmentOptions = useMemo(() => {
    return [...new Set(contacts.map((item) => item.department).filter(Boolean))].map((item) => ({
      label: item,
      value: item
    }));
  }, [contacts]);

  const filtered = contacts.filter((item) => {
    const keywordOk = !searchText || item.name?.toLowerCase().includes(searchText.toLowerCase()) || item.mobile?.includes(searchText);
    const departmentOk = !department || item.department === department;
    return keywordOk && departmentOk;
  });

  const openCreate = () => {
    setEditing(null);
    form.setFieldsValue(defaultForm);
    setOpen(true);
  };

  const openEdit = (record) => {
    setEditing(record);
    form.setFieldsValue({
      name: record.name,
      mobile: record.mobile,
      department: record.department,
      title: record.title,
      status: record.status
    });
    setOpen(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editing) {
        await updateContact(editing.id, { ...editing, ...values });
        message.success('联系人已更新');
      } else {
        await createContact(values);
        message.success('联系人已创建');
      }
      setOpen(false);
      form.resetFields();
      loadContacts(searchText);
    } catch (err) {
      if (err?.errorFields) return;
      message.error(err.message || '保存联系人失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteContact(id);
      message.success('联系人已删除');
      loadContacts(searchText);
    } catch (err) {
      message.error(err.message || '删除联系人失败');
    }
  };

  const columns = [
    { title: '联系人ID', dataIndex: 'id', width: 100 },
    { title: '姓名', dataIndex: 'name', width: 120 },
    { title: '手机号', dataIndex: 'mobile', width: 150 },
    { title: '部门', dataIndex: 'department', width: 140 },
    { title: '职位', dataIndex: 'title', width: 140 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => <Tag color={status === 'active' ? 'green' : 'default'}>{statusTextMap[status] || status}</Tag>
    },
    {
      title: '操作',
      width: 140,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" onClick={() => openEdit(record)}>编辑</Button>
          <Popconfirm title="确认删除该联系人？" onConfirm={() => handleDelete(record.id)}>
            <Button type="link" size="small" danger icon={<DeleteOutlined />}>删除</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <Space direction="vertical" size={16} style={{ width: '100%' }}>
      <Card className="soft-card" size="small">
        <Space wrap>
          <Input
            placeholder="搜索联系人姓名/手机号"
            prefix={<SearchOutlined />}
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            onPressEnter={() => loadContacts(searchText)}
            style={{ width: 220 }}
          />
          <Select
            placeholder="部门筛选"
            style={{ width: 160 }}
            allowClear
            options={departmentOptions}
            value={department}
            onChange={setDepartment}
          />
          <Button onClick={() => loadContacts(searchText)}>查询</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新建联系人</Button>
          <Button icon={<UploadOutlined />} disabled>批量导入</Button>
          <Button icon={<DownloadOutlined />} disabled>导出</Button>
        </Space>
      </Card>
      <Card className="soft-card" title="通讯录列表">
        <Table rowKey="id" loading={loading} dataSource={filtered} columns={columns} pagination={{ pageSize: 10 }} />
      </Card>

      <Modal
        title={editing ? '编辑联系人' : '新建联系人'}
        open={open}
        onOk={handleSubmit}
        onCancel={() => setOpen(false)}
        destroyOnClose
      >
        <Form form={form} layout="vertical" initialValues={defaultForm}>
          <Form.Item label="姓名" name="name" rules={[{ required: true, message: '请输入姓名' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="手机号" name="mobile" rules={[{ required: true, message: '请输入手机号' }]}>
            <Input />
          </Form.Item>
          <Form.Item label="部门" name="department">
            <Input />
          </Form.Item>
          <Form.Item label="职位" name="title">
            <Input />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={[{ label: '有效', value: 'active' }, { label: '停用', value: 'inactive' }]} />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
