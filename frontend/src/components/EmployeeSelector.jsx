import { SearchOutlined } from '@ant-design/icons';
import { Input, Modal, Select, Space, Table, Tag } from 'antd';
import { useEffect, useState } from 'react';
import { fetchDepartments, fetchEmployees } from '../api/contact';

export default function EmployeeSelector({ open, onOk, onCancel, selectedIds = [], multiple = true }) {
  const [keyword, setKeyword] = useState('');
  const [dept, setDept] = useState('');
  const [departments, setDepartments] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [pageSize] = useState(10);
  const [loading, setLoading] = useState(false);
  const [picked, setPicked] = useState(selectedIds);

  useEffect(() => {
    if (open) {
      fetchDepartments().then(setDepartments).catch(() => {});
      setPicked(selectedIds);
    }
  }, [open, selectedIds]);

  useEffect(() => {
    if (!open) return;
    setLoading(true);
    fetchEmployees({ keyword: keyword || undefined, dept: dept || undefined, page, pageSize })
      .then((data) => {
        setEmployees(data?.list || data || []);
        setTotal(data?.total || 0);
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [open, keyword, dept, page]);

  const safeDepartments = Array.isArray(departments) ? departments : [];
  const safeEmployees = Array.isArray(employees) ? employees : [];
  const safePicked = Array.isArray(picked) ? picked : [];

  const handleOk = () => {
    onOk?.(safePicked);
  };

  const rowSelection = multiple
    ? {
        selectedRowKeys: safePicked,
        onChange: (keys) => setPicked(keys),
        type: 'checkbox'
      }
    : null;

  return (
    <Modal
      title="选择员工"
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      width={680}
      okText="确认选择"
      cancelText="取消"
    >
      <Space style={{ marginBottom: 12 }}>
        <Input
          placeholder="搜索姓名/手机号"
          prefix={<SearchOutlined />}
          value={keyword}
          onChange={(e) => { setKeyword(e.target.value); setPage(1); }}
          style={{ width: 200 }}
          allowClear
        />
        <Select
          placeholder="部门筛选"
          style={{ width: 140 }}
          allowClear
          value={dept || undefined}
          onChange={(v) => { setDept(v || ''); setPage(1); }}
          options={safeDepartments.map((d) => ({ value: d, label: d }))}
        />
        {safePicked.length > 0 && <Tag color="blue">已选 {safePicked.length} 人</Tag>}
      </Space>
      <Table
        rowKey="id"
        loading={loading}
        dataSource={safeEmployees}
        rowSelection={rowSelection}
        size="small"
        pagination={{
          current: page,
          pageSize,
          total,
          onChange: setPage,
          showTotal: (t) => `共 ${t} 人`
        }}
        columns={[
          { title: '姓名', dataIndex: 'name', width: 100 },
          { title: '手机号', dataIndex: 'mobile', width: 130 },
          { title: '部门', dataIndex: 'department', width: 120 },
          { title: '职位', dataIndex: 'title', width: 120 }
        ]}
        scroll={{ y: 360 }}
      />
    </Modal>
  );
}
