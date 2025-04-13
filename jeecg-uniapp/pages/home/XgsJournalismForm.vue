<template>
    <view>
        <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">头条新闻</block>
		</cu-custom>
		 <!--表单区域-->
		<view>
			<form>
          <view class="cu-form-group" v-if="false">
            <view class="flex align-center">
              <view class="title"><text>{{model.journalismHead}}</text></view>
            </view>
          </view>
          <view class="cu-form-group">
            <view class="flex align-center">
              <view class="title"><rich-text space="ensp" :nodes="model.journalismText"></rich-text></view>
            </view>
          </view>
			</form>
		</view>
    </view>
</template>

<script>
    import myDate from '@/components/my-componets/my-date.vue'

    export default {
        name: "XgsJournalismForm",
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
                  queryById: "/xgsJournalism/xgsJournalism/queryById",
                  add: "/positions/xgsJournalism/add",
                  edit: "/positions/xgsJournalism/edit",
                },
            }
        },
        onLoad(options){
            this.initFormData();
        },
        methods:{
            initFormData(){
                if(this.$Route){
                    let params = this.$Route.query;
                    let dataId = params.id;
                    this.$http.get(this.url.queryById,{params:{id:dataId}}).then((res)=>{
                        if(res.data.success){
                            console.log("表单数据",res);
                            this.model = res.data.result;
                        }
                    })
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

<style scoped>
    .cu-form-group .title {
        min-width: calc(4em + 15px);
		align-content: center;
		text-align: center;
		font-size: 30rpx;
		font-weight: 600;
		color: #333333;
		margin-right: 20rpx
    }
</style>