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
                  <input placeholder="请输入姓名" v-model="model.name"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">性别：</text></view>
                  <!-- <input placeholder="请输入性别" v-model="model.sex"/> -->
				  <radio-group @change="(e) => radioChange(e,'sex')" class="horizontal-radio-group">
					<label class="radio-label">
						<view class="radio-container">
							<radio value="男" :checked="model.sex === '男'"/>
						</view>
						<view>男</view>
					</label>
					<label class="radio-label">
						<view class="radio-container">
							<radio value="女" :checked="model.sex === '女'"/>
						</view>
						<view>女</view>
					</label>
				  </radio-group>
                </view>
              </view>
			  <!-- <my-date label="出生年月：" v-model="model.birthday" placeholder="请输入出生年月"></my-date> -->
			  <view class="cu-form-group">
			    <view class="flex align-center">
					<view class="title"><text space="ensp">出生年月：</text></view>
					<view class="uni-list-cell-db">
						<picker mode="date" :value="model.birthday" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'birthday')">
							<view class="uni-input" v-if="birthday">{{birthday}}</view>
							<view class="uni-input" v-else>
								<input  placeholder="请输入出生年月" style="pointer-events: none;"/>
							</view>
						</picker>
					</view>
			    </view>
			  </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">籍贯：</text></view>
                  <input  placeholder="请输入籍贯" v-model="model.nativePlace"/>
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
                  <view class="title"><text space="ensp">身份证号：</text></view>
                  <input  placeholder="请输入身份证号" v-model="model.idNumber"/>
                </view>
              </view>							
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">民族：</text></view>
                  <input  placeholder="请输入民族" v-model="model.nation"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">从事专业：</text></view>
                  <input  placeholder="请输入从事专业" v-model="model.profession"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">现工作单位及职位：</text></view>
                  <input  placeholder="请输入现工作单位及职位" v-model="model.workUnit"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">户口所在地：</text></view>
                  <input  placeholder="请输入户口所在地" v-model="model.hukou"/>
                </view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">主要学术成就、科技成果及创新点：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入主要学术成就、科技成果及创新点" v-model="model.reseachResult" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">主要论著目录：（1.论文作者、题目、期刊名称、年份、卷期、页；2.著作：著者、书名、出版社、年份）：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入主要论著目录" v-model="model.researchWorks" maxlength="-1"/>
					</view>
				</view>
              </view>
			  <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">主持(参与)科研项目及申请专利：(项目来源、项目名称、经费、个人在其中的作用)</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入主持(参与)科研项目及申请专利" v-model="model.researchPaper" maxlength="-1"/>
					</view>
				</view>
			  </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">获科技奖情况：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入获科技奖情况" v-model="model.carryPrizeScience" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">获荣誉奖情况：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入获荣誉奖情况" v-model="model.carryPrizeHonor" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">受聘后拟开展研究工作的计划和思路（包括研究方向、内容和目标）：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入拟研究计划" v-model="model.researchProposal" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">所需科研条件：（包括科研经费、实验室面积、仪器设备、人员等，并简要说明所需条件的必要性和预算依据）：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入所需科研条件" v-model="model.researchCondition" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">其它说明及希望研究所协助解决的问题：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入研究问题协助" v-model="model.researchQuestionsSolve" maxlength="-1"/>
					</view>
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
				radioNum: 0,
				radioList: [],
				startDate: "",
				endDate: "",
				birthday: "",
				graduateDate: "",
				workDate: "",
				adminPositionDate: "",
				professionLevelData: "",
				certificateTime: "",
            }
        },
		watch: {
			formData() {
				this.currentFormData()
				this.currentDate()
				this.currentRadio()
			},
		},
        created(){
			this.getStartDate();
			this.getEndDate();
			this.currentFormData();
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
			formSubmission(){
				return this.model
            },
			radioChange(evt, radioName){
				switch(radioName){
					case "sex":
						this.model.sex = evt.detail.value;
						break;
					case "yjbys":
						this.model.yjbys = evt.detail.yjbys;
						break;
					case "tztf":
						this.model.tztf = evt.detail.value;
						break;
					case "secretText":
						this.model.secretText = evt.detail.value;
						break;
					case "positionType":
						this.model.positionType = evt.detail.value;
						break;
					case "proposerStatus":
						this.model.proposerStatus = evt.detail.value;
						break;
					case "postdoctorType":
						this.model.postdoctorType = evt.detail.value;
						break;
				}
				if (!this.radioList.includes(radioName)) {
					this.radioList.push(radioName);
					this.radioNum++
				}
			},
			bindDateChange: function(e,value) {
				switch(value){
					case "birthday":
						this.model.birthday = e.detail.value
						this.birthday = this.model.birthday
						break;
					case "graduateDate":
						this.model.graduateDate = e.detail.value
						this.graduateDate = this.model.graduateDate
						break;
					case "workDate":
						this.model.workDate = e.detail.value
						this.workDate = this.model.workDate
						break;
					case "adminPositionDate":
						this.model.adminPositionDate = e.detail.value
						this.adminPositionDate = this.model.adminPositionDate
						break;
					case "professionLevelData":
						this.model.professionLevelData = e.detail.value
						this.professionLevelData = this.model.professionLevelData
						break;
					case "certificateTime":
						this.model.certificateTime = e.detail.value
						this.certificateTime = this.model.certificateTime
						break;
				}
			},
			getDate(type) {
				const date = new Date();
				let year = date.getFullYear();
				let month = date.getMonth() + 1;
				let day = date.getDate();
				
				if(type === 'start'){
					year = year  - 100;
				}
				
				month = month > 9 ? month : '0' + month;
				day = day > 9 ? day : '0' + day;
				return `${year}-${month}-${day}`;
			},
			getStartDate() {
				this.startDate = this.getDate('start');
			},
			getEndDate() {
				this.endDate = this.getDate('end');
			},
			currentDate(){
				this.birthday = this.model.birthday
				this.graduateDate = this.model.graduateDate
				this.workDate = this.model.workDate
				this.adminPositionDate = this.model.adminPositionDate
				this.professionLevelData = this.model.professionLevelData
				this.certificateTime = this.model.certificateTime
			},
			currentFormData(){
				this.model = {...this.formData}
			},
			currentRadio(){
				this.radioNum = 0
				this.radioList = []
				let radioValueList = [this.model.sex]
				let radioNameList = ["sex"]
				
				radioValueList.forEach((radioValue, index) => {
					if(radioValue){
						if (!this.radioList.includes(radioNameList[index])) {
							this.radioList.push(radioNameList[index]);
							this.radioNum++
						}
					}
				})
			}
        }
    }
</script>
