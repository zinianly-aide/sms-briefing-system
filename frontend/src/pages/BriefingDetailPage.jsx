import { ArrowLeftOutlined } from '@ant-design/icons';
import { Alert, Button, Card, Col, Descriptions, Row, Space, Spin, Tag, Typography } from 'antd';
import { useEffect, useState } from 'react';
import { fetchBriefing } from '../api/briefing';

const statusConfig = {
  '待审核': 'orange',
  '待发送': 'blue',
  '已发送': 'green',
  '草稿': 'default'
};

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
    return (
      <div className="loading-wrap">
        <Spin size="large" tip="加载中..." />
      </div>
    );
  }

  if (error) {
    return <Alert type="error" showIcon message={error} />;
  }

  if (!briefing) {
    return <Typography.Text>未找到对应简讯</Typography.Text>;
  }

  return (
    <Space direction="vertical" size={20} style={{ width: '100%' }}>
      <Button type="text" icon={<ArrowLeftOutlined />} onClick={onBack} style={{ padding: 0 }}>
        返回列表
      </Button>
      <Card className="soft-card" bordered={false} title={briefing.title}>
        <Row gutter={[24, 24]}>
          <Col xs={24} xl={14}>
            <Descriptions column={1} bordered size="small" labelStyle={{ width: 100, fontWeight: 500 }}>
              <Descriptions.Item label="简讯ID">{briefing.id}</Descriptions.Item>
              <Descriptions.Item label="状态">
                <Tag color={statusConfig[briefing.status] || 'default'}>{briefing.status}</Tag>
              </Descriptions.Item>
              <Descriptions.Item label="发送渠道">{briefing.channel || '-'}</Descriptions.Item>
              <Descriptions.Item label="作者">{briefing.author || '-'}</Descriptions.Item>
              <Descriptions.Item label="更新时间">{briefing.updatedAt || '-'}</Descriptions.Item>
              <Descriptions.Item label="版本">{briefing.version || '-'}</Descriptions.Item>
              <Descriptions.Item label="发送对象">{briefing.audience || '-'}</Descriptions.Item>
              {briefing.disasterType && <Descriptions.Item label="灾害类别">{briefing.disasterType}</Descriptions.Item>}
              {briefing.disasterLevel && <Descriptions.Item label="灾害级别">{briefing.disasterLevel}</Descriptions.Item>}
              {briefing.contentPart2 && (
                <Descriptions.Item label="补充内容">
                  <div className="sms-bubble">{briefing.contentPart2}</div>
                </Descriptions.Item>
              )}
              {briefing.remark && <Descriptions.Item label="说明">{briefing.remark}</Descriptions.Item>}
            </Descriptions>
            <Card
              className="soft-card"
              bordered={false}
              title="简讯正文"
              style={{ marginTop: 16 }}
              size="small"
            >
              <div className="sms-bubble">{briefing.content}</div>
            </Card>
          </Col>
          <Col xs={24} xl={10}>
            <Card className="soft-card" bordered={false} title="状态信息" size="small">
              <Space direction="vertical" size={12} style={{ width: '100%' }}>
                <div>
                  <Typography.Text type="secondary" style={{ fontSize: 13 }}>当前状态</Typography.Text>
                  <div style={{ fontSize: 16, fontWeight: 600, marginTop: 2 }}>
                    <Tag color={statusConfig[briefing.status] || 'default'} style={{ fontSize: 14, padding: '2px 8px' }}>{briefing.status}</Tag>
                  </div>
                </div>
                <div>
                  <Typography.Text type="secondary" style={{ fontSize: 13 }}>创建人</Typography.Text>
                  <div style={{ marginTop: 2 }}>{briefing.createdBy || briefing.author || '-'}</div>
                </div>
                <div>
                  <Typography.Text type="secondary" style={{ fontSize: 13 }}>关联模板</Typography.Text>
                  <div style={{ marginTop: 2 }}>{briefing.templateId || '未关联'}</div>
                </div>
                <div>
                  <Typography.Text type="secondary" style={{ fontSize: 13 }}>发送渠道</Typography.Text>
                  <div style={{ marginTop: 2 }}>{briefing.channel || '-'}</div>
                </div>
              </Space>
            </Card>
          </Col>
        </Row>
      </Card>
    </Space>
  );
}
