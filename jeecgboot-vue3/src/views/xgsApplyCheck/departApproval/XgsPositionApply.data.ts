import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
const positionTypes = [
  { label: '普通岗位', value: '普通岗位' },
  { label: '博士后岗位', value: '博士后岗位' },
  { label: '副高级以上岗位', value: '副高级以上岗位' },
  { label: '人才派遣岗位', value: '人才派遣岗位' },
];
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '申请人姓名',
    align: 'center',
    dataIndex: 'userName',
  },
  {
    title: '岗位名称',
    align: 'center',
    dataIndex: 'positionName',
  },
  {
    title: '岗位部门',
    align: 'center',
    dataIndex: 'positionDept',
  },
  {
    title: '岗位类型',
    align: 'center',
    dataIndex: 'positionType',
  },
  {
    title: '审批状态',
    align: 'center',
    dataIndex: 'approvalStatus',
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
  // {
  //   label: '岗位部门',
  //   field: 'positionDept',
  //   component: 'JSelectDept',
  //   // colProps: {span: 8},
  // },
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
    label: '申请人姓名',
    field: 'userName',
    component: 'Input',
  },
  {
    label: '岗位名称',
    field: 'positionName',
    component: 'Input',
  },
  {
    label: '岗位部门',
    field: 'positionDept',
    component: 'Input',
  },
  {
    label: '岗位类型',
    field: 'positionType',
    component: 'Input',
  },
  {
    label: '所属部门',
    field: 'sysOrgCode',
    component: 'Input',
  },
  {
    label: '申请状态',
    field: 'applyStatus',
    component: 'Input',
  },
  {
    label: '申请结果',
    field: 'applyResult',
    component: 'Input',
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
  userName: { title: '申请人姓名', order: 0, view: 'text', type: 'string' },
  positionName: { title: '岗位名称', order: 1, view: 'text', type: 'string' },
  positionDept: { title: '岗位部门', order: 2, view: 'text', type: 'string' },
  positionType: { title: '岗位类型', order: 3, view: 'text', type: 'string' },
  approvalNode: { title: '审批环节', order: 4, view: 'list', type: 'string', dictCode: '' },
  approvalStatus: { title: '审批状态', order: 5, view: 'list', type: 'string', dictCode: '' },
  sysOrgCode: { title: '所属部门', order: 6, view: 'text', type: 'string' },
  applyStatus: { title: '申请状态', order: 7, view: 'text', type: 'string' },
  applyResult: { title: '申请结果', order: 8, view: 'text', type: 'string' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
