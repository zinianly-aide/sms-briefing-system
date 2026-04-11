import { DeleteOutlined, PlusOutlined, SearchOutlined, SyncOutlined, TeamOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Popconfirm, Select, Space, Table, Tag, message } from 'antd';
import { useEffect, useState } from 'react';
import { addGroupMembers, createGroup, deleteGroup, fetchGroupMembers, fetchGroups, removeGroupMember, searchGroups, updateGroup } from '../api/group';
import { fetchContacts } from '../api/contact';

const defaultForm = {
  name: '',
  ownerDept: '',
  memberCount: 0,
  tags: [],
  status: '启用'
};

export default function GroupsPage() {
  const [groups, setGroups] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();
  const [memberModal, setMemberModal] = useState({ open: false, groupId: null, groupName: '' });
  const [members, setMembers] = useState([]);
  const [memberLoading, setMemberLoading] = useState(false);
  const [addModal, setAddModal] = useState(false);
  const [allContacts, setAllContacts] = useState([]);
  const [selectedContactIds, setSelectedContactIds] = useState([]);

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
      tags: record.tags ? record.tags.split(',').map(t => t.trim()).filter(Boolean) : [],
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
        tags: (values.tags || []).join(','),
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

  const openMemberModal = async (record) => {
    setMemberModal({ open: true, groupId: record.id, groupName: record.name });
    try {
      setMemberLoading(true);
      const data = await fetchGroupMembers(record.id);
      setMembers(data || []);
    } catch (err) {
      message.error(err.message || '加载成员失败');
    } finally {
      setMemberLoading(false);
    }
  };

  const openAddMemberModal = async () => {
    try {
      const data = await fetchContacts();
      const list = data?.list || data || [];
      setAllContacts(list);
      setSelectedContactIds([]);
      setAddModal(true);
    } catch (err) {
      message.error(err.message || '加载联系人失败');
    }
  };

  const handleAddMembers = async () => {
    if (selectedContactIds.length === 0) {
      message.warning('请选择联系人');
      return;
    }
    try {
      await addGroupMembers(memberModal.groupId, selectedContactIds);
      message.success(`成功添加 ${selectedContactIds.length} 名成员`);
      setAddModal(false);
      const data = await fetchGroupMembers(memberModal.groupId);
      setMembers(data || []);
    } catch (err) {
      message.error(err.message || '添加成员失败');
    }
  };

  const handleRemoveMember = async (contactId) => {
    try {
      await removeGroupMember(memberModal.groupId, contactId);
      message.success('成员已移除');
      const data = await fetchGroupMembers(memberModal.groupId);
      setMembers(data || []);
    } catch (err) {
      message.error(err.message || '移除成员失败');
    }
  };

  const columns = [
    { title: '群组名称', dataIndex: 'name' },
    { title: '归属部门', dataIndex: 'ownerDept', width: 140 },
    { title: '成员数', dataIndex: 'memberCount', width: 100 },
    {
      title: '标签',
      dataIndex: 'tags',
      width: 200,
      render: (tags) => tags ? <Space wrap>{tags.split(',').filter(Boolean).map((tag) => <Tag key={tag}>{tag}</Tag>)}</Space> : '-'
    },
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
          <Button type="link" size="small" icon={<TeamOutlined />} onClick={() => openMemberModal(record)}>成员</Button>
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
      <Card className="toolbar-card" bordered={false} size="small">
        <div className="toolbar-inner">
          <div className="toolbar-left">
            <Input
              placeholder="搜索群组名称/部门"
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onPressEnter={() => loadGroups(searchText)}
              style={{ width: 240 }}
              allowClear
            />
            <Button type="primary" icon={<SearchOutlined />} onClick={() => loadGroups(searchText)}>查询</Button>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新建群组</Button>
        </div>
      </Card>
      <Card className="soft-card" bordered={false}>
        <Table rowKey="id" loading={loading} dataSource={filtered} columns={columns} pagination={{ pageSize: 10, showSizeChanger: true, showTotal: (total) => `共 ${total} 条` }} size="middle" />
      </Card>

      <Modal title={editing ? '编辑群组' : '新建群组'} open={open} onOk={handleSubmit} onCancel={() => setOpen(false)} destroyOnClose okText="确认" cancelText="取消">
        <Form form={form} layout="vertical" initialValues={defaultForm}>
          <Form.Item label="群组名称" name="name" rules={[{ required: true, message: '请输入群组名称' }]}>
            <Input placeholder="请输入群组名称" />
          </Form.Item>
          <Form.Item label="归属部门" name="ownerDept">
            <Input placeholder="请输入归属部门" />
          </Form.Item>
          <Form.Item label="成员数" name="memberCount">
            <Input type="number" placeholder="0" />
          </Form.Item>
          <Form.Item label="标签" name="tags">
            <Select mode="tags" placeholder="输入标签后回车添加，如：销售、华东、核心群组" />
          </Form.Item>
          <Form.Item label="状态" name="status">
            <Select options={[{ label: '启用', value: '启用' }, { label: '停用', value: '停用' }]} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={`${memberModal.groupName} — 成员列表`}
        open={memberModal.open}
        onCancel={() => setMemberModal({ open: false, groupId: null, groupName: '' })}
        footer={<Button onClick={() => setMemberModal({ open: false, groupId: null, groupName: '' })}>关闭</Button>}
        width={600}
      >
        <div style={{ marginBottom: 12 }}>
          <Button type="primary" size="small" icon={<PlusOutlined />} onClick={openAddMemberModal}>添加成员</Button>
        </div>
        <Table
          rowKey="contactId"
          loading={memberLoading}
          dataSource={members}
          columns={[
            { title: '姓名', dataIndex: 'contactName', width: 100 },
            { title: '手机号', dataIndex: 'contactMobile', width: 140 },
            { title: '部门', dataIndex: 'contactDepartment', width: 120 },
            { title: '角色', dataIndex: 'role', width: 80 },
            {
              title: '操作',
              width: 80,
              render: (_, record) => (
                <Popconfirm title="确认移除该成员？" onConfirm={() => handleRemoveMember(record.contactId)}>
                  <Button type="link" size="small" danger>移除</Button>
                </Popconfirm>
              )
            }
          ]}
          pagination={false}
          size="small"
        />
      </Modal>

      <Modal
        title="选择联系人"
        open={addModal}
        onOk={handleAddMembers}
        onCancel={() => setAddModal(false)}
        okText="添加"
        cancelText="取消"
        width={500}
      >
        <Select
          mode="multiple"
          style={{ width: '100%' }}
          placeholder="搜索并选择联系人"
          value={selectedContactIds}
          onChange={setSelectedContactIds}
          options={allContacts.map((c) => ({ value: c.id, label: `${c.name} (${c.mobile})` }))}
          showSearch
          optionFilterProp="label"
        />
      </Modal>
    </Space>
  );
}
