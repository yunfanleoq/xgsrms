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
                  <input  placeholder="请输入招聘部门" v-model="model.dept" :disabled="htmlTypeStatce"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">课题组：</text></view>
                  <input  placeholder="请输入课题组" v-model="model.ktz" :disabled="htmlTypeStatce"/>
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
					<button class="cu-btn block bg-blue margin-tb-sm lg" @click="onSubmit">
						<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>提交
					</button>
				</view>
				<view class="padding" v-else-if="model.htmlType == '招聘'">
					<button class="cu-btn block bg-blue margin-tb-sm lg" @click="onCollectDel" v-if="isCollected">
						已收藏
					</button>
					<button class="cu-btn block bg-blue margin-tb-sm lg" @click="onCollectAdd" v-else>
						收藏岗位
					</button>
					<button class="cu-btn block bg-blue margin-tb-sm lg" @click="onApply">
						<text v-if="loading" class="cuIcon-loading2 cuIconfont-spin"></text>申请
					</button>
				</view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

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
                },
				htmlTypeStatce: true,
				isCollected: false
            }
        },
        created(){
             this.initFormData();
        },
        methods:{
           initFormData(){
			   this.model = this.$Route.query;
			   console.log("this.model===========",this.model)
			   
			   let userInfo = uni.getStorageSync('userInfo');
			   console.log("userInfo===========",userInfo)
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
				
			},
			//收藏岗位
			onCollectAdd(){
				
			},
			//取消收藏岗位
			onCollectDel(){
				
			}
        }
    }
</script>
