<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">简历填写</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<resumeApplyForm style="display: none;"></resumeApplyForm>
			<resumeBaseForm v-show="formNumber === 1">1</resumeBaseForm>
			<resumeWorkForm v-show="formNumber === 2">2</resumeWorkForm>
			<resumeEduForm v-show="formNumber === 3">3</resumeEduForm>
			<resumeHomeForm v-show="formNumber === 4">4</resumeHomeForm>
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
        props:{
			formData:{
				type:Object,
				default:()=>{},
				required:false,
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
					queryById: "/positions/xgsPositions/queryById",
					add: "/positions/xgsPositions/add",
					edit: "/positions/xgsPositions/edit",
					userUrl:'/sys/user/queryById',
					xgsFavoriteJobAddUrl:'/positions/xgsFavoriteJob/add',
					xgsFavoriteJobDelUrl: '/positions/xgsFavoriteJob/delete',
					xgsFavoriteJobListUrl: '/positions/xgsFavoriteJob/list',
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
				formNumber: 0
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
			   this.model = this.$Route.query;
			   this.params.positionId = this.model.id;
			   this.params.positionName = this.model.positionName;
			   this.params.positionDept = this.model.dept_dictText;
			   this.params.positionKtz = this.model.ktz_dictText;
			   this.params.positionCount = this.model.personCount;
			   
			   //获取用户信息
			   let userId = this.$store.getters.userid;
			   this.$http.get(this.url.userUrl,{params:{id: userId}}).then(res=>{
					if (res.data.success) {
						let perArr = res.data.result
						let avatar=(perArr.avatar && perArr.avatar.length > 0)? api.getFileAccessHttpUrl(perArr.avatar):'/static/avatar_boy.png'
						this.params.userId = perArr.id
						this.params.userName = perArr.username
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
				if(resumeApplyForm.onNextPage()){
					console.log("aaa/")
				}
				this.formNumber ++
			},
			//提交岗位（对于‘新增’和‘编辑’）
            onSubmit() {
            },
        }
    }
</script>
