import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '招聘公告',
    align:"center",
    dataIndex: 'recruitAnnouncement',
   },
   {
    title: '新闻标题',
    align:"center",
    dataIndex: 'newTitle'
   },
   {
    title: '新闻',
    align:"center",
    dataIndex: 'news',
   },
   {
    title: '图片',
    align:"center",
    dataIndex: 'photograph',
   },
   {
    title: '招聘公告标题',
    align:"center",
    dataIndex: 'recruitAnnouncementTitle'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '招聘公告标题',
    field: 'recruitAnnouncementTitle',
    component: 'Input',
  },
  {
    label: '招聘公告',
    field: 'recruitAnnouncement',
    component: 'JEditor',
  },
  {
    label: '新闻标题',
    field: 'newTitle',
    component: 'Input',
  },
  {
    label: '新闻',
    field: 'news',
    component: 'JEditor',
  },
  {
    label: '图片',
    field: 'photograph',
    component: 'JEditor',
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
  recruitAnnouncement: {title: '招聘公告',order: 0,view: 'umeditor', type: 'string',},
  newTitle: {title: '新闻标题',order: 1,view: 'text', type: 'string',},
  news: {title: '新闻',order: 2,view: 'umeditor', type: 'string',},
  photograph: {title: '图片',order: 3,view: 'umeditor', type: 'string',},
  recruitAnnouncementTitle: {title: '招聘公告标题',order: 4,view: 'text', type: 'string',},
};

/**
* 流程表单调用这个方法获取formSchema
* @param param
*/
export function getBpmFormSchema(_formData): FormSchema[]{
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
