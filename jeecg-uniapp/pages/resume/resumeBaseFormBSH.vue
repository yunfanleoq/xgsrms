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
                  <view class="title"><text space="ensp">研究室：</text></view>
                  <input  placeholder="请输入研究室" v-model="model.lob"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究方向：</text></view>
                  <input  placeholder="请输入研究方向" v-model="model.researchOrientation"/>
                </view>
              </view>  
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
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">籍贯：</text></view>
                  <input  placeholder="请输入籍贯" v-model="model.nativePlace"/>
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
                  <view class="title"><text space="ensp">民族：</text></view>
                  <input  placeholder="请输入民族" v-model="model.nation"/>
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
                  <view class="title"><text space="ensp">婚姻状况：</text></view>
                  <input  placeholder="请输入婚姻状况" v-model="model.maritalStatus"/>
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
                  <view class="title"><text space="ensp">户口所在地：</text></view>
                  <input  placeholder="请输入户口所在地" v-model="model.hukou"/>
                </view>
              </view>
			  <view class="cu-form-group">
                <view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
                  <view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
					  <text space="ensp">申请人当前身份：</text>
				  </view>
					<radio-group @change="(e) => radioChange(e,'proposerStatus')" class="horizontal-radio-group" style="flex-direction: column; height: auto; width: 100%;">
						<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
							<view class="radio-container" style=" margin-right: 10px;">
								<radio value="国家统招统分" :checked="model.proposerStatus === '国家统招统分'"/>
							</view>
							<view>国家统招统分</view>
						</label>
						<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
							<view class="radio-container" style=" margin-right: 10px;">
								<radio value="定向委培" :checked="model.proposerStatus === '定向委培'"/>
							</view>
							<view>定向委培</view>
						</label>
						<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
							<view class="radio-container" style=" margin-right: 10px;">
								<radio value="在职人员" :checked="model.proposerStatus === '在职人员'"/>
							</view>
							<view>在职人员</view>
						</label>
						<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
							<view class="radio-container" style=" margin-right: 10px;">
								<radio value="现役军人" :checked="model.proposerStatus === '现役军人'"/>
							</view>
							<view>现役军人</view>
						</label>
						<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
							<view class="radio-container" style=" margin-right: 10px;">
								<radio value="留学回国" :checked="model.proposerStatus === '留学回国'"/>
							</view>
							<view>留学回国</view>
						</label>
						<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
							<view class="radio-container" style=" margin-right: 10px;">
								<radio value="其他" :checked="model.proposerStatus === '其他'"/>
							</view>
							<view>其他</view>
						</label>
					</radio-group>
			    </view>
			  </view>
              <view class="cu-form-group">
                <view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
                  <view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
					  <text space="ensp">申报博士后类型：</text>
				  </view>
				  <radio-group @change="(e) => radioChange(e,'postdoctorType')" class="horizontal-radio-group" style="flex-direction: column; height: auto; width: 100%;">
				  	<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
				  		<view class="radio-container" style=" margin-right: 10px;">
				  			<radio value="流动站招收" :checked="model.postdoctorType === '流动站招收'"/>
				  		</view>
				  		<view>流动站招收</view>
				  	</label>
				  	<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
				  		<view class="radio-container" style=" margin-right: 10px;">
				  			<radio value="与工作站联合招收" :checked="model.postdoctorType === '与工作站联合招收'"/>
				  		</view>
				  		<view>与工作站联合招收</view>
				  	</label>
				  	<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
				  		<view class="radio-container" style=" margin-right: 10px;">
				  			<radio value="留学博士计划外招收" :checked="model.postdoctorType === '留学博士计划外招收'"/>
				  		</view>
				  		<view>留学博士计划外招收</view>
				  	</label>
				  	<label class="radio-label" style="align-items: center; margin-bottom: 10px;">
				  		<view class="radio-container" style=" margin-right: 10px;">
				  			<radio value="其它" :checked="model.postdoctorType === '其它'"/>
				  		</view>
				  		<view>其它</view>
				  	</label>
				  </radio-group>
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
					<view class="title"><text space="ensp">博士学位证书 获得时间：</text></view>
					<picker mode="date" :value="model.certificateTime" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'certificateTime')">
						<view class="uni-input" v-if="certificateTime">{{certificateTime}}</view>
						<view class="uni-input" v-else>
							<input  placeholder="请输入博士学位证书" style="pointer-events: none;"/>
						</view>
					</picker>	
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
					<view class="title"><text space="ensp">参加工作时间：</text></view>
					<picker mode="date" :value="model.workDate" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'workDate')">
						<view class="uni-input" v-if="workDate">{{workDate}}</view>
						<view class="uni-input" v-else>
							<input  placeholder="请输入参加工作时间" style="pointer-events: none;"/>
						</view>
					</picker>
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
                  <view class="title"><text space="ensp">现专业技术职务：</text></view>
                  <input  placeholder="请输入现专业技术职务" v-model="model.technicalPosition"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">在站期间研究内容有无涉密内容：</text></view>
				  <radio-group @change="(e) => radioChange(e,'secretText')" class="horizontal-radio-group">
				  	<label class="radio-label">
				  		<view class="radio-container">
				  			<radio value="是" :checked="model.secretText === '是'"/>
				  		</view>
				  		<view>是</view>
				  	</label>
				  	<label class="radio-label">
				  		<view class="radio-container">
				  			<radio value="否" :checked="model.secretText === '否'"/>
				  		</view>
				  		<view>否</view>
				  	</label>
				  </radio-group>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">一站单位名称（二站博士后填写）：</text></view>
                  <input  placeholder="请输入一站单位名称" v-model="model.workFirst"/>
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
                  <view class="title"><text space="ensp">联系电话：</text></view>
                  <input  placeholder="请输入联系电话" v-model="model.mobile"/>
                </view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">申请人在学术刊物或会议上发表的有代表性的论文和专著情况（请注明论文或专著名称、学术刊物或会议名称以及发表时间）：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入代表性的论文和专著" v-model="model.researchWorks" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">申请人获科研成果奖励、申请科学基金和专利情况（请注明奖励、基金和专利的具体名称和内容）：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入相关奖励、基金和专利" v-model="model.researchPaper" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">博士后研究工作计划：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入博士后研究工作计划" v-model="model.researchProposal" maxlength="-1"/>
					</view>
				</view>
              </view>
			  <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">合作导师对申请人提出的研究计划的评价（如可行性、先进性、创新之处、理论和实用意义）及博士后经费的出处，请注明课题编号：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入导师评价" v-model="model.teacherEvaluate" maxlength="-1"/>
					</view>
				</view>
			  </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">合作导师签字：</text></view>
                  <input  placeholder="请输入合作导师签字" v-model="model.teacherSignature"/>
                </view>
              </view>
			  <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">研究室意见：</text>
					</view>
					<radio-group @change="(e) => radioChange(e,'labIdea')" class="horizontal-radio-group" style="width: 100%; margin-top: 10px;">
						<label class="radio-label">
							<view class="radio-container">
								<radio value="科研" :checked="model.labIdea === '科研'"/>
							</view>
							<view>科研</view>
						</label>
						<label class="radio-label">
							<view class="radio-container">
								<radio value="在职" :checked="model.labIdea === '在职'"/>
							</view>
							<view>在职</view>
						</label>
						<label class="radio-label">
							<view class="radio-container">
								<radio value="博士后" :checked="model.labIdea === '博士后'"/>
							</view>
							<view>博士后</view>
						</label>
					</radio-group>
				</view>
			  </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究室负责人：</text></view>
                  <input  placeholder="请输入研究室负责人" v-model="model.labSignature"/>
                </view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">人力资源处意见：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入人力资源处意见" v-model="model.resourcesIdea" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">人力资源处负责人：</text></view>
                  <input  placeholder="请输入人力资源处负责人" v-model="model.resourceSsignature"/>
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
                  <view class="title"><text space="ensp">主管所领导：</text></view>
                  <input  placeholder="请输入主管所领导" v-model="model.schoolIdea"/>
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
				let radioValueList = [this.model.sex, this.model.secretText, this.model.proposerStatus, this.model.postdoctorType]
				let radioNameList = ["sex", "secretText", "proposerStatus", "postdoctorType"]
				
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
