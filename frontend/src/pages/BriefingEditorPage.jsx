import { ArrowLeftOutlined, FileTextOutlined, SendOutlined, TeamOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, DatePicker, Form, Input, InputNumber, Progress, Row, Select, Space, Typography, message } from 'antd';
import dayjs from 'dayjs';
import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createBriefing } from '../api/briefing';
import { fetchGroupMembers, fetchGroups } from '../api/group';
import { fetchTemplates } from '../api/template';
import ConfirmSendModal from '../components/ConfirmSendModal';
import RecipientPreview from '../components/RecipientPreview';
import { BRIEFING_STATUS_OPTIONS, CHANNEL_OPTIONS } from '../constants/domain';
import { useAuth } from '../context/AuthContext';

export default function BriefingEditorPage() {
  const navigate = useNavigate();
  const { displayName } = useAuth();
  const [form] = Form.useForm();
  const [preview, setPreview] = useState('');
  const [groups, setGroups] = useState([]);
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [confirmModal, setConfirmModal] = useState(false);
  const [recipients, setRecipients] = useState([]);
  const [previewData, setPreviewData] = useState(null);
  const scheduleType = Form.useWatch('scheduleType', form);
  const selectedGroupIds = Form.useWatch('groupIds', form);
  const briefingStatusByScheduleType = {
    immediate: BRIEFING_STATUS_OPTIONS.find((item) => item.value === 'pending_send')?.value || 'pending_send',
    scheduled: BRIEFING_STATUS_OPTIONS.find((item) => item.value === 'pending_review')?.value || 'pending_review',
    recurring: BRIEFING_STATUS_OPTIONS.find((item) => item.value === 'pending_review')?.value || 'pending_review',
  };

  useEffect(() => {
    const loadOptions = async () => {
      try {
        const [groupData, templateData] = await Promise.all([fetchGroups(), fetchTemplates()]);
        setGroups(groupData?.list || groupData || []);
        setTemplates(templateData?.list || templateData || []);
      } catch (err) {
        setError(err.message || '加载选项失败');
      }
    };
    loadOptions();
  }, []);

  const safeGroups = Array.isArray(groups) ? groups : [];
  const safeTemplates = Array.isArray(templates) ? templates : [];
  const safeRecipients = Array.isArray(recipients) ? recipients : [];

  const handleTemplateSelect = (templateId) => {
    const tpl = safeTemplates.find((t) => t.id === templateId);
    if (tpl) {
      form.setFieldsValue({ content: tpl.content });
      setPreview(tpl.content);
    }
  };

  const resolveRecipients = useCallback(async (groupIds) => {
    const allMembers = [];
    for (const gid of groupIds) {
      try {
        const members = await fetchGroupMembers(gid);
        for (const m of (members || [])) {
          allMembers.push({ name: m.contactName, mobile: m.contactMobile, department: m.contactDepartment, source: safeGroups.find((g) => g.id === gid)?.name || '群组' });
        }
      } catch { /* skip failed groups */ }
    }
    return allMembers;
  }, [safeGroups]);

  useEffect(() => {
    if (!selectedGroupIds || selectedGroupIds.length === 0) {
      setRecipients([]);
      return;
    }
    resolveRecipients(selectedGroupIds).then(setRecipients).catch(() => setRecipients([]));
  }, [selectedGroupIds, resolveRecipients]);

  const handleFinish = async (values) => {
    setPreviewData(values);
    setConfirmModal(true);
  };

  const handleConfirmSend = async () => {
    try {
      setLoading(true);
      const values = previewData;
      const created = await createBriefing({
        title: values.title,
        content: values.content,
        templateId: values.templateId,
        status: briefingStatusByScheduleType[values.scheduleType] || 'pending_send',
        channel: values.channel,
        author: displayName,
        version: 'V1.0',
        audience: (values.groupIds || []).join(','),
        createdBy: displayName,
        disasterType: values.disasterType || null,
        disasterLevel: values.disasterLevel || null,
        contentPart2: values.contentPart2 || null,
        remark: values.remark || null,
        legacyPayload: JSON.stringify({
          scheduleType: values.scheduleType || 'immediate',
          scheduledTime: values.scheduledTime ? dayjs(values.scheduledTime).format('YYYY-MM-DDTHH:mm:ss') : null,
          recurrenceInterval: values.recurrenceInterval || null,
          recurrenceUnit: values.recurrenceUnit || null,
          recurrenceEndTime: values.recurrenceEndTime ? dayjs(values.recurrenceEndTime).format('YYYY-MM-DDTHH:mm:ss') : null,
          recurrenceMaxCount: values.recurrenceMaxCount || null,
          groupIds: values.groupIds || [],
        }),
      });
      message.success('简讯已提交');
      setConfirmModal(false);
      form.resetFields();
      setPreview('');
      setRecipients([]);
      if (created?.id) {
        navigate(`/briefings/${created.id}`);
      } else {
        navigate('/briefings');
      }
    } catch (err) {
      message.error(err.message || '提交简讯失败');
    } finally {
      setLoading(false);
    }
  };

  const charCount = preview.length;
  const charPercent = Math.round((charCount / 500) * 100);

  return (
    <Space direction="vertical" size={20} style={{ width: '100%' }}>
      <Button type="text" icon={<ArrowLeftOutlined />} onClick={() => navigate('/briefings')} style={{ padding: 0 }}>
        返回简讯列表
      </Button>

      <Row gutter={[24, 24]}>
        <Col xs={24} xl={14}>
          {error ? <Alert type="error" showIcon message={error} style={{ marginBottom: 16 }} /> : null}
          <Card className="soft-card" bordered={false} title="简讯编辑器">
            <Form form={form} layout="vertical" onFinish={handleFinish}>
              <Form.Item label="简讯标题" name="title" rules={[{ required: true, message: '请输入标题' }]}>
                <Input placeholder="例如：园区暴雨值班提醒" />
              </Form.Item>
              <Form.Item label="选择模板" name="templateId">
                <Select
                  allowClear
                  placeholder="可选：从模板库快速载入"
                  options={safeTemplates.map((t) => ({ value: t.id, label: t.name }))}
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
                  options={safeGroups.map((g) => ({ value: g.id, label: g.name }))}
                />
              </Form.Item>
              <Form.Item label="灾害类别" name="disasterType">
                <Select
                  allowClear
                  placeholder="可选：灾害类别"
                  options={[
                    { value: '暴雨', label: '暴雨' },
                    { value: '台风', label: '台风' },
                    { value: '洪水', label: '洪水' },
                    { value: '地震', label: '地震' },
                    { value: '火灾', label: '火灾' },
                    { value: '泥石流', label: '泥石流' },
                    { value: '其他', label: '其他' },
                  ]}
                />
              </Form.Item>
              <Form.Item label="灾害级别" name="disasterLevel">
                <Select
                  allowClear
                  placeholder="可选：灾害级别"
                  options={[
                    { value: 'Ⅰ级（特别重大）', label: 'Ⅰ级（特别重大）' },
                    { value: 'Ⅱ级（重大）', label: 'Ⅱ级（重大）' },
                    { value: 'Ⅲ级（较大）', label: 'Ⅲ级（较大）' },
                    { value: 'Ⅳ级（一般）', label: 'Ⅳ级（一般）' },
                  ]}
                />
              </Form.Item>
              <Form.Item label="补充内容" name="contentPart2">
                <Input.TextArea rows={4} maxLength={1000} placeholder="可选：补充内容" />
              </Form.Item>
              <Form.Item label="发送渠道" name="channel" initialValue="sms">
                <Select options={CHANNEL_OPTIONS} />
              </Form.Item>
              <Form.Item label="调度方式" name="scheduleType" initialValue="immediate">
                <Select options={[
                  { value: 'immediate', label: '立即发送' },
                  { value: 'scheduled', label: '预约发送' },
                  { value: 'recurring', label: '定时循环发送' },
                ]} />
              </Form.Item>
              {(scheduleType === 'scheduled' || scheduleType === 'recurring') && (
                <Form.Item label={scheduleType === 'recurring' ? '首次发送时间' : '预约时间'} name="scheduledTime" rules={[{ required: true, message: '请选择发送时间' }]}>
                  <DatePicker showTime style={{ width: '100%' }} placeholder={scheduleType === 'recurring' ? '选择首次发送时间' : '选择预约发送时间'} />
                </Form.Item>
              )}
              {scheduleType === 'recurring' && (
                <>
                  <Row gutter={12}>
                    <Col span={8}>
                      <Form.Item label="循环间隔" name="recurrenceInterval" initialValue={1} rules={[{ required: true, message: '请输入循环间隔' }]}>
                        <InputNumber min={1} style={{ width: '100%' }} placeholder="例如 1" />
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item label="循环单位" name="recurrenceUnit" initialValue="day" rules={[{ required: true, message: '请选择循环单位' }]}>
                        <Select options={[
                          { value: 'hour', label: '小时' },
                          { value: 'day', label: '天' },
                          { value: 'week', label: '周' },
                          { value: 'month', label: '月' },
                        ]} />
                      </Form.Item>
                    </Col>
                    <Col span={8}>
                      <Form.Item label="最大次数" name="recurrenceMaxCount">
                        <InputNumber min={1} style={{ width: '100%' }} placeholder="可选" />
                      </Form.Item>
                    </Col>
                  </Row>
                  <Form.Item label="循环结束时间" name="recurrenceEndTime">
                    <DatePicker showTime style={{ width: '100%' }} placeholder="可选：到此时间后停止发送" />
                  </Form.Item>
                </>
              )}
              <Form.Item label="说明" name="remark">
                <Input.TextArea rows={2} maxLength={500} placeholder="可选：备注说明" />
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
          <Card className="soft-card" bordered={false} title="内容预览">
            <div className="preview-box">
              <div style={{ fontSize: 12, color: '#8c8c8c', marginBottom: 4 }}>短信预览</div>
              <div className="preview-content">
                {preview || <span style={{ color: '#bfbfbf' }}>编辑内容后在此预览</span>}
              </div>
            </div>
            <div style={{ marginTop: 20 }}>
              <div style={{ fontSize: 13, color: '#8c8c8c', marginBottom: 8 }}>字数统计</div>
              <Progress
                percent={charPercent}
                format={() => `${charCount} / 500`}
                strokeColor={charPercent > 80 ? '#ff4d4f' : '#1677ff'}
                size="small"
              />
            </div>
          </Card>
          <Card className="soft-card" bordered={false} title={<><TeamOutlined /> 接收人预览</>} style={{ marginTop: 16 }}>
            <RecipientPreview recipients={safeRecipients} />
          </Card>
          <ConfirmSendModal
            open={confirmModal}
            briefing={previewData}
            recipients={safeRecipients}
            onConfirm={handleConfirmSend}
            onCancel={() => setConfirmModal(false)}
          />
        </Col>
      </Row>
    </Space>
  );
}
