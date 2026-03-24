import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '概况类型',
    align: 'center',
    dataIndex: 'type_dictText',
  },
  {
    title: '图片附文',
    align: 'center',
    dataIndex: 'pictureText',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '概况类型',
    field: 'type',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'introduce_type',
    },
  },
  {
    label: '概况内容',
    field: 'text',
    component: 'JEditor',
  },
  {
    label: '图片',
    field: 'picture',
    component: 'JImageUpload',
    componentProps: {
      fileMax: 0,
    },
  },
  {
    label: '图片附文',
    field: 'pictureText',
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
  type: { title: '概况类型', order: 0, view: 'list', type: 'string', dictCode: 'introduce_type' },
  text: { title: '概况内容', order: 1, view: 'umeditor', type: 'string' },
  picture: { title: '图片', order: 2, view: 'image', type: 'string' },
  pictureText: { title: '图片附文', order: 3, view: 'text', type: 'string' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
