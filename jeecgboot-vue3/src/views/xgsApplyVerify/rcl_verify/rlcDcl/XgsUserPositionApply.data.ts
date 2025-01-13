import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
import {usePermission} from "@/hooks/web/usePermission";
const positionTypes = [
  { label: '普通岗位', value: '普通岗位' },
  { label: '博士后岗位', value: '博士后岗位' },
  { label: '副高级以上岗位', value: '副高级以上岗位' },
  { label: '人才派遣岗位', value: '人才派遣岗位' },
];
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '申请人ID',
    align: 'center',
    dataIndex: 'userId',
    ifShow: true,
    width: '200px',
  },
  {
    title: '申请人姓名',
    align: 'center',
    dataIndex: 'userName',
    ifShow: true,
    width: '150px',
  },
  {
    title: '简历ID',
    align: 'center',
    dataIndex: 'resumeId',
    ifShow: false,
    width: '200px',
  },
  {
    title: '简历名称',
    align: 'center',
    dataIndex: 'resumeName',
    ifShow: false,
    width: '200px',
  },
  {
    title: '岗位ID',
    align: 'center',
    dataIndex: 'positionId',
    ifShow: false,
    width: '200px',
  },
  {
    title: '岗位名称',
    align: 'center',
    dataIndex: 'positionName',
    ellipsis: true,
    width: '200px',
  },
  {
    title: '岗位部门',
    align: 'center',
    dataIndex: 'positionDept',
    ellipsis: true,
    width: '200px',
  },
  {
    title: '岗位类型',
    align: 'center',
    dataIndex: 'positionType',
    ellipsis: true,
    width: '200px',
  },
  {
    title: '申请状态',
    align: 'center',
    dataIndex: 'status',
    ellipsis: true,
    // width: '150px',
  },
  {
    title: '申请备注',
    align: 'center',
    dataIndex: 'mark',
    ifShow: false,
  },
  {
    title: '申请标签',
    align: 'center',
    dataIndex: 'tagIds',
    ifShow: false,
  },
  {
    title: '创建者',
    align: 'center',
    dataIndex: 'creator',
    ifShow: false,
  },
  {
    title: '更新者',
    align: 'center',
    dataIndex: 'updater',
    ifShow: false,
  },
  {
    title: '是否删除',
    align: 'center',
    dataIndex: 'deleted',
    ifShow: false,
  },
  {
    title: '租户编号',
    align: 'center',
    dataIndex: 'tenantId',
    ifShow: false,
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: '岗位名称',
    field: 'positionName',
    component: 'Input',
    colProps: {span: 7},

  },
  {
    label: '岗位部门',
    field: 'positionDept',
    component: 'Input',
    // colProps: {span: 8},
    componentProps: {},
  },
  {
    label: '岗位类型',
    field: 'positionType',
    component: 'Select',
    // colProps: {span: 8},
    componentProps: {
      options: positionTypes,
    },
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '申请人ID',
    field: 'userId',
    component: 'Input',
    show: false,
  },
  {
    label: '申请人姓名',
    field: 'userName',
    component: 'Input',
    show: false,
  },
  {
    label: '简历ID',
    field: 'resumeId',
    component: 'Input',
    show: false,
  },
  {
    label: '简历名称',
    field: 'resumeName',
    component: 'Input',
    show: false,
  },
  {
    label: '岗位ID',
    field: 'positionId',
    component: 'Input',
    show: false,
  },
  {
    label: '岗位名称',
    field: 'positionName',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '岗位部门',
    field: 'positionDept',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '岗位类型',
    field: 'positionType',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '申请状态',
    field: 'status',
    component: 'Input',
    dynamicDisabled: true,
    show: false,
  },
  {
    label: '申请备注',
    field: 'mark',
    component: 'Input',
    show: false,
  },
  {
    label: '申请标签',
    field: 'tagIds',
    component: 'Input',
    show: false,
  },
  {
    label: '创建者',
    field: 'creator',
    component: 'Input',
    show: false,
  },
  {
    label: '更新者',
    field: 'updater',
    component: 'Input',
    show: false,
  },
  {
    label: '是否删除',
    field: 'deleted',
    component: 'Input',
    show: false,
  },
  {
    label: '租户编号',
    field: 'tenantId',
    component: 'InputNumber',
    show: false,
  },
  // TODO 主键隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];

// 高级查询数据
export const superQuerySchema = {
  positionName: { title: '岗位名称', order: 5, view: 'text', type: 'string' },
  positionDept: { title: '岗位部门', order: 6, view: 'text', type: 'string' },
  positionType: { title: '岗位类型', order: 7, view: 'text', type: 'string' },
  status: { title: '申请状态', order: 8, view: 'text', type: 'string' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  console.log('forData', _formData);
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
