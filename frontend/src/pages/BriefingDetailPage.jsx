import { ArrowLeftOutlined } from '@ant-design/icons';
import { Button, Card, Col, Descriptions, Row, Space, Tag, Timeline, Typography } from 'antd';

export default function BriefingDetailPage({ briefing }) {
  if (!briefing) {
    return <Typography.Text>请从简讯列表选择一条记录查看详情</Typography.Text>;
  }

  return (
    <Space direction="vertical" size={20} style={{ width: '100%' }}>
      <Button type="text" icon={<ArrowLeftOutlined />}>返回列表</Button>
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
              <Descriptions.Item label="更新时间">{briefing.updatedAt}</Descriptions.Item>
              <Descriptions.Item label="版本">{briefing.version}</Descriptions.Item>
              <Descriptions.Item label="发送对象">{briefing.audience}</Descriptions.Item>
            </Descriptions>
            <Card className="soft-card" title="简讯正文" style={{ marginTop: 16 }}>
              <Typography.Paragraph style={{ whiteSpace: 'pre-wrap' }}>{briefing.content}</Typography.Paragraph>
            </Card>
          </Col>
          <Col xs={24} xl={10}>
            <Card className="soft-card" title="审核轨迹">
              <Timeline
                items={briefing.reviewTrail.map((item) => ({
                  color: item.status === '完成' ? 'green' : item.status === '处理中' ? 'blue' : 'gray',
                  children: (
                    <div>
                      <Typography.Text strong>{item.step}</Typography.Text>
                      <br />
                      <Typography.Text type="secondary">{item.owner} · {item.time}</Typography.Text>
                    </div>
                  )
                }))}
              />
            </Card>
          </Col>
        </Row>
      </Card>
    </Space>
  );
}
