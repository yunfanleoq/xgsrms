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
					<view class="title"><text space="ensp">简历名称：</text></view>
					<input  placeholder="请输入简历名称" v-model="model.resumeName"/>
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
                  <!-- <input  placeholder="请输入是否应届毕业生" v-model="model.yjbys"/> -->
					<radio-group @change="(e) => radioChange(e,'yjbys')" class="horizontal-radio-group">
						<label class="radio-label">
							<view class="radio-container">
								<radio value="是" :checked="model.yjbys === '是'"/>
							</view>
							<view>是</view>
						</label>
						<label class="radio-label">
							<view class="radio-container">
								<radio value="否" :checked="model.yjbys === '否'"/>
							</view>
							<view>否</view>
						</label>
					</radio-group>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">是否统招统分：</text></view>
                  <!-- <input  placeholder="请输入是否统招统分" v-model="model.tztf"/> -->
					<radio-group @change="(e) => radioChange(e,'tztf')" class="horizontal-radio-group">
						<label class="radio-label">
							<view class="radio-container">
								<radio value="是" :checked="model.tztf === '是'"/>
							</view>
							<view>是</view>
						</label>
						<label class="radio-label">
							<view class="radio-container">
								<radio value="否" :checked="model.tztf === '否'"/>
							</view>
							<view>否</view>
						</label>
					</radio-group>
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
				  <!-- <input  placeholder="请输入毕业时间" v-model="model.graduateDate"/> -->
					<picker mode="date" :value="model.graduateDate" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'graduateDate')">
						<view class="uni-input" v-if="graduateDate">{{graduateDate}}</view>
						<view class="uni-input" v-else>
							<input  placeholder="请输入毕业时间" style="pointer-events: none;"/>
						</view>
					</picker>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
					<view class="title"><text space="ensp">参加工作时间：</text></view>
                  <!-- <input  placeholder="请输入参加工作时间" v-model="model.workDate"/> -->
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
					<view class="title"><text space="ensp">任职时间：</text></view>
					<!-- <input  placeholder="请输入任职时间" v-model="model.adminPositionDate"/> -->
					<picker mode="date" :value="model.adminPositionDate" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'adminPositionDate')">
						<view class="uni-input" v-if="adminPositionDate">{{adminPositionDate}}</view>
						<view class="uni-input" v-else>
							<input  placeholder="请输入任职时间" style="pointer-events: none;"/>
						</view>
					</picker>					
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
					<!-- <input  placeholder="请输入聘任时间" v-model="model.professionLevelData"/> -->
					<picker mode="date" :value="model.professionLevelData" :start="startDate" :end="endDate" @change="(e) => bindDateChange(e,'professionLevelData')">
						<view class="uni-input" v-if="professionLevelData">{{professionLevelData}}</view>
						<view class="uni-input" v-else>
							<input  placeholder="请输入聘任时间" style="pointer-events: none;"/>
						</view>
					</picker>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center" style="flex-direction: column; height: auto;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">与本所职工(包括在读研究生)是否有夫妻关系、直系血亲关系、三代以内旁系血亲或者近姻亲关系(若有请写出姓名)?：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<input  placeholder='若有请写出姓名否则填"无"' v-model="model.xgsRelation" style="width: 100%;"/>
					</view>
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
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究方向与专长：</text></view>
                  <input  placeholder="请输入研究方向与专长" v-model="model.researchDirection"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">承担科研、管理工作情况：（根据重要程度列举，不超5项）</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入承担科研、管理工作情况" v-model="model.researchWork" maxlength="-1"/>
					</view>
                </view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">工作主要业绩：（根据重要程度列举，不超5项）</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入工作主要业绩" v-model="model.reseachResult" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">近五年发表的代表性论文、专著及授权专利目录（最多列举10项）</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入论文专著专利" v-model="model.researchPaper" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">应聘岗位陈述（简述对岗位的理解、个人应聘优势、工作设想等）</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入应聘岗位陈述" v-model="model.positionDescription" maxlength="-1"/>
					</view>
				</view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位类型：</text></view>
                  <!-- <input  placeholder="请输入岗位类型" v-model="model.positionType"/> -->
					<radio-group @change="(e) => radioChange(e,'positionType')" class="horizontal-radio-group">
						<label class="radio-label">
							<view class="radio-container">
								<radio value="科研" :checked="model.positionType === '科研'"/>
							</view>
							<view>科研</view>
						</label>
						<label class="radio-label">
							<view class="radio-container">
								<radio value="管理" :checked="model.positionType === '管理'"/>
							</view>
							<view>管理</view>
						</label>
						<label class="radio-label">
							<view class="radio-container">
								<radio value="支撑" :checked="model.positionType === '支撑'"/>
							</view>
							<view>支撑</view>
						</label>
					</radio-group>
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
              <view class="cu-form-group" style="display: none;">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">本人照片：</text></view>
                  <input  placeholder="请输入本人照片" v-model="model.photograph"/>
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
        created(){
			this.getStartDate();
			this.getEndDate();
			// this.currentDate();
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
				}
				if (!this.radioList.includes(radioName)) {
					this.radioList.push(radioName);
					this.radioNum++
				}
				console.log("list",this.radioList)
				console.log("radioName",this.radioNum)
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
				this.birthday = this.getDate('news');
			}
			
        }
    }
</script>
