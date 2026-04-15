import { Alert, Modal, Typography } from 'antd';
import { getChannelLabel } from '../constants/domain';
import RecipientPreview from './RecipientPreview';

const { Text, Paragraph } = Typography;

export default function ConfirmSendModal({ open, briefing, recipients = [], onConfirm, onCancel }) {
  if (!briefing) return null;

  const safeRecipients = Array.isArray(recipients) ? recipients : [];

  return (
    <Modal
      title="发送确认"
      open={open}
      onOk={onConfirm}
      onCancel={onCancel}
      okText="确认发送"
      cancelText="返回修改"
      width={600}
    >
      <div style={{ marginBottom: 16 }}>
        <Text strong>简讯标题：</Text>
        <Text>{briefing.title}</Text>
      </div>
      <div style={{ marginBottom: 16 }}>
        <Text strong>发送内容：</Text>
        <Paragraph style={{ background: '#f5f5f5', padding: 12, borderRadius: 6, marginTop: 4 }}>
          {briefing.content}
        </Paragraph>
      </div>
      {briefing.disasterType && (
        <div style={{ marginBottom: 16 }}>
          <Text strong>灾害信息：</Text>
          <Text>{briefing.disasterType} / {briefing.disasterLevel}</Text>
        </div>
      )}
      <div style={{ marginBottom: 16 }}>
        <Text strong>发送渠道：</Text>
        <Text>{getChannelLabel(briefing.channel)}</Text>
      </div>
      <div style={{ marginBottom: 16 }}>
        <Text strong>接收人列表：</Text>
      </div>
      <RecipientPreview recipients={safeRecipients} />
      {safeRecipients.length === 0 && (
        <Alert type="warning" showIcon message="尚未选择接收人，请返回添加" style={{ marginTop: 12 }} />
      )}
    </Modal>
  );
}
