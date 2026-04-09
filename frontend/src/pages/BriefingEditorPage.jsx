import { FileTextOutlined, SendOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, Form, Input, Row, Select, Space, Typography, message } from 'antd';
import { useEffect, useState } from 'react';
import { createBriefing } from '../api/briefing';
import { fetchGroups } from '../api/group';
import { fetchTemplates } from '../api/template';

export default function BriefingEditorPage() {
  const [form] = Form.useForm();
  const [preview, setPreview] = useState('');
  const [groups, setGroups] = useState([]);
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const loadOptions = async () => {
      try {
        const [groupData, templateData] = await Promise.all([fetchGroups(), fetchTemplates()]);
        setGroups(groupData || []);
        setTemplates(templateData || []);
      } catch (err) {
        setError(err.message || '加载选项失败');
      }
    };
    loadOptions();
  }, []);

  const handleTemplateSelect = (templateId) => {
    const tpl = templates.find((t) => t.id === templateId);
    if (tpl) {
      form.setFieldsValue({ content: tpl.content });
      setPreview(tpl.content);
    }
  };

  const handleFinish = async (values) => {
    try {
      setLoading(true);
      await createBriefing({
        title: values.title,
        content: values.content,
        templateId: values.templateId,
        status: values.scheduleType === '立即' ? '待发送' : '待审核',
        channel: values.channel,
        author: '当前用户',
        version: 'V1.0',
        audience: (values.groupIds || []).join(','),
        createdBy: '当前用户'
      });
      message.success('简讯已提交');
      form.resetFields();
      setPreview('');
    } catch (err) {
      message.error(err.message || '提交简讯失败');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Row gutter={[24, 24]}>
      <Col xs={24} xl={14}>
        {error ? <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} /> : null}
        <Card className="soft-card" title="简讯编辑器">
          <Form form={form} layout="vertical" onFinish={handleFinish}>
            <Form.Item label="简讯标题" name="title" rules={[{ required: true, message: '请输入标题' }]}>
              <Input placeholder="例如：园区暴雨值班提醒" />
            </Form.Item>
            <Form.Item label="选择模板" name="templateId">
              <Select
                allowClear
                placeholder="可选：从模板库快速载入"
                options={templates.map((t) => ({ value: t.id, label: t.name }))}
                onChange={handleTemplateSelect}
              />
            </Form.Item>
            <Form.Item label="发送内容" name="content" rules={[{ required: true, message: '请输入内容' }]}>
              <Input.TextArea
                rows={8}
                showCount
                maxLength={500}
                placeholder="输入短信正文，建议控制在 70 字以内"
                onChange={(e) => setPreview(e.target.value)}
              />
            </Form.Item>
            <Form.Item label="目标群组" name="groupIds" rules={[{ required: true, message: '请选择群组' }]}>
              <Select
                mode="multiple"
                placeholder="选择发送对象"
                options={groups.map((g) => ({ value: g.id, label: g.name }))}
              />
            </Form.Item>
            <Form.Item label="发送渠道" name="channel" initialValue="短信">
              <Select options={[{ value: '短信', label: '短信' }, { value: '短信+企微', label: '短信 + 企微' }]} />
            </Form.Item>
            <Form.Item label="调度方式" name="scheduleType" initialValue="立即">
              <Select options={[{ value: '立即', label: '立即发送' }, { value: '预约', label: '预约发送' }]} />
            </Form.Item>
            <Space>
              <Button type="primary" htmlType="submit" icon={<SendOutlined />} loading={loading}>
                提交审核
              </Button>
              <Button icon={<FileTextOutlined />}>保存草稿</Button>
            </Space>
          </Form>
        </Card>
      </Col>
      <Col xs={24} xl={10}>
        <Card className="soft-card" title="内容预览">
          <div className="preview-box">
            <Typography.Text type="secondary">短信预览</Typography.Text>
            <div className="preview-content">
              {preview || <span style={{ color: '#bfbfbf' }}>编辑内容后在此预览</span>}
            </div>
          </div>
          <div style={{ marginTop: 16 }}>
            <Typography.Text type="secondary">字数统计</Typography.Text>
            <Typography.Title level={4}>{preview.length} / 500</Typography.Title>
          </div>
        </Card>
      </Col>
    </Row>
  );
}
