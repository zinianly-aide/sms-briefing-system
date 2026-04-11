import { CheckCircleOutlined, CloseCircleOutlined, DeleteOutlined, DownloadOutlined, PlusOutlined, SearchOutlined, SyncOutlined, UploadOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Popconfirm, Select, Space, Table, Tag, Upload, message } from 'antd';
import { useEffect, useMemo, useState } from 'react';
import { createContact, deleteContact, exportContacts, fetchContacts, importContacts, searchContacts, syncHrData, updateContact } from '../api/contact';

const defaultForm = {
  name: '',
  mobile: '',
  department: '',
  title: '',
  status: 'active'
};

const statusConfig = {
  active: { text: '有效', color: 'green', icon: <CheckCircleOutlined /> },
  inactive: { text: '停用', color: 'default', icon: <CloseCircleOutlined /> }
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

  const handleImport = async (file) => {
    try {
      const result = await importContacts(file);
      const { success: ok, fail: ng, errors } = result;
      if (ng > 0) {
        Modal.info({ title: '导入完成', content: `成功 ${ok} 条，失败 ${ng} 条\n${errors?.slice(0, 5).join('\n') || ''}` });
      } else {
        message.success(`成功导入 ${ok} 条联系人`);
      }
      loadContacts();
    } catch (err) {
      message.error(err.message || '导入失败');
    }
    return false;
  };

  const handleSyncHr = async () => {
    try {
      const result = await syncHrData();
      message.success(`同步完成：新增 ${result.synced} 人，跳过 ${result.skipped} 人`);
      loadContacts();
    } catch (err) {
      message.error(err.message || '同步失败');
    }
  };

  const columns = [
    { title: '姓名', dataIndex: 'name', width: 120, fontWeight: 500 },
    { title: '手机号', dataIndex: 'mobile', width: 150 },
    { title: '部门', dataIndex: 'department', width: 140 },
    { title: '职位', dataIndex: 'title', width: 140 },
    {
      title: '状态',
      dataIndex: 'status',
      width: 100,
      render: (status) => {
        const cfg = statusConfig[status] || { text: status, color: 'default' };
        return <Tag color={cfg.color} icon={cfg.icon}>{cfg.text}</Tag>;
      }
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
      <Card className="toolbar-card" bordered={false} size="small">
        <div className="toolbar-inner">
          <div className="toolbar-left">
            <Input
              placeholder="搜索联系人姓名/手机号"
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onPressEnter={() => loadContacts(searchText)}
              style={{ width: 240 }}
              allowClear
            />
            <Select
              placeholder="部门筛选"
              style={{ width: 160 }}
              allowClear
              options={departmentOptions}
              value={department}
              onChange={(v) => { setDepartment(v); }}
            />
            <Button type="primary" icon={<SearchOutlined />} onClick={() => loadContacts(searchText)}>查询</Button>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新建联系人</Button>
          <Upload accept=".csv" showUploadList={false} beforeUpload={handleImport}>
            <Button icon={<UploadOutlined />}>导入CSV</Button>
          </Upload>
          <Button icon={<DownloadOutlined />} onClick={exportContacts}>导出CSV</Button>
          <Button icon={<SyncOutlined />} onClick={handleSyncHr}>HR同步</Button>
        </div>
      </Card>
      <Card className="soft-card" bordered={false}>
        <Table rowKey="id" loading={loading} dataSource={filtered} columns={columns} pagination={{ pageSize: 10, showSizeChanger: true, showTotal: (total) => `共 ${total} 条` }} size="middle" />
      </Card>

      <Modal
        title={editing ? '编辑联系人' : '新建联系人'}
        open={open}
        onOk={handleSubmit}
        onCancel={() => setOpen(false)}
        destroyOnClose
        okText="确认"
        cancelText="取消"
      >
        <Form form={form} layout="vertical" initialValues={defaultForm}>
          <Form.Item label="姓名" name="name" rules={[{ required: true, message: '请输入姓名' }]}>
            <Input placeholder="请输入姓名" />
          </Form.Item>
          <Form.Item label="手机号" name="mobile" rules={[{ required: true, message: '请输入手机号' }]}>
            <Input placeholder="请输入手机号" />
          </Form.Item>
          <Form.Item label="部门" name="department">
            <Input placeholder="请输入部门" />
          </Form.Item>
          <Form.Item label="职位" name="title">
            <Input placeholder="请输入职位" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={[{ label: '有效', value: 'active' }, { label: '停用', value: 'inactive' }]} />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
