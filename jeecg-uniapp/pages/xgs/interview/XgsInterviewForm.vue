<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">面试详情</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位名称</text></view>
                  <input  placeholder="请输入职位名称" v-model="model.positionName"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">候选人：</text></view>
                  <input  placeholder="请输入候选人" v-model="model.candidate"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">面试时间：</text></view>
                  <input  placeholder="请输入面试官" v-model="model.interviewDate"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">面试地点：</text></view>
                  <input  placeholder="请输入面试官" v-model="model.interviewPlace"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">邀请信息</text></view>
                  <input  placeholder="请输入面试状态" v-model="model.inviteLetter"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">面试结果</text></view>
                  <input  placeholder="请输入面试反馈" v-model="model.interviewResult"/>
                </view>
              </view>
            <view class="padding">
              <button class="cu-btn block bg-blue margin-tb-sm lg" @click="onSubmit" v-if="false">
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
        name: "XgsInterviewForm",
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
                  queryById: "/xgsInviteToInterview/xgsInviteToInterview/queryById",
                  add: "/xgsInterview/xgsInterview/add",
                  edit: "/xgsInterview/xgsInterview/edit",
                },
            }
        },
        onLoad(options){
             this.initFormData();
        },
        methods:{
           initFormData(){
              if(this.$Route){
                    let dataId = this.$Route.query.dataId;
                    this.$http.get(this.url.queryById,{params:{id:dataId}}).then((res)=>{
                        if(res.data.success){
                            console.log("表单数据",res);
                            this.model = res.data.result;
                        }
                    });
                }
            },
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
            }
        }
    }
</script>
