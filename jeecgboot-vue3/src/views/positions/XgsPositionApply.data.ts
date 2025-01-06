import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '申请人ID',
    align: "center",
    dataIndex: 'userId',
    ifShow: false
  },
  {
    title: '申请人姓名',
    align: "center",
    dataIndex: 'userName',
    ifShow: false,
  },
  {
    title: '简历ID',
    align: "center",
    dataIndex: 'resumeId',
    ifShow: false,
  },
  {
    title: '简历名称',
    align: "center",
    dataIndex: 'resumeName',
    ifShow: false,
  },
  {
    title: '岗位ID',
    align: "center",
    dataIndex: 'positionId',
    ifShow: false,
  },
  {
    title: '岗位名称',
    align: "center",
    dataIndex: 'positionName',
    ellipsis: true,
    width: 500,
  },
  {
    title: '岗位部门',
    align: "center",
    dataIndex: 'positionDept',
    ellipsis: true,
    width: 300,
  },
  {
    title: '岗位类型',
    align: "center",
    dataIndex: 'positionType',
    ellipsis: true,
    width: 120,
  },
  {
    title: '申请状态',
    align: "center",
    dataIndex: 'status',
    ellipsis: true,
    width: 120,
  },
  {
    title: '申请备注',
    align: "center",
    dataIndex: 'mark',
    ifShow: false,
  },
  {
    title: '申请标签',
    align: "center",
    dataIndex: 'tagIds',
    ifShow: false,
  },
  {
    title: '创建者',
    align: "center",
    dataIndex: 'creator',
    ifShow: false,
  },
  {
    title: '更新者',
    align: "center",
    dataIndex: 'updater',
    ifShow: false,
  },
  {
    title: '是否删除',
    align: "center",
    dataIndex: 'deleted',
    ifShow: false,
  },
  {
    title: '租户编号',
    align: "center",
    dataIndex: 'tenantId',
    ifShow: false,
  },
  {
    title: '',
    align: "center",
    dataIndex: '',
  },
];

// 高级查询数据
export const superQuerySchema = {
  // userName: {title: '申请人姓名',order: 1,view: 'text', type: 'string',},
  resumeName: {title: '简历名称',order: 3,view: 'text', type: 'string',},
  positionName: {title: '岗位名称',order: 5,view: 'text', type: 'string',},
  positionDept: {title: '岗位部门',order: 6,view: 'text', type: 'string',},
  positionType: {title: '岗位类型',order: 7,view: 'text', type: 'string',},
  status: {title: '申请状态',order: 8,view: 'number', type: 'number',},
  // tagIds: {title: '申请标签',order: 10,view: 'text', type: 'string',},
};
