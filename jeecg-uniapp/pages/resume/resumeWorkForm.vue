<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">工作经历测试</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<form>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">工作单位：</text></view>
                  <input  placeholder="请输入工作单位" v-model="model.workUnit"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">部门：</text></view>
                  <input  placeholder="请输入部门" v-model="model.department"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">职位：</text></view>
                  <input  placeholder="请输入职位" v-model="model.position"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">职称：</text></view>
                  <input  placeholder="请输入职称" v-model="model.professionLevel"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">创建者：</text></view>
                  <input  placeholder="请输入创建者" v-model="model.createBy"/>
                </view>
              </view>
              <my-date label="创建时间：" v-model="model.createTime" placeholder="请输入创建时间"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">更新者：</text></view>
                  <input  placeholder="请输入更新者" v-model="model.updateBy"/>
                </view>
              </view>
              <my-date label="更新时间：" v-model="model.upateTime" placeholder="请输入更新时间"></my-date>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">是否删除：</text></view>
                  <input  placeholder="请输入是否删除" v-model="model.deleted"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">租户ID：</text></view>
                  <input  placeholder="请输入租户ID" v-model="model.tenantId"/>
                </view>
              </view>
              <view class="cu-form-group">
                <view class="flex align-center">
                  <view class="title"><text space="ensp">编码：</text></view>
                  <input  placeholder="请输入编码" v-model="model.sysOrgCode"/>
                </view>
              </view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

    export default {
        name: "WorkTestForm",
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
                  queryById: "/xgsUserResumes/workTest/queryById",
                  add: "/xgsUserResumes/workTest/add",
                  edit: "/xgsUserResumes/workTest/edit",
                },
            }
        },
        created(){
             this.initFormData();
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
        }
    }
</script>
