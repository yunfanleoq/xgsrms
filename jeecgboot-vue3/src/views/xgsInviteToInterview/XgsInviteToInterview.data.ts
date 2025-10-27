import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '申请人姓名',
    align: 'center',
    dataIndex: 'candidate',
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
    title: '面试状态',
    align: 'center',
    dataIndex: 'interviewStatus',
    ifShow: false,
  },
  {
    title: '面试时间',
    align: 'center',
    dataIndex: 'interviewDate',
  },
  {
    title: '面试地点',
    align: 'center',
    dataIndex: 'interviewPlace',
  },
  {
    title: '邀请信息',
    align: 'center',
    dataIndex: 'interviewInformation',
    customRender: ({ text, record }) => text ?? record.inviteLetter,
  },
  {
    title: '邀请状态',
    align: 'center',
    dataIndex: 'inviteStatus',
  },
  {
    title: '是否接受邀请',
    align: 'center',
    dataIndex: 'inviteResult',
  },
  {
    title: '面试结果',
    align: 'center',
    dataIndex: 'interviewResult',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
  {
    label: '申请人姓名',
    field: 'userName',
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: '岗位名称',
    field: 'positionName',
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: '岗位部门',
    field: 'positionDept',
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: '岗位类型',
    field: 'positionType',
    component: 'Input',
    //colProps: {span: 6},
  },
  {
    label: '面试状态',
    field: 'interviewStatus',
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: '面试状态',
    },
    //colProps: {span: 6},
  },
  {
    label: '面试时间',
    field: 'interviewDate',
    component: 'RangePicker',
    componentProps: {
      valueType: 'Date',
      showTime: true,
    },
    //colProps: {span: 6},
  },
  {
    label: '邀请状态',
    field: 'inviteStatus',
    component: 'JSelectMultiple',
    componentProps: {
      options: [
        { label: '接受邀请', value: '接受邀请' },
        { label: '拒绝邀请', value: '拒绝邀请' },
        { label: '已发送邀请', value: '已发送邀请' },
        { label: '待邀请', value: '待邀请' },
      ],
    },
    //colProps: {span: 6},
  },
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '申请人姓名',
    field: 'candidate',
    component: 'Input',
    dynamicDisabled: true,
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
    label: '面试状态',
    field: 'interviewStatus',
    component: 'JSelectMultiple',
    componentProps: {
      dictCode: '面试状态',
    },
    ifShow: false,
  },
  {
    label: '面试时间',
    field: 'interviewDate',
    component: 'DatePicker',
    componentProps: {
      showTime: { format: 'HH' },
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
    },
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请输入面试时间!' }];
    },
  },
  {
    label: '面试地点',
    field: 'interviewPlace',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请输入面试地点!' }];
    },
  },
  {
    label: '邀请信息',
    field: 'interviewInformation',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: true, message: '请输邀请信息!' }];
    },
  },
  {
    label: '邀请状态',
    field: 'inviteStatus',
    defaultValue: '待邀请',
    component: 'JDictSelectTag',
    componentProps: {
      // dictCode: '邀请面试状态',
      options: [
        // { label: '接受邀请', value: '接受邀请' },
        // { label: '拒绝邀请', value: '拒绝邀请' },
        { label: '已发送邀请', value: '已发送邀请' },
        { label: '待邀请', value: '待邀请' },
      ],
    },
    ifShow: false,
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
  interviewStatus: { title: '面试状态', order: 6, view: 'list', type: 'string', dictCode: '面试状态' },
  interviewDate: { title: '面试时间', order: 7, view: 'datetime', type: 'string' },
  inviteStatus: { title: '邀请状态', order: 9, view: 'list', type: 'string', dictCode: '邀请面试状态' },
  interviewInformation: { title: '面试信息', order: 8, view: 'text', type: 'string' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
