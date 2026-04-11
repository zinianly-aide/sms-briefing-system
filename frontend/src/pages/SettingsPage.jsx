import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import { Button, Card, Form, Input, Modal, Popconfirm, Space, Table, message } from 'antd';
import { useEffect, useState } from 'react';
import { createConfig, deleteConfig, fetchConfigs, updateConfig } from '../api/config';

export default function SettingsPage() {
  const [configs, setConfigs] = useState([]);
  const [loading, setLoading] = useState(false);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form] = Form.useForm();

  const loadConfigs = async () => {
    try {
      setLoading(true);
      const data = await fetchConfigs();
      setConfigs(data || []);
    } catch (err) {
      message.error(err.message || '加载配置失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadConfigs();
  }, []);

  const openCreate = () => {
    setEditing(null);
    form.resetFields();
    setOpen(true);
  };

  const openEdit = (record) => {
    setEditing(record);
    form.setFieldsValue({ configKey: record.configKey, configValue: record.configValue, configDesc: record.configDesc });
    setOpen(true);
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();
      if (editing) {
        await updateConfig(editing.id, values);
        message.success('配置已更新');
      } else {
        await createConfig(values);
        message.success('配置已创建');
      }
      setOpen(false);
      loadConfigs();
    } catch (err) {
      if (err?.errorFields) return;
      message.error(err.message || '保存失败');
    }
  };

  const handleDelete = async (id) => {
    try {
      await deleteConfig(id);
      message.success('配置已删除');
      loadConfigs();
    } catch (err) {
      message.error(err.message || '删除失败');
    }
  };

  const columns = [
    { title: '配置键', dataIndex: 'configKey', width: 200 },
    { title: '配置值', dataIndex: 'configValue', width: 180 },
    { title: '说明', dataIndex: 'configDesc' },
    { title: '更新时间', dataIndex: 'updatedAt', width: 180 },
    {
      title: '操作',
      width: 120,
      render: (_, record) => (
        <Space>
          <Button type="link" size="small" icon={<EditOutlined />} onClick={() => openEdit(record)} disabled={!!editing && editing.id === record.id}>编辑</Button>
          <Popconfirm title="确认删除该配置？" onConfirm={() => handleDelete(record.id)}>
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
          <div className="toolbar-left" />
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>新建配置</Button>
        </div>
      </Card>
      <Card className="soft-card" bordered={false}>
        <Table rowKey="id" loading={loading} dataSource={configs} columns={columns} pagination={false} size="middle" />
      </Card>
      <Modal title={editing ? '编辑配置' : '新建配置'} open={open} onOk={handleSubmit} onCancel={() => setOpen(false)} destroyOnClose okText="确认" cancelText="取消">
        <Form form={form} layout="vertical">
          <Form.Item label="配置键" name="configKey" rules={[{ required: true, message: '请输入配置键' }]}>
            <Input placeholder="例如: sms_max_length" disabled={!!editing} />
          </Form.Item>
          <Form.Item label="配置值" name="configValue" rules={[{ required: true, message: '请输入配置值' }]}>
            <Input placeholder="例如: 70" />
          </Form.Item>
          <Form.Item label="说明" name="configDesc">
            <Input.TextArea rows={2} placeholder="配置说明" />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
