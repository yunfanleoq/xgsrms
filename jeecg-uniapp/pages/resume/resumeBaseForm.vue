<template>
    <view>
        <!--标题和返回-->
		<!-- <cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">基本信息测试</block>
		</cu-custom> -->
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">姓名：</text></view>
                  <input  placeholder="请输入姓名" v-model="model.name"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">性别：</text></view>
                  <input  placeholder="请输入性别" v-model="model.sex"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">籍贯：</text></view>
                  <input  placeholder="请输入籍贯" v-model="model.nativePlace"/>
                </view>
              </view>
              <my-date label="出生年月：" v-model="model.birthday" placeholder="请输入出生年月"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">民族：</text></view>
                  <input  placeholder="请输入民族" v-model="model.nation"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">身份证号：</text></view>
                  <input  placeholder="请输入身份证号" v-model="model.idNumber"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">政治面貌：</text></view>
                  <input  placeholder="请输入政治面貌" v-model="model.politicBackground"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">户口所在地：</text></view>
                  <input  placeholder="请输入户口所在地" v-model="model.hukou"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">是否应届毕业生：</text></view>
                  <input  placeholder="请输入是否应届毕业生" v-model="model.yjbys"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">是否统招统分：</text></view>
                  <input  placeholder="请输入是否统招统分" v-model="model.tztf"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">毕业院校：</text></view>
                  <input  placeholder="请输入毕业院校" v-model="model.graduateCollege"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">学历：</text></view>
                  <input  placeholder="请输入学历" v-model="model.education"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">学位：</text></view>
                  <input  placeholder="请输入学位" v-model="model.degree"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">专业：</text></view>
                  <input  placeholder="请输入专业" v-model="model.profession"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">毕业时间：</text></view>
                  <input  placeholder="请输入毕业时间" v-model="model.graduateDate"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">参加工作时间：</text></view>
                  <input  placeholder="请输入参加工作时间" v-model="model.workDate"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">目前工作单位：</text></view>
                  <input  placeholder="请输入目前工作单位" v-model="model.workUnit"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">档案所在单位：</text></view>
                  <input  placeholder="请输入档案所在单位" v-model="model.personFilesUnit"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">现行政职务：</text></view>
                  <input  placeholder="请输入现行政职务" v-model="model.adminPosition"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">任职时间：</text></view>
                  <input  placeholder="请输入任职时间" v-model="model.adminPositionDate"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">现岗位：</text></view>
                  <input  placeholder="请输入现岗位" v-model="model.professionLevel"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">聘任时间：</text></view>
                  <input  placeholder="请输入聘任时间" v-model="model.professionLevelData"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">与本所职工(包括在读研究生)是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系(若有请写出姓名)?(请务必勾选)：</text></view>
                  <input  placeholder="请输入与本所职工(包括在读研究生)是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系(若有请写出姓名)?(请务必勾选)" v-model="model.xgsRelation"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">联系电话：</text></view>
                  <input  placeholder="请输入联系电话" v-model="model.mobile"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">E-mail：</text></view>
                  <input  placeholder="请输入E-mail" v-model="model.email"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究方向与专长：</text></view>
                  <input  placeholder="请输入研究方向与专长" v-model="model.researchDirection"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">承担科研、管理工作情况：</text></view>
                  <input  placeholder="请输入承担科研、管理工作情况" v-model="model.researchWork"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">工作主要业绩：</text></view>
                  <input  placeholder="请输入工作主要业绩" v-model="model.reseachResult"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">论文专著专利：</text></view>
                  <input  placeholder="请输入论文专著专利" v-model="model.researchPaper"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">应聘岗位陈述：</text></view>
                  <input  placeholder="请输入应聘岗位陈述" v-model="model.positionDescription"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">所在地：</text></view>
                  <input  placeholder="请输入所在地" v-model="model.areaId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">标签编号列表：</text></view>
                  <input  placeholder="请输入标签编号列表" v-model="model.tagIds"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">等级编号：</text></view>
                  <input  placeholder="请输入等级编号" v-model="model.levelId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">分组编号：</text></view>
                  <input  placeholder="请输入分组编号" v-model="model.groupId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">创建者：</text></view>
                  <input  placeholder="请输入创建者" v-model="model.createBy"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">创建时间：</text></view>
                  <input  placeholder="创建时间" v-model="model.createTime"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">更新者：</text></view>
                  <input  placeholder="请输入更新者" v-model="model.updateBy"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">更新时间：</text></view>
                  <input  placeholder="请输入更新时间" v-model="model.updateTime"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">是否删除：</text></view>
                  <input  placeholder="请输入是否删除" v-model="model.deleted"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">租户编号：</text></view>
                  <input  placeholder="请输入租户编号" v-model="model.tenantId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">简历名称：</text></view>
                  <input  placeholder="请输入简历名称" v-model="model.resumeName"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位类型：</text></view>
                  <input  placeholder="请输入岗位类型" v-model="model.positionType"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">国籍：</text></view>
                  <input  placeholder="请输入国籍" v-model="model.nationality"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">应聘部门：</text></view>
                  <input  placeholder="请输入应聘部门" v-model="model.applyDept"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">简历类别：</text></view>
                  <input  placeholder="请输入简历类别" v-model="model.resumeType"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">应聘岗位名称：</text></view>
                  <input  placeholder="请输入应聘岗位名称" v-model="model.applyPosition"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">婚姻状况：</text></view>
                  <input  placeholder="请输入婚姻状况" v-model="model.maritalStatus"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">博士毕业院校：</text></view>
                  <input  placeholder="请输入博士毕业院校" v-model="model.graduateInstitutions"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究室意见：</text></view>
                  <input  placeholder="请输入研究室意见" v-model="model.labIdea"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">科研条件：</text></view>
                  <input  placeholder="请输入科研条件" v-model="model.researchCondition"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">拟研究计划：</text></view>
                  <input  placeholder="请输入拟研究计划" v-model="model.researchProposal"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">获荣誉奖情况：</text></view>
                  <input  placeholder="请输入获荣誉奖情况" v-model="model.carryPrizeHonor"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">学习经历：</text></view>
                  <input  placeholder="请输入学习经历" v-model="model.studyExperience"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">主要论著目录：</text></view>
                  <input  placeholder="请输入主要论著目录" v-model="model.researchWorks"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人电子邮箱：</text></view>
                  <input  placeholder="请输入推荐人电子邮箱" v-model="model.referrerEmail"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">一级学科：</text></view>
                  <input  placeholder="请输入一级学科" v-model="model.subject1"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">二级学科：</text></view>
                  <input  placeholder="请输入二级学科" v-model="model.subject2"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人技术职务：</text></view>
                  <input  placeholder="请输入推荐人技术职务" v-model="model.referrerPosition"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人工作单位：</text></view>
                  <input  placeholder="请输入推荐人工作单位" v-model="model.referrerUnit"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">一站单位名称：</text></view>
                  <input  placeholder="请输入一站单位名称" v-model="model.workFirst"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究室：</text></view>
                  <input  placeholder="请输入研究室" v-model="model.lob"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">申报博士后类型：</text></view>
                  <input  placeholder="请输入申报博士后类型" v-model="model.postdoctorType"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">现专业技术职务：</text></view>
                  <input  placeholder="请输入现专业技术职务" v-model="model.technicalPosition"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐原因：</text></view>
                  <input  placeholder="请输入推荐原因" v-model="model.cause"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究问题协助：</text></view>
                  <input  placeholder="请输入研究问题协助" v-model="model.researchQuestionsSolve"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人联系方式：</text></view>
                  <input  placeholder="请输入推荐人联系方式" v-model="model.referrerMobile"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">导师签字：</text></view>
                  <input  placeholder="请输入导师签字" v-model="model.teacherSignature"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">合作导师：</text></view>
                  <input  placeholder="请输入合作导师" v-model="model.teacher"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">导师评价：</text></view>
                  <input  placeholder="请输入导师评价" v-model="model.teacherEvaluate"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">人力资源处签字：</text></view>
                  <input  placeholder="请输入人力资源处签字" v-model="model.resourceSsignature"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">人力资源处意见：</text></view>
                  <input  placeholder="请输入人力资源处意见" v-model="model.resourcesIdea"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">博士后研究方向：</text></view>
                  <input  placeholder="请输入博士后研究方向" v-model="model.researchOrientation"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究室负责人签字：</text></view>
                  <input  placeholder="请输入研究室负责人签字" v-model="model.labSignature"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">申请人当前身份：</text></view>
                  <input  placeholder="请输入申请人当前身份" v-model="model.proposerStatus"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人签名：</text></view>
                  <input  placeholder="请输入推荐人签名" v-model="model.referrerSignature"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">博士学位证书 获得时间：</text></view>
                  <input  placeholder="请输入博士学位证书 获得时间" v-model="model.certificateTime"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究所意见：</text></view>
                  <input  placeholder="请输入研究所意见" v-model="model.schoolSsignature"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人：</text></view>
                  <input  placeholder="请输入推荐人" v-model="model.referrer"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">本人照片：</text></view>
                  <input  placeholder="请输入本人照片" v-model="model.photograph"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">获科技奖情况：</text></view>
                  <input  placeholder="请输入获科技奖情况" v-model="model.carryPrizeScience"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">被推荐人：</text></view>
                  <input  placeholder="请输入被推荐人" v-model="model.propositus"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">在站期间研究内容有无涉密内容：</text></view>
                  <input  placeholder="请输入在站期间研究内容有无涉密内容" v-model="model.secretText"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">拟申报岗位等级：</text></view>
                  <input  placeholder="请输入拟申报岗位等级" v-model="model.positionClass"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究所签字：</text></view>
                  <input  placeholder="请输入研究所签字" v-model="model.schoolIdea"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">所属部门：</text></view>
                  <input  placeholder="请输入所属部门" v-model="model.sysOrgCode"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">外键：</text></view>
                  <input  placeholder="请输入外键" v-model="model.baseId"/>
                </view>
              </view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

    export default {
        name: "BaseTestForm",
        components:{ myDate },
        props:{
          formData:{
              type:Object,
              default:()=>{},
              required:false
          }
        },
        data(){
            return {
				CustomBar: this.CustomBar,
				NavBarColor: this.NavBarColor,
				loading:false,
                model: {},
                backRouteName:'index',
                url: {
                  queryById: "/xgsUserResumes/baseTest/queryById",
                  add: "/xgsUserResumes/baseTest/add",
                  edit: "/xgsUserResumes/baseTest/edit",
                },
            }
        },
        created(){
             this.onNextPage();
        },
        methods:{
           initFormData(){
               if(this.formData){
                    let dataId = this.formData.dataId;
                    this.$http.get(this.url.queryById,{params:{id:dataId}}).then((res)=>{
                        if(res.data.success){
                            console.log("表单数据",res);
                            this.model = res.data.result;
                        }
                    })
                }
            },
			onNextPage(){
				let inputs = document.querySelectorAll('input[type="text"], input[type="password"], input[type="email"], input[type="number"], textarea');
				let allFilled = true;
				
				// 遍历每个input元素并检查其值
					inputs.forEach(function(input) {
						// 忽略disabled和readonly的input元素
						console.log('所有输入字段都已填写。',input.disabled);
						if (!input.disabled && !input.readonly && (!input.value || input.value.trim() === '')) {
							allFilled = false;
						}
					});
		 
					// 根据检查结果显示消息
					if (allFilled) {
						console.log('所有输入字段都已填写。');
					} else {
						console.log('有输入字段未填写。');
					}
			}
        }
    }
</script>
