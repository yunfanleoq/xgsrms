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
    dataIndex: 'userId'
  },
  {
    title: '申请人姓名',
    align: "center",
    dataIndex: 'userName'
  },
  {
    title: '简历ID',
    align: "center",
    dataIndex: 'resumeId'
  },
  {
    title: '简历名称',
    align: "center",
    dataIndex: 'resumeName'
  },
  {
    title: '岗位ID',
    align: "center",
    dataIndex: 'positionId'
  },
  {
    title: '岗位名称',
    align: "center",
    dataIndex: 'positionName'
  },
  {
    title: '岗位部门',
    align: "center",
    dataIndex: 'positionDept'
  },
  {
    title: '岗位类型',
    align: "center",
    dataIndex: 'positionType'
  },
  {
    title: '申请状态',
    align: "center",
    dataIndex: 'status'
  },
  {
    title: '申请备注',
    align: "center",
    dataIndex: 'mark'
  },
  {
    title: '申请标签',
    align: "center",
    dataIndex: 'tagIds'
  },
  {
    title: '创建者',
    align: "center",
    dataIndex: 'creator'
  },
  {
    title: '更新者',
    align: "center",
    dataIndex: 'updater'
  },
  {
    title: '是否删除',
    align: "center",
    dataIndex: 'deleted'
  },
  {
    title: '租户编号',
    align: "center",
    dataIndex: 'tenantId'
  },
];

// 高级查询数据
export const superQuerySchema = {
  userId: {title: '申请人ID',order: 0,view: 'text', type: 'string',},
  userName: {title: '申请人姓名',order: 1,view: 'text', type: 'string',},
  resumeId: {title: '简历ID',order: 2,view: 'text', type: 'string',},
  resumeName: {title: '简历名称',order: 3,view: 'text', type: 'string',},
  positionId: {title: '岗位ID',order: 4,view: 'text', type: 'string',},
  positionName: {title: '岗位名称',order: 5,view: 'text', type: 'string',},
  positionDept: {title: '岗位部门',order: 6,view: 'text', type: 'string',},
  positionType: {title: '岗位类型',order: 7,view: 'text', type: 'string',},
  status: {title: '申请状态',order: 8,view: 'number', type: 'number',},
  mark: {title: '申请备注',order: 9,view: 'text', type: 'string',},
  tagIds: {title: '申请标签',order: 10,view: 'text', type: 'string',},
  creator: {title: '创建者',order: 11,view: 'text', type: 'string',},
  updater: {title: '更新者',order: 12,view: 'text', type: 'string',},
  deleted: {title: '是否删除',order: 13,view: 'text', type: 'string',},
  tenantId: {title: '租户编号',order: 14,view: 'number', type: 'number',},
};
