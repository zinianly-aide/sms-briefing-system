import { ArrowLeftOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, Descriptions, Row, Space, Tag, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { fetchBriefing } from '../api/briefing';

export default function BriefingDetailPage({ briefingId, onBack }) {
  const [briefing, setBriefing] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!briefingId) return;
    const loadDetail = async () => {
      try {
        setLoading(true);
        const data = await fetchBriefing(briefingId);
        setBriefing(data);
        setError('');
      } catch (err) {
        setError(err.message || '加载简讯详情失败');
      } finally {
        setLoading(false);
      }
    };
    loadDetail();
  }, [briefingId]);

  if (!briefingId) {
    return <Typography.Text>请先选择一条简讯记录</Typography.Text>;
  }

  if (loading) {
    return <Typography.Text>加载中...</Typography.Text>;
  }

  if (error) {
    return <Alert type="error" showIcon message={error} />;
  }

  if (!briefing) {
    return <Typography.Text>未找到对应简讯</Typography.Text>;
  }

  return (
    <Space direction="vertical" size={20} style={{ width: '100%' }}>
      <Button type="text" icon={<ArrowLeftOutlined />} onClick={onBack}>返回编辑</Button>
      <Card className="soft-card" title={briefing.title}>
        <Row gutter={[24, 24]}>
          <Col xs={24} xl={14}>
            <Descriptions column={1} bordered size="small">
              <Descriptions.Item label="简讯ID">{briefing.id}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color="blue">{briefing.status}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="发送渠道">{briefing.channel}</Descriptions.Item>
              <Descriptions.Item label="作者">{briefing.author}</Descriptions.Item>
              <Descriptions.Item label="更新时间">{briefing.updatedAt || '-'}</Descriptions.Item>
              <Descriptions.Item label="版本">{briefing.version || '-'}</Descriptions.Item>
              <Descriptions.Item label="发送对象">{briefing.audience || '-'}</Descriptions.Item>
            </Descriptions>
            <Card className="soft-card" title="简讯正文" style={{ marginTop: 16 }}>
              <Typography.Paragraph style={{ whiteSpace: 'pre-wrap' }}>{briefing.content}</Typography.Paragraph>
            </Card>
          </Col>
          <Col xs={24} xl={10}>
            <Card className="soft-card" title="当前状态">
              <Typography.Paragraph>当前状态：{briefing.status}</Typography.Paragraph>
              <Typography.Paragraph>创建人：{briefing.createdBy || briefing.author || '-'}</Typography.Paragraph>
              <Typography.Paragraph>模板ID：{briefing.templateId || '-'}</Typography.Paragraph>
            </Card>
          </Col>
        </Row>
      </Card>
    </Space>
  );
}
