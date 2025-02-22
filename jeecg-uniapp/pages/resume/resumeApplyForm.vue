<template>
    <view>
        <!--标题和返回-->
<!-- 		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">申请测试</block>
		</cu-custom> -->
		 <!--表单区域-->
		<view>
			<form>
				<view class="cu-form-group">
				  <view class="flex align-center">
				    <view class="title"><text space="ensp">申请人ID：</text></view>
				    <input  placeholder="请输入申请人ID" v-model="model.userId"/>
				  </view>
				</view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">申请人：</text></view>
                  <input  placeholder="请输入申请人" v-model="model.userName"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位名称：</text></view>
                  <input  placeholder="请输入岗位名称" v-model="model.positionName"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位部门：</text></view>
                  <input  placeholder="请输入岗位部门" v-model="model.positionDept"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">岗位类型：</text></view>
                  <input  placeholder="请输入岗位类型" v-model="model.positionType"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">申请状态：</text></view>
                  <input  placeholder="请输入申请状态" v-model="model.status"/>
                </view>
              </view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

    export default {
        name: "ApplyTestForm",
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
                  queryById: "/xgsUserResumes/applyTest/queryById",
                  add: "/xgsUserResumes/applyTest/add",
                  edit: "/xgsUserResumes/applyTest/edit",
                },
            }
        },
        created(){
             this.parameterConfig();
        },
        methods:{
           formSubmission(){
               
            },
			parameterConfig(){
				this.model = {...this.formData}
			}
        }
    }
</script>
