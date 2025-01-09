import {BasicColumn} from '/src/components/Table';
import {FormSchema} from '/src/components/Table';
import { rules} from '/src/utils/helper/validator';
import { render } from '/src/utils/common/renderUtils';
import {JVxeTypes,JVxeColumn} from '/src/components/jeecg/JVxeTable/types'
import { getWeekMonthQuarterYear } from '/src/utils';
import {defineProps} from "vue/dist/vue";
import {ref} from "vue";

import { useUserStore } from "@/store/modules/user"

const userStore = useUserStore();
import {
  xgsResumeEdusList,
  xgsResumeHomeList
} from "@/views/xgsResumeBase/xgsResumeBSH/XgsResumeBSH.api";
//列表数据
export const columns: BasicColumn[] = [
   {
    title: '姓名',
    align:"center",
    dataIndex: 'name',
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
    title: '户口所在地',
    align:"center",
    dataIndex: 'hukou'
   },
   {
    title: '参加工作时间',
    align:"center",
    dataIndex: 'workDate',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '目前工作单位',
    align:"center",
    dataIndex: 'workUnit'
   },
   {
    title: '档案所在单位',
    align:"center",
    dataIndex: 'personFilesUnit'
   },
   {
    title: '现行政职务',
    align:"center",
    dataIndex: 'adminPosition'
   },
   {
    title: '研究室',
    align:"center",
    dataIndex: 'lob'
   },
   {
    title: '博士后研究方向',
    align:"center",
    dataIndex: 'researchOrientation'
   },
   {
    title: '申请人当前身份',
    align:"center",
    dataIndex: 'proposerStatus_dictText'
   },
   {
    title: '申报博士后类型',
    align:"center",
    dataIndex: 'postdoctorType_dictText'
   },
   {
    title: '合作导师',
    align:"center",
    dataIndex: 'teacher'
   },
   {
    title: '博士毕业院校',
    align:"center",
    dataIndex: 'graduateInstitutions'
   },
   {
    title: '一级学科',
    align:"center",
    dataIndex: 'subject1'
   },
   {
    title: '二级学科',
    align:"center",
    dataIndex: 'subject2'
   },
   {
    title: '博士学位证书 获得时间',
    align:"center",
    dataIndex: 'certificateTime',
    customRender:({text}) =>{
      text = !text ? "" : (text.length > 10 ? text.substr(0,10) : text);
      return text;
    },
   },
   {
    title: '现专业技术职务',
    align:"center",
    dataIndex: 'technicalPosition'
   },
   {
    title: '在站期间研究内容有无涉密内容',
    align:"center",
    dataIndex: 'secretText_dictText'
   },
   {
    title: '一站单位名称',
    align:"center",
    dataIndex: 'workFirst'
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
    dynamicDisabled: true,
  },
  {
    label: '本人照片',
    field: 'photograph',
    dynamicDisabled: true,
     component: 'JImageUpload',
     componentProps:{
        fileMax: 0
      },
  },
  {
    label: '性别',
    field: 'sex',
    dynamicDisabled: true,
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"sex"
     },
  },
  {
    label: '籍贯',
    field: 'nativePlace',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '出生年月',
    field: 'birthday',
    dynamicDisabled: true,
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '民族',
    field: 'nation',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '身份证号',
    field: 'idNumber',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '婚姻状况',
    field: 'maritalStatus',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '政治面貌',
    field: 'politicBackground',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '户口所在地',
    field: 'hukou',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '参加工作时间',
    field: 'workDate',
    dynamicDisabled: true,
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '目前工作单位',
    field: 'workUnit',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '档案所在单位',
    field: 'personFilesUnit',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '现行政职务',
    field: 'adminPosition',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '学习经历',
    field: 'studyExperience',
    component: 'InputTextArea',
    dynamicDisabled: true,
  },
  {
    label: '研究方向与专长',
    field: 'researchDirection',
    component: 'InputTextArea',
    dynamicDisabled: true,
  },
  {
    label: '承担科研、管理工作情况',
    field: 'researchWork',
    component: 'InputTextArea',
    dynamicDisabled: true,
  },
  {
    label: '主要论著目录',
    field: 'researchWorks',
    component: 'InputTextArea',
    dynamicDisabled: true,
  },
  {
    label: '论文专著专利',
    field: 'researchPaper',
    component: 'InputTextArea',
    dynamicDisabled: true,
  },
  {
    label: '拟研究计划',
    field: 'researchProposal',
    component: 'InputTextArea',
    dynamicDisabled: true,
  },
  {
    label: '所在地',
    field: 'areaId',
    component: 'InputNumber',
    dynamicDisabled: true,
  },
  {
    label: '研究室',
    field: 'lob',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '博士后研究方向',
    field: 'researchOrientation',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '申请人当前身份',
    field: 'proposerStatus',
    dynamicDisabled: true,
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"申请人身份"
     },
  },
  {
    label: '申报博士后类型',
    field: 'postdoctorType',
    dynamicDisabled: true,
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"博士后类型"
     },
  },
  {
    label: '合作导师',
    field: 'teacher',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '博士毕业院校',
    field: 'graduateInstitutions',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '一级学科',
    field: 'subject1',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '二级学科',
    field: 'subject2',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '博士学位证书 获得时间',
    field: 'certificateTime',
    dynamicDisabled: true,
    component: 'DatePicker',
    componentProps: {
      valueFormat: 'YYYY-MM-DD'
    },
  },
  {
    label: '现专业技术职务',
    field: 'technicalPosition',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '在站期间研究内容有无涉密内容',
    field: 'secretText',
    dynamicDisabled: true,
    component: 'JDictSelectTag',
    componentProps:{
        dictCode:"yes_or_no"
     },
  },
  {
    label: '一站单位名称',
    field: 'workFirst',
    component: 'Input',
    dynamicDisabled: true,
  },
  {
    label: '导师评价',
    field: 'teacherEvaluate',
    component: 'Input',
    dynamicDisabled: true,
  },
  // {
  //   label: '导师签字',
  //   field: 'teacherSignature',
  //   dynamicDisabled: true,
  //    component: 'JImageUpload',
  //    componentProps:{
  //       fileMax: 0
  //     },
  // },
  {
    label: '研究室意见',
    field: 'labIdea',
    component: 'JEditor',
    componentProps: {
      dictCode: "研究室意见"
    },
    colProps: {
      span: 24,
    },
    rules: [
      {
        required: true,
        message: '请填写研究室意见',
      },
    ],
    helpMessage: '请务必填写研究室意见',
  },
  // {
  //   label: '研究室负责人签字',
  //   field: 'labSignature',
  //    component: 'JImageUpload',
  //    componentProps:{
  //       fileMax: 0
  //     },
  // },
  // {
  //   label: '人力资源处意见',
  //   field: 'resourcesIdea',
  //   component: 'Input',
  //   dynamicDisabled: true,
  // },
  // {
  //   label: '人力资源处签字',
  //   field: 'resourceSsignature',
  //   dynamicDisabled: true,
  //    component: 'JImageUpload',
  //    componentProps:{
  //       fileMax: 0
  //     },
  // },
  // {
  //   label: '研究所意见',
  //   field: 'schoolSsignature',
  //   component: 'Input',
  //   dynamicDisabled: true,
  // },
  // {
  //   label: '研究所签字',
  //   field: 'schoolIdea',
  //   dynamicDisabled: true,
  //    component: 'JImageUpload',
  //    componentProps:{
  //       fileMax: 0
  //     },
  // },
  // {
  //   label: '简历名称',
  //   field: 'resumeName',
  //   component: 'Input',
  //   dynamicDisabled: true,
  // },
  // {
  //   label: '简历类别',
  //   field: 'resumeType',
  //   // dynamicDisabled: true,
  //   component: 'JDictSelectTag',
  //   componentProps:{
  //       dictCode:"岗位分类"
  //    },
  // },
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
      defaultValue:'山东临沂',
    },
    {
      title: '毕业院校',
      key: 'graduateCollege',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'聊城大学',
    },
    {
      title: '学历',
      key: 'education',
      type: JVxeTypes.input,
      width:"200px",
      placeholder: '请输入${title}',
      defaultValue:'硕士',
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
  hukou: {title: '户口所在地',order: 9,view: 'text', type: 'string',},
  workDate: {title: '参加工作时间',order: 10,view: 'date', type: 'string',},
  workUnit: {title: '目前工作单位',order: 11,view: 'text', type: 'string',},
  personFilesUnit: {title: '档案所在单位',order: 12,view: 'text', type: 'string',},
  adminPosition: {title: '现行政职务',order: 13,view: 'text', type: 'string',},
  lob: {title: '研究室',order: 21,view: 'text', type: 'string',},
  researchOrientation: {title: '博士后研究方向',order: 22,view: 'text', type: 'string',},
  proposerStatus: {title: '申请人当前身份',order: 23,view: 'list', type: 'string',dictCode: '申请人身份',},
  postdoctorType: {title: '申报博士后类型',order: 24,view: 'list', type: 'string',dictCode: '博士后类型',},
  teacher: {title: '合作导师',order: 25,view: 'text', type: 'string',},
  graduateInstitutions: {title: '博士毕业院校',order: 26,view: 'text', type: 'string',},
  subject1: {title: '一级学科',order: 27,view: 'text', type: 'string',},
  subject2: {title: '二级学科',order: 28,view: 'text', type: 'string',},
  certificateTime: {title: '博士学位证书 获得时间',order: 29,view: 'date', type: 'string',},
  technicalPosition: {title: '现专业技术职务',order: 30,view: 'text', type: 'string',},
  secretText: {title: '在站期间研究内容有无涉密内容',order: 31,view: 'list', type: 'string',dictCode: 'yes_or_no',},
  workFirst: {title: '一站单位名称',order: 32,view: 'text', type: 'string',},
  resumeName: {title: '简历名称',order: 41,view: 'text', type: 'string',},
  resumeType: {title: '简历类别',order: 42,view: 'list', type: 'string',dictCode: '岗位分类',},
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

//主表信息
const resume = ref("")
//子表信息
const resumeWorks = ref("")
const resumeEdus = ref("")
const resumeHome = ref("")

//获取当前登录的用户的id
const userId = ref(  )
userId.value = userStore.getUserInfo.username
const getResume = () => {

}


