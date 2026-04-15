export const CHANNEL_OPTIONS = [
  { value: 'sms', label: '短信' },
  { value: 'sms_wecom', label: '短信 + 企微' },
];

export const CHANNEL_LABELS = Object.fromEntries(CHANNEL_OPTIONS.map((item) => [item.value, item.label]));
export function getChannelLabel(channel) {
  return CHANNEL_LABELS[channel] || channel || '-';
}

export const CONTACT_STATUS_OPTIONS = [
  { value: 'active', label: '有效' },
  { value: 'inactive', label: '停用' },
];

export const CONTACT_STATUS_LABELS = Object.fromEntries(CONTACT_STATUS_OPTIONS.map((item) => [item.value, item.label]));
export const CONTACT_STATUS_COLORS = {
  active: 'green',
  inactive: 'default',
};
export function getContactStatusMeta(status) {
  return {
    label: CONTACT_STATUS_LABELS[status] || status || '-',
    color: CONTACT_STATUS_COLORS[status] || 'default',
  };
}

export const GROUP_STATUS_OPTIONS = [
  { value: 'enabled', label: '启用' },
  { value: 'disabled', label: '停用' },
];

export const GROUP_STATUS_LABELS = Object.fromEntries(GROUP_STATUS_OPTIONS.map((item) => [item.value, item.label]));
export const GROUP_STATUS_COLORS = {
  enabled: 'green',
  disabled: 'default',
};
export function getGroupStatusMeta(status) {
  return {
    label: GROUP_STATUS_LABELS[status] || status || '-',
    color: GROUP_STATUS_COLORS[status] || 'default',
  };
}

export const TEMPLATE_STATUS_OPTIONS = [
  { value: 'active', label: '启用中' },
  { value: 'draft', label: '草稿' },
];

export const TEMPLATE_STATUS_LABELS = Object.fromEntries(TEMPLATE_STATUS_OPTIONS.map((item) => [item.value, item.label]));
export const TEMPLATE_STATUS_COLORS = {
  active: 'green',
  draft: 'gold',
};
export function getTemplateStatusMeta(status) {
  return {
    label: TEMPLATE_STATUS_LABELS[status] || status || '-',
    color: TEMPLATE_STATUS_COLORS[status] || 'default',
  };
}

export const BRIEFING_STATUS_OPTIONS = [
  { value: 'draft', label: '草稿' },
  { value: 'pending_review', label: '待审核' },
  { value: 'pending_send', label: '待发送' },
  { value: 'sent', label: '已发送' },
];

export const BRIEFING_STATUS_LABELS = Object.fromEntries(BRIEFING_STATUS_OPTIONS.map((item) => [item.value, item.label]));
export const BRIEFING_STATUS_COLORS = {
  draft: 'default',
  pending_review: 'orange',
  pending_send: 'blue',
  sent: 'green',
};
export function getBriefingStatusMeta(status) {
  return {
    label: BRIEFING_STATUS_LABELS[status] || status || '-',
    color: BRIEFING_STATUS_COLORS[status] || 'default',
  };
}

export const TASK_STATUS_OPTIONS = [
  { value: 'draft', label: '草稿' },
  { value: 'pending', label: '待发送' },
  { value: 'sending', label: '发送中' },
  { value: 'completed', label: '已完成' },
  { value: 'partial_success', label: '部分成功' },
  { value: 'failed', label: '失败' },
  { value: 'cancelled', label: '已取消' },
];

export const TASK_STATUS_LABELS = Object.fromEntries(TASK_STATUS_OPTIONS.map((item) => [item.value, item.label]));
export const TASK_STATUS_COLORS = {
  draft: 'default',
  pending: 'blue',
  sending: 'processing',
  completed: 'green',
  partial_success: 'orange',
  failed: 'red',
  cancelled: 'default',
};
export function getTaskStatusMeta(status) {
  return {
    label: TASK_STATUS_LABELS[status] || status || '-',
    color: TASK_STATUS_COLORS[status] || 'default',
  };
}

export const TASK_EDITABLE_STATUSES = ['draft', 'pending'];

export const RECIPIENT_STATUS_LABELS = {
  pending: '待发送',
  success: '成功',
  failed: '失败',
};

export const RECIPIENT_STATUS_COLORS = {
  pending: 'default',
  success: 'green',
  failed: 'red',
};
export function getRecipientStatusMeta(status) {
  return {
    label: RECIPIENT_STATUS_LABELS[status] || status || '-',
    color: RECIPIENT_STATUS_COLORS[status] || 'default',
  };
}
