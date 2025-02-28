<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack>
			<block slot="backText" @click="backHome">返回</block>
			<block slot="content">简历填写</block>
		</cu-custom>
		 <!--表单区域-->
		<view v-if="labelStatus == 2">
			 <view class="cu-form-group">
			   <view class="flex align-center">
				<view class="title"><text space="ensp">简历名称：</text></view>
					<picker @change="bindPickerChange" :value="myResumeIndex" :range="myResumeNameList">
						<view class="uni-input">{{myResumeNameList[myResumeIndex]}}</view>
					</picker>
			   </view>
			 </view>
			<view>
				<resumeApplyForm ref="resumeApplyForm" style="display: none;" :formData="params"></resumeApplyForm>
				<view v-show="formNumber === 1 && formTest">
					<resumeBaseForm ref="resumeBaseForm" :formData="formData"></resumeBaseForm>
				</view>
				<view v-show="formNumber === 1 && params.positionType === '普通岗位' && !formTest">
					<resumeBaseFormPT ref="resumeBaseFormPT" :formData="formData"></resumeBaseFormPT>
				</view>
				<view v-show="formNumber === 1 && params.positionType === '副高级以上岗位' && !formTest">
					<resumeBaseFormFG ref="resumeBaseFormFG" :formData="formData"></resumeBaseFormFG>
				</view>
				<view v-show="formNumber === 1 && params.positionType === '博士后岗位' && !formTest">
					<resumeBaseFormBSH ref="resumeBaseFormBSH" :formData="formData"></resumeBaseFormBSH>
				</view>
				<view v-show="formNumber === 1 && params.positionType === '人才派遣岗位' && !formTest">
					<resumeBaseFormTJ ref="resumeBaseFormTJ" :formData="formData"></resumeBaseFormTJ>
				</view>
				<resumeWorkList ref="resumeWorkList" v-show="formNumber === 2"></resumeWorkList>
				<resumeEduList ref="resumeEduList" v-show="formNumber === 3"></resumeEduList>
				<resumeHomeList ref="resumeHomeList" v-show="formNumber === 4"></resumeHomeList>
			</view>
			<button v-if="formNumber > 1" class="cu-btn block bg-blue margin-tb-sm lg" @tap="onUpPage">
				<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>上一步
			</button>
			<button v-if=" 0 < formNumber && formNumber < 4" class="cu-btn block bg-blue margin-tb-sm lg" @tap="onNextPage">
				<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>下一步
			</button>
			<button v-if=" formNumber === 4" class="cu-btn block bg-blue margin-tb-sm lg" @tap="onSubmit">
				<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>提交
			</button>
		</view>
		<view v-else>
			若长时间未加载出信息请重新登录
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'
	import api from '@/api/api'
	import resumeApplyForm from '@/pages/resume/resumeApplyForm.vue'
	import resumeBaseForm from '@/pages/resume/resumeBaseForm.vue'
	import resumeBaseFormPT from '@/pages/resume/resumeBaseFormPT.vue'
	import resumeBaseFormFG from '@/pages/resume/resumeBaseFormFG.vue'
	import resumeBaseFormBSH from '@/pages/resume/resumeBaseFormBSH.vue'
	import resumeBaseFormTJ from '@/pages/resume/resumeBaseFormTJ.vue'
	import resumeWorkList from '@/pages/resume/resumeWorkList.vue'
	import resumeEduList from '@/pages/resume/resumeEduList.vue'
	import resumeHomeList from '@/pages/resume/resumeHomeList.vue'

    export default {
        name: "resume_form",
        components:{ myDate },
        props:{},
        data(){
            return {
				CustomBar: this.CustomBar,
				NavBarColor: this.NavBarColor,
				loading:false,
                model: {},
                backRouteName:'index',
                url: {
					queryById: "/positions/xgsPositions/queryById",
					add: "/positions/xgsPositions/add",
					edit: "/positions/xgsPositions/edit",
					userUrl:'/sys/user/queryById',
					xgsFavoriteJobAddUrl:'/positions/xgsFavoriteJob/add',
					xgsFavoriteJobDelUrl: '/positions/xgsFavoriteJob/delete',
					xgsFavoriteJobListUrl: '/positions/xgsFavoriteJob/list',
					submissionUrl: '/positions/xgsPositionApply/doPositionApply',
					myResumeUrl: '/xgsResume/xgsResumeBase/listMine'
                },
				htmlTypeStatce: true,
				isCollected: false,
				favoriteJob: "",
				params: {
					userId: "",
					userName: "",
					positionId: "",
					positionName: "",
					positionDept: "",
					positionKtz: "",
					positionCount: "",
				},
				xgsPositionApplyVO: {},
				formNumber: 0,
				labelStatus: 0,
				formTest: false,
				myResumeIndex: 0,
				myResumeList: [{"resumeName": "不选择"}],
				myResumeNameList: [],
				formData: {},
            }
        },
		watch: {
			cur: {
				immediate: true,
				handler() {
				    this.userId=this.$store.getters.userid;
				},
			},
			myResumeIndex(newVal, oldVal){
				if(newVal != oldVal){
					if(this.myResumeIndex > 0){
						this.xgsPositionApplyVO = this.myResumeList[this.myResumeIndex]
					}else{
						this.xgsPositionApplyVO = {}
					}
					this.formData = this.xgsPositionApplyVO
				}
			},
		},
        created(){
             this.initFormData();
        },
        methods : {
			backHome() {
				console.log('backhome');
				uni.navigateTo({
					url: 'pages/index/index?id=1'
				});
			},
           initFormData(){
			   //获取岗位信息
			   // this.model = this.$Route.query;
			   this.model = uni.$globalParams;
			   if(!this.model){
				   this.model = {}
				   this.model.id = sessionStorage.getItem("positionId")
				   this.$http.get(this.url.queryById,{params:{id: this.model.id}}).then(res=>{
						if (res.data.success) {
							this.model = {...res.data.result}
						   this.model.dept_dictText = sessionStorage.getItem("dept_dictText")
						   this.model.ktz_dictText = sessionStorage.getItem("ktz_dictText")
						   this.model.htmlType = sessionStorage.getItem("htmlType")
						   this.model.category = sessionStorage.getItem("category")
						   
						   this.params.positionId = this.model.id;
						   this.params.positionName = this.model.positionName;
						   this.params.positionDept = this.model.dept_dictText;
						   this.params.positionKtz = this.model.ktz_dictText;
						   this.params.positionCount = this.model.personCount;
						   
						   this.params.status = "草稿";
						   this.params.positionType = this.model.category;	
						}
				   }).catch(err => {
				   	console.log(err);
				   });
			   }else{
				   this.params.positionId = this.model.id;
				   this.params.positionName = this.model.positionName;
				   this.params.positionDept = this.model.dept_dictText;
				   this.params.positionKtz = this.model.ktz_dictText;
				   this.params.positionCount = this.model.personCount;
				   
				   this.params.status = "草稿";
				   this.params.positionType = this.model.category;				   
			   }
			   
			   //获取用户信息
			   let userId = this.$store.getters.userid;
			   this.$http.get(this.url.userUrl,{params:{id: userId}}).then(res=>{
					if (res.data.success) {
						let perArr = res.data.result
						let avatar=(perArr.avatar && perArr.avatar.length > 0)? api.getFileAccessHttpUrl(perArr.avatar):'/static/avatar_boy.png'
						this.params.userId = perArr.id
						this.params.userName = perArr.username
						this.labelStatus++
					}
			   }).catch(err => {
					console.log(err);
			   });
			   
			   //获取简历集合
			   this.$http.get(this.url.myResumeUrl,{params:{pageSize: 1000}}).then(res=>{
					if (res.data.success) {
						let resumeList = res.data.result.records
						this.myResumeNameList = this.myResumeList.map(item => item.resumeName);
						resumeList.forEach((input, index) => {
							if(input.resumeName){
								this.myResumeNameList.push(input.resumeName)
							}else{
								this.myResumeNameList.push("简历"+index)
							}
						})
						if(resumeList.length > 0){
							this.myResumeList = [...this.myResumeList, ...resumeList];
						}
					}
			   }).catch(err => {
					console.log(err);
			   }).finally(() =>{
				   this.labelStatus++
			   })
			   
			   this.formNumber = 1;
            },
			//上一步
			onUpPage(){
				this.formNumber --
			},
			//下一步
			onNextPage(){
				// if(this.validateAndNext()){		
					this.formNumber ++
				// }else{
				// 	this.$tip.toast('有信息未填写');
				// }	
			},
			//提交岗位（对于‘新增’和‘编辑’）
            onSubmit() {
				if(this.validateAndNext()){
					this.xgsPositionApplyVO.xgsPositionApply = this.$refs.resumeApplyForm.formSubmission()
					this.xgsPositionApplyVO.xgsResumeBasePage = this.$refs.resumeBaseForm.formSubmission()
				}else{
					this.$tip.toast('有信息未填写');
				}
            },
			//判断信息是否全部填写
			validateAndNext(){
				//测试使用（^_^;
				// this.xgsPositionApplyVO.xgsPositionApply = this.$refs.resumeApplyForm.formSubmission()
				// this.xgsPositionApplyVO.xgsResumeBasePage = this.$refs.resumeBaseForm.formSubmission()
				// console.log("vo",this.xgsPositionApplyVO)
				
				//判断单选框是否全部选择
				let radios = document.querySelectorAll('uni-radio-group');
				radios = Array.from(radios).filter(radio => radio.offsetParent !== null);
				let radioNum = -1;
				if(this.formTest){
					radioNum = this.$refs.resumeBaseForm.radioNum;
				}else{
					switch(this.params.positionType){
						case '普通岗位':
							radioNum = this.$refs.resumeBaseFormPT.radioNum;
							break;
						case '副高级以上岗位':
							radioNum = this.$refs.resumeBaseFormFG.radioNum;
							break;
						case '博士后岗位	':
							radioNum = this.$refs.resumeBaseFormBSH.radioNum;
							break;
						case '人才派遣岗位':
							radioNum = this.$refs.resumeBaseFormPT.radioNum;
							break;
					}
				}
				
				console.log(radios.length,"111",radioNum)
				if(radios.length === radioNum){
				}else{
					return false
				}
				//判断输入框是否全部选择
				let inputs = document.querySelectorAll('input[type="text"], input[type="password"], input[type="email"], input[type="number"], textarea');
				let allFilled = true;
				// 遍历每个input元素并检查其值
				inputs.forEach(function(input) {
					// 检查输入框是否可见
					if (input.offsetParent !== null) { // offsetParent为null表示元素不可见
						// 忽略disabled和readonly的input元素
						if (!input.value || input.value.trim() === '') {
							allFilled = false;
						}
					}
				});
				return allFilled
			},
			//简历选择
			bindPickerChange: function(e) {
				this.myResumeIndex = e.detail.value
			},
        }
    }
</script>
