import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '审批人',
    align: 'center',
    dataIndex: 'approvalUser',
  },
  {
    title: '审批环节',
    align: 'center',
    dataIndex: 'approvalNode_dictText',
  },
  {
    title: '办理意见',
    align: 'center',
    dataIndex: 'opinions',
  },
  {
    title: '审批状态',
    align: 'center',
    dataIndex: 'approvalStatus_dictText',
  },
  {
    title: '业务ID',
    align: 'center',
    dataIndex: 'parentId',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: '审批人',
    field: 'approvalUser',
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: '审批环节',
    field: 'approvalNode',
    component: 'JSelectMultiple',
    componentProps: {},
    //colProps: {span: 6},
  },
  {
    label: '办理意见',
    field: 'opinions',
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: '审批状态',
    field: 'approvalStatus',
    component: 'JSelectMultiple',
    componentProps: {},
    //colProps: {span: 6},
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '审批人',
    field: 'approvalUser',
    component: 'Input',
  },
  {
    label: '审批环节',
    field: 'approvalNode',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: '',
    },
  },
  {
    label: '办理意见',
    field: 'opinions',
    component: 'InputTextArea',
  },
  {
    label: '审批状态',
    field: 'approvalStatus',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: '',
      type: 'radio',
    },
  },
  {
    label: '业务ID',
    field: 'parentId',
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
  approvalUser: { title: '审批人', order: 0, view: 'text', type: 'string' },
  approvalNode: { title: '审批环节', order: 1, view: 'list', type: 'string', dictCode: '' },
  opinions: { title: '办理意见', order: 2, view: 'textarea', type: 'string' },
  approvalStatus: { title: '审批状态', order: 3, view: 'radio', type: 'string', dictCode: '' },
  parentId: { title: '业务ID', order: 4, view: 'text', type: 'string' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
