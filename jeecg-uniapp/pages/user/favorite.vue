<template>
	<view>
	   <!--标题和返回-->
		<cu-custom :bgColor="NavBarColor" isBack :backRouterName="backRouteName">
			<block slot="backText">返回</block>
			<block slot="content">岗位列表</block>
		</cu-custom>
		<!--滚动加载列表-->
		<mescroll-body ref="mescrollRef" bottom="88"  @init="mescrollInit" :up="positionsList" :down="downOption" @down="downCallback" @up="upCallback">
		    <view class="cu-list menu">
				<view class="cu-item" v-for="(item,index) in favoriteList" :key="index" @tap="goToDetail(item)">
					<view class="flex" style="width:100%">
                        <text class="text-lg" style="color: #000;">
                             {{ item.positionName}}
                        </text>
						<text class="text-lg" style="color: #999; font-size: 12px;">
						     {{ item.positionDept}}
						</text>
					</view>
				</view>
			</view>
		</mescroll-body>
	</view>
</template>

<script>
	import MescrollMixin from "@/components/mescroll-uni/mescroll-mixins.js";
	import Mixin from "@/common/mixin/Mixin.js";

	export default {
		name: 'positions_list',
		mixins: [MescrollMixin,Mixin],
		data() {
			return {
				CustomBar:this.CustomBar,
				NavBarColor:this.NavBarColor,
				url: {
					xgsFavoriteJobListUrl: '/positions/xgsFavoriteJob/list',
				},
				favoriteList:[],
				params: {
					userId: "",
					userName: "",
					positionId: "",
					positionName: "",
					positionDept: "",
					positionKtz: "",
					positionCount: "",
				}
			};
		},
		watch: {
			cur: {
				immediate: true,
				handler() {
				    this.params.userId=this.$store.getters.userid;
				},
			},
		},
        created(){
             this.onCollectList();
        },
		methods: {
			goHome(){
                this.$Router.push({name: "index"})
			},
			goToDetail(item){
				let params = {...item}
				params.page = 'positionsList'
				params.htmlType = '招聘'
				// this.$Router.push({
				// 	name: "positionsDetail", // 新页面的路由名称
				// 	params:parmas, // 通过 parmas 传递 id
				// });
				sessionStorage.setItem("positionId", params.positionId)
				sessionStorage.setItem("dept_dictText", params.positionDept)
				sessionStorage.setItem("ktz_dictText", params.positionKtz)
				sessionStorage.setItem("htmlType", params.htmlType)
				sessionStorage.setItem("page", params.page)
				uni.$globalParams = null;
				uni.navigateTo({
					url:"/pages/positions/xgsPositionsForm"
				})
			},
			onCollectList(){
				this.$http.get(this.url.xgsFavoriteJobListUrl,{params:{userId: this.params.userId}}).then(res=>{
					this.favoriteList = res.data.result.records
				}).catch(err => {
					console.log(err);
				});
			},
		}
	}
</script>

