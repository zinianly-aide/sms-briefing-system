import { Card, Statistic, Typography } from 'antd';

export default function StatCard({ title, value, suffix, extra }) {
  return (
    <Card className="soft-card">
      <Statistic title={title} value={value} suffix={suffix} />
      {extra ? <Typography.Text type="secondary">{extra}</Typography.Text> : null}
    </Card>
  );
}
