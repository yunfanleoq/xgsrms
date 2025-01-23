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
    title: '岗位数量',
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
  dataSource: [
    { reBizCode: '1', type: '转移登记', acceptBy: '张1三', acceptDate: '2019-01-22', curNode: '任务分派', flowRate: 1 },
    { reBizCode: '2', type: '抵押登记', acceptBy: '李四', acceptDate: '2019-01-23', curNode: '领导审核', flowRate: 30 },
    { reBizCode: '3', type: '转移登记', acceptBy: '王武', acceptDate: '2019-01-25', curNode: '任务处理', flowRate: 1 },
    { reBizCode: '4', type: '转移登记', acceptBy: '赵楼', acceptDate: '2019-11-22', curNode: '部门审核', flowRate: 80 },
    { reBizCode: '5', type: '转移登记', acceptBy: '钱就', acceptDate: '2019-12-12', curNode: '任务分派', flowRate: 90 },
    { reBizCode: '6', type: '转移登记', acceptBy: '孙吧', acceptDate: '2019-03-06', curNode: '任务处理', flowRate: 10 },
    { reBizCode: '7', type: '抵押登记', acceptBy: '周大', acceptDate: '2019-04-13', curNode: '任务分派', flowRate: 1 },
    { reBizCode: '8', type: '抵押登记', acceptBy: '吴二', acceptDate: '2019-05-09', curNode: '任务上报', flowRate: 50 },
    { reBizCode: '9', type: '抵押登记', acceptBy: '郑爽', acceptDate: '2019-07-12', curNode: '任务处理', flowRate: 63 },
    { reBizCode: '20', type: '抵押登记', acceptBy: '林有', acceptDate: '2019-12-12', curNode: '任务打回', flowRate: 59 },
    { reBizCode: '11', type: '转移登记', acceptBy: '码云', acceptDate: '2019-09-10', curNode: '任务签收', flowRate: 87 },
  ],
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
  dataSource: [
    { reBizCode: 'A001', type: '转移登记', acceptBy: '张四', acceptDate: '2019-01-22', curNode: '任务分派', flowRate: 12 },
    { reBizCode: 'A002', type: '抵押登记', acceptBy: '李吧', acceptDate: '2019-01-23', curNode: '任务签收', flowRate: 3 },
    { reBizCode: 'A003', type: '转移登记', acceptBy: '王三', acceptDate: '2019-01-25', curNode: '任务处理', flowRate: 24 },
    { reBizCode: 'A004', type: '转移登记', acceptBy: '赵二', acceptDate: '2019-11-22', curNode: '部门审核', flowRate: 10 },
    { reBizCode: 'A005', type: '转移登记', acceptBy: '钱大', acceptDate: '2019-12-12', curNode: '任务签收', flowRate: 8 },
    { reBizCode: 'A006', type: '转移登记', acceptBy: '孙就', acceptDate: '2019-03-06', curNode: '任务处理', flowRate: 10 },
    { reBizCode: 'A007', type: '抵押登记', acceptBy: '周晕', acceptDate: '2019-04-13', curNode: '部门审核', flowRate: 24 },
    { reBizCode: 'A008', type: '抵押登记', acceptBy: '吴有', acceptDate: '2019-05-09', curNode: '部门审核', flowRate: 30 },
    { reBizCode: 'A009', type: '抵押登记', acceptBy: '郑武', acceptDate: '2019-07-12', curNode: '任务分派', flowRate: 1 },
    { reBizCode: 'A0010', type: '抵押登记', acceptBy: '林爽', acceptDate: '2019-12-12', curNode: '部门审核', flowRate: 16 },
    { reBizCode: 'A0011', type: '转移登记', acceptBy: '码楼', acceptDate: '2019-09-10', curNode: '部门审核', flowRate: 7 },
  ],
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
  dataSource: [
    { sysOrgCode: '转移登记', dept: '张三', createTime: '2019-01-22', personCount: '任务分派', category: 60 },
    { sysOrgCode: '抵押登记', dept: '李四', createTime: '2019-01-23', personCount: '领导审核', category: 30 },
    { sysOrgCode: '转移登记', dept: '王武', createTime: '2019-01-25', personCount: '任务处理', category: 20 },
    { sysOrgCode: '转移登记', dept: '赵楼', createTime: '2019-11-22', personCount: '部门审核', category: 80 },
    { sysOrgCode: '转移登记', dept: '钱就', createTime: '2019-12-12', personCount: '任务分派', category: 90 },
    { sysOrgCode: '转移登记', dept: '孙吧', createTime: '2019-03-06', personCount: '任务处理', category: 10 },
    { sysOrgCode: '抵押登记', dept: '周大', createTime: '2019-04-13', personCount: '任务分派', category: 100 },
    { sysOrgCode: '抵押登记', dept: '吴二', createTime: '2019-05-09', personCount: '任务上报', category: 50 },
    { sysOrgCode: '抵押登记', dept: '郑爽', createTime: '2019-07-12', personCount: '任务处理', category: 63 },
    { sysOrgCode: '抵押登记', dept: '林有', createTime: '2019-12-12', personCount: '任务打回', category: 59 },
    { sysOrgCode: '转移登记', dept: '码云', createTime: '2019-09-10', personCount: '任务签收', category: 87 },
  ],
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
    }
  ],

  dataSource: [
    {
      reBizCode: 'A001',
      type: '转移登记',
      acceptBy: '张四',
      acceptDate: '2019-01-22'
    },
    {
      reBizCode: 'A002',
      type: '抵押登记',
      acceptBy: '李吧',
      acceptDate: '2019-01-23'
    },
    {
      reBizCode: 'A003',
      type: '转移登记',
      acceptBy: '王三',
      acceptDate: '2019-01-25'
    },
    {
      reBizCode: 'A004',
      type: '转移登记',
      acceptBy: '赵二',
      acceptDate: '2019-11-22'
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
}

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
      dataIndex: 'imterviewTime' ,
    },
    {
      title: '面试地点',
      align: 'center',
      dataIndex: 'imterviewPlace' ,
    }
  ],
  dataSource: [
    {
      positionName: '前端开发',
      deptName: '技术部',
      imterviewTime: '2019-01-22',
      imterviewPlace: 'office'
    },
    {
      positionName: '后端开发',
      deptName: '技术部',
      imterviewTime: '2019-01-23',
      imterviewPlace: 'office'
    },
    {
      positionName: '测试工程师',
      deptName: '技术部',
      imterviewTime: '2019-01-25',
      imterviewPlace: 'office'
    }
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
}


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
      dataIndex: 'applyTime' ,
    },
    {
      title: '申请状态',
      align: 'center',
      dataIndex: 'applyStatus' ,
    }
  ],
  dataSource: [
    {
      positionName: '前端开发',
      deptName: '技术部',
      applyTime: '2019-01-22',
      applyStatus: '已通过'
    },
    {
      positionName: '后端开发',
      deptName: '技术部',
      applyTime: '2019-01-23',
      applyStatus: '已通过'
    },
    {
      positionName: '测试工程师',
      deptName: '技术部',
      applyTime: '2019-01-25',
      applyStatus: '已通过'
    }
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
}
