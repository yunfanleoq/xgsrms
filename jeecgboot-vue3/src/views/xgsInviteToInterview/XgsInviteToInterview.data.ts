import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '职位id',
    align:"center",
    dataIndex: 'positionId'
   },
   {
    title: '职位名称',
    align:"center",
    dataIndex: 'positionName'
  },
   {
    title: '候选人id',
    align:"center",
    dataIndex: 'candidateId'
   },
   {
    title: '候选人',
    align:"center",
    dataIndex: 'candidate'
  },
   {
    title: '面试官id',
    align:"center",
    dataIndex: 'interviewerId'
   },
   {
    title: '面试官',
    align:"center",
    dataIndex: 'interviewer'
   },
   {
    title: '面试状态',
    align:"center",
    dataIndex: 'status_dictText'
   },
   {
    title: '面试时间',
    align:"center",
    dataIndex: 'interviewDate'
   },
   {
    title: '面试反馈',
    align:"center",
    dataIndex: 'interviewFeedback'
   },
   {
    title: '邀请状态',
    align:"center",
    dataIndex: 'inviteStatus_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "职位名称",
      field: 'positionName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "候选人",
      field: 'candidate',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "面试官",
      field: 'interviewer',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "面试状态",
      field: 'status',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"面试状态"
      },
      //colProps: {span: 6},
 	},
     {
      label: "面试时间",
      field: "interviewDate",
      component: 'RangePicker',
      componentProps: {
          valueType: 'Date',
          showTime:true
      },
      //colProps: {span: 6},
	},
	{
      label: "邀请状态",
      field: 'inviteStatus',
      component: 'JSelectMultiple',
      componentProps:{
          dictCode:"邀请面试状态"
      },
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '职位id',
    field: 'positionId',
    component: 'Input',
  },
  {
    label: '职位名称',
    field: 'positionName',
    component: 'Input',
    dynamicDisabled:true
  },
  {
    label: '候选人id',
    field: 'candidateId',
    component: 'Input',
  },
  {
    label: '候选人',
    field: 'candidate',
    component: 'Input',
  },
  {
    label: '面试官id',
    field: 'interviewerId',
    component: 'Input',
  },
  {
    label: '面试官',
    field: 'interviewer',
    component: 'Input',
  },
  {
    label: '面试状态',
    field: 'status',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"面试状态"
     },
  },
  {
    label: '面试时间',
    field: 'interviewDate',
    component: 'DatePicker',
    componentProps: {
       showTime: true,
       valueFormat: 'YYYY-MM-DD HH:mm:ss'
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入面试时间!'},
          ];
     },
  },
  {
    label: '面试反馈',
    field: 'interviewFeedback',
    component: 'Input',
  },
  {
    label: '邀请状态',
    field: 'inviteStatus',
    defaultValue: "待邀请",
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"邀请面试状态"
     },
    dynamicRules: ({model,schema}) => {
          return [
                 { required: true, message: '请输入邀请状态!'},
          ];
     },
  },
	// TODO 主键隐藏字段，目前写死为ID
	{
	  label: '',
	  field: 'id',
	  component: 'Input',
	  show: false
	},
];

// 高级查询数据
export const superQuerySchema = {
  positionId: {title: '职位id',order: 0,view: 'text', type: 'string',},
  positionName: {title: '职位名称',order: 1,view: 'text', type: 'string',},
  candidateId: {title: '候选人id',order: 2,view: 'text', type: 'string',},
  candidate: {title: '候选人',order: 3,view: 'text', type: 'string',},
  interviewerId: {title: '面试官id',order: 4,view: 'text', type: 'string',},
  interviewer: {title: '面试官',order: 5,view: 'text', type: 'string',},
  status: {title: '面试状态',order: 6,view: 'list', type: 'string',dictCode: '面试状态',},
  interviewDate: {title: '面试时间',order: 7,view: 'datetime', type: 'string',},
  interviewFeedback: {title: '面试反馈',order: 8,view: 'text', type: 'string',},
  inviteStatus: {title: '邀请状态',order: 9,view: 'list', type: 'string',dictCode: '邀请面试状态',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
