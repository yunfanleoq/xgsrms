import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { JVxeTypes, JVxeColumn } from '/@/components/jeecg/JVxeTable/types';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '被推荐人',
    align: 'center',
    dataIndex: 'propositus',
  },
  {
    title: '推荐人',
    align: 'center',
    dataIndex: 'referrer',
  },
  {
    title: '推荐人技术职务',
    align: 'center',
    dataIndex: 'referrerPosition',
  },
  {
    title: '推荐人工作单位',
    align: 'center',
    dataIndex: 'referrerUnit',
  },
  {
    title: '推荐人联系方式',
    align: 'center',
    dataIndex: 'referrerMobile',
  },
  {
    title: '推荐人电子邮箱',
    align: 'center',
    dataIndex: 'referrerEmail',
  },
  {
    title: '推荐人签名',
    align: 'center',
    dataIndex: 'referrerSignature',
    customRender: render.renderImage,
  },
  {
    title: '拟申报岗位等级',
    align: 'center',
    dataIndex: 'positionClass',
  },
  {
    title: '推荐原因',
    align: 'center',
    dataIndex: 'cause',
  },
  {
    title: '简历名称',
    align: 'center',
    dataIndex: 'resumeName',
  },
  {
    title: '简历类别',
    align: 'center',
    dataIndex: 'resumeType_dictText',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '被推荐人',
    field: 'propositus',
    component: 'Input',
  },
  {
    label: '推荐人',
    field: 'referrer',
    component: 'Input',
  },
  {
    label: '推荐人技术职务',
    field: 'referrerPosition',
    component: 'Input',
  },
  {
    label: '推荐人工作单位',
    field: 'referrerUnit',
    component: 'Input',
  },
  {
    label: '推荐人联系方式',
    field: 'referrerMobile',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: false }, { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码!' }];
    },
  },
  {
    label: '推荐人电子邮箱',
    field: 'referrerEmail',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: false }, { pattern: /^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: '请输入正确的电子邮件!' }];
    },
  },
  {
    label: '推荐人签名',
    field: 'referrerSignature',
    component: 'JImageUpload',
    componentProps: {
      fileMax: 0,
    },
  },
  {
    label: '拟申报岗位等级',
    field: 'positionClass',
    component: 'Input',
  },
  {
    label: '推荐原因',
    field: 'cause',
    component: 'InputTextArea',
  },
  {
    label: '简历名称',
    field: 'resumeName',
    component: 'Input',
  },
  {
    label: '简历类别',
    field: 'resumeType',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: '岗位分类',
    },
  },
  // TODO 主键隐藏字段，目前写死为ID
  {
    label: '',
    field: 'id',
    component: 'Input',
    show: false,
  },
];
//子表单数据
//子表表格配置
export const xgsResumeWorksColumns: JVxeColumn[] = [
  {
    title: '开始日期',
    key: 'beginDate',
    type: JVxeTypes.date,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '终止日期',
    key: 'endDate',
    type: JVxeTypes.date,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '工作单位',
    key: 'workUnit',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '工作部门',
    key: 'deptment',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '职务',
    key: 'position',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '专业技术岗位或职员职级',
    key: 'professionLevel',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
];
export const xgsResumeEdusColumns: JVxeColumn[] = [
  {
    title: '毕业院校',
    key: 'graduateCollege',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '专业',
    key: 'profession',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '学历',
    key: 'education',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '学位',
    key: 'degree',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '起始日期',
    key: 'beginDate',
    type: JVxeTypes.date,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '终止日期',
    key: 'endDate',
    type: JVxeTypes.date,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
];
export const xgsResumeHomeColumns: JVxeColumn[] = [
  {
    title: '关系',
    key: 'relation',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '家属姓名',
    key: 'name',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '性别',
    key: 'sex',
    type: JVxeTypes.select,
    options: [],
    dictCode: 'sex',
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '国籍',
    key: 'nationality',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '籍贯',
    key: 'nativePlace',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '出生年月',
    key: 'birthday',
    type: JVxeTypes.date,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '民族',
    key: 'nation',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '身份证号',
    key: 'idNumber',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '政治面貌',
    key: 'politicBackground',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '户口所在地',
    key: 'hukou',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '毕业院校',
    key: 'graduateCollege',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '学历',
    key: 'education',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '学位',
    key: 'degree',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '专业',
    key: 'profession',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '毕业时间',
    key: 'graduateDate',
    type: JVxeTypes.date,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '参加工作时间',
    key: 'workDate',
    type: JVxeTypes.date,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
  {
    title: '目前工作单位',
    key: 'workUnit',
    type: JVxeTypes.input,
    width: '200px',
    placeholder: '请输入${title}',
    defaultValue: '',
  },
];

// 高级查询数据
export const superQuerySchema = {
  propositus: { title: '被推荐人', order: 0, view: 'text', type: 'string' },
  referrer: { title: '推荐人', order: 1, view: 'text', type: 'string' },
  referrerPosition: { title: '推荐人技术职务', order: 2, view: 'text', type: 'string' },
  referrerUnit: { title: '推荐人工作单位', order: 3, view: 'text', type: 'string' },
  referrerMobile: { title: '推荐人联系方式', order: 4, view: 'text', type: 'string' },
  referrerEmail: { title: '推荐人电子邮箱', order: 5, view: 'text', type: 'string' },
  referrerSignature: { title: '推荐人签名', order: 6, view: 'image', type: 'string' },
  positionClass: { title: '拟申报岗位等级', order: 7, view: 'text', type: 'string' },
  cause: { title: '推荐原因', order: 8, view: 'textarea', type: 'string' },
  resumeName: { title: '简历名称', order: 9, view: 'text', type: 'string' },
  resumeType: { title: '简历类别', order: 10, view: 'list', type: 'string', dictCode: '岗位分类' },
  //子表高级查询
  xgsResumeWorks: {
    title: '工作经历',
    view: 'table',
    fields: {
      beginDate: { title: '开始日期', order: 0, view: 'date', type: 'string' },
      endDate: { title: '终止日期', order: 1, view: 'date', type: 'string' },
      workUnit: { title: '工作单位', order: 2, view: 'text', type: 'string' },
      deptment: { title: '工作部门', order: 3, view: 'text', type: 'string' },
      position: { title: '职务', order: 4, view: 'text', type: 'string' },
      professionLevel: { title: '专业技术岗位或职员职级', order: 5, view: 'text', type: 'string' },
    },
  },
  xgsResumeEdus: {
    title: '教育经历',
    view: 'table',
    fields: {
      graduateCollege: { title: '毕业院校', order: 0, view: 'text', type: 'string' },
      profession: { title: '专业', order: 1, view: 'text', type: 'string' },
      education: { title: '学历', order: 2, view: 'text', type: 'string' },
      degree: { title: '学位', order: 3, view: 'text', type: 'string' },
      beginDate: { title: '起始日期', order: 4, view: 'date', type: 'string' },
      endDate: { title: '终止日期', order: 5, view: 'date', type: 'string' },
    },
  },
  xgsResumeHome: {
    title: '家庭状况',
    view: 'table',
    fields: {
      relation: { title: '关系', order: 0, view: 'text', type: 'string' },
      name: { title: '家属姓名', order: 1, view: 'text', type: 'string' },
      sex: { title: '性别', order: 2, view: 'list', type: 'string', dictCode: 'sex' },
      nationality: { title: '国籍', order: 3, view: 'text', type: 'string' },
      nativePlace: { title: '籍贯', order: 4, view: 'text', type: 'string' },
      birthday: { title: '出生年月', order: 5, view: 'date', type: 'string' },
      nation: { title: '民族', order: 6, view: 'text', type: 'string' },
      idNumber: { title: '身份证号', order: 7, view: 'text', type: 'string' },
      politicBackground: { title: '政治面貌', order: 8, view: 'text', type: 'string' },
      hukou: { title: '户口所在地', order: 9, view: 'text', type: 'string' },
      graduateCollege: { title: '毕业院校', order: 10, view: 'text', type: 'string' },
      education: { title: '学历', order: 11, view: 'text', type: 'string' },
      degree: { title: '学位', order: 12, view: 'text', type: 'string' },
      profession: { title: '专业', order: 13, view: 'text', type: 'string' },
      graduateDate: { title: '毕业时间', order: 14, view: 'date', type: 'string' },
      workDate: { title: '参加工作时间', order: 15, view: 'date', type: 'string' },
      workUnit: { title: '目前工作单位', order: 16, view: 'text', type: 'string' },
    },
  },
};
