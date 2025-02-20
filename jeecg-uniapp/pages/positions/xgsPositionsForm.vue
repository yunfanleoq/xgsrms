<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">岗位列表</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">招聘部门：</text></view>
                  <input  placeholder="请输入招聘部门" v-model="model.dept_dictText" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">课题组：</text></view>
                  <input  placeholder="请输入课题组" v-model="model.ktz_dictText" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">咨询电话：</text></view>
                  <input  placeholder="请输入咨询电话" v-model="model.telphone" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">邮箱：</text></view>
                  <input  placeholder="请输入邮箱" v-model="model.email" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位名称：</text></view>
                  <input  placeholder="请输入岗位名称" v-model="model.positionName" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">研究方向：</text></view>
                  <input  placeholder="请输入研究方向" v-model="model.researchDirection" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">招聘人数：</text></view>
                  <input  placeholder="请输入招聘人数" v-model="model.personCount" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位职责：</text></view>
                  <input  placeholder="请输入岗位职责" v-model="model.duty" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">学历学位：</text></view>
                  <input  placeholder="请输入学历学位" v-model="model.xlxw" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">专业：</text></view>
                  <input  placeholder="请输入专业" v-model="model.professional" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">工作年限：</text></view>
                  <input  placeholder="请输入工作年限" v-model="model.workYears" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">备注：</text></view>
                  <input  placeholder= "请输入备注" v-model="model.memo" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位状态：</text></view>
                  <input  placeholder="请输入岗位状态" v-model="model.status" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位分类：</text></view>
                  <input  placeholder="请输入岗位分类" v-model="model.category" :disabled="htmlTypeStatce"/>
                </view>
              </view>
				<view class="padding" v-if="model.htmlType == '编辑' || model.htmlType == '新增'">
					<button class="cu-btn block bg-blue margin-tb-sm lg" @tap="onSubmit">
						<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>提交
					</button>
				</view>
				<view class="padding" v-else-if="model.htmlType == '招聘'">
					<button class="cu-btn block bg-red margin-tb-sm lg" @tap="onCollectDel" v-show="isCollected">
						已收藏
					</button>
					<button class="cu-btn block bg-not-blue margin-tb-sm lg" @tap="onCollectAdd" v-show="!isCollected">
						收藏岗位
					</button>
					<button class="cu-btn block bg-blue margin-tb-sm lg" @tap="onApply">
						<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>申请
					</button>
				</view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'
	import api from '@/api/api'

    export default {
        name: "XgsPositionsForm",
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
				}
            }
        },
		watch: {
			cur: {
				immediate: true,
				handler() {
					console.log('watch',this.cur)
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
					console.log("res",res)
					if (res.data.success) {
						let perArr = res.data.result
						let avatar=(perArr.avatar && perArr.avatar.length > 0)? api.getFileAccessHttpUrl(perArr.avatar):'/static/avatar_boy.png'
						this.params.userId = perArr.id
						this.params.userName = perArr.username
				   
						console.log("params=-=========",this.params)
						this.onCollectList()
					}
			   }).catch(err => {
			   	console.log(err);
			   });
			   
			   console.log("model===========",this.model)
			   
			   //根据岗位id进行查询
               // if(this.formData){
               //      let dataId = this.formData.dataId;
               //      this.$http.get(this.url.queryById,{params:{id:dataId}}).then((res)=>{
               //          if(res.data.success){
               //              console.log("表单数据",res);
               //              this.model = res.data.result;
               //          }
               //      })
               //  }
            },
			//提交岗位（对于‘新增’和‘编辑’）
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
            },
			//岗位申请（对于‘招聘’）
			onApply(){
				// onCollectAdd(){
				// 	this.$http.get(this.url.userUrl,{params:{id: userId}}).then(res=>{
				// 						console.log("res",res)
										
				// 	}).catch(err => {
				// 		console.log(err);
				// 	});
				// },
			},
			//收藏岗位
			onCollectAdd(){
				console.log("params",this.params)
				this.$http.post(this.url.xgsFavoriteJobAddUrl,this.params).then(res=>{
					console.log("res",res)
					if (res.data.code == 200) {
						this.isCollected = true;
						this.$tip.success("收藏职位成功")
						this.onCollectList()
					} else {
						this.$tip.alert(res.data.message);
					}
				}).catch(err => {
					console.log(err);
				});
			},
			//取消收藏岗位
			onCollectDel(){
				if(this.favoriteJob === ""){
					return
				}
				this.$http.delete(this.url.xgsFavoriteJobDelUrl + "?id=" + this.favoriteJob).then(res=>{
					console.log("res",res)
					if (res.data.code === 200) {
						this.isCollected = false;
						this.$tip.success("取消收藏职位成功")
						this.favoriteJob = ""
					} else {
						this.$tip.success("取消收藏职位失败")
					}
				}).catch(err => {
					console.log(err);
				});
			},
			//查看岗位是否收藏
			onCollectList(){
				this.$http.get(this.url.xgsFavoriteJobListUrl,{params:{userId: this.params.userId, positionId: this.params.positionId}}).then(res=>{
					if (res.data.result.records.length > 0) {
					        this.isCollected = true;
					        this.favoriteJob = res.data.result.records[0].id;
					}
				}).catch(err => {
					console.log(err);
				});
			},
        }
    }
</script>
