import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '用户ID',
    align:"center",
    dataIndex: 'userId',
     ifShow: false,
   },
   {
    title: '用户姓名',
    align:"center",
    dataIndex: 'userName',
     ifShow: false,
   },
   {
    title: '职位ID',
    align:"center",
    dataIndex: 'positionId',
     ifShow: false,
   },
   {
    title: '职位名称',
    align:"center",
    dataIndex: 'positionName'
   },
   {
    title: '职位部门',
    align:"center",
    dataIndex: 'positionDept'
   },
   {
    title: '职位课题组',
    align:"center",
    dataIndex: 'positionKtz'
   },
   {
    title: '招聘人数',
    align:"center",
    dataIndex: 'positionCount'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '用户ID',
    field: 'userId',
    component: 'Input',
  },
  {
    label: '用户姓名',
    field: 'userName',
    component: 'Input',
  },
  {
    label: '职位ID',
    field: 'positionId',
    component: 'Input',
  },
  {
    label: '职位名称',
    field: 'positionName',
    component: 'Input',
  },
  {
    label: '职位部门',
    field: 'positionDept',
    component: 'Input',
  },
  {
    label: '职位课题组',
    field: 'positionKtz',
    component: 'Input',
  },
  {
    label: '招聘人数',
    field: 'positionCount',
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
  positionName: {title: '职位名称',order: 3,view: 'text', type: 'string',},
  positionDept: {title: '职位部门',order: 4,view: 'text', type: 'string',},
  positionKtz: {title: '职位课题组',order: 5,view: 'text', type: 'string',},
  positionCount: {title: '招聘人数',order: 6,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
