import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '招聘部门',
    align:"center",
    dataIndex: 'dept'
   },
   {
    title: '课题组',
    align:"center",
    dataIndex: 'ktz'
   },
   {
    title: '咨询电话',
    align:"center",
    dataIndex: 'telphone'
   },
   {
    title: '邮箱',
    align:"center",
    dataIndex: 'email'
   },
   {
    title: '岗位名称',
    align:"center",
    dataIndex: 'positionName'
   },
   {
    title: '研究方向',
    align:"center",
    dataIndex: 'researchDirection'
   },
   {
    title: '招聘人数',
    align:"center",
    dataIndex: 'personCount'
   },
   {
    title: '岗位职责',
    align:"center",
    dataIndex: 'duty'
   },
   {
    title: '学历学位',
    align:"center",
    dataIndex: 'xlxw'
   },
   {
    title: '专业',
    align:"center",
    dataIndex: 'professional'
   },
   {
    title: '工作年限',
    align:"center",
    dataIndex: 'workYears'
   },
   {
    title: '备注',
    align:"center",
    dataIndex: 'memo'
   },
   {
    title: '岗位状态',
    align:"center",
    dataIndex: 'status'
   },
   {
    title: '岗位分类',
    align:"center",
    dataIndex: 'category'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
	{
      label: "招聘部门",
      field: 'dept',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "课题组",
      field: 'ktz',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "岗位名称",
      field: 'positionName',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "学历学位",
      field: 'xlxw',
      component: 'Input',
      //colProps: {span: 6},
 	},
	{
      label: "专业",
      field: 'professional',
      component: 'Input',
      //colProps: {span: 6},
 	},
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '招聘部门',
    field: 'dept',
    component: 'Input',
  },
  {
    label: '课题组',
    field: 'ktz',
    component: 'Input',
  },
  {
    label: '咨询电话',
    field: 'telphone',
    component: 'Input',
  },
  {
    label: '邮箱',
    field: 'email',
    component: 'Input',
  },
  {
    label: '岗位名称',
    field: 'positionName',
    component: 'Input',
  },
  {
    label: '研究方向',
    field: 'researchDirection',
    component: 'Input',
  },
  {
    label: '招聘人数',
    field: 'personCount',
    component: 'Input',
  },
  {
    label: '岗位职责',
    field: 'duty',
    component: 'Input',
  },
  {
    label: '学历学位',
    field: 'xlxw',
    component: 'Input',
  },
  {
    label: '专业',
    field: 'professional',
    component: 'Input',
  },
  {
    label: '工作年限',
    field: 'workYears',
    component: 'Input',
  },
  {
    label: '备注',
    field: 'memo',
    component: 'Input',
  },
  {
    label: '岗位状态',
    field: 'status',
    component: 'Input',
  },
  {
    label: '岗位分类',
    field: 'category',
    component: 'Input',
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
  dept: {title: '招聘部门',order: 0,view: 'text', type: 'string',},
  ktz: {title: '课题组',order: 1,view: 'text', type: 'string',},
  telphone: {title: '咨询电话',order: 2,view: 'text', type: 'string',},
  email: {title: '邮箱',order: 3,view: 'text', type: 'string',},
  positionName: {title: '岗位名称',order: 4,view: 'text', type: 'string',},
  researchDirection: {title: '研究方向',order: 5,view: 'text', type: 'string',},
  personCount: {title: '招聘人数',order: 6,view: 'text', type: 'string',},
  duty: {title: '岗位职责',order: 7,view: 'text', type: 'string',},
  xlxw: {title: '学历学位',order: 8,view: 'text', type: 'string',},
  professional: {title: '专业',order: 9,view: 'text', type: 'string',},
  workYears: {title: '工作年限',order: 10,view: 'text', type: 'string',},
  memo: {title: '备注',order: 11,view: 'text', type: 'string',},
  status: {title: '岗位状态',order: 12,view: 'text', type: 'string',},
  category: {title: '岗位分类',order: 13,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}