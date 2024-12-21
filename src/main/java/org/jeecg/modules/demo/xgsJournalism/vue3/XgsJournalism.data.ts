import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '新闻标题',
    align:"center",
    dataIndex: 'journalismHead'
   },
   {
    title: '新闻内容',
    align:"center",
    dataIndex: 'journalismText',
   },
   {
    title: '新闻类型',
    align:"center",
    dataIndex: 'tyep_dictText'
   },
   {
    title: '新闻状态',
    align:"center",
    dataIndex: 'state_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '新闻标题',
    field: 'journalismHead',
    component: 'Input',
  },
  {
    label: '新闻内容',
    field: 'journalismText',
    component: 'JEditor',
  },
  {
    label: '新闻类型',
    field: 'tyep',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"jour_type"
     },
  },
  {
    label: '新闻状态',
    field: 'state',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"jour_state"
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
  journalismHead: {title: '新闻标题',order: 0,view: 'text', type: 'string',},
  journalismText: {title: '新闻内容',order: 1,view: 'umeditor', type: 'string',},
  tyep: {title: '新闻类型',order: 2,view: 'list', type: 'string',dictCode: 'jour_type',},
  state: {title: '新闻状态',order: 3,view: 'list', type: 'string',dictCode: 'jour_state',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}