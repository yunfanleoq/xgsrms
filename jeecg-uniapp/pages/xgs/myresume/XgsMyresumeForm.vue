<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">简历详情</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="title">个人基本信息</view>
              </view>
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
              <view class="cu-form-group" style="height: 90px;">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">与本所职工（包括在读研究生）是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系（若有请写出姓名）？（请务必勾选）：</text></view>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <input placeholder="请输入" v-model="model.xgsRelation"/>
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

              <!-- 工作经历 -->
              <view class="cu-form-group">
                <view class="title">工作经历</view>
                <button class="cu-btn bg-green sm" @click="addWorkExperience">添加工作经历</button>
              </view>
              <view v-for="(work, index) in model.xgsResumeWorks" :key="work.id" class="work-experience-item">
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">开始日期：</text></view>
                    <my-date fields="day" v-model="work.beginDate" placeholder="请输入开始日期"></my-date>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">终止日期：</text></view>
                    <my-date fields="day" v-model="work.endDate" placeholder="请输入终止日期"></my-date>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">工作单位：</text></view>
                    <input placeholder="请输入工作单位" v-model="work.workUnit"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">工作部门：</text></view>
                    <input placeholder="请输入工作部门" v-model="work.deptment"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">职务：</text></view>
                    <input placeholder="请输入职务" v-model="work.position"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">专业技术岗位或职员职级：</text></view>
                    <input placeholder="请输入专业技术岗位或职员职级" v-model="work.professionLevel"/>
                  </view>
                </view>
                <button class="cu-btn bg-red sm" @click="removeWorkExperience(index)">删除</button>
              </view>

              <!-- 教育经历 -->
              <view class="cu-form-group">
                <view class="title">教育经历</view>
                <button class="cu-btn bg-green sm" @click="addEducationExperience">添加教育经历</button>
              </view>
              <view v-for="(edu, index) in model.xgsResumeEdus" :key="edu.id" class="education-experience-item">
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">毕业院校：</text></view>
                    <input placeholder="请输入毕业院校" v-model="edu.graduateCollege"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">专业：</text></view>
                    <input placeholder="请输入专业" v-model="edu.profession"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">学历：</text></view>
                    <input placeholder="请输入学历" v-model="edu.education"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">学位：</text></view>
                    <input placeholder="请输入学位" v-model="edu.degree"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">起始日期：</text></view>
                    <my-date fields="day" v-model="edu.beginDate" placeholder="请输入起始日期"></my-date>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">终止日期：</text></view>
                    <my-date fields="day" v-model="edu.endDate" placeholder="请输入终止日期"></my-date>
                  </view>
                </view>
                <button class="cu-btn bg-red sm" @click="removeEducationExperience(index)">删除</button>
              </view>

              <!-- 家庭状况 -->
              <view class="cu-form-group">
                <view class="title">家庭状况</view>
                <button class="cu-btn bg-green sm" @click="addFamilyMember">添加家庭成员</button>
              </view>
              <view v-for="(member, index) in model.xgsResumeHome" :key="member.id" class="family-member-item">
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">关系：</text></view>
                    <input placeholder="请输入关系" v-model="member.relation"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">姓名：</text></view>
                    <input placeholder="请输入姓名" v-model="member.name"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">性别：</text></view>
                    <input placeholder="请输入性别" v-model="member.sex"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">国籍：</text></view>
                    <input placeholder="请输入国籍" v-model="member.nationality"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">籍贯：</text></view>
                    <input placeholder="请输入籍贯" v-model="member.nativePlace"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">出生年月：</text></view>
                    <my-date fields="day" v-model="member.birthday" placeholder="请输入出生年月"></my-date>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">民族：</text></view>
                    <input placeholder="请输入民族" v-model="member.nation"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">身份证号：</text></view>
                    <input placeholder="请输入身份证号" v-model="member.idNumber"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">政治面貌：</text></view>
                    <input placeholder="请输入政治面貌" v-model="member.politicBackground"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">户口所在地：</text></view>
                    <input placeholder="请输入户口所在地" v-model="member.hukou"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">毕业院校：</text></view>
                    <input placeholder="请输入毕业院校" v-model="member.graduateCollege"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">学历：</text></view>
                    <input placeholder="请输入学历" v-model="member.education"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">学位：</text></view>
                    <input placeholder="请输入学位" v-model="member.degree"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">专业：</text></view>
                    <input placeholder="请输入专业" v-model="member.profession"/>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">毕业时间：</text></view>
                    <my-date fields="day" v-model="member.graduateDate" placeholder="请输入毕业时间"></my-date>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">参加工作时间：</text></view>
                    <my-date fields="day" v-model="member.workDate" placeholder="请输入参加工作时间"></my-date>
                  </view>
                </view>
                <view class="cu-form-group">
                  <view class="flex align-center">
                    <view class="title"><text space="ensp">目前工作单位：</text></view>
                    <input placeholder="请输入目前工作单位" v-model="member.workUnit"/>
                  </view>
                </view>
                <button class="cu-btn bg-red sm" @click="removeFamilyMember(index)">删除</button>
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
                dataId: '',
                CustomBar: this.CustomBar,
                NavBarColor: this.NavBarColor,
                loading:false,
                model: {
                    xgsResumeWorks: [],
                    xgsResumeEdus: [],
                    xgsResumeHome: []
                },
                backRouteName:'index',
                url: {
                  queryById: "/xgsResume/xgsResumeBase/queryById",
                  queryXgsResumeWorksByMainId: '/xgsResume/xgsResumeBase/queryXgsResumeWorksByMainId',
                  queryXgsResumeEdusByMainId: '/xgsResume/xgsResumeBase/queryXgsResumeEdusByMainId',
                  queryXgsResumeHomeByMainId: '/xgsResume/xgsResumeBase/queryXgsResumeHomeByMainId',
                  add: "/xgsMyresume/xgsMyresume/add",
                  edit: "/xgsResume/xgsResumeBase/edit",
                },
            }
        },
        onLoad: function (option) { //option为object类型，会序列化上个页面传递的参数
          this.dataId = option.id;
          this.initFormData()
        },
        methods:{
          uuid() {
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
              var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
              return v.toString(16)
            }).replace(/-/g, '');
          },
           initFormData(){
              let dataId = this.dataId;
              let req1 = this.$http.get(this.url.queryById,{params:{id:dataId}}).then((res)=>res)
              let req2 = this.$http.get(this.url.queryXgsResumeWorksByMainId,{params:{id:dataId}}).then((res)=>res)
              let req3 = this.$http.get(this.url.queryXgsResumeEdusByMainId,{params:{id:dataId}}).then((res)=>res)
              let req4 = this.$http.get(this.url.queryXgsResumeHomeByMainId,{params:{id:dataId}}).then((res)=>res)
              Promise.all([req1,req2,req3,req4]).then(([res1,res2,res3,res4])=>{
                if(res1.data.success){
                      console.log("表单数据1",res1);
                      this.model = res1.data.result;
                  }
                  if(res2.data.success){
                      console.log("表单数据2",res2);
                      this.model.xgsResumeWorks = res2.data.result;
                  }
                  if(res3.data.success){
                      console.log("表单数据3",res3);
                      this.model.xgsResumeEdus = res3.data.result;
                  }
                  if(res4.data.success){
                      console.log("表单数据4",res4);
                      this.model.xgsResumeHome = res4.data.result;
                  }
              })
            },
            // 添加工作经历
            addWorkExperience() {
                this.model.xgsResumeWorks.push({
                    id: this.uuid(),
                    beginDate: '',
                    endDate: '',
                    workUnit: '',
                    deptment: '',
                    position: '',
                    professionLevel: ''
                });
                this.model = Object.assign({},this.model);
            },
            // 删除工作经历
            removeWorkExperience(index) {
                this.model.xgsResumeWorks.splice(index, 1);
            },
            // 添加教育经历
            addEducationExperience() {
                this.model.xgsResumeEdus.push({
                    id: this.uuid(),
                    graduateCollege: '',
                    profession: '',
                    education: '',
                    degree: '',
                    beginDate: '',
                    endDate: ''
                });
                this.model = Object.assign({},this.model);
            },
            // 删除教育经历
            removeEducationExperience(index) {
                this.model.xgsResumeEdus.splice(index, 1);
            },
            // 添加家庭成员
            addFamilyMember() {
              console.log("添加家庭成员",this.uuid(),this.model.xgsResumeHome);
                this.model.xgsResumeHome.push({
                    id: this.uuid(),
                    relation: '',
                    name: '',
                    sex: '',
                    nationality: '',
                    nativePlace: '',
                    birthday: '',
                    nation: '',
                    idNumber: '',
                    politicBackground: '',
                    hukou: '',
                    graduateCollege: '',
                    education: '',
                    degree: '',
                    profession: '',
                    graduateDate: '',
                    workDate: '',
                    workUnit: ''
                });
                this.model = Object.assign({},this.model);
            },
            // 删除家庭成员
            removeFamilyMember(index) {
                this.model.xgsResumeHome.splice(index, 1);
            },
            onSubmit() {
                let myForm = {...this.model};
                this.loading = true;
                let url = myForm.id?this.url.edit:this.url.add;
                this.$http.post(url,myForm).then(res=>{
                  console.log("res",res)
                  // 这里弹出提示框
                  uni.showToast({
                    title: res.data.message,
                    icon: 'success'
                  });
                  this.loading = false
                  setTimeout(()=>{
                    this.$Router.push({name:this.backRouteName})
                  },1500)
                }).catch(()=>{
                  this.loading = false
                });
            }
        }
    }
</script>

<style lang="scss" scoped>
.work-experience-item,
.education-experience-item,
.family-member-item {
    margin-bottom: 20rpx;
    padding: 20rpx;
    border: 1px solid #eee;
    border-radius: 8rpx;
}
</style>
