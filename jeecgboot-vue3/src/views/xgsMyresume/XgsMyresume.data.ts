import { BasicColumn } from '/@/components/Table';
import { FormSchema } from '/@/components/Table';
import { rules } from '/@/utils/helper/validator';
import { render } from '/@/utils/common/renderUtils';
import { getWeekMonthQuarterYear } from '/@/utils';
//列表数据
export const columns: BasicColumn[] = [
  {
    title: '姓名',
    align: 'center',
    dataIndex: 'name',
  },
  {
    title: '性别',
    align: 'center',
    dataIndex: 'sex_dictText',
  },
  {
    title: '政治面貌',
    align: 'center',
    dataIndex: 'politicBackground',
  },
  {
    title: '国籍',
    align: 'center',
    dataIndex: 'nationality',
  },
  {
    title: '户口所在地',
    align: 'center',
    dataIndex: 'hukou',
  },
  {
    title: '毕业院校',
    align: 'center',
    dataIndex: 'graduateCollege',
  },
  {
    title: '学历',
    align: 'center',
    dataIndex: 'education',
  },
  {
    title: '专业',
    align: 'center',
    dataIndex: 'profession',
  },
  {
    title: '目前工作单位',
    align: 'center',
    dataIndex: 'workUnit',
  },
];
//查询数据
export const searchFormSchema: FormSchema[] = [];
//表单数据
export const formSchema: FormSchema[] = [
  {
    label: '姓名',
    field: 'name',
    component: 'Input',
  },
  {
    label: '本人照片',
    field: 'photograph',
    component: 'JImageUpload',
    componentProps: {
      fileMax: 0,
    },
  },
  {
    label: '性别',
    field: 'sex',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'sex',
    },
  },
  {
    label: '籍贯',
    field: 'nativePlace',
    component: 'Input',
  },
  {
    label: '出生年月',
    field: 'birthday',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '民族',
    field: 'nation',
    component: 'Input',
  },
  {
    label: '身份证号',
    field: 'idNumber',
    component: 'Input',
  },
  {
    label: '婚姻状况',
    field: 'maritalStatus',
    component: 'Input',
  },
  {
    label: '政治面貌',
    field: 'politicBackground',
    component: 'Input',
  },
  {
    label: '国籍',
    field: 'nationality',
    component: 'Input',
  },
  {
    label: '户口所在地',
    field: 'hukou',
    component: 'Input',
  },
  {
    label: '是否应届毕业生',
    field: 'yjbys',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'yes_or_no',
    },
  },
  {
    label: '是否统招统分',
    field: 'tztf',
    component: 'JDictSelectTag',
    componentProps: {
      dictCode: 'yes_or_no',
    },
  },
  {
    label: '毕业院校',
    field: 'graduateCollege',
    component: 'Input',
  },
  {
    label: '学历',
    field: 'education',
    component: 'Input',
  },
  {
    label: '学位',
    field: 'degree',
    component: 'Input',
  },
  {
    label: '专业',
    field: 'profession',
    component: 'Input',
  },
  {
    label: '毕业时间',
    field: 'graduateDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '参加工作时间',
    field: 'workDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '目前工作单位',
    field: 'workUnit',
    component: 'Input',
  },
  {
    label: '档案所在单位',
    field: 'personFilesUnit',
    component: 'Input',
  },
  {
    label: '现行政职务',
    field: 'adminPosition',
    component: 'Input',
  },
  {
    label: '任职时间',
    field: 'adminPositionDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '现岗位',
    field: 'professionLevel',
    component: 'Input',
  },
  {
    label: '聘任时间',
    field: 'professionLevelDate',
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    label: '与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）',
    field: 'xgsRelation',
    component: 'Input',
  },
  {
    label: '联系电话',
    field: 'mobile',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: false }, { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码!' }];
    },
  },
  {
    label: 'E-mail',
    field: 'email',
    component: 'Input',
    dynamicRules: ({ model, schema }) => {
      return [{ required: false }, { pattern: /^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: '请输入正确的电子邮件!' }];
    },
  },
  {
    label: '学习经历',
    field: 'studyExperience',
    component: 'InputTextArea',
  },
  {
    label: '研究方向与专长',
    field: 'researchDirection',
    component: 'InputTextArea',
  },
  {
    label: '承担科研、管理工作情况',
    field: 'researchWork',
    component: 'InputTextArea',
  },
  {
    label: '主要论著目录',
    field: 'researchWorks',
    component: 'InputTextArea',
  },
  {
    label: '工作主要业绩',
    field: 'researchResult',
    component: 'InputTextArea',
  },
  {
    label: '论文专著专利',
    field: 'researchPaper',
    component: 'InputTextArea',
  },
  {
    label: '获科技奖情况',
    field: 'carryPrizeScience',
    component: 'InputTextArea',
  },
  {
    label: '获荣誉奖情况',
    field: 'carryPrizeHonor',
    component: 'InputTextArea',
  },
  {
    label: '拟研究计划',
    field: 'researchProposal',
    component: 'InputTextArea',
  },
  {
    label: '科研条件',
    field: 'researchCondition',
    component: 'InputTextArea',
  },
  {
    label: '研究问题协助',
    field: 'researchQuestionsSolve',
    component: 'InputTextArea',
  },
  {
    label: '应聘岗位陈述',
    field: 'positionDescription',
    component: 'InputTextArea',
  },
  {
    label: '应聘部门',
    field: 'applyDept',
    component: 'Input',
  },
  {
    label: '应聘岗位名称',
    field: 'applyPosition',
    component: 'Input',
  },
  {
    label: '所在地',
    field: 'areaId',
    component: 'InputNumber',
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
  name: { title: '姓名', order: 0, view: 'text', type: 'string' },
  sex: { title: '性别', order: 2, view: 'list', type: 'string', dictCode: 'sex' },
  politicBackground: { title: '政治面貌', order: 8, view: 'text', type: 'string' },
  nationality: { title: '国籍', order: 9, view: 'text', type: 'string' },
  hukou: { title: '户口所在地', order: 10, view: 'text', type: 'string' },
  graduateCollege: { title: '毕业院校', order: 13, view: 'text', type: 'string' },
  education: { title: '学历', order: 14, view: 'text', type: 'string' },
  profession: { title: '专业', order: 16, view: 'text', type: 'string' },
  workUnit: { title: '目前工作单位', order: 19, view: 'text', type: 'string' },
};

/**
 * 流程表单调用这个方法获取formSchema
 * @param param
 */
export function getBpmFormSchema(_formData): FormSchema[] {
  // 默认和原始表单保持一致 如果流程中配置了权限数据，这里需要单独处理formSchema
  return formSchema;
}
