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
						<view class="title"><text space="ensp">被推荐人：</text></view>
						<input  placeholder="请输入被推荐人" v-model="model.propositus"/>
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
                  <view class="title"><text space="ensp">推荐人：</text></view>
                  <input  placeholder="请输入推荐人" v-model="model.referrer"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人技术职务：</text></view>
                  <input  placeholder="请输入推荐人技术职务" v-model="model.referrerPosition"/>
                </view>
              </view>
              <view class="cu-form-group">
				<view class="flex align-center" style="flex-direction: column; height: auto; width: 100%;">
					<view class="title" style="white-space: normal; word-break: break-all; height: auto; width: 100%;">
						<text space="ensp">推荐意见（对被推荐人的代表作品及研究工作进行综合评价）：</text>
					</view>
					<view style="width: 100%; margin-top: 10px;">
						<textarea placeholder="请输入推荐意见" v-model="model.labIdea" maxlength="-1"/>
					</view>
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
                  <view class="title"><text space="ensp">推荐人工作单位：</text></view>
                  <input  placeholder="请输入推荐人工作单位" v-model="model.referrerUnit"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人专业技术职务：</text></view>
                  <input  placeholder="请输入推荐人专业技术职务" v-model="model.technicalPosition"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人联系电话：</text></view>
                  <input  placeholder="请输入推荐人联系电话" v-model="model.referrerMobile"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">推荐人电子邮箱：</text></view>
                  <input  placeholder="请输入推荐人电子邮箱" v-model="model.referrerEmail"/>
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
				let radioValueList = [this.model.sex, this.model.yjbys, this.model.tztf, this.model.secretText, this.model.positionType, this.model.proposerStatus, this.model.postdoctorType]
				let radioNameList = ["sex", "yjbys", "tztf", "secretText", "positionType", "proposerStatus", "postdoctorType"]
				
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
