import { CheckCircleOutlined, WarningOutlined } from '@ant-design/icons';
import { Space, Table, Tag, Typography } from 'antd';

const { Text } = Typography;

export default function RecipientPreview({ recipients = [] }) {
  if (recipients.length === 0) {
    return <Text type="secondary">暂无接收人，请选择群组或手动添加</Text>;
  }

  const deduped = [];
  const seen = new Set();
  for (const r of recipients) {
    if (!seen.has(r.mobile)) {
      seen.add(r.mobile);
      deduped.push(r);
    }
  }

  const validCount = deduped.filter((r) => r.mobile && /^1[3-9]\d{9}$/.test(r.mobile)).length;
  const invalidCount = deduped.length - validCount;

  const columns = [
    { title: '姓名', dataIndex: 'name', width: 100, render: (v) => v || '-' },
    { title: '手机号', dataIndex: 'mobile', width: 140,
      render: (v) => {
        const valid = v && /^1[3-9]\d{9}$/.test(v);
        return valid ? v : <Text type="danger">{v || '无效'}</Text>;
      }
    },
    { title: '部门', dataIndex: 'department', width: 120, render: (v) => v || '-' },
    { title: '来源', dataIndex: 'source', width: 100, render: (v) => <Tag>{v || '手动'}</Tag> }
  ];

  return (
    <div>
      <Space style={{ marginBottom: 8 }}>
        <Tag icon={<CheckCircleOutlined />} color="green">有效 {validCount}</Tag>
        {invalidCount > 0 && <Tag icon={<WarningOutlined />} color="red">无效 {invalidCount}</Tag>}
        <Text type="secondary">去重后共 {deduped.length} 人</Text>
      </Space>
      <Table
        rowKey={(r) => r.mobile}
        dataSource={deduped}
        columns={columns}
        pagination={false}
        size="small"
        scroll={{ y: 240 }}
      />
    </div>
  );
}
