export interface GrowCardItem {
  icon: string;
  title: string;
  value?: number;
  total: number;
  color?: string;
  action?: string;
  footer?: string;
}

export const growCardList: GrowCardItem[] = [
  {
    title: '访问数',
    icon: 'visit-count|svg',
    value: 2000,
    total: 120000,
    color: 'green',
    action: '月',
  },
  {
    title: '成交额',
    icon: 'total-sales|svg',
    value: 20000,
    total: 500000,
    color: 'blue',
    action: '月',
  },
  {
    title: '下载数',
    icon: 'download-count|svg',
    value: 8000,
    total: 120000,
    color: 'orange',
    action: '周',
  },
  {
    title: '成交数',
    icon: 'transaction|svg',
    value: 5000,
    total: 50000,
    color: 'purple',
    action: '年',
  },
];
export const chartCardList: GrowCardItem[] = [
  {
    title: '岗位数量',
    icon: 'total-sales|svg',
    total: 560,
    value: 234.56,
    footer: '日均销售额',
  },
  {
    title: '招聘数量',
    icon: 'total-sales|svg',
    value: 1234,
    total: 8846,
    color: 'blue',
    footer: '日订单量',
  },
  {
    title: '审核数量',
    icon: 'total-sales|svg',
    value: 60,
    total: 6560,
    color: 'orange',
    footer: '转化率',
  },
  {
    title: '简历数量',
    icon: 'total-sales|svg',
    total: 78,
  },
];
export const bdcCardList: GrowCardItem[] = [
  {
    title: '申请数量',
    icon: 'ant-design:info-circle-outlined',
    total: 100,
    value: 60,
    footer: '今日受理量',
  },
  {
    title: '面试数量',
    icon: 'ant-design:info-circle-outlined',
    value: 54,
    total: 87,
    color: 'blue',
    footer: '今日办结量',
  },
  {
    title: '提交审核数量',
    icon: 'ant-design:info-circle-outlined',
    value: 13,
    total: 15,
    color: 'orange',
    footer: '用户今日受理量',
  },
  {
    title: '我的简历数量',
    icon: 'ant-design:info-circle-outlined',
    total: 9,
    value: 7,
    footer: '用户今日办结量',
  },
];

export const table = {
  dataSource: [],
  columns: [
    {
      title: '业务号',
      align: 'center',
      dataIndex: 'reBizCode',
    },
    {
      title: '业务类型',
      align: 'center',
      dataIndex: 'type',
    },
    {
      title: '受理人',
      align: 'center',
      dataIndex: 'acceptBy',
    },
    {
      title: '受理时间',
      align: 'center',
      dataIndex: 'acceptDate',
    },
    {
      title: '当前节点',
      align: 'center',
      dataIndex: 'curNode',
    },
    {
      title: '办理时长',
      align: 'center',
      dataIndex: 'flowRate',
    },
  ],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};
export const table1 = {
  dataSource: [],
  columns: [
    {
      title: '业务号',
      align: 'center',
      dataIndex: 'reBizCode',
    },
    {
      title: '受理人',
      align: 'center',
      dataIndex: 'acceptBy',
    },
    {
      title: '发起时间',
      align: 'center',
      dataIndex: 'acceptDate',
    },
    {
      title: '当前节点',
      align: 'center',
      dataIndex: 'curNode',
    },
    {
      title: '超时时间',
      align: 'center',
      dataIndex: 'flowRate',
    },
  ],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};

export const totalApple = {
  dataSource: [],
  columns: [
    {
      title: '所属部门',
      align: 'center',
      dataIndex: 'sysOrgCode',
    },
    {
      title: '招聘部门',
      align: 'center',
      dataIndex: 'dept',
    },
    {
      title: '创建日期',
      align: 'center',
      dataIndex: 'createTime',
    },
    {
      title: '招聘人数',
      align: 'center',
      dataIndex: 'personCount',
    },
    {
      title: '岗位分类',
      align: 'center',
      dataIndex: 'category',
    },
  ],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};

export const myResume = {
  columns: [
    {
      title: '简历编号',
      align: 'center',
      dataIndex: 'reBizCode',
    },
    {
      title: '简历类型',
      align: 'center',
      dataIndex: 'type',
    },
    {
      title: '简历状态',
      align: 'center',
      dataIndex: 'acceptBy',
    },
    {
      title: '简历时间',
      align: 'center',
      dataIndex: 'acceptDate',
    },
  ],
  dataSource: [],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};

export const myInterView = {
  columns: [
    {
      title: '岗位名称',
      align: 'center',
      dataIndex: 'positionName',
    },
    {
      title: '部门名称',
      align: 'center',
      dataIndex: 'deptName',
    },
    {
      title: '面试时间',
      align: 'center',
      dataIndex: 'imterviewTime',
    },
    {
      title: '面试地点',
      align: 'center',
      dataIndex: 'imterviewPlace',
    },
  ],
  dataSource: [],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};

export const myApply = {
  columns: [
    {
      title: '岗位名称',
      align: 'center',
      dataIndex: 'positionName',
    },
    {
      title: '部门名称',
      align: 'center',
      dataIndex: 'deptName',
    },
    {
      title: '申请时间',
      align: 'center',
      dataIndex: 'applyTime',
    },
    {
      title: '申请状态',
      align: 'center',
      dataIndex: 'applyStatus',
    },
  ],
  dataSource: [],
  ipagination: {
    current: 1,
    pageSize: 5,
    pageSizeOptions: ['10', '20', '30'],
    showTotal: (total, range) => {
      return range[0] + '-' + range[1] + ' 共' + total + '条';
    },
    showQuickJumper: true,
    showSizeChanger: true,
    total: 0,
  },
};
