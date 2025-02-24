<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">简历填写</block>
		</cu-custom>
		 <!--表单区域-->
		<view v-if="labelStatus">
			<view>
				<resumeApplyForm ref="resumeApplyForm" style="display: none;" :formData="params"></resumeApplyForm>
				<view v-show="formNumber === 1">
					<resumeBaseForm ref="resumeBaseForm">1</resumeBaseForm>
				</view>
				<resumeWorkForm ref="resumeWorkForm" v-show="formNumber === 2">2</resumeWorkForm>
				<resumeEduForm ref="resumeEduForm" v-show="formNumber === 3">3</resumeEduForm>
				<resumeHomeForm ref="resumeHomeForm" v-show="formNumber === 4">4</resumeHomeForm>
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
	import resumeWorkForm from '@/pages/resume/resumeWorkForm.vue'
	import resumeEduForm from '@/pages/resume/resumeEduForm.vue'
	import resumeHomeForm from '@/pages/resume/resumeHomeForm.vue'

    export default {
        name: "简历填写",
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
					submissionUrl:'/positions/xgsPositionApply/doPositionApply'
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
				formNumber: 0,
				labelStatus: false
            }
        },
		watch: {
			cur: {
				immediate: true,
				handler() {
				    this.userId=this.$store.getters.userid;
				},
			},
		},
        created(){
             this.initFormData();
        },
        methods:{
           initFormData(){
			   //获取岗位信息
			   // this.model = this.$Route.query;
			   this.model = uni.$globalParams;;
			   this.params.positionId = this.model.id;
			   this.params.positionName = this.model.positionName;
			   this.params.positionDept = this.model.dept_dictText;
			   this.params.positionKtz = this.model.ktz_dictText;
			   this.params.positionCount = this.model.personCount;
			   
			   this.params.status = "草稿";
			   this.params.positionType = this.model.category;
			   
			   //获取用户信息
			   let userId = this.$store.getters.userid;
			   this.$http.get(this.url.userUrl,{params:{id: userId}}).then(res=>{
					if (res.data.success) {
						let perArr = res.data.result
						let avatar=(perArr.avatar && perArr.avatar.length > 0)? api.getFileAccessHttpUrl(perArr.avatar):'/static/avatar_boy.png'
						this.params.userId = perArr.id
						this.params.userName = perArr.username
						this.labelStatus = true
					}
			   }).catch(err => {
					console.log(err);
			   });
			   
			   this.formNumber = 1;
            },
			//上一步
			onUpPage(){
				this.formNumber --
			},
			//下一步
			onNextPage(){
				if(this.validateAndNext()){		
					this.formNumber ++
				}else{
					this.$tip.toast('有信息未填写');
				}	
			},
			//提交岗位（对于‘新增’和‘编辑’）
            onSubmit() {
				if(this.validateAndNext()){
					let xgsPositionApplyVO = {}
					xgsPositionApplyVO.xgsPositionApply = this.$refs.resumeApplyForm.formSubmission()
					xgsPositionApplyVO.xgsResumeBasePage = this.$refs.resumeBaseForm.formSubmission()
				}else{
					this.$tip.toast('有信息未填写');
				}
            },
			//判断信息是否全部填写
			validateAndNext(){
				let inputs = document.querySelectorAll('input[type="text"], input[type="password"], input[type="email"], input[type="number"], textarea');
				let allFilled = true;
				
				let radios = document.querySelectorAll('uni-radio-group');
				console.log("radioNum",this.$refs.resumeBaseForm.radioNum);
				if(radios.length === this.$refs.resumeBaseForm.radioNum){
					console.log("ok")
				}else{
					console.log("不ok")
				}
				
				let num1 = 0;
				let num2 = 0;
				// 遍历每个input元素并检查其值
				inputs.forEach(function(input) {
					// 检查输入框是否可见
					if (input.offsetParent !== null) { // offsetParent为null表示元素不可见
						num1++
						// 忽略disabled和readonly的input元素
						if (!input.value || input.value.trim() === '') {
							num2++
							allFilled = false;
						}
					}
				});
				
				console.log("num1",num1)
				console.log("num2",num2)
				
				// 根据检查结果显示消息
				if (allFilled) {
					console.log('所有输入字段都已填写。');
					return true
				} else {
					console.log('有输入字段未填写。');
					return false
				}
			}
        }
    }
</script>
