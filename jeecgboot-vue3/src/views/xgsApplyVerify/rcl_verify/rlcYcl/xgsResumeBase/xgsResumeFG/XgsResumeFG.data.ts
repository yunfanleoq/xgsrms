import {BasicColumn} from '/src/components/Table';
import {FormSchema} from '/src/components/Table';
import { rules} from '/src/utils/helper/validator';
import { render } from '/src/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/src/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/src/utils';
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '姓名',
    align:"center",
    dataIndex: 'name'
   },
   {
    title: '本人照片',
    align:"center",
    dataIndex: 'photograph',
    customRender:render.renderImage,
   },
   {
    title: '性别',
    align:"center",
    dataIndex: 'sex_dictText'
   },
   {
    title: '籍贯',
    align:"center",
    dataIndex: 'nativePlace'
   },
   {
    title: '出生年月',
    align:"center",
    dataIndex: 'birthday',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '民族',
    align:"center",
    dataIndex: 'nation'
   },
   {
    title: '身份证号',
    align:"center",
    dataIndex: 'idNumber'
   },
   {
    title: '婚姻状况',
    align:"center",
    dataIndex: 'maritalStatus'
   },
   {
    title: '政治面貌',
    align:"center",
    dataIndex: 'politicBackground'
   },
   {
    title: '国籍',
    align:"center",
    dataIndex: 'nationality'
   },
   {
    title: '户口所在地',
    align:"center",
    dataIndex: 'hukou'
   },
   {
    title: '专业',
    align:"center",
    dataIndex: 'profession'
   },
   {
    title: '目前工作单位',
    align:"center",
    dataIndex: 'workUnit'
   },
   {
    title: '现行政职务',
    align:"center",
    dataIndex: 'adminPosition'
   },
   {
    title: '现岗位',
    align:"center",
    dataIndex: 'professionLevel'
   },
   {
    title: '联系电话',
    align:"center",
    dataIndex: 'mobile'
   },
   {
    title: 'E-mail',
    align:"center",
    dataIndex: 'email'
   },
   {
    title: '应聘岗位名称',
    align:"center",
    dataIndex: 'applyPosition'
   },
   {
    title: '简历名称',
    align:"center",
    dataIndex: 'resumeName'
   },
   {
    title: '简历类别',
    align:"center",
    dataIndex: 'resumeType_dictText'
   },
];
//查询数据
export const searchFormSchema: FormSchema[] = [
];
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
     componentProps:{
        fileMax: 0
      },
  },
  {
    label: '性别',
    field: 'sex',
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sex"
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
      valueFormat: 'YYYY-MM-DD'
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
    label: '专业',
    field: 'profession',
    component: 'Input',
  },
  {
    label: '目前工作单位',
    field: 'workUnit',
    component: 'Input',
  },
  {
    label: '现行政职务',
    field: 'adminPosition',
    component: 'Input',
  },
  {
    label: '现岗位',
    field: 'professionLevel',
    component: 'Input',
  },
  {
    label: '联系电话',
    field: 'mobile',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: false},
                 { pattern: /^1[3456789]\d{9}$/, message: '请输入正确的手机号码!'},
          ];
     },
  },
  {
    label: 'E-mail',
    field: 'email',
    component: 'Input',
    dynamicRules: ({model,schema}) => {
          return [
                 { required: false},
                 { pattern: /^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: '请输入正确的电子邮件!'},
          ];
     },
  },
  {
    label: '学习经历',
    field: 'studyExperience',
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
    label: '应聘岗位名称',
    field: 'applyPosition',
    component: 'Input',
  },
  {
    label: '所在地',
    field: 'areaId',
    component: 'InputNumber',
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
    componentProps:{
        dictCode:"岗位分类"
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
//子表单数据
//子表表格配置
export const xgsResumeWorksColumns: JVxeColumn[] = [
    {
      title: '开始日期',
      key: 'beginDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '终止日期',
      key: 'endDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '工作单位',
      key: 'workUnit',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '工作部门',
      key: 'deptment',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '职务',
      key: 'position',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '专业技术岗位或职员职级',
      key: 'professionLevel',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]
export const xgsResumeEdusColumns: JVxeColumn[] = [
    {
      title: '毕业院校',
      key: 'graduateCollege',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '专业',
      key: 'profession',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '学历',
      key: 'education',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '学位',
      key: 'degree',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '起始日期',
      key: 'beginDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '终止日期',
      key: 'endDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]
export const xgsResumeHomeColumns: JVxeColumn[] = [
    {
      title: '关系',
      key: 'relation',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '家属姓名',
      key: 'name',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '性别',
      key: 'sex',
      type: JVxeTypes.select,
      options:[],
      dictCode:"sex",
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '国籍',
      key: 'nationality',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '籍贯',
      key: 'nativePlace',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '出生年月',
      key: 'birthday',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '民族',
      key: 'nation',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '身份证号',
      key: 'idNumber',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '政治面貌',
      key: 'politicBackground',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '户口所在地',
      key: 'hukou',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '毕业院校',
      key: 'graduateCollege',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '学历',
      key: 'education',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '学位',
      key: 'degree',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '专业',
      key: 'profession',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '毕业时间',
      key: 'graduateDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '参加工作时间',
      key: 'workDate',
      type: JVxeTypes.date,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
    {
      title: '目前工作单位',
      key: 'workUnit',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'',
    },
  ]

// 高级查询数据
export const superQuerySchema = {
  name: {title: '姓名',order: 0,view: 'text', type: 'string',},
  photograph: {title: '本人照片',order: 1,view: 'image', type: 'string',},
  sex: {title: '性别',order: 2,view: 'list', type: 'string',dictCode: 'sex',},
  nativePlace: {title: '籍贯',order: 3,view: 'text', type: 'string',},
  birthday: {title: '出生年月',order: 4,view: 'date', type: 'string',},
  nation: {title: '民族',order: 5,view: 'text', type: 'string',},
  idNumber: {title: '身份证号',order: 6,view: 'text', type: 'string',},
  maritalStatus: {title: '婚姻状况',order: 7,view: 'text', type: 'string',},
  politicBackground: {title: '政治面貌',order: 8,view: 'text', type: 'string',},
  nationality: {title: '国籍',order: 9,view: 'text', type: 'string',},
  hukou: {title: '户口所在地',order: 10,view: 'text', type: 'string',},
  profession: {title: '专业',order: 11,view: 'text', type: 'string',},
  workUnit: {title: '目前工作单位',order: 12,view: 'text', type: 'string',},
  adminPosition: {title: '现行政职务',order: 13,view: 'text', type: 'string',},
  professionLevel: {title: '现岗位',order: 14,view: 'text', type: 'string',},
  mobile: {title: '联系电话',order: 15,view: 'text', type: 'string',},
  email: {title: 'E-mail',order: 16,view: 'text', type: 'string',},
  applyPosition: {title: '应聘岗位名称',order: 28,view: 'text', type: 'string',},
  resumeName: {title: '简历名称',order: 30,view: 'text', type: 'string',},
  resumeType: {title: '简历类别',order: 31,view: 'list', type: 'string',dictCode: '岗位分类',},
  //子表高级查询
  xgsResumeWorks: {
    title: '工作经历',
    view: 'table',
    fields: {
        beginDate: {title: '开始日期',order: 0,view: 'date', type: 'string',},
        endDate: {title: '终止日期',order: 1,view: 'date', type: 'string',},
        workUnit: {title: '工作单位',order: 2,view: 'text', type: 'string',},
        deptment: {title: '工作部门',order: 3,view: 'text', type: 'string',},
        position: {title: '职务',order: 4,view: 'text', type: 'string',},
        professionLevel: {title: '专业技术岗位或职员职级',order: 5,view: 'text', type: 'string',},
    }
  },
  xgsResumeEdus: {
    title: '教育经历',
    view: 'table',
    fields: {
        graduateCollege: {title: '毕业院校',order: 0,view: 'text', type: 'string',},
        profession: {title: '专业',order: 1,view: 'text', type: 'string',},
        education: {title: '学历',order: 2,view: 'text', type: 'string',},
        degree: {title: '学位',order: 3,view: 'text', type: 'string',},
        beginDate: {title: '起始日期',order: 4,view: 'date', type: 'string',},
        endDate: {title: '终止日期',order: 5,view: 'date', type: 'string',},
    }
  },
  xgsResumeHome: {
    title: '家庭状况',
    view: 'table',
    fields: {
        relation: {title: '关系',order: 0,view: 'text', type: 'string',},
        name: {title: '家属姓名',order: 1,view: 'text', type: 'string',},
        sex: {title: '性别',order: 2,view: 'list', type: 'string',dictCode: 'sex',},
        nationality: {title: '国籍',order: 3,view: 'text', type: 'string',},
        nativePlace: {title: '籍贯',order: 4,view: 'text', type: 'string',},
        birthday: {title: '出生年月',order: 5,view: 'date', type: 'string',},
        nation: {title: '民族',order: 6,view: 'text', type: 'string',},
        idNumber: {title: '身份证号',order: 7,view: 'text', type: 'string',},
        politicBackground: {title: '政治面貌',order: 8,view: 'text', type: 'string',},
        hukou: {title: '户口所在地',order: 9,view: 'text', type: 'string',},
        graduateCollege: {title: '毕业院校',order: 10,view: 'text', type: 'string',},
        education: {title: '学历',order: 11,view: 'text', type: 'string',},
        degree: {title: '学位',order: 12,view: 'text', type: 'string',},
        profession: {title: '专业',order: 13,view: 'text', type: 'string',},
        graduateDate: {title: '毕业时间',order: 14,view: 'date', type: 'string',},
        workDate: {title: '参加工作时间',order: 15,view: 'date', type: 'string',},
        workUnit: {title: '目前工作单位',order: 16,view: 'text', type: 'string',},
    }
  },
};
