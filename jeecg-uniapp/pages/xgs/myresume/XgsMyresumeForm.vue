<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">简历列表</block>
		</cu-custom>
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
                  <view class="title"><text space="ensp">本人照片：</text></view>
                  <input  placeholder="请输入本人照片" v-model="model.photograph"/>
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
              <my-date label="出生年月：" fields="day" v-model="model.birthday" placeholder="请输入出生年月"></my-date>
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
                  <view class="title"><text space="ensp">婚姻状况：</text></view>
                  <input  placeholder="请输入婚姻状况" v-model="model.maritalStatus"/>
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
                  <view class="title"><text space="ensp">国籍：</text></view>
                  <input  placeholder="请输入国籍" v-model="model.nationality"/>
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
              <my-date label="毕业时间：" fields="day" v-model="model.graduateDate" placeholder="请输入毕业时间"></my-date>
              <my-date label="参加工作时间：" fields="day" v-model="model.workDate" placeholder="请输入参加工作时间"></my-date>
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
              <my-date label="任职时间：" fields="day" v-model="model.adminPositionDate" placeholder="请输入任职时间"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">现岗位：</text></view>
                  <input  placeholder="请输入现岗位" v-model="model.professionLevel"/>
                </view>
              </view>
              <my-date label="聘任时间：" fields="day" v-model="model.professionLevelDate" placeholder="请输入聘任时间"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）：</text></view>
                  <input  placeholder="请输入与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）" v-model="model.xgsRelation"/>
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
                  <view class="title"><text space="ensp">学习经历：</text></view>
                  <input  placeholder="请输入学习经历" v-model="model.studyExperience"/>
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
                  <view class="title"><text space="ensp">主要论著目录：</text></view>
                  <input  placeholder="请输入主要论著目录" v-model="model.researchWorks"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">工作主要业绩：</text></view>
                  <input  placeholder="请输入工作主要业绩" v-model="model.researchResult"/>
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
                  <view class="title"><text space="ensp">获科技奖情况：</text></view>
                  <input  placeholder="请输入获科技奖情况" v-model="model.carryPrizeScience"/>
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
                  <view class="title"><text space="ensp">拟研究计划：</text></view>
                  <input  placeholder="请输入拟研究计划" v-model="model.researchProposal"/>
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
                  <view class="title"><text space="ensp">研究问题协助：</text></view>
                  <input  placeholder="请输入研究问题协助" v-model="model.researchQuestionsSolve"/>
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
                  <view class="title"><text space="ensp">应聘部门：</text></view>
                  <input  placeholder="请输入应聘部门" v-model="model.applyDept"/>
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
                  <view class="title"><text space="ensp">所在地：</text></view>
                  <input type="number" placeholder="请输入所在地" v-model="model.areaId"/>
                </view>
              </view>
				<view class="padding">
					<button class="cu-btn block bg-blue margin-tb-sm lg" @click="onSubmit">
						<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>提交
					</button>
				</view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

    export default {
        name: "XgsMyresumeForm",
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
                  queryById: "/positions/xgsMyresume/queryById",
                  add: "/positions/xgsMyresume/add",
                  edit: "/positions/xgsMyresume/edit",
                },
            }
        },
        created(){
             this.initFormData();
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
            onSubmit() {
                let myForm = {...this.model};
                this.loading = true;
                let url = myForm.id?this.url.edit:this.url.add;
				this.$http.post(url,myForm).then(res=>{
				   console.log("res",res)
				   this.loading = false
				   this.$Router.push({name:this.backRouteName})
				}).catch(()=>{
					this.loading = false
				});
            }
        }
    }
</script>
