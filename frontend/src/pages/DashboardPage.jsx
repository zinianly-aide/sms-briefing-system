import { Button, Card, Col, Empty, Form, Input, Row, Select, Space, Table, Tag, message } from 'antd';
import dayjs from 'dayjs';
import { useMemo } from 'react';
import { createTask } from '../api/dashboard';
import StatCard from '../components/StatCard';

export default function DashboardPage({ dashboard, onTaskCreated }) {
  const [form] = Form.useForm();
  const stats = useMemo(() => [
    { title: '联系人总量', value: dashboard?.totalContacts ?? 0, extra: '通讯录已汇聚 HR 数据与手工补录' },
    { title: '启用群组', value: dashboard?.activeGroups ?? 0, extra: '支持部门、标签、值班维度分组' },
    { title: '模板数量', value: dashboard?.templateCount ?? 0, extra: '覆盖预警、日报、通知等模板' },
    { title: '待发送任务', value: dashboard?.pendingTasks ?? 0, extra: '支持草稿、预约、立即发送流程' }
  ], [dashboard]);

  const handleFinish = async (values) => {
    try {
      const payload = {
        ...values,
        plannedSendTime: dayjs().add(2, 'hour').second(0).millisecond(0).format('YYYY-MM-DDTHH:mm:ss'),
        groupIds: values.groupIds,
        channel: values.channel,
        status: '待发送',
        recipientCount: 0,
        creator: '当前用户',
        successRate: '—'
      };
      await createTask(payload);
      message.success('发送任务已创建');
      form.resetFields();
      onTaskCreated?.();
    } catch (error) {
      message.error(error.message || '提交失败');
    }
  };

  const groupColumns = [
    { title: '群组名称', dataIndex: 'name' },
    { title: '归属部门', dataIndex: 'ownerDept' },
    { title: '成员数', dataIndex: 'memberCount' },
    { title: '最近同步', dataIndex: 'lastSyncTime', render: (v) => v || '-' },
    {
      title: '标签',
      dataIndex: 'tags',
      render: (tags) => tags ? <Space wrap>{tags.map((tag) => <Tag key={tag}>{tag}</Tag>)}</Space> : '-'
    }
  ];

  const templateColumns = [
    { title: '模板名称', dataIndex: 'name' },
    { title: '分类', dataIndex: 'category' },
    { title: '模板内容', dataIndex: 'content', ellipsis: true },
    { title: '更新时间', dataIndex: 'updatedAt', render: (v) => v || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      render: (status) => <Tag color={status === '启用中' ? 'green' : 'gold'}>{status}</Tag>
    }
  ];

  const taskColumns = [
    { title: '任务标题', dataIndex: 'title' },
    { title: '发送渠道', dataIndex: 'channel' },
    { title: '预约时间', dataIndex: 'plannedSendTime', render: (v) => v || '-' },
    {
      title: '状态',
      dataIndex: 'status',
      render: (status) => {
        const colorMap = { '已完成': 'green', '待发送': 'blue', '草稿': 'default' };
        return <Tag color={colorMap[status] || 'default'}>{status}</Tag>;
      }
    },
    { title: '覆盖人数', dataIndex: 'recipientCount' },
    { title: '创建人', dataIndex: 'creator' }
  ];

  if (!dashboard) {
    return <Empty description="暂无数据" />;
  }

  const hasGroups = dashboard.groups?.length > 0;
  const hasTemplates = dashboard.templates?.length > 0;
  const hasTasks = dashboard.tasks?.length > 0;

  return (
    <Space direction="vertical" size={20} style={{ width: '100%' }}>
      <Row gutter={[16, 16]}>
        {stats.map((item) => (
          <Col xs={24} md={12} xl={6} key={item.title}>
            <StatCard {...item} />
          </Col>
        ))}
      </Row>

      <Row gutter={[16, 16]}>
        <Col xs={24} xl={12}>
          <Card title="快速创建简讯任务" className="soft-card" bordered={false}>
            <Form layout="vertical" form={form} onFinish={handleFinish} initialValues={{ channel: '短信' }}>
              <Form.Item label="简讯标题" name="title" rules={[{ required: true, message: '请输入简讯标题' }]}>
                <Input placeholder="例如：园区暴雨值班提醒" />
              </Form.Item>
              <Form.Item label="发送内容" name="content" rules={[{ required: true, message: '请输入简讯内容' }]}>
                <Input.TextArea rows={4} showCount maxLength={300} placeholder="输入短信正文" />
              </Form.Item>
              <Form.Item label="目标群组" name="groupIds" rules={[{ required: true, message: '请选择目标群组' }]}>
                <Select mode="multiple" options={(dashboard.groups || []).map((g) => ({ value: g.id, label: g.name }))} placeholder="选择发送对象群组" />
              </Form.Item>
              <Form.Item label="发送渠道" name="channel">
                <Select options={[{ value: '短信', label: '短信' }, { value: '短信+企微', label: '短信 + 企微' }]} />
              </Form.Item>
              <Button type="primary" htmlType="submit">创建任务</Button>
            </Form>
          </Card>
        </Col>

        <Col xs={24} xl={12}>
          <Card title="平台概览" className="soft-card" bordered={false}>
            <Row gutter={[12, 12]}>
              <Col span={12}>
                <div className="quick-action-card">
                  <div style={{ fontSize: 20, fontWeight: 700, color: '#1677ff' }}>{dashboard.totalContacts ?? 0}</div>
                  <div style={{ fontSize: 13, color: '#595959' }}>联系人</div>
                </div>
              </Col>
              <Col span={12}>
                <div className="quick-action-card">
                  <div style={{ fontSize: 20, fontWeight: 700, color: '#52c41a' }}>{dashboard.activeGroups ?? 0}</div>
                  <div style={{ fontSize: 13, color: '#595959' }}>启用群组</div>
                </div>
              </Col>
              <Col span={12}>
                <div className="quick-action-card">
                  <div style={{ fontSize: 20, fontWeight: 700, color: '#fa8c16' }}>{dashboard.templateCount ?? 0}</div>
                  <div style={{ fontSize: 13, color: '#595959' }}>模板库</div>
                </div>
              </Col>
              <Col span={12}>
                <div className="quick-action-card">
                  <div style={{ fontSize: 20, fontWeight: 700, color: '#722ed1' }}>{dashboard.pendingTasks ?? 0}</div>
                  <div style={{ fontSize: 13, color: '#595959' }}>待发送任务</div>
                </div>
              </Col>
            </Row>
            <ul className="highlight-list" style={{ marginTop: 16 }}>
              <li>阶段 1-4 已完成：系统设计、工程搭建、主数据管理、简讯闭环</li>
              <li>当前阶段：全局联调、文档收口、体验完善</li>
              <li>支持短信 / 短信+企微双渠道发送</li>
            </ul>
          </Card>
        </Col>
      </Row>

      <Card title="群组列表" className="soft-card" bordered={false}>
        {hasGroups ? (
          <Table rowKey="id" pagination={false} dataSource={dashboard.groups} columns={groupColumns} size="middle" />
        ) : (
          <Empty description="暂无群组数据" />
        )}
      </Card>

      <Card title="模板中心" className="soft-card" bordered={false}>
        {hasTemplates ? (
          <Table rowKey="id" pagination={false} dataSource={dashboard.templates} columns={templateColumns} size="middle" />
        ) : (
          <Empty description="暂无模板数据" />
        )}
      </Card>

      <Card title="发送任务" className="soft-card" bordered={false}>
        {hasTasks ? (
          <Table rowKey="id" pagination={false} dataSource={dashboard.tasks} columns={taskColumns} size="middle" />
        ) : (
          <Empty description="暂无发送任务" />
        )}
      </Card>
    </Space>
  );
}
