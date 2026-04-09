export const mockTemplates = [
  {
    id: 'tpl-001',
    name: '暴雨值班提醒',
    category: '应急通知',
    content: '【应急中心】今日18:00起启动暴雨值班，请相关同事保持电话畅通。',
    updatedAt: '2026-04-08 18:20',
    status: '启用中',
    owner: '行政运营'
  },
  {
    id: 'tpl-002',
    name: '日报发送模板',
    category: '常规播报',
    content: '【运营日报】今日新增客户{count}，重点跟进项目{project}。',
    updatedAt: '2026-04-07 09:15',
    status: '草稿',
    owner: '市场团队'
  },
  {
    id: 'tpl-003',
    name: '节假日服务通知',
    category: '客户通知',
    content: '【服务公告】节日期间值班安排如下，请留意服务窗口时间。',
    updatedAt: '2026-04-06 14:42',
    status: '启用中',
    owner: '客服中心'
  }
];

export const mockGroups = [
  {
    id: 'grp-001',
    name: '华东销售群',
    ownerDept: '销售中心',
    memberCount: 126,
    lastSyncTime: '2026-04-09 09:00',
    tags: ['销售', '华东', '核心群组'],
    status: '启用',
    syncMode: 'HR自动同步'
  },
  {
    id: 'grp-002',
    name: '总部值班群',
    ownerDept: '行政运营',
    memberCount: 32,
    lastSyncTime: '2026-04-09 08:30',
    tags: ['值班', '总部'],
    status: '启用',
    syncMode: '手工维护'
  },
  {
    id: 'grp-003',
    name: '华南客户通知组',
    ownerDept: '客户成功',
    memberCount: 218,
    lastSyncTime: '2026-04-08 20:10',
    tags: ['客户', '华南'],
    status: '停用',
    syncMode: 'CRM同步'
  }
];

export const mockSendRecords = [
  {
    id: 'task-001',
    title: '园区暴雨值班提醒',
    channel: '短信',
    plannedSendTime: '2026-04-09 16:00',
    status: '待发送',
    recipientCount: 86,
    creator: '王宁',
    successRate: '—',
    templateName: '暴雨值班提醒'
  },
  {
    id: 'task-002',
    title: '客户节假日通知',
    channel: '短信+企微',
    plannedSendTime: '2026-04-09 10:30',
    status: '已完成',
    recipientCount: 218,
    creator: '李青',
    successRate: '98.6%',
    templateName: '节假日服务通知'
  },
  {
    id: 'task-003',
    title: '市场日报播报',
    channel: '短信',
    plannedSendTime: '2026-04-10 09:00',
    status: '草稿',
    recipientCount: 42,
    creator: '周晨',
    successRate: '—',
    templateName: '日报发送模板'
  }
];

export const mockContacts = [
  {
    id: 'ct-001',
    name: '陈思远',
    phone: '138****1024',
    dept: '销售中心',
    group: '华东销售群',
    tags: ['核心销售', '上海'],
    source: 'HR同步',
    status: '有效'
  },
  {
    id: 'ct-002',
    name: '林嘉怡',
    phone: '139****5568',
    dept: '行政运营',
    group: '总部值班群',
    tags: ['值班', '总部'],
    source: '手工录入',
    status: '有效'
  },
  {
    id: 'ct-003',
    name: '赵可',
    phone: '137****8821',
    dept: '客户成功',
    group: '华南客户通知组',
    tags: ['客户', '广州'],
    source: 'CRM同步',
    status: '停用'
  }
];

export const mockBriefings = [
  {
    id: 'briefing-001',
    title: '园区暴雨值班提醒',
    status: '待审核',
    channel: '短信',
    author: '王宁',
    updatedAt: '2026-04-09 11:20',
    version: 'V1.3',
    audience: '总部值班群 / 华东销售群',
    content: '【应急中心】今日18:00起启动暴雨值班，请相关同事于17:30前完成到岗确认，并保持电话畅通。',
    reviewTrail: [
      { step: '草稿创建', owner: '王宁', time: '2026-04-09 10:40', status: '完成' },
      { step: '运营复核', owner: '李青', time: '2026-04-09 11:10', status: '处理中' },
      { step: '发送调度', owner: '系统', time: '待定', status: '未开始' }
    ]
  }
];
