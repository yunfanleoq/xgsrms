import {BasicColumn} from '/@/components/Table';
import {FormSchema} from '/@/components/Table';
import { rules} from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';

// 招聘公告列定义
export const announcementColumns: BasicColumn[] = [
  {
    title: '招聘公告标题',
    align: 'center',
    dataIndex: 'recruitAnnouncementTitle',
    width: 200,
  },
  {
    title: '招聘公告内容',
    align: 'center',
    dataIndex: 'recruitAnnouncement',
    width: 400,
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createTime',
    width: 180,
  },
];

// 新闻列定义
export const newsColumns: BasicColumn[] = [
  {
    title: '新闻标题',
    align: 'center',
    dataIndex: 'newTitle',
    width: 200,
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createTime',
    width: 180,
  },
];

// 兼容旧代码，默认使用招聘公告列
export const columns: BasicColumn[] = announcementColumns;

// 查询数据
export const searchFormSchema: FormSchema[] = [];

// 招聘公告表单定义
export const announcementFormSchema: FormSchema[] = [
  {
    label: '类型',
    field: 'newsType',
    component: 'Input',
    defaultValue: '招聘公告',
    show: false,
  },
  {
    label: '招聘公告标题',
    field: 'recruitAnnouncementTitle',
    component: 'Input',
    required: true,
  },
  {
    label: '招聘公告内容',
    field: 'recruitAnnouncement',
    component: 'JEditor',
    required: true,
  },
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];

// 新闻表单定义
export const newsFormSchema: FormSchema[] = [
  {
    label: '类型',
    field: 'newsType',
    component: 'Input',
    defaultValue: '新闻',
    show: false,
  },
  {
    label: '新闻标题',
    field: 'newTitle',
    component: 'Input',
    required: true,
  },
  {
    label: '新闻内容',
    field: 'news',
    component: 'JEditor',
    required: true,
  },
  {
    label: '新闻图片',
    field: 'images',
    component: 'JImageUpload',
    required: false,
  },
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];

// 兼容旧代码，默认使用招聘公告表单
export const formSchema: FormSchema[] = announcementFormSchema;

// 根据类型获取表单定义
export function getFormSchemaByType(newsType: string): FormSchema[] {
  return newsType === 'rczp' ? announcementFormSchema : newsFormSchema;
}

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
